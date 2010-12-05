package hudson.plugins.bap_ftp;

import hudson.plugins.bap_publisher.BPBuildInfo;
import hudson.plugins.bap_publisher.BPClient;
import hudson.plugins.bap_publisher.BPHostConfiguration;
import hudson.plugins.bap_publisher.BapPublisherException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintWriter;

public class BapFtpHostConfiguration extends BPHostConfiguration<BapFtpClient> {

    static final long serialVersionUID = 1L;
    
    public static final int DEFAULT_PORT = FTP.DEFAULT_PORT;
    public static final int DEFAULT_TIMEOUT = 300000;

    public static int getDefaultPort() { return DEFAULT_PORT; }
    public static int getDefaultTimeout() { return DEFAULT_TIMEOUT; }
    
    private int timeOut;
    private boolean useActiveData;

	public BapFtpHostConfiguration() {}

    @DataBoundConstructor
	public BapFtpHostConfiguration(String name, String hostname, String username, String password, String remoteRootDir, int port, int timeOut, boolean useActiveData) {
        super(name, hostname, username, password, remoteRootDir, port);
        this.timeOut = timeOut;
        this.useActiveData = useActiveData;
	}

	public int getTimeOut() { return timeOut; }
	public void setTimeOut(int timeOut) { this.timeOut = timeOut; }

    public boolean isUseActiveData() { return useActiveData; }
    public void setUseActiveData(boolean useActiveData) { this.useActiveData = useActiveData; }

    @Override
    public BapFtpClient createClient(BPBuildInfo buildInfo) {
        BapFtpClient client = new BapFtpClient(createFTPClient(), buildInfo);
        try {
            init(client);
        } catch (IOException ioe) {
            throw new BapPublisherException(Messages.exception_failedToCreateClient(ioe.getLocalizedMessage()), ioe);
        }
        return client;
    }

    public FTPClient createFTPClient() {
        return new FTPClient();
    }

	private void init(BapFtpClient client) throws IOException {
		FTPClient ftpClient = client.getFtpClient();
        BPBuildInfo buildInfo = client.getBuildInfo();
        PrintCommandListener commandPrinter = null;
        if (buildInfo.isVerbose()) {
            commandPrinter = new PrintCommandListener(new PrintWriter(buildInfo.getListener().getLogger()));
            ftpClient.addProtocolCommandListener(commandPrinter);
        }
        configureFTPClient(ftpClient);
        connect(client);

        login(client, commandPrinter);

        changeToRootDirectory(client);
        setRootDirectoryInClient(client);
	}

    private void configureFTPClient(FTPClient ftpClient) {
        ftpClient.setDefaultTimeout(timeOut);
        ftpClient.setDataTimeout(timeOut);
    }

    private void setRootDirectoryInClient(BapFtpClient client) throws IOException {
        if (isDirAbsolute(getRemoteRootDir())) {
            client.setAbsoluteRemoteRoot(getRemoteRootDir());
        } else {
            client.setAbsoluteRemoteRoot(getRootDirectoryFromPwd(client));
        }
    }

    private String getRootDirectoryFromPwd(BapFtpClient client) throws IOException {
        BPBuildInfo buildInfo = client.getBuildInfo();
        buildInfo.printIfVerbose(Messages.console_usingPwd());
        String pwd = client.getFtpClient().printWorkingDirectory();
        if (!isDirAbsolute(pwd))
            exception(client, Messages.exception_pwdNotAbsolute(pwd));
        return pwd;
    }

    private boolean isDirAbsolute(String dir) {
        if (dir == null)
            return false;
        return dir.startsWith("/") || dir.startsWith("\\");
    }

    private void changeToRootDirectory(BapFtpClient client) throws IOException {
        String remoteRootDir = getRemoteRootDir();
        if ((remoteRootDir != null) && (!"".equals(remoteRootDir.trim()))) {
            if (!client.changeDirectory(remoteRootDir)) {
                exception(client, Messages.exception_cwdRemoteRoot(remoteRootDir));
            }
        }
    }

    private void login(BapFtpClient client, PrintCommandListener commandListener) throws IOException {
        FTPClient ftpClient = client.getFtpClient();
        BPBuildInfo buildInfo = client.getBuildInfo();
        if (commandListener != null) {
            buildInfo.println(Messages.console_logInHidingCommunication());
            ftpClient.removeProtocolCommandListener(commandListener);
        }
        if (!ftpClient.login(getUsername(), getPassword())) {
            exception(client, Messages.exception_logInFailed(getUsername()));
        }
        if (commandListener != null) {
            buildInfo.println(Messages.console_loggedInShowingCommunication());
            ftpClient.addProtocolCommandListener(commandListener);
        }
    }

    private void connect(BapFtpClient client) throws IOException {
        FTPClient ftpClient = client.getFtpClient();
        ftpClient.connect(getHostname(), getPort());
        int responseCode = ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(responseCode)) {
            exception(client, Messages.exception_connectFailed(getHostname(), getPort(), responseCode));
        }
        setDataTransferMode(ftpClient);
    }

    private void setDataTransferMode(FTPClient ftpClient) {
        if (useActiveData) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
    }

    private void exception(BPClient client, String message) {
        BapPublisherException.exception(client, message);
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BapFtpHostConfiguration that = (BapFtpHostConfiguration) o;
        
        return createEqualsBuilder(that)
            .append(useActiveData, that.useActiveData)
            .append(timeOut, that.timeOut)
            .isEquals();
    }

    public int hashCode() {
        return createHashCodeBuilder()
            .append(useActiveData)
            .append(timeOut)
            .toHashCode();
    }
    
    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE))
            .append("useActiveData", useActiveData)
            .append("timeOut", timeOut)
            .toString();
    }

}
