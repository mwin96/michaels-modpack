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
