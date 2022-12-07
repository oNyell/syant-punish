package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.Commands;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Iterator;

public class YTCommand extends Commands {

    public YTCommand() {
        super("parceiros");
    }

    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {

            HashMap<ProxiedPlayer, String> yt = new HashMap<>();
            HashMap<ProxiedPlayer, String> streamer = new HashMap<>();
            for (String s : Main.getInstance().getProxy().getServers().keySet()) {
                ServerInfo serverInfo = (ServerInfo)Main.getInstance().getProxy().getServers().get(s);
                for (ProxiedPlayer todos : ProxyServer.getInstance().getPlayers()) {
                    sender.sendMessage(TextComponent.fromLegacyText("\n§e Parceiros online neste momento:"));
                    if (todos.hasPermission("lista.youtube")) {
                        int che = 0;

                        if (todos.hasPermission("role.youtuber")) {
                            yt.put(todos, todos.getName());
                            che = 1;
                        }
                        if (todos.hasPermission("role.streamer") && che == 0) {
                            streamer.put(todos, todos.getName());
                            boolean tags = true;
                        }
                    } else {
                        sender.sendMessage(TextComponent.fromLegacyText("\n§cNenhum parceiro online no momento."));
                        return;
                    }
                }
            }
            Iterator iterator = Main.getInstance().getProxy().getServers().keySet().iterator();


            iterator = yt.keySet().iterator();

            String ch;
            ProxiedPlayer todos;
            while(iterator.hasNext()) {
                todos = (ProxiedPlayer)iterator.next();
                ch = "";
                sender.sendMessage(TextComponent.fromLegacyText(" §f- " + getTag("role.youtuber") + " " + todos.getName() + " §f(" + todos.getServer().getInfo().getName().replaceAll("login", "Login").replaceAll("lby-room01", "Lobby").replaceAll("bw-room01", "BedWars").replaceAll("SkyWars", "SkyWars") + ") " + ch));
            }

            iterator = streamer.keySet().iterator();

            while(iterator.hasNext()) {
                todos = (ProxiedPlayer)iterator.next();
                ch = "";
                sender.sendMessage(TextComponent.fromLegacyText(" §f- " + getTag("role.streamer") + " " + todos.getName() + " §f(" + todos.getServer().getInfo().getName().replaceAll("login", "Login").replaceAll("lby-room01", "Lobby").replaceAll("bw-room01", "BedWars").replaceAll("SkyWars", "SkyWars") + ") " + ch));
            }

            sender.sendMessage(TextComponent.fromLegacyText("§2"));
            return;
        }
    }

    public static String getTag(String grup) {
        String tag;
        switch (grup) {
            case "role.youtuber":
                tag = "§c[YouTuber]";
                return tag;
            case "role.streamer":
                tag = "§9[Streamer]";
                return tag;
            default:
                return null;
        }
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
