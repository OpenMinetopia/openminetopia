package nl.openminetopia.modules.portal;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.portal.commands.LinkCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PortalModule extends ExtendedSpigotModule {

    public PortalModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        if (OpenMinetopia.getDefaultConfiguration().isPortalEnabled()) {
            registerComponent(new LinkCommand());
        }
    }

    public String getPortalUrl() {
        return OpenMinetopia.getDefaultConfiguration().getPortalUrl();
    }

    public String getPortalBaseUrl() {
        return resolveBaseUrl(getPortalUrl());
    }

    public String getPortalApiUrl() {
        return getPortalBaseUrl() + "/api";
    }

    public boolean isLocalDevelopmentPortal() {
        return isLocalDevelopmentHost(getPortalUrl());
    }

    /**
     * A url with an explicit scheme is used as given, so self-hosted portals can run on plain http.
     * A bare host keeps the old behaviour: https, or http for local `.test` development hosts.
     */
    public static String resolveBaseUrl(String portalUrl) {
        String url = stripTrailingSlashes(portalUrl.trim());
        if (hasScheme(url)) return url;
        return (isLocalDevelopmentHost(url) ? "http://" : "https://") + url;
    }

    public static boolean isLocalDevelopmentHost(String portalUrl) {
        if (portalUrl == null) return false;
        String host = portalUrl.trim();
        if (hasScheme(host)) host = host.substring(host.indexOf("://") + 3);

        int end = host.length();
        for (char separator : new char[]{'/', ':', '?', '#'}) {
            int index = host.indexOf(separator);
            if (index >= 0) end = Math.min(end, index);
        }
        return host.substring(0, end).toLowerCase(Locale.ROOT).endsWith(".test");
    }

    private static boolean hasScheme(String url) {
        String lower = url.toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private static String stripTrailingSlashes(String url) {
        int end = url.length();
        while (end > 0 && url.charAt(end - 1) == '/') end--;
        return url.substring(0, end);
    }
}
