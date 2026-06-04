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
        NamespacedKey ownerKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner");
        for (Block linked : getLinkedBlocks(block)) {
            new CustomBlockData(linked, OpenMinetopia.getInstance()).set(ownerKey, DataType.UUID, ownerUuid);
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

        for (Block linked : getLinkedBlocks(block)) {
            PersistentDataContainer data = new CustomBlockData(linked, OpenMinetopia.getInstance());
            data.remove(ownerKey);
            data.remove(membersKey);
            data.remove(groupsKey);
        }
    }

    public void addLockMember(Block block, UUID memberUuid) {
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());

        String[] members = data.get(key, DataType.STRING_ARRAY);
        List<String> updated = members != null ? new ArrayList<>(List.of(members)) : new ArrayList<>();

        if (updated.contains(memberUuid.toString())) return;
        updated.add(memberUuid.toString());
        setLinkedData(block, key, updated.toArray(new String[0]));
    }

    public void removeLockMember(Block block, UUID memberUuid) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");

        String[] members = data.get(key, DataType.STRING_ARRAY);
        if (members == null) return;

        List<String> updatedMembers = new ArrayList<>(List.of(members));
        updatedMembers.remove(memberUuid.toString());

        setLinkedData(block, key, updatedMembers.toArray(new String[0]));
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

        if (updated.contains(group)) return;
        updated.add(group);
        setLinkedData(block, key, updated.toArray(new String[0]));
    }


    public void removeLockGroup(Block block, String group) {
        PersistentDataContainer data = new CustomBlockData(block, OpenMinetopia.getInstance());
        NamespacedKey key = new NamespacedKey(OpenMinetopia.getInstance(), "lock.groups");

        String[] groups = data.get(key, DataType.STRING_ARRAY);
        if (groups == null) return;

        List<String> updatedGroups = new ArrayList<>(List.of(groups));
        updatedGroups.remove(group);

        setLinkedData(block, key, updatedGroups.toArray(new String[0]));
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

    public void inheritDoubleDoorLock(Block placed) {
        if (!(placed.getBlockData() instanceof Door door)) return;

        Block bottom = door.getHalf() == Bisected.Half.BOTTOM
                ? placed
                : placed.getRelative(BlockFace.DOWN);
        Block partner = findDoubleDoorPartner(bottom);
        if (partner == null || !isLocked(partner)) return;

        NamespacedKey ownerKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.owner");
        NamespacedKey membersKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.members");
        NamespacedKey groupsKey = new NamespacedKey(OpenMinetopia.getInstance(), "lock.groups");

        PersistentDataContainer partnerData = new CustomBlockData(partner, OpenMinetopia.getInstance());
        UUID owner = partnerData.get(ownerKey, DataType.UUID);
        String[] members = partnerData.get(membersKey, DataType.STRING_ARRAY);
        String[] groups = partnerData.get(groupsKey, DataType.STRING_ARRAY);

        for (Block linked : getLinkedBlocks(bottom)) {
            PersistentDataContainer data = new CustomBlockData(linked, OpenMinetopia.getInstance());
            if (owner != null) data.set(ownerKey, DataType.UUID, owner);
            if (members != null) data.set(membersKey, DataType.STRING_ARRAY, members);
            if (groups != null) data.set(groupsKey, DataType.STRING_ARRAY, groups);
        }
    }

    private void setLinkedData(Block block, NamespacedKey key, String[] value) {
        for (Block linked : getLinkedBlocks(block)) {
            new CustomBlockData(linked, OpenMinetopia.getInstance()).set(key, DataType.STRING_ARRAY, value);
        }
    }

    private List<Block> getLinkedBlocks(Block block) {
        List<Block> linked = new ArrayList<>();
        linked.add(block);

        if (block.getBlockData() instanceof Door door) {
            Block otherHalf = door.getHalf() == Bisected.Half.BOTTOM
                    ? block.getRelative(BlockFace.UP)
                    : block.getRelative(BlockFace.DOWN);
            linked.add(otherHalf);

            Block bottom = door.getHalf() == Bisected.Half.BOTTOM ? block : otherHalf;
            Block partner = findDoubleDoorPartner(bottom);
            if (partner != null) {
                linked.add(partner);
                linked.add(partner.getRelative(BlockFace.UP));
            }
        } else if (block.getBlockData() instanceof Chest chest && chest.getType() != Chest.Type.SINGLE) {
            Block connected = getConnectedChest(block, chest);
            if (connected != null) linked.add(connected);
        }

        return linked;
    }

    public Block getDoubleDoorPartner(Block block) {
        if (!(block.getBlockData() instanceof Door door)) return null;
        Block bottom = door.getHalf() == Bisected.Half.BOTTOM ? block : block.getRelative(BlockFace.DOWN);
        return findDoubleDoorPartner(bottom);
    }

    public void setDoorOpen(Block doorBottom, boolean open) {
        if (doorBottom.getBlockData() instanceof Door bottomData) {
            bottomData.setOpen(open);
            doorBottom.setBlockData(bottomData);
        }
        Block top = doorBottom.getRelative(BlockFace.UP);
        if (top.getBlockData() instanceof Door topData) {
            topData.setOpen(open);
            top.setBlockData(topData);
        }
    }

    private Block findDoubleDoorPartner(Block bottom) {
        if (!(bottom.getBlockData() instanceof Door door)) return null;

        BlockFace[] sides = switch (door.getFacing()) {
            case NORTH, SOUTH -> new BlockFace[]{BlockFace.EAST, BlockFace.WEST};
            case EAST, WEST -> new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH};
            default -> new BlockFace[0];
        };

        for (BlockFace side : sides) {
            Block neighbour = bottom.getRelative(side);
            if (neighbour.getBlockData() instanceof Door neighbourDoor
                    && neighbourDoor.getFacing() == door.getFacing()
                    && neighbourDoor.getHinge() != door.getHinge()) {
                return neighbour;
            }
        }

        return null;
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
