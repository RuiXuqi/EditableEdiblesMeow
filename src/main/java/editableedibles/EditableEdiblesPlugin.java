package editableedibles;

import java.util.*;

import editableedibles.handlers.CompatHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.ILateMixinLoader;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-5000)
public class EditableEdiblesPlugin implements IFMLLoadingPlugin, ILateMixinLoader {

	public EditableEdiblesPlugin() {
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

	@Override
	public List<String> getMixinConfigs() {
		List<String> mixins = new ArrayList<>();
		mixins.add("mixins.editableedibles.vanilla.json");

		if (CompatHandler.isFoodExpansionLoaded()) {
			mixins.add("mixins.editableedibles.foodexpansion.json");
		}

		return mixins;
	}
}