package hudson.plugins.bap_ftp.hudson;

import hudson.plugins.bap_ftp.BapFtpHostConfiguration;
import hudson.plugins.bap_ftp.BapFtpPublisherPlugin;
import hudson.util.CopyOnWriteList;

import java.lang.reflect.Field;

public class HudsonTestHelper {
    
    public void setGlobalConfig(BapFtpHostConfiguration... newHostConfigurations) throws Exception {
        CopyOnWriteList<BapFtpHostConfiguration> hostConfigurations = getHostConfigurations();
        hostConfigurations.replaceBy(newHostConfigurations);
    }
    
    public CopyOnWriteList<BapFtpHostConfiguration> getHostConfigurations() throws Exception {
        Field hostConfig = BapFtpPublisherPlugin.DESCRIPTOR.getClass().getDeclaredField("hostConfigurations");
        hostConfig.setAccessible(true);
        return (CopyOnWriteList)hostConfig.get(BapFtpPublisherPlugin.DESCRIPTOR);        
    }
    
}
