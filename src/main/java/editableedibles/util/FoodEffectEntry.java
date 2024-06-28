package editableedibles.util;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.potion.PotionEffect;

public class FoodEffectEntry {

    private final Object2FloatOpenHashMap<PotionEffect> effectMap = new Object2FloatOpenHashMap<>();
    private boolean cancelsDefault = false;

    public Object2FloatOpenHashMap<PotionEffect> getEffectMap() {
        return this.effectMap;
    }

    public boolean getCancelsDefault() {
        return this.cancelsDefault;
    }

    public void addEffect(PotionEffect effect, float chance) {
        if(effect != null) this.effectMap.put(effect, Math.max(0.0F, Math.min(1.0F, chance)));
    }

    public void setCancelsDefault(boolean val) {
        this.cancelsDefault = val;
    }
}