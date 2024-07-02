package editableedibles;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = EditableEdibles.MODID, version = EditableEdibles.VERSION, name = EditableEdibles.NAME, dependencies = "required-after:fermiumbooter", acceptableRemoteVersions = "*")
public class EditableEdibles {
    public static final String MODID = "editableedibles";
    public static final String VERSION = "1.2.0";
    public static final String NAME = "EditableEdibles";
    public static final Logger LOGGER = LogManager.getLogger();
	
	@Instance(MODID)
	public static EditableEdibles instance;
}