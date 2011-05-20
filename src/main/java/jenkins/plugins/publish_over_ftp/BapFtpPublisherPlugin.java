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

package jenkins.plugins.publish_over_ftp;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import jenkins.plugins.publish_over.BPPlugin;
import jenkins.plugins.publish_over.BPPluginDescriptor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;

@SuppressWarnings("PMD.LooseCoupling") // serializable
public class BapFtpPublisherPlugin extends BPPlugin<BapFtpPublisher, BapFtpClient, Object> {

    private static final long serialVersionUID = 1L;

    @DataBoundConstructor
    public BapFtpPublisherPlugin(final ArrayList<BapFtpPublisher> publishers, final boolean continueOnError, final boolean failOnError,
                                 final boolean alwaysPublishFromMaster, final String masterNodeName) {
        super(Messages.console_message_prefix(), publishers, continueOnError, failOnError, alwaysPublishFromMaster, masterNodeName);
    }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return createEqualsBuilder((BapFtpPublisherPlugin) that).isEquals();
    }

    public int hashCode() {
        return createHashCodeBuilder().toHashCode();
    }

    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }

    @Override
    public Descriptor getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(Descriptor.class);
    }

    public BapFtpHostConfiguration getConfiguration(final String name) {
        return getDescriptor().getConfiguration(name);
    }

    @Extension
    public static class Descriptor extends BPPluginDescriptor<BapFtpHostConfiguration, Object> {
        public Descriptor() {
            super(new DescriptorMessages(), BapFtpPublisherPlugin.class, BapFtpHostConfiguration.class, null);
        }
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return !BPPlugin.PROMOTION_JOB_TYPE.equals(aClass.getCanonicalName());
        }
        public BapFtpPublisher.DescriptorImpl getPublisherDescriptor() {
            return Hudson.getInstance().getDescriptorByType(BapFtpPublisher.DescriptorImpl.class);
        }
        public BapFtpHostConfiguration.DescriptorImpl getHostConfigurationDescriptor() {
            return Hudson.getInstance().getDescriptorByType(BapFtpHostConfiguration.DescriptorImpl.class);
        }
    }

    public static class DescriptorMessages implements BPPluginDescriptor.BPDescriptorMessages {
        public String displayName() {
            return Messages.descriptor_displayName();
        }
        public String connectionOK() {
            return Messages.descriptor_testConnection_ok();
        }
        public String connectionErr() {
            return Messages.descriptor_testConnection_error();
        }
    }

}
