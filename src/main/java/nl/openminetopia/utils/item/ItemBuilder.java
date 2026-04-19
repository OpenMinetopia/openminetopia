package nl.openminetopia.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Easily create itemstacks, without messing your hands. <i>Note that if you do
 * use this in one of your projects, leave this notice.</i> <i>Please do credit
 * me if you do use this in one of your projects.</i>
 *
 * @author NonameSL, Jazzkuh
 */
public class ItemBuilder {
    private ItemStack is;

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m) {
        this(m, 1);
    }

    /**
     * Create a new ItemBuilder over an existing itemstack.
     *
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    /**Ok
     * Clone the ItemBuilder into a new one.
     *
     * @return The cloned instance.
     */
    public ItemBuilder clone() {
        return new ItemBuilder(is.clone());
    }

    public ItemBuilder setNBT(String key, Object value) {
        is = PersistentDataUtil.set(is, value, key);
        return this;
    }

    public ItemBuilder setType(Material material) {
        is.setType(material);
        return this;
    }

    /**
     * Change the damage of the item.
     *
     * @param damage The damage to set it to.
     */
    public ItemBuilder setDamage(int damage) {
        Damageable im = (Damageable) is.getItemMeta();
        im.setDamage(damage);
        return this;
    }

    /**
     * Set the item_model of the item.
     *
     * @param itemModel The item_model to set it to.
     */
    public ItemBuilder setItemModel(String itemModel) {
        ItemMeta im = is.getItemMeta();
        String[] split = itemModel.split(":");
        if (split.length != 2) throw new IllegalArgumentException("Item model must be in the format namespace:key");
        NamespacedKey key = new NamespacedKey(split[0], split[1]);
        im.setItemModel(key);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     */
    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.displayName(ChatUtils.color(name).decoration(TextDecoration.ITALIC, false));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     */
    public ItemBuilder setName(Component name) {
        ItemMeta im = is.getItemMeta();
        im.displayName(name.decoration(TextDecoration.ITALIC, false));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Make the item glow.
     *
     * @param toggle Glow on or off
     */
    public ItemBuilder setGlowing(boolean toggle) {
        if (toggle) {
            addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
            setItemFlag(ItemFlag.HIDE_ENCHANTS);
            return this;
        }
        ItemMeta im = is.getItemMeta();
        removeEnchantment(Enchantment.LUCK_OF_THE_SEA);
        im.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add an unsafe enchantment.
     *
     * @param ench  The enchantment to add.
     * @param level The level to put the enchant on.
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    /**
     * Remove a certain enchant from the item.
     *
     * @param ench The enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    /**
     * Add an enchant to the item.
     *
     * @param ench  The enchantment to add
     * @param level The level
     */
    public ItemBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchantments The enchants to add.
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addUnsafeEnchantments(enchantments);
        return this;
    }

    /**
     * Sets infinity durability on the item by setting the durability to
     * Short.MAX_VALUE.
     */
    public ItemBuilder setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(Component... lore) {
        ItemMeta im = is.getItemMeta();
        im.lore(Arrays.asList(lore));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(List<Component> lore) {
        ItemMeta im = is.getItemMeta();
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemBuilder lore(List<Component> lore) {
        ItemMeta im = is.getItemMeta();
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(Component line) {
        ItemMeta im = is.getItemMeta();
        List<Component> lore = new ArrayList<>(im.lore());
        if (!lore.contains(line))
            return this;
        lore.remove(line);
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param index The index of the lore line to remove.
     */
    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<Component> lore = new ArrayList<>(im.lore());
        if (index < 0 || index > lore.size())
            return this;
        lore.remove(index);
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLine(String line) {
        this.addLoreLine(ChatUtils.color(line).decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder addLoreLine(Component line) {
        ItemMeta im = is.getItemMeta();
        List<Component> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.lore());
        lore.add(line);
        im.lore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor
     * pieces.
     *
     * @param color The color to set it to.
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    /**
     * Set the owner of a skull.
     *
     * @param owner The owner of the desired skull.
     */
    public ItemBuilder setSkullOwner(Player owner) {
        SkullMeta im = (SkullMeta) is.getItemMeta();
        im.setOwningPlayer(owner);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the owner of a skull.
     *
     * @param owner The owner of the desired skull.
     */
    public ItemBuilder setSkullOwner(OfflinePlayer owner) {
        SkullMeta im = (SkullMeta) is.getItemMeta();
        im.setOwningPlayer(owner);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the owner of a skull.
     *
     * @param owner The owner of the desired skull.
     */
    public ItemBuilder setSkullOwner(String owner) {
        SkullMeta im = (SkullMeta) is.getItemMeta();
        im.setOwner(owner);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the flags of an item.
     *
     * @param itemFlag The itemFlag you want to add.
     */
    public ItemBuilder setItemFlag(ItemFlag itemFlag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(itemFlag);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the flags of an item.
     *
     * @param itemFlag The itemFlag you want to add.
     */
    public ItemBuilder setItemFlag(ItemFlag[] itemFlag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(itemFlag);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the Custom Model Data of an item.
     *
     * @param data The custom model data value.
     */
    public ItemBuilder setCustomModelData(Integer data) {
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(data);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the equippable component of an item.
     *
     * @param slot The equipment slot to set it to.
     */
    public ItemBuilder setEquippableSlot(EquipmentSlot slot) {
        ItemMeta im = is.getItemMeta();
        EquippableComponent equippable = im.getEquippable();
        if (equippable == null) return this;
        equippable.setSlot(slot);
        im.setEquippable(equippable);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Set the equippable component of an item.
     *
     * @param model The equipment asset_id to set it to.
     */
    public ItemBuilder setEquippableModel(NamespacedKey model) {
        ItemMeta im = is.getItemMeta();
        EquippableComponent equippable = im.getEquippable();
        if (equippable == null) return this;
        equippable.setModel(model);
        im.setEquippable(equippable);
        is.setItemMeta(im);
        return this;
    }


    public ItemBuilder setSkinURL(String url) {
        try {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "skull");
            PropertyMap properties = gameProfile.properties();
            if (properties == null) {
                return this;
            }

            String textureJSON = "{textures:{SKIN:{url:\"" + url + "\"}}}";
            String encoded = Base64Coder.encodeString(textureJSON);

            Property newProperty = new Property("textures", encoded);
            properties.put("textures", newProperty);

            ItemMeta skullMeta = is.getItemMeta();
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, gameProfile);

            is.setItemMeta(skullMeta);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    /**
     * Retrieves the itemstack from the ItemBuilder.
     *
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack() {
        return is;
    }
}