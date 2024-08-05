package com.github.messycraft.publicvote.command;

import com.github.messycraft.publicvote.Util;
import com.github.messycraft.publicvote.gui.GUIProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Util.send(sender, "&c这个命令不能被控制台执行!");
            return true;
        }
        if (args.length != 0) {
            Util.send(sender, "&c正确用法: /" + label);
            return true;
        }
        Player p = (Player) sender;
        GUIProvider.openVoteListGUI(p);
        return true;
    }

}
