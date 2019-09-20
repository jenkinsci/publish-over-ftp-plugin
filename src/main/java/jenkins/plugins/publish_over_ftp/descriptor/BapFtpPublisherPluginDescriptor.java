/*
 * The MIT License
 *
 * Copyright (C) 2010-2011 by Anthony Robinson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.publish_over_ftp.descriptor;

import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.BPBuildInfo;
import jenkins.plugins.publish_over.BPInstanceConfig;
import jenkins.plugins.publish_over.BPPlugin;
import jenkins.plugins.publish_over.BPPluginDescriptor;
import jenkins.plugins.publish_over_ftp.BapFtpHostConfiguration;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.Messages;
import jenkins.plugins.publish_over_ftp.options.FtpDefaults;
import jenkins.plugins.publish_over_ftp.options.FtpPluginDefaults;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.List;

@SuppressWarnings("PMD.TooManyMethods")
public class BapFtpPublisherPluginDescriptor extends BuildStepDescriptor<Publisher> {

    /** prevent complaints from XStream */
    @Deprecated private transient BPPluginDescriptor.BPDescriptorMessages msg;
    /** prevent complaints from XStream */
    @Deprecated private transient Class hostConfigClass;
    private final CopyOnWriteList<BapFtpHostConfiguration> hostConfigurations = new CopyOnWriteList<>();
    private FtpDefaults defaults;

    public BapFtpPublisherPluginDescriptor() {
        super(BapFtpPublisherPlugin.class);
        load();
        if (defaults == null)
            defaults = new FtpPluginDefaults();
    }

    public FtpDefaults getDefaults() {
        return defaults;
    }

    public String getDisplayName() {
        return Messages.descriptor_displayName();
    }

    public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
        return !BPPlugin.PROMOTION_JOB_TYPE.equals(aClass.getCanonicalName());
    }

    public List<BapFtpHostConfiguration> getHostConfigurations() {
        return hostConfigurations.getView();
    }

    public BapFtpHostConfiguration getConfiguration(final String name) {
        for (BapFtpHostConfiguration configuration : hostConfigurations) {
            if (configuration.getName().equals(name)) {
                return configuration;
            }
        }
        return null;
    }

    public boolean configure(final StaplerRequest request, final JSONObject formData) {
        hostConfigurations.replaceBy(request.bindJSONToList(BapFtpHostConfiguration.class, formData.get("instance")));
        if (isEnableOverrideDefaults())
            defaults = request.bindJSON(FtpDefaults.class, formData.getJSONObject("defaults"));
        save();
        return true;
    }

    public boolean canSetMasterNodeName() {
        return false;
    }

    public String getDefaultMasterNodeName() {
        return BPInstanceConfig.DEFAULT_MASTER_NODE_NAME;
    }

    public boolean isEnableOverrideDefaults() {
        return true;
    }

    public BapFtpPublisherDescriptor getPublisherDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(BapFtpPublisherDescriptor.class);
    }

    public BapFtpHostConfigurationDescriptor getHostConfigurationDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(BapFtpHostConfigurationDescriptor.class);
    }

    public FtpPluginDefaults.FtpPluginDefaultsDescriptor getPluginDefaultsDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(FtpPluginDefaults.FtpPluginDefaultsDescriptor.class);
    }

    public jenkins.plugins.publish_over.view_defaults.BPInstanceConfig.Messages getCommonFieldNames() {
        return new jenkins.plugins.publish_over.view_defaults.BPInstanceConfig.Messages();
    }

    public jenkins.plugins.publish_over.view_defaults.manage_jenkins.Messages getCommonManageMessages() {
        return new jenkins.plugins.publish_over.view_defaults.manage_jenkins.Messages();
    }

    public FormValidation doTestConnection(final String name, final String hostname, final String username,
            final String encryptedPassword, final String remoteRootDir, final int port, final int timeout,
            final boolean useActiveData, final String controlEncoding, final boolean disableMakeNestedDirs,
            final boolean disableRemoteVerification, final boolean useFtpOverTls, final boolean useImplicitTls,
            final String trustedCertificate) {
        final BapFtpHostConfiguration hostConfig = new BapFtpHostConfiguration(name, hostname, username,
                encryptedPassword, remoteRootDir, port, timeout, useActiveData, controlEncoding,
                disableMakeNestedDirs, disableRemoteVerification);
        hostConfig.setUseFtpOverTls(useFtpOverTls);
        hostConfig.setUseImplicitTls(useImplicitTls);
        hostConfig.setTrustedCertificate(trustedCertificate);
        return validateConnection(hostConfig, createDummyBuildInfo());
    }

    public static FormValidation validateConnection(final BapFtpHostConfiguration hostConfig, final BPBuildInfo buildInfo) {
        try {
            hostConfig.createClient(buildInfo).disconnect();
            return FormValidation.ok(Messages.descriptor_testConnection_ok());
        } catch (Exception e) {
            return FormValidation.errorWithMarkup("<p>"
                    + Messages.descriptor_testConnection_error() + "</p><p><pre>"
                    + Util.escape(e.getClass().getCanonicalName() + ": " + e.getLocalizedMessage())
                    + "</pre></p>");
        }
    }

    public static BPBuildInfo createDummyBuildInfo() {
        return new BPBuildInfo(
            TaskListener.NULL,
            "",
            Jenkins.getInstance().getRootPath(),
            null,
            null
        );
    }

    public Object readResolve() {
        if (defaults == null)
            defaults = new FtpPluginDefaults();
        return this;
    }

}
