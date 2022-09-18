package com.ayane.nyel.listeners;

import com.ayane.nyel.Main;
import com.ayane.nyel.punish.dao.PunishDao;
import com.ayane.nyel.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;

public class Listeners implements Listener {

    private PunishDao punishDao;

    public Listeners() {
        punishDao = Main.getInstance().getPunishDao();
    }

    @EventHandler
    public void login(LoginEvent event) {
        String name = event.getConnection().getName();

        punishDao.clearPunishes(name);
        punishDao.isBanned(name).findAny().ifPresent(punish -> {
            event.setCancelled(true);

            String proof = (punish.getProof() == null ? "Indisponível" : punish.getProof());
            event.setCancelReason(TextComponent.fromLegacyText("§c§lSYANTMC\n\n§cVocê está banido permanentemente do servidor.\n" +
                    "\n§cMotivo: " + punish.getReason().getText() + " - " + proof +
                    "\n§cAutor da punição: §7" + punish.getStafferName() +
                    "\n§cID da punição: §e#" + punish.getId() +
                    "\n\n§cUse o ID §e#" + punish.getId() + " §cpara criar uma revisão em SYANTMC §n§csyantmc.com/discord"));
        });
    }

    @EventHandler
    public void chat(ChatEvent event) {

        //ve agora
        ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getSocketAddress().equals(event.getSender().getSocketAddress())).findAny().ifPresent(player -> {
            punishDao.clearPunishes(player.getName());

            punishDao.isMuted(player.getName()).findAny().ifPresent(punish -> {
                List<String> commands = Arrays.asList("/tell", "/g", "/r", "/.", "/l", "/p", "/s");

                String proof = (punish.getProof() == null ? "Indisponível" : punish.getProof());
                String message = event.getMessage();

                if (event.isCommand()) {
                    if (commands.stream().noneMatch(s -> message.startsWith(s) || message.startsWith(s.toUpperCase()) || message.equalsIgnoreCase(s))) {
                        event.setCancelled(false);
                        return;
                    }
                    if (message.startsWith("/report") || message.startsWith("/reportar") ||
                            message.equalsIgnoreCase("/lobby") ||
                            message.startsWith("/logar") || message.startsWith("/login") || message.equalsIgnoreCase("/rejoin") ||
                            message.equalsIgnoreCase("/reentrar") || message.equalsIgnoreCase("/leave") || message.equalsIgnoreCase("/loja")) {
                        event.setCancelled(false);
                        return;

                        // tu n ta salvando o ?????pera a ia
                    }
                }
                if (!player.getServer().getInfo().getName().equalsIgnoreCase("ss")) {
                    event.setCancelled(true);
                    player.sendMessage(TextComponent.fromLegacyText("\n§c* Você estará silenciado " + (punish.getReason().getTime() > 0 ? "por mais §7" + Util.fromLong(punish.getExpire()) : "para sempre") +
                            "\n\n§c* Motivo: " + punish.getReason().getText() + " - " + proof +
                            "\n§c* Autor: " + punish.getStafferName() +
                            "\n§c* Use o id §e#" + punish.getId() + "§c para criar uma revisão em §n§csyantmc.com/discord" +
                            "\n"));
                }
            });
        });
    }

}
