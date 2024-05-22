package main;

import level.Level;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkullHead extends Enemy {
    private List<BufferedImage> frames;
    private int currentFrame;
    private long lastFrameTime, frameDuration, lastAttackTime, attackCooldown = 1500;
    private double upwardRange = 160, downwardRange = 5, speed = 50, startY;;
    private boolean movingUp = true;
    private Clip damageSound;

    public SkullHead(Level level, Location loc) {
        super(level, EntityType.SKULL_HEAD, loc, 30, 28);
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.frameDuration = 100;

        this.startY = loc.getY();
        this.setDamage(30); // Set the damage amount

        this.lastAttackTime = 0;

        loadFrames();
        loadSound();
    }

    private void loadFrames() {
        for (int i = 0; i < 15; i++) {
            String path = EntityType.SKULL_HEAD.getFilePath() + "_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                frames.add((BufferedImage) Game.flipImageHorizontal(frame));
            } else {
                System.err.println("Error: Could not load frame from path: " + path);
            }
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Error: File not found at path " + path);
                return null;
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadSound() {
        try {
            File soundFile = new File("resources/sounds/skullheadDmg.wav"); // Ensure the sound file is a .wav file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            damageSound = AudioSystem.getClip();
            damageSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processMovement(double dt) {
        double moveY = (movingUp ? -1 : 1) * speed * dt;

        // Update the Y position
        setLocation(getLocation().getX(), getLocation().getY() + moveY);

        // Check if we've reached the movement bounds and reverse direction if necessary
        if (movingUp && getLocation().getY() <= startY - upwardRange) {
            movingUp = false;
        } else if (!movingUp && getLocation().getY() >= startY + downwardRange) {
            movingUp = true;
        }

        // Check for collision with player
        if (canAttack()) {
            attack();
        }
    }

    @Override
    public void attack() {
        Player player = getLevel().getPlayer();
        if (player != null) {
            player.setHealth(player.getHealth() - getDamage());
            System.out.println("Player hit! Health remaining: " + player.getHealth());
            lastAttackTime = System.currentTimeMillis();
            playDamageSound();
        }
    }

    @Override
    public boolean hasAttackAnimation() {
        return false;
    }

    @Override
    public boolean canAttack() {
        Player player = getLevel().getPlayer();
        long currentTime = System.currentTimeMillis();
        return player != null && player.getCollisionBox().collidesWith(this.getCollisionBox()) && (currentTime - lastAttackTime) >= attackCooldown;
    }

    private void playDamageSound() {
        if (damageSound != null) {
            damageSound.setFramePosition(0); // Rewind to the beginning
            damageSound.start();
        }
    }

    @Override
    public void render(Camera cam) {
        updateAnimation();
        double offsetX = getLocation().getX() + cam.centerOffsetX;
        double offsetY = getLocation().getY() + cam.centerOffsetY;

        if (frames.isEmpty()) {
            System.err.println("Error: No frames to render.");
            return;
        }

        BufferedImage currentFrame = frames.get(this.currentFrame);
        if (currentFrame == null) {
            System.err.println("Error: Current frame is null");
            return;
        }

        getLevel().getManager().getEngine().drawImage(currentFrame, offsetX, offsetY, getWidth(), getHeight());

        if (cam.showHitboxes) {
            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }
    }

    @Override
    public Image getActiveFrame() {
        return frames.get(currentFrame);
    }

    @Override
    public double getWidth() {
        return frames.get(currentFrame).getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        return frames.get(currentFrame).getHeight() * getScale();
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}

