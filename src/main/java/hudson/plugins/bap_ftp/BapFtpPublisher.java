package hudson.plugins.bap_ftp;

import hudson.plugins.bap_publisher.BapPublisher;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class BapFtpPublisher extends BapPublisher<BapFtpTransfer> {

    @DataBoundConstructor
    public BapFtpPublisher(String configName, boolean verbose, List<BapFtpTransfer> transfers) {
        super(configName, verbose, transfers);
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        return createEqualsBuilder((BapFtpPublisher) o).isEquals();
    }

    public int hashCode() {
        return createHashCodeBuilder().toHashCode();
    }
    
    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }

}
