package com.github.overidle.overidleLauncher;

import fr.theshark34.swinger.util.WindowMover;

import javax.swing.*;

public class LauncherFrame extends JFrame {

    private LauncherPanel launcherPanel = new LauncherPanel(this);

    public LauncherFrame() {
        super();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        this.setTitle("Over-Idle Launcher");
        this.setSize(850, 489);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //TODO A voir
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setIconImage(new ImageIcon(getClass().getResource("/icone.png")).getImage());
        this.setContentPane(launcherPanel);
        WindowMover mover = new WindowMover(this);
        this.addMouseListener(mover);
        this.addMouseMotionListener(mover);
        this.setOpacity(0.90f);
        this.setVisible(true);
    }

    public LauncherPanel getLauncherPanel() {
        return launcherPanel;
    }



}
