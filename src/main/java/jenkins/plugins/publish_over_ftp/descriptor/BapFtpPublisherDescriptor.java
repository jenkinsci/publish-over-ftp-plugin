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
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over_ftp.BapFtpPublisher;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.Messages;

@Extension
public class BapFtpPublisherDescriptor extends Descriptor<BapFtpPublisher> {

    public BapFtpPublisherDescriptor() {
        super(BapFtpPublisher.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.publisher_descriptor_displayName();
    }

    public BapFtpPublisherPlugin.Descriptor getPublisherPluginDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(BapFtpPublisherPlugin.Descriptor.class);
    }

    public BapFtpTransferDescriptor getTransferDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(BapFtpTransferDescriptor.class);
    }

    public jenkins.plugins.publish_over.view_defaults.BapPublisher.Messages getCommonFieldNames() {
        return new jenkins.plugins.publish_over.view_defaults.BapPublisher.Messages();
    }

}
