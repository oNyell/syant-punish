package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import dev.vulcanth.nyel.gerementions.enums.reason.Reason;
import dev.vulcanth.nyel.gerementions.punish.Punish;
import dev.vulcanth.nyel.gerementions.punish.dao.PunishDao;
import dev.vulcanth.nyel.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.stream.Stream;

public class BanCommand extends Commands {
    public BanCommand() {
        super("ban", "vban", "banip", "ban-ip");
    }
    public Punish punish;

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("role.gerente")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cSomente Gerente ou superior podem executar este comando."));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize \"/ban <user> <duração> [motivo]"));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (impossibleToBan(target.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode banir esse usuário"));
            return;
        }

        String format = StringUtils.formatColors(StringUtils.join((Object[])args, " "));
        if (format == null) {
            sender.sendMessage(TextComponent.fromLegacyText("\n§eBanimento aplicado com sucesso."));
        }
        ProxyServer.getInstance().getPlayers().stream().filter(player -> player.hasPermission("vulcanth.cmd.punir")).forEach(player -> {
            player.sendMessage(TextComponent.fromLegacyText("§c- " + punish.getPlayerName() + " §cfoi banido por " + sender.getName() +
                    "\n§c- Motivo: " + format +
                    "\n§c- Duração: Permanente\n"));
        });
        PunishDao punish = new PunishDao();
        apply(punish.createPunish(target.getName(), sender.getName(), Reason.NENHUM, null, PunishType.BAN.name()), target, sender.getName());
    }
    private void apply(Punish punish, ProxiedPlayer player, String staffer) {
        ProxyServer.getInstance().getPlayers().stream().filter(online -> online.hasPermission("vulcanth.cmd.punir")).forEach(online -> {
            online.sendMessage(TextComponent.fromLegacyText("§c- " + punish.getPlayerName() + " §cfoi banido por " + staffer+
                    "\n§c- Motivo: " + punish.getReason() +
                    "\n§c- Duração: Permanente\n"));
        });
    }
    private static boolean impossibleToBan(String nickName) {
        return Stream.of("NyellPlay").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }
}
