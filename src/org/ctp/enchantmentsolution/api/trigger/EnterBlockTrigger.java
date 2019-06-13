package org.ctp.enchantmentsolution.api.trigger;

import com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.ctp.enchantmentsolution.api.shared.BlockObject;

/**
 * Fires whenever a hitbox intersects with a block, including air, as well as the block the an ender pearl had landed at. This trigger fires constantly.
 */

public class EnterBlockTrigger extends Trigger {
	private @Nullable BlockObject block = null;
	
	public EnterBlockTrigger() {
		super(Type.ENTER_BLOCK);
	}
	
	
	
	/**
	 * @return information about the block the player is intersecting with or null, if none was specified
	 */
	public @Nullable BlockObject getBlock() {
		return block;
	}
	
	/**
	 * @param block information about the block the player is intersecting with or null, if it should be cleared
	 * @return the current trigger for chaining
	 */
	public EnterBlockTrigger setBlock(@Nullable BlockObject block) {
		this.block = block;
		return this;
	}
	
	
	
	@Override
	protected JsonObject getConditions() {
		return block == null ? null : block.toJson();
	}
}
