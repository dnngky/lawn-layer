# LawnLayer

Note: Gradle is required to run the game. Gradle is a build automation tool which can be easily installed at gradle.org. Once Gradle has been installed, download all files and directories as a zip file, and enter 'gradle.run' in the terminal in the extracted root directory. After running, the Javadoc for the game can be found in build/docs/javadoc/index.html.

Lawn Layer is a mini-game where the player fills grass tiles and avoids enemies in the process. On each level, the player spawns as a green ball on a large dirt platform, and moves using arrow keys. The player lays down path tiles as they move on the dirt. When a region is enclosed by the path, it turns into a grass region if there are no enemies present. If the player or an enemy hits a path tile before it turns into grass, the path will turn red and propagate outwards and kills the player should it hit them. There are four power-ups which spawn randomly: boost, freeze, shield, and invincible.

The hearts on the top left indicates the number of lives remaining. The number on the top right corner indicates the percentage of dirt required to be filled for the level. There are three default levels, although they can be modified and new levels can be created (read below).

Levels are created using the .txt files and loaded in the config.json file. Each level file draws out the shape of the map, where an 'X' represents a concrete tile at the respective location. Each tile is 20 x 20 px, and the map is 1280 x 640 px, hence the map drawn in the level files should be 64 x 32. If a new level file has been created, ensure it has been updated into the config.json file for it to be playable in the game.
