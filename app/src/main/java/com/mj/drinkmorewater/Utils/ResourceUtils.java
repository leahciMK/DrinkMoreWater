package com.mj.drinkmorewater.Utils;

import com.google.android.gms.common.util.Strings;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 *
 *
 * @see com.mj.drinkmorewater.components.resources.CoreResourceBundleManager
 */
public class ResourceUtils {
    private static final Logger log = Logger.getLogger(ResourceUtils.class.getName());

    public static final String MESSAGES = "com.mj.drinkmorewater.components.core_messages";
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale SLOVENIAN = new Locale("sl");
    public static final Locale SLOVENIA = new Locale("sl", "SI");
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static Locale[] getSupportedLocales() {
        Locale[] availableLocales = Locale.getAvailableLocales();
        Set<Locale> supportedLocalesList = new HashSet<>(Collections.singleton(Locale.ENGLISH));

        for (Locale locale : availableLocales) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(MESSAGES, locale);
                if (bundle != null && !Strings.isEmptyOrWhitespace(bundle.getLocale().getLanguage())) {
                    supportedLocalesList.add(bundle.getLocale());
                }
            } catch (MissingResourceException e) {
                // ignore
            }
        }
        if (!supportedLocalesList.isEmpty()) {
            Locale[] supportedLocalesArray = new Locale[supportedLocalesList.size()];
            supportedLocalesList.toArray(supportedLocalesArray);
            return supportedLocalesArray;
        } else {
            return availableLocales;
        }
    }

    public static List<String> getListOfSupportedLanguages() {
        Locale[] supportedLocales = getSupportedLocales();
        Arrays.sort(supportedLocales, (a, b) -> a.toString().compareTo(b.toString()));
        final List<String> supportedLocalesStrings =
                Arrays.stream(supportedLocales).map(locale1 -> locale1.getLanguage()).collect(Collectors.toList());
        return supportedLocalesStrings;
    }
}
