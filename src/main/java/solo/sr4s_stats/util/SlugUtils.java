package solo.sr4s_stats.util;

import java.text.Normalizer;

public final class SlugUtils {
    private SlugUtils() {}

    public static String toSlug(String name) {
        if (name == null || name.isBlank()) return null;
        String normalized = Normalizer.normalize(name.trim(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "")
                         .replaceAll("[^a-zA-Z0-9]", "");
    }
}
