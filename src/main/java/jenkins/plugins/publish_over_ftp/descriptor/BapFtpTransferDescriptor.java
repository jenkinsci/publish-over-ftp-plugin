package jenkins.plugins.publish_over_ftp.descriptor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import jenkins.plugins.publish_over.BPTransfer;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.BapFtpTransfer;
import jenkins.plugins.publish_over_ftp.Messages;
import org.kohsuke.stapler.QueryParameter;

@Extension
public class BapFtpTransferDescriptor extends Descriptor<BapFtpTransfer> {

    public BapFtpTransferDescriptor() {
        super(BapFtpTransfer.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.transfer_descriptor_displayName();
    }

    public BapFtpPublisherPlugin.Descriptor getPublisherPluginDescriptor() {
        return Hudson.getInstance().getDescriptorByType(BapFtpPublisherPlugin.Descriptor.class);
    }

    public FormValidation doCheckSourceFiles(@QueryParameter final String value) {
        return FormValidation.validateRequired(value);
    }

    public jenkins.plugins.publish_over.view_defaults.BPTransfer.Messages getCommonFieldNames() {
        return new jenkins.plugins.publish_over.view_defaults.BPTransfer.Messages();
    }

}
