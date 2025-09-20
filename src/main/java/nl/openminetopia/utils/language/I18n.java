package nl.openminetopia.utils.language;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class I18n {
    private final JavaPlugin plugin;
    private final Locale defaultLocale;
    private final File langDir;
    private final MiniMessage mm = MiniMessage.miniMessage();

    // Cache loaded YAML per filename
    private final Map<String, YamlConfiguration> cache = new ConcurrentHashMap<>();

    public I18n(JavaPlugin plugin, Locale defaultLocale) {
        this.plugin = plugin;
        this.defaultLocale = defaultLocale;
        this.langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        // Make sure defaults exist in data folder (optional, but handy)
        saveIfMissing("lang/" + "en_EN.yml");
        saveIfMissing("lang/" + "nl_NL.yml");
    }

    private void saveIfMissing(String resourcePath) {
        File out = new File(plugin.getDataFolder(), resourcePath);
        if (!out.exists()) {
            out.getParentFile().mkdirs();
            plugin.saveResource(resourcePath, false);
        }
    }

    // Locale detection (Paper modern + fallback for older)
    private static Locale localeOf(CommandSender sender, Locale fallback) {
        if (sender instanceof Player p) {
            try {
                Locale loc = p.locale(); // Paper 1.19+
                if (loc != null) return loc;
            } catch (NoSuchMethodError ignored) {
                try {
                    String s = p.getLocale(); // older Bukkit returns "en_us"
                    if (s != null && !s.isEmpty())
                        return Locale.forLanguageTag(s.replace('_', '-'));
                } catch (Throwable ignored2) {}
            }
        }
        return fallback; // console & others
    }

    // Candidate filenames in order of specificity
    private List<String> candidates(Locale loc) {
        List<String> out = new ArrayList<>();
        String lang = loc.getLanguage();              // "nl"
        String country = loc.getCountry();            // "BE"
        String defLang = defaultLocale.getLanguage();
        String defCountry = defaultLocale.getCountry();

        if (!lang.isEmpty() && !country.isEmpty()) out.add(lang + "_" + country + ".yml"); // messages_nl_BE.yml
        if (!lang.isEmpty()) out.add(lang + ".yml");                                        // messages_nl.yml
        if (!defLang.isEmpty() && !defCountry.isEmpty()) out.add(defLang + "_" + defCountry + ".yml");
        if (!defLang.isEmpty()) out.add(defLang + ".yml");
        out.add("messages.yml"); // ultimate fallback if you keep one
        return out;
    }

    private YamlConfiguration loadFile(String filename) {
        return cache.computeIfAbsent(filename, f -> {
            File file = new File(langDir, f);
            if (file.exists()) {
                return YamlConfiguration.loadConfiguration(file);
            }
            // fallback to bundled resource inside the JAR
            try (InputStreamReader reader = new InputStreamReader(
                    plugin.getResource("lang/" + f), StandardCharsets.UTF_8)) {
                if (reader != null) {
                    YamlConfiguration conf = YamlConfiguration.loadConfiguration(reader);
                    return conf;
                }
            } catch (Exception ignored) {}
            return new YamlConfiguration(); // empty
        });
    }

    private String rawString(CommandSender target, String key) {
        Locale loc = localeOf(target, defaultLocale);
        for (String name : candidates(loc)) {
            String v = loadFile(name).getString(key);
            if (v != null) return v;
        }
        return null;
    }

    private static String normalizeMiniMessage(String s) {
        // Let you keep <new_line> in files
        return s.replace("<new_line>", "<newline>");
    }

    public Component translate(CommandSender target, String key, TagResolver... placeholders) {
        String s = rawString(target, key);
        if (s == null) return Component.text(key); // show key when missing
        // Render either MiniMessage or legacy "&" if used
        if (s.indexOf('<') >= 0) {
            return mm.deserialize(normalizeMiniMessage(s), TagResolver.resolver(placeholders));
        }
        if (s.indexOf('&') >= 0) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
        }
        return Component.text(s);
    }

    public Component trCount(CommandSender target, String baseKey, int n, TagResolver... extra) {
        String suffix = (n == 0) ? ".zero" : (n == 1) ? ".one" : ".many";
        TagResolver count = Placeholder.unparsed("n", Integer.toString(n));
        return translate(target, baseKey + suffix, TagResolver.resolver(count, TagResolver.resolver(extra)));
    }

    public Component withPrefix(CommandSender target, Component message) {
        return translate(target, "general.prefix").append(Component.space()).append(message);
    }

    public void reload() { cache.clear(); }
}

