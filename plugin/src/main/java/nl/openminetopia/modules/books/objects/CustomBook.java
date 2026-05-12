package nl.openminetopia.modules.books.objects;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CustomBook {

    private final String identifier;
    private final boolean copy;
    private final Material menuItem;
    private final String name;
    private String itemName;
    private final String description;

    /**
     * A map of variables used in the book.
     * <p>
     * The key represents the variable name, and the value is the explanation of the variable that is sent to the player.
     */
    private final Map<String, String> variables;
    private String content;

    public ItemStack getBookItem(Map<String, String> variables, Player player, boolean copy) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return null;

        // Vervang de variabelen in de content
        String processedContent = content;
        String processedName = itemName;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            processedContent = processedContent.replace(entry.getKey(), entry.getValue());
            processedName = processedName.replace(entry.getKey(), entry.getValue());
        }

        String prefix = copy ? "<reset><white>[COPY] <reset>" : "";
        int maxTitleLength = 32 - prefix.length();
        String trimmedName = processedName.length() > maxTitleLength ? processedName.substring(0, maxTitleLength) : processedName;
        String bookTitle = prefix + trimmedName;

        ItemStack book = ItemStack.of(Material.WRITTEN_BOOK);
        WrittenBookContent.Builder builder = WrittenBookContent.writtenBookContent(bookTitle, player.getName());

        for (String page : splitContentToPages(processedContent)) {
            builder.addPage(ChatUtils.format(minetopiaPlayer, page));
        }

        if (copy) builder.generation(1);
        else builder.generation(0);

        book.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, builder.build());

        return new ItemBuilder(book).setName(ChatUtils.format(minetopiaPlayer, bookTitle)).toItemStack();
    }

    private static final int MAX_CHARS_PER_PAGE = 1024;
    private List<String> splitContentToPages(String text) {
        List<String> pages = new ArrayList<>();

        for (int i = 0; i < text.length(); i += MAX_CHARS_PER_PAGE) {
            pages.add(text.substring(i, Math.min(i + MAX_CHARS_PER_PAGE, text.length())));
        }
        return pages;
    }
}