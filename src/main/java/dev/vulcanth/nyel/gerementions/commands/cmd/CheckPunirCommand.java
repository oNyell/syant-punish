package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.gerementions.enums.reason.Reason;
import dev.vulcanth.nyel.gerementions.enums.reason.ReasonRevogar;
import dev.vulcanth.nyel.gerementions.punish.dao.PunishDao;
import dev.vulcanth.nyel.gerementions.util.Util;
import dev.vulcanth.nyel.player.role.Role;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;


public class CheckPunirCommand extends Commands {

    public CheckPunirCommand() {
        super("despunir", "checkpunir");
    }

    private static PunishDao punishDao;
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");

    @Override
    public void perform(CommandSender sender, String[] args) {

    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("role.admin")) {
            player.sendMessage(TextComponent.fromLegacyText("§fComando desconhecido."));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize /checkpunir <jogador>."));
            return;
        }
        String target = args[0];


        player.sendMessage(TextComponent.fromLegacyText("§eRevogando punições do " + target + "§e:"));
        player.sendMessage(TextComponent.fromLegacyText(" "));

        if (punishDao.getPunishService().getPunishes().stream().anyMatch(punish -> punish.getPlayerName().equalsIgnoreCase(target))) {
            punishDao.getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equalsIgnoreCase(target)).forEach(punish -> {

                TextComponent text = new TextComponent("");
                text.setText("§e§l[§e"+ SDF.format(punish.getDate()) + "§e§l] §e§l[§e" + punish.getReasona().getText() + "§e§l] ");
                text.addExtra("§f§l[§fRevogar§f§l]");
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para revogar.")));
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revogar " + target));
                sender.sendMessage(text);
            });
        } else {
            player.sendMessage(TextComponent.fromLegacyText("§fNenhuma."));
        }
        player.sendMessage(TextComponent.fromLegacyText(" "));

    }
}

