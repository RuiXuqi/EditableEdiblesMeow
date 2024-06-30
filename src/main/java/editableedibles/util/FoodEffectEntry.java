package editableedibles.util;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.potion.PotionEffect;

public class FoodEffectEntry {

    private final Object2FloatOpenHashMap<PotionEffect> effectMap = new Object2FloatOpenHashMap<>();
    private final Object2FloatOpenHashMap<PotionEffect> cureEffectMap = new Object2FloatOpenHashMap<>();
    private final Object2FloatOpenHashMap<CureType> cureTypeMap = new Object2FloatOpenHashMap<>();
    private boolean cancelsDefault = false;
    private boolean alwaysEdible = false;

    public Object2FloatOpenHashMap<PotionEffect> getEffectMap() {
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

    public void addEffect(PotionEffect effect, float chance) {
        if(effect != null) this.effectMap.put(effect, Math.max(0.0F, Math.min(1.0F, chance)));
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

    public enum CureType {
        ALL,
        POSITIVE,
        NEGATIVE
    }
}