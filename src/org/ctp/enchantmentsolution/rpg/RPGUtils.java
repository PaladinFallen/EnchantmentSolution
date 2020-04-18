package org.ctp.enchantmentsolution.rpg;

import java.math.*;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ctp.enchantmentsolution.api.ApiEnchantmentWrapper;
import org.ctp.enchantmentsolution.enchantments.CustomEnchantment;
import org.ctp.enchantmentsolution.enchantments.CustomEnchantmentWrapper;
import org.ctp.enchantmentsolution.enchantments.RegisterEnchantments;
import org.ctp.enchantmentsolution.enchantments.helper.EnchantmentLevel;
import org.ctp.enchantmentsolution.utils.ChatUtils;
import org.ctp.enchantmentsolution.utils.Configurations;
import org.ctp.enchantmentsolution.utils.config.ConfigString;
import org.ctp.enchantmentsolution.utils.config.RPGConfiguration;

public class RPGUtils {

	private static List<RPGPlayer> PLAYERS = new ArrayList<RPGPlayer>();

	public static BigDecimal getExperienceNextLevel(int level) {
		Double base = ConfigString.RPG_BASE.getDouble();
		Double multiply = ConfigString.RPG_MULTIPLY.getDouble();
		if (base == 0 && multiply == 0) return new BigDecimal("10000000000");
		return new BigDecimal(base + (level - 1) * multiply + "", new MathContext(2, RoundingMode.HALF_UP));
	}

	public static BigInteger getPointsForLevel(int level) {
		Double levelZero = ConfigString.RPG_LEVELS_0.getDouble();
		Double base = ConfigString.RPG_LEVELS_BASE.getDouble();
		Double add = ConfigString.RPG_LEVELS_ADD.getDouble();
		Double addPower = ConfigString.RPG_LEVELS_POWER.getDouble();
		Double divisor = ConfigString.RPG_LEVELS_DIVISOR.getDouble();
		if (divisor == 0) divisor = 1.0;
		return new BigInteger((int) (levelZero + base * level + Math.pow(add * level, addPower) / divisor) + "");
	}

	public static BigInteger getPointsForEnchantment(Enchantment key, int value) {
		CustomEnchantment enchant = RegisterEnchantments.getCustomEnchantment(key);
		String namespace = "default_enchantments";
		if (enchant.getRelativeEnchantment() instanceof ApiEnchantmentWrapper) {
			JavaPlugin plugin = ((ApiEnchantmentWrapper) enchant.getRelativeEnchantment()).getPlugin();
			if (plugin == null) {
				ChatUtils.sendToConsole(Level.WARNING, "Enchantment " + enchant.getName() + " (Display Name " + enchant.getDisplayName() + ")" + " does not have a JavaPlugin set. Refusing to set config defaults.");
				return new BigInteger("0");
			}
			namespace = plugin.getName().toLowerCase();
		} else if (enchant.getRelativeEnchantment() instanceof CustomEnchantmentWrapper) namespace = "custom_enchantments";
		int pointsLevelOne = ConfigString.RPG_ENCHANTMENT_LEVELONE.getInt(namespace + "." + enchant.getName() + ".points_level_one");
		int pointsIncrease = ConfigString.RPG_ENCHANTMENT_INCREASE.getInt(namespace + "." + enchant.getName() + ".points_increase");

		BigInteger integer = new BigInteger("0");
		if (getFreeEnchantments().containsKey(key) && getFreeEnchantments().get(key) >= value) return integer;
		else
			integer = integer.add(new BigInteger(pointsLevelOne + pointsIncrease * (value - 1) + ""));
		return integer;
	}

	public static Map<Enchantment, Integer> getFreeEnchantments() {
		Map<Enchantment, Integer> free = new HashMap<Enchantment, Integer>();

		List<String> freeStrings = Configurations.getRPG().getStringList("free_enchantments");
		for(String s: freeStrings) {
			EnchantmentLevel level = new EnchantmentLevel(s);
			if (level.getEnchant() != null && level.getLevel() > 0) free.put(level.getEnchant().getRelativeEnchantment(), level.getLevel());
		}
		return free;
	}

	public static void giveExperience(Player player, EnchantmentLevel enchantment) {
		if (!ConfigString.GAMETYPES.getStringList().contains("RPG")) return;
		RPGPlayer rpg = null;
		for(RPGPlayer p: PLAYERS)
			if (p.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) {
				rpg = p;
				break;
			}
		if (rpg == null) {
			rpg = new RPGPlayer(player);
			PLAYERS.add(rpg);
		}

		rpg.addExperience(getExperience(enchantment));
	}

	private static double getExperience(EnchantmentLevel enchantment) {
		RPGConfiguration config = Configurations.getRPG();
		CustomEnchantment enchant = enchantment.getEnchant();
		String path = "enchantments.";
		String namespace = "default_enchantments";
		if (enchant.getRelativeEnchantment() instanceof ApiEnchantmentWrapper) {
			JavaPlugin plugin = ((ApiEnchantmentWrapper) enchant.getRelativeEnchantment()).getPlugin();
			if (plugin == null) {
				ChatUtils.sendToConsole(Level.WARNING, "Enchantment " + enchant.getName() + " (Display Name " + enchant.getDisplayName() + ")" + " does not have a JavaPlugin set. Refusing to set config defaults.");
				return 0;
			}
			namespace = plugin.getName().toLowerCase();
		} else if (enchant.getRelativeEnchantment() instanceof CustomEnchantmentWrapper) namespace = "custom_enchantments";
		return config.getDouble(path + namespace + "." + enchant.getName() + ".experience") * (enchantment.getEnchant().isMaxLevelOne() ? 1 : enchantment.getLevel());
	}

	public static RPGPlayer addRPGPlayer(Player player) {
		for(RPGPlayer p: PLAYERS)
			if (!p.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) return p;
		RPGPlayer p = new RPGPlayer(player);
		PLAYERS.add(p);
		return p;
	}

	public static RPGPlayer addRPGPlayer(String uuid, int level, String experience) {
		for(RPGPlayer p: PLAYERS)
			if (p.getPlayer().getUniqueId().toString().equals(uuid)) return p;
		RPGPlayer p = new RPGPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), level, experience);
		PLAYERS.add(p);
		return p;
	}

	public static RPGPlayer getPlayer(Player player) {
		for(RPGPlayer rpg: PLAYERS)
			if (rpg.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) return rpg;
		return addRPGPlayer(player);
	}

	public static boolean canEnchant(Player player, CustomEnchantment customEnchantment, int i) {
		if (!ConfigString.GAMETYPES.getStringList().contains("RPG")) return true;

		RPGPlayer rpg = getPlayer(player);
		if (rpg == null) rpg = addRPGPlayer(player);
		return rpg.hasEnchantment(customEnchantment.getRelativeEnchantment(), i);
	}

	public static List<RPGPlayer> getPlayers() {
		return PLAYERS;
	}

}
