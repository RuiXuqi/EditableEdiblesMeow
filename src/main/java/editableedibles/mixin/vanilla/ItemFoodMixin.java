package editableedibles.mixin.vanilla;

import editableedibles.handlers.ForgeConfigHandler;
import editableedibles.util.FoodEffectEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ItemFood.class)
public abstract class ItemFoodMixin {

    @Unique
    private ItemStack rlmixins$curStack = null;

    @Inject(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z", shift = At.Shift.BEFORE)
    )
    public void rlmixins_vanillaItemFood_onItemRightClick_inject(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        this.rlmixins$curStack = playerIn.getHeldItem(handIn);
    }

    @Redirect(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z")
    )
    public boolean rlmixins_vanillaItemFood_onItemRightClick_redirect(EntityPlayer instance, boolean ignoreHunger) {
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
    public void rlmixins_vanillaItemFood_onItemUseFinish(ItemFood instance, ItemStack stack, World worldIn, EntityPlayer player) {
        Int2ObjectArrayMap<FoodEffectEntry> metaMap = ForgeConfigHandler.getFoodEffectMap().get(stack.getItem());
        boolean overrideEat = false;
        if(metaMap != null) {
            FoodEffectEntry anyEntry = metaMap.get(-1);
            FoodEffectEntry metaEntry = metaMap.get(stack.getMetadata());

            if(anyEntry != null) {
                editableEdibles$handleEffectEntry(anyEntry, worldIn, player);
                overrideEat |= anyEntry.getCancelsDefault();
            }

            if(metaEntry != null) {
                editableEdibles$handleEffectEntry(metaEntry, worldIn, player);
                overrideEat |= metaEntry.getCancelsDefault();
            }
        }
        if(!overrideEat) ((ItemFoodInvoker)instance).onFoodEaten(stack, worldIn, player);
    }

    @Unique
    private static void editableEdibles$handleEffectEntry(FoodEffectEntry effectEntry, World world, EntityPlayer player) {
        if(world == null || world.isRemote || player == null) return;
        for(Map.Entry<PotionEffect, Float> eff : effectEntry.getEffectMap().entrySet()) {
            if(world.rand.nextFloat() < eff.getValue()) {
                player.addPotionEffect(new PotionEffect(eff.getKey()));
            }
        }
        for(Map.Entry<PotionEffect, Float> cure : effectEntry.getCureEffectMap().entrySet()) {
            if(world.rand.nextFloat() < cure.getValue()) {
                PotionEffect active = player.getActivePotionEffect(cure.getKey().getPotion());
                if(active != null) {
                    int maxAmp = cure.getKey().getAmplifier();
                    int maxDur = cure.getKey().getDuration();
                    if((maxAmp == -1 || maxAmp >= active.getAmplifier()) && (maxDur == -1 || maxDur >= active.getDuration())) {
                        player.removePotionEffect(cure.getKey().getPotion());
                    }
                }
            }
        }
        for(Map.Entry<FoodEffectEntry.CureType, Float> type : effectEntry.getCureTypeMap().entrySet()) {
            if(world.rand.nextFloat() < type.getValue()) {
                List<PotionEffect> toRemove = new ArrayList<>();
                switch(type.getKey()) {
                    case ALL:
                        player.clearActivePotions();
                        break;
                    case POSITIVE:
                        for(PotionEffect eff : player.getActivePotionEffects()) {
                            if(!eff.getPotion().isBadEffect()) toRemove.add(eff);
                        }
                        break;
                    case NEGATIVE:
                        for(PotionEffect eff : player.getActivePotionEffects()) {
                            if(eff.getPotion().isBadEffect()) toRemove.add(eff);
                        }
                        break;
                }
                for(PotionEffect eff : toRemove) {
                    player.removePotionEffect(eff.getPotion());
                }
            }
        }
    }
}