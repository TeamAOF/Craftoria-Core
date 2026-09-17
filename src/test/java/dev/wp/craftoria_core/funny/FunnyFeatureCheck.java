package dev.wp.craftoria_core.funny;

/** Self-check for the chat transform. Run with {@code ./gradlew funnyCheck}. */
public final class FunnyFeatureCheck {
    private static final int ITERATIONS = 20_000;

    private FunnyFeatureCheck() {
    }

    public static void main(String[] args) {
        urlSurvivesTransform();
        noSentinelsLeak();
        outputStaysBounded();
        shortMessagesAreNotBuried();
        contextFlavorActuallyFires();
        System.out.println("FunnyFeature self-check passed (" + ITERATIONS + " iterations per case).");
    }

    private static void urlSurvivesTransform() {
        String url = "https://example.com/wiki/Ore_Processing";
        String raw = "check out " + url + " for the ore setup";
        for (FunnyIntensity intensity : withoutOff()) {
            for (int i = 0; i < ITERATIONS; i++) {
                String out = FunnyFeature.transform(raw, intensity);
                check(out.contains(url), intensity + " lost the URL: " + out);
            }
        }
    }

    private static void noSentinelsLeak() {
        String raw = "mail me at someone@example.com or see http://a.example/b";
        for (FunnyIntensity intensity : withoutOff()) {
            for (int i = 0; i < ITERATIONS; i++) {
                String out = FunnyFeature.transform(raw, intensity);
                check(out.indexOf('\0') < 0, intensity + " leaked a sentinel: " + out);
            }
        }
    }

    private static void outputStaysBounded() {
        String raw = "we finally found the ancient debris after the creeper exploded the reactor "
                + "and the server started lagging so we went mining near the portal instead ok";
        int limit = Math.max(512, raw.length() * 2 + 120);
        for (int i = 0; i < ITERATIONS; i++) {
            String out = FunnyFeature.transform(raw, FunnyIntensity.HIGH);
            check(out.length() <= limit, "output ran long (" + out.length() + " > " + limit + "): " + out);
        }
    }

    private static void shortMessagesAreNotBuried() {
        for (int i = 0; i < ITERATIONS; i++) {
            String out = FunnyFeature.transform("hi fren", FunnyIntensity.HIGH);
            check(out.length() < 200, "short message was buried (" + out.length() + "): " + out);
        }
    }

    private static void contextFlavorActuallyFires() {
        boolean fired = false;
        for (int i = 0; i < ITERATIONS && !fired; i++) {
            fired = FunnyFeature.transform("so many diamonds in this ore vein", FunnyIntensity.LOW)
                    .contains("shiny rock");
        }
        check(fired, "MINING flavor never fired in " + ITERATIONS + " runs - the dictionary is dead again");
    }

    private static FunnyIntensity[] withoutOff() {
        return new FunnyIntensity[]{FunnyIntensity.LOW, FunnyIntensity.MEDIUM, FunnyIntensity.HIGH};
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
