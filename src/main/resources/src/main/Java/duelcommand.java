package com.yfend.yfendduel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DuelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage(Component.text("Kullanim: /duel <oyuncu> veya /duel create <arena> veya /duel ayarla <arena> <1/2>", NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length > 1) {
            String arenaName = args[1];
            YfendDuel.getInstance().getArenas().put(arenaName, new org.bukkit.Location[2]);
            player.sendMessage(Component.text("Arena '" + arenaName + "' olusturuldu. /duel ayarla " + arenaName + " 1/2 kullanin.", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("ayarla") && args.length > 2) {
            String arenaName = args[1];
            int pos;
            try {
                pos = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Pozisyon 1 veya 2 olmalidir!", NamedTextColor.RED));
                return true;
            }

            if (!YfendDuel.getInstance().getArenas().containsKey(arenaName)) {
                player.sendMessage(Component.text("Boyle bir arena bulunamadi!", NamedTextColor.RED));
                return true;
            }
            if (pos < 1 || pos > 2) {
                player.sendMessage(Component.text("Lutfen 1 ya da 2 yazin!", NamedTextColor.RED));
                return true;
            }

            YfendDuel.getInstance().getArenas().get(arenaName)[pos - 1] = player.getLocation();
            player.sendMessage(Component.text("Arena " + arenaName + " icin " + pos + ". dogma noktasi ayarlandi!", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("kabul")) {
            YfendDuel.DuelRequest req = YfendDuel.getInstance().getActiveRequests().get(player.getUniqueId());
            if (req == null) {
                player.sendMessage(Component.text("Aktif bir duello isteginiz yok!", NamedTextColor.RED));
                return true;
            }
            Player challenger = Bukkit.getPlayer(req.challenger);
            if (challenger != null && req.arenaName != null) {
                org.bukkit.Location[] locs = YfendDuel.getInstance().getArenas().get(req.arenaName);
                if (locs != null && locs[0] != null && locs[1] != null) {
                    challenger.teleport(locs[0]);
                    player.teleport(locs[1]);
                    challenger.sendMessage(Component.text("Duello basladi!", NamedTextColor.GOLD));
                    player.sendMessage(Component.text("Duello basladi!", NamedTextColor.GOLD));
                } else {
                    player.sendMessage(Component.text("Arena dogma noktalari ayarlanmamis!", NamedTextColor.RED));
                }
            }
            YfendDuel.getInstance().getActiveRequests().remove(player.getUniqueId());
            return true;
        }

        if (args[0].equalsIgnoreCase("reddet")) {
            YfendDuel.getInstance().getActiveRequests().remove(player.getUniqueId());
            player.sendMessage(Component.text("Duello teklifini reddettiniz.", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || target.equals(player)) {
            player.sendMessage(Component.text("Gecersiz oyuncu!", NamedTextColor.RED));
            return true;
        }

        YfendDuel.getInstance().getActiveRequests().put(target.getUniqueId(), new YfendDuel.DuelRequest(player.getUniqueId(), target.getUniqueId()));
        openDuelGUI(player, target.getName());
        return true;
    }

    public static void openDuelGUI(Player player, String targetName) {
        Inventory gui = Bukkit.createInventory(null, 27, Component.text("Duello Ayarlari: " + targetName));

        gui.setItem(10, createGuiItem(Material.CHEST, "Envanter Dusmesi", List.of("Durum: KORUMALI")));
        gui.setItem(12, createGuiItem(Material.OAK_LOG, "Blok/Merdiven Koyma", List.of("Durum: ACIK")));
        gui.setItem(14, createGuiItem(Material.COBWEB, "Ag Koyma", List.of("Durum: ACIK")));
        gui.setItem(16, createGuiItem(Material.PAPER, "Arena Sec", List.of("Varsayilan Arena Secili")));
        gui.setItem(22, createGuiItem(Material.LIME_WOOL, "GONDER", List.of("Istegi Oyuncuya Yolla")));

        player.openInventory(gui);
    }

    private static ItemStack createGuiItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, NamedTextColor.GOLD));
            meta.lore(lore.stream().map(l -> Component.text(l, NamedTextColor.GRAY)).toList());
            item.setItemMeta(meta);
        }
        return item;
    }
}
