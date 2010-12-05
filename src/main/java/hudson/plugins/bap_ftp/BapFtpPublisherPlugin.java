package hudson.plugins.bap_ftp;

import hudson.Extension;
import hudson.plugins.bap_publisher.BPPlugin;
import hudson.plugins.bap_publisher.BPPluginDescriptor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class BapFtpPublisherPlugin extends BPPlugin<BapFtpPublisher, BapFtpClient> {

    private static BPPluginDescriptor.DescriptorMessages createDescriptorMessages() {
        return new BPPluginDescriptor.DescriptorMessages() {
            public String displayName() {
                return Messages.descriptor_displayName();
            }
            public String connectionOK() {
                return Messages.descriptor_testConnection_ok();
            }
            public String connectionErr() {
                return Messages.descriptor_testConnection_error();
            }
        };
    }

    @Extension
    public static final BPPluginDescriptor<BapFtpHostConfiguration> DESCRIPTOR = new BPPluginDescriptor<BapFtpHostConfiguration>(
            createDescriptorMessages(), BapFtpPublisherPlugin.class, BapFtpHostConfiguration.class);

    @DataBoundConstructor
	public BapFtpPublisherPlugin(List<BapFtpPublisher> publishers, boolean continueOnError, boolean failOnError, boolean alwaysPublishFromMaster) {
        super(Messages.console_message_prefix(), publishers, continueOnError, failOnError, alwaysPublishFromMaster);
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        return createEqualsBuilder((BapFtpPublisherPlugin) o).isEquals();
    }

    public int hashCode() {
        return createHashCodeBuilder().toHashCode();
    }
    
    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }

    public BapFtpHostConfiguration getConfiguration(String name) {
		return DESCRIPTOR.getConfiguration(name);
	}

}
