package nl.openminetopia.modules.items;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.items.commands.ItemsCommand;
import nl.openminetopia.modules.items.configuration.CategoriesConfiguration;
import nl.openminetopia.modules.items.configuration.ItemConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.stream.Stream;

@Getter
public class ItemsModule extends ExtendedSpigotModule {
    public ItemsModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    private CategoriesConfiguration categoriesConfiguration;

    @Override
    public void onEnable() {
        reload();

        OpenMinetopia.getCommandManager().getCommandCompletions().registerAsyncCompletion("items",
                c -> categoriesConfiguration.getCategories().values().stream()
                        .flatMap(category -> category.items().stream())
                        .map(item -> item.namespacedKey().asString())
                        .toList()
        );

        registerComponent(new ItemsCommand());
    }

    public void reload() {
        Path itemsPath = OpenMinetopia.getInstance().getDataFolder().toPath().resolve("items");
        try {
            if (!Files.exists(itemsPath)) {
                Files.createDirectories(itemsPath);
                copyResources("default/items", itemsPath);
            }
        } catch (Exception e) {
            OpenMinetopia.getInstance().getLogger().warning("Failed to create items folder or copy resources.");
            e.printStackTrace();
            return;
        }

        if (this.categoriesConfiguration != null) {
            this.categoriesConfiguration.getCategories().clear();
        }

        this.categoriesConfiguration = new CategoriesConfiguration(itemsPath.toFile());

        loadItems(itemsPath.toFile());
    }

    private void copyResources(String resourcePath, Path destination) throws URISyntaxException, IOException {
        URI uri = OpenMinetopia.class.getResource("/" + resourcePath).toURI();
        Path myPath;

        if (uri.getScheme().equals("jar")) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                myPath = fs.getPath("/" + resourcePath);
                copyPathContents(myPath, destination);
            } catch (FileSystemAlreadyExistsException e) {
                myPath = FileSystems.getFileSystem(uri).getPath("/" + resourcePath);
                copyPathContents(myPath, destination);
            }
        } else {
            myPath = Paths.get(uri);
            copyPathContents(myPath, destination);
        }
    }

    private void copyPathContents(Path source, Path destination) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    Path target = destination.resolve(source.relativize(path).toString());
                    if (Files.isDirectory(path)) {
                        if (!Files.exists(target)) {
                            Files.createDirectories(target);
                        }
                    } else {
                        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private void loadItems(File directory) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().equalsIgnoreCase("categories.yml")) continue;
            if (file.isDirectory()) {
                loadItems(file);
            } else if (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")) {
                new ItemConfiguration(file.getParentFile(), file.getName());
            }
        }
    }
}
