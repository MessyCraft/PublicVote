package com.github.messycraft.publicvote.gui;

import com.github.messycraft.publicvote.PublicVote;
import com.github.messycraft.publicvote.Util;
import com.github.messycraft.publicvote.entity.Vote;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public final class GUIProvider {

    public static void openCreateVoteGUI(Player p, String topic) {
        Inventory inv = Bukkit.createInventory(new CreateVoteInventoryHolder(), 45, Util.color("&3&l创建投票 &8") + topic);
        ItemStack border = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        setItemNameAndLore(border, "&c取消创建", "&7- 点击关闭此界面");
        ItemStack finish = new ItemStack(Material.NETHER_STAR);
        setItemNameAndLore(finish, "&a确认创建", "&7- 点击完成创建", "&7- 创建完成后可使用&n/votes&7选择并发起一项投票", "&7- 在开始投票前投票内容仅对你可见");
        ItemStack choosePublic = new ItemStack(Material.SLIME_BALL);
        setItemNameAndLore(choosePublic, "&b是否公开结果", "&7- 当前状态: &a是", "&7- 若设置为非公开则只有投票发起者和管理员可以查看");
        ItemStack setVoteTime = new ItemStack(Material.CLOCK);
        setItemNameAndLore(setVoteTime, "&b设置投票时间", "&7- 单位: 分钟(min)", "&7- &e左键&7 增加 1 &8| &eShift+左键&7 增加 10", "&7- &e右键&7 减少 1 &8| &eShift+右键&7 减少 10", "&7- 也可以在&n/votes&7中提前结束投票");
        setInv(inv, border, 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 43, 42, 41, 39, 38, 37, 36, 27, 18, 9);
        setInv(inv, finish, 40);
        setInv(inv, choosePublic, 20);
        setInv(inv, setVoteTime, 24);
        p.openInventory(inv);
    }

    public static void openVoteListGUI(Player p) {
        Inventory inv = Bukkit.createInventory(new VoteListInventoryHolder(), 54, Util.color("&l近期投票列表"));
        List<Vote> list = new ArrayList<>(PublicVote.getVoteList());
        Collections.reverse(list);
        list = list.stream().filter(vote ->
                vote.getCreator().equals(p.getUniqueId())
                || PublicVote.getVotingMap().containsKey(vote.getUniqueId().toString())
                || (vote.isEnded() && (vote.isPublic() || p.hasPermission("publicvote.see-private")))
        ).collect(Collectors.toList());
        for (int i = 0; i < list.size() && i < 54; i ++) {
            Vote vote = list.get(i);
            List<String> lore = new ArrayList<>();
            lore.add("&8" + vote.getUniqueId().toString());
            List<String> common = new ArrayList<>();
            common.add("&6- 创建者: &e" + vote.getCreatorName());
            common.add("&6- 创建时间: &e" + vote.getCreateTime());
            common.add("&6- 是否公开结果: " + (vote.isPublic() ? "&a是" : "&c否"));
            common.add("&6- 预设定投票时长: &e" + vote.getVoteTime() + "min");
            ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS);
            if (PublicVote.getVotingMap().containsKey(vote.getUniqueId().toString())) {
                item.setType(Material.ORANGE_STAINED_GLASS);
                lore.add("");
                lore.add(vote.getVoters().containsKey(p.getName()) ? "&a   您已参与本次投票" : "&a   点击参与投票!");
                lore.add("");
                lore.addAll(common);
                lore.add("&6- 已参与的投票者列表:");
                for (String key : vote.getVoters().keySet()) {
                    lore.add("&f&o  " + key);
                }
                if (vote.getVoters().isEmpty()) {
                    lore.add("&7  [暂时无人投票]");
                }
                lore.add("");
                if (vote.getCreator().equals(p.getUniqueId())) {
                    lore.add("&cShift+左键: 中止并结算此投票");
                }
            }
            else if (!vote.isEnded()) {
                item.setType(Material.LIGHT_GRAY_STAINED_GLASS);
                lore.add("");
                lore.add("&a   点击发起此投票!");
                lore.add("");
                lore.addAll(common);
                lore.add("");
            }
            else {
                lore.addAll(common);
                List<String> agree = new ArrayList<>();
                List<String> disagree = new ArrayList<>();
                for (String key : vote.getVoters().keySet()) {
                    if (vote.getVoters().get(key))
                        agree.add(key);
                    else
                        disagree.add(key);
                }
                lore.add("&6- 实际投票时长: &e" + (vote.getRealVoteTime() / 60) + "min " + (vote.getRealVoteTime() % 60) + "s");
                lore.add("&6- 投票结束时间: &e" + vote.getFinishTime());
                lore.add("&6- &l投票结果: " + (agree.size() == disagree.size() ? "&9&l票数相等" : (agree.size() > disagree.size() ? "&a&l支持者&e&l较多" : "&c&l反对者&e&l较多")));
                lore.add("&7  支持此投票的玩家列表(共" + agree.size() + "票):");
                for (String s : agree) {
                    lore.add("&7&o    " + s);
                }
                if (agree.isEmpty()) {
                    lore.add("&7    [空]");
                }
                lore.add("&7  反对此投票的玩家列表(共" + disagree.size() + "票):");
                for (String s : disagree) {
                    lore.add("&7&o    " + s);
                }
                if (disagree.isEmpty()) {
                    lore.add("&7    [空]");
                }
                lore.add("");
            }
            if (p.hasPermission("publicvote.delete")) {
                lore.add("&cShift+右键: &m删除&c此投票");
            }
            setItemNameAndLore(item, "&b" + vote.getTitle(), lore.toArray(new String[0]));
            inv.setItem(i, item);
        }
        p.openInventory(inv);
    }

    public static void openVoteGUI(Player p, Vote vote) {
        Inventory inv = Bukkit.createInventory(new VoteInventoryHolder(), 27, Util.color("&4&l投票&5 " + vote.getTitle()));
        ItemStack agree = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        setItemNameAndLore(agree, "&a支持此项提议", "&7- 点击投赞成票");
        ItemStack disagree = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        setItemNameAndLore(disagree, "&c反对此项提议", "&7- 点击投反对票");
        ItemStack info = new ItemStack(Material.CONDUIT);
        List<String> lore = new ArrayList<>();
        lore.add("&8" + vote.getUniqueId());
        lore.add("&6- 创建者: &e" + vote.getCreatorName());
        lore.add("&6- 创建时间: &e" + vote.getCreateTime());
        lore.add("&6- 是否公开结果: " + (vote.isPublic() ? "&a是" : "&c否"));
        lore.add("&6- 预计投票时长: &e" + vote.getVoteTime() + "min");
        lore.add("&6- 已参与的投票者列表:");
        for (String key : vote.getVoters().keySet())
            lore.add("&f&o  " + key);
        if (vote.getVoters().isEmpty())
            lore.add("&7  [暂时无人投票]");
        setItemNameAndLore(info, "&3投票详情", lore.toArray(new String[0]));
        setInv(inv, agree, 0, 1, 2, 3, 4, 9, 10, 11, 12, 18, 19, 20, 21);
        setInv(inv, disagree, 5, 6, 7, 8, 14, 15, 16, 17, 22, 23, 24, 25, 26);
        setInv(inv, info, 13);
        p.openInventory(inv);
    }

    public static class CreateVoteInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class VoteListInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class VoteInventoryHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private static void setInv(Inventory inv, ItemStack item, int... index) {
        for (int i : index) {
            inv.setItem(i, item);
        }
    }

    private static void setItemNameAndLore(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Util.color(name));
        meta.setLore(Arrays.stream(lore).map(Util::color).collect(Collectors.toList()));
        item.setItemMeta(meta);
    }

}
