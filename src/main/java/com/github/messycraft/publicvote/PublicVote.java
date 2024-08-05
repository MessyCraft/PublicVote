package com.github.messycraft.publicvote;

import com.github.messycraft.publicvote.bstats.Metrics;
import com.github.messycraft.publicvote.command.BackupCommand;
import com.github.messycraft.publicvote.command.CreateCommand;
import com.github.messycraft.publicvote.command.ListCommand;
import com.github.messycraft.publicvote.command.VoteCommand;
import com.github.messycraft.publicvote.entity.Vote;
import com.github.messycraft.publicvote.gui.GUIListener;
import lombok.Getter;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class PublicVote extends JavaPlugin {

    @Getter
    private static PublicVote instance;

    @Getter
    private static List<Vote> voteList = new ArrayList<>();

    @Getter
    private static Map<String, Vote> votingMap = new HashMap<>();

    @Getter
    private static Map<String, BukkitRunnable> runnableMap = new HashMap<>();

    @Getter
    private static Map<String, Long> startTimeMap = new HashMap<>();

    private static final TabCompleter EMPTY_TABCOMPLETER = (sender, command, alias, args) -> Collections.emptyList();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        Util.initVoteList();
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getCommand("backupvotes").setExecutor(new BackupCommand());
        getCommand("createvote").setExecutor(new CreateCommand());
        getCommand("votelist").setExecutor(new ListCommand());
        getCommand("pvote").setExecutor(new VoteCommand());
        getCommand("backupvotes").setTabCompleter(EMPTY_TABCOMPLETER);
        getCommand("createvote").setTabCompleter(EMPTY_TABCOMPLETER);
        getCommand("votelist").setTabCompleter(EMPTY_TABCOMPLETER);
        getCommand("pvote").setTabCompleter(EMPTY_TABCOMPLETER);
        getLogger().info("Plugin has been enabled.");
        new Metrics(this, 22841);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getLogger().info("Done.");
    }

}
