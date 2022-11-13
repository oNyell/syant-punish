package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.gerementions.punish.dao.PunishDao;
import dev.vulcanth.nyel.player.role.Role;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class DespunirCommand extends Commands {

    public DespunirCommand() {
        super("despunir", "revogar");
    }

    private static PunishDao punishDao;

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
        if (!player.hasPermission("role.gerente")) {
            player.sendMessage(TextComponent.fromLegacyText("§fComando desconhecido."));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize /despunir <jogador>§c."));
            return;
        }
        String target = args[0];


        player.sendMessage(TextComponent.fromLegacyText("§fPunições do jogador " + Role.getColored(target) + "§f:"));
        player.sendMessage(TextComponent.fromLegacyText(" "));

        if (punishDao.getPunishService().getPunishes().stream().anyMatch(punish -> punish.getPlayerName().equalsIgnoreCase(target))) {
            punishDao.getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equalsIgnoreCase(target)).forEach(punish -> {
                TextComponent text = new TextComponent("§7#" + punish.getId() + " §7- §f" + punish.getReason().getText());
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para revogar essa punição.")));
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/did " + punish.getId()));

                player.sendMessage(text);
            });
        } else {
            player.sendMessage(TextComponent.fromLegacyText("§fNenhuma."));
        }
        player.sendMessage(TextComponent.fromLegacyText(" "));

    }
}

