package main;

import block.Block;
import block.BlockTypes;
import block.decorations.Decoration;
import block.decorations.FakeLightSpot;
import entity.Entity;
import entity.EntityLiving;
import entity.Player;
import level.Particle;
import level.TextMessage;

import java.awt.*;
import java.time.Duration;

/*
 *   This class handles the rendering of all objects on the screen.
 *
 *   It works by making the players location the center of the screen and only drawings the objects that are
 *   around the player, taking into account the dimensions of the screen.
 *
 * */
public class Camera {

    public Location loc, centerPoint;
    public Game game;
    public Player player;
    private boolean hasPlayedKeyAudio = false;

    public boolean debugMode;
    private int DEBUG_ENTITIES_ON_SCREEN;
    private int DEBUG_BLOCKS_ON_SCREEN;
    private int DEBUG_DECORATIONS_ON_SCREEN;
    private int DEBUG_PARTICLES_ON_SCREEN;

    private long lastFpsCheck = 0;
    private int currentFps = 0;
    private int totalFrames = 0;
    private CollisionBox collisionBox;

    public double centerOffsetX, centerOffsetY;

    public Camera(Game game, Player p) {
        this.game = game;
        this.player = p;
        this.loc = new Location(p.getLocation().getX(), p.getLocation().getY());

        Location point1 = new Location(p.getLocation().getX() - (game.width() / 2), p.getLocation().getY() - (game.height() / 2));
        this.collisionBox = new CollisionBox(point1.getX(), point1.getY(), game.width(), game.height());
    }

    public void update() {
        calculateFPS();

        this.loc.setX(player.getLocation().getX());
        this.loc.setY(player.getLocation().getY());

        centerPoint = new Location(game.width() / 2, game.height() / 2);
        centerOffsetX = centerPoint.getX() - player.getLocation().getX();
        centerOffsetY = centerPoint.getY() - player.getLocation().getY();

        collisionBox.setLocation(player.getLocation().getX() - (double) game.width() / 2, player.getLocation().getY() - (double) game.height() / 2);
        collisionBox.setSize(game.width(), game.height());
    }

    public void draw() {
        renderBackground();
        renderDecorations();
        renderBlocks();
        renderEntities();
        getPlayer().render(this);
        renderTextMessages();
        renderFX();
        renderSpotLights();
        renderUI();
    }

    private void renderDecorations() {
        DEBUG_DECORATIONS_ON_SCREEN = 0;
        for (Decoration deco : game.getActiveLevel().getDecorations()) {
            if (deco.getCollisionBox().collidesWith(this.getCollisionBox())) {
                deco.render(this);
                DEBUG_DECORATIONS_ON_SCREEN++;
            }
        }
    }

    private void renderSpotLights() {
        for (FakeLightSpot spotLight : game.getActiveLevel().getSpotLights()) {
            if (spotLight.getParent().getCollisionBox().collidesWith(this.getCollisionBox())) {
                spotLight.render(this);
            }
        }
    }

    private void renderBlocks() {
        DEBUG_BLOCKS_ON_SCREEN = 0;
        for (int x = 0; x < game.getActiveLevel().getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < game.getActiveLevel().getBlockGrid().getHeight(); y++) {
                Block b = game.getActiveLevel().getBlockGrid().getBlocks()[x][y];
                if (b.getType() == BlockTypes.VOID || b.getType() == BlockTypes.BARRIER) {
                    continue;
                }

                if (b.getLocation().isBlockBetween(getPoint1(), getPoint2())) {
                    double blockOffsetX = b.getLocation().getX() + centerOffsetX;
                    double blockOffsetY = b.getLocation().getY() + centerOffsetY;

                    b.drawBlock(this, blockOffsetX, blockOffsetY);
                    DEBUG_BLOCKS_ON_SCREEN++;
                }
            }
        }
    }

    public void renderEntities() {
        DEBUG_ENTITIES_ON_SCREEN = 0;
        for (Entity entity : game.getActiveLevel().getEntities()) {
            if (!entity.isActive()) {
                continue;
            }

            if (entity.getCollisionBox().collidesWith(this.getCollisionBox())) {
                entity.render(this);
                double offsetX = entity.getLocation().getX() + centerOffsetX;
                double offsetY = entity.getLocation().getY() + centerOffsetY;

                if(entity.getHealth() < entity.getMaxHealth()){
                    drawHealthBar(entity, offsetX, offsetY - 50);
                }

                DEBUG_ENTITIES_ON_SCREEN++;
            }
        }
    }

    public void renderTextMessages() {
        for (TextMessage txtMsg : game.getActiveLevel().getTextMessages().values()) {
            if (txtMsg == null) {
                continue;
            }

            double localXDiff = txtMsg.getLocation().getX();
            double localYDiff = txtMsg.getLocation().getY();
            if (!txtMsg.isStatic()) {
                localXDiff += centerOffsetX;
                localYDiff += centerOffsetY;
            }

            game.changeColor(txtMsg.getColor());
            if (!txtMsg.isBold()) {
                game.drawText(localXDiff, localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            } else {
                game.drawBoldText(localXDiff, localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            }
        }
    }

    public void renderUI() {
        if (game.isPaused) {
            game.changeColor(Color.orange);
            game.drawText((game.width() / 2) - 100, game.height() / 2, "Paused", 75);
            game.drawText((game.width() / 2) - 110, game.height() / 2 + 100, "Press 'Q' to Quit.", 40);
            return;
        }

        Location healthBarLoc = new Location(50, 35);

        double localXDiff = healthBarLoc.getX();
        double localYDiff = healthBarLoc.getY();

        game.changeColor(Color.white);
        game.drawText(50,35,"Health:",15);
        game.drawText(1180,50,"Key : ", 20);
        game.drawText(150,35,"Score : " + player.getScore(), 20);

        // Display the timer
        Duration elapsedTime = game.lvlManager.getElapsedTime();
        long minutes = elapsedTime.toMinutes();
        long seconds = elapsedTime.toSecondsPart();
        game.drawText(600, 35, String.format("Time: %02d:%02d", minutes, seconds), 20);

        if (game.getActiveLevel().getPlayer().hasKey()) {
            game.drawImage(game.imageBank.get("key"), 1230, 20, 50, 50);
        }

        drawHealthBar(player, localXDiff, localYDiff);
        game.changeColor(Color.red);
        game.drawText(localXDiff + 50, localYDiff, String.valueOf(player.getHealth()), 20);

        if (debugMode) { //Press 'H' to enable
            game.changeColor(Color.yellow);
            game.drawText(25, 100, "fps: " + currentFps, "Serif", 20);
            game.drawText(25, 120, "entities on screen: " + DEBUG_ENTITIES_ON_SCREEN, "Serif", 20);
            game.drawText(25, 140, "blocks on screen: " + DEBUG_BLOCKS_ON_SCREEN, "Serif", 20);
            game.drawText(25, 160, "decorations on screen: " + DEBUG_DECORATIONS_ON_SCREEN, "Serif", 20);
            game.drawText(25, 180, "particles on screen: " + DEBUG_PARTICLES_ON_SCREEN, "Serif", 20);
            game.drawText(25, 220, "player:", "Serif", 20);
            game.drawText(35, 240, "pos: " + getPlayer().getLocation().toString(), "Serif", 20);
            game.drawText(35, 260, "velocity: " + Math.round(getPlayer().moveX) + ", " + Math.round(getPlayer().moveY), "Serif", 20);
            if (getPlayer().getTarget() != null) {
                game.drawText(35, 280, "target: " + getPlayer().getTarget().toString(), "Serif", 20);
            } else {
                game.drawText(35, 280, "target: null", "Serif", 20);
            }
            game.drawText(35, 300, "onGround: " + getPlayer().isOnGround(), "Serif", 20);
            game.drawText(35, 320, "hasKey: " + getPlayer().hasKey(), "Serif", 20);

            double hitboxOffsetX = getCollisionBox().getLocation().getX() + centerOffsetX;
            double hitboxOffsetY = getCollisionBox().getLocation().getY() + centerOffsetY;
            game.changeColor(Color.RED);
            game.drawRectangle(hitboxOffsetX, hitboxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    public void renderFX() {
        if (game.getActiveLevel().getId() == 3) {
            game.drawImage(game.getTexture("snowFall"), 0, 0, game.width(), game.height());
        }
        DEBUG_PARTICLES_ON_SCREEN = 0;
        for (Particle particle : game.getActiveLevel().getParticles()){
            particle.render(this);
            DEBUG_PARTICLES_ON_SCREEN++;
        }

        if (game.imageBank.get("overlay") != null) {
            game.drawImage(game.imageBank.get("overlay"), 0, 0, game.width(), game.height());
        }
    }

    private void renderBackground() {
        if (game.imageBank.get("background") != null) {
            game.drawImage(game.imageBank.get("background"), 0, 0, game.width(), game.height());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public Location getPoint1() {
        return getCollisionBox().getLocation();
    }

    public Location getPoint2() {
        return getCollisionBox().getCorner();
    }

    private void calculateFPS() {
        totalFrames++;
        if (System.nanoTime() > lastFpsCheck + 1000000000) {
            lastFpsCheck = System.nanoTime();
            currentFps = totalFrames;
            totalFrames = 0;
        }
    }

    public void drawHealthBar(Entity entity, double xPos, double yPos) {
        double difference = (double) 100 / entity.getMaxHealth();
        double barSize = entity.getHealth() * difference;

        game.changeColor(Color.red);
        game.drawSolidRectangle(xPos,yPos, barSize, 15);
        game.changeColor(Color.darkGray);
        game.drawSolidRectangle(xPos + barSize,yPos, 100 - barSize, 15);
    }
}

