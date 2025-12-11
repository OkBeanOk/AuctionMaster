package me.qKing12.AuctionMaster.ItemConstructor;

import me.qKing12.AuctionMaster.AuctionMaster;
import me.qKing12.AuctionMaster.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Locale;

import static me.qKing12.AuctionMaster.Utils.SkullTexture.getSkull;

public class ItemConstructorLegacy implements ItemConstructor {

    public ItemStack getItem(Material material, String name, ArrayList<String> lore){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(Utils.chat(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem(Material material, short data, String name, ArrayList<String> lore){
        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(Utils.chat(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem(String material, String name, ArrayList<String> lore){
        ItemStack item = getItemFromMaterial(material);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(Utils.chat(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem(ItemStack itemFrom, String name, ArrayList<String> lore){
        ItemStack item = itemFrom.clone();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(Utils.chat(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItemFromMaterial(String material) {
        // If you have skull/head support here, keep it before this block
        if (material == null || material.trim().isEmpty()) {
            AuctionMaster.plugin.getLogger().warning(
                    "[AuctionMaster] Empty material string in config, using STONE as fallback.");
            return new ItemStack(Material.STONE, 1);
        }

        material = material.trim().toUpperCase(Locale.ROOT);

        ItemStack item;

        try {
            // Try direct Material enum first
            Material mat = Material.getMaterial(material);
            if (mat != null) {
                return new ItemStack(mat, 1);
            }

            // Try split format: "ID:DATA:DURABILITY" or "NAME:DATA:DURABILITY"
            String[] materialArgs = material.split("[:]", 3);
            short data = 0;
            short durability = 0;

            if (materialArgs.length > 1) {
                data = Short.parseShort(materialArgs[1]);

                if (materialArgs.length > 2)
                    durability = Short.parseShort(materialArgs[2]);
            }

            try {
                // Numeric ID path (legacy)
                int id = Integer.parseInt(materialArgs[0]);
                Material legacyMat = Material.getMaterial("LEGACY_" + Utils.getIdF(id));
                if (legacyMat == null) {
                    AuctionMaster.plugin.getLogger().warning(
                            "[AuctionMaster] Invalid legacy numeric material id '" + material
                                    + "' in config, using STONE as fallback.");
                    legacyMat = Material.STONE;
                }

                if (data <= 0)
                    item = new ItemStack(legacyMat, 1);
                else
                    item = new ItemStack(legacyMat, 1, data);
            } catch (Exception x) {
                // Non‑numeric, treat first part as a material name
                String id = materialArgs[0];
                Material nameMat = Material.getMaterial(id);
                if (nameMat == null) {
                    AuctionMaster.plugin.getLogger().warning(
                            "[AuctionMaster] Invalid material '" + material
                                    + "' in config, using STONE as fallback.");
                    nameMat = Material.STONE;
                }
                item = new ItemStack(nameMat, 1);
            }

            if (durability > 0)
                item.setDurability(durability);

        } catch (Exception ex) {
            // Last‑resort safety net – never let this method throw and break plugin startup
            AuctionMaster.plugin.getLogger().warning(
                    "[AuctionMaster] Failed to parse material '" + material
                            + "' in config, using STONE as fallback. Error: " + ex.getMessage());
            item = new ItemStack(Material.STONE, 1);
        }

        return item;
    }

}

