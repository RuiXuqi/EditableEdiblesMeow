package editableedibles.util;

import editableedibles.handlers.CompatHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FoodUtil {

    public static void handleEffectEntry(FoodEffectEntry effectEntry, World world, EntityPlayer player) {
        if(world == null || world.isRemote || player == null) return;
        for(Map.Entry<PotionEffect, Float> eff : effectEntry.getEffectMap().entrySet()) {
            if(world.rand.nextFloat() < eff.getValue()) {
                PotionEffect effect = eff.getKey();
                if(effect.getPotion().isInstant()) effect.getPotion().affectEntity(player, player, player, effect.getAmplifier(), 1.0D);
                else player.addPotionEffect(new PotionEffect(effect));
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
        CompatHandler.handleCompatEffectEntry(effectEntry, world, player);
    }
}