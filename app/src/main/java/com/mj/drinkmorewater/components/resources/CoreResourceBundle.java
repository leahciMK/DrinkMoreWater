package com.mj.drinkmorewater.components.resources;
import com.mj.drinkmorewater.Utils.ResourceUtils;

import java.util.Locale;

public class CoreResourceBundle extends CoreResourceBundleManager{

    public static final String BUNDLE_NAME = "com.mj.drinkmorewater.components.core_messages";

    public CoreResourceBundle() {
    }

    private CoreResourceBundle(Locale locale, ClassLoader classLoader) {
        super(BUNDLE_NAME, locale, classLoader);
    }

    public static CoreResourceBundle getDefaultInstance() {
        return getInstance(ResourceUtils.DEFAULT_LOCALE);
    }

    public static CoreResourceBundle getInstance(Locale locale) {
        return getInstance(locale, null);
    }

    public static CoreResourceBundle getInstance(Locale locale, ClassLoader classLoader) {
        return new CoreResourceBundle(locale, classLoader);
    }

}
