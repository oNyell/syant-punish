package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.syantmc.pewd.utils.StringUtils;
import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import dev.vulcanth.nyel.gerementions.punish.Punish;
import dev.vulcanth.nyel.gerementions.punish.dao.PunishDao;
import dev.vulcanth.nyel.gerementions.util.Util;
import dev.vulcanth.nyel.gerementions.util.Webhook;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class KickCommand extends Commands {

    public KickCommand() {
        super("kick", "expulsar");
    }

    private static PunishDao punishDao;
    private static Punish punish;
    private String webhookURL = "https://discord.com/api/webhooks/1057495063304871976/QENHseh2b6JAPgCIciSI2w0w09TrkAPZojChPxI3Rx7LYdhBjkgQy5ul4X2sP_IKFzh2";
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public void perform(CommandSender sender, String[] args) {

    }


    private static boolean impossibleToBan(String nickName) {
        return Stream.of("oNyell").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //ProxiedPlayer player = (ProxiedPlayer) (sender);
        if (!sender.hasPermission("syant.cmd.kick")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cSomente Moderador ou superior podem executar este comando."));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize \"/kick <player>\"."));
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
        if (sender.hasPermission("role.coord") || !sender.hasPermission("syant.cmd.kick.moderador")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê expulsou o jogador " + target.getName() + "§c."));
            target.disconnect(TextComponent.fromLegacyText("§c§lSYANT\n\n§cVocê foi desconectado do servidor por " + sender + "."));
            String textString = "\n§c* " + target.getName() + " §cfoi expulso por §c" + sender + "\n";
            TextComponent text = new TextComponent(textString);
            Webhook webhook = new Webhook(webhookURL);
            webhook.addEmbed(
                    new Webhook.EmbedObject()
                            .setDescription("Um usuário foi expulso do servidor.")
                            .setThumbnail("https://mc-heads.net/avatar/" + target + "/500")
                            .setColor(Color.decode("#00A8FF"))
                            .addField("Usuário:", target.getName(), true)
                            .addField("Aplicador:", sender.getName(), false)
            );
            try {
                webhook.execute();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe(e.getStackTrace().toString());
            }
            ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("role.coord")).forEach(o -> {
                o.sendMessage(TextComponent.fromLegacyText(" "));
                o.sendMessage(text);
                o.sendMessage(TextComponent.fromLegacyText(" "));
            });
            if (target != null) {
                target.sendMessage(TextComponent.fromLegacyText(" "));
                target.sendMessage(text);
                target.sendMessage(TextComponent.fromLegacyText(" "));

            }
        } else if (sender.hasPermission("syant.cmd.kick.moderador")) {
            sender.sendMessage(TextComponent.fromLegacyText("§eExpulsão aplicada com sucesso."));
            target.disconnect(TextComponent.fromLegacyText("§c§lSYANT\n\n§cVocê foi expulso da rede\n" +
                    "\n§cAutor: " + sender +
                    "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em nosso forum: §esyantmc.com/forum"));
            String textString = "\n§c* " + target.getName() + " §cfoi expulso por §c" + sender + "\n";
            TextComponent text = new TextComponent(textString);

            ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("role.coord")).forEach(o -> {
                o.sendMessage(TextComponent.fromLegacyText(" "));
                o.sendMessage(text);
                o.sendMessage(TextComponent.fromLegacyText(" "));
            });
            Webhook webhook = new Webhook(webhookURL);
            webhook.addEmbed(
                    new Webhook.EmbedObject()
                            .setDescription("Um usuário foi punido do servidor.")
                            .setThumbnail("https://mc-heads.net/avatar/" + target + "/500")
                            .setColor(Color.decode("#00A8FF"))
                            .addField("Usuário:", target.getName(), true)
                            .addField("Staff:", sender.getName(), true)
            );
            try {
                webhook.execute();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe(e.getStackTrace().toString());
            }
            return;
        }
    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }
}


