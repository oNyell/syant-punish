package dev.vulcanth.nyel.gerementions.commands;

import dev.vulcanth.nyel.gerementions.Main;
import dev.vulcanth.nyel.gerementions.commands.cmd.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public abstract class Commands extends Command {

    public Commands(String name, String... aliases) {
        super(name, null, aliases);
        ProxyServer.getInstance().getPluginManager().registerCommand(Main.getInstance(), this);
    }

    public abstract void perform(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender sender, String[] args) {
        perform(sender, args);
    }
    public static void setupCommands() {
        new DespunirCommand();
        new DespunirIDCommand();
        new KickCommand();
        new PunirCommand();
        new BanCommand();
        new YTCommand();
    }
}
