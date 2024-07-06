package editableedibles;

import java.util.Map;

import editableedibles.handlers.CompatHandler;
import fermiumbooter.FermiumRegistryAPI;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-5000)
public class EditableEdiblesPlugin implements IFMLLoadingPlugin {

	public EditableEdiblesPlugin() {
		MixinBootstrap.init();
		FermiumRegistryAPI.enqueueMixin(false, "mixins.editableedibles.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.editableedibles.foodexpansion.json", CompatHandler::isFoodExpansionLoaded);
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
}