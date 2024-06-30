package editableedibles.handlers;

import editableedibles.EditableEdibles;
import editableedibles.util.FoodEffectEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

@Config(modid = EditableEdibles.MODID)
public class ForgeConfigHandler {

	private static Object2ObjectArrayMap<Item, Int2ObjectArrayMap<FoodEffectEntry>> foodEffectMap;
	
	@Config.Comment("Server-Side Options")
	@Config.Name("Server Options")
	public static final ServerConfig server = new ServerConfig();

	public static class ServerConfig {

		@Config.Comment(
				"List of food items with their effects and chance to be applied when eaten \n" +
				"Format: String itemid, Int metadata (-1 for any), String potionid, Int duration, Int amplifier, Boolean showparticles, Float chance \n" +
				"Example: minecraft:chicken, -1, minecraft:hunger, 120, 0, false, 0.25")
		@Config.Name("Food Effects and Chances")
		public String[] foodEffectArray = {

		};

		@Config.Comment("List of food items and if their default onFoodEaten handling should be cancelled \n" +
				"Format: String itemid, Int metadata (-1 for any), Boolean shouldcancel \n" +
				"Example: minecraft:chicken, -1, true")
		@Config.Name("Food Default Effect Override")
		public String[] foodCancelArray = {

		};

		@Config.Comment("List of food items with an effect to be cured and the chance to cure it \n" +
				"Format: String itemid, Int metadata (-1 for any), String potionid, Int maxDuration (-1 for any), Int maxAmplifier (-1 for any), Float chance \n" +
				"Example: minecraft:carrot, -1, minecraft:blindness, -1, -1, 1.0")
		@Config.Name("Food Cure Effects and Chances")
		public String[] foodCureEffectArray = {

		};

		@Config.Comment("List of food items with overall cure type to be cured and the chance to cure it \n" +
				"Format: String itemid, Int metadata (-1 for any), CureType cureType (ALL, POSITIVE, NEGATIVE), Float chance \n" +
				"Example: minecraft:poisonous_potato, -1, POSITIVE, 0.25")
		@Config.Name("Food Cure Types and Chances")
		public String[] foodCureTypeArray = {

		};

		@Config.Comment("List of food items that should always be edible \n" +
				"Format: String itemid, Int metadata (-1 for any) \n" +
				"Example: minecraft:apple, -1")
		@Config.Name("Always Edible Food Items")
		public String[] alwaysEdibleArray = {

		};

		@Config.Comment("If food items should always be edible, overrides the list")
		@Config.Name("Override Always Edible")
		public boolean overrideAlwaysEdible = false;
	}

	public static Object2ObjectArrayMap<Item, Int2ObjectArrayMap<FoodEffectEntry>> getFoodEffectMap() {
		if(ForgeConfigHandler.foodEffectMap == null) parseItemEntries();
		return ForgeConfigHandler.foodEffectMap;
	}

	private static void parseItemEntries() {
		foodEffectMap = new Object2ObjectArrayMap<>();
		parseFoodEffectArray();
		parseFoodCancelArray();
		parseFoodCureEffectArray();
		parseFoodCureTypeArray();
		parseAlwaysEdiblerray();
	}

	private static void parseFoodEffectArray() {
		for(String string : ForgeConfigHandler.server.foodEffectArray) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());
				Potion potion = Potion.getPotionFromResourceLocation(arr[2].trim());
				if(potion == null) {
					EditableEdibles.LOGGER.log(Level.WARN, "Defined potion effect not found: " + arr[2].trim() + ", skipping");
					continue;
				}
				int duration = Integer.parseInt(arr[3].trim());
				int amplifier = Integer.parseInt(arr[4].trim());
				boolean show = Boolean.parseBoolean(arr[5].trim());
				float chance = Float.parseFloat(arr[6].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.addEffect(new PotionEffect(potion, duration, amplifier, false, show), chance);
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse food effect entry: " + string);
			}
		}
	}

	private static void parseFoodCancelArray() {
		for(String string : ForgeConfigHandler.server.foodCancelArray) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());
				boolean cancel = Boolean.parseBoolean(arr[2].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.setCancelsDefault(cancel);
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse food default effect override entry: " + string);
			}
		}
	}

	private static void parseFoodCureEffectArray() {
		for(String string : ForgeConfigHandler.server.foodCureEffectArray) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());
				Potion potion = Potion.getPotionFromResourceLocation(arr[2].trim());
				if(potion == null) {
					EditableEdibles.LOGGER.log(Level.WARN, "Defined potion effect not found: " + arr[2].trim() + ", skipping");
					continue;
				}
				int duration = Integer.parseInt(arr[3].trim());
				int amplifier = Integer.parseInt(arr[4].trim());
				float chance = Float.parseFloat(arr[5].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.addCureEffect(new PotionEffect(potion, duration, amplifier), chance);
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse food cure effect entry: " + string);
			}
		}
	}

	private static void parseFoodCureTypeArray() {
		for(String string : ForgeConfigHandler.server.foodCureTypeArray) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());
				FoodEffectEntry.CureType cureType = FoodEffectEntry.CureType.valueOf(arr[2].trim());
				float chance = Float.parseFloat(arr[3].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.addCureType(cureType, chance);
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse food cure type entry: " + string);
			}
		}
	}

	private static void parseAlwaysEdiblerray() {
		for(String string : ForgeConfigHandler.server.alwaysEdibleArray) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.setAlwaysEdible();
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse always edible entry: " + string);
			}
		}
	}

	@Mod.EventBusSubscriber(modid = EditableEdibles.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EditableEdibles.MODID)) {
				ForgeConfigHandler.foodEffectMap = null;
				ConfigManager.sync(EditableEdibles.MODID, Config.Type.INSTANCE);
			}
		}
	}
}