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
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import jenkins.plugins.publish_over.BPTransfer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class BapFtpTransfer extends BapTransfer implements Describable<BapFtpTransfer> {

    private static final long serialVersionUID = 1L;

    @DataBoundConstructor
    public BapFtpTransfer(final String sourceFiles, final String excludes, final String remoteDirectory, final String removePrefix,
                       final boolean asciiMode, final boolean remoteDirectorySDF, final boolean flatten, final boolean cleanRemote) {
        super(sourceFiles, excludes, remoteDirectory, removePrefix, asciiMode, remoteDirectorySDF, flatten, cleanRemote);
    }

    public DescriptorImpl getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(DescriptorImpl.class);
    }
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return addToEquals(new EqualsBuilder(), (BapFtpTransfer) that).isEquals();
    }

    public int hashCode() {
        return addToHashCode(new HashCodeBuilder()).toHashCode();
    }

    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<BapFtpTransfer> {
        public BapFtpPublisherPlugin.Descriptor getPublisherPluginDescriptor() {
            return Hudson.getInstance().getDescriptorByType(BapFtpPublisherPlugin.Descriptor.class);
        }
        public FormValidation doCheckSourceFiles(@QueryParameter final String value) {
            return FormValidation.validateRequired(value);
        }
        public boolean canUseExcludes() {
            return BPTransfer.canUseExcludes();
        }
        @Override
        public String getDisplayName() {
            return Messages.transfer_descriptor_displayName();
        }
    }
}
