package n643064.zombie_tactics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
@Mod(Main.MODID)
public class Main {
 public static final String MODID="zombie_tactics";
 public static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,MODID);
 public static final RegistryObject<EntityType<MarkerEntity>> MARKER=ENTITIES.register("marker",()->EntityType.Builder.<MarkerEntity>of(MarkerEntity::new,MobCategory.MISC).noSave().updateInterval(100).sized(0.1f,0.1f).canSpawnFarFromPlayer().build("zombie_tactics:marker"));
 public Main(){IEventBus bus=FMLJavaModLoadingContext.get().getModEventBus();ENTITIES.register(bus);ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,Config.SPEC);}
}
