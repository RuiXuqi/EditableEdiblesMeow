package editableedibles.mixin.foodexpansion;

import lellson.foodexpansion.items.ItemFoodBasic;
import lellson.foodexpansion.items.ItemNetherWartSoup;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(ItemNetherWartSoup.class)
public abstract class ItemNetherWartSoupMixin extends ItemFoodBasic {

    public ItemNetherWartSoupMixin(String name, int foodAmount, float saturation) {
        super(name, foodAmount, saturation);
    }

    /**
     * @author fonnymunkey
     * @reason Add compatibility, fix FoodExpansion calling client-sided isBeneficial from server-side
     */
    @Overwrite
    public ItemStack onItemUseFinish(ItemStack item, World world, EntityLivingBase player) {
        return super.onItemUseFinish(item, world, player);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if(!worldIn.isRemote) {
            Collection<PotionEffect> activeeffects = player.getActivePotionEffects();
            ArrayList<PotionEffect> effectstoremove = new ArrayList<>();
            for(PotionEffect effect : activeeffects) {
                if(effect.getPotion().isBadEffect()) effectstoremove.add(effect);
            }
            for(PotionEffect potionEffect : effectstoremove) {
                Potion potion = potionEffect.getPotion();
                player.removePotionEffect(potion);
            }
        }
        player.setFire(5);
    }
}