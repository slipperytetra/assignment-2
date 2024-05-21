package main;

public enum EntityType {

    PLAYER("resources/images/characters/idle.png"),
    DOOR("resources/images/blocks/door.png"),
    KEY("resources/images/blocks/key.png"),
    PLANT_MONSTER("resources/images/characters/plant_monster.png"),
    SKULL_HEAD("resources/images/characters/skull_head"),
    STONE_DOOR("resources/images/blocks/stoneDoor.png");


    private final String filePath;

    EntityType(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
