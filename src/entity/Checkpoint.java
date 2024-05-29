package entity;

import level.Level;
import main.GameEngine;
import main.Location;

import java.awt.*;

public class Checkpoint extends Entity {

    private GameEngine.AudioClip checkpointinnit;
    public Checkpoint(Level level, Location loc) {
        super(EntityType.CHECKPOINT, level, loc, 50, 50);
        setScale(1);
        setCanMove(false);
        checkpointinnit = level.getManager().getEngine().loadAudio("resources/sounds/checkpoint.wav");
    }

    public void update(double dt) {
        if (getLevel().getPlayer().getCollisionBox().collidesWith(this.getCollisionBox())) {

            getLevel().getManager().getEngine().playAudio(checkpointinnit);
            destroy();
            getLevel().setSpawn(getLocation());
        }
    }
    @Override
    public Image getActiveFrame() {
        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public double getWidth() {
        return 50 * getScale();
    }

    public double getHeight() {
        return 50 * getScale();
    }

    @Override
    public void processMovement(double dt) {
        return;
    }

    public boolean isFlipped(){
        return true;
    }
}


