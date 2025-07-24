package editableedibles.util;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodEffectEntry {

    private final List<EffectEntry> chanceEffectList = new ArrayList<>();
    private final List<EffectEntry> weightEffectList = new ArrayList<>();
    private float totalWeight = 0;
    private final Object2FloatOpenHashMap<PotionEffect> cureEffectMap = new Object2FloatOpenHashMap<>();
    private final Object2FloatOpenHashMap<CureType> cureTypeMap = new Object2FloatOpenHashMap<>();
    private boolean cancelsDefault = false;
    private boolean alwaysEdible = false;

    //MistyWorlds compat
    private Pair<Integer, Float> intoxicationPair = null;

    //MistyWorlds compat
    private Pair<Integer, Float> pollutionPair = null;

    public List<EffectEntry> getChanceEffectList() {
        return this.chanceEffectList;
    }
    
    @Nullable
    public EffectEntry getWeightedEffectChoice(Random rand) {
        float target = this.totalWeight * rand.nextFloat();
        float current = 0.0F;
        for(EffectEntry entry : this.weightEffectList) {
            current += entry.getChance();
            if(current >= target) return entry;
        }
        return null;
    }

    public Object2FloatOpenHashMap<PotionEffect> getCureEffectMap() {
        return this.cureEffectMap;
    }

    public Object2FloatOpenHashMap<CureType> getCureTypeMap() {
        return this.cureTypeMap;
    }

    public boolean getCancelsDefault() {
        return this.cancelsDefault;
    }

    public boolean getAlwaysEdible() {
        return this.alwaysEdible;
    }

    //MistyWorlds compat
    public Pair<Integer, Float> getIntoxicationPair() {
        return this.intoxicationPair;
    }

    //MistyWorlds compat
    public Pair<Integer, Float> getPollutionPair() {
        return this.pollutionPair;
    }

    public void addEffect(PotionEffect effect, float chance, boolean additiveDuration, int maxDuration, boolean additiveAmplifier, int maxAmplifier) {
        if(effect != null) {
            if(chance <= 1.0) this.chanceEffectList.add(new EffectEntry(effect, Math.max(0.0F, Math.min(1.0F, chance)), additiveDuration, maxDuration, additiveAmplifier, maxAmplifier));
            else {
                this.weightEffectList.add(new EffectEntry(effect, chance, additiveDuration, maxDuration, additiveAmplifier, maxAmplifier));
                this.totalWeight += chance;
            }
        }
    }

    public void addCureEffect(PotionEffect effect, float chance) {
        if(effect != null) this.cureEffectMap.put(effect, Math.max(0.0F, Math.min(1.0F, chance)));
    }

    public void addCureType(CureType cureType, float chance) {
        if(cureType != null) this.cureTypeMap.put(cureType, Math.max(0.0F, Math.min(1.0F, chance)));
    }

    public void setCancelsDefault(boolean val) {
        this.cancelsDefault = val;
    }

    public void setAlwaysEdible() {
        this.alwaysEdible = true;
    }

    //MistyWorlds compat
    public void setIntoxicationPair(Integer left, Float right) {
        this.intoxicationPair = new Pair<>(left, right);
    }

    //MistyWorlds compat
    public void setPollutionPair(Integer left, Float right) {
        this.pollutionPair = new Pair<>(left, right);
    }

    public enum CureType {
        ALL,
        POSITIVE,
        NEGATIVE
    }
    
    public static class EffectEntry {
        
        private final PotionEffect effect;
        private final float chance;
        private final boolean additiveDuration;
        private final int maxDuration;
        private final boolean additiveAmplifier;
        private final int maxAmplifier;
        
        public EffectEntry(PotionEffect effect, float chance, boolean additiveDuration, int maxDuration, boolean additiveAmplifier, int maxAmplifier) {
            this.effect = effect;
            this.chance = chance;
            this.additiveDuration = additiveDuration;
            this.maxDuration = maxDuration;
            this.additiveAmplifier = additiveAmplifier;
            this.maxAmplifier = maxAmplifier;
        }
        
        public PotionEffect getEffect() {
            return this.effect;
        }
        
        public float getChance() {
            return this.chance;
        }
        
        public boolean getAdditiveDuration() {
            return this.additiveDuration;
        }
        
        public int getMaxDuration() {
            return this.maxDuration;
        }
        
        public boolean getAdditiveAmplifier() {
            return this.additiveAmplifier;
        }
        
        public int getMaxAmplifier() {
            return this.maxAmplifier;
        }
    }
}