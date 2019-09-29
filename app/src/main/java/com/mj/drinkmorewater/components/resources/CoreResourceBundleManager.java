package com.mj.drinkmorewater.components.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.core.util.Preconditions;

public abstract class CoreResourceBundleManager {
    private static final Logger log = Logger.getLogger(CoreResourceBundleManager.class.getName());

    private ClassLoader classLoader;
    private String bundleBaseName;
    private Locale locale;

    public CoreResourceBundleManager() {

    }

    protected CoreResourceBundleManager(String bundleBaseName, Locale locale, ClassLoader classLoader) {
        Preconditions.checkNotNull(bundleBaseName);
        this.bundleBaseName = bundleBaseName;
        this.locale = Optional.ofNullable(locale).orElse(Locale.ENGLISH);
        this.classLoader = classLoader;
    }

    public String getMessage(String label, Object... arguments) {
        String messagesResource = getMessageSafe(label);
        if (arguments == null || arguments.length == 0) {
            return messagesResource;
        } else {
            return MessageFormat.format(getMessageSafe(label), arguments);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public static String getMessage(String label, String bundleBaseName) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleBaseName);
            return bundle.getString(label);
        } catch (MissingResourceException e) {
            log.log(Level.WARNING, String.format("%s", e.getMessage()));
            return label;
        }
    }

    private String getMessageSafe(String label) {
        try {
            return getBundle().getString(label);
        } catch (MissingResourceException e) {
            log.log(Level.WARNING, String.format("%s", e.getMessage()));
            return label;
        }
    }

    private ResourceBundle getBundle() {
        if (classLoader == null) {
            return ResourceBundle.getBundle(bundleBaseName);
        } else {
            return ResourceBundle.getBundle(bundleBaseName, locale, classLoader);
        }
    }
}
