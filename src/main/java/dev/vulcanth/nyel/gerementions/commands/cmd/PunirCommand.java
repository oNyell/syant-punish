package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import dev.vulcanth.nyel.gerementions.enums.reason.Reason;
import dev.vulcanth.nyel.gerementions.punish.Punish;
import dev.vulcanth.nyel.gerementions.util.Util;
import dev.vulcanth.nyel.gerementions.util.Webhook;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import dev.vulcanth.nyel.gerementions.punish.dao.PunishDao;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class PunirCommand extends Commands {

    public PunirCommand() {
        super("punir", "punish");
    }

    private String webhookURL = "https://discord.com/api/webhooks/1057495063304871976/QENHseh2b6JAPgCIciSI2w0w09TrkAPZojChPxI3Rx7LYdhBjkgQy5ul4X2sP_IKFzh2";
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static PunishDao punishDao;

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("syant.cmd.punir")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente Helper ou superior podem executar este comando."));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUso incorreto, use /punir <player> e selecione o motivo."));
            return;
        }

        if (args.length == 1) {
            String targetName = args[0];

            if (targetName.equals(sender.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se punir."));
                return;
            }
            if (impossibleToBan(targetName)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir este jogador."));
                return;
            }
            sender.sendMessage(TextComponent.fromLegacyText("§aSelecione o motivo por qual você deseja punir " + targetName + "§f:"));
            sender.sendMessage(TextComponent.fromLegacyText(" "));

            boolean a = true;

            for (Reason value : Reason.values()) {
                String punishType = value.getPunishType().name().replace("TEMP", "");

                if (sender.hasPermission("syant.punir." + punishType.toLowerCase())) {
                    TextComponent text = new TextComponent((a ? "§f" : "§7") + value.getText());
                    String rank;

                    switch (value.getPunishType()) {
                        case BAN:
                        case KICK:
                            rank = "§5Coordenador";
                            break;
                        case TEMPBAN:
                            rank = "§2Moderador";
                            break;
                        default:
                            rank = "§eHelper";
                            break;
                    }
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§e" + value.getText() +
                            "\n\n§7Tipo de punição: §f" + punishType +
                            "\n§7Tempo de punição: §f" + (value.getTime() > 0 ? Util.fromLongWithoutDiff(value.getTime()) : "Permanente") +
                            "\n§7Cargo mínimo: §f" + rank)));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/punir " + targetName + " " + value.name() + " <prova>"));
                    sender.sendMessage(text);
                    a = !a;
                }
            }
            sender.sendMessage(TextComponent.fromLegacyText(" "));
            return;
        }
        if (args.length == 2) {
            String targetName = args[0];
            Reason reason;
            if (impossibleToBan(targetName)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir este jogador."));
                return;
            }
            if (targetName.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se punir."));
                return;
            }
            try {
                reason = Reason.valueOf(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVerifique se você deixou um espaço em branco extra no motivo."));
                return;
            }
            if (sender.hasPermission("role.coord")) {
                if (punishDao.getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equalsIgnoreCase(targetName)).filter(punish -> punish.getReasona() == reason).noneMatch(Punish::isLocked)) {
                    apply(punishDao.createPunish(targetName, sender.getName(), reason, null, reason.getPunishType().name()), ProxyServer.getInstance().getPlayer(targetName), sender.getName());
                    
                    Webhook webhook = new Webhook(webhookURL);
                    webhook.addEmbed(
                            new Webhook.EmbedObject()
                                    .setDescription("Um usuário foi punido do servidor.")
                                    .setThumbnail("https://mc-heads.net/avatar/" + targetName + "/500")
                                    .setColor(Color.decode("#00A8FF"))
                                    .addField("Usuário:", targetName, true)
                                    .addField("Motivo:", reason.getText(), true)
                                    .addField("Duração:", reason.getTime() == 0 ? "Permanente" : Util.fromLongWithoutDiff(reason.getTime()), true)
                                    .addField("Tipo:", reason.getPunishType().getText(), true)
                                    .addField("Expira em:", reason.getTime() == 0 ? "Nunca" : SDF.format(System.currentTimeMillis() + reason.getTime()), true)
                                    .addField("Provas:", "Nenhuma", true)
                                    .addField("Staff:", sender.getName(), false)
                    );

                    try {
                        webhook.execute();
                    } catch (IOException e) {
                        Main.getInstance().getLogger().severe(e.getStackTrace().toString());
                    }
                    sender.sendMessage(TextComponent.fromLegacyText("§ePunição aplicada com sucesso."));
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cEste jogador já está punido por este motivo."));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para punir sem prova."));
            }
        }
        if (args.length == 3) {
            String targetName = args[0];
            String proof = args[2];
            Reason reason;

            if (impossibleToBan(targetName)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir este jogador."));
                return;
            }
            if (targetName.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se punir."));
                return;
            }
            try {
                reason = Reason.valueOf(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVerifique se você deixou um espaço em branco extra no motivo."));
                return;
            }
            if (reason != Reason.AC) {
                if (!proof.startsWith("https://")) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cToda prova deve começar com §fhttps://§c."));
                    return;
                }
            }

            if (sender.hasPermission("syant.punir." + reason.getPunishType().name().replace("TEMP", "").toLowerCase())) {
                if (punishDao.getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equalsIgnoreCase(targetName)).filter(punish -> punish.getReasona() == reason).noneMatch(Punish::isLocked)) {
                    apply(punishDao.createPunish(targetName, sender.getName(), reason, proof, reason.getPunishType().getText()), ProxyServer.getInstance().getPlayer(targetName), sender.getName());
                    Webhook webhook = new Webhook(webhookURL);
                    webhook.addEmbed(
                            new Webhook.EmbedObject()
                                    .setDescription("Um usuário foi punido do servidor.")
                                    .setThumbnail("https://mc-heads.net/avatar/" + targetName + "/500")
                                    .setColor(Color.decode("#00A8FF"))
                                    .addField("Usuário:", targetName, true)
                                    .addField("Motivo:", reason.getText(), true)
                                    .addField("Duração:", reason.getTime() == 0 ? "Permanente" : Util.fromLongWithoutDiff(reason.getTime()), false)
                                    .addField("Tipo:", reason.getPunishType().getText(), true)
                                    .addField("Expira em:", reason.getTime() == 0 ? "Nunca" : SDF.format(System.currentTimeMillis() + reason.getTime()), true)
                                    .addField("Provas:", proof, true)
                                    .addField("Staff:", sender.getName(), false)
                    );
                    try {
                        webhook.execute();
                    } catch (IOException e) {
                        Main.getInstance().getLogger().severe(e.getStackTrace().toString());
                    }
                    sender.sendMessage(TextComponent.fromLegacyText("§ePunição aplicada com sucesso."));
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cEste jogador já está punido por este motivo."));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para executar esta punição."));
            }
        }
    }

    private static void apply(Punish punish, ProxiedPlayer target, String staffer) {
        final String textString;
        final Reason reason = punish.getReasona();
        final String proof = (punish.getProof() == null ? "Nenhuma" : punish.getProof());

        switch (reason.getPunishType()) {
            case BAN:
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido por " + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: Permanente\n";
                break;
            case MUTE:
                textString = "§c* " + punish.getPlayerName() + " §cfoi silenciado por " + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: Permanente\n";
                break;
            case TEMPBAN:
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido por " + staffer +
                        "\n§c* Motivo:" + reason.getText() + " - " + proof +
                        "\n§c* Duração: " + Util.fromLong(punish.getExpire());
                break;
            case TEMPMUTE:
                textString = "\n§c* " + punish.getPlayerName() + " §cfoi silenciado por " + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: " + Util.fromLong(punish.getExpire());
                break;
            case KICK:
                textString = "\n§c* " + punish.getPlayerName() + " §cfoi expulso por §c" + staffer + "\n";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + reason.getPunishType());
        }
        final TextComponent text = new TextComponent(textString);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, proof));

        /*if (ReportManagerBukkit.getReports().stream().anyMatch(report -> report.getTarget().equalsIgnoreCase(punish.getPlayerName()))) {
            ReportManagerBukkit.getReports().stream().filter(report -> report.getTarget().equalsIgnoreCase(punish.getPlayerName())).map(ReportManagerBukkit::getAccuser).forEach(s -> {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(s);

                if (proxiedPlayer != null) {
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(" "));
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText("§a* Olá, o jogador §f" + punish.getPlayerName()
                            + " §aacaba de ser punido em nossa rede, §aobrigado por reportar esse infrator, §aagradecemos sua colaboração."));
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(" "));
                }
            });
        }*/
        ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("syant.cmd.punir")).forEach(o -> {
            o.sendMessage(TextComponent.fromLegacyText(" "));
            o.sendMessage(text);
            o.sendMessage(TextComponent.fromLegacyText(" "));
        });
        if (target != null) {
            target.sendMessage(TextComponent.fromLegacyText(" "));
            target.sendMessage(text);
            target.sendMessage(TextComponent.fromLegacyText(" "));

            if (reason.getPunishType() == PunishType.TEMPBAN) {
                target.disconnect(TextComponent.fromLegacyText("§c§lSYANT\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: " + Util.fromLong(punish.getExpire()) +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em nosso forum: §esyantmc.com/forum"));
                return;
            }
            if (reason.getPunishType() == PunishType.BAN) {
                target.disconnect(TextComponent.fromLegacyText("§c§lSYANT\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: Permanente" +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em: §esyantmc.com/forum"));
            }
        }
    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }

    private static boolean impossibleToBan(String nickName) {
        return Stream.of("oNyell").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }
}


