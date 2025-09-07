package nl.openminetopia.framework.runnables;


import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AbstractDirtyRunnable<K> implements Runnable {

    @Getter
    private final DirtyPolicy policy;

    private final Set<K> dirty = ConcurrentHashMap.newKeySet();
    private final Map<K, Long> lastTouch = new ConcurrentHashMap<>();
    private final long minIntervalMs;
    private final int batch;
    private final long heartbeatMs;
    private final Supplier<List<K>> allKeysSupplier;
    private final boolean async;

    private volatile long lastHeartbeat = 0L;
    private int sweepCursor = 0;

    private BukkitTask task;

    protected AbstractDirtyRunnable(long minIntervalMs, int batch, long heartbeatMs, Supplier<List<K>> allKeysSupplier) {
        this(minIntervalMs, batch, heartbeatMs, allKeysSupplier, false);
    }
    protected AbstractDirtyRunnable(long minIntervalMs, int batch, long heartbeatMs, Supplier<List<K>> allKeysSupplier, boolean async) {
        this(DirtyPolicy.DEFAULT, minIntervalMs, batch, heartbeatMs, allKeysSupplier, async);
    }
    protected AbstractDirtyRunnable(DirtyPolicy dirtyPolicy, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<K>> allKeysSupplier, boolean async) {
        this.policy = dirtyPolicy;
        this.minIntervalMs = minIntervalMs;
        this.heartbeatMs = heartbeatMs;
        this.allKeysSupplier = allKeysSupplier;
        this.batch = batch;
        this.async = async;
    }

    public void start(Plugin plugin, long periodTicks) {
        this.task = async
                ? Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 2L, periodTicks)
                : Bukkit.getScheduler().runTaskTimer(plugin, this, 2L, periodTicks);
    }

    public void forceMarkDirty(K key) {
        dirty.add(key);
        lastTouch.put(key, System.currentTimeMillis());
    }

    public void markDirty(K key) {
        long now = System.currentTimeMillis();
        Long last = lastTouch.get(key);
        if (last == null || (now - last) >= minIntervalMs) {
            dirty.add(key);
            lastTouch.put(key, now);
        }
    }

    public void remove(K key) {
        dirty.remove(key);
        lastTouch.remove(key);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        if (heartbeatMs > 0 && now - lastHeartbeat >= heartbeatMs) {
            lastHeartbeat = now;
            dirty.addAll(allKeysSafe());
        }

        int remaining = batch;
        List<K> all = allKeysSafe();
        if (!all.isEmpty()) {
            int n = Math.min(remaining / 2, Math.max(1, all.size() / 20));
            for (int i = 0; i < n; i++) {
                if (sweepCursor >= all.size()) sweepCursor = 0;
                K k = all.get(sweepCursor++);
                markDirty(k);
            }
        }

        Iterator<K> it = dirty.iterator();
        while (it.hasNext() && remaining-- > 0) {
            K k = it.next();
            it.remove();
            process(k);
        }
    }

    private List<K> allKeysSafe() {
        try {
            List<K> list = allKeysSupplier.get();
            return (list != null) ? list : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    protected abstract void process(K key);

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
