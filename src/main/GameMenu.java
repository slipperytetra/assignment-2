package main;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameMenu {
    private JFrame frame;
    private JLabel backgroundLabel;
    private Clip menuMusic;

    public GameMenu() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 700);

        JLabel titleLabel = new JLabel("Our Game's Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setOpaque(false);

        ImageIcon backgroundIcon = new ImageIcon("resources/images/bgVid.gif");
        Image img = backgroundIcon.getImage().getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_DEFAULT);
        backgroundLabel = new JLabel(new ImageIcon(img));
        frame.setContentPane(backgroundLabel);
        backgroundLabel.setLayout(new GridBagLayout());

        JButton startGameButton = createButton("Start Game");
        startGameButton.addActionListener(e -> {
            stopMenuMusic();
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(startGameButton);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            new Game().startGame();
        });

        JButton infoButton = createButton("Game Info");
        infoButton.addActionListener(e -> showInfo());

        JButton quitButton = createButton("Quit Game");
        quitButton.addActionListener(e -> System.exit(0));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Adding a vertical space component to push the title label lower
        constraints.gridy = 0;
        constraints.weighty = 0.2; // Adjust this value to push the title label lower
        backgroundLabel.add(Box.createVerticalGlue(), constraints);

        constraints.gridy = 1;
        constraints.weighty = 0;
        backgroundLabel.add(titleLabel, constraints);

        constraints.gridy = 2;
        constraints.weighty = 1.0; // Use this to push the buttons to the bottom
        backgroundLabel.add(Box.createVerticalGlue(), constraints);

        constraints.gridy = 3;
        constraints.weighty = 0;
        backgroundLabel.add(startGameButton, constraints);
        constraints.gridy = 4;
        backgroundLabel.add(infoButton, constraints);
        constraints.gridy = 5;
        backgroundLabel.add(quitButton, constraints);

        try {
            File menuMusicFile = new File("resources/sounds/menuMusic.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(menuMusicFile);
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioIn);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40)); // Adjust the size as needed
        button.setFont(new Font("Arial", Font.PLAIN, 18)); // Adjust the font size as needed
        return button;
    }

    private void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.close();
        }
    }

    private void showInfo() {
        JFrame infoFrame = new JFrame("Game Info");
        infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoTextArea);

        try {
            File readmeFile = new File("resources/gameInfo.txt");
            if (readmeFile.exists()) {
                FileReader reader = new FileReader(readmeFile);
                infoTextArea.read(reader, "resources/gameInfo.txt");
            } else {
                JOptionPane.showMessageDialog(frame, "README file not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to open README file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        infoFrame.add(scrollPane);
        infoFrame.pack();
        infoFrame.setVisible(true);
    }
}
