package dev.wp.craftoria_core.mixin.gametechbcsspellbooks;

import com.gametechbc.gametechbcsspellbooks.entity.extended_mobs.ExtendedDeadKingBoss;
import com.gametechbc.gametechbcsspellbooks.init.GSSpells;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.goals.DeadKingAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

// Irons Spellbooks 3.16+ moved DeadKingAnimatedWarlockAttackGoal into the goals subpackage,
// which NoClassDefFoundErrors the original setFinalPhaseGoals when the boss enters phase 2.
@Restriction(
        require = {
                @Condition("gametechbcs_spellbooks")
        }
)
@Mixin(ExtendedDeadKingBoss.class)
public abstract class ExtendedDeadKingBossMixin extends DeadKingBoss {
    protected ExtendedDeadKingBossMixin(EntityType<? extends AbstractSpellCastingMob> type, Level level) {
        super(type, level);
    }

    /**
     * @author WP
     * @reason Rebuild the goal list against the relocated DeadKingAnimatedWarlockAttackGoal.
     */
    @Overwrite
    protected void setFinalPhaseGoals() {
        this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
        this.goalSelector.removeAllGoals(x -> true);
        this.goalSelector.addGoal(1, craftoria_core$barrage(this, SpellRegistry.WITHER_SKULL_SPELL.get(), 5, 5, 60, 140, 4));
        this.goalSelector.addGoal(2, craftoria_core$barrage(this, SpellRegistry.SUMMON_VEX_SPELL.get(), 3, 5, 400, 600, 1));
        this.goalSelector.addGoal(3, craftoria_core$barrage(this, GSSpells.AERIAL_COLLAPSE_SPELL.get(), 4, 5, 240, 400, 1));
        this.goalSelector.addGoal(4, new DeadKingAnimatedWarlockAttackGoal(this, 1f, 55, 85)
                .setSpellQuality(.6f, .8f)
                .setSpells(
                        List.of(
                                SpellRegistry.BLOOD_SLASH_SPELL.get(),
                                SpellRegistry.WITHER_SKULL_SPELL.get(), SpellRegistry.WITHER_SKULL_SPELL.get(),
                                GSSpells.ASHEN_BREATH.get(),
                                SpellRegistry.BLAZE_STORM_SPELL.get(),
                                SpellRegistry.ACID_ORB_SPELL.get()
                        ),
                        List.of(GSSpells.METEOR_STORM_SPELL.get()),
                        List.of(SpellRegistry.BLOOD_STEP_SPELL.get()),
                        List.of()
                )
                .setIsFlying()
                .setSingleUseSpell(GSSpells.BLACKOUT_SPELL.get(), 10, 60, 3, 3)
                .setMeleeBias(.8f, .8f)
                .setAllowFleeing(false));
        this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32f, .9f));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.hasUsedSingleAttack = false;
    }

    // Stand-in for ExtendedDeadKingBoss' private ExtendedDeadKingBarrageGoal, which we can't reach from here.
    @Unique
    private static SpellBarrageGoal craftoria_core$barrage(DeadKingBoss king, AbstractSpell spell, int minLevel, int maxLevel, int intervalMin, int intervalMax, int projectileCount) {
        return new SpellBarrageGoal(king, spell, minLevel, maxLevel, intervalMin, intervalMax, projectileCount) {
            @Override
            public boolean canUse() {
                return !king.isMeleeing && super.canUse();
            }
        };
    }
}
