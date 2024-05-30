package level;

import main.Game;
import main.GameEngine;
import main.Location;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class LevelManager {

    Game engine;

    HashMap<String, Level> levels;
    
    private Instant startTime;
    private Instant endTime;
    private boolean timerRunning;
    private Duration elapsedTime;

    // Define the level variable name.
    public Level DEMO;
    public Level DEMO_2;
    public Level LEVEL_3;
    public Level LEVEL_4;
    public Level FOREST;
    public Level LEVEL_5;
    public Level ROB;
    public Level END;


    public LevelManager(Game engine) {
        this.engine = engine;
        this.levels = new HashMap<>();
        loadLevels();
        elapsedTime = Duration.ZERO;
    }

    public void loadLevels() {
        // Assign the level name to your level. This points to your level's file under resources/levels.
        // Format: *NAME* = new level.Level(id, spawn_location, key_location)
        DEMO = new Level(this, 0, "resources/levels/level_demo.txt");
        LEVEL_3 = new Level(this, 2, "resources/levels/nap_level.txt");
        LEVEL_4 = new Level(this,3,"resources/levels/zyra_map.txt");
        FOREST = new Level(this,4,"resources/levels/level_forest.txt");
        LEVEL_5 = new Level(this,5,"resources/levels/level5.txt");
        END = new Level(this,6,"resources/levels/endLevel.txt");
        ROB = new Level(this,7,"resources/levels/rob_level.txt");




        levels.put("level_demo", DEMO);
        levels.put("nap", LEVEL_3);
        levels.put("zyra", LEVEL_4);
        levels.put("forest", FOREST);
        levels.put("zyra2", LEVEL_5);
        levels.put("rob", ROB);
        levels.put("end", END);

        /*
         *  This is for adding text to the level. You set the x and y coordinates where it should show.
         *  The boolean static means if the text should show at a fixed position in the world e.g. the player
         *  can walk past it or if static is set to true, then the text will show at a constant position on the screen
         *  and follow the player as they move.
         */
        DEMO.addTextMessage(new TextMessage(new Location(0, 100), "Welcome to our game!", 50, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(0, 700), "Press 'D' to move right and 'A' to move left.", 18, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(500, 200), "Hold 'Q' to attack with your sword.", 18, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(0, 750), "Press 'Space' to jump!", 18, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(250, 750), "Avoid the water!!", 18, false, Color.red));
        DEMO.addTextMessage(new TextMessage(new Location(0, 280), "Grab key to unlock door to proceed to next level!", 15, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(950, 670), "Press 'E' on door to enter!", 20, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(950, 720), "Pick up coins for extra score!", 18, false, Color.white));
        DEMO.addTextMessage(new TextMessage(new Location(440, 600),"This is a checkpoint, you will respawn on these if you die",15,true,Color.white));
    }

        // Method to start the timer
    public void startTimer() {
        if (!timerRunning) {
            startTime = Instant.now();
            timerRunning = true;
        }
    }

    // Method to stop the timer
    public void stopTimer() {
        if (timerRunning) {
            endTime = Instant.now();
            elapsedTime = Duration.between(startTime, endTime); // Store the elapsed time
            timerRunning = false;
        }
    }

    // Method to get the elapsed time
    public Duration getElapsedTime() {
        if (timerRunning) {
            return Duration.between(startTime, Instant.now());
        }
        return elapsedTime; // Return the stored elapsed time when the timer is stopped
    }

    public void onTutorialComplete() {
        startTimer();
    }

    public void onLastLevelEnter() {
        stopTimer();
    }

    public Game getEngine() {
        return engine;
    }

    public HashMap<String, Level> getLevels() {
        return levels;
    }
}
