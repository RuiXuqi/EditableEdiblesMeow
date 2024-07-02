package editableedibles.compat;

import editableedibles.util.FoodEffectEntry;
import editableedibles.util.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import ru.liahim.mist.capability.handler.IMistCapaHandler;

public class MistyWorldCompat {

    public static void handleCompatEffectEntry(FoodEffectEntry effectEntry, World world, EntityPlayer player) {
        IMistCapaHandler capa = IMistCapaHandler.getHandler(player);
        if(capa == null) return;
        Pair<Integer, Float> intoxPair = effectEntry.getIntoxicationPair();
        Pair<Integer, Float> polluPair = effectEntry.getPollutionPair();

        if(intoxPair != null && world.rand.nextFloat() < intoxPair.getRight()) {
            capa.addToxic(intoxPair.getLeft());
        }
        if(polluPair != null && world.rand.nextFloat() < polluPair.getRight()) {
            capa.addPollution(polluPair.getLeft());
        }
    }
}