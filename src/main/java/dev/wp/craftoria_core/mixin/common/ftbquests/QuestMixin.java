package dev.wp.craftoria_core.mixin.common.ftbquests;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.wp.craftoria_core.util.QuestCompletionFix;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition("ftbquests"))
@Mixin(Quest.class)
public class QuestMixin {
    @Inject(method = "onCompleted", at = @At("TAIL"))
    private void completeStuckDependants(QuestProgressEventData<?> data, CallbackInfo ci) {
        QuestCompletionFix.completeStuckDependants((Quest) (Object) this, data);
    }
}
