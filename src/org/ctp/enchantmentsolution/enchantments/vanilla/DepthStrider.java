package org.ctp.enchantmentsolution.enchantments.vanilla;

import java.util.Arrays;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.ctp.enchantmentsolution.api.ItemType;
import org.ctp.enchantmentsolution.enchantments.CustomEnchantment;
import org.ctp.enchantmentsolution.enchantments.DefaultEnchantments;
import org.ctp.enchantmentsolution.enchantments.Weight;

public class DepthStrider extends CustomEnchantment{
	
	public DepthStrider() {
		setDefaultDisplayName("Depth Strider");
		setDefaultFiftyConstant(5);
		setDefaultThirtyConstant(0);
		setDefaultFiftyModifier(15);
		setDefaultThirtyModifier(10);
		setDefaultFiftyMaxConstant(25);
		setDefaultThirtyMaxConstant(15);
		setDefaultFiftyStartLevel(10);
		setDefaultThirtyStartLevel(1);
		setDefaultFiftyMaxLevel(3);
		setDefaultThirtyMaxLevel(3);
		setDefaultWeight(Weight.RARE);
	}

	@Override
	public String getName() {
		return "depth_strider";
	}

	@Override
	public Enchantment getRelativeEnchantment() {
		return Enchantment.DEPTH_STRIDER;
	}
	
	@Override
	protected List<ItemType> getEnchantmentItemTypes() {
		return Arrays.asList(ItemType.BOOTS);
	}

	@Override
	protected List<ItemType> getAnvilItemTypes() {
		return Arrays.asList(ItemType.BOOTS);
	}

	@Override
	protected List<CustomEnchantment> getConflictingEnchantments() {
		return Arrays.asList(this, DefaultEnchantments.getCustomEnchantment(Enchantment.FROST_WALKER),
				DefaultEnchantments.getCustomEnchantment(DefaultEnchantments.MAGMA_WALKER),
				DefaultEnchantments.getCustomEnchantment(DefaultEnchantments.VOID_WALKER));
	}

	@Override
	public String getDescription() {
		return "Increases underwater movement speed.";
	}

}
