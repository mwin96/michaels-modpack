# Independent final review — 1.0.2

**GO for the private monitored pilot.** Exact core SHA-256 `9376c18bce770460d8aef7c5fec600fe748e4471ab4271eb681dced8d002c4fd`; compatibility jar `2fbfc115ce1683c3f0a6436d4561dcdb28423b39ae71178fc58d272c35f9d49c`.

The exact reconstructed client joined the matching local server. A genuinely fresh player had zero inventory after the first-login cleanup, an empty client hotbar, and the persisted cleanup marker. A subsequently awarded bestiary remained in the immediate inventory query; that check was later found to have occurred on the death screen and is not a complete living/rejoin preservation test. Source inspection shows that the persisted completion marker prevents recurring cleanup. Source review confirms the cleanup targets four automatic handouts for the initial twenty ticks; it is not a recurring inventory wipe. The new map showed terrain without mob radar or geology lines. The geology action changed false → true → false through its native toggle method, preserving the optional action used by F4; physical keyboard input was not tested. Release audio remains master1 and shaders ON; the isolated test harness remains muted. No production account or security dialog was touched.

Nine newly generated chunks completed through Forge's built-in generate command with zero players, while game time stayed24717 before and after. This validates compatibility of background generation with empty-world simulation pause; it does not estimate completion time of the full requested area.

## First-session observations on the actual hosted seed

A fresh local world used the exact production seed and original spawn. This was a structured tour with teleports and some controlled encounters, not an uninterrupted survival playthrough.

- Original spawn: snowy spruce island, accessible wood and exposed stone, four sheep about50blocks away, other nearby islands/plains biome about232blocks north. A natural Dungeons & Taverns illager camp is roughly40–65blocks north. The unarmored player stayed20health during a30-second idle check at spawn; nearby pillagers did not immediately aggro through terrain. This is a threatening landmark, not proven unavoidable spawn-camping. The later SCP-173 death below means the30-second check must not be generalized into a safe-spawn promise. Per the final user preference, retain this wilderness spawn; the explored village alternative is not the recommendation.
- Natural content rendered: nearby hostile camp, Valhelsia player house, Dungeons & Taverns site, SCP facility exterior, and Dungeons Arise towers. The map opened through its application key action. This is visual/availability coverage, not completion of every dungeon or verification of all entrances.
- A real naturally generated Lootr dungeon chest at(-385,14,18) contained an AK-47 and60matching7.62×39rounds. The normal block-use/menu path opened its instanced inventory and native quick-move transferred gun/ammo. Access to this underground chest was scaffolded with teleport/creative inspection; acquiring it normally was not timed.
- A normal-health, iron-armored Knocker encounter used25successful native client gun-operator shots with ordinary cooldowns and live server projectiles. Loaded ammunition went30→5 and player health20→17.17. The Knocker disappeared; this test does not establish whether the last disappearance was death or its scripted behavior. Its encounter briefly obscured visibility; normal daylight rendering returned afterward.
- The simple melee automation was not competent combat control. It injured a zombie, then an idle player died; a later insufficiently protected recovery attempt also died. Native respawn worked, and dropped gun/ammo were recovered by ordinary automatic pickup when returning. These deaths should not be presented as evidence that a human with iron gear cannot handle zombies. A short giant observation was obstructed by terrain and supplies no useful fight/escape verdict.

## Important reused-world death and correction

The final fresh username was killed by SCP Additions173 at20:25:04,27seconds after joining the already-used tour world. This was an actual hostile death, not an explicit kill command or teleport suffocation. The later diagnostic teleport happened after death. The first `final102-clean-minimap.png` therefore contains a death screen and must not be used as a living-play screenshot.

Bytecode inspection of `Scp173SpawnEvents` confirms a6000-tick first check after each login, subsequent6000-tick checks, and a1-in-3 random chance to spawn near the player when no existing173 is found. No173 was manually summoned in this tour. Consequently the27-second death is consistent with a surviving naturally spawned173 from the earlier tour, and cannot be attributed to a new first-login spawn by that handler. The tour world had already experienced substantial simulated play and teleports; the production world remained at time0. Moving coordinates alone would not disable this global near-player horror mechanic. This is evidence of real danger after play time, not a reason to promise either safety or unavoidable death at a pristine spawn.

A follow-up natural respawn placed the player at(-311.5,66,154.5), near original spawn. Only viewing direction was changed; no position relocation, protection effect or gear was supplied. The player remained at20health with empty inventory and no effects through the follow-up capture. `final102-living-natural-respawn.png` shows the nighttime view; `final102-living-empty-clean-map.png` shows the same location with local test time explicitly set to day for visibility. Both use the exact1.0.2 client with minimal map, no radar and default shader. The client and local server were then stopped normally.

## Native crafting check

All173 TaCZ station recipes loaded. A native gun-smith menu consumed16iron and produced a Glock17. The separate native ammo-workbench menu consumed10copper and2gunpowder and produced50matching9mm rounds, which the player automatically picked up. Ingredient tags resolve to vanilla iron/copper/gunpowder. The ordinary table recipes are available: gun-smith table costs13iron+3logs; ammo workbench costs2copper+1iron+an ammo box, and the basic ammo box costs1iron+7planks. Thus a Glock, both stations, and50rounds cost31iron,12copper,2gunpowder,3logs and7planks before tools/fuel. No alternate dimension or quest progression is required. The practical instruction is: **guns use the gun-smith table; bullets use the ammo workbench.**

The tour uncovered concrete presentation clutter, now addressed by the final map/handout defaults, and verified a workable exploration/crafting loop. It does not establish that the complete first hour is fun for this group, that every horror encounter is fair, or that unknown weaker PCs meet the performance target. Prior measured performance used muted in-game audio and does not measure normal SoundPhysics audio-source overhead. Full ordinary survival and group feedback remain the useful next validation.

Evidence: `work/first-session-evidence/` (commands, captures, native crafting and natural loot); `work/final102-evidence/` (exact candidate, fresh inventory, map toggle, empty generation). Existing 1.0.1 portal and progression review remains applicable to unchanged gameplay classes. No release archive was altered by this review.

## Release and hosted checks

The later Windows convenience download `michaels-modpack-1.0.2-windows-easy.zip` has SHA256 `3d9f94e5e0831b8949190aa1ebd2d74ec247bdf69e46ecb559a3b2cbd85a6ce5`. It changes only the launcher's filename to **CLICK HERE TO INSTALL AND PLAY.cmd** and the accompanying short instructions. The command contents and every other archive file are byte-identical to the reviewed Windows starter; the core pack is unchanged.


Windows starter SHA256: `f29e9aa658dd62ba649814f60e60091a5ec0f71b0c64b7606ac5868713651120`. Matching GPL source ZIP SHA256: `e69afb58157afcc8fa5dc4c494406804a7c3ce9f8a37c5fb7bdf0ed876db7c2a`.

A separate GPT-6.1 Sol extra-high artifact/source review confirmed all96 total publisher files (93 client-supported), exactly90 client jars, and all39 preexisting class files unchanged from1.0.1. Only the first-join cleanup and geology-default classes were added. All upstream publisher archives remain unchanged. The actual published packwiz hash graph passed288 file hashes and93 publisher pins. Its automatic installer completed; the installed result matched all195 override files, all93 supported publisher archives, the90-jar client, original thumbnail, normal master volume and enabled shader.

The paid server downloaded back the exact new jar through its normal file panel and SHA256-matched it. Its previous own jar is disabled. It booted with all76 expected server mod files and answers the external1.20.1/Forge3/six-slot status check. Existing-world pause settings were read back, and game time remained0 after startup. Seed, natural wilderness spawn and terrain remain unchanged; no test inventory/entities were added to production. Final2000x2000block pregeneration is scheduled for8:40PM Pacific after all pack changes.

An authenticated account joining the paid remote server remains a first-player check; all joined test-user checks above were local. The earlier118FPS shader and active-mob samples below use an RTX5070Ti and muted game audio, not the unknown weakest friend's PC or normal-volume SoundPhysics overhead.

The1.0.1 and1.0.0 evidence below remains historical and identifies its own tested artifacts.

---

# Independent 1.0.1 follow-up — first-hour friction

**Verdict: GO for the private group's monitored pilot.** Exact reconstructed release joined the local server and passed the focused checks below. Core SHA256: `437662e20d304a0cf53f964bf39276ee88621fbe0e7e8a843dd51397c36e8b2b`. Windows starter SHA256: `a21fe86a3702894244948ae76244d5b6bc816ee5e3f8a409347cb98ab3db5c76`. Compatibility jar SHA256: `8807c6bacb7189555a97655b25303f4fe57ccef2c192ee12a791496ac8bc4dc9`.

The review found three practical problems beyond startup compatibility: ordinary starter shelters could be mined quickly, accidental Backrooms entry could replace a player's bed spawn, and a shared horror timer ran faster with more players. The proposed update addresses those specific problems. It has not been assessed as a complete ordinary survival session, and the controlled tests do not prove that everyone will find the pack fun.

## Changes and focused evidence

- **Shelter recovery:** Zombie Tactics retains mining, climbing and noise tracking, but its maximum mineable hardness is 0.6. A native mining-goal fixture broke dirt and glass while rejecting oak planks, stone and cobblestone. The Man's daytime spawning and search destruction are disabled, and its ordinary hardness limit is 0.6. Door/glass scares remain enabled. Source inspection confirms the separate vanish/despawn branch is unaffected; its whole chase was not newly playtested.
- **Optional Backrooms trips:** accidental suffocation, sleep and Nether-portal entry are disabled. Eight paper around one ender pearl craft two existing Backrooms Portal blocks, allowing one to enter and a spare to leave. The recipe exists, and normal BlockItem placement succeeds. The client shows the readable item name and three short usage instructions.
- **Safe portal arrival and return:** entry searches nearby clear, supported space instead of placing the player inside a wall. Return prefers a clear supported position close to the original entrance and height. Bed spawn is preserved. If the original return area becomes unsafe, the existing upstream fallback remains; that exceptional fallback can choose a different location.
- **Movement correctness:** direct collision fixtures initially passed, but real client movement exposed an upstream collision-stack problem that could overwrite arrival coordinates. Portal travel now waits until the end of the server tick, with a pending-player guard and normal cooldown. Application-level forward-key movement through the portal then entered LevelZero at floor height and returned to the original surface height, each stable at 5- and 10-second checks. This used normal client movement packets without OS keyboard/mouse input. Repeated controlled round trips also preserved the bed and verified cooldown blocking.
- **Multiplayer timer:** four calls to the actual transformed Knocker spawn and despawn procedures in one server tick each advanced their shared timer by one, not four. Existing event choices and encounter eligibility are retained. This is a callback regression fixture, not a four-human multiplayer frequency study.

## Visual and release checks

The hidden client remained in LevelZero long enough to render its corridors with the default MakeUp NoEffects shader enabled, then returned to the Overworld. The item tooltip was read from the live client. Fresh exact-archive checks are recorded alongside the final release hashes in the machine report.

On the exact final import, application-key walking entered LevelZero at Y=1 and returned to the original surface at Y=71; both remained stable at 5- and 10-second checks. Its bed spawn remained in the Overworld at Y=71. Fresh screenshots captured the clear corridor view and the returned Overworld scene. The archive comparison found exactly the nine intended entries changed from 1.0.0, with all third-party publisher-file entries unchanged; the bundled source matches the corresponding source ZIP.

Normal release sound remains enabled, the minimal shader remains enabled, physics remain ocean-waves-only, and test flags are absent from shipped launch settings. Validation itself stayed muted. The earlier FPS results therefore do not measure normal-volume Sound Physics audio overhead. No new universal performance claim follows from this update.

The remaining useful acceptance test is an ordinary first session: gather resources without supplied equipment or protective effects, establish shelter, find a gun/ammo route, encounter threats, die/recover if it happens, and judge interruption frequency and clarity. Unknown friend hardware, four-player exploration, large disasters and long-term stability remain outside the focused evidence.

Evidence includes `ux-101-source-freeze.json`, the exact release audit, `ux-portal.json`, `ux-mining.json`, `ux-timers.json`, the application-walking entry/exit records, live tooltip text, and LevelZero/Overworld captures. Raw logs and private test-world state remain local.

The owned hidden client and local server were stopped normally after the final captures. No account or security prompt was handled by this reviewer, and no publication or hosted-server action was performed here.


## Hosted deployment update

The production host received the same 1.0.1 compatibility jar. A copy retrieved through the normal hosting-panel download action matched SHA-256 8807c6bacb7189555a97655b25303f4fe57ccef2c192ee12a791496ac8bc4dc9 exactly. Live configuration inspection confirmed the shelter, nighttime-stalker and optional-Backrooms settings. No world reset, whitelist or authentication change was made. An authenticated hosted player join remains pending; the local joined tests above are completed.

## Earlier full compatibility baseline

The following report covers the original 1.0.0 archive. All third-party archive entries and every pre-existing compatibility-mod class are byte-identical in 1.0.1; only the documented settings, added UX classes/resources, version manifest and corresponding source changed. Its combat/progression/rendering evidence is retained below with its original exact archive hash.

---

# Independent review — michael's modpack 1.0.0

Reviewed November 4, 2026, by an independent GPT-6 Astra reviewer using high reasoning.

## Verdict

**GO for a private, monitored release of the tested candidate, with the limits below.** The pack boots, connects, preserves the normal End progression, supplies paired guns/ammunition through Lootr, and passes the targeted regression checks. This is **not** certification of 60–100 FPS on the weakest friend's unknown computer, a complete four-player survival playthrough, every boss encounter, or the hosted server. This independent pack review used the local test server; subsequent hosted deployment checks are recorded below.

Tested release SHA-256: `651c18878b1ec299870af0c17d670264ea33afbd851aaecc74ffec975390e8e9`.

Minecraft 1.20.1, Forge 47.4.28, Java 17.0.21. Final checks used the reconstructed release archive, not only the development staging folder. The final jar adds only the client menu-branding class; every previously tested gameplay entry is byte-identical to the earlier combat-tested jar. Validation used muted background clients and a local-only test server. No mouse or keyboard input automation was used. The final menu and connection checks additionally monitored native window visibility and foreground ownership after tightening the hidden-window test harness.

## Defects found and fixed

- **SCP Additions 173 ignored ordinary TaCZ rounds.** Its upstream durability handler subtracts six health from every damage component, eliminating TaCZ's split damage. A narrow compatibility mixin bypasses only that subtraction for actual TaCZ bullets against this exact creature. Its existing health, gaze/movement behavior and other damage rules remain. A repeated native-flight test killed it at 1,024 HP after 214 controlled body hits.
- **Eternal Jesus crashed the multiplayer client at low health.** Its client-only bell was missing from the server-synchronized sound registry. The client now resolves its existing local sound asset directly when that registry entry is absent. The imported release passed repeated low-health hurt-path triggers, including with the default shader enabled; the flash was visually inspected and the client continued running. Testing stayed muted, so audio loudness/quality was not judged.
- **ArPhEx rebuilt an identical 90-by-45 sphere mesh every render pass.** A targeted change reuses the mod's existing static vertex buffer while preserving its drawing conditions and transforms. The original local Java profile showed 226 render-thread top-frame samples in this builder. A subsequent 30-second profile contained zero stacks in that sphere builder. These profiles do not measure GPU time or establish a universal FPS gain.
- **Requested Dragon balance:** configurable maximum health defaults to 400. Natural crystal-driven respawn produced a 400/400 Dragon. Configuration changes preserved health fraction (100/400 became 150/600, then 100/400), and applying the adjustment to a dead Dragon left it dead. Vanilla phases, crystals, portal logic and attacks were retained.

The compatibility mod compiled successfully after the final changes, and the final imported client joined the matching server. Validation-only capture/mute/shader controls require a JVM flag absent from the release.

## Progression and loot

- Audited the live wishlist: 69 retained selections, 20 excluded, no excluded selection present. The only missing selection IDs in the lock are the documented omissions 14, 21 and 48. Zombie Tactics is retained as the backport; the Tremorzilla addon is represented by the documented replacement behavior. The redundancy/version decisions are reasonable for this pack.
- The Eye of Ender recipe is present. No installed jar overrides the vanilla Eye recipe, stronghold definition or blaze loot table.
- Located a natural stronghold at X=1360, Z=-432 and a Nether fortress at X=-128, Z=-320. The stronghold had all 12 natural portal frames. Calling the normal Eye item interaction on those frames activated the portal; the connected player entered the End through it.
- The natural Dragon accepted ordinary player damage and completed its normal death/exit-portal sequence. After the 400 HP change, native TaCZ **head collision processing** removed nine health per hit; 45 controlled impacts triggered the dying phase and the normal exit portal appeared. The separate moving-head flight fixture missed, so actual player aiming at a flying Dragon is not claimed tested. These rapid controlled impacts are not a combat time-to-kill benchmark.
- Opened **60 actual Lootr chest menus** with deterministic dungeon loot seeds: **20 guns, zero guns without matching ammunition**. This exercised the personal-inventory path rather than only calling a loot table.
- Repeated runtime sampling of 150 chests per table yielded the following. No sampled gun lacked its matching ammunition.

| Table | Guns | Artifacts | Unpaired guns |
|---|---:|---:|---:|
| `minecraft:chests/simple_dungeon` | 31 | 14 | 0 |
| `minecraft:chests/stronghold_library` | 39 | 12 | 0 |
| `idas:chests/dread_citadel/dread_citadel` | 37 | 9 | 0 |

Artifacts, Curios and Artifacts' embedded ExpandAbility dependency loaded. Curios inventory slots were present. Individual wearable abilities were not exhaustively exercised.

## Gun combat coverage

The final flight fixture used a native AK47 bullet with its gun/bullet data, native speed, real projectile ticking and collision. Targets were stationary with AI disabled, and hurt cooldowns were cleared between rounds. **22 of 24 living types reached death through this fixture.** This verifies collision/damage/death handling under those conditions, not an entire encounter, reload inputs, every armor combination, regeneration over time, shields or boss-phase timing.

| Creature | Initial HP | Controlled rounds | Result |
|---|---:|---:|---|
| `alexscaves:hullbreaker` | 400 | 56 | Killed |
| `alexscaves:tremorzilla` | 500 | 136 | Killed |
| `arphex:centipede_stalker` | 50 | 11 | Killed |
| `arphex:spider_goliath` | 200 | 43 | Killed |
| `born_in_chaos_v1:door_knight` | 30 | 8 | Killed |
| `born_in_chaos_v1:nightmare_stalker` | 70 | 16 | Killed |
| `cataclysm:the_leviathan` | 400 | 12 | No damage in this fixture; see limits |
| `cave_dweller:cave_dweller` | 60 | 13 | Killed |
| `crimsonsteves_mutant_mobs:mutant_skeleton` | 150 | 32 | Killed |
| `man:manfromthefog` | 10 | 3 | Killed |
| `minecraft:ender_dragon` | 400 | 12 | No damage in this fixture; see limits |
| `minecraft:zombie` | 20 | 5 | Killed |
| `pigzilla:pigzilla` | 500 | 105 | Killed |
| `scp_additions:scp_173` | 1024 | 214 | Killed |
| `scp_ultimate:scp_096` | 500 | 105 | Killed |
| `scp_ultimate:scp_173` | 100 | 21 | Killed |
| `scpo:scp_096_aggro` | 1000 | 521 | Killed |
| `scpo:scp_173` | 100 | 53 | Killed |
| `siren_head:siren_head` | 800 | 167 | Killed |
| `slenderman:slenderman` | 200 | 42 | Killed |
| `sons_of_sins:butcher` | 650 | 136 | Killed |
| `the_knocker:knocker` | 50 | 11 | Killed |
| `threateningly_mobs:cave_hunter` | 80 | 18 | Killed |
| `threateningly_mobs:undead_paladin` | 75 | 21 | Killed |

The Dragon subsequently passed the separately described native head-collision test. The Leviathan's dry-air idle fixture produced no damage; its water fight, multipart targeting and vulnerable phases remain unverified. The Sandworm head is a non-living segment, so the generic living-target fixture was not applicable; its full segmented fight remains unverified. No blanket immunity bypass was applied to these bosses. Scripted hauntings such as From the Fog remain encounters rather than universally killable ordinary mobs.

Overtime 096 is killable in this test but exceptionally ammunition-intensive: 521 AK47 body hits. SCP Additions 173 required 214. Those counts are useful warnings about toughness, not recommendations to stand still and trade shots.

## Controls, visuals and performance

- The branded title menu and native window title both displayed **michael's modpack**. The first-run Backrooms photosensitivity notice remains in the release; it was acknowledged only inside the isolated menu-validation instance. The optional Supplementaries Amendments suggestion is suppressed.
- Effective runtime bindings confirmed **R = TaCZ reload**, with all three Iris shader shortcuts unbound. Curios uses J, new waypoint N, enlarged minimap F9, and Backrooms wiki F8. JEI's R remains an inventory action; Structure Gel's R handler requires its held building tool and permission. This was a runtime binding/source check, not an automated physical-key press.
- Fresh screenshots showed vanilla hearts/hunger and no added SCP health/stamina/blink meters. A newly summoned dirt item entered the player's inventory automatically, with no right-click input.
- SCP creatures remained present. Source inspection found independent SCP door-button interaction handling; a complete keycard/door gameplay sequence was not tested.
- Ocean-only Physics settings survived restarts: ocean waves enabled; block, mob, item, cloth, smoke, weather and other requested physics disabled. Normal Minecraft block-break particles remain enabled. Ocean waves were visually inspected.
- The final imported release ran **MakeUp UltraFast 9.6, NoEffects**, with shadows, reflections/refraction, ambient occlusion, antialiasing, bloom, volumetric effects and foliage waving disabled. Oculus reported the shader active, and Physics successfully constructed its ocean shader. The ocean, a cute Melody villager model and a held TaCZ AK47 rendered together. The shader also rendered the Nether and End, then returned to the Overworld, with fresh unobstructed screenshots. A natural Man From the Fog encounter appeared during this visual test. This spot check does not certify every custom model/animation. Flywheel fell back to its non-instanced rendering mode under shaders; no crash resulted.
- Default performance preset: six render chunks, six simulation chunks, **MakeUp UltraFast NoEffects shaders on**, as requested. Shaders were restored on after the paired test. The shipped archive retains normal audio; test mute was applied only to the validation instance.

These are short background-window observations on an RTX 5070 Ti at 1920x1080, with a 120 FPS cap, four CPU threads allocated to validation, and another game running. A paired ON/OFF/ON sequence used the **same forest position, direction, time/weather and six-chunk settings**. Each phase ran for 50 seconds after shader reload; the final 30 samples were:

| Same forest scene | Median FPS | Observed range |
|---|---:|---:|
| MakeUp NoEffects ON, first pass | 118 | 116–119 |
| Shaders OFF | 118 | 117–119 |
| MakeUp NoEffects ON, repeat | 118 | 117–119 |

The final reconstructed release, with native Windows checks confirming it stayed hidden and never foreground, repeated the shader-on forest check at a median of 118 FPS (116–119). Its low-health effect passed again after joining.

**No noticeable FPS penalty appeared in this capped test.** This is the lightest included shader profile, with the expensive listed effects disabled. The cap and strong GPU prevent measuring small computational differences, so the results do not mean shaders are free or establish an exact overhead percentage. Concurrent workload can also vary. Earlier short samples under different conditions included a forest median of 78 FPS (56–95 range); those are retained as evidence of variability rather than used as a shader comparison.

The weakest friend's CPU/GPU/RAM/resolution are still unknown. Sustained 60+ FPS and preferred 100+ FPS on that machine remain unverified. Four-player exploration, new-chunk generation, large disasters and sustained boss fights may cost more than these stationary samples. If needed, disabling shaders is the first simple performance fallback.

## Release hygiene and remaining work

The independently inspected archive includes only the locally built compatibility jar; third-party archives are referenced by publisher URL and hashes. Its own jar matches the tested build. No test world, account/session file, RCON secret or validation process state was present. Production server defaults enable account authentication, allow up to six players without a whitelist, disable RCON, and omit the validation flag and four-thread test cap. Release sound defaults to normal and the tested NoEffects shader defaults to on.

Empty-server pause was rechecked independently, and joining resumed simulation on the local test server. Subsequent hosted checks reported by the deployment agent confirmed all 76 expected server mod files, six slots, account authentication enabled, no whitelist, and game time unchanged across 95 seconds while empty. Signed-in remote joining, friends' connections, regional latency and four-player capacity are outside this independent review's scope. No purchase, deployment or public publication was performed by this reviewer.

Upstream nonfatal warnings remain, including missing optional assets, shader uniform notices and absent mixin-version metadata. They are not evidence of a crash-free guarantee. The discovered low-health crash was fixed and specifically retested; broad overnight stability was not tested.

Evidence retained with the build includes the combat matrix, natural portal/Dragon reports, Lootr sampling, final runtime/pickup checks, selection and packaging audits, FPS samples, local Java profiles, and fresh screenshots. Raw test logs and private test-world state should remain outside the public release.

The final validation client was stopped and the local server was shut down through its normal stop command after evidence capture.


## Bounded active-monster and reload follow-up

A subsequent 60-second sample used the exact reconstructed release at 1920x1080, six chunks, default MakeUp NoEffects and ocean waves, with 14 initially active hostile mobs in a constructed ocean-side arena (six zombies, four skeletons, two spiders, a Born in Chaos Door Knight and Nightmare Stalker). Normal AI was enabled; the single survival player had test-only resistance/saturation and stood on a protected perch. Thirteen monsters remained at the end. On the RTX 5070 Ti test machine with the 120 FPS cap and four Java CPU threads, the sample measured **118 FPS median, 116–119 range and 117 at the 10th percentile**. The local server reported **20 TPS and 12.769 ms mean tick time**. This is a modest synthetic encounter with one client and a mostly fixed view, not a four-player dungeon/chunk-generation benchmark or a result for an unknown weaker PC.

The installed TaCZ application-level R event path was also exercised without OS input. An empty AK47 loaded **29 magazine rounds plus one chambered round**, reducing matching ammunition from 64 to 34; automatic reload was disabled. The shader remained active. This validates the application's configured reload action, not a physical keyboard press.

Native window monitoring recorded 390 samples, all hidden and never foreground. Both owned test processes stopped normally afterward. Core and Windows starter archive hashes remained unchanged. Evidence is retained privately in busy-scene-review.md, busy-scene-result.json, and the associated screenshots/command/state captures.

The production hosted world was subsequently restored to the untouched world folder, reported game time 0, and answered the external Minecraft status request with version 1.20.1, six slots and Forge network 3. An authenticated hosted player join remains a first-player/final deployment check; it is not claimed completed here.
