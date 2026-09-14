package dev.wp.craftoria_core.mixin.ftbquests;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.wp.craftoria_core.util.QuestCompletionFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Quest.class)
public class QuestMixin {
    @Inject(method = "onCompleted", at = @At("TAIL"))
    private void craftoriaCore$completeStuckDependants(QuestProgressEventData<?> data, CallbackInfo ci) {
        QuestCompletionFix.completeStuckDependants((Quest) (Object) this, data);
    }
}
