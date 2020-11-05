package eu.revamp.system.utilities.economy;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.plugin.RevampSystem;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class VaultAccount extends AbstractEconomy {



/*
    public double balance() {
        return this.economy.getBalance(this.name);
    }

    public boolean set(final double amount) {
        if (!this.economy.withdrawPlayer(this.name, this.balance()).transactionSuccess()) {
            return false;
        }
        if (amount == 0) {
            return true;
        }
        return this.economy.depositPlayer(this.name, amount).transactionSuccess();
    }


    public boolean add(final double amount) {
        return this.economy.depositPlayer(this.name, amount).transactionSuccess();
    }


    public boolean subtract(final double amount) {
        return this.economy.withdrawPlayer(this.name, amount).transactionSuccess();
    }


    public boolean multiply(final double amount) {
        final double balance = this.balance();
        return this.set(balance * amount);
    }


    public boolean divide(final double amount) {
        final double balance = this.balance();
        return this.set(balance / amount);
    }


    public boolean hasEnough(final double amount) {
        return this.balance() >= amount;
    }


    public boolean hasOver(final double amount) {
        return this.balance() > amount;
    }


    public boolean hasUnder(final double amount) {
        return this.balance() < amount;
    }


    public boolean isNegative() {
        return this.balance() < 0;
    }


    public boolean remove() {
        return this.set(0.0);
    }
*/
    @Override
    public boolean isEnabled() {
        return RevampSystem.INSTANCE != null;
    }

    @Override
    public String getName() {
        return "RevampEconomy";
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
    public String format(double v) {
        return String.valueOf(v);
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        PlayerData playerData = RevampSystem.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
        return playerData != null;
    }

    @Override
    public boolean hasAccount(String playerName, String world) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        PlayerData playerData = RevampSystem.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
        return playerData.getBalance();
    }

    @Override
    public double getBalance(String playerName, String playerName1) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double v) {
        return false;
    }

    @Override
    public boolean has(String playerName, String playerName1, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        PlayerData playerData = RevampSystem.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
        double newBalance = playerData.getBalance();
        if (!playerData.withdrawBalance(amount)){
            return new EconomyResponse(0.0D, newBalance, EconomyResponse.ResponseType.FAILURE, "The value is bigger than player balance");
        }
        else {
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        PlayerData playerData = RevampSystem.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());
        if (!playerData.addBalance(amount)){
            return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Value is less than 0");
        }
        else {
            return new EconomyResponse(amount, 0.0D, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String playerName1, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String playerName, String playerName1) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String playerName) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String playerName) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String playerName, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String playerName, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String playerName, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String playerName, String playerName1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String playerName, String playerName1) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String world) {
        return true;
    }
}
