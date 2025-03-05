package nl.openminetopia.modules.police;

import com.craftmend.storm.api.enums.Where;
import lombok.Getter;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.player.models.PlayerModel;
import nl.openminetopia.modules.police.commands.BodysearchCommand;
import nl.openminetopia.modules.police.commands.CriminalRecordCommand;
import nl.openminetopia.modules.police.commands.EmergencyCommand;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import nl.openminetopia.modules.police.handcuff.listeners.*;
import nl.openminetopia.modules.police.handcuff.objects.HandcuffedPlayer;
import nl.openminetopia.modules.police.listeners.PlayerArmorChangeListener;
import nl.openminetopia.modules.police.listeners.PlayerPeppersprayListener;
import nl.openminetopia.modules.police.listeners.PlayerTaserListener;
import nl.openminetopia.modules.police.models.CriminalRecordModel;
import nl.openminetopia.modules.police.taser.TaserManager;
import nl.openminetopia.modules.police.walkietalkie.WalkieTalkieManager;
import nl.openminetopia.modules.police.walkietalkie.listeners.PlayerChatListener;
import nl.openminetopia.modules.police.walkietalkie.listeners.PlayerInteractListener;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class PoliceModule extends SpigotModule<@NotNull OpenMinetopia> {

    private final HashMap<UUID, Long> emergencyCooldowns = new HashMap<>();

    private WalkieTalkieManager walkieTalkieManager;
    private TaserManager taserManager;

    public PoliceModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        walkieTalkieManager = new WalkieTalkieManager();
        taserManager = new TaserManager();

        registerComponent(new EmergencyCommand());
        registerComponent(new CriminalRecordCommand());
        registerComponent(new BodysearchCommand());
        registerComponent(new PlayerArmorChangeListener());

        /* ---- Handcuff ---- */
        registerComponent(new PlayerHandcuffListener());
        registerComponent(new PlayerMoveListener());
        registerComponent(new PlayerDropItemListener());
        registerComponent(new PlayerEntityDamageListener());
        registerComponent(new PlayerOpenInventoryListener());
        registerComponent(new PlayerPickupItemListener());
        registerComponent(new PlayerSlotChangeListener());
        registerComponent(new PlayerInventoryClickListener());

        /* ---- Walkie-talkie ---- */
        registerComponent(new PlayerChatListener());
        registerComponent(new PlayerInteractListener());

        /* ---- Pepperspray ---- */
        registerComponent(new PlayerPeppersprayListener());

        /* ---- Taser ---- */
        registerComponent(new PlayerTaserListener());
    }

    @Override
    public void onDisable() {
        HandcuffManager.getInstance().getHandcuffedPlayers().forEach(HandcuffedPlayer::release);
    }

    public CompletableFuture<Integer> addCriminalRecord(PlayerModel playerModel, String description, UUID officerUuid, Long date) {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        StormDatabase.getExecutorService().submit(() -> {
            CriminalRecordModel criminalRecordModel = new CriminalRecordModel();
            criminalRecordModel.setPlayerId(playerModel.getId());
            criminalRecordModel.setDescription(description);
            criminalRecordModel.setOfficerId(officerUuid);
            criminalRecordModel.setDate(date);

            playerModel.getCriminalRecords().add(criminalRecordModel);

            int id = StormDatabase.getInstance().saveStormModel(criminalRecordModel).join();
            completableFuture.complete(id);
        });
        return completableFuture;
    }

    public CompletableFuture<Void> removeCriminalRecord(PlayerModel playerModel, CriminalRecordModel criminalRecordModel) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        StormDatabase.getExecutorService().submit(() -> {
            playerModel.getCriminalRecords().remove(criminalRecordModel);
            StormUtils.deleteModelData(CriminalRecordModel.class, query ->
                    query.where("id", Where.EQUAL, criminalRecordModel.getId()));
            completableFuture.complete(null);
        });
        return completableFuture;
    }
}
