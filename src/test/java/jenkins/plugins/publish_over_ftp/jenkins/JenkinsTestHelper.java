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

package jenkins.plugins.publish_over_ftp.jenkins;

import hudson.util.CopyOnWriteList;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over_ftp.BapFtpHostConfiguration;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.descriptor.BapFtpPublisherPluginDescriptor;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class JenkinsTestHelper {

    public void setGlobalConfig(final BapFtpHostConfiguration... newHostConfigurations)
                                                                            throws NoSuchFieldException, IllegalAccessException {
        final CopyOnWriteList<BapFtpHostConfiguration> hostConfigurations = getHostConfigurations();
        hostConfigurations.replaceBy(newHostConfigurations);
    }

    public CopyOnWriteList<BapFtpHostConfiguration> getHostConfigurations() throws NoSuchFieldException, IllegalAccessException {
        final Field hostConfigurations = BapFtpPublisherPluginDescriptor.class.getDeclaredField("hostConfigurations");
        try {
            return AccessController.doPrivileged(new GetMeTheHostConfigurations(hostConfigurations));
        } catch (PrivilegedActionException pae) {
            throw (IllegalAccessException) pae.getException();
        }
    }

    public static final class GetMeTheHostConfigurations implements PrivilegedExceptionAction<CopyOnWriteList<BapFtpHostConfiguration>> {
        private final transient Field hostConfigurations;
        protected GetMeTheHostConfigurations(final Field hostConfigurations) {
            this.hostConfigurations = hostConfigurations;
        }
        public CopyOnWriteList<BapFtpHostConfiguration> run() throws IllegalAccessException {
            hostConfigurations.setAccessible(true);
            return (CopyOnWriteList) hostConfigurations.get(Jenkins.getInstance().getDescriptorByType(
                                                                                            BapFtpPublisherPlugin.Descriptor.class));
        }
    }

}
