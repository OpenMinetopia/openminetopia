package nl.openminetopia.framework.runnables;

public record DirtyPolicy(
        boolean dirtyOnJoin,
        boolean dirtyOnQuit,
        long initialDelayMs
) {

    public static final DirtyPolicy DEFAULT = new DirtyPolicy(true, false, 2 * 1000L);
    public static final DirtyPolicy DB_SAVING_TASK = new DirtyPolicy(false, false, 30 * 1000L);

}
