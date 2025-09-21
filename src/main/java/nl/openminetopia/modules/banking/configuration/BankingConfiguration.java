package nl.openminetopia.modules.banking.configuration;

import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.menus.BankContentsMenu;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class BankingConfiguration extends ConfigurateConfig {

    private final String economyFormat;
    private final List<Material> atmMaterials;

    private final double startingBalance;

    private final String typeSelectionTitle;
    private final String accountSelectionTitle;
    private final String accountContentsTitle;

    private final List<BankContentsMenu.BankNote> bankNotes;
    private final List<String> bankNoteLore;

    @SneakyThrows
    public BankingConfiguration(File file) {
        super(file, "banking.yml", "", false);

        this.economyFormat = rootNode.node("banking", "economy-format").getString("#,##0.00");
        this.atmMaterials = new ArrayList<>();
        rootNode.node("banking", "atm-materials").getList(String.class, List.of(
                "RED_SANDSTONE_STAIRS"
        )).forEach(materialString -> {
            Material material = Material.matchMaterial(materialString);
            if (material == null) {
                OpenMinetopia.getInstance().getLogger().warning("Invalid material in atm-materials: " + materialString);
                return;
            }
            this.atmMaterials.add(material);
        });

        this.startingBalance = rootNode.node("banking", "starting-balance").getDouble(0.0);

        this.typeSelectionTitle = rootNode.node("banking", "inventories", "select-type-title").getString("<gray>Selecteer het rekeningtype:");
        this.accountSelectionTitle = rootNode.node("banking", "inventories", "select-account-title").getString("<type_color><type_name>");
        this.accountContentsTitle = rootNode.node("banking", "inventories", "account-contents-title").getString("<type_color><account_name> <reset>| <gold><account_balance>");

        this.bankNotes = new ArrayList<>();
        List<BankContentsMenu.BankNote> dummyNotes = new ArrayList<>();
        dummyNotes.add(new BankContentsMenu.BankNote(Material.GHAST_TEAR, 500));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.DIAMOND, 200));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.REDSTONE, 100));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.EMERALD, 50));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.COAL, 20));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.IRON_INGOT, 10));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.QUARTZ, 5));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.GOLD_INGOT, 1));
        dummyNotes.add(new BankContentsMenu.BankNote(Material.GOLD_NUGGET, 0.10));

        Map<String, Double> defaultNotes = dummyNotes.stream()
                .collect(Collectors.toMap(
                        note -> note.getMaterial().name(),
                        BankContentsMenu.BankNote::getValue
                ));

        Map<String, Double> notesMap = rootNode.node("banking", "notes").get(new TypeToken<>() {
        }, defaultNotes);

        this.bankNoteLore = rootNode.node("banking", "lore").getList(String.class, List.of(
                "<yellow>Officieel Minetopia bankbiljet.",
                "<yellow>Eigendom van de Centrale Bank."
        ));

        notesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(entry -> {
                    Material material = Material.matchMaterial(entry.getKey());

                    if (material == null) {
                        OpenMinetopia.getInstance().getLogger().warning("No valid material for bank note: " + entry.getKey());
                        return;
                    }

                    bankNotes.add(new BankContentsMenu.BankNote(material, entry.getValue()));
                });

    }
}
