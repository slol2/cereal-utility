package me.cereal.utility;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class Discord {

    public static final String APP_ID = " ";

    public static DiscordRichPresence presence;

    public static boolean connected;

    public static void start() {
        CerealMod.log.info("Starting Discord RPC");
        if (connected)
            return;
        connected = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize(" ", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        setRpcFromSettings();
        (new Thread(Discord::setRpcFromSettingsNonInt, "Discord-RPC-Callback-Handler")).start();
        CerealMod.log.info("Discord RPC initialised successfully");
    }

    public static void end() {
        CerealMod.log.info("Shutting down Discord RPC...");
        connected = false;
        rpc.Discord_Shutdown();
    }

    public static String getIGN() {
        if ((Minecraft.getMinecraft()).player != null)
            return (Minecraft.getMinecraft()).player.getName();
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public static String getIP() {
        if (Minecraft.getMinecraft().getCurrentServerData() != null)
            return (Minecraft.getMinecraft().getCurrentServerData()).serverIP;
        if (Minecraft.getMinecraft().isIntegratedServerRunning())
            return "Singleplayer";
        return "Main Menu";
    }

    private static void setRpcFromSettingsNonInt() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                rpc.Discord_RunCallbacks();
                details = getIGN();
                state = getIP();
                presence.details = details;
                presence.state = state;
                rpc.Discord_UpdatePresence(presence);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private static void setRpcFromSettings() {
        details = getIGN();
        state = getIP();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.state = "aaaaaaaaaaaaaaaaaaaaaaaaa";
        presence.details = "discord.gg/2b2tshop";

    }

    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;

    private static String details;

    private static String state;

    static {
        presence = new DiscordRichPresence();
        connected = false;
    }
}