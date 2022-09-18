package com.ayane.nyel.commands.cmd;

import com.ayane.nyel.commands.Commands;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class BanCommand extends Commands {
    public BanCommand() {
        super("ban", "vban");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("role.admin")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cSomente ADMIN ou superior podem executar este comando."));
        }
        if (args.length == 0){
            sender.sendMessage(TextComponent.fromLegacyText("§cNecessário nickname e prova para concluir o banimento."));
        }
    }
}
