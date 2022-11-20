package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.Commands;
import dev.vulcanth.nyel.gerementions.enums.punish.PunishType;
import dev.vulcanth.nyel.gerementions.enums.reason.Reason;
import dev.vulcanth.nyel.gerementions.punish.Punish;
import dev.vulcanth.nyel.gerementions.util.Util;
import dev.vulcanth.nyel.gerementions.util.Webhook;
import dev.vulcanth.nyel.player.role.Role;
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

    private String webhookURL = "https://discord.com/api/webhooks/1005663639967121518/xlXHMJauZYeJNrac5XbVHiUt4S2mijPi4e_VdOmDLJ2_bXBjeU0aomZoq97YPF_XlA-B";
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static PunishDao punishDao;

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("vulcanth.cmd.punir")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente Ajudante ou superior podem executar este comando."));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUso incorreto, use /punir <player> e selecione o motivo."));
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

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
            if (target == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cEste usuário não se encontra online"));
                return;
            }
            if (target.hasPermission("vulcanth.cmd.punir")) {
                sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode punir um membro da equipe."));
                return;
            }
            sender.sendMessage(TextComponent.fromLegacyText("§aSelecione o motivo por qual você deseja punir " + Role.getColored(targetName) + "§f:"));
            sender.sendMessage(TextComponent.fromLegacyText(" "));

            boolean a = true;

            for (Reason value : Reason.values()) {
                String punishType = value.getPunishType().name().replace("TEMP", "");

                if (sender.hasPermission("vulcanth.punir." + punishType.toLowerCase())) {
                    TextComponent text = new TextComponent((a ? "§f" : "§7") + value.getText());
                    String rank;

                    switch (value.getPunishType()) {
                        case BAN:
                        case TEMPBAN:
                            rank = "§2Moderador";
                            break;
                        default:
                            rank = "§eAjudante";
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
            if (sender.hasPermission("role.admin")) {
                if (punishDao.getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equalsIgnoreCase(targetName)).filter(punish -> punish.getReasona() == reason).noneMatch(Punish::isLocked)) {
                    apply(punishDao.createPunish(targetName, sender.getName(), reason, null, reason.getPunishType().name()), ProxyServer.getInstance().getPlayer(targetName), sender.getName());
                    
                    Webhook webhook = new Webhook(webhookURL);
                    webhook.addEmbed(
                            new Webhook.EmbedObject()
                                    .setDescription("Um usuário foi punido do servidor.")
                                    .setThumbnail("https://mc-heads.net/avatar/" + targetName + "/500")
                                    .setColor(Color.decode("#FFAA00"))
                                    .addField("Usuário:", targetName, true)
                                    .addField("Motivo:", reason.getText(), true)
                                    .addField("Duração:", reason.getTime() == 0 ? "Permanente" : Util.fromLongWithoutDiff(System.currentTimeMillis() + reason.getTime()), false)
                                    .addField("Expira em:", reason.getTime() == 0 ? "Nunca" : SDF.format(System.currentTimeMillis() + reason.getTime()), true)
                                    .addField("Provas:", "Nenhuma", true)
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

            if (sender.hasPermission("vulcanth.punir." + reason.getPunishType().name().replace("TEMP", "").toLowerCase())) {
                if (punishDao.getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equalsIgnoreCase(targetName)).filter(punish -> punish.getReasona() == reason).noneMatch(Punish::isLocked)) {
                    apply(punishDao.createPunish(targetName, sender.getName(), reason, proof, reason.getPunishType().getText()), ProxyServer.getInstance().getPlayer(targetName), sender.getName());
                    Webhook webhook = new Webhook(webhookURL);
                    webhook.addEmbed(
                            new Webhook.EmbedObject()
                                    .setDescription("Um usuário foi punido do servidor.")
                                    .setThumbnail("https://mc-heads.net/avatar/" + targetName + "/500")
                                    .setColor(Color.decode("#FFAA00"))
                                    .addField("Usuário:", targetName, false)
                                    .addField("Motivo:", reason.getText(), false)
                                    .addField("Duração:", reason.getTime() == 0 ? "Permanente" : Util.fromLongWithoutDiff(System.currentTimeMillis() + reason.getTime()), false)
                                    .addField("Expira em:", reason.getTime() == 0 ? "Nunca" : SDF.format(System.currentTimeMillis() + reason.getTime()), true)
                                    .addField("Provas:", proof, false)
                                    .addField("Staff:", sender.getName(), true)
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
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido por " + staffer+
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: Permanente\n";
                break;
            case MUTE:
                textString = "§c* " + punish.getPlayerName() + " §cfoi silenciado por §c" + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: Permanente\n";
                break;
            case TEMPBAN:
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido por §c" + staffer +
                        "\n§c* Motivo:" + reason.getText() + " - " + proof +
                        "\n§c* Duração: " + Util.fromLong(punish.getExpire());
                break;
            case TEMPMUTE:
                textString = "\n§c* " + punish.getPlayerName() + " §cfoi silenciado por §c" + staffer +
                        "\n§c* Motivo: " + reason.getText() + " - " + proof +
                        "\n§c* Duração: " + Util.fromLong(punish.getExpire());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + reason.getPunishType());
        }
        final TextComponent text = new TextComponent(textString);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, proof));

        /*if (ReportsUtils.getReports().stream().anyMatch(report -> report.getReported().equalsIgnoreCase(punish.getPlayerName()))) {
            ReportsUtils.getReports().stream().filter(report -> report.getReported().equalsIgnoreCase(punish.getPlayerName())).map(ReportsUtils::getReported).forEach(s -> {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(s);

                if (proxiedPlayer != null) {
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(" "));
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText("§a* Olá, o jogador §f" + punish.getPlayerName()
                            + " §aacaba de ser punido em nossa rede, §aobrigado por reportar esse infrator, §aagradecemos sua colaboração."));
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(" "));
                }
            });
        }*/
        ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("vulcanth.cmd.punir")).forEach(o -> {
            o.sendMessage(TextComponent.fromLegacyText(" "));
            o.sendMessage(text);
            o.sendMessage(TextComponent.fromLegacyText(" "));
        });
        if (target != null) {
            target.sendMessage(TextComponent.fromLegacyText(" "));
            target.sendMessage(text);
            target.sendMessage(TextComponent.fromLegacyText(" "));

            if (reason.getPunishType() == PunishType.TEMPBAN) {
                target.disconnect(TextComponent.fromLegacyText("§c§lVULCANTH\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: " + Util.fromLong(punish.getExpire()) +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em nosso forum: §evulcanth.com/forum"));
                return;
            }
            if (reason.getPunishType() == PunishType.BAN) {
                target.disconnect(TextComponent.fromLegacyText("§c§lVULCANTH\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: Permanente" +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em nosso forum: §evulcanth.com/forum"));
            }
        }
    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }

    private static boolean impossibleToBan(String nickName) {
        return Stream.of("NyellPlay", "_ImZaskie_").anyMatch(s -> s.equalsIgnoreCase(nickName));
    }
}


