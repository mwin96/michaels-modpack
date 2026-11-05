package n643064.zombie_tactics;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashSet;
import java.util.List;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.BooleanValue AFFECT_PIGLINS = BUILDER.comment("Should the changes be applied to zombified piglins").define("affectZombifiedPiglins", true);
    private static final ForgeConfigSpec.BooleanValue TARGET_ANIMALS = BUILDER.comment("Should zombies target animals").define("zombiesTargetAnimals", false);
    private static final ForgeConfigSpec.IntValue TARGET_ANIMALS_PRIORITY = BUILDER.comment("Animal targeting priority (lower values mean higher priority)").defineInRange("targetAnimalsPriority", 3, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue TARGET_ANIMALS_VISIBILITY = BUILDER.comment("Does animal targeting require line of sight").define("targetAnimalsVisibilityCheck", false);
    private static final ForgeConfigSpec.BooleanValue MINE_BLOCKS = BUILDER.comment("Should zombies attempt to break blocks in their way").define("zombiesMineBlocks", true);
    private static final ForgeConfigSpec.DoubleValue MIN_DISTANCE = BUILDER.comment("Min distance to target entity for mining").defineInRange("minDistForMining", 0.2, 0, Double.MAX_VALUE);
    private static final ForgeConfigSpec.DoubleValue MAX_DISTANCE = BUILDER.comment("Max distance to target entity for mining").defineInRange("maxDistForMining", 32, 0, Double.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue DROP_BROKEN_BLOCKS = BUILDER.comment("Should broken blocks be dropped").define("dropBrokenBlocks", true);
    private static final ForgeConfigSpec.IntValue MINING_PRIORITY = BUILDER.comment("Block breaking goal priority").defineInRange("minePriority", 1, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue ZOMBIE_CLIMBING = BUILDER.comment("Should zombies climb each other on collision").define("zombiesClimb", true);
    private static final ForgeConfigSpec.DoubleValue CLIMBING_SPEED = BUILDER.comment("Zombie climbing speed").defineInRange("zombieClimbingSpeed", 0.3, 0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MINING_INCREMENT = BUILDER.comment("The amount of mining progress made per tick").defineInRange("miningIncrement", 0.1, 0, Double.MAX_VALUE);
    private static final ForgeConfigSpec.DoubleValue MAX_HARDNESS = BUILDER.comment("The maximal hardness of targeted blocks").defineInRange("maxHardness", 2, 0, Double.MAX_VALUE);
    private static final ForgeConfigSpec.DoubleValue HARDNESS_MULTIPLIER = BUILDER.comment("Target block hardness multiplier, doesn't affect block selection").defineInRange("hardnessMultiplier", 5, 0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.BooleanValue ENABLE_MARKERS = BUILDER.comment("Should markers spawn and be targeted").define("enableMarkers", true);
    private static final ForgeConfigSpec.IntValue MARKER_TIME = BUILDER.comment("The amount of seconds a marker exists for").defineInRange("markerLifeTime", 8, 0, Integer.MAX_VALUE / 20);
    private static final ForgeConfigSpec.DoubleValue MARKER_SPAWN_CHANCE = BUILDER.comment("Marker spawn chance").defineInRange("markerSpawnChance", 0.2, 0, 1);
    private static final ForgeConfigSpec.DoubleValue MARKER_RANGE = BUILDER.comment("Range of markers").defineInRange("markerRange", 24, 0.1, Double.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue MARKERS_DAMAGE = BUILDER.comment("Should markers spawn upon item use").define("markersSpawnOnDamage", true);
    private static final ForgeConfigSpec.BooleanValue MARKERS_ITEM = BUILDER.comment("Should markers spawn when taking damage").define("markersSpawnOnItem", true);
    private static final ForgeConfigSpec.BooleanValue MARKERS_PROJECTILE = BUILDER.comment("Should markers spawn when a projectile hits").define("markersSpawnOnProjectile", true);
    private static final ForgeConfigSpec.IntValue MARKER_PATH_PRIORITY = BUILDER.comment("The priority for moving towards markers").defineInRange("markerPathingPriority", 2, 0, Integer.MAX_VALUE / 20);


    @SuppressWarnings("deprecation")
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MARKER_SPAWNING_ENTITIES = BUILDER.comment("Entities which should spawn markers").defineListAllowEmpty("markerSpawningEntities",
            List.of(
                    "minecraft:player",
                    "minecraft:sheep",
                    "minecraft:chicken",
                    "minecraft:cow",
                    "minecraft:pig",
                    "minecraft:villager"
            ), string -> string instanceof String);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean affectPiglins, mineBlocks, targetAnimals, targetAnimalsVisibility, zombiesClimbing, dropBlocks, enableMarkers, markersFromDamage, markersFromItemUse, markersFromProjectiles;
    public static double increment, maxHardness, hardnessMult, climbingSpeed, minDist, maxDist, markerSpawnChance, markerRange;
    public static int targetAnimalsPriority, miningPriority, markerLifeTime, markerPathingPriority;

    public static final HashSet<EntityType<? extends Entity>> markerSpawningEntities = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        affectPiglins = AFFECT_PIGLINS.get();
        mineBlocks = MINE_BLOCKS.get();
        minDist = MIN_DISTANCE.get();
        maxDist = MAX_DISTANCE.get();
        dropBlocks = DROP_BROKEN_BLOCKS.get();
        miningPriority = MINING_PRIORITY.get();
        targetAnimals = TARGET_ANIMALS.get();
        targetAnimalsPriority = TARGET_ANIMALS_PRIORITY.get();
        targetAnimalsVisibility = TARGET_ANIMALS_VISIBILITY.get();
        increment = MINING_INCREMENT.get();
        maxHardness = MAX_HARDNESS.get();
        hardnessMult = HARDNESS_MULTIPLIER.get();
        zombiesClimbing = ZOMBIE_CLIMBING.get();
        climbingSpeed = CLIMBING_SPEED.get();
        enableMarkers = ENABLE_MARKERS.get();
        markerLifeTime = MARKER_TIME.get();
        markerSpawnChance = MARKER_SPAWN_CHANCE.get();
        markerRange = MARKER_RANGE.get();
        markersFromDamage = MARKERS_DAMAGE.get();
        markersFromItemUse = MARKERS_ITEM.get();
        markerPathingPriority = MARKER_PATH_PRIORITY.get();
        markersFromProjectiles = MARKERS_PROJECTILE.get();

        for (String s : MARKER_SPAWNING_ENTITIES.get())
        {
            markerSpawningEntities.add(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(s)));
        }
    }
}
