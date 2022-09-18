package com.ayane.nyel.commands.cmd;

import com.ayane.nyel.Main;
import com.ayane.nyel.commands.Commands;
import com.ayane.nyel.punish.Punish;
import com.ayane.nyel.punish.dao.PunishDao;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DespunirIDCommand extends Commands {

    public DespunirIDCommand() {
        super("did", "despunirid");
    }

    private static final PunishDao punishDao;

    @Override
    public void perform(CommandSender sender, String[] args) {
            }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("vulcanth.punish.did")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente Coordenador ou superior podem executar este comando."));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize /did <id>."));
            return;
        }
        String id = args[0];
        Punish punish = punishDao.getPunishService().get(id);

        if (punish == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cNão existe punição com este id."));
            return;
        }
        String playerName = punish.getPlayerName();
        punishDao.disablePunish(id);
        TextComponent text = new TextComponent(TextComponent.fromLegacyText("§cO jogador §f" + playerName + " §cacabou de ter sua punição revogada.", ChatColor.RED));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§e" + playerName +
                "\n\n§fID: §e#" + punish.getId() +
                "\n§fMotivo: §7" + punish.getReason().getText() +
                "\n§fTipo de punição: §7" + punish.getReason().getPunishType().name().replace("TEMP", ""))));

        sender.sendMessage(TextComponent.fromLegacyText("§aVocê despuniu o jogador " + playerName + "§a."));
        ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("vulcanth.punish.punir")).forEach(o -> o.sendMessage(text));

    }
    static {
        punishDao = Main.getInstance().getPunishDao();
    }
}
