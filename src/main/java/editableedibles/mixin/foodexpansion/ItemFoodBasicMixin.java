package editableedibles.mixin.foodexpansion;

import lellson.foodexpansion.items.ItemFoodBasic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemFoodBasic.class)
public abstract class ItemFoodBasicMixin extends ItemFood {

    @Shadow(remap = false)
    private ItemStack returnItem;

    @Shadow(remap = false)
    private PotionEffect[] effects;

    @Shadow(remap = false)
    private int probability;

    public ItemFoodBasicMixin(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    /**
     * @author fonnymunkey
     * @reason Add compatibility, fix bug with eating stacked food deleting the stack
     */
    @Overwrite
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        ItemStack returnable = super.onItemUseFinish(stack, world, entity);

        if(this.returnItem != null) {
            if(entity instanceof EntityPlayer) {
                ((EntityPlayer)entity).inventory.addItemStackToInventory(new ItemStack(this.returnItem.getItem(), 1));
            }
            else entity.dropItem(this.returnItem.getItem(), 1);
        }

        return returnable;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if(!world.isRemote && this.effects != null) {
            PotionEffect[] var8 = this.effects;
            for(PotionEffect effect : var8) {
                if(this.probability > 0 && world.rand.nextInt(this.probability) == 0) {
                    player.addPotionEffect(new PotionEffect(effect));
                }
            }
        }
    }
}