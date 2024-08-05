package com.github.messycraft.publicvote.command;

import com.github.messycraft.publicvote.PublicVote;
import com.github.messycraft.publicvote.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BackupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PublicVote.getInstance().saveDefaultConfig();
        String fileName = "backup_" + Util.formattedTime() + ".yml";
        try {
            Files.copy(new File(PublicVote.getInstance().getDataFolder(), "config.yml").toPath(), new File(PublicVote.getInstance().getDataFolder(), fileName).toPath());
            Util.send(sender, "已生成投票记录备份: &b" + fileName);
        } catch (IOException e) {
            Util.send(sender, "&c备份文件&n" + fileName + "&c生成错误! 请检查控制台报错.");
            throw new RuntimeException(e);
        }
        return true;
    }

}
