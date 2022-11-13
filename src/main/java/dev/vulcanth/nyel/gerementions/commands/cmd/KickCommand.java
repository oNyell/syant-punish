package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.player.role.Role;
import dev.vulcanth.nyel.utils.StringUtils;
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
        return Stream.of("NyellPlay").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) (sender);
        if (!player.hasPermission("vulcanth.cmd.kick")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente Moderador ou superior podem executar este comando."));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize /kick <player> <motivo>§c."));
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        String format = StringUtils.formatColors(StringUtils.join((Object[])args, " "));
        if (target == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cO jogador " + args[0] + "§c não encontra-se presente."));
            return;
        }

        if (impossibleToBan(target.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode kickar este jogador."));
            return;
        }
        target.disconnect(TextComponent.fromLegacyText("§c§lVULCANTH\n\n§cVocê foi desconectado do servidor por §c" + sender.getName()));
        sender.sendMessage(TextComponent.fromLegacyText("§cVocê expulsou o jogador " + Role.getColored(target.getName()) + "§c por §f" + format + "§a."));

    }
}


