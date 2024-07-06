package editableedibles.mixin.vanilla;

import editableedibles.handlers.ForgeConfigHandler;
import editableedibles.util.FoodEffectEntry;
import editableedibles.util.FoodUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFood.class)
public abstract class ItemFoodMixin {

    @Unique
    private ItemStack rlmixins$curStack = null;

    @Inject(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z", shift = At.Shift.BEFORE)
    )
    public void editableedibles_vanillaItemFood_onItemRightClick_inject(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        this.rlmixins$curStack = playerIn.getHeldItem(handIn);
    }

    @Redirect(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z")
    )
    public boolean editableedibles_vanillaItemFood_onItemRightClick_redirect(EntityPlayer instance, boolean ignoreHunger) {
        if(ForgeConfigHandler.server.overrideAlwaysEdible) ignoreHunger = true;
        else if(this.rlmixins$curStack != null) {
            Int2ObjectArrayMap<FoodEffectEntry> metaMap = ForgeConfigHandler.getFoodEffectMap().get(this.rlmixins$curStack.getItem());
            if(metaMap != null) {
                FoodEffectEntry anyEntry = metaMap.get(-1);
                FoodEffectEntry metaEntry = metaMap.get(this.rlmixins$curStack.getMetadata());
                if(anyEntry != null) ignoreHunger |= anyEntry.getAlwaysEdible();
                if(metaEntry != null) ignoreHunger |= metaEntry.getAlwaysEdible();
            }
        }
        this.rlmixins$curStack = null;
        return instance.canEat(ignoreHunger);
    }

    @Redirect(
            method = "onItemUseFinish",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemFood;onFoodEaten(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V")
    )
    public void editableedibles_vanillaItemFood_onItemUseFinish(ItemFood instance, ItemStack stack, World worldIn, EntityPlayer player) {
        Int2ObjectArrayMap<FoodEffectEntry> metaMap = ForgeConfigHandler.getFoodEffectMap().get(stack.getItem());
        boolean overrideEat = false;
        if(metaMap != null) {
            FoodEffectEntry anyEntry = metaMap.get(-1);
            FoodEffectEntry metaEntry = metaMap.get(stack.getMetadata());

            if(anyEntry != null) {
                FoodUtil.handleEffectEntry(anyEntry, worldIn, player);
                overrideEat |= anyEntry.getCancelsDefault();
            }

            if(metaEntry != null) {
                FoodUtil.handleEffectEntry(metaEntry, worldIn, player);
                overrideEat |= metaEntry.getCancelsDefault();
            }
        }
        if(!overrideEat) ((ItemFoodInvoker)instance).onFoodEaten(stack, worldIn, player);
    }
}