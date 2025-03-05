package nl.openminetopia.modules.banking.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class VaultEconomyHandler implements Economy {

    private final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

    @Override
    public boolean isEnabled() {
        return OpenMinetopia.getInstance().isEnabled();
    }

    @Override
    public String getName() {
        return "OpenMinetopia";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return bankingModule.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "euro";
    }

    @Override
    public String currencyNameSingular() {
        return "€";
    }

    @Override
    public boolean hasAccount(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) return false;
        return bankingModule.getAccountById(player.getUniqueId()) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return bankingModule.getAccountById(offlinePlayer.getUniqueId()) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) return -1;
        return bankingModule.getAccountById(player.getUniqueId()).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        BankAccountModel accountModel = bankingModule.getAccountById(offlinePlayer.getUniqueId());
        if(accountModel == null) return -1;
        return accountModel.getBalance();
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return getBalance(offlinePlayer) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return getBalance(offlinePlayer) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not online or doesn't exist.");
        BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());
        if(accountModel == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        accountModel.setBalance(accountModel.getBalance() - amount);
        return new EconomyResponse(amount, accountModel.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        BankAccountModel accountModel = bankingModule.getAccountById(offlinePlayer.getUniqueId());
        if(accountModel == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        accountModel.setBalance(accountModel.getBalance() - amount);
        return new EconomyResponse(amount, accountModel.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not online or doesn't exist.");
        BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());
        if(accountModel == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        accountModel.setBalance(accountModel.getBalance() + amount);
        return new EconomyResponse(amount, accountModel.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        BankAccountModel accountModel = bankingModule.getAccountById(offlinePlayer.getUniqueId());
        if(accountModel == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account not found");
        accountModel.setBalance(accountModel.getBalance() + amount);
        return new EconomyResponse(amount, accountModel.getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
