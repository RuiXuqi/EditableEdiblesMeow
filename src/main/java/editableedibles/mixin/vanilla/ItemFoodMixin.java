package editableedibles.mixin.vanilla;

import editableedibles.handlers.ForgeConfigHandler;
import editableedibles.util.FoodEffectEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ItemFood.class)
public abstract class ItemFoodMixin {

    @Redirect(
            method = "onItemUseFinish",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemFood;onFoodEaten(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V")
    )
    public void rlmixins_vanillaItemFood_onItemUseFinish(ItemFood instance, ItemStack stack, World worldIn, EntityPlayer player) {
        Int2ObjectArrayMap<FoodEffectEntry> metaMap = ForgeConfigHandler.getFoodEffectMap().get(stack.getItem());
        boolean overrideEat = false;
        if(metaMap != null) {
            FoodEffectEntry anyEntry = metaMap.get(-1);
            FoodEffectEntry metaEntry = metaMap.get(stack.getMetadata());

            if(anyEntry != null) {
                for(Map.Entry<PotionEffect, Float> eff : anyEntry.getEffectMap().entrySet()) {
                    if(!worldIn.isRemote && worldIn.rand.nextFloat() < eff.getValue()) {
                        player.addPotionEffect(new PotionEffect(eff.getKey()));
                    }
                }
                overrideEat |= anyEntry.getCancelsDefault();
            }

            if(metaEntry != null) {
                for(Map.Entry<PotionEffect, Float> eff : metaEntry.getEffectMap().entrySet()) {
                    if(!worldIn.isRemote && worldIn.rand.nextFloat() < eff.getValue()) {
                        player.addPotionEffect(new PotionEffect(eff.getKey()));
                    }
                }
                overrideEat |= metaEntry.getCancelsDefault();
            }
        }
        if(!overrideEat) ((ItemFoodInvoker)instance).onFoodEaten(stack, worldIn, player);
    }
}