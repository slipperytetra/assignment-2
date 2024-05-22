package main;

import level.Level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
<<<<<<< HEAD
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
=======
>>>>>>> origin/napn-v2

public class GoldCoin extends Entity {
    private List<BufferedImage> frames;
    private int currentFrame;
    private long lastFrameTime, frameDuration;
    private boolean collected;
<<<<<<< HEAD
    private Clip coinSound;
=======
>>>>>>> origin/napn-v2

    public GoldCoin(Level level, Location loc) {
        super(EntityType.GOLD_COIN, level, loc, 18, 18);
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.frameDuration = 100;
        this.collected = false;
        loadFrames();
<<<<<<< HEAD
        loadSound();
=======
>>>>>>> origin/napn-v2
    }

    private void loadFrames() {
        for (int i = 0; i < 9; i++) {
            String path = EntityType.GOLD_COIN.getFilePath() + "_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                frames.add(frame);
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

<<<<<<< HEAD
    private void loadSound(){
        try{
            File soundFile = new File("resources/sounds/coin.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            coinSound = AudioSystem.getClip();
            coinSound.open(audioInputStream);
        } catch(Exception e){
            e.printStackTrace();
        }
    }



=======
>>>>>>> origin/napn-v2
    @Override
    public void render(Camera cam) {
        if (!collected) {
            updateAnimation();
            double offsetX = getLocation().getX() + cam.centerOffsetX;
            double offsetY = getLocation().getY() + cam.centerOffsetY;

            BufferedImage currentFrame = frames.get(this.currentFrame);
            getLevel().getManager().getEngine().drawImage(currentFrame, offsetX, offsetY, getWidth(), getHeight());

            if (cam.showHitboxes) {
                double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
                double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

                getLevel().getManager().getEngine().changeColor(getHitboxColor());
                getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
            }
        }
    }

    @Override
    public void processMovement(double dt) {
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
<<<<<<< HEAD
        if(coinSound != null){
            coinSound.setFramePosition(0);
            coinSound.start();
        }
=======
>>>>>>> origin/napn-v2
    }
}
