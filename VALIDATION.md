# Validation — michael's modpack1.0.5

Core SHA256: `6ea26cf2dc5734edd68fad60b6f7e3ca68b0e6ba8f3a2eba8ba364c1f6dc06a2`

In the controlled test scene, Windows working-set memory was about1.0–1.5GB lower than two1.0.4 baseline runs, with the same6GB memory cap, graphics and chunks. FPS and CPU stayed within the baseline range; no FPS increase is claimed.

| Metric | Stock1.0.4: two runs | RAM1.0.5 |
| --- | --- | --- |
| Windows working set | 8.333–8.846GB | 7.336GB |
| Windows private bytes | 12.766–13.270GB | 11.738GB |
| Committed Java heap | 3.683–4.165GB | 2.693GB |
| Process CPU, average cores | 0.686–0.740 | 0.737 |
| FPS at60FPS target | 59.455–59.535 | 59.470 |
| Frame time99th percentile | 17.683–17.851ms | 17.927ms |

These are decimalGB, measured on the test PC:1920×1080,6-chunk client/server distances, minimal MakeUp enabled, ocean Physics enabled, all resource packs and96 fixed mobs. No forced collection was used. The candidate received about92seconds of extra unmeasured startup-check warmup, so small FPS differences are not evidence of a speed improvement. P99 increased about0.08ms versus the slower baseline tail, a negligible observed change; the candidate's120second capped phase had no frames above33.33ms. The natural post-GC occupancy proxy fell about0.4GB; that proxy is separate from actual Windows memory and is not an exact live-set measurement. The friend's8GB hardware has not been benchmarked.

The client-only ModernFix replacement enables on-demand model loading and includes a narrow startup event-order fix. The first boot, without a resource reload, preserves Alex's Caves fullbright models, both Mutant Monsters weapon wrappers and all10 bone-pile states. The15,630 targeted model signatures, curated native GUI/held/world checks, Create contraption, gun aiming/firing/reload and gunsmith opening, animals and ocean checks passed. The9-item GUI grid is pixel-identical to the canonical baseline before and after resource reload. A revisit after more than300seconds away with natural collections passed without large frame stalls. The explicit-false fresh startup uses the original eager maps and leaves the deferred queue dormant. Ten lifecycle queue fixtures and corresponding-source/binary checks passed.

The original1.0.4 full381,176-signature/17,371-GUI-call reference was retained; the final candidate used the bounded15,630-signature regression subset and curated native checks. Time/random item overrides and missing-reference identity differences were adjudicated separately. This does not claim exhaustive visual coverage of every possible mod combination.

Real manual upgrades from1.0.2/1.0.3/1.0.4 and the pinned native automatic1.0.4→1.0.5 path passed, including repeat, backup and preference preservation. Existing3.5/4/5/6/8GB caps, chunk/shader settings, markers and explicit ModernFix opt-outs remain unchanged. Fresh starter allocation remains the existing4GB in this version. Unknown manually modified ModernFix files are rejected before writes by the manual updater; the normal automatic installer replaces its managed files.

The server's original ModernFix, all server bytes, world progress and other content are unchanged; no restart is needed. No rejected FPS/ETF prototype or Distant Horizons is included. Full corresponding upstream source, patch/build instructions and LGPL notices accompany the modified client file. Independent final native review used GPT-6.1Sol at xhigh. Test processes were normally stopped; game audio was muted only externally for private testing, and published audio settings remain normal.
