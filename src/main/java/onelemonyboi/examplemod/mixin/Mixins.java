package onelemonyboi.examplemod.mixin;

import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

public class Mixins {
	@Mixin(Entity.class)
	public static class Mixin1 {
		/**
		 * @author OneLemonyBoi
		 */
		@Overwrite
		public boolean isOnSameTeam(Entity entityIn) {
			return false;
		}
	}
}
