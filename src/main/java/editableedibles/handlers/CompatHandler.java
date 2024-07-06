package editableedibles.handlers;

import editableedibles.compat.MistyWorldCompat;
import editableedibles.util.FoodEffectEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CompatHandler {

    private static Boolean isMistyWorldLoaded = null;
    private static Boolean isFoodExpansionLoaded = null;

    public static boolean isMistyWorldLoaded() {
        if(isMistyWorldLoaded == null) isMistyWorldLoaded = Loader.isModLoaded("mist");
        return isMistyWorldLoaded;
    }

    public static boolean isFoodExpansionLoaded() {

        if(isFoodExpansionLoaded == null) isFoodExpansionLoaded = Loader.isModLoaded("foodexpansion");
        return isFoodExpansionLoaded;
    }

    public static void handleCompatEffectEntry(FoodEffectEntry effectEntry, World world, EntityPlayer player) {
        if(isMistyWorldLoaded()) MistyWorldCompat.handleCompatEffectEntry(effectEntry, world, player);
    }
}