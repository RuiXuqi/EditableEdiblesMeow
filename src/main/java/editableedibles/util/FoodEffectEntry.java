package editableedibles.util;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.potion.PotionEffect;

public class FoodEffectEntry {

    private final Object2ObjectOpenHashMap<PotionEffect, EffectEntry> effectMap = new Object2ObjectOpenHashMap<>();
    private final Object2FloatOpenHashMap<PotionEffect> cureEffectMap = new Object2FloatOpenHashMap<>();
    private final Object2FloatOpenHashMap<CureType> cureTypeMap = new Object2FloatOpenHashMap<>();
    private boolean cancelsDefault = false;
    private boolean alwaysEdible = false;

    //MistyWorlds compat
    private Pair<Integer, Float> intoxicationPair = null;

    //MistyWorlds compat
    private Pair<Integer, Float> pollutionPair = null;

    public Object2ObjectOpenHashMap<PotionEffect, EffectEntry> getEffectMap() {
        return this.effectMap;
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
        if(effect != null) this.effectMap.put(effect, new EffectEntry(Math.max(0.0F, Math.min(1.0F, chance)), additiveDuration, maxDuration, additiveAmplifier, maxAmplifier));
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
        
        private final float chance;
        private final boolean additiveDuration;
        private final int maxDuration;
        private final boolean additiveAmplifier;
        private final int maxAmplifier;
        
        public EffectEntry(float chance, boolean additiveDuration, int maxDuration, boolean additiveAmplifier, int maxAmplifier) {
            this.chance = chance;
            this.additiveDuration = additiveDuration;
            this.maxDuration = maxDuration;
            this.additiveAmplifier = additiveAmplifier;
            this.maxAmplifier = maxAmplifier;
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