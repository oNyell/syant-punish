package com.ayane.nyel.commands.cmd;

import com.ayane.nyel.commands.Commands;
import dev.ayane.pewd.player.role.Role;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.stream.Stream;

public class KickCommand extends Commands {

    public KickCommand() {
        super("kick");
    }


    @Override
    public void perform(CommandSender sender, String[] args) {

    }


    private static boolean impossibleToBan(String nickName) {
        return Stream.of("NyellPlay", "oPewdBR", "_KyuraKing_").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) (sender);
        if (!player.hasPermission("ayane.cmd.kick")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente ADMIN ou superior podem executar este comando."));
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§c§lINFO: §cUtilize /kick <player> <motivo>§c."));
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        String reason = args[1];

        if (target == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cO jogador §f" + args[0] + "§c não encontra-se presente."));
            return;
        }
        if (impossibleToBan(target.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode kickar este jogador."));
            return;
        }
        target.disconnect(TextComponent.fromLegacyText("§c§lSYANTMC\n\n§cVocê foi expulso da rede\n\n§cAutor da expulsão: §7" + sender.getName() +
                "\n§cMotivo da expulsão: " + reason + "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão acessando: §esyantmc.com/discord"));
        sender.sendMessage(TextComponent.fromLegacyText("§cVocê expulsou o jogador " + Role.getColored(target.getName()) + "§a por §f" + reason + "§a."));

    }
}


