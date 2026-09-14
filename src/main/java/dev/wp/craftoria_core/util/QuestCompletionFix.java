package dev.wp.craftoria_core.util;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.ProgressionMode;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public final class QuestCompletionFix {
    private QuestCompletionFix() {
    }

    public static void completeStuckDependants(Quest quest, QuestProgressEventData<?> data) {
        TeamData teamData = data.getTeamData();
        if (teamData == null || !teamData.getFile().isServerSide()) return;

        for (QuestObject dependant : quest.getDependants()) {
            if (dependant instanceof Quest dependantQuest) completeIfStuck(dependantQuest, teamData);
        }
    }

    // The mixin only repairs dependants at the moment a dependency completes, so quests already stuck
    // before this fix existed need one sweep to catch up.
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerQuestFile file = ServerQuestFile.INSTANCE;
        if (file == null) return;

        TeamData teamData = file.getOrCreateTeamData(player);
        if (teamData == null) return;

        for (QuestObjectBase object : file.getAllObjects()) {
            if (object instanceof Quest quest) completeIfStuck(quest, teamData);
        }
    }

    private static void completeIfStuck(Quest quest, TeamData teamData) {
        // Only FLEXIBLE quests accept task progress before their dependencies are met, so only they
        // can end up with every task finished while the quest itself was never completed.
        if (quest.getProgressionMode() != ProgressionMode.FLEXIBLE) return;
        if (teamData.isCompleted(quest) || !quest.areDependenciesComplete(teamData)) return;

        Collection<Task> tasks = quest.getTasks();
        if (tasks.isEmpty()) return;

        for (Task task : tasks) {
            if (teamData.getProgress(task.id) < task.getMaxProgress()) return;
        }

        for (Task task : tasks) {
            if (!teamData.isCompleted(task)) teamData.markTaskCompleted(task);
        }

        // markTaskCompleted only re-checks the parent quest when it actually changes a task, so a
        // quest whose tasks were all already flagged complete still needs completing explicitly.
        if (!teamData.isCompleted(quest)) {
            quest.onCompleted(new QuestProgressEventData<>(
                    new Date(), teamData, quest, teamData.getOnlineMembers(), List.of()
            ));
        }
    }
}
