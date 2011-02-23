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

import hudson.model.Project;
import jenkins.plugins.publish_over_ftp.BapFtpHostConfiguration;
import jenkins.plugins.publish_over_ftp.BapFtpPublisher;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.BapFtpTransfer;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

import java.util.LinkedList;
import java.util.List;

public class LegacyConfigurationTest extends HudsonTestCase {

    @LocalData
    public void testLoadR0x1() throws Exception {
        List<BapFtpHostConfiguration> configurations = BapFtpPublisherPlugin.DESCRIPTOR.getHostConfigurations();
        assertEquals(2, configurations.size());
        final int expectedConfigAPort = 21;
        final int expectedConfigATimeout = 300000;
        assertEquals(createHostConfiguration("a", expectedConfigAPort, expectedConfigATimeout, false), configurations.get(0));
        final int expectedConfigBPort = 121;
        final int expectedConfigBTimeout = 121000;
        assertEquals(createHostConfiguration("b", expectedConfigBPort, expectedConfigBTimeout, true), configurations.get(1));

        BapFtpTransfer transfer1 = new BapFtpTransfer("**/*", "'helloo-${BUILD_NUMBER}'-MMDD", "target", true, true, true);
        List<BapFtpTransfer> transfers1 = new LinkedList<BapFtpTransfer>();
        transfers1.add(transfer1);
        BapFtpPublisher publisher1 = new BapFtpPublisher("Config b", true, transfers1);
        BapFtpTransfer transfer21 = new BapFtpTransfer("target\\images\\*", "", "", false, false, false);
        BapFtpTransfer transfer22 = new BapFtpTransfer("target\\logs\\**\\*", "serverlogs", "target\\logs", true, false, true);
        List<BapFtpTransfer> transfers2 = new LinkedList<BapFtpTransfer>();
        transfers2.add(transfer21);
        transfers2.add(transfer22);
        BapFtpPublisher publisher2 = new BapFtpPublisher("Config a", false, transfers2);
        List<BapFtpPublisher> publishers = new LinkedList<BapFtpPublisher>();
        publishers.add(publisher1);
        publishers.add(publisher2);
        BapFtpPublisherPlugin expectedPlugin = new BapFtpPublisherPlugin(publishers, true, true, true, "MASTER");
        assertEquals(expectedPlugin, getConfiguredPlugin());
    }

    private BapFtpPublisherPlugin getConfiguredPlugin() {
        for (Project project : hudson.getProjects()) {
            if (project.getPublisher(BapFtpPublisherPlugin.DESCRIPTOR) != null)
                return (BapFtpPublisherPlugin) project.getPublisher(BapFtpPublisherPlugin.DESCRIPTOR);
        }
        fail();
        return null;
    }

    public BapFtpHostConfiguration createHostConfiguration(final String suffix, final int port,
                                                           final int timeout, final boolean useActiveData) {
        return new BapFtpHostConfiguration("Config " + suffix, "hostname." + suffix, "username." + suffix,
            "password." + suffix, "remoteRoot." + suffix, port, timeout, useActiveData);
    }

}
