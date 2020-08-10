package com.github.overidle.overidleLauncher;



import com.sunproject.minebootApi.api.MineBootModuleType;
import com.sunproject.minebootApi.api.init.MineBootAPiInit;
import com.sunproject.minebootApi.api.providers.modulemanager.MineBootModule;

import java.io.File;
import java.io.IOException;

public class Main {

    private static MineBootModule overIdleMineBootLauncher;

    public static void main(String[] args) throws IOException, NoSuchMethodException {
        System.out.println(new File("./").getAbsolutePath());
        overIdleMineBootLauncher = new MineBootModule("overIdleLauncher", MineBootModuleType.ModuleType.BASIC, 1) {
            @Override
            public void onEnable() {
                System.out.println("Starting " + overIdleMineBootLauncher.getModuleName() + " ...");
                try {
                    Launcher.up();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisable() {
                System.out.println("Stopping " + overIdleMineBootLauncher.getModuleName() + " ...");
                Launcher.down();
            }
        };

        MineBootAPiInit.initMineBootAPI();
        MineBootAPiInit.getModuleManager().enableModule(overIdleMineBootLauncher);
    }
}
