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
        for(FoodEffectEntry.EffectEntry eff : effectEntry.getChanceEffectList()) {
            if(world.rand.nextFloat() < eff.getChance()) {
                PotionEffect effect = eff.getEffect();
                if(effect.getPotion().isInstant()) effect.getPotion().affectEntity(player, player, player, effect.getAmplifier(), 1.0D);
                else {
                    int duration = effect.getDuration();
                    int amplifier = effect.getAmplifier();
                    if(eff.getAdditiveDuration() || eff.getAdditiveAmplifier()) {
                        PotionEffect effectPrev = player.getActivePotionEffect(effect.getPotion());
                        if(effectPrev != null) {
                            if(eff.getAdditiveDuration()) {
                                duration = eff.getMaxDuration() >= 0 ? Math.min(duration + effectPrev.getDuration(), eff.getMaxDuration()) : duration + effectPrev.getDuration();
                            }
                            if(eff.getAdditiveAmplifier()) {
                                amplifier = eff.getMaxAmplifier() >= 0 ? Math.min(1 + amplifier + effectPrev.getAmplifier(), eff.getMaxAmplifier()) : 1 + amplifier + effectPrev.getAmplifier();
                            }
                        }
                    }
                    player.addPotionEffect(new PotionEffect(effect.getPotion(), duration, amplifier, effect.getIsAmbient(), effect.doesShowParticles()));
                }
            }
        }
        FoodEffectEntry.EffectEntry weightEffect = effectEntry.getWeightedEffectChoice(world.rand);
        if(weightEffect != null) {
            PotionEffect effect = weightEffect.getEffect();
            if(effect.getPotion().isInstant()) effect.getPotion().affectEntity(player, player, player, effect.getAmplifier(), 1.0D);
            else {
                int duration = effect.getDuration();
                int amplifier = effect.getAmplifier();
                if(weightEffect.getAdditiveDuration() || weightEffect.getAdditiveAmplifier()) {
                    PotionEffect effectPrev = player.getActivePotionEffect(effect.getPotion());
                    if(effectPrev != null) {
                        if(weightEffect.getAdditiveDuration()) {
                            duration = weightEffect.getMaxDuration() >= 0 ? Math.min(duration + effectPrev.getDuration(), weightEffect.getMaxDuration()) : duration + effectPrev.getDuration();
                        }
                        if(weightEffect.getAdditiveAmplifier()) {
                            amplifier = weightEffect.getMaxAmplifier() >= 0 ? Math.min(1 + amplifier + effectPrev.getAmplifier(), weightEffect.getMaxAmplifier()) : 1 + amplifier + effectPrev.getAmplifier();
                        }
                    }
                }
                player.addPotionEffect(new PotionEffect(effect.getPotion(), duration, amplifier, effect.getIsAmbient(), effect.doesShowParticles()));
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