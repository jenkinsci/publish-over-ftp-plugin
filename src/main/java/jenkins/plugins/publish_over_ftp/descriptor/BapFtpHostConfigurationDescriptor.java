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

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.BPValidators;
import jenkins.plugins.publish_over_ftp.BapFtpHostConfiguration;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.Messages;
import org.kohsuke.stapler.QueryParameter;

@Extension
public class BapFtpHostConfigurationDescriptor extends Descriptor<BapFtpHostConfiguration> {

    public BapFtpHostConfigurationDescriptor() {
        super(BapFtpHostConfiguration.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.hostconfig_descriptor();
    }

    public int getDefaultPort() {
        return BapFtpHostConfiguration.DEFAULT_PORT;
    }

    public int getDefaultTimeout() {
        return BapFtpHostConfiguration.DEFAULT_TIMEOUT;
    }

    public FormValidation doCheckName(@QueryParameter final String value) {
        return BPValidators.validateName(value);
    }

    public FormValidation doCheckHostname(@QueryParameter final String value) {
        return FormValidation.validateRequired(value);
    }

    public FormValidation doCheckPort(@QueryParameter final String value) {
        return FormValidation.validatePositiveInteger(value);
    }

    public FormValidation doCheckTimeout(@QueryParameter final String value) {
        return FormValidation.validateNonNegativeInteger(value);
    }

    public FormValidation doTestConnection(@QueryParameter final String name, @QueryParameter final String hostname,
            @QueryParameter final String username, @QueryParameter final String encryptedPassword,
            @QueryParameter final String remoteRootDir, @QueryParameter final int port,
            @QueryParameter final int timeout, @QueryParameter final boolean useActiveData,
            @QueryParameter final String controlEncoding, @QueryParameter final boolean disableMakeNestedDirs,
            @QueryParameter final boolean disableRemoteVerification, @QueryParameter final boolean useFtpOverTls,
            @QueryParameter final boolean useImplicitTls, @QueryParameter final String trustedCertificate) {
        final BapFtpPublisherPlugin.Descriptor pluginDescriptor = Jenkins.getInstance().getDescriptorByType(
                BapFtpPublisherPlugin.Descriptor.class);
        return pluginDescriptor.doTestConnection(name, hostname, username, encryptedPassword, remoteRootDir, port,
                timeout, useActiveData, controlEncoding, disableMakeNestedDirs, disableRemoteVerification,
                useFtpOverTls, useImplicitTls, trustedCertificate);
    }

    public jenkins.plugins.publish_over.view_defaults.HostConfiguration.Messages getCommonFieldNames() {
        return new jenkins.plugins.publish_over.view_defaults.HostConfiguration.Messages();
    }

}
