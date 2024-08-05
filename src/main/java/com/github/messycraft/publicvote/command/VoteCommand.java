package com.github.messycraft.publicvote.command;

import com.github.messycraft.publicvote.PublicVote;
import com.github.messycraft.publicvote.Util;
import com.github.messycraft.publicvote.entity.Vote;
import com.github.messycraft.publicvote.gui.GUIProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Util.send(sender, "&c这个命令不能被控制台执行!");
            return true;
        }
        if (args.length != 1 || !PublicVote.getVotingMap().containsKey(args[0])) {
            Util.send(sender, "&cEmm..或许这个投票已经结束了?");
            return true;
        }
        Player p = (Player) sender;
        Vote vote = PublicVote.getVotingMap().get(args[0]);
        GUIProvider.openVoteGUI(p, vote);
        return true;
    }

}
