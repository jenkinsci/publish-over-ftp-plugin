package hudson.plugins.bap_ftp.hudson;

import hudson.model.FreeStyleProject;
import hudson.plugins.bap_ftp.BapFtpHostConfiguration;
import hudson.plugins.bap_ftp.BapFtpPublisher;
import hudson.plugins.bap_ftp.BapFtpPublisherPlugin;
import hudson.plugins.bap_ftp.BapFtpTransfer;
import hudson.util.CopyOnWriteList;
import org.jvnet.hudson.test.HudsonTestCase;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class CurrentConfigurationTest extends HudsonTestCase {
    
    FreeStyleProject project;
    BapFtpHostConfiguration configA = new BapFtpHostConfiguration("host A", "", "", "", "", 0, 0, false);
    BapFtpHostConfiguration configB = new BapFtpHostConfiguration("host B", "", "", "", "", 0, 0, false);
    HudsonTestHelper testHelper = new HudsonTestHelper();
    
    public void setUp() throws Exception {
        super.setUp();
        project = createFreeStyleProject();
        testHelper.setGlobalConfig(configA, configB);
    }
    
    public void testRoundTrip() throws Exception {
        BapFtpPublisherPlugin plugin = createPlugin();       
        project.getPublishersList().add(plugin);
        
        submit(new WebClient().getPage(project, "configure").getFormByName("config"));
        
        BapFtpPublisherPlugin configured = (BapFtpPublisherPlugin)project.getPublisher(BapFtpPublisherPlugin.DESCRIPTOR);
        assertNotSame(plugin, configured);
        assertEquals(plugin, configured);
    }
    
//    @TODO test configuring various ways using the jelly forms (promotions?)
//    public void testConfigureProject() throws Exception {}
//    
//    public void testConfigureGlobal() throws Exception {}
    
    private BapFtpPublisherPlugin createPlugin() {
        BapFtpTransfer transfer1 = new BapFtpTransfer("**/*", "/pub", "target", true, false, true);
        BapFtpTransfer transfer2 = new BapFtpTransfer("*", "", "WebApp", false, true, false);
        BapFtpTransfer transfer3 = new BapFtpTransfer("dave", "", "", false, true, true);
        List<BapFtpTransfer> transfers1 = new LinkedList<BapFtpTransfer>();
        transfers1.add(transfer1);
        transfers1.add(transfer2);
        List<BapFtpTransfer> transfers2 = new LinkedList<BapFtpTransfer>();
        transfers2.add(transfer3);
        BapFtpPublisher publisher1 = new BapFtpPublisher(configA.getName(), true, transfers1);
        BapFtpPublisher publisher2 = new BapFtpPublisher(configB.getName(), false, transfers2);
        List<BapFtpPublisher> publishers = new LinkedList<BapFtpPublisher>();
        publishers.add(publisher1);
        publishers.add(publisher2);
        BapFtpPublisherPlugin plugin = new BapFtpPublisherPlugin(publishers, true, true, true);
        return plugin;
    }
 
}
