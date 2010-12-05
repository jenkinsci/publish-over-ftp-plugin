package hudson.plugins.bap_ftp.hudson;

import hudson.model.Project;
import hudson.plugins.bap_ftp.BapFtpHostConfiguration;
import hudson.plugins.bap_ftp.BapFtpPublisher;
import hudson.plugins.bap_ftp.BapFtpPublisherPlugin;
import hudson.plugins.bap_ftp.BapFtpTransfer;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

import java.util.LinkedList;
import java.util.List;

public class LegacyConfigurationTest extends HudsonTestCase {
    
    @LocalData
    public void testLoad_0_1() throws Exception {
        List<BapFtpHostConfiguration> configurations = BapFtpPublisherPlugin.DESCRIPTOR.getHostConfigurations();
        assertEquals(2, configurations.size());
        assertEquals(createHostConfiguration("a", 21, 300000, false), configurations.get(0));
        assertEquals(createHostConfiguration("b", 121, 121000, true), configurations.get(1));
        
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
        BapFtpPublisherPlugin expectedPlugin = new BapFtpPublisherPlugin(publishers, true, true, true);
        assertEquals(expectedPlugin, getConfiguredPlugin());
    }

    private BapFtpPublisherPlugin getConfiguredPlugin() {
        for (Project project : hudson.getProjects()) {
            if (project.getPublisher(BapFtpPublisherPlugin.DESCRIPTOR) != null)
                return (BapFtpPublisherPlugin)project.getPublisher(BapFtpPublisherPlugin.DESCRIPTOR);
        }
        fail();
        return null;
    }

    public BapFtpHostConfiguration createHostConfiguration(String suffix, int port, int timeout, boolean useActiveData) {
        return new BapFtpHostConfiguration("Config " + suffix, "hostname." + suffix, "username." + suffix,
            "password." + suffix, "remoteRoot." + suffix, port, timeout, useActiveData);
    }
    
}
