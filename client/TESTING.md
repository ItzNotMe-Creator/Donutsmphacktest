# Validation of Donut Client 2.0.0

Validated on 2026-11-04 with Minecraft 1.21.11, Fabric Loader 0.20.1, Fabric API 0.141.6+1.21.11 and JDK 21, using Xvfb for the graphical client.

`./gradlew build runClientGameTest` completed successfully (exit 0). One focused Fabric client game test executed in a newly created local world:

- Verified that the module registry contains more than 100 modules (153 registered; 151 categorized modules listed by the dashboard).
- Opened the dashboard through the actual Right Shift keybind.
- Searched for Fullbright through the search widget.
- Clicked the filtered result and asserted Fullbright became enabled.
- Right-clicked the result and asserted its settings screen opened.
- Enabled Fullbright, ChestESP, PlayerESP, BaseFinder and Freecam individually, allowed five ticks to execute with each enabled, and disabled each afterward without a test failure.
- Captured dashboard, search and settings screenshots. Reviewed the dashboard and search screenshots visually; corrected a search text layering/alpha issue and reran successfully.

Both distributable and source JAR ZIP integrity checks passed. The distributable contains the expected Fabric metadata, dashboard class and GPL license.

The broader upstream test suite, combat outcomes, server anticheat behavior, remote base discovery and compatibility with other mods were not validated. Module toggle/render smoke checks do not prove every algorithm or multiplayer interaction is correct. No connection to DonutSMP was used for testing.

The cloud runner logged unavailable audio, optional Sodium classes, Minecraft account-service requests, cape lookup, and an X11 cursor warning. These did not fail the local-world test. Audio, account services and Sodium compatibility are not covered by this result.
