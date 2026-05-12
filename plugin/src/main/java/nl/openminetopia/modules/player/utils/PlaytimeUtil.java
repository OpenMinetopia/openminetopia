package nl.openminetopia.modules.player.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.configuration.MessageConfiguration;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class PlaytimeUtil {

    public static long seconds(long time) {
        return TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
    }

    public static long minutes(long time) {
        return TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
    }

    public static long hours(long time) {
        return TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(time));
    }

    public static long days(long time) {
        return TimeUnit.MILLISECONDS.toDays(time);
    }

    public static String formatPlaytime(long playtimeInSeconds) {
        long days = days(playtimeInSeconds);
        long hours = hours(playtimeInSeconds);
        long minutes = minutes(playtimeInSeconds);
        long seconds = seconds(playtimeInSeconds);
        return MessageConfiguration.message("player_time_format")
                .replace("<days>", days + "")
                .replace("<hours>", hours + "")
                .replace("<minutes>", minutes + "")
                .replace("<seconds>", seconds + "");
    }
}