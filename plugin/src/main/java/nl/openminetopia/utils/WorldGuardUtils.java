package nl.openminetopia.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.experimental.UtilityClass;
import nl.openminetopia.modules.fitness.listeners.PlayerEatListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@UtilityClass
public class WorldGuardUtils {
    public ProtectedRegion getProtectedRegion(@Nonnull Location location, Predicate<Integer> priorityPredicate) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        ProtectedRegion region = null;
        if (manager != null) {
            ApplicableRegionSet fromRegions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
            region = fromRegions.getRegions().stream().filter(protectedRegion -> priorityPredicate.test(protectedRegion.getPriority()))
                    .max(Comparator.comparing(ProtectedRegion::getPriority)).orElse(null);
        }
        return region;
    }

    public List<ProtectedRegion> getProtectedRegions(@Nonnull Location location, Predicate<Integer> priorityPredicate) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (manager == null) return new ArrayList<>();
        return manager.getApplicableRegions(BukkitAdapter.asBlockVector(location)).getRegions().stream()
                .filter(protectedRegion -> priorityPredicate.test(protectedRegion.getPriority())).toList();
    }

    public List<ProtectedRegion> getProtectedRegions(@Nonnull World world, Predicate<Integer> priorityPredicate) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        if (manager == null) return new ArrayList<>();
        return manager.getRegions().values().stream().filter(protectedRegion -> priorityPredicate.test(protectedRegion.getPriority())).toList();
    }

    public List<ProtectedRegion> getProtectedRegions(Predicate<Integer> priorityPredicate) {
        List<ProtectedRegion> protectedRegions = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            protectedRegions.addAll(getProtectedRegions(world, priorityPredicate));
        }
        return protectedRegions;
    }

    public int getOwnedRegions(Player player) {
        int ownedRegions = 0;
        for (World world : Bukkit.getWorlds()) {
            ownedRegions += getOwnedRegions(player, world);
        }
        return ownedRegions;
    }

    public int getOwnedRegions(Player player, World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        if (manager == null) return 0;
        return (int) manager.getRegions().values().stream().filter(protectedRegion -> protectedRegion.isOwner(WorldGuardPlugin.inst().wrapPlayer(player))).count();
    }

    public boolean isRegionOwner(ProtectedRegion protectedRegion, Player player) {
        return protectedRegion != null && protectedRegion.isOwner(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public boolean isRegionMember(ProtectedRegion protectedRegion, Player player) {
        return protectedRegion != null && protectedRegion.isMember(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public boolean isRegionOwnerOrMember(ProtectedRegion protectedRegion, Player player) {
        return protectedRegion != null && (protectedRegion.isOwner(WorldGuardPlugin.inst().wrapPlayer(player)) || protectedRegion.isMember(WorldGuardPlugin.inst().wrapPlayer(player)));
    }

    public List<String> getRegionOwners(ProtectedRegion protectedRegion) {
        if (protectedRegion == null) return new ArrayList<>();
        List<String> owners = new ArrayList<>();
        for (UUID uuid : protectedRegion.getOwners().getUniqueIds()) {
            owners.add(Bukkit.getOfflinePlayer(uuid).getName());
        }

        if (owners.isEmpty()) return new ArrayList<>();
        return owners;
    }

    public List<String> getRegionMembers(ProtectedRegion protectedRegion) {
        if (protectedRegion == null) return new ArrayList<>();
        List<String> members = new ArrayList<>();
        for (UUID uuid : protectedRegion.getMembers().getUniqueIds()) {
            members.add(Bukkit.getOfflinePlayer(uuid).getName());
        }

        if (members.isEmpty()) return new ArrayList<>();
        return members;
    }

    public boolean isInRegionWithFlag(ProtectedRegion protectedRegion, StringFlag stringFlag) {
        return protectedRegion != null && protectedRegion.getFlag(stringFlag) != null;
    }

    @Nullable
    public String getRegionFlag(ProtectedRegion protectedRegion, StringFlag stringFlag) {
        if (protectedRegion != null && protectedRegion.getFlag(stringFlag) != null) {
            return protectedRegion.getFlag(stringFlag);
        }

        return null;
    }

    public boolean getRegionFlag(ProtectedRegion protectedRegion, BooleanFlag booleanFlag) {
        if (protectedRegion != null && protectedRegion.getFlag(booleanFlag) != null) {
            return Boolean.TRUE.equals(protectedRegion.getFlag(booleanFlag));
        }

        return false;
    }

    public StringFlag registerFlag(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StringFlag stringFlag = new StringFlag(flagName);
            registry.register(stringFlag);
            return stringFlag;
        } catch (Exception e) {
            Flag<?> existingFlag = registry.get(flagName);
            if (existingFlag instanceof StringFlag stringFlag) return stringFlag;
        }

        return null;
    }

    public DoubleFlag registerDoubleFlag(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            DoubleFlag doubleFlag = new DoubleFlag(flagName);
            registry.register(doubleFlag);
            return doubleFlag;
        } catch (Exception e) {
            Flag<?> existingFlag = registry.get(flagName);
            if (existingFlag instanceof DoubleFlag doubleFlag) return doubleFlag;
        }

        return null;
    }

    public BooleanFlag registerBooleanFlag(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            BooleanFlag booleanFlag = new BooleanFlag(flagName);
            registry.register(booleanFlag);
            return booleanFlag;
        } catch (Exception e) {
            Flag<?> existingFlag = registry.get(flagName);
            if (existingFlag instanceof BooleanFlag booleanFlag) return booleanFlag;
        }

        return null;
    }

    public FlagRegistry getFlagRegistry() {
        return WorldGuard.getInstance().getFlagRegistry();
    }
}