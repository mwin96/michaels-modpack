"""Add permanent asset names without replacing any existing release asset or tag.

Uses GitHub CLI authentication already available to the operator or GITHUB_TOKEN.
Run with --apply after uploading the versioned Windows installer ZIP. Both aliases
deliver that full installer, including the compatibility name client-update.zip.
Published-release events can arrive before assets, so a missing ZIP is retried.
"""
from pathlib import Path
import argparse,hashlib,json,os,re,shutil,subprocess,tempfile,time,urllib.parse,urllib.request

PATTERNS={
    'michaels-modpack-windows-easy.zip':r'michaels-modpack-(?:launcher-)?\d+(?:\.\d+){2,3}-windows-easy\.zip',
    'michaels-modpack-client-update.zip':r'michaels-modpack-(?:launcher-)?\d+(?:\.\d+){2,3}-windows-easy\.zip',
}

def gh(*args):
    env=dict(os.environ);env.pop('GH_DEBUG',None)
    return subprocess.run(['gh',*args],check=True,capture_output=True,text=True,env=env).stdout

def release(repo,tag):
    value=json.loads(gh('api',f'repos/{repo}/releases/tags/{urllib.parse.quote(tag,safe="")}'))
    if value['draft'] or value['prerelease']:raise RuntimeError('Only a published stable release can supply permanent downloads.')
    return value

def select_sources(value):
    result={}
    for alias,pattern in PATTERNS.items():
        matches=[a for a in value['assets'] if re.fullmatch(pattern,a['name']) and a['state']=='uploaded']
        if len(matches)>1:raise RuntimeError('Ambiguous versioned source for '+alias)
        if not matches:return None
        result[alias]=matches[0]
    return result

def sha(path):
    digest=hashlib.sha256()
    with path.open('rb') as stream:
        for chunk in iter(lambda:stream.read(1024*1024),b''):digest.update(chunk)
    return digest.hexdigest()

def download(asset,target):
    request=urllib.request.Request(asset['browser_download_url'],headers={'User-Agent':'MichaelsModpackStableDownloads/1'})
    with urllib.request.urlopen(request,timeout=60) as response,target.open('wb') as stream:shutil.copyfileobj(response,stream)
    if target.stat().st_size!=asset['size']:raise RuntimeError('Downloaded release asset has an unexpected size.')
    digest=sha(target)
    if asset.get('digest') and asset['digest']!='sha256:'+digest:raise RuntimeError('Downloaded release asset failed its GitHub digest.')
    return digest

def verify_alias(asset,digest,size,scratch):
    if asset['size']!=size:raise RuntimeError('Existing alias differs; nothing will be overwritten: '+asset['name'])
    actual=asset.get('digest')
    if actual is None:actual='sha256:'+download(asset,scratch/('check-'+asset['name']))
    if actual!='sha256:'+digest:raise RuntimeError('Existing alias differs; nothing will be overwritten: '+asset['name'])

def identity(asset):
    return {key:asset.get(key) for key in ['id','name','size','digest','created_at','updated_at']}

def main():
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--repo',default=os.environ.get('GITHUB_REPOSITORY','mwin96/michaels-modpack'))
    parser.add_argument('--tag',required=True);parser.add_argument('--apply',action='store_true');parser.add_argument('--source-dir',type=Path);parser.add_argument('--report',type=Path)
    args=parser.parse_args()
    if not re.fullmatch(r'[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+',args.repo):raise RuntimeError('Unexpected repository name.')
    if not args.tag or args.tag.startswith('-') or len(args.tag)>200 or any(ord(c)<32 for c in args.tag):raise RuntimeError('A release tag is required.')
    for attempt in range(37):
        before=release(args.repo,args.tag);sources=select_sources(before)
        if sources:break
        if not args.apply or attempt==36:raise RuntimeError('The versioned Windows installer must be uploaded first.')
        print('Waiting for the versioned Windows installer...',flush=True);time.sleep(5)
    original={a['name']:identity(a) for a in before['assets']};results=[]
    with tempfile.TemporaryDirectory(prefix='michaels-stable-downloads-') as temporary:
        scratch=Path(temporary);prepared={}
        for alias,source in sources.items():
            if source['name'] in prepared:local,digest=prepared[source['name']]
            else:
                local=args.source_dir/source['name'] if args.source_dir else None
                if local and local.is_file() and source.get('digest'):
                    digest=sha(local)
                    if local.stat().st_size!=source['size'] or source['digest']!='sha256:'+digest:raise RuntimeError('Local file differs from the published source asset.')
                else:local=scratch/source['name'];digest=download(source,local)
                prepared[source['name']]=(local,digest)
            current=release(args.repo,args.tag);existing=next((a for a in current['assets'] if a['name']==alias),None)
            if existing:
                verify_alias(existing,digest,source['size'],scratch);action='already-identical'
            elif args.apply:
                upload=scratch/alias;shutil.copyfile(local,upload)
                try:gh('release','upload',args.tag,str(upload),'--repo',args.repo)
                except subprocess.CalledProcessError:
                    # A concurrent invocation may have uploaded the identical alias.
                    current=release(args.repo,args.tag);existing=next((a for a in current['assets'] if a['name']==alias),None)
                    if existing is None:raise
                    verify_alias(existing,digest,source['size'],scratch)
                current=release(args.repo,args.tag);existing=next(a for a in current['assets'] if a['name']==alias)
                verify_alias(existing,digest,source['size'],scratch);action='uploaded'
            else:action='would-upload'
            results.append({'alias':alias,'source':source['name'],'sha256':digest,'bytes':source['size'],'action':action,'payload':'full-windows-installer'})
    after=release(args.repo,args.tag);after_assets={a['name']:identity(a) for a in after['assets']}
    for name,value in original.items():
        if after_assets.get(name)!=value:raise RuntimeError('An existing release asset changed unexpectedly: '+name)
    if before['id']!=after['id'] or before['tag_name']!=after['tag_name'] or before['target_commitish']!=after['target_commitish']:raise RuntimeError('Release identity changed unexpectedly.')
    report={'passed':True,'repo':args.repo,'tag':args.tag,'applyRequested':args.apply,'aliases':results,'allExistingAssetsUnchanged':True,'releaseIdentityUnchanged':True,'neverDeletesOrClobbers':True}
    if args.report:args.report.write_text(json.dumps(report,indent=2)+'\n')
    print(json.dumps(report,indent=2))

if __name__=='__main__':main()
