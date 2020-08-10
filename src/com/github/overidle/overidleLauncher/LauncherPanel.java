package com.github.overidle.overidleLauncher;

import com.sunproject.minebootApi.api.init.MineBootAPiInit;
import fr.litarvan.openauth.AuthenticationException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;

public class LauncherPanel extends JPanel {

    private static String userName;
    private LauncherFrame launcherFrame;
    private ImageIcon background = new ImageIcon(getClass().getResource("/background.png"));
    private static JTextField fldUserName = new JTextField(userName);
    private static JPasswordField fldPassWord = new JPasswordField();
    private static ImageIcon playButtonHover = new ImageIcon(LauncherPanel.class.getResource("/jouer.png"));
    private ImageIcon reduceIcon = new ImageIcon(getClass().getResource("/reduire.png"));
    private ImageIcon closeIcon = new ImageIcon(getClass().getResource("/quit.png"));
    private static JButton playButton = new JButton();
    private JButton closeButton = new JButton();
    private JButton reduceButton = new JButton();
    private JButton settingsButton = new JButton();
    private JButton discordLink = new JButton();
    private JButton instaLink = new JButton();
    private JButton webLink = new JButton();
    private JProgressBar updateProgressBar = new JProgressBar();

    private Border buttonsBorder = BorderFactory.createDashedBorder(Color.CYAN, 5, 5);

    private ArrayList<JComponent> components = new ArrayList<>();


    public LauncherPanel(LauncherFrame lFrame) {
        this.launcherFrame = lFrame;
        this.setLayout(null);
        fldUserName.setBounds(530, 240, 295, 43);
        fldPassWord.setBounds(530, 320, 295, 43);
        playButton.setBounds(525, 383, 307, 88);
        reduceButton.setBounds(727, 4, 48, 48);
        closeButton.setBounds(795, 4, 48, 48);
        updateProgressBar.setBounds(5, 458, 510, 25);
        discordLink.setBounds(10, 275, 127, 121);
        instaLink.setBounds(182, 275, 127, 121);
        webLink.setBounds(365, 275, 127, 121);
        //settingsButton.setBounds(); TODO Il me manque les coordonnées du bouton !!!!



        closeButton.setFocusable(false);
        reduceButton.setFocusable(false);

        updateProgressBar.setForeground(new Color(51, 204, 255));
        updateProgressBar.setFont(new Font(null, Font.BOLD, 16));
        fldUserName.setCaretColor(Color.WHITE);
        fldUserName.setForeground(Color.WHITE);
        fldPassWord.setCaretColor(Color.WHITE);
        fldPassWord.setForeground(Color.WHITE);

        reduceButton.addMouseListener(hoverListener(reduceButton, reduceIcon));
        reduceButton.addActionListener(e -> lFrame.setState(JFrame.ICONIFIED));
        closeButton.addMouseListener(hoverListener(closeButton, closeIcon));
        closeButton.addActionListener(e -> MineBootAPiInit.shutdownApi()); //TODO a revoir
        playButton.addMouseListener(hoverListener(playButton, playButtonHover));
        playButton.addActionListener(e -> auth(fldUserName, fldPassWord));



        discordLink.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new URI("https://discord.gg/PTTJy6q")); }
            catch (Exception ex) { ex.printStackTrace();}
        });
        instaLink.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new URI("https://instagram.com/over_idle_officiel?igshid=17xvzs8ay5ciu")); }
            catch (Exception ex) { ex.printStackTrace();}
        });

        webLink.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new URI("")); }
            catch (Exception ex) { ex.printStackTrace();}
        });


        components.add(closeButton);
        components.add(reduceButton);
        components.add(playButton);
        components.add(updateProgressBar);
        components.add(fldUserName);
        components.add(fldPassWord);
        components.add(instaLink);
        components.add(webLink);
        components.add(discordLink);

        for(JComponent component : components) {
            component.setBorder(null);
            component.setOpaque(false);
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    component.setBorder(BorderFactory.createDashedBorder(Color.CYAN, 5, 5));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    component.setBorder(null);
                }
            });
            component.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    component.setBorder(BorderFactory.createDashedBorder(Color.CYAN, 5, 5));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    component.setBorder(null);
                }
            });
            this.add(component);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
    }


    private static MouseListener hoverListener(JButton button, ImageIcon image) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(image);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(null);
            }
        };
    }

    private void auth(JTextField fldUserName, JPasswordField fldPassWord) {
        if (!fldUserName.getText().replaceAll(" ", "").isEmpty() && !fldPassWord.getText().replaceAll(" ", "").isEmpty()) {
           toggleInputFeatures(false);
           getUpdateProgressBar().setStringPainted(true);
            Thread authThread = new Thread(() -> {
                try {
                    getUpdateProgressBar().setString("Authenticating ...");
                    Launcher.auth(fldUserName.getText(), fldPassWord.getText());
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                    getUpdateProgressBar().setString("Failed to authenticate ...");
                    JOptionPane.showMessageDialog(this, "An error occurred while authenticating !!", "Error !", JOptionPane.ERROR_MESSAGE);
                    toggleInputFeatures(true);
                }

                if (Launcher.isAuthed()) {
                    try {
                        Launcher.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Sorry, an error occurred !!", "Error !", JOptionPane.ERROR_MESSAGE);
                        toggleInputFeatures(true);
                    }
                }

            });
            authThread.setDaemon(true);
            authThread.start();


        } else {
            JOptionPane.showMessageDialog(this, "Please required fields !!!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public JProgressBar getUpdateProgressBar() {
        return updateProgressBar;
    }

    public static void toggleInputFeatures(boolean value) {
        fldUserName.setEnabled(value);
        fldPassWord.setEnabled(value);
        if (!value) {
            playButton.removeMouseListener(playButton.getMouseListeners()[1]);
        } else {
            playButton.addMouseListener(hoverListener(playButton, playButtonHover));
        }
        playButton.setEnabled(value);
    }

}
