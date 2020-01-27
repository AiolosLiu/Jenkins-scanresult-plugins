package io.jenkins.plugins.config;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class ScannerConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static ScannerConfiguration get() {
        return GlobalConfiguration.all().get(ScannerConfiguration.class);
    }

    private String label;

    public ScannerConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /** @return the currently configured label, if any */
    public String getLabel() {
        return label;
    }

    /**
     * Together with {@link #getLabel}, binds to entry in {@code config.jelly}.
     * @param label the new value of this field
     */
    @DataBoundSetter
    public void setLabel(String label) {
        this.label = label;
        save();
    }

    public FormValidation doCheckLabel(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a label.");
        }
        return FormValidation.ok();
    }

}
