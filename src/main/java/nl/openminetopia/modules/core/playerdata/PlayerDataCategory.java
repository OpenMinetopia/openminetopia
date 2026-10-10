package nl.openminetopia.modules.core.playerdata;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.modules.prefix.PrefixModule;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum PlayerDataCategory {

    COLORS(ColorModule.class) {
        @Override
        public void reset(MinetopiaPlayer player) {
            new ArrayList<>(player.getColors()).forEach(player::removeColor);
            for (OwnableColorType type : OwnableColorType.values()) {
                player.setActiveColor(type.defaultColor(), type);
            }
        }
    },
    PREFIXES(PrefixModule.class) {
        @Override
        public void reset(MinetopiaPlayer player) {
            new ArrayList<>(player.getPrefixes()).forEach(player::removePrefix);
            player.setActivePrefix(new Prefix(-1, OpenMinetopia.getDefaultConfiguration().getDefaultPrefix(), -1));
        }
    },
    BALANCE(BankingModule.class) {
        @Override
        public void reset(MinetopiaPlayer player) {
            OpenMinetopia.getModuleManager().get(BankingModule.class).getAccountByIdAsync(player.getUuid()).thenAccept(account -> {
                if (account == null) return;
                account.setBalance(0.0);
                account.save();
            });
        }
    },
    FITNESS(FitnessModule.class) {
        @Override
        public void reset(MinetopiaPlayer player) {
            if (player.getFitness() != null) player.getFitness().reset();
        }
    },
    LEVEL(null) {
        @Override
        public void reset(MinetopiaPlayer player) {
            player.setLevel(1);
        }
    },
    PLAYTIME(null) {
        @Override
        public void reset(MinetopiaPlayer player) {
            player.setPlaytime(0);
        }
    },
    CRIMINALRECORDS(PoliceModule.class) {
        @Override
        public void reset(MinetopiaPlayer player) {
            new ArrayList<>(player.getCriminalRecords()).forEach(player::removeCriminalRecord);
        }
    };

    private final Class<? extends ExtendedSpigotModule> module;

    public abstract void reset(MinetopiaPlayer player);

    public String key() {
        return name().toLowerCase();
    }

    public boolean isAvailable() {
        return module == null || !OpenMinetopia.getDefaultConfiguration().isModuleDisabled(module);
    }

    public static List<PlayerDataCategory> available() {
        return Arrays.stream(values()).filter(PlayerDataCategory::isAvailable).toList();
    }

    public static PlayerDataCategory byKey(String key) {
        return Arrays.stream(values()).filter(category -> category.key().equalsIgnoreCase(key)).findFirst().orElse(null);
    }
}
