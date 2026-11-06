"""Explicitly authorized historical client-ZIP routing; original bytes are retained.

Upload the replacement under a staging name, verify it, then use short renames.
Only known client entrypoints and this helper's own notice are mutable. Original
client ZIPs remain at ARCHIVED-ORIGINAL-<expected-SHA12>-<old-name>; pinned tooling
must use that archived name after routing, rather than the mutable old filename.
"""
from pathlib import Path
import concurrent.futures,copy,json,os,re,shutil
from publish_download_aliases import gh,download,sha,identity

DOC='LATEST-DOWNLOAD-ROUTING.md'
HEADER='# Client installer download routing\n'
MARKER='<!-- michaels-client-routing-json -->'
TARGET=re.compile(r'michaels-modpack-(?:(?:launcher-)?\d+(?:\.\d+){2,3}-windows(?:-easy)?|client-update-\d+(?:\.\d+){2,3}|launcher-\d+(?:\.\d+){2,3}-client-update)\.zip')

def is_target(name):return TARGET.fullmatch(name) is not None
def archive_name(name,digest):return 'ARCHIVED-ORIGINAL-'+digest[:12]+'-'+name
def stage_name(name,digest):return 'ROUTING-STAGED-'+digest[:12]+'-'+name
def pages(value):return [row for page in value for row in page]

def consumed_staging_asset(asset,result):
    if not result or not result.get('applied'):return False
    canonical=result['canonical']
    if asset.get('digest')!='sha256:'+canonical['sha256'] or asset['size']!=canonical['bytes']:return False
    return any(row.get('currentAssetId')==asset['id'] and row.get('stageName')==asset['name'] and row.get('action')=='cutover-complete' for release in result['releases'] for row in release['targets'])

def preserved_archive_renamed_during_recovery(before,after,result):
    if not result or not result.get('applied') or not after:return False
    keys=['id','name','size','digest','created_at']
    if any(before.get(k)!=after.get(k) for k in keys):return False
    return any(row.get('archiveAssetId')==before['id'] and row.get('archiveName')==before['name'] and row.get('oldDigest')==before.get('digest') and row.get('oldSize')==before['size'] and row.get('action')=='cutover-complete' for release in result['releases'] for row in release['targets'])

class API:
    def __init__(self,repo):self.repo=repo
    def releases(self):return pages(json.loads(gh('api','--paginate','--slurp',f'repos/{self.repo}/releases?per_page=100')))
    def assets(self,release_id):return pages(json.loads(gh('api','--paginate','--slurp',f'repos/{self.repo}/releases/{release_id}/assets?per_page=100')))
    def latest(self):return json.loads(gh('api',f'repos/{self.repo}/releases/latest'))
    def rename(self,asset_id,name):
        if not (is_target(name) or name.startswith('ARCHIVED-ORIGINAL-')):raise RuntimeError('Disallowed rename destination.')
        return json.loads(gh('api','--method','PATCH',f'repos/{self.repo}/releases/assets/{asset_id}','-f','name='+name))
    def upload(self,tag,path,clobber=False):
        if not (path.name.startswith('ROUTING-STAGED-') or path.name==DOC):raise RuntimeError('Disallowed upload name.')
        if clobber and path.name!=DOC:raise RuntimeError('ZIP assets are never clobbered.')
        args=['release','upload',tag,str(path),'--repo',self.repo]
        if clobber:args+=['--clobber']
        gh(*args)
    def delete_duplicate_alias(self,current,archived,record):
        if not record.get('compatibilityAlias') or record.get('activeAssetId')!=current['id'] or current['id'] in {record.get('originalAssetId'),archived['id']} or current.get('digest')!=archived.get('digest') or current['size']!=archived['size']:
            raise RuntimeError('An original source asset cannot be deleted; only a recorded duplicate compatibility alias with a verified backup is replaceable.')
        gh('api','--method','DELETE',f'repos/{self.repo}/releases/assets/{current["id"]}')
    def fetch(self,asset,path):return download(asset,path)

def asset_digest(api,asset,scratch):
    value=asset.get('digest') or ''
    if re.fullmatch(r'sha256:[0-9a-f]{64}',value):return value[7:]
    return api.fetch(asset,scratch/('digest-'+str(asset['id'])))

def require_content(api,asset,digest,size,scratch):
    if asset['state']!='uploaded' or asset['size']!=size or asset_digest(api,asset,scratch)!=digest:raise RuntimeError('Asset content mismatch: '+asset['name'])

def find(assets,name):
    matches=[a for a in assets if a['name']==name]
    if len(matches)>1:raise RuntimeError('Duplicate asset name: '+name)
    return matches[0] if matches else None

def load_notice(api,assets,scratch):
    asset=find(assets,DOC)
    if not asset:return {'schema':1,'managedBy':'michaels-client-routing','routes':{}}
    path=scratch/DOC;api.fetch(asset,path);text=path.read_text()
    if not text.startswith(HEADER) or text.count(MARKER)!=1:raise RuntimeError('Existing routing notice is not owned by this helper.')
    value=json.loads(text.split(MARKER,1)[1].strip())
    if value.get('schema')!=1 or value.get('managedBy')!='michaels-client-routing':raise RuntimeError('Unexpected routing notice schema.')
    return value

def reconcile_pending(api,notice,assets,scratch):
    """Finalize a completed cutover whose final journal upload was interrupted.

    This returns an in-memory repair, so plan-only invocations remain read-only.
    Old pending canonical fields are consumed before a new run changes them.
    """
    value=copy.deepcopy(notice);changed=False
    for name,record in list(value['routes'].items()):
        pending=record.get('pending');active=find(assets,name)
        if not pending or not active or active['id']!=pending['stageAssetId']:continue
        archived=find(assets,pending['archiveName'])
        if not archived or archived['id']!=pending.get('archiveAssetId',pending['oldAssetId']):raise RuntimeError('Pending journal archive identity does not match.')
        new_sha=pending.get('newSha256',notice.get('canonicalSha256'));new_size=pending.get('newSize',notice.get('canonicalBytes'))
        require_content(api,active,new_sha,new_size,scratch);require_content(api,archived,pending['oldSha256'],pending['oldSize'],scratch)
        archives=record.get('archives',[])
        if not any(a['id']==archived['id'] for a in archives):archives=archives+[{'id':archived['id'],'name':pending['archiveName'],'sha256':pending['oldSha256'],'bytes':pending['oldSize']}]
        value['routes'][name]={'activeAssetId':active['id'],'sha256':new_sha,'compatibilityAlias':True,'originalAssetId':record.get('originalAssetId',pending.get('originalAssetId',pending['oldAssetId'])),'archives':archives};changed=True
    return value,changed

def save_notice(api,release,value,scratch):
    text=HEADER+'\nClient ZIP filenames in this release deliver the current full Windows installer.\n\n'
    text+='Original ZIP bytes remain under `ARCHIVED-ORIGINAL-<original-SHA-prefix>-<old-name>`. Original checksum/source/server/resource/core files and tags are unchanged. **Historical SHA256SUMS entries describe those archived original bytes, not the current compatibility downloads at the old filenames.**\n\n'
    text+='Current installer: https://github.com/'+api.repo+'/releases/latest/download/michaels-modpack-windows-easy.zip\n\n'
    text+='Routing details and preserved original asset IDs:\n\n'+MARKER+'\n'+json.dumps(value,indent=2,sort_keys=True)+'\n'
    path=scratch/DOC;path.write_text(text);current=find(api.assets(release['id']),DOC)
    if current and current.get('digest')=='sha256:'+sha(path):return
    api.upload(release['tag_name'],path,clobber=current is not None)

def build_plan(api,canonical,scratch):
    if api.latest()['tag_name']!=canonical['tag']:raise RuntimeError('Supplied canonical tag is not the latest published stable release.')
    result=[]
    for release in api.releases():
        if release['draft']:continue
        assets=api.assets(release['id']);notice=load_notice(api,assets,scratch);notice,repair=reconcile_pending(api,notice,assets,scratch)
        names={a['name'] for a in assets if is_target(a['name'])}
        names|={name for name,row in notice['routes'].items() if row.get('pending') and is_target(name)}
        targets=[]
        for name in sorted(names):
            asset=find(assets,name);record=notice['routes'].get(name,{})
            if asset:
                digest=asset_digest(api,asset,scratch);row={'tag':release['tag_name'],'releaseId':release['id'],'id':asset['id'],'name':name,'oldDigest':'sha256:'+digest,'oldSize':asset['size'],'needsChange':digest!=canonical['sha256']}
                if row['needsChange']:
                    archived=find(assets,archive_name(name,digest))
                    if archived:
                        require_content(api,archived,digest,asset['size'],scratch)
                        if record.get('activeAssetId')!=asset['id'] or not record.get('compatibilityAlias'):raise RuntimeError('Archive exists for an unrecognized original; refusing to delete or duplicate it: '+name)
                        if asset['id']==record.get('originalAssetId'):raise RuntimeError('An original source asset cannot be deleted.')
                    row.update({'archiveName':archive_name(name,digest),'stageName':stage_name(name,canonical['sha256']),'reuseVerifiedArchive':archived is not None})
            else:
                pending=record['pending'];archived=find(assets,pending['archiveName']);new_stage=stage_name(name,canonical['sha256']);staged=find(assets,new_stage)
                if not archived or archived['id']!=pending.get('archiveAssetId',pending['oldAssetId']):raise RuntimeError('Interrupted cutover needs its preserved original archive.')
                require_content(api,archived,pending['oldSha256'],pending['oldSize'],scratch)
                if staged:require_content(api,staged,canonical['sha256'],canonical['bytes'],scratch)
                row={**pending,'tag':release['tag_name'],'releaseId':release['id'],'id':archived['id'],'name':name,'oldDigest':'sha256:'+pending['oldSha256'],'oldSize':pending['oldSize'],'needsChange':True,'recoverInterruptedCutover':True,'stageName':new_stage}
            targets.append(row)
        if targets:result.append({'release':release,'assetsBefore':assets,'noticeBefore':notice,'noticeNeedsRepair':repair,'targets':targets})
    return {'canonical':{k:v for k,v in canonical.items() if k!='file'},'releases':result,'releaseCount':len(result),'targetCount':sum(len(r['targets']) for r in result),'changeCount':sum(t['needsChange'] for r in result for t in r['targets'])}

def apply_release(api,entry,canonical,scratch):
    release=entry['release'];scratch.mkdir();notice=copy.deepcopy(entry['noticeBefore']);notice.update({'canonicalTag':canonical['tag'],'canonicalSha256':canonical['sha256'],'canonicalBytes':canonical['bytes']})
    if entry.get('noticeNeedsRepair'):save_notice(api,release,notice,scratch)
    results=[]
    for row in entry['targets']:
        if not row['needsChange']:results.append({**row,'action':'already-current'});continue
        if api.latest()['tag_name']!=canonical['tag']:raise RuntimeError('Latest release changed; stopping before another cutover.')
        name=row['name'];assets=api.assets(release['id']);current=find(assets,name);record=notice['routes'].get(name,{})
        if current and asset_digest(api,current,scratch)==canonical['sha256']:
            require_content(api,current,canonical['sha256'],canonical['bytes'],scratch)
            fresh=load_notice(api,assets,scratch);fresh,repair=reconcile_pending(api,fresh,assets,scratch)
            if repair:notice=fresh;notice.update({'canonicalTag':canonical['tag'],'canonicalSha256':canonical['sha256'],'canonicalBytes':canonical['bytes']});save_notice(api,release,notice,scratch)
            results.append({**row,'action':'already-current-after-refresh'});continue
        staged=find(assets,row['stageName'])
        if row.get('recoverInterruptedCutover') and not staged and current is None:
            # A newer canonical may supersede an interrupted older stage. Restore
            # the old URL before uploading its new stage; retain the older stage.
            backup=find(assets,row['archiveName']);require_content(api,backup,row['oldDigest'][7:],row['oldSize'],scratch)
            api.rename(backup['id'],name);row={**row,'recoverInterruptedCutover':False}
        if not staged:
            path=scratch/row['stageName']
            try:os.link(canonical['file'],path)
            except OSError:shutil.copyfile(canonical['file'],path)
            api.upload(release['tag_name'],path);staged=find(api.assets(release['id']),row['stageName'])
        if not staged:raise RuntimeError('Staged upload did not appear.')
        require_content(api,staged,canonical['sha256'],canonical['bytes'],scratch)
        if api.latest()['tag_name']!=canonical['tag']:raise RuntimeError('Latest release changed during upload; no cutover was started.')
        old_sha=row['oldDigest'][7:];archived=find(api.assets(release['id']),row['archiveName'])
        pending={'stageName':row['stageName'],'stageAssetId':staged['id'],'archiveName':row['archiveName'],'archiveAssetId':archived['id'] if archived else row['id'],'oldAssetId':row['id'],'oldSha256':old_sha,'oldSize':row['oldSize'],'newSha256':canonical['sha256'],'newSize':canonical['bytes'],'originalAssetId':record.get('originalAssetId',row['id'])}
        notice['routes'][name]={**record,'pending':pending};save_notice(api,release,notice,scratch)
        reused=False
        try:
            if not row.get('recoverInterruptedCutover'):
                assets=api.assets(release['id']);current=find(assets,name)
                if current is None or current['id']!=row['id']:raise RuntimeError('Original asset changed before cutover.')
                require_content(api,current,old_sha,row['oldSize'],scratch)
                if archived:
                    require_content(api,archived,old_sha,row['oldSize'],scratch)
                    api.delete_duplicate_alias(current,archived,record);reused=True
                else:archived=api.rename(current['id'],row['archiveName'])
            if not archived:raise RuntimeError('Preserved original archive is missing.')
            require_content(api,archived,old_sha,row['oldSize'],scratch)
            api.rename(staged['id'],name)
        except Exception as error:
            occupied=find(api.assets(release['id']),name)
            if occupied and occupied['id']==staged['id']:
                require_content(api,occupied,canonical['sha256'],canonical['bytes'],scratch)
            else:
                if occupied and occupied['id']!=row['id']:raise RuntimeError('Cutover collision: original archive remains safe; original filename is occupied.') from error
                if not occupied:
                    archived=find(api.assets(release['id']),row['archiveName'])
                    if not archived:raise RuntimeError('Original asset was not found for rollback; journal retained.') from error
                    require_content(api,archived,old_sha,row['oldSize'],scratch);api.rename(archived['id'],name)
                notice['routes'][name]={**record,'lastFailure':'cutover failed; original filename restored','retainedStagedAssetId':staged['id']};save_notice(api,release,notice,scratch)
                raise RuntimeError('Cutover failed; original filename restored. Staged installer retained for retry.') from error
        active=find(api.assets(release['id']),name);require_content(api,active,canonical['sha256'],canonical['bytes'],scratch)
        archives=record.get('archives',[])
        if not any(a['id']==archived['id'] for a in archives):archives=archives+[{'id':archived['id'],'name':row['archiveName'],'sha256':old_sha,'bytes':row['oldSize']}]
        notice['routes'][name]={'activeAssetId':active['id'],'sha256':canonical['sha256'],'compatibilityAlias':True,'originalAssetId':record.get('originalAssetId',row['id']),'archives':archives}
        save_notice(api,release,notice,scratch);results.append({**row,'action':'cutover-complete','archiveAssetId':archived['id'],'currentAssetId':active['id'],'originalIdPreserved':not reused,'reusedVerifiedArchive':reused})
    final=api.assets(release['id']);by_id={a['id']:a for a in final}
    targeted={t['id'] for t in entry['targets']}
    for old in entry['assetsBefore']:
        if old['id'] in targeted or old['name']==DOC or old['name'].startswith('ROUTING-STAGED-'):continue
        if identity(by_id.get(old['id'],{}))!=identity(old):raise RuntimeError('An unrelated original asset changed: '+old['name'])
    return {'tag':release['tag_name'],'passed':True,'targets':results,'checksumsCoreServerSourceResourcesAndTagsNotEdited':True}

def run(api,canonical,scratch,apply=False,workers=3):
    plan=build_plan(api,canonical,scratch)
    if not apply:return {'passed':True,'applied':False,'plan':plan}
    if workers not in [1,2,3]:raise RuntimeError('At most three release workers are permitted.')
    results=[];errors=[]
    with concurrent.futures.ThreadPoolExecutor(max_workers=workers) as pool:
        futures={pool.submit(apply_release,api,entry,canonical,scratch/str(entry['release']['id'])):entry['release']['tag_name'] for entry in plan['releases']}
        for future in concurrent.futures.as_completed(futures):
            try:results.append(future.result())
            except Exception as error:errors.append({'tag':futures[future],'error':str(error)})
    return {'passed':not errors,'applied':True,'canonical':plan['canonical'],'targetCount':plan['targetCount'],'changeCount':plan['changeCount'],'releases':results,'errors':errors}
