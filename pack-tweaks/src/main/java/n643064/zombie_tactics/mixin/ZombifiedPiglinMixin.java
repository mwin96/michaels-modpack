package n643064.zombie_tactics.mixin;

import n643064.zombie_tactics.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglin.class)
public abstract class ZombifiedPiglinMixin extends Zombie implements NeutralMob
{
    public ZombifiedPiglinMixin(EntityType<? extends Zombie> entityType, Level level) {super(entityType, level);}

    /**
     * @author me
     * @reason no :3
     */
    @Inject(method="addBehaviourGoals", at=@At("TAIL"))
    private void michaelspack$addGoals(CallbackInfo ci)
    {
        if (Config.affectPiglins)
        {
            if (Config.targetAnimals)
                this.targetSelector.addGoal(Config.targetAnimalsPriority, new NearestAttackableTargetGoal<>(this, Animal.class, Config.targetAnimalsVisibility));
            if (Config.mineBlocks)
                this.goalSelector.addGoal(Config.miningPriority, new ZombieMineGoal<>((Zombie & IMarkerFollower) this));
            if (Config.enableMarkers)
            {
                this.goalSelector.addGoal(2, new RemoveMarkerGoal<>((Zombie & IMarkerFollower) this));
                this.goalSelector.addGoal(Config.markerPathingPriority, new MoveTowardsMarkerGoal<>((Zombie & IMarkerFollower) this));
            }
        }
    }
}
