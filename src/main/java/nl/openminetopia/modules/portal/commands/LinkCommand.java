package nl.openminetopia.modules.portal.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.portal.PortalModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

@CommandAlias("koppel|link")
public class LinkCommand extends BaseCommand {

    @Default
    public void verify(Player player, String token) {
        verifyPlayer(player, token);
    }

    @SuppressWarnings("unchecked")
    private void verifyPlayer(Player player, String token) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("token", token);
        requestBody.put("minecraft_username", player.getName());
        requestBody.put("minecraft_uuid", player.getUniqueId().toString());

        PortalModule portalModule = OpenMinetopia.getModuleManager().get(PortalModule.class);
        WebClient webClient = WebClient.create(OpenMinetopia.getInstance().getOrCreateVertx());

        webClient.postAbs(portalModule.getPortalApiUrl() + "/minecraft/verify")
                .putHeader("Content-Type", "application/json")
                .putHeader("X-API-Key", OpenMinetopia.getDefaultConfiguration().getPortalToken())
                .sendBuffer(Buffer.buffer(requestBody.toString()), ar -> {
                    if (ar.failed()) {
                        // Handle failure case
                        ChatUtils.sendMessage(player, "<red>Er is iets fout gegaan bij het verifiëren van je account.");
                        ar.cause().printStackTrace();
                        return;
                    }

                    HttpResponse<Buffer> response = ar.result();
                    if (response.statusCode() != 200) {
                        // Handle non-200 status codes
                        ChatUtils.sendMessage(player, "<red>Er is iets fout gegaan bij het verifiëren van je account.");
                        return;
                    }

                    // Successful verification
                    ChatUtils.sendMessage(player, "<green>Je account is succesvol gekoppeld!");
                });
    }
}
