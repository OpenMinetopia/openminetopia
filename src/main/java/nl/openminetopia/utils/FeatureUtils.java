package nl.openminetopia.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;

/**
 * Utility class for feature checks
 */
@UtilityClass
public class FeatureUtils {

    /**
     * Check if a feature is enabled
     * @param featureName The name of the feature to check
     * @return true if the feature is enabled, false otherwise
     */
    public static boolean isFeatureEnabled(String featureName) {
        if (OpenMinetopia.getDefaultConfiguration() == null) {
            return true; // Default to enabled if config is not loaded
        }
        return OpenMinetopia.getDefaultConfiguration().isFeatureEnabled(featureName);
    }

    /**
     * Check if a feature is disabled
     * @param featureName The name of the feature to check
     * @return true if the feature is disabled, false otherwise
     */
    public static boolean isFeatureDisabled(String featureName) {
        return !isFeatureEnabled(featureName);
    }
}
