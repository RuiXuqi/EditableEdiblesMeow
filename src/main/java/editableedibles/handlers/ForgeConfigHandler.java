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

	private static Object2ObjectArrayMap<Item, Int2ObjectArrayMap<FoodEffectEntry>> foodEffectMap = null;
	
	@Config.Comment("Server-Side Options")
	@Config.Name("Server Options")
	public static final ServerConfig server = new ServerConfig();

	@Config.Comment("Mod Compat Options")
	@Config.Name("Compat Options")
	public static final CompatConfig compat = new CompatConfig();

	public static class ServerConfig {

		@Config.Comment(
				"List of food items with their effects and chance to be applied when eaten \n" +
				"Format: String itemid, Int metadata (-1 for any), String potionid, Int duration, Int amplifier, Boolean showparticles, Float chance \n" +
				"Optional Additional Args, allows for modifying application rules of custom effects: Boolean additiveDuration, Int maxDuration (-1 for any), Boolean additiveAmplifier, Int maxAmplifier (-1 for any)\n" +
				"Example: minecraft:chicken, -1, minecraft:hunger, 120, 0, false, 0.25 \n" +
				"Example (Additional): minecraft:steak, -1, minecraft:strength, 30, 0, false, 1.0, true, 120, false, -1")
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

	public static class CompatConfig {

		@Config.Comment("List of food items with value to be added to intoxication and the chance to add it \n" +
				"Intoxication is between 0 and 10000, values added can be negative \n" +
				"Format: String itemid, Int metadata (-1 for any), Int intoxicationAddValue, Float chance \n" +
				"Example: minecraft:poisonous_potato, -1, -2000, 0.75")
		@Config.Name("MistyWorld Food Intoxication and Chances")
		public String[] mistyWorldFoodIntox = {

		};

		@Config.Comment("List of food items with value to be added to pollution and the chance to add it \n" +
				"Pollution is between 0 and 10000, values added can be negative \n" +
				"Format: String itemid, Int metadata (-1 for any), Int pollutionAddValue, Float chance \n" +
				"Example: minecraft:apple, -1, 1000, 0.5")
		@Config.Name("MistyWorld Food Pollution and Chances")
		public String[] mistyWorldFoodPollu = {

		};
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
		parseMistyWorldCompatArray();
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
				
				boolean additiveDuration = false;
				int maxDuration = -1;
				boolean additiveAmplifier = false;
				int maxAmplifier = -1;
				if(arr.length > 7) {
					try {
						additiveDuration = Boolean.parseBoolean(arr[7].trim());
						maxDuration = Integer.parseInt(arr[8].trim());
						additiveAmplifier = Boolean.parseBoolean(arr[9].trim());
						maxAmplifier = Integer.parseInt(arr[10].trim());
					}
					catch(Exception ignored) {}
				}

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.addEffect(new PotionEffect(potion, duration, amplifier, false, show), chance, additiveDuration, maxDuration, additiveAmplifier, maxAmplifier);
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

	private static void parseMistyWorldCompatArray() {
		for(String string : ForgeConfigHandler.compat.mistyWorldFoodIntox) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());
				int addValue = Integer.parseInt(arr[2].trim());
				float chance = Float.parseFloat(arr[3].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.setIntoxicationPair(addValue, chance);
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse MistyWorld intoxication entry: " + string);
			}
		}
		for(String string : ForgeConfigHandler.compat.mistyWorldFoodPollu) {
			try {
				String[] arr = string.split(",");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0].trim()));
				int meta = Integer.parseInt(arr[1].trim());
				int addValue = Integer.parseInt(arr[2].trim());
				float chance = Float.parseFloat(arr[3].trim());

				Int2ObjectArrayMap<FoodEffectEntry> metaMap = foodEffectMap.get(item);
				if(metaMap == null) metaMap = new Int2ObjectArrayMap<>();

				FoodEffectEntry entry = metaMap.get(meta);
				if(entry == null) entry = new FoodEffectEntry();

				entry.setPollutionPair(addValue, chance);
				metaMap.put(meta, entry);
				foodEffectMap.put(item, metaMap);
			}
			catch(Exception ex) {
				EditableEdibles.LOGGER.log(Level.WARN, "Failed to parse MistyWorld pollution entry: " + string);
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