package editableedibles.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import editableedibles.handlers.ForgeConfigHandler;
import editableedibles.util.FoodEffectEntry;
import editableedibles.util.FoodUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFood.class)
public abstract class ItemFoodMixin {

    @ModifyExpressionValue(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z")
    )
    private boolean editableedibles_vanillaItemFood_onItemRightClick_canEat(boolean original, World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if(ForgeConfigHandler.server.overrideAlwaysEdible) return true;
        if(worldIn != null && playerIn != null && handIn != null) {
            ItemStack foodStack = playerIn.getHeldItem(handIn);
            if(!foodStack.isEmpty()) {
                Int2ObjectArrayMap<FoodEffectEntry> metaMap = ForgeConfigHandler.getFoodEffectMap().get(foodStack.getItem());
                if(metaMap != null) {
                    FoodEffectEntry anyEntry = metaMap.get(-1);
                    FoodEffectEntry metaEntry = metaMap.get(foodStack.getMetadata());
                    if(anyEntry != null) original |= anyEntry.getAlwaysEdible();
                    if(metaEntry != null) original |= metaEntry.getAlwaysEdible();
                }
            }
        }
        return original;
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