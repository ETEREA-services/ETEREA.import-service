package eterea.migration.api.rest.service.internal;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern DIACRITICS_AND_FRIENDS
            = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    public static String stripDiacritics(String str) {
        if (str == null) {
            return null;
        }
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return DIACRITICS_AND_FRIENDS.matcher(normalized).replaceAll("");
    }
}
