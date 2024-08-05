package com.github.messycraft.publicvote.gui;

import com.github.messycraft.publicvote.PublicVote;
import com.github.messycraft.publicvote.Util;
import com.github.messycraft.publicvote.entity.Vote;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIListener implements Listener {

    @EventHandler
    public void onCreateVote(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof GUIProvider.CreateVoteInventoryHolder)
            e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof GUIProvider.CreateVoteInventoryHolder) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (Material.CYAN_STAINED_GLASS_PANE.equals(e.getCurrentItem().getType())) {
                e.getWhoClicked().closeInventory();
                return;
            }
            if (Material.NETHER_STAR.equals(e.getCurrentItem().getType())) {
                Vote vote = new Vote(
                        e.getView().getTitle().substring(11),
                        e.getWhoClicked().getUniqueId(),
                        e.getWhoClicked().getName(),
                        Util.now(),
                        Material.SLIME_BALL.equals(e.getClickedInventory().getItem(20).getType()),
                        e.getClickedInventory().getItem(24).getAmount()
                );
                e.getWhoClicked().closeInventory();
                PublicVote.getVoteList().add(vote);
                Util.saveVote(vote);
                Util.send(e.getWhoClicked(), Util.text("&a投票已创建成功! 使用&n/votes&a查看近期投票列表. "), Util.interactiveText("&b点击打开", "/votes", "&6&l[点击打开]"));
            }
            else if (Material.SLIME_BALL.equals(e.getCurrentItem().getType())) {
                e.getCurrentItem().setType(Material.MAGMA_CREAM);
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                lore.set(0, Util.color("&7- 当前状态: &c否"));
                meta.setLore(lore);
                e.getCurrentItem().setItemMeta(meta);
            }
            else if (Material.MAGMA_CREAM.equals(e.getCurrentItem().getType())) {
                e.getCurrentItem().setType(Material.SLIME_BALL);
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                lore.set(0, Util.color("&7- 当前状态: &a是"));
                meta.setLore(lore);
                e.getCurrentItem().setItemMeta(meta);
            }
            else if (Material.CLOCK.equals(e.getCurrentItem().getType())) {
                int amount = e.getCurrentItem().getAmount();
                switch (e.getClick()) {
                    case LEFT:
                        amount ++; break;
                    case SHIFT_LEFT:
                        amount += 10; break;
                    case RIGHT:
                        amount --; break;
                    case SHIFT_RIGHT:
                        amount -= 10;
                }
                if (amount < 1)
                    amount = 1;
                if (amount > 64)
                    amount = 64;
                e.getCurrentItem().setAmount(amount);
            }
        }
    }

    @EventHandler
    public void onVoteList(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof GUIProvider.VoteListInventoryHolder)
            e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof GUIProvider.VoteListInventoryHolder) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            ItemStack item = e.getCurrentItem();
            UUID uniqueId = UUID.fromString(item.getItemMeta().getLore().get(0).substring(2));
            Vote vote = Util.getVoteByUniqueId(uniqueId);
            if (vote == null) {
                GUIProvider.openVoteListGUI((Player) e.getWhoClicked());
                return;
            }
            if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (e.getWhoClicked().hasPermission("publicvote.delete")) {
                    if (PublicVote.getVotingMap().containsKey(uniqueId.toString()))
                        Util.finishVote(vote, true);
                    Util.deleteVote(uniqueId);
                    GUIProvider.openVoteListGUI((Player) e.getWhoClicked());
                }
                return;
            }
            if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                if (PublicVote.getVotingMap().containsKey(uniqueId.toString()) && vote.getCreator().equals(e.getWhoClicked().getUniqueId())) {
                    e.getWhoClicked().closeInventory();
                    Util.finishVote(vote, false);
                }
                return;
            }
            if (e.getClick().equals(ClickType.LEFT)) {
                if (Material.LIGHT_GRAY_STAINED_GLASS.equals(item.getType())) {
                    e.getWhoClicked().closeInventory();
                    Util.startVote(vote, e.getWhoClicked().getName());
                }
                else if (Material.ORANGE_STAINED_GLASS.equals(item.getType())) {
                    if (!vote.getVoters().containsKey(e.getWhoClicked().getName())) {
                        e.getWhoClicked().closeInventory();
                        Bukkit.dispatchCommand(e.getWhoClicked(), "pvote " + uniqueId);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVote(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof GUIProvider.VoteInventoryHolder)
            e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof GUIProvider.VoteInventoryHolder) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            UUID uniqueId = UUID.fromString(e.getClickedInventory().getItem(13).getItemMeta().getLore().get(0).substring(2));
            Vote vote = Util.getVoteByUniqueId(uniqueId);
            if (vote == null || vote.getVoters().containsKey(e.getWhoClicked().getName())) {
                GUIProvider.openVoteListGUI((Player) e.getWhoClicked());
                return;
            }
            if (Material.GREEN_STAINED_GLASS_PANE.equals(e.getCurrentItem().getType())) {
                e.getWhoClicked().closeInventory();
                vote.getVoters().put(e.getWhoClicked().getName(), true);
                Util.send(e.getWhoClicked(), "&6您已选择: 支持票. 感谢您的参与.");
            }
            else if (Material.RED_STAINED_GLASS_PANE.equals(e.getCurrentItem().getType())) {
                e.getWhoClicked().closeInventory();
                vote.getVoters().put(e.getWhoClicked().getName(), false);
                Util.send(e.getWhoClicked(), "&6您已选择: 反对票. 感谢您的参与.");
            }
        }
    }

}
