package n643064.zombie_tactics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

public class MarkerEntity extends Entity
{
    private int timeToLive;
    private boolean notified;
    public MarkerEntity(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
        timeToLive = Config.markerLifeTime * 20;

    }

    public MarkerEntity(Level level)
    {
        this(Main.MARKER.get(), level);
    }


    @Override
    public void baseTick()
    {
        if (!level().isClientSide && !notified) {
            notified = true;
            for (Entity entity : level().getEntities(this, getBoundingBox().inflate(Config.markerRange),
                    entity -> entity instanceof IMarkerFollower)) {
                ((IMarkerFollower) entity).zombieTactics$setTargetMarker(this);
            }
        }
        timeToLive--;
        if (timeToLive <= 0)
            this.remove(RemovalReason.DISCARDED);
    }

    @Override @ParametersAreNonnullByDefault
    protected void defineSynchedData()
    {

    }

    @Override @ParametersAreNonnullByDefault
    protected void readAdditionalSaveData(CompoundTag compoundTag)
    {

    }

    @Override @ParametersAreNonnullByDefault
    protected void addAdditionalSaveData(CompoundTag compoundTag)
    {

    }
}
