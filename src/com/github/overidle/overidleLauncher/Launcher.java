package com.github.overidle.overidleLauncher;

import com.sunproject.minebootApi.api.init.MineBootAPiInit;
import com.sunproject.sunupdate.About;
import com.sunproject.sunupdate.DownloadUpdate;
import com.sunproject.sunupdate.GithubAPI;
import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.internal.InternalLaunchProfile;
import fr.theshark34.openlauncherlib.internal.InternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.exception.BadServerResponseException;
import fr.theshark34.supdate.exception.BadServerVersionException;
import fr.theshark34.supdate.exception.ServerDisabledException;
import fr.theshark34.supdate.exception.ServerMissingSomethingException;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class Launcher {

    private static boolean isAuthed;
    private static LauncherFrame launcherFrame;
    public static final GameVersion OI_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
    public static final GameInfos OI_INFOS = new GameInfos("Over-Idle_Launcher", OI_VERSION, new GameTweak[]{GameTweak.FORGE});
    private static SUpdate updater;
    private static GameFolder OI_FOLDER = new GameFolder("assets/", "libs/", "natives/", "minecraft.jar");

    public static final File OI_DIR = OI_INFOS.getGameDir();
    private static AuthInfos authInfos;
    private static Thread minecraftInstanceProcess;
    private static Thread updateThread;
    private static SystemTray tray;
    private static TrayIcon trayIcon;
    private static MenuItem quitMni = new MenuItem("Quit ...");
    private static PopupMenu trayMenu = new PopupMenu();
    private static About about = new About("0.9.0", "overIdle-Launcher", "https://api.github.com/repos/Over-Idle/OverIdle-Launcher/releases/latest");


    // public Launcher(GameInfos infos, Authenticator authenticator) {}

    public static void auth(String userName, String passWord) throws AuthenticationException {
        isAuthed = false;
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
        AuthResponse authResponse = authenticator.authenticate(AuthAgent.MINECRAFT, userName, passWord, "");
        authInfos = new AuthInfos(authResponse.getSelectedProfile().getName(), authResponse.getAccessToken(), authResponse.getSelectedProfile().getId());
        if (authInfos.getUuid() != null) isAuthed = true;
    }

    public static void update() throws BadServerResponseException, IOException, BadServerVersionException, ServerDisabledException, ServerMissingSomethingException {
        updater = new SUpdate("http://37.44.237.59:458", OI_DIR);
        updateThread = new Thread(() -> {
            int val;
            int max;
            int curCalcValue;

           while (!updateThread.isInterrupted()) {
               launcherFrame.getLauncherPanel().getUpdateProgressBar().setStringPainted(true);
               if (BarAPI.getNumberOfFileToDownload() == 0) {
                   launcherFrame.getLauncherPanel().getUpdateProgressBar().setString("Verifying files ...");
               } else {
                   val = (int) BarAPI.getNumberOfTotalDownloadedBytes();
                   max = (int) BarAPI.getNumberOfTotalBytesToDownload();

                   curCalcValue = (int)((double)val / (double)max * (double)100);
                   launcherFrame.getLauncherPanel().getUpdateProgressBar().setMaximum(max);
                   launcherFrame.getLauncherPanel().getUpdateProgressBar().setValue(val);
                   launcherFrame.getLauncherPanel().getUpdateProgressBar().setString("Downloading required files ... ( " + curCalcValue + "% )");
               }
           }
           launcherFrame.getLauncherPanel().getUpdateProgressBar().setString(null);
           launcherFrame.getLauncherPanel().getUpdateProgressBar().setStringPainted(false);
        });

        updateThread.start();
        updater.start();
        stopUpdateThread();
        launch();
    }

    public static void up() throws Exception {

    /*    File updatePath = new File("");

        GithubAPI githubAPI = new GithubAPI(about.getRepoUrl());

        if (githubAPI.getJson() != null) {
            DownloadUpdate.download(githubAPI.getLatestRelease().getBinUrl(), new File(updatePath.getAbsolutePath() + "/" + githubAPI.getLatestRelease().getBinName()));
        }


        System.out.println(githubAPI.getJson());


*/


        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(ImageIO.read(Launcher.class.getResource("/tray.png")), "Over-Idle Launcher", trayMenu);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() != 3) {
                        if (launcherFrame.getState() == JFrame.ICONIFIED) {
                            launcherFrame.setState(JFrame.NORMAL);
                        } else {
                            launcherFrame.setState(JFrame.ICONIFIED);
                        }
                    }
                }
            });

            tray.add(trayIcon);
            quitMni.addActionListener(e -> MineBootAPiInit.shutdownApi());
            trayMenu.add(quitMni);

            DiscordRPC.discordInitialize("742145579035000862", null, true);

            DiscordRichPresence rich = new DiscordRichPresence.Builder("Idle").setDetails("Idle").build();
            DiscordRPC.discordUpdatePresence(rich);
        }

        launcherFrame = new LauncherFrame();
    }

    public static void down() {
        launcherFrame.dispose();
        if (updateThread.isAlive()) stopUpdateThread();
        if (minecraftInstanceProcess.isAlive()) minecraftInstanceProcess.stop();
    }

    public static void stopUpdateThread() {
        if (updateThread != null) updateThread.stop();
    }

    public static void launch() {

        try {
            InternalLaunchProfile profile = MinecraftLauncher.createInternalProfile(OI_INFOS, GameFolder.BASIC, authInfos);
            InternalLauncher launcher = new InternalLauncher(profile);
            minecraftInstanceProcess = new Thread(() -> {
                try {
                    launcher.launch();
                } catch (LaunchException e) {
                    e.printStackTrace();
                }
            });
            minecraftInstanceProcess.start();
            launcherFrame.setVisible(false);

            while (minecraftInstanceProcess.isAlive()) {
                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            }

            launcherFrame.setVisible(true);
            launcherFrame.getLauncherPanel().toggleInputFeatures(true);
        } catch (LaunchException e) {
            e.printStackTrace();
            launcherFrame.setVisible(true);
            JOptionPane.showMessageDialog(null, "An error occurred while launching a minecraft instance !!", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean isAuthed() {
        return isAuthed;
    }
}
