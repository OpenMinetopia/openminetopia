package nl.openminetopia.modules.lock.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class LockUtil {

    public void setLocked(Block block, UUID ownerUuid) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey ownerKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner");
        data.set(ownerKey, DataType.UUID, ownerUuid);

        if (block.getBlockData() instanceof Door door) {
            Block otherHalf = door.getHalf() == Bisected.Half.BOTTOM
                    ? block.getRelative(BlockFace.UP)
                    : block.getRelative(BlockFace.DOWN);
            PersistentDataContainer otherData = new CustomBlockData(otherHalf, OpenMinetopia.getInstance());
            otherData.set(ownerKey, DataType.UUID, ownerUuid);
        }

        if (block.getBlockData() instanceof Chest chest && chest.getType() != Chest.Type.SINGLE) {
            Block connected = getConnectedChest(block, chest);
            if (connected != null) {
                PersistentDataContainer otherData = new CustomBlockData(connected, OpenMinetopia.getInstance());
                otherData.set(ownerKey, DataType.UUID, ownerUuid);
            }
        }
    }

    private Block getConnectedChest(Block block, Chest chest) {
        BlockFace facing = chest.getFacing();
        return switch (chest.getType()) {
            case LEFT -> block.getRelative(rotateClockwise(facing));
            case RIGHT -> block.getRelative(rotateCounterClockwise(facing));
            default -> null;
        };
    }

    private BlockFace rotateClockwise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.EAST;
            case EAST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            default -> face;
        };
    }

    private BlockFace rotateCounterClockwise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.WEST;
            case WEST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            default -> face;
        };
    }

    private Block getOtherHalf(Block block) {
        if (block.getBlockData() instanceof Door door) {
            return door.getHalf() == Bisected.Half.TOP
                    ? block.getRelative(BlockFace.DOWN)
                    : block.getRelative(BlockFace.UP);
        }
        
        if (block.getBlockData() instanceof Chest chest && chest.getType() != Chest.Type.SINGLE) {
            return getConnectedChest(block, chest);
        }
        
        return null;
    }

    public void removeLock(Block block) {
        NamespacedKey ownerKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner");
        NamespacedKey membersKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");
        NamespacedKey groupsKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.groups");

        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        data.remove(ownerKey);
        data.remove(membersKey);
        data.remove(groupsKey);

        if (block.getBlockData() instanceof Door door) {
            Block otherHalf = door.getHalf() == Bisected.Half.BOTTOM
                    ? block.getRelative(BlockFace.UP)
                    : block.getRelative(BlockFace.DOWN);
            PersistentDataContainer otherData = new CustomBlockData(otherHalf, OpenMinetopia.getInstance());
            otherData.remove(ownerKey);
            otherData.remove(membersKey);
            otherData.remove(groupsKey);
        }

        if (block.getBlockData() instanceof Chest chest && chest.getType() != Chest.Type.SINGLE) {
            Block connected = getConnectedChest(block, chest);
            if (connected != null) {
                PersistentDataContainer otherData = new CustomBlockData(connected, OpenMinetopia.getInstance());
                otherData.remove(ownerKey);
                otherData.remove(membersKey);
                otherData.remove(groupsKey);
            }
        }
    }

    public void addLockMember(Block block, UUID memberUuid) {
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());

        String[] members = data.get(key, DataType.STRING_ARRAY);
        List<String> updated = members != null ? new ArrayList<>(List.of(members)) : new ArrayList<>();

        if (!updated.contains(memberUuid.toString())) {
            updated.add(memberUuid.toString());
            data.set(key, DataType.STRING_ARRAY, updated.toArray(new String[0]));
        }

        syncLockData(block, key, updated.toArray(new String[0]));
    }

    public void removeLockMember(Block block, UUID memberUuid) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");

        String[] members = data.get(key, DataType.STRING_ARRAY);
        if (members == null) return;

        List<String> updatedMembers = new ArrayList<>(List.of(members));
        updatedMembers.remove(memberUuid.toString());

        data.set(key, DataType.STRING_ARRAY, updatedMembers.toArray(new String[0]));
        
        syncLockData(block, key, updatedMembers.toArray(new String[0]));
    }

    public List<UUID> getLockMembers(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");

        String[] members = data.get(key, DataType.STRING_ARRAY);
        
        if (members == null) {
            Block otherBlock = getOtherHalf(block);
            if (otherBlock != null) {
                PersistentDataContainer otherData = new CustomBlockData(otherBlock, OpenMinetopia.getInstance());
                members = otherData.get(key, DataType.STRING_ARRAY);
            }
        }
        
        List<UUID> uuids = new ArrayList<>();
        if (members == null) return uuids;
        
        for (String member : members) {
            uuids.add(UUID.fromString(member));
        }

        return uuids;
    }

    public void addLockGroup(Block block, String group) {
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.groups");
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());

        String[] groups = data.get(key, DataType.STRING_ARRAY);
        List<String> updated = groups != null ? new ArrayList<>(List.of(groups)) : new ArrayList<>();

        if (!updated.contains(group)) {
            updated.add(group);
            data.set(key, DataType.STRING_ARRAY, updated.toArray(new String[0]));
        }

        syncLockData(block, key, updated.toArray(new String[0]));
    }


    public void removeLockGroup(Block block, String group) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.groups");

        String[] groups = data.get(key, DataType.STRING_ARRAY);
        if (groups == null) return;

        List<String> updatedGroups = new ArrayList<>(List.of(groups));
        updatedGroups.remove(group);

        data.set(key, DataType.STRING_ARRAY, updatedGroups.toArray(new String[0]));
        
        syncLockData(block, key, updatedGroups.toArray(new String[0]));
    }

    public List<String> getLockGroups(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.groups");

        String[] groups = data.get(key, DataType.STRING_ARRAY);
        
        if (groups == null) {
            Block otherBlock = getOtherHalf(block);
            if (otherBlock != null) {
                PersistentDataContainer otherData = new CustomBlockData(otherBlock, OpenMinetopia.getInstance());
                groups = otherData.get(key, DataType.STRING_ARRAY);
            }
        }
        
        List<String> groupList = new ArrayList<>();
        if (groups == null) return groupList;
        
        Collections.addAll(groupList, groups);

        return groupList;
    }

    public UUID getLockOwner(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey ownerKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner");
        UUID owner = data.get(ownerKey, DataType.UUID);
        
        if (owner == null) {
            Block otherBlock = getOtherHalf(block);
            if (otherBlock != null) {
                PersistentDataContainer otherData = new CustomBlockData(otherBlock, OpenMinetopia.getInstance());
                owner = otherData.get(ownerKey, DataType.UUID);
            }
        }
        
        return owner;
    }

    public boolean isLocked(Block block) {
        NamespacedKey ownerKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner");
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());

        if (data.has(ownerKey, DataType.UUID)) return true;

        if (block.getBlockData() instanceof Door door) {
            Block otherHalf = door.getHalf() == Bisected.Half.TOP
                    ? block.getRelative(BlockFace.DOWN)
                    : block.getRelative(BlockFace.UP);
            PersistentDataContainer otherData = new CustomBlockData(otherHalf, OpenMinetopia.getInstance());
            return otherData.has(ownerKey, DataType.UUID);
        }

        if (block.getBlockData() instanceof Chest chest && chest.getType() != Chest.Type.SINGLE) {
            Block connected = getConnectedChest(block, chest);
            if (connected != null) {
                PersistentDataContainer otherData = new CustomBlockData(connected, OpenMinetopia.getInstance());
                return otherData.has(ownerKey, DataType.UUID);
            }
        }

        return false;
    }

    public boolean canOpen(Block block, Player player) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        if (!isLocked(block)) return true;

        if (player.hasPermission("openminetopia.lock.bypass")) return true;

        UUID ownerUuid = data.get(new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner"), DataType.UUID);
        if (ownerUuid == null) return false;

        if (ownerUuid.equals(player.getUniqueId())) return true;

        List<UUID> members = getLockMembers(block);
        if (members.contains(player.getUniqueId())) return true;
        List<String> groups = getLockGroups(block);
        for (String group : groups) {
            if (player.hasPermission("group." + group)) return true;
        }

        return false;
    }

    private void syncLockData(Block block, NamespacedKey key, String[] value) {
        if (block.getBlockData() instanceof Door door) {
            Block other = door.getHalf() == Bisected.Half.TOP
                    ? block.getRelative(BlockFace.DOWN)
                    : block.getRelative(BlockFace.UP);
            PersistentDataContainer otherData = new CustomBlockData(other, OpenMinetopia.getInstance());
            otherData.set(key, DataType.STRING_ARRAY, value);
        }

        if (block.getBlockData() instanceof Chest chest && chest.getType() != Chest.Type.SINGLE) {
            Block connected = getConnectedChest(block, chest);
            if (connected != null) {
                PersistentDataContainer otherData = new CustomBlockData(connected, OpenMinetopia.getInstance());
                otherData.set(key, DataType.STRING_ARRAY, value);
            }
        }
    }


    public boolean isLockable(Block block) {
        if (block.getBlockData() instanceof Door) return true;
        if (block.getBlockData() instanceof TrapDoor) return true;
        if (block.getBlockData() instanceof Gate) return true;
        if (block.getBlockData() instanceof Chest) return true;
        if (block.getBlockData() instanceof Sign) return true;
        if (block.getBlockData() instanceof Furnace) return true;
        return block.getBlockData() instanceof Barrel;
    }
}
