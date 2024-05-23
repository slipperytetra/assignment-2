package level;

import block.*;
import main.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Level {
    LevelManager manager;
    private Player player;
    private Door door;
    private Key key;
    private String nextLevel;

    int id;
    String name;
    int sizeWidth;
    int sizeHeight;
    ArrayList<String> lines;
    HashMap<Integer, TextMessage> textMessages;
    BlockGrid grid;
    private int textCounter;

    private final String levelDoc;
    public final double gravity = 9.8;
    public double scale = 2;
    String backgroundImgFilePath;
    Location spawnPoint;
    Location keyLoc;
    Location doorLoc;

    ArrayList<Entity> entities;

    public Level(LevelManager manager, int id, String levelDoc) {
        this.manager = manager;
        this.id = id;
        this.levelDoc = levelDoc;
        this.lines = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.textMessages = new HashMap<>();
        init();
    }

    public void init() {
        System.out.println("init!");
        File file = new File(levelDoc);
        try {
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate file!");
            return;
        }

        name = lines.get(0).substring("name: ".length());
        backgroundImgFilePath = lines.get(1).substring("background: ".length());
        nextLevel = lines.get(2).substring("next_level: ".length());
        sizeWidth = Integer.parseInt(lines.get(3).substring("level_width: ".length()));
        sizeHeight = Integer.parseInt(lines.get(4).substring("level_height: ".length()));
        System.out.println(sizeWidth);
        System.out.println(sizeHeight);
        this.grid = new BlockGrid(sizeWidth, sizeHeight);
    }

    public void load() {
        int relY = 0;
        for (int y = 6; y < lines.size(); y++) {
            String line = lines.get(y);
            line = line.substring(3);
            for (int x = 0; x < line.length(); x++) {
                char tile = line.charAt(x);
                double spawnX = x * Game.BLOCK_SIZE;
                double spawnY = relY * Game.BLOCK_SIZE;

                switch (tile) {
                    case 'P':
                        player = new Player(this, new Location(spawnX, spawnY));
                        spawnY = spawnY - (player.getHeight() - Game.BLOCK_SIZE);
                        player.setLocation(player.getLocation().getX(), spawnY);
                        spawnPoint = new Location(spawnX, spawnY);
                        break;
                    case 'K':
                        keyLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        key = new Key(this, keyLoc);
                        double keyHeightDiff = key.getLocation().getY() - (key.getHeight() - Game.BLOCK_SIZE);
                        key.setLocation(key.getLocation().getX(), keyHeightDiff);
                        addEntity(key);
                        break;
                    case 'D':
                    case 'd':
                        doorLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        door = new Door(this, doorLoc);
                        if (tile == 'd') {
                            door.setType(EntityType.STONE_DOOR);
                        }
                        double doorHeightDiff = door.getLocation().getY() - (door.getHeight() - Game.BLOCK_SIZE);
                        door.setLocation(door.getLocation().getX(), doorHeightDiff);
                        addEntity(door);
                        break;
                    case 'X':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.DIRT, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'G':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.GRASS, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'L':
                        grid.setBlock(x, relY, new BlockClimbable(BlockTypes.LADDER, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'E':
                        Location enemyLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        EnemyPlant enemy = new EnemyPlant(this, enemyLoc);
                        double enemyHeightDiff = enemy.getLocation().getY() - Game.BLOCK_SIZE - 16;
                        enemy.setLocation(enemy.getLocation().getX(), enemyHeightDiff);
                        addEntity(enemy);
                        break;
                    case 'B':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.BARRIER, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'W':
                        grid.setBlock(x, relY, new BlockLiquid(BlockTypes.WATER_TOP, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'O':
                        grid.setBlock(x, relY, new BlockLiquid(BlockTypes.WATER_BOTTOM, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'S':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.STONE_FLOOR, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 's':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.STONE_FILLER, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'l':
                        grid.setBlock(x, relY, new BlockLiquid(BlockTypes.LAVA, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'b':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.BRIDGE, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'm':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.BL, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'r':
                        grid.setBlock(x, relY, new BlockSolid(BlockTypes.BR, new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE)));
                        break;
                    case 'Q':
                        Location skullHeadLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        SkullHead skullHead = new SkullHead(this, skullHeadLoc);
                        addEntity(skullHead);
                        break;
                    case 'C': // Add this case for gold coin
                        Location coinLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        GoldCoin coin = new GoldCoin(this, coinLoc);
                        addEntity(coin);
                        break;
                    case 'M':
                        Location beeLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        Bee bee = new Bee(this, beeLoc);
                        addEntity(bee);
                }
            }
            relY++;
        }
        if (!backgroundImgFilePath.isEmpty()) {
            getManager().getEngine().imageBank.put("background", Toolkit.getDefaultToolkit().createImage(backgroundImgFilePath));
        }
        if (player == null) {
            System.out.println("level.Level error: no player location specified.");
            return;
        }
        if (keyLoc == null) {
            System.out.println("level.Level error: no key location specified.");
            return;
        }
        if (doorLoc == null) {
            System.out.println("level.Level error: no door location specified.");
            return;
        }

        System.out.println("Player: " + player.getLocation().toString());
        System.out.println("Key: " + keyLoc.toString());
        System.out.println("Door: " + doorLoc.toString());
    }

    public Player getPlayer() {
        return player;
    }

    public void reset() {
        getPlayer().setLocation(spawnPoint.getX(), spawnPoint.getY());
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public Door getDoor() {
        return door;
    }

    public String getNextLevel() {
        return nextLevel;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public LevelManager getManager() {
        return manager;
    }

    public int getWidth() {
        return sizeWidth;
    }

    public int getHeight() {
        return sizeHeight;
    }

    public Location getKeyLocation() {
        return keyLoc;
    }

    public Location getDoorLocation() {
        return doorLoc;
    }

    public double getGravity() {
        return 0.98;
    }

    public BlockGrid getBlockGrid() {
        return grid;
    }

    public void addTextMessage(TextMessage text) {
        textMessages.put(textCounter, text);
        textCounter++;
    }

    public HashMap<Integer, TextMessage> getTextMessages() {
        return textMessages;
    }

    public void clearTextMessages() {
        textMessages.clear();
    }
}
