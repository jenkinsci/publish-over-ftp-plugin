package hudson.plugins.bap_ftp;

import hudson.plugins.bap_publisher.BPTransfer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

public class BapFtpTransfer extends BPTransfer {

    static final long serialVersionUID = 1L;

    private boolean asciiMode;

    @DataBoundConstructor
	public BapFtpTransfer(String sourceFiles, String remoteDirectory, String removePrefix, boolean asciiMode, boolean remoteDirectorySDF, boolean flatten) {
        super(sourceFiles, remoteDirectory, removePrefix, remoteDirectorySDF, flatten);
        this.asciiMode = asciiMode;
	}

    public boolean isAsciiMode() { return asciiMode; }
    public void setAsciiMode(boolean asciiMode) { this.asciiMode = asciiMode; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BapFtpTransfer that = (BapFtpTransfer) o;
        
        return createEqualsBuilder(that)
            .append(asciiMode, that.asciiMode)
            .isEquals();
    }

    public int hashCode() {
        return createHashCodeBuilder()
            .append(asciiMode)
            .toHashCode();
    }
    
    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE))
            .append("asciiMode", asciiMode)
            .toString();
    }
    
}
