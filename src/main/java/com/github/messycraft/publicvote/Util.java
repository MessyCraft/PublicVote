package com.github.messycraft.publicvote;

import com.github.messycraft.publicvote.entity.Vote;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Util {

    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Util() {}

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static TextComponent text(String s) {
        return new TextComponent(color(s));
    }

    public static void send(CommandSender sender, String msg) {
        send(sender, text(msg));
    }

    public static void send(CommandSender sender, TextComponent... msg) {
        sender.spigot().sendMessage(text("&3&l『PublicVote』 &r"), new TextComponent(msg));
    }

    public static TextComponent interactiveText(String hover, String clickCommand, String text) {
        TextComponent component = text(text);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{text(hover)}));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
        return component;
    }

    public static void broadcast(TextComponent... msg) {
        Bukkit.spigot().broadcast(text("&8&l>> &r"), new TextComponent(msg));
    }

    public static String now() {
        return LocalDateTime.now().format(FMT);
    }

    public static String formattedTime() {
        return now().replace(" ", "_").replace(":", "");
    }

    public static void saveVote(Vote vote) {
        ConfigurationSection section = PublicVote.getInstance().getConfig().createSection(vote.getUniqueId().toString());
        section.set("title", vote.getTitle());
        section.set("creator", vote.getCreator().toString());
        section.set("creatorName", vote.getCreatorName());
        section.set("createTime", vote.getCreateTime());
        section.set("isPublic", vote.isPublic());
        section.set("voteTime", vote.getVoteTime());
        section.set("realVoteTime", vote.getRealVoteTime());
        section.set("finishTime", vote.getFinishTime());
        section.set("isEnded", vote.isEnded());
        PublicVote.getInstance().getConfig().createSection(vote.getUniqueId().toString() + ".voters");
        for (String key : vote.getVoters().keySet()) {
            section.set("voters." + key, vote.getVoters().get(key));
        }
        PublicVote.getInstance().saveConfig();
    }

    public static void deleteVote(UUID uniqueId) {
        PublicVote.getVoteList().remove(getVoteByUniqueId(uniqueId));
        PublicVote.getInstance().getConfig().set(uniqueId.toString(), null);
        PublicVote.getInstance().saveConfig();
    }

    public static void initVoteList() {
        for (String uniqueId : PublicVote.getInstance().getConfig().getKeys(false)) {
            ConfigurationSection section = PublicVote.getInstance().getConfig().getConfigurationSection(uniqueId);
            ConfigurationSection votersSection = section.getConfigurationSection("voters");
            Map<String, Boolean> voters = new HashMap<>();
            for (String voterName : votersSection.getKeys(false)) {
                voters.put(voterName, votersSection.getBoolean(voterName));
            }
            Vote vote = new Vote(
                    section.getString("title"),
                    UUID.fromString(section.getString("creator")),
                    section.getString("creatorName"),
                    section.getString("createTime"),
                    section.getBoolean("isPublic"),
                    section.getInt("voteTime"),
                    section.getInt("realVoteTime"),
                    section.getString("finishTime"),
                    section.getBoolean("isEnded"),
                    voters,
                    UUID.fromString(uniqueId)
            );
            PublicVote.getVoteList().add(vote);
        }
    }

    public static Vote getVoteByUniqueId(UUID uniqueId) {
        for (Vote vote : PublicVote.getVoteList()) {
            if (vote.getUniqueId().equals(uniqueId)) {
                return vote;
            }
        }
        return null;
    }

    public static void startVote(Vote vote, String playerName) {
        if (PublicVote.getVotingMap().containsKey(vote.getUniqueId().toString()))
            return;
        PublicVote.getVotingMap().put(vote.getUniqueId().toString(), vote);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                finishVote(vote, false);
            }
        };
        runnable.runTaskLater(PublicVote.getInstance(), vote.getVoteTime() * 60L * 20L);
        PublicVote.getStartTimeMap().put(vote.getUniqueId().toString(), System.currentTimeMillis());
        PublicVote.getRunnableMap().put(vote.getUniqueId().toString(), runnable);
        broadcast(text("&e玩家 &c" + playerName + "&e 发起了一项投票: &b" + vote.getTitle() + " &r"), interactiveText("&b点击加入此次投票", "/pvote " + vote.getUniqueId().toString(), "&6&l[点击参与]"));
    }

    public static void finishVote(Vote vote, boolean silent) {
        PublicVote.getVotingMap().remove(vote.getUniqueId().toString());
        BukkitRunnable runnable = PublicVote.getRunnableMap().get(vote.getUniqueId().toString());
        if (runnable != null && !runnable.isCancelled()) {
            runnable.cancel();
        }
        PublicVote.getRunnableMap().remove(vote.getUniqueId().toString());
        vote.setEnded(true);
        vote.setFinishTime(now());
        vote.setRealVoteTime((int) (System.currentTimeMillis() - PublicVote.getStartTimeMap().get(vote.getUniqueId().toString())) / 1000 + 1);
        PublicVote.getStartTimeMap().remove(vote.getUniqueId().toString());
        saveVote(vote);
        if (!silent) {
            if (vote.isPublic())
                broadcast(text("&e投票 &b" + vote.getTitle() + "&e 已结束. "), interactiveText("&b/votes", "/votes", "&6&l[查看列表]"));
            else
                broadcast(text("&e投票 &b" + vote.getTitle() + "&e 已结束."));
        }
    }
}
