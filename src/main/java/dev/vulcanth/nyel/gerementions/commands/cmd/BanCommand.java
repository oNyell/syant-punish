package dev.vulcanth.nyel.gerementions.commands.cmd;

import dev.vulcanth.nyel.gerementions.commands.Commands;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class BanCommand extends Commands {
    public BanCommand() {
        super("ban", "vban");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("role.admin")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cSomente Gerente ou superior podem executar este comando."));
        }
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cNecessário nickname e prova para concluir o banimento."));
        }
        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê digitou somente o nickname do acusado, favor completar com o motivo"));
        }
        String reason = args[1];
        if (reason == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§ePunição aplicada com sucesso.\n"));
        }
    }
}
