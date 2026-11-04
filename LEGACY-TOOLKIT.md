# Donut Lab — Fabric 1.21.11

Client exploration toolkit for your Minecraft Java server.

[Download Donut Lab 1.0.0 (.jar)](https://github.com/ItzNotMe-Creator/Donutsmphacktest/raw/refs/heads/main/downloads/donut-lab-1.0.0.jar)

Compilation and JAR integrity checks passed. In-game behavior has not been verified.

Install Fabric Loader 0.18.4 or newer for **Minecraft 1.21.11**, install Fabric API for that same Minecraft version, and place `donut-lab-1.0.0.jar` in your Minecraft `mods` folder. Requires Java 21.

Press **Right Shift** while in-game to open the dark teal menu. Escape closes it.

- Nearby storage scanner: chests, barrels, shulker boxes, furnaces and hoppers in a 32-block cube radius around the scan's starting position. Processes 2,048 positions per tick; reports at most 100 matches. Only loaded client chunks are searchable. Results are block coordinates, not proof of a base. No remote or server-wide scan.
- Auto sprint while holding forward, subject to Minecraft's normal movement rules.
- Maximum vanilla brightness toggle, restoring the previous value when toggled off. This is not fullbright.
- One session-only waypoint, with distance and dimension awareness.
- Toggleable coordinates HUD, coordinates in the menu, and a copy-to-clipboard button.
- Clear scan results and waypoint.

Settings and waypoint are held for the current game session. The results panel shows as many entries as fit on screen; it currently has no scrolling. Use a normal GUI scale for the 400-pixel-wide menu.

## Build

With Java 21: `./gradlew build`. The installable remapped artifact is `build/libs/donut-lab-1.0.0.jar`. Gradle needs access to Fabric, Mojang, Maven Central and Gradle distribution hosts. On proxy-only hosts configure Java/Gradle HTTP and HTTPS proxy properties in the user's Gradle configuration, not in this repository.
