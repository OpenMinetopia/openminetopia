package nl.openminetopia.modules.boots.objects;

import nl.openminetopia.modules.boots.enums.BootEffect;
import nl.openminetopia.modules.boots.enums.BootType;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CustomBoots {
    
    private final BootType bootType;
    private final BootEffect effect;
    private final double multiplier;
    
    public CustomBoots(BootType bootType, BootEffect effect, double multiplier) {
        this.bootType = bootType;
        this.effect = effect;
        this.multiplier = multiplier;
    }
    
    public ItemStack createBoots() {
        ItemStack boots = new ItemStack(bootType.getMaterial());
        ItemMeta meta = boots.getItemMeta();
        
        if (meta != null) {
            // Set display name
            meta.displayName(ChatUtils.color("<gold>" + bootType.getDisplayName() + " (" + effect.getDisplayName() + ")"));
            
            // No lore - attributes will show the effects
            
            // Add enchantments and attributes based on effect
            if (effect == BootEffect.ICE) {
                meta.addEnchant(Enchantment.FROST_WALKER, (int) multiplier, true);
            } else if (effect == BootEffect.SPEED) {
                // Add speed attribute modifier
                meta.addAttributeModifier(
                    org.bukkit.attribute.Attribute.MOVEMENT_SPEED,
                    new org.bukkit.attribute.AttributeModifier(
                        java.util.UUID.randomUUID(),
                        "speed_boost",
                        multiplier,
                        org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR,
                        org.bukkit.inventory.EquipmentSlot.FEET
                    )
                );
            } else if (effect == BootEffect.BLUB) {
                // Blub boots get depth strider enchantment
                meta.addEnchant(Enchantment.DEPTH_STRIDER, Math.min(3, (int) (multiplier * 100)), true);
            }
            
            // Add custom data
            meta.getPersistentDataContainer().set(
                new NamespacedKey("openmt", "custom_boots"), 
                PersistentDataType.BOOLEAN, 
                true
            );
            meta.getPersistentDataContainer().set(
                new NamespacedKey("openmt", "boots_effect"), 
                PersistentDataType.STRING, 
                effect.name()
            );
            meta.getPersistentDataContainer().set(
                new NamespacedKey("openmt", "boots_multiplier"), 
                PersistentDataType.DOUBLE, 
                multiplier
            );
            
            // Hide enchantments
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            boots.setItemMeta(meta);
        }
        
        return boots;
    }
    
    public BootType getBootType() {
        return bootType;
    }
    
    public BootEffect getEffect() {
        return effect;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
}
