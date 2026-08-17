package nl.openminetopia.modules.books.configuration;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.books.objects.CustomBook;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BooksConfiguration extends ConfigurateConfig {

    private final List<CustomBook> customBooks = new ArrayList<>();

    public BooksConfiguration(File file) {
        super(file, "books.yml", "default/books.yml", true);

        this.rootNode.childrenMap().forEach((key, value) -> {
            if (!(key instanceof String identifier)) return;

            boolean enabled = value.node("enabled").getBoolean();
            if (!enabled) return;

            boolean copy = value.node("copy").getBoolean();
            String menuItem = value.node("menu-item").getString("DIAMOND");
            Material material = Material.matchMaterial(menuItem);
            if (material == null) {
                OpenMinetopia.getInstance().getLogger().warning("Invalid material in books.yml for book '" + identifier + "': " + menuItem);
                return;
            }

            String name = value.node("name").getString();
            String itemName = value.node("item-name").getString();
            String description = value.node("description").getString();

            Map<String, String> variables = new HashMap<>();
            value.node("variables").childrenMap().forEach((k, v) -> {
                if (!(k instanceof String variable)) return;
                variables.put(variable, v.node("explanation").getString());
            });

            String content = value.node("content").getString();

            CustomBook customBook = new CustomBook(identifier, copy, material, name, itemName, description, variables, content);
            customBooks.add(customBook);
        });
    }
}
