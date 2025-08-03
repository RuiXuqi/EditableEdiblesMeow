package editableedibles;

import editableedibles.handlers.CompatHandler;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class EditableEdiblesPluginLate implements  ILateMixinLoader {

	@Override
	public List<String> getMixinConfigs() {
		return CompatHandler.isFoodExpansionLoaded() ? Collections.singletonList("mixins.editableedibles.foodexpansion.json") : Collections.emptyList();
	}
}