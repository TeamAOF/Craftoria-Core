package dev.wp.craftoria_core.funny;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.wp.craftoria_core.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FunnyFeature {
    private static final ThreadLocal<Random> TRANSFORM_RANDOM = new ThreadLocal<>();
    private enum ReactionCategory {
        EXCITED,
        CONFUSED,
        NEUTRAL,
        SAD
    }

    private enum MessageContext {
        GREETING,
        GOODBYE,
        QUESTION,
        GRATITUDE,
        APOLOGY,
        COMMAND,
        DISCOVERY,
        VICTORY,
        FAILURE,
        COMPLAINT,
        FOOD,
        SLEEPY,
        MINING,
        CRAFTING,
        MACHINE,
        REDSTONE,
        ENCHANTING,
        FARMING,
        FISHING,
        BUILDING,
        EXPLORATION,
        TRADING,
        MOBS,
        ANIMAL,
        WEATHER,
        WATER,
        FIRE,
        EXPLOSION,
        DANGER,
        DEATH,
        LAG,
        CHUNK_LOADING,
        TECHNICAL,
        SERVER_ADMIN,
        INVENTORY,
        SLEEPING,
        ACHIEVEMENT,
        RARE_LOOT,
        PORTAL,
        NETHER,
        END,
        DIMENSION,
        COORDINATES,
        MAGIC,
        TIME_OF_DAY,
        SOCIAL,
        PARANOIA,
        NONE
    }

    private record Reaction(String face, String suffix, int weight) {
    }

    private record ContextRule(MessageContext context, Pattern pattern, int baseWeight) {
    }

    private record ContextScore(MessageContext context, int score, int hits) {
    }

    private record ContextPair(MessageContext first, MessageContext second, int bonus) {
    }

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[\\w']+\\b");
    private static final Pattern STUTTER_PATTERN = Pattern.compile("\\b([A-Za-z])([A-Za-z]{2,})\\b");
    private static final Pattern URL_OR_EMAIL_PATTERN = Pattern.compile(
            "(?i)\\b(?:https?://|www\\.)\\S+|\\b[\\w.+-]+@[\\w.-]+\\.\\w+\\b");
    private static final Pattern QUESTION_MARK = Pattern.compile("\\?");
    private static final Pattern COMMAND_PREFIX = Pattern.compile("^\\s*(?:/|(?:please|pls|can you|could you|do|make|give|set|run|check|fix)\\b)", Pattern.CASE_INSENSITIVE);

    private static final Pattern HAPPY_KEYWORDS = keywordPattern(
            "love", "wuv", "happy", "excited", "yay", "awesome", "great", "fun", "nice",
            "cool", "amazing", "cute", "adorable", "win", "won", "perfect", "best", "woo",
            "yummy", "welcome", "sparkle", "pog", "hooray");
    private static final Pattern DISTRESS_KEYWORDS = keywordPattern(
            "tired", "exhausted", "sleepy", "sad", "cry", "crying", "hurt", "injured", "pain",
            "ouch", "sick", "hate", "angry", "mad", "annoyed", "frustrated", "alone", "lonely",
            "lost", "scared", "afraid", "worried", "bored", "down", "depressed", "die", "dead",
            "terrible", "awful", "broken", "lag", "crash");
    private static final Pattern CONFUSION_KEYWORDS = keywordPattern(
            "confused", "confusing", "unsure", "why", "huh", "hmm", "wait", "what", "explain",
            "which", "who", "clueless", "confuzzle");

    private static Pattern keywordPattern(String... words) {
        String joined = String.join("|", words).replace(" ", "\\s+");
        return Pattern.compile("(?i)(?<!\\w)(?:" + joined + ")(?!\\w)");
    }

    private static final String[] ACTIONS = {
            "*happy wiggle*",
            "*does a tiny twirl*",
            "*bounces excitedly*",
            "*offers a warm cookie*",
            "*sparkles softly*",
            "*waves tiny paws*",
            "*scurries closer*",
            "*does a cozy hop*",
            "*puts on a very small hat*",
            "*wiggles with suspicious confidence*",
            "*produces a tiny clipboard*",
            "*carefully dusts off the nearest pebble*"
    };

    private static final String[] GENERAL_WHIMSY = {
            "the chat goblin has awakened",
            "a tiny duck has approved this message",
            "three invisible mice applaud",
            "the local sparkle budget has been exceeded",
            "someone's emotional support potato rolls past",
            "a tiny wizard checks the paperwork and nods",
            "a suspiciously round fairy observes from nearby",
            "the whimsy department has been notified",
            "a small bell goes *ding ding* for no apparent reason",
            "the nearest mushroom is now slightly more confident",
            "a tiny committee of woodland creatures takes notes",
            "the moon briefly gives this message a thumbs-up",
            "a tiny goose has entered the room and refuses to elaborate",
            "the nearest pebble has become emotionally invested",
            "a very small bureaucrat stamps this conversation with 'interesting'",
            "one extremely polite moth approves",
            "a tiny wizard writes this down in sparkly ink",
            "a miniature stagehand adjusts the curtains for no reason"
    };

    private static final String[] HIGH_WHIMSY = {
            "*The Tiny Chat Wizard appears wearing ceremonial socks.*",
            "*a tiny creature sprints through the chat carrying a spoon*",
            "*the universe briefly squeaks*",
            "*someone has released one (1) ceremonial goose*",
            "*the council has decided that you are friend-shaped*",
            "*a tiny librarian shushes the entire server and leaves*",
            "*a small wizard rolls a d20 for absolutely no reason*",
            "*the local frog has been promoted to middle management*",
            "*an invisible parade has passed through the server room*",
            "*a tiny oracle points dramatically at nothing in particular*",
            "*the whimsy reactor reaches 103% sparkle*",
            "*a miniature bard plays exactly two notes and looks satisfied*",
            "*the Department of Little Things requests a meeting*",
            "*a suspiciously qualified duck has assumed temporary command*"
    };

    private static final Map<ReactionCategory, List<Reaction>> REACTIONS = Map.of(
            ReactionCategory.EXCITED, List.of(
                    new Reaction("owo", "nya~", 5),
                    new Reaction("(*^w^*)", "ehehe~", 4),
                    new Reaction("(ﾉ>ω<)ﾉ", "yaaay!", 3),
                    new Reaction("\\(^w^)/", "is super happy!", 3),
                    new Reaction("(≧◡≦)", "sparkles everywhere~", 2),
                    new Reaction("☆w☆", "cannot contain the joy!", 1)),
            ReactionCategory.CONFUSED, List.of(
                    new Reaction("owo?", "eep?", 5),
                    new Reaction(">w<?", "wait, what?", 4),
                    new Reaction("(・ω・)?", "is a little confused~", 3),
                    new Reaction("(・・ )?", "needs a tiny explanation~", 3),
                    new Reaction("(｡•́︿•̀｡)", "is trying very hard to understand~", 2),
                    new Reaction("(╥﹏╥)", "has lost the plot~", 1)),
            ReactionCategory.NEUTRAL, List.of(
                    new Reaction("uwu", "nya~", 5),
                    new Reaction("^w^", "is doing their best~", 4),
                    new Reaction("(*^w^*)", "quietly sparkles~", 3),
                    new Reaction("(・ω・)", "nods thoughtfully~", 3),
                    new Reaction("(￣ω￣)", "vibes peacefully~", 2),
                    new Reaction("(✿◕‿◕)", "adds a tiny sparkle~", 1)),
            ReactionCategory.SAD, List.of(
                    new Reaction(";w;", "will miss you~", 5),
                    new Reaction("(｡•́︿•̀｡)", "sniffles softly~", 4),
                    new Reaction("(つω`｡)", "waves a tiny goodbye~", 3),
                    new Reaction("(；ω；)", "is a little lonely now~", 3),
                    new Reaction("(╥﹏╥)", "sends a cozy farewell~", 2),
                    new Reaction("(；︿；)", "sadly watches the door~", 1))
    );

    private static final Map<MessageContext, List<String>> CONTEXT_EVENTS = Map.ofEntries(
            Map.entry(MessageContext.GREETING, List.of(
                    "*a tiny bell rings politely*", "*waves with both tiny paws*", "*the welcome committee arrives with a banner*",
                    "*sprinkles one tasteful handful of confetti*", "*the receptionist has prepared your tiny welcome badge*")),
            Map.entry(MessageContext.GOODBYE, List.of(
                    "*the tiny farewell orchestra plays one note*", "*waves both tiny paws until they disappear over the horizon*",
                    "*the friendship committee packs a little snack for the journey*", "*a tiny moon sticker is placed on the departure form*")),
            Map.entry(MessageContext.QUESTION, List.of(
                    "*consults the tiny encyclopedia*", "*raises one tiny finger thoughtfully*", "*a small owl begins taking notes*",
                    "*searches the pockets for an answer*", "*the answer department starts flipping through suspiciously tiny files*")),
            Map.entry(MessageContext.GRATITUDE, List.of(
                    "*accepts the gratitude and stores it safely in a jar*", "*gives a tiny appreciative nod*",
                    "*the thank-you department stamps a little seal of approval*", "*offers a celebratory cookie in return*")),
            Map.entry(MessageContext.APOLOGY, List.of(
                    "*offers a tiny forgiveness certificate*", "*carefully places a blanket over the incident*",
                    "*the apology has been received by the small council*", "*gently pats the situation*")),
            Map.entry(MessageContext.COMMAND, List.of(
                    "*salutes with surprising seriousness*", "*scribbles that down on the tiny clipboard*",
                    "*runs off with important little footsteps*", "*checks the mission scroll twice*")),
            Map.entry(MessageContext.DISCOVERY, List.of(
                    "*gasp*", "*a tiny detective circles the discovery in red crayon*", "*deploys the ceremonial magnifying glass*",
                    "*the discovery department rings a tiny gong*")),
            Map.entry(MessageContext.VICTORY, List.of(
                    "*confetti cannon: carefully, responsibly, approximately*", "*the tiny crowd goes WOOOOOO*",
                    "*a miniature trophy appears from nowhere*", "*victory trumpet sounds from somewhere slightly to the left*")),
            Map.entry(MessageContext.FAILURE, List.of(
                    "*places a tiny consolation sticker on the problem*", "*the emergency beanbag has been deployed*",
                    "*a small violin performs one extremely dignified note*", "*puts a tiny helmet on the next attempt*")),
            Map.entry(MessageContext.COMPLAINT, List.of(
                    "*the complaint department opens a very tiny ticket*", "*places the problem gently into the official grumble drawer*",
                    "*the tiny customer service wizard appears with a clipboard*", "*nods sympathetically while taking completely unnecessary notes*")),
            Map.entry(MessageContext.FOOD, List.of(
                    "*sniffs the air hopefully*", "*the snack committee has become extremely attentive*",
                    "*produces a suspiciously fresh cookie*", "*a nearby potato nods knowingly*")),
            Map.entry(MessageContext.SLEEPY, List.of(
                    "*yawns so hard the chat briefly wobbles*", "*pulls out a tiny pillow*",
                    "*the bedtime goblin checks the clock*", "*wraps the nearest sentence in a blanket*")),
            Map.entry(MessageContext.MINING, List.of(
                    "*puts on a tiny safety helmet*", "*a junior mining inspector appears with a clipboard*",
                    "*listens carefully for suspicious rocks*", "*the cave has been politely informed that you are coming*")),
            Map.entry(MessageContext.CRAFTING, List.of(
                    "*puts on tiny safety goggles*", "*checks the recipe with one extremely serious finger*",
                    "*the crafting table clears its throat*", "*a tiny recipe book flips to the correct page by itself*")),
            Map.entry(MessageContext.MACHINE, List.of(
                    "*the factory floor lights up one tiny warning lamp*", "*puts on a tiny hard hat and checks the throughput clipboard*",
                    "*a miniature engineer tightens one very important bolt*", "*the machine hums approvingly and requests a snack*")),
            Map.entry(MessageContext.REDSTONE, List.of(
                    "*the redstone dust starts humming suspiciously*", "*a tiny redstone technician follows the signal with a magnifying glass*",
                    "*the circuit receives a polite little click*", "*one tiny piston performs a completely unnecessary demonstration*")),
            Map.entry(MessageContext.ENCHANTING, List.of(
                    "*opens the extremely sparkly book of mysterious numbers*", "*a tiny wizard carefully counts the experience orbs*",
                    "*the enchantment table makes a pleased little hum*", "*puts on ceremonial wizard spectacles*")),
            Map.entry(MessageContext.FARMING, List.of(
                    "*the farming committee checks the soil with tiny serious faces*", "*a miniature scarecrow salutes*",
                    "*the crops receive a motivational speech*", "*the harvest department opens one suspiciously tiny ledger*")),
            Map.entry(MessageContext.FISHING, List.of(
                    "*a tiny fisherman leans forward with enormous professional concentration*", "*the fish union has been notified*",
                    "*a miniature bobber performs an unnecessarily dramatic wobble*", "*the fishing department quietly asks whether you have snacks*")),
            Map.entry(MessageContext.BUILDING, List.of(
                    "*a tiny architect unrolls an absurdly detailed blueprint*", "*checks the wall for structural vibes*",
                    "*the decoration committee produces one tasteful flower pot*", "*a miniature foreman nods at the build*")),
            Map.entry(MessageContext.EXPLORATION, List.of(
                    "*the tiny expedition leader points heroically at the horizon*", "*unfolds the adventure map with dramatic flair*",
                    "*the exploration committee packs emergency biscuits*", "*a tiny compass spins twice and pretends that was intentional*")),
            Map.entry(MessageContext.TRADING, List.of(
                    "*the tiny economist checks the exchange rate*", "*a miniature villager negotiates very intensely with a bean*",
                    "*the emerald accountant clears their throat*", "*the trading floor opens for business*")),
            Map.entry(MessageContext.MOBS, List.of(
                    "*the mob census taker arrives with a tiny clipboard*", "*a very small monster hunter checks the corners*",
                    "*the nearest hostile mob has been given a strongly worded look*", "*a tiny security guard whispers 'we're probably fine'*")),
            Map.entry(MessageContext.ANIMAL, List.of(
                    "*the animal appreciation committee arrives immediately*", "*a tiny zoologist takes one delighted note*",
                    "*the nearest creature receives a ceremonial head pat*", "*an extremely qualified duck approves the animal situation*")),
            Map.entry(MessageContext.WEATHER, List.of(
                    "*checks the tiny weather station*", "*the cloud department updates the forecast on a sticky note*",
                    "*a tiny raincoat appears from nowhere*", "*the sun receives a polite scheduling reminder*")),
            Map.entry(MessageContext.WATER, List.of(
                    "*puts on tiny inflatable arm floaties*", "*a duck immediately volunteers as lifeguard*",
                    "*the water receives a respectful little splash*", "*checks the tiny boat for important tiny boat paperwork*")),
            Map.entry(MessageContext.FIRE, List.of(
                    "*very carefully carries a comically small fire extinguisher*", "*the fire department's tiniest intern arrives*",
                    "*backs away from the lava with professional dignity*", "*puts the nearest block on the no-touch list*")),
            Map.entry(MessageContext.EXPLOSION, List.of(
                    "*three tiny engineers stare at the crater in silence*", "*the emergency helmet department runs in late*",
                    "*someone gently places a 'WHOOPS' sign nearby*", "*the blast radius receives an official measuring tape*")),
            Map.entry(MessageContext.DANGER, List.of(
                    "*deploys the emergency tiny helmet*", "*the safety goblin drops everything and runs over*",
                    "*looks around with extremely professional concern*", "*places a tiny warning sign nearby*")),
            Map.entry(MessageContext.DEATH, List.of(
                    "*the tiny mortician sighs and opens the paperwork drawer*", "*deploys the emergency respawn blanket*",
                    "*a tiny ghost waves from the administrative wing*", "*the afterlife receptionist hands over a numbered ticket*")),
            Map.entry(MessageContext.LAG, List.of(
                    "*the packet-delivery duck sprints past carrying a tiny queue number*", "*the tick hamster requests hazard pay*",
                    "*gently pokes the server with a stick*", "*the latency department flips the emergency pancake*")),
            Map.entry(MessageContext.CHUNK_LOADING, List.of(
                    "*stares intensely at the horizon while holding a tiny loading bar*", "*encourages the chunks to believe in themselves*",
                    "*a miniature world-generation engineer adjusts one invisible knob*", "*the loading committee would like everyone to remain calm*")),
            Map.entry(MessageContext.TECHNICAL, List.of(
                    "*opens the sacred drawer labeled 'probably fine'*", "*a tiny debugger arrives with a magnifying glass*",
                    "*the stack trace is placed gently on a tiny pillow*", "*the bug department pretends not to know you*")),
            Map.entry(MessageContext.SERVER_ADMIN, List.of(
                    "*puts on the tiny server-admin hat*", "*opens the emergency binder of Important Server Things*",
                    "*the maintenance goblin checks the backup twice*", "*a miniature sysadmin whispers 'have you tried turning it off and on again?'*")),
            Map.entry(MessageContext.INVENTORY, List.of(
                    "*a tiny inventory clerk counts the slots with great concern*", "*the storage goblin opens one drawer and immediately closes it again*",
                    "*checks the tiny pockets for forbidden spaghetti*", "*a miniature accountant reorganizes exactly one item stack*")),
            Map.entry(MessageContext.SLEEPING, List.of(
                    "*fluffs the nearest pillow with professional dedication*", "*the bedtime goblin makes sure the blankets are symmetrical*",
                    "*places a tiny 'do not wake' sign nearby*", "*checks the respawn paperwork twice*")),
            Map.entry(MessageContext.ACHIEVEMENT, List.of(
                    "*the tiny achievement banner unfurls dramatically*", "*a miniature ceremony is scheduled immediately*",
                    "*the accomplishment receives a gold star sticker*", "*the milestone department fires one tasteful confetti puff*")),
            Map.entry(MessageContext.RARE_LOOT, List.of(
                    "*the rare loot alarm goes DING DING DING*", "*a tiny treasure inspector arrives wearing a monocle*",
                    "*the valuables department respectfully gasps*", "*someone places velvet underneath the item for dramatic reasons*")),
            Map.entry(MessageContext.PORTAL, List.of(
                    "*the portal inspector checks the frame for dimensional paperwork*", "*a tiny interdimensional toll booth opens for business*",
                    "*the portal makes a polite FWOOOSH*", "*someone checks that nobody left a sandwich in the portal*")),
            Map.entry(MessageContext.NETHER, List.of(
                    "*checks that the emergency portal umbrella is nearby*", "*the nether travel committee recommends snacks and courage*",
                    "*a tiny piglin checks the exchange rate*", "*looks at the nearest patch of lava with deep suspicion*")),
            Map.entry(MessageContext.END, List.of(
                    "*adjusts the tiny interdimensional helmet*", "*a tiny enderman carefully holds three blocks and looks proud*",
                    "*the expedition map acquires one extremely purple corner*", "*the dragon department has been notified*")),
            Map.entry(MessageContext.DIMENSION, List.of(
                    "*the dimensional cartographer unfolds seventeen tiny maps*", "*a miniature physicist stares into the distance and says 'hmm'*",
                    "*the reality department has been notified*", "*carefully checks that this dimension is still the correct one*")),
            Map.entry(MessageContext.COORDINATES, List.of(
                    "*the tiny cartographer circles the coordinates with heroic precision*", "*a miniature compass spins once for dramatic effect*",
                    "*the coordinate goblin files the numbers alphabetically*", "*the navigation department nods knowingly*")),
            Map.entry(MessageContext.MAGIC, List.of(
                    "*puts on ceremonial wizard sleeves*", "*a tiny apprentice writes this down in an extremely sparkly notebook*",
                    "*the nearby runes make a polite little chime*", "*someone whispers 'behold' from behind a curtain*")),
            Map.entry(MessageContext.TIME_OF_DAY, List.of(
                    "*checks the tiny pocket watch*", "*the bedtime committee glances at the clock*",
                    "*the sun receives a polite scheduling reminder*", "*a tiny rooster prepares very seriously for its shift*")),
            Map.entry(MessageContext.SOCIAL, List.of(
                    "*the friendship committee arrives holding hands*", "*a tiny party planner starts taking reservations*",
                    "*the socialization department releases tasteful confetti*", "*offers everyone a little friendship coupon*")),
            Map.entry(MessageContext.PARANOIA, List.of(
                    "*slowly checks behind the nearest block*", "*the suspicious-object inspector arrives immediately*",
                    "*looks at the ceiling for a little too long*", "*a tiny detective begins quietly following the situation*"))
    );

    private static final List<ContextRule> CONTEXT_RULES = List.of(
            rule(MessageContext.EXPLOSION, 16, "explode", "exploded", "explosion", "blast", "creeper", "tnt", "boom", "kaboom", "blew up", "blown up"),
            rule(MessageContext.DEATH, 15, "died", "death", "dead", "killed", "fell", "lava", "void", "respawn"),
            rule(MessageContext.LAG, 14, "lag", "lagging", "freeze", "freezing", "rubberband", "rubberbanding", "tps", "tick", "ticks", "ms"),
            rule(MessageContext.CHUNK_LOADING, 13, "chunk", "chunks", "loading", "generation", "generating", "rendering", "render distance"),
            rule(MessageContext.WEATHER, 7, "rain", "raining", "storm", "thunder", "snow", "snowing", "weather", "sunny", "lightning"),
            rule(MessageContext.WATER, 7, "water", "ocean", "river", "fishing", "fish", "boat", "drowned", "bubble"),
            rule(MessageContext.FIRE, 8, "fire", "flame", "lava", "burning", "burned", "campfire"),
            rule(MessageContext.MACHINE, 11, "machine", "machines", "assembler", "mixer", "generator", "turbine", "reactor", "compressor", "centrifuge", "electrolyzer", "fabricator", "factory", "automation"),
            rule(MessageContext.CRAFTING, 8, "craft", "crafting", "recipe", "smelt", "smelting", "crafting table"),
            rule(MessageContext.REDSTONE, 9, "redstone", "lever", "button", "piston", "observer", "hopper", "comparator", "repeater", "clock", "circuit", "signal"),
            rule(MessageContext.ENCHANTING, 8, "enchant", "enchantment", "anvil", "mending", "fortune", "silk touch", "sharpness", "experience", "xp"),
            rule(MessageContext.MAGIC, 7, "magic", "magical", "spell", "wizard", "potion", "brewing", "rune", "runes"),
            rule(MessageContext.MINING, 10, "mine", "mining", "ore", "diamond", "diamonds", "iron", "gold", "copper", "coal", "lapis", "deepslate", "cave", "caves"),
            rule(MessageContext.RARE_LOOT, 11, "ancient debris", "netherite", "elytra", "totem", "legendary", "rare loot", "treasure", "rare"),
            rule(MessageContext.FISHING, 9, "fish", "fishing", "fished", "fishing rod", "catch", "caught"),
            rule(MessageContext.FARMING, 8, "farm", "farming", "crop", "crops", "wheat", "carrot", "carrots", "potato", "potatoes", "harvest", "seed", "seeds"),
            rule(MessageContext.BUILDING, 7, "build", "building", "house", "base", "tower", "castle", "wall", "floor", "roof", "bridge", "decorate", "decoration"),
            rule(MessageContext.EXPLORATION, 7, "explore", "exploring", "adventure", "travel", "travelling", "traveling", "biome", "village", "fortress", "bastion", "dungeon"),
            rule(MessageContext.TRADING, 7, "trade", "trading", "trader", "villager", "emerald", "emeralds", "price", "prices", "discount", "discounted"),
            rule(MessageContext.MOBS, 8, "mob", "mobs", "monster", "monsters", "zombie", "skeleton", "creeper", "spider", "enderman", "witch", "warden"),
            rule(MessageContext.ANIMAL, 7, "cat", "kitten", "dog", "wolf", "fox", "axolotl", "frog", "bee", "goat", "cow", "pig", "chicken", "sheep", "horse"),
            rule(MessageContext.PORTAL, 9, "portal", "nether portal", "end portal", "portal frame"),
            rule(MessageContext.NETHER, 9, "nether", "netherite", "blaze", "wither", "soul sand", "soul soil", "ghast", "piglin", "magma"),
            rule(MessageContext.END, 9, "the end", "ender", "enderman", "ender pearl", "dragon", "elytra", "shulker", "chorus"),
            rule(MessageContext.DIMENSION, 7, "dimension", "overworld", "nether", "end dimension"),
            rule(MessageContext.COORDINATES, 10, "\\bx\\s*:?\\s*-?\\d+\\b", "\\by\\s*:?\\s*-?\\d+\\b", "\\bz\\s*:?\\s*-?\\d+\\b"),
            rule(MessageContext.INVENTORY, 7, "inventory", "backpack", "storage", "chest", "shulker box", "full inventory", "slot", "slots"),
            rule(MessageContext.SLEEPING, 6, "bed", "sleeping", "sleep", "spawn point", "respawn"),
            rule(MessageContext.ACHIEVEMENT, 7, "achievement", "advancement", "challenge", "completed", "unlocked", "milestone"),
            rule(MessageContext.TIME_OF_DAY, 5, "morning", "afternoon", "evening", "night", "midnight", "sunrise", "sunset", "daytime"),
            rule(MessageContext.GREETING, 5, "hello", "hi", "hey", "hewwo", "welcome", "good morning", "good afternoon", "good evening"),
            rule(MessageContext.GOODBYE, 6, "bye", "goodbye", "good bye", "see you", "later", "cya"),
            rule(MessageContext.GRATITUDE, 5, "thanks", "thank", "thx", "ty", "appreciate", "grateful"),
            rule(MessageContext.APOLOGY, 5, "sorry", "apologies", "apologize", "oops", "my bad", "forgive"),
            rule(MessageContext.VICTORY, 7, "won", "win", "victory", "beat", "defeated", "finished", "done", "success", "finally", "yay", "got it"),
            rule(MessageContext.DISCOVERY, 7, "found", "discover", "discovered", "look what", "here it is", "there it is", "i got", "we got"),
            rule(MessageContext.FAILURE, 7, "failed", "failure", "lost", "broken", "broke", "crashed", "rip", "ugh"),
            rule(MessageContext.FOOD, 5, "food", "hungry", "eat", "eating", "ate", "cookie", "cake", "bread", "potato", "pizza", "coffee", "tea", "yummy"),
            rule(MessageContext.SLEEPY, 5, "sleepy", "tired", "exhausted", "yawn", "nap"),
            rule(MessageContext.DANGER, 9, "danger", "dangerous", "careful", "watch out", "run", "help", "help me", "uh oh", "oh no", "incoming"),
            rule(MessageContext.COMPLAINT, 6, "why is", "why does", "annoying", "frustrating", "this sucks", "broken again", "hate this", "what a mess"),
            rule(MessageContext.TECHNICAL, 9, "bug", "glitch", "error", "crash", "stacktrace", "exception", "config", "code", "compile", "compiled", "mod", "mods", "server", "log", "logs"),
            rule(MessageContext.SERVER_ADMIN, 10, "server", "restart", "backup", "whitelist", "op", "operator", "permission", "permissions", "maintenance", "console", "admin"),
            rule(MessageContext.SOCIAL, 4, "party", "team", "together", "join", "joined", "welcome", "friend", "fwiend", "everyone", "guys"),
            rule(MessageContext.PARANOIA, 7, "suspicious", "watching", "behind me", "following me", "someone is", "is that", "i hear", "i saw"),
            rule(MessageContext.COMMAND, 4, "please", "pls", "can you", "could you", "do this", "make this", "set this", "fix this", "check this")
    );

    private static final List<ContextPair> CONTEXT_SYNERGIES = List.of(
            new ContextPair(MessageContext.EXPLOSION, MessageContext.MACHINE, 13),
            new ContextPair(MessageContext.EXPLOSION, MessageContext.REDSTONE, 9),
            new ContextPair(MessageContext.EXPLOSION, MessageContext.BUILDING, 8),
            new ContextPair(MessageContext.EXPLOSION, MessageContext.DEATH, 11),
            new ContextPair(MessageContext.LAG, MessageContext.SERVER_ADMIN, 12),
            new ContextPair(MessageContext.LAG, MessageContext.CHUNK_LOADING, 10),
            new ContextPair(MessageContext.TECHNICAL, MessageContext.SERVER_ADMIN, 10),
            new ContextPair(MessageContext.TECHNICAL, MessageContext.MACHINE, 7),
            new ContextPair(MessageContext.DISCOVERY, MessageContext.RARE_LOOT, 13),
            new ContextPair(MessageContext.DISCOVERY, MessageContext.MINING, 9),
            new ContextPair(MessageContext.RARE_LOOT, MessageContext.NETHER, 9),
            new ContextPair(MessageContext.RARE_LOOT, MessageContext.FISHING, 8),
            new ContextPair(MessageContext.FISHING, MessageContext.WATER, 8),
            new ContextPair(MessageContext.FARMING, MessageContext.FOOD, 8),
            new ContextPair(MessageContext.BUILDING, MessageContext.MACHINE, 7),
            new ContextPair(MessageContext.BUILDING, MessageContext.REDSTONE, 7),
            new ContextPair(MessageContext.PORTAL, MessageContext.DANGER, 11),
            new ContextPair(MessageContext.PORTAL, MessageContext.DEATH, 10),
            new ContextPair(MessageContext.NETHER, MessageContext.PORTAL, 9),
            new ContextPair(MessageContext.END, MessageContext.PORTAL, 9),
            new ContextPair(MessageContext.MOBS, MessageContext.PARANOIA, 9),
            new ContextPair(MessageContext.MOBS, MessageContext.DANGER, 10),
            new ContextPair(MessageContext.COORDINATES, MessageContext.EXPLORATION, 8),
            new ContextPair(MessageContext.COORDINATES, MessageContext.RARE_LOOT, 8),
            new ContextPair(MessageContext.ACHIEVEMENT, MessageContext.VICTORY, 8),
            new ContextPair(MessageContext.FAILURE, MessageContext.COMPLAINT, 7),
            new ContextPair(MessageContext.SLEEPY, MessageContext.TIME_OF_DAY, 7),
            new ContextPair(MessageContext.SLEEPING, MessageContext.TIME_OF_DAY, 9),
            new ContextPair(MessageContext.GREETING, MessageContext.SOCIAL, 6),
            new ContextPair(MessageContext.GOODBYE, MessageContext.SOCIAL, 7)
    );

    private static LiteralArgumentBuilder<CommandSourceStack> intensity(String name, FunnyIntensity intensity) {
        return Commands.literal(name)
                .executes(context -> setSelf(context, intensity))
                .then(Commands.argument("player", EntityArgument.players())
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> setTargets(context, intensity)));
    }

    private FunnyFeature() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("uwu")
                .executes(FunnyFeature::toggleSelf)
                .then(intensity("off", FunnyIntensity.OFF))
                .then(intensity("low", FunnyIntensity.LOW))
                .then(intensity("medium", FunnyIntensity.MEDIUM))
                .then(intensity("high", FunnyIntensity.HIGH)));
    }

    private static int toggleSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!ServerConfig.funny) return disabled(context);

        ServerPlayer player = context.getSource().getPlayerOrException();
        FunnyPlayerData data = FunnyPlayerData.get(player.serverLevel());
        FunnyIntensity current = data.getIntensity(player.getUUID());
        if (current == FunnyIntensity.OFF) {
            current = data.getLastEnabled(player.getUUID());
            data.setIntensity(player.getUUID(), current);
        } else {
            data.setLastEnabled(player.getUUID(), current);
            data.setIntensity(player.getUUID(), FunnyIntensity.OFF);
        }

        FunnyIntensity result = data.getIntensity(player.getUUID());
        context.getSource().sendSuccess(
                () -> Component.literal("Funny chat: " + result.serializedName()), false);
        return 1;
    }

    private static int setSelf(CommandContext<CommandSourceStack> context, FunnyIntensity intensity)
            throws CommandSyntaxException {
        if (!ServerConfig.funny) return disabled(context);

        ServerPlayer player = context.getSource().getPlayerOrException();
        setIntensity(player, intensity);
        context.getSource().sendSuccess(
                () -> Component.literal("Funny chat for you: " + intensity.serializedName()), false);
        return 1;
    }

    private static int setTargets(CommandContext<CommandSourceStack> context, FunnyIntensity intensity)
            throws CommandSyntaxException {
        if (!ServerConfig.funny) return disabled(context);

        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
        for (ServerPlayer player : players) setIntensity(player, intensity);
        context.getSource().sendSuccess(
                () -> Component.literal("Set funny chat to " + intensity.serializedName()
                        + " for " + players.size() + " player(s)."), false);
        return players.size();
    }

    private static void setIntensity(ServerPlayer player, FunnyIntensity intensity) {
        FunnyPlayerData.get(player.serverLevel()).setIntensity(player.getUUID(), intensity);
    }

    private static int disabled(CommandContext<CommandSourceStack> context) {
        context.getSource().sendFailure(Component.literal("Funny chat is disabled on this server."));
        return 0;
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        if (!ServerConfig.funny) return;

        String transformed = transformForPlayer(event.getPlayer(), event.getRawText());
        if (transformed.equals(event.getRawText())) return;

        event.setMessage(Component.literal(transformed)
                .withStyle(event.getMessage().getStyle()));
    }

    public static String transformForPlayer(ServerPlayer player, String rawText) {
        if (!ServerConfig.funny) return rawText;

        FunnyIntensity intensity = FunnyPlayerData.get(player.serverLevel()).getIntensity(player.getUUID());
        return intensity == FunnyIntensity.OFF ? rawText
                : transformSeeded(rawText, intensity, player.getUUID().getMostSignificantBits()
                ^ player.getUUID().getLeastSignificantBits());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ServerConfig.funny || !(event.getEntity() instanceof ServerPlayer player)) return;

        FunnyPlayerData data = FunnyPlayerData.get(player.serverLevel());
        String name = player.getGameProfile().getName();
        if (data.isFirstJoin(player.getUUID())) {
            String message = randomMessage(FIRST_JOIN_MESSAGES);
            data.markJoined(player.getUUID());
            player.server.getPlayerList().broadcastSystemMessage(
                    announcement(message, name, ChatFormatting.LIGHT_PURPLE, ChatFormatting.YELLOW), false);
        } else {
            player.server.getPlayerList().broadcastSystemMessage(
                    announcement(randomMessage(JOIN_MESSAGES), name, ChatFormatting.AQUA, ChatFormatting.GREEN), false);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!ServerConfig.funny || !(event.getEntity() instanceof ServerPlayer player)) return;

        String name = player.getGameProfile().getName();
        player.server.getPlayerList().broadcastSystemMessage(
                announcement(randomMessage(LEAVE_MESSAGES), name, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY), false);
    }

    static String transform(String rawText, FunnyIntensity intensity) {
        return transformSeeded(rawText, intensity, 0L);
    }

    private static String transformSeeded(String rawText, FunnyIntensity intensity, long salt) {
        Random previous = TRANSFORM_RANDOM.get();
        TRANSFORM_RANDOM.set(new Random(rawText.hashCode() * 31L + salt + intensity.ordinal()));
        try {
            return transformBody(rawText, intensity);
        } finally {
            if (previous == null) TRANSFORM_RANDOM.remove();
            else TRANSFORM_RANDOM.set(previous);
        }
    }

    private static String transformBody(String rawText, FunnyIntensity intensity) {
        if (intensity == FunnyIntensity.OFF) return rawText;

        List<String> protectedText = new ArrayList<>();
        String source = protectUrlsAndEmails(rawText, protectedText);
        ReactionCategory category = reactionCategory(source);
        List<ContextScore> contexts = scoreContexts(source);
        ContextScore dominant = contexts.isEmpty() ? new ContextScore(MessageContext.NONE, 0, 0) : contexts.get(0);

        double wordChance = intensity == FunnyIntensity.LOW ? 0.70 : 1.0;
        String text = replaceWords(source, wordChance);
        text = text.replaceAll("(?i)\\bthis\\b", "dis")
                .replaceAll("(?i)\\bthat\\b", "dat")
                .replaceAll("(?i)\\bthink\\b", "tink")
                .replaceAll("(?i)\\bwith\\b", "wif")
                .replaceAll("(?i)\\bwithout\\b", "wifout")
                .replaceAll("(?i)\\bthanks\\b", "fanks~")
                .replaceAll("(?i)\\bplease\\b", "pwease~")
                .replaceAll("(?i)\\bprobably\\b", "pwobabwy")
                .replaceAll("(?i)\\bprobably not\\b", "pwobabwy not~");

        text = playfulPhonetics(text, intensity);
        text = softenEndings(text, intensity);
        text = stretchCuteWords(text, intensity);

        if (intensity != FunnyIntensity.LOW) {
            text = text.replace('r', 'w').replace('l', 'w').replace('R', 'W').replace('L', 'W')
                    .replaceAll("(?i)n([aeiou])", "ny$1")
                    .replaceAll("(?i)\\bthe\\b", "da")
                    .replaceAll("(?i)th", "d");
            text = text.replaceAll("(?i)\\b(you) are\\b", "yuw awe")
                    .replaceAll("(?i)\\b(you) have\\b", "yuw haz")
                    .replaceAll("(?i)\\b(i am)\\b", "i's")
                    .replaceAll("(?i)\\bgoing to\\b", "gonna")
                    .replaceAll("(?i)\\bwant to\\b", "wan ta");
            text = playfulGrammar(text, intensity, category);
            text = expressivePunctuation(text, intensity == FunnyIntensity.HIGH);
        }

        text = contextFlavorTransform(text, contexts, intensity);

        double dominance = dominance(contexts);
        if (intensity == FunnyIntensity.LOW) {
            if (roll(contextChance(0.08, dominance) * 0.75)) {
                text = addContextualEvent(text, dominant.context());
            }
            return restoreProtectedText(maybeTildes(text, 0.45), protectedText);
        }

        if (roll(contextChance(intensity == FunnyIntensity.MEDIUM ? 0.16 : 0.28, dominance))) {
            text = addWeightedContextEvent(text, contexts);
        }

        if (roll(intensity == FunnyIntensity.MEDIUM ? 0.12 : 0.24) && dominant.score() >= 6) {
            text = addContextSpecial(text, dominant.context());
        }

        if (roll(intensity == FunnyIntensity.MEDIUM ? 0.08 : 0.16)) {
            text = addContextualAction(text, category);
        }

        if (roll(intensity == FunnyIntensity.MEDIUM ? 0.05 : 0.11)) {
            text = insertWhimsy(text, pick(GENERAL_WHIMSY));
        }

        if (roll(intensity == FunnyIntensity.MEDIUM ? 0.08 : 0.16)) {
            text = addComboWhimsy(text, contexts);
        }

        if (intensity == FunnyIntensity.HIGH) {
            if (roll(0.24)) text = addActions(text, 0.16);
            if (roll(0.24)) text = stutter(text);
            if (roll(0.18)) text = insertWhimsy(text, pick(HIGH_WHIMSY));
            if (roll(0.035)) text = maximumWhimsy(text, contexts);
            if (roll(0.08) && !contexts.isEmpty()) text = addSecondaryContextWhimsy(text, contexts);
        }

        return restoreProtectedText(addReaction(text), protectedText);
    }

    private static String playfulPhonetics(String text, FunnyIntensity intensity) {
        if (intensity == FunnyIntensity.LOW) {
            return text.replaceAll("(?i)\\b(yes|yeah|yep)\\b", "yesh~")
                    .replaceAll("(?i)\\b(ok|okay)\\b", "okie dokie~")
                    .replaceAll("(?i)\\b(nope)\\b", "nupe~");
        }

        return text.replaceAll("(?i)\\b(yes|yeah|yep)\\b", "yesh~")
                .replaceAll("(?i)\\b(you|u)\\b", "yuw")
                .replaceAll("(?i)\\b(are|r)\\b", "aw")
                .replaceAll("(?i)\\b(really)\\b", "weawwy")
                .replaceAll("(?i)\\b(little)\\b", "wittwe")
                .replaceAll("(?i)\\b(thing|something|anything|nothing|everything)\\b", "fing")
                .replaceAll("(?i)\\b(things)\\b", "fings")
                .replaceAll("(?i)\\b(best)\\b", "bestest")
                .replaceAll("(?i)\\b(big)\\b", "biggy")
                .replaceAll("(?i)\\b(small|tiny)\\b", "smol");
    }

    private static String softenEndings(String text, FunnyIntensity intensity) {
        if (intensity == FunnyIntensity.LOW) return text;
        String result = text
                .replaceAll("(?i)\\b(goin|going)\\b", "goin'")
                .replaceAll("(?i)\\b(doing)\\b", "doin'")
                .replaceAll("(?i)\\b(working)\\b", "workin'")
                .replaceAll("(?i)\\b(running)\\b", "runnin'")
                .replaceAll("(?i)\\b(mining)\\b", "minin'")
                .replaceAll("(?i)\\b(crafting)\\b", "craftin'");

        if (intensity == FunnyIntensity.HIGH && roll(0.20)) {
            result = result.replaceAll("(?i)\\b(very)\\b", "wery, wery");
        }
        return result;
    }

    private static String stretchCuteWords(String text, FunnyIntensity intensity) {
        double chance = intensity == FunnyIntensity.LOW ? 0.06 : intensity == FunnyIntensity.MEDIUM ? 0.12 : 0.22;
        Matcher matcher = WORD_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String word = matcher.group();
            if (word.length() >= 3 && roll(chance)
                    && !word.matches("(?i)(https?|www|da|the)")) {
                String stretched = word;
                if (roll(0.55)) stretched = stretchVowel(stretched);
                if (roll(0.30)) stretched = duplicateLastLetter(stretched);
                matcher.appendReplacement(result, Matcher.quoteReplacement(stretched));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(word));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String stretchVowel(String word) {
        Matcher matcher = Pattern.compile("(?i)([aeiouy])").matcher(word);
        if (!matcher.find()) return word;
        char vowel = matcher.group(1).charAt(0);
        String replacement = "" + vowel + vowel;
        if (roll(0.20)) replacement += vowel;
        return matcher.replaceFirst(Matcher.quoteReplacement(replacement));
    }

    private static String duplicateLastLetter(String word) {
        if (word.length() < 2) return word;
        char last = word.charAt(word.length() - 1);
        return word + last;
    }

    private static String playfulGrammar(String text, FunnyIntensity intensity, ReactionCategory category) {
        if (!roll(intensity == FunnyIntensity.MEDIUM ? 0.18 : 0.30)) return text;

        String result = text
                .replaceAll("(?i)\\bI think (?:that )?", "mwe tink ")
                .replaceAll("(?i)\\bI guess\\b", "mwe guess")
                .replaceAll("(?i)\\bI don't know\\b", "mwe do not know... owo")
                .replaceAll("(?i)\\bI know\\b", "mwe know~")
                .replaceAll("(?i)\\bwait\\b", "w-wait")
                .replaceAll("(?i)\\bwhat\\b", "whawt");

        if (category == ReactionCategory.EXCITED && roll(0.45)) {
            result = result.replaceFirst("^", "very important!! ");
        } else if (category == ReactionCategory.CONFUSED && roll(0.40)) {
            result = result.replaceFirst("^", "tiny question: ");
        } else if (category == ReactionCategory.SAD && roll(0.40)) {
            result = result.replaceFirst("^", "oh nuu... ");
        }
        return result;
    }

    private static String contextFlavorTransform(String text, List<ContextScore> contexts, FunnyIntensity intensity) {
        if (contexts.isEmpty()) return text;

        MessageContext dominant = contexts.get(0).context();
        double chance = intensity == FunnyIntensity.LOW ? 0.10 : intensity == FunnyIntensity.MEDIUM ? 0.20 : 0.32;
        if (!roll(chance)) return text;

        return switch (dominant) {
            case MINING, RARE_LOOT -> text
                    .replaceAll("(?i)\\bfind\b", "discover")
                    .replaceAll("(?i)\\bore\b", "shiny rock")
                    .replaceAll("(?i)\\bdiamond(s)?\\b", "sparkle rock$1");
            case MACHINE, REDSTONE, TECHNICAL -> text
                    .replaceAll("(?i)\\bmachine\b", "fancy box")
                    .replaceAll("(?i)\\berror\b", "forbidden little problem")
                    .replaceAll("(?i)\\bbug(s)?\\b", "wiggly bug$1");
            case EXPLOSION, DANGER -> text
                    .replaceAll("(?i)\\bexplode(d|s)?\\b", "goes boom$1")
                    .replaceAll("(?i)\\bdanger\b", "impending silliness");
            case FOOD -> text
                    .replaceAll("(?i)\\bfood\b", "snackies")
                    .replaceAll("(?i)\\bhungry\b", "tummy rumblin'")
                    .replaceAll("(?i)\\beat\\b", "nom");
            case ANIMAL, MOBS -> text
                    .replaceAll("(?i)\\bcat\b", "kitty")
                    .replaceAll("(?i)\\bdog\b", "doggo")
                    .replaceAll("(?i)\\bcreeper\b", "cweepy")
                    .replaceAll("(?i)\\bskeleton\b", "bonely guy");
            case SERVER_ADMIN, LAG, CHUNK_LOADING -> text
                    .replaceAll("(?i)\\bserver\b", "the smol server")
                    .replaceAll("(?i)\\blag(ging)?\\b", "wigglin'")
                    .replaceAll("(?i)\\bload(ing)?\\b", "thinkin' very hard");
            case BUILDING -> text
                    .replaceAll("(?i)\\bhouse\b", "cozy cube")
                    .replaceAll("(?i)\\bbase\b", "fwiend fortress")
                    .replaceAll("(?i)\\bwall(s)?\\b", "important wall$1");
            case FARMING -> text
                    .replaceAll("(?i)\\bfarm\b", "plant baby factory")
                    .replaceAll("(?i)\\bcrop(s)?\\b", "tiny crop fren$1");
            case FISHING, WATER -> text
                    .replaceAll("(?i)\\bfish(ing)?\\b", "suspicious fish$1")
                    .replaceAll("(?i)\\bwater\b", "the splishy place");
            case PORTAL, NETHER, END, DIMENSION -> text
                    .replaceAll("(?i)\\bportal\b", "spicy doorway")
                    .replaceAll("(?i)\\bnether\b", "spicy dimension")
                    .replaceAll("(?i)\\bthe end\b", "the extremely ominous place");
            case TRADING -> text
                    .replaceAll("(?i)\\bemerald(s)?\\b", "green friendship coupon$1")
                    .replaceAll("(?i)\\bvillager(s)?\\b", "business neighbor$1");
            case SLEEPY, SLEEPING -> text
                    .replaceAll("(?i)\\bsleep\b", "go eepy")
                    .replaceAll("(?i)\\bbed\b", "eepy rectangle");
            case COORDINATES -> text
                    .replaceAll("(?i)\\bcoords?\b", "tiny world directions")
                    .replaceAll("(?i)\\bx\b", "x-ish")
                    .replaceAll("(?i)\\by\b", "y-ish")
                    .replaceAll("(?i)\\bz\b", "z-ish");
            case GREETING -> text.replaceFirst("^", "hewwo fren~ ");
            case GRATITUDE -> text.replaceFirst("$", " fanks fanks~");
            case APOLOGY -> text.replaceFirst("^", "tiny apology incoming: ");
            case QUESTION -> text.replaceFirst("^", "hmm hmm... ");
            case VICTORY, ACHIEVEMENT, DISCOVERY -> text.replaceFirst("^", "☆ BIG NEWS ☆ ");
            case DEATH, FAILURE, COMPLAINT -> text.replaceFirst("^", "oh dear... ");
            case PARANOIA -> text.replaceFirst("^", "*looks around suspiciously* ");
            case WEATHER -> text.replaceAll("(?i)\\b(rain|raining)\\b", "sky soup")
                    .replaceAll("(?i)\\b(thunder)\\b", "sky bonk");
            case TIME_OF_DAY -> text.replaceFirst("^", "according to the tiny clock~ ");
            case SOCIAL -> text.replaceFirst("^", "fwiendly bulletin: ");
            default -> text;
        };
    }

    private static double contextChance(double base, double dominance) {
        return Math.min(0.95, base * (0.65 + dominance * 0.8));
    }

    private static double dominance(List<ContextScore> contexts) {
        if (contexts.isEmpty()) return 0.0;
        int total = contexts.stream().mapToInt(ContextScore::score).sum();
        return total == 0 ? 0.0 : (double) contexts.get(0).score() / total;
    }

    private static List<ContextScore> scoreContexts(String text) {
        List<ContextScore> scores = new ArrayList<>();
        String normalized = text.toLowerCase(Locale.ROOT);

        for (ContextRule rule : CONTEXT_RULES) {
            Matcher matcher = rule.pattern().matcher(normalized);
            int hits = 0;
            while (matcher.find()) hits++;
            if (hits == 0) continue;

            int score = rule.baseWeight() * Math.min(hits, 3);
            if (hits > 3) score += (hits - 3) * Math.max(1, rule.baseWeight() / 3);
            scores.add(new ContextScore(rule.context(), score, hits));
        }

        if (QUESTION_MARK.matcher(normalized).find() || CONFUSION_KEYWORDS.matcher(normalized).find()) {
            scores.add(new ContextScore(MessageContext.QUESTION, 5, 1));
        }
        if (COMMAND_PREFIX.matcher(normalized).find()) {
            scores.add(new ContextScore(MessageContext.COMMAND, 5, 1));
        }

        applySynergies(scores, normalized);
        scores.sort(Comparator.comparingInt(ContextScore::score).reversed());
        return scores;
    }

    private static ContextRule rule(MessageContext context, int weight, String... words) {
        StringBuilder regex = new StringBuilder();
        regex.append("(?i)(?<!\\w)(?:");
        for (int i = 0; i < words.length; i++) {
            if (i > 0) regex.append('|');
            String word = words[i];
            if (word.startsWith("\\")) {
                regex.append(word);
            } else {
                regex.append(Pattern.quote(word).replace(" ", "\\E\\s+\\Q"));
            }
        }
        regex.append(")(?!\\w)");
        return new ContextRule(context, Pattern.compile(regex.toString()), weight);
    }

    private static void applySynergies(List<ContextScore> scores, String text) {
        for (ContextPair pair : CONTEXT_SYNERGIES) {
            if (!hasContext(scores, pair.first()) || !hasContext(scores, pair.second())) continue;
            boost(scores, pair.first(), pair.bonus());
            boost(scores, pair.second(), pair.bonus() / 2);
        }

        if (hasContext(scores, MessageContext.DISCOVERY) && hasContext(scores, MessageContext.RARE_LOOT)
                && (text.contains("finally") || text.contains("at last"))) {
            boost(scores, MessageContext.DISCOVERY, 8);
        }
        if (hasContext(scores, MessageContext.EXPLOSION) && hasContext(scores, MessageContext.LAG)) {
            boost(scores, MessageContext.SERVER_ADMIN, 7);
        }
        if (hasContext(scores, MessageContext.QUESTION) && hasContext(scores, MessageContext.TECHNICAL)) {
            boost(scores, MessageContext.TECHNICAL, 5);
        }
        if (hasContext(scores, MessageContext.GREETING) && hasContext(scores, MessageContext.GOODBYE)) {
            boost(scores, MessageContext.SOCIAL, 5);
        }
    }

    private static boolean hasContext(List<ContextScore> scores, MessageContext context) {
        return scores.stream().anyMatch(score -> score.context() == context);
    }

    private static void boost(List<ContextScore> scores, MessageContext context, int amount) {
        for (int i = 0; i < scores.size(); i++) {
            ContextScore score = scores.get(i);
            if (score.context() == context) {
                scores.set(i, new ContextScore(context, score.score() + amount, score.hits()));
                return;
            }
        }
    }

    private static String addWeightedContextEvent(String text, List<ContextScore> contexts) {
        if (contexts.isEmpty()) return insertWhimsy(text, pick(GENERAL_WHIMSY));
        ContextScore chosen = weightedContext(contexts);
        return addContextualEvent(text, chosen.context());
    }

    private static ContextScore weightedContext(List<ContextScore> contexts) {
        int total = contexts.stream()
                .limit(5)
                .mapToInt(score -> score.score() * score.score())
                .sum();
        if (total <= 0) return contexts.get(0);

        int roll = random().nextInt(total);
        for (ContextScore score : contexts.stream().limit(5).toList()) {
            roll -= score.score() * score.score();
            if (roll < 0) return score;
        }
        return contexts.get(0);
    }

    private static String addContextSpecificWhimsy(String text, MessageContext context) {
        return addContextualEvent(text, context);
    }

    private static String addContextSpecial(String text, MessageContext context) {
        return switch (context) {
            case EXPLOSION -> insertWhimsy(text, pick(
                    "*the crater has been declared an abstract art piece*",
                    "*the blast report is somehow 14 pages long*",
                    "*everyone has agreed not to ask what happened*"));
            case RARE_LOOT -> insertWhimsy(text, pick(
                    "*the treasure goblin puts on a monocle*",
                    "*velvet rope has been deployed around the loot*",
                    "*the valuables department is screaming quietly*"));
            case LAG, CHUNK_LOADING -> insertWhimsy(text, pick(
                    "*the server hamster has requested a union representative*",
                    "*the loading bar is doing its best and deserves encouragement*",
                    "*one packet has been personally escorted by a duck*"));
            case MACHINE -> insertWhimsy(text, pick(
                    "*the maintenance goblin taps the machine twice and says 'there'*",
                    "*a tiny engineer records this as a totally normal sound*",
                    "*the machine's warranty becomes visibly nervous*"));
            case MINING -> insertWhimsy(text, pick(
                    "*a suspicious rock has been promoted to suspect number one*",
                    "*the cave has started keeping a visitor log*",
                    "*the mining inspector whispers 'shiny' very professionally*"));
            case FISHING -> insertWhimsy(text, pick(
                    "*the fish have formed a negotiation committee*",
                    "*the water goes suspiciously still for dramatic effect*",
                    "*a tiny fisherman salutes the bobber*"));
            case BUILDING -> insertWhimsy(text, pick(
                    "*the tiny architect has Opinions about that roof*",
                    "*one block is moved 0.3 centimeters for aesthetic reasons*",
                    "*the blueprint acquires seventeen tiny arrows*"));
            case TRADING -> insertWhimsy(text, pick(
                    "*the emerald accountant checks the invoice twice*",
                    "*the villager has requested legal counsel*",
                    "*one potato is now technically a financial asset*"));
            case PORTAL, NETHER, END, DIMENSION -> insertWhimsy(text, pick(
                    "*the dimensional paperwork is stamped with unreasonable enthusiasm*",
                    "*reality makes a tiny administrative noise*",
                    "*the interdimensional crossing receives a safety sticker*"));
            case MOBS, PARANOIA -> insertWhimsy(text, pick(
                    "*slowly looks toward the nearest suspicious corner*",
                    "*the tiny security team advances in a line of two*",
                    "*someone whispers 'we saw it too' and refuses to elaborate*"));
            case FOOD -> insertWhimsy(text, pick(
                    "*the snack committee has reached DEFCON COOKIE*",
                    "*the potato department approves this development*",
                    "*a tiny chef materializes with a wooden spoon*"));
            case SLEEPY, SLEEPING -> insertWhimsy(text, pick(
                    "*the sentence is gently tucked into bed*",
                    "*the tiny bedtime goblin lowers the lights*",
                    "*one sleepy moth turns the sign to 'closed'*"));
            case QUESTION -> insertWhimsy(text, pick(
                    "*the answer committee looks at each other nervously*",
                    "*the tiny owl underlines the question three times*",
                    "*someone opens a book upside down with confidence*"));
            default -> text;
        };
    }

    private static String addSecondaryContextWhimsy(String text, List<ContextScore> contexts) {
        if (contexts.size() < 2) return text;
        ContextScore secondary = contexts.get(1);
        if (secondary.score() < Math.max(4, contexts.get(0).score() / 3)) return text;
        return addContextSpecial(text, secondary.context());
    }

    private static String addComboWhimsy(String text, List<ContextScore> contexts) {
        if (contexts.size() < 2) return text;

        MessageContext a = contexts.get(0).context();
        MessageContext b = contexts.get(1).context();
        String combo = comboEvent(a, b);
        if (combo == null) return text;
        return insertWhimsy(text, combo);
    }

    private static String comboEvent(MessageContext a, MessageContext b) {
        if (samePair(a, b, MessageContext.EXPLOSION, MessageContext.MACHINE))
            return "*the tiny engineers have unanimously agreed to stop touching the reactor*";
        if (samePair(a, b, MessageContext.EXPLOSION, MessageContext.BUILDING))
            return "*the architect looks at the crater, then quietly updates the blueprint*";
        if (samePair(a, b, MessageContext.EXPLOSION, MessageContext.DEATH))
            return "*the respawn department sends the explosion a strongly worded memo*";
        if (samePair(a, b, MessageContext.LAG, MessageContext.SERVER_ADMIN))
            return "*the server hamster submits a formal performance complaint*";
        if (samePair(a, b, MessageContext.LAG, MessageContext.CHUNK_LOADING))
            return "*the chunks are encouraged to arrive one at a time and use the stairs*";
        if (samePair(a, b, MessageContext.DISCOVERY, MessageContext.RARE_LOOT))
            return "*the treasure goblin rings a bell and puts on ceremonial gloves*";
        if (samePair(a, b, MessageContext.DISCOVERY, MessageContext.MINING))
            return "*the cave receives a very smug visitor badge*";
        if (samePair(a, b, MessageContext.FISHING, MessageContext.WATER))
            return "*the fish union asks that negotiations remain peaceful*";
        if (samePair(a, b, MessageContext.BUILDING, MessageContext.REDSTONE))
            return "*the tiny architect and tiny engineer argue politely about wire placement*";
        if (samePair(a, b, MessageContext.PORTAL, MessageContext.DANGER))
            return "*the portal inspector slowly adds three more warning stickers*";
        if (samePair(a, b, MessageContext.NETHER, MessageContext.RARE_LOOT))
            return "*the netherite tax collector appears from nowhere*";
        if (samePair(a, b, MessageContext.MOBS, MessageContext.PARANOIA))
            return "*the security team begins checking corners in alphabetical order*";
        if (samePair(a, b, MessageContext.ACHIEVEMENT, MessageContext.VICTORY))
            return "*a tiny trophy is presented with far too much ceremony*";
        if (samePair(a, b, MessageContext.FOOD, MessageContext.FARMING))
            return "*the harvest committee immediately starts planning sandwiches*";
        return null;
    }

    private static boolean samePair(MessageContext a, MessageContext b, MessageContext one, MessageContext two) {
        return (a == one && b == two) || (a == two && b == one);
    }

    private static String addContextualEvent(String text, MessageContext context) {
        List<String> events = CONTEXT_EVENTS.get(context);
        if (events == null || events.isEmpty()) return insertWhimsy(text, pick(GENERAL_WHIMSY));
        return insertWhimsy(text, pick(events.toArray(String[]::new)));
    }

    private static String addContextualAction(String text, ReactionCategory category) {
        List<String> actions = switch (category) {
            case EXCITED -> List.of("*happy wiggle intensifies*", "*throws imaginary confetti*", "*tiny celebratory spin*",
                    "*produces a party horn that is much too loud*");
            case CONFUSED -> List.of("*pulls out a comically large diagram*", "*consults the ancient scrolls*",
                    "*slowly turns the situation around in their paws*", "*squints at reality*");
            case NEUTRAL -> List.of("*nods very seriously*", "*places one tiny pebble on the table*",
                    "*quietly adjusts the ambience*", "*vibes with administrative precision*");
            case SAD -> List.of("*offers a tiny blanket*", "*scoots over and makes room*",
                    "*deploys emergency emotional support cookie*", "*gently pats the situation*");
        };
        return insertWhimsy(text, pick(actions.toArray(String[]::new)));
    }

    private static String addTinyWhimsy(String text) {
        return insertWhimsy(text, pick(GENERAL_WHIMSY));
    }

    private static String maximumWhimsy(String text, List<ContextScore> contexts) {
        String result = insertWhimsy(text, pick(HIGH_WHIMSY));
        if (!contexts.isEmpty()) result = addContextualEvent(result, contexts.get(0).context());
        return addContextualAction(result, reactionCategory(result));
    }

    private static String addReaction(String text) {
        Reaction reaction = reaction(reactionCategory(text));
        String reactionText = "§o" + reaction.suffix() + "§r " + reaction.face();
        double roll = random().nextDouble();
        if (roll < 0.14) return reaction.face() + " §o" + reaction.suffix() + "§r " + text;
        if (roll < 0.29) return insertWhimsy(text, reactionText);
        return text + " " + reactionText;
    }

    private static String insertWhimsy(String text, String whimsy) {
        if (text.isBlank()) return whimsy;
        String[] words = text.split("\\s+");
        if (words.length < 2 || roll(0.62)) {
            return text + " §o" + whimsy + "§r";
        }

        int split = 1 + random().nextInt(words.length - 1);
        StringBuilder result = new StringBuilder(text.length() + whimsy.length() + 4);
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(' ');
            if (i == split) result.append("§o").append(whimsy).append("§r ");
            result.append(words[i]);
        }
        return result.toString();
    }

    private static String replaceWords(String text, double chance) {
        Map<String, String> words = UWU_WORDS;
        Matcher matcher = WORD_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String original = matcher.group();
            String replacement = words.get(original.toLowerCase(Locale.ROOT));
            if (replacement == null || random().nextDouble() > chance) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(original));
                continue;
            }
            if (Character.isUpperCase(original.charAt(0))) {
                replacement = Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String protectUrlsAndEmails(String text, List<String> protectedText) {
        Matcher matcher = URL_OR_EMAIL_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            protectedText.add(matcher.group());
            matcher.appendReplacement(result, Matcher.quoteReplacement("\u0000" + (protectedText.size() - 1) + "\u0000"));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String restoreProtectedText(String text, List<String> protectedText) {
        for (int i = 0; i < protectedText.size(); i++) {
            text = text.replace("\u0000" + i + "\u0000", protectedText.get(i));
        }
        return text;
    }

    private static String expressivePunctuation(String text, boolean strong) {
        if (!strong) return text;
        text = text.replaceAll("!+", random().nextBoolean() ? "!!" : "!!1!");
        return text.replaceAll("\\?+", "?!");
    }

    private static String addActions(String text, double chance) {
        Matcher matcher = Pattern.compile("(?<=\\S)\\s+(?=\\S)").matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group();
            if (roll(chance)) replacement += pick(ACTIONS) + " ";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String stutter(String text) {
        Matcher matcher = STUTTER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String word = matcher.group();
            String replacement = roll(0.20) ? matcher.group(1) + "-" + word : word;
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static ReactionCategory reactionCategory(String text) {
        Matcher happy = HAPPY_KEYWORDS.matcher(text);
        Matcher distress = DISTRESS_KEYWORDS.matcher(text);
        int happyCount = 0;
        int distressCount = 0;
        while (happy.find()) happyCount++;
        while (distress.find()) distressCount++;

        if (distressCount > happyCount) return ReactionCategory.SAD;
        if (happyCount > distressCount || (text.contains("!") && happyCount >= distressCount)) {
            return ReactionCategory.EXCITED;
        }
        return CONFUSION_KEYWORDS.matcher(text).find() || QUESTION_MARK.matcher(text).find()
                ? ReactionCategory.CONFUSED : ReactionCategory.NEUTRAL;
    }

    private static Reaction reaction(ReactionCategory category) {
        List<Reaction> reactions = REACTIONS.get(category);
        int totalWeight = reactions.stream().mapToInt(Reaction::weight).sum();
        int roll = random().nextInt(totalWeight);
        for (Reaction reaction : reactions) {
            roll -= reaction.weight();
            if (roll < 0) return reaction;
        }
        return reactions.get(reactions.size() - 1);
    }

    private static String maybeTildes(String text, double chance) {
        return roll(chance) ? text.replaceAll("([,.])(?=\\s|$)", "$1~") : text;
    }

    private static String randomMessage(String[] messages) {
        return messages[random().nextInt(messages.length)];
    }

    private static String pick(String... options) {
        return options[random().nextInt(options.length)];
    }

    private static boolean roll(double chance) {
        return random().nextDouble() < chance;
    }

    private static Random random() {
        Random random = TRANSFORM_RANDOM.get();
        return random != null ? random : ThreadLocalRandom.current();
    }

    private static Component announcement(String message, String name,
                                          ChatFormatting nameColor, ChatFormatting textColor) {
        String[] parts = message.split("%s", 2);
        return Component.literal(parts[0]).withStyle(textColor)
                .append(Component.literal(name).withStyle(nameColor))
                .append(Component.literal(parts[1]).withStyle(textColor));
    }

    private static final Map<String, String> UWU_WORDS = Map.ofEntries(
            Map.entry("hello", "hewwo~"), Map.entry("hi", "haii~"), Map.entry("hey", "henwo~"),
            Map.entry("friend", "fwiend"), Map.entry("love", "wuv"), Map.entry("cute", "kawaii~"),
            Map.entry("little", "wittwe"), Map.entry("baby", "babu~"), Map.entry("you're", "uwu awe~"),
            Map.entry("you", "yuw"), Map.entry("yours", "yuws"), Map.entry("my", "muh"),
            Map.entry("me", "m-meh"), Map.entry("good", "gud~"), Map.entry("okay", "okie dokie~"),
            Map.entry("no", "nuuu~"), Map.entry("stop", "stahp~"), Map.entry("really", "weawwy"),
            Map.entry("want", "wan"), Map.entry("don't", "dun"), Map.entry("can't", "canny"),
            Map.entry("do", "du"), Map.entry("have", "haz"), Map.entry("please", "pwease~"),
            Map.entry("thanks", "fanks~"), Map.entry("thank", "fank"), Map.entry("sorry", "sowwy"),
            Map.entry("because", "becaws"), Map.entry("about", "abowt"), Map.entry("around", "awound"),
            Map.entry("probably", "pwobabwy"), Map.entry("very", "vewy"), Map.entry("great", "gwate~"),
            Map.entry("right", "wight"), Map.entry("wrong", "wong"), Map.entry("friendship", "fwiendship"),
            Map.entry("everyone", "evewyone"), Map.entry("something", "somefing"), Map.entry("nothing", "nofing"),
            Map.entry("anything", "anyfing"), Map.entry("everything", "evewyfing"), Map.entry("thing", "fing"),
            Map.entry("things", "fings"), Map.entry("help", "hewp"), Map.entry("awesome", "awesomesauce~"),
            Map.entry("helping", "hewpin'"), Map.entry("yes", "yesh~"), Map.entry("yeah", "yeh~"));

    private static final String[] JOIN_MESSAGES = {
            "*~* Hewwo, %s! The world was waiting for you~",
            "A soft sparkle announces %s has arrived! *^w^*",
            "%s has entered the world! Let the cozy adventures begin~",
            "Make room for our new fwiend, %s!",
            "Ding ding! %s has joined the adventure party~",
            "A wild %s appeared! Please give them a warm welcome~",
            "%s has popped into our little world. How adorable~",
            "Welcome back to the land of fun, %s!",
            "The welcome committee has been alerted: %s is here!",
            "A tiny confetti cannon has announced the arrival of %s!",
            "%s has materialized with suspiciously good timing~",
            "The server has detected friend-shaped activity. It is %s!",
            "%s has wandered back into the cozy zone. Nobody panic~",
            "Quick! Hide the ceremonial cookies! %s has arrived!",
            "%s has entered the chat dimension. Please provide one complimentary sparkle.",
            "The tiny town bell rings for %s! Ding ding~",
            "%s has returned from the mysterious elsewhere-place!",
            "A small wizard points dramatically toward %s and declares: FRIEND.",
            "%s has arrived! The local ducks have been informed.",
            "A tiny bureaucrat has stamped the paperwork: %s may now enter.",
            "%s has joined us! Please keep hands, paws, and important machinery inside the server.",
            "The adventure party has gained one (1) %s!",
            "%s just crossed the threshold. Someone gave them a welcome sticker.",
            "The world makes a tiny *boing* as %s arrives~",
            "%s has been gently deposited into the adventure dimension.",
            "A mysterious rustling... a sparkle... and behold: %s!",
            "%s has arrived. The nearest pebble is already emotionally invested.",
            "Welcome, %s! The tiny accountant has added you to the friendship ledger.",
            "%s has spawned! The local frog would like to say hello.",
            "The server softly whispers: \"oh good, %s is here\"",
            "%s has appeared! Somewhere, a tiny trumpet plays a single heroic note.",
            "A warm little breeze announces that %s has come wandering in~",
            "%s has arrived carrying absolutely no explanation and one tiny amount of whimsy.",
            "The gates are open! %s has entered the realm of snacks and questionable decisions.",
            "%s has joined the adventure! Please assign them a complimentary emotional support potato.",
            "The stars briefly rearrange themselves to spell: %s is here.",
            "%s has returned! The friendship reserves have been replenished.",
            "A ceremonial spoon has been raised in honor of %s's arrival.",
            "%s appears at the horizon, accompanied by the sound of tiny footsteps.",
            "The Department of Tiny Entrances confirms that %s has arrived safely."
    };

    private static final String[] FIRST_JOIN_MESSAGES = {
            "*~* A brand-new adventurer, %s, has arrived! Welcome~",
            "%s is here for the vewy first time! Everybody say haii~",
            "The world gains a new fwiend today: %s!",
            "A fresh face has appeared! Welcome to your new home, %s~",
            "First adventure unlocked for %s! Let us make it extra cozy~",
            "Please welcome %s to the party! They are new and adorable~",
            "A tiny star named %s has joined our sky of adventures~",
            "Hewwo, %s! Your first visit starts with maximum sparkles *^w^*",
            "Attention, everyone! A brand-new fwiend called %s has arrived!",
            "%s has taken their first tiny step into the blocky wilderness~",
            "The welcome wagon rolls up for %s! It has snacks.",
            "A new adventurer appears! The server's tiny wizard has selected %s for maximum cozy potential.",
            "%s has opened the door to their new adventure. Please offer them one ceremonial cookie.",
            "A mysterious sparkle points toward %s. The prophecy begins...",
            "%s has arrived for the first time! The friendship ledger has a fresh page ready.",
            "Behold! %s has crossed into the realm. The ducks approve.",
            "The world has gained a new little star: %s~",
            "%s is new here! Someone deploy the tiny welcome committee!",
            "A wild newcomer named %s has appeared! Please do not scare them with the creepers.",
            "%s has joined for the first time. The local pebble would like to say congratulations.",
            "The first-login bells are ringing for %s! Ding ding ding~",
            "%s begins their very first adventure! May their inventory contain fewer potatoes than expected.",
            "A fresh adventurer has spawned: %s. Please admire responsibly.",
            "%s has arrived from the mysterious land known as \"somewhere else\". Welcome~",
            "The server has prepared a tiny badge for %s. It says: HELLO FWIEND.",
            "%s has entered the realm for the first time! The cozy department is extremely pleased.",
            "A little fanfare for %s! *tiny trumpet noises*",
            "%s has taken their first step into our world. Somewhere, a frog quietly cheers.",
            "Welcome, %s! The Tiny Chat Wizard has been expecting you for approximately seven business days.",
            "%s's first adventure begins now! Please remember to wave at the moon.",
            "The server gains a new sparkle today: %s!",
            "%s has arrived! The ceremonial confetti has been deployed with minimal property damage.",
            "A fresh face joins the party: %s. The friendship committee is already making snacks.",
            "%s has entered the world! The nearest three woodland creatures have filed a welcome report."
    };

    private static final String[] LEAVE_MESSAGES = {
            "%s has slipped away for now. Come back soon, fwiend~",
            "The world feels quieter... %s has gone offline.",
            "A soft breeze carries %s away until next time~",
            "%s has wandered home. We will save some fun for them!",
            "Goodbye, %s! May your dreams be full of cozy adventures~",
            "%s has vanished into the sunset. Farewell, little star~",
            "The party is one fwiend smaller: %s has left us.",
            "See you later, %s! The world will be waiting right here~",
            "%s has gently logged off and entered the realm of snacks.",
            "A tiny farewell bell rings as %s wanders away~ ding...",
            "%s has departed! The ducks have been instructed to wave.",
            "The server watches %s disappear into the distance and gives a tiny salute.",
            "%s has gone home for now. Someone save their favorite chair!",
            "%s has left the adventure party. The friendship ledger records a dramatic little sniffle.",
            "A soft *poof* announces that %s has vanished for now.",
            "%s has gone offline. The local pebble will remember them.",
            "The tiny farewell committee escorts %s toward the exit with snacks.",
            "%s has wandered beyond the server's tiny horizon. Safe travels, fwiend~",
            "%s has left us! Somewhere, a small wizard closes a tiny book in solidarity.",
            "The adventure pauses for %s. Their spot in the cozy zone remains reserved.",
            "%s has returned to the mysterious elsewhere-place. The moon waves goodbye.",
            "%s has departed. The party loses one tiny sparkle until their return.",
            "A miniature parade follows %s to the exit. It consists of one duck.",
            "%s has logged off! The ceremonial spoon is lowered respectfully.",
            "The world whispers a tiny \"see you soon\" as %s heads out~",
            "%s has gone to touch grass. The grass sends its regards.",
            "%s has left the realm! Please do not disturb the tiny sleeping goblin guarding their spot.",
            "The server has placed a little invisible bookmark where %s was standing.",
            "%s has wandered home. The emotional support potato has been safely returned.",
            "%s has disappeared! The Department of Tiny Departures has filed Form 7-B.",
            "A small star drifts away as %s logs off. See you next adventure~",
            "%s has slipped through the logout door. It closes with a polite little click.",
            "%s has gone offline. The welcome banner has been folded up for later.",
            "%s has left the server! The nearest frog gives one solemn nod.",
            "Farewell, %s~ May your next login be accompanied by fewer surprises and more cookies.",
            "%s has departed. Three invisible mice wave from the windowsill.",
            "The cozy zone has one fewer fwiend: %s has headed out.",
            "%s has logged off. Somewhere, a tiny bureaucrat checks the departure box.",
            "%s has gone for now. The server leaves a little sparkle behind in their place.",
            "%s has vanished into the sunset, pursued by absolutely no ducks whatsoever."
    };
}
