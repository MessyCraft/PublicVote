package com.github.messycraft.publicvote.command;

import com.github.messycraft.publicvote.Util;
import com.github.messycraft.publicvote.gui.GUIProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Util.send(sender, "&c这个命令不能被控制台执行!");
            return true;
        }
        if (args.length == 0) {
            Util.send(sender, "&c正确用法: /" + label + " <投票标题>");
            return true;
        }
        Player p = (Player) sender;
        String title = "";
        for (String s : args)
            title += s + " ";
        title = Util.color(title.substring(0, title.length() - 1));
        GUIProvider.openCreateVoteGUI(p, title);
        return true;
    }

}
