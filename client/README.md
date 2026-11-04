# Donut Client 2.0 — Wurst-based edition

A customized **Wurst Client** build for **Fabric / Minecraft Java 1.21.11**. This replaces the limited Donut Lab toolkit with the upstream Wurst module engine and a new searchable Donut dashboard. It is not Krypton, is not affiliated with Krypton or DonutSMP, and does not claim Krypton's server-specific bypasses or base-finding algorithms.

## Install

Use Java 21, Fabric Loader 0.20.1 or newer, and Fabric API 0.141.6+1.21.11 or a compatible newer 1.21.11 release. Put `Donut-Client-2.0.0.jar` in your `mods` folder. Remove the old Donut Lab JAR and any separate Wurst JAR first. This fork retains the `wurst` mod ID for compatibility with upstream mixins.

## Controls

- **Right Shift:** new Donut dashboard.
- **Right Control:** classic draggable category-window GUI.
- Search the module name or description, or select a category.
- Left-click a module row to toggle it; right-click for its real settings and keybind controls.
- Click `+`/`*` to add/remove a favorite. Favorites survive restarts.
- Scroll or use Previous/Next to page through results.
- The **BaseFinding** tab groups relevant upstream tools such as BaseFinder, ChestESP, CaveFinder, MobSpawnerESP, Freecam and Fullbright. These are client-side tools, limited by the information the server sends.
- Configurations, enabled modules, keybinds and favorites are stored in `.minecraft/donut-client/`.

## Included capabilities

The upstream engine provides combat, rendering, movement, block, item and utility modules, including BaseFinder, ChestESP, PlayerESP, MobESP, Freecam, Fullbright, AutoTotem, TriggerBot, AutoArmor, AutoTool, AutoEat and many more. Availability and behavior on multiplayer servers depend on server mechanics. A module's existence is not evidence of an anticheat bypass.

## Source and license

Based on https://github.com/Wurst-Imperium/Wurst7/tree/1.21.11 at the exact revision in `UPSTREAM_COMMIT`. Original code copyright Wurst-Imperium and contributors. This directory is distributed under **GPL-3.0-or-later**, with `LICENSE.txt` and original copyright notices retained. The supplied source JAR and this source tree correspond to this build.

Changes: original Donut dashboard, category/search/favorite/page controls, BaseFinding grouping, purple/charcoal default ClickGUI colors, dashboard return navigation, separate config directory, fork metadata, reproducible Gradle build and a focused graphical smoke test. CrashChest, ForceOP and MassTPA are not registered in this distribution. Analytics defaults to off, and upstream update polling is disabled for this fork. Wurst branding in upstream screens is retained to make its origin clear.

Public design references reviewed: https://kryptonclient.org/features (module categories and feature presentation), https://wurst.wiki/clickgui (actual GUI screenshot), and https://meteorclient.com/ (public documentation). No Krypton code, paid binaries, logos or assets are included. Its embedded video preview was not accessible in this environment.

## Build and test

`./gradlew build` with JDK 21. Artifacts are in `build/libs/`.

`./gradlew runClientGameTest` runs the focused graphical test in `src/smoke/`, requiring an OpenGL-capable display. It creates a local test world, opens the dashboard, captures screenshots, searches Fullbright, exercises a toggle/settings interaction, and enables/disables representative modules. The broader upstream suite is retained under `src/gametest/` as reference but is not wired into this focused test task.
