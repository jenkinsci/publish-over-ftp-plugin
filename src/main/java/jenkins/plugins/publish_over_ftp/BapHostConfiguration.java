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

package jenkins.plugins.publish_over_ftp;

import jenkins.plugins.publish_over.BPBuildInfo;
import jenkins.plugins.publish_over.BPHostConfiguration;
import jenkins.plugins.publish_over.BapPublisherException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("PMD.TooManyMethods")
public class BapHostConfiguration extends BPHostConfiguration<BapFtpClient, Object> {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_PORT = FTP.DEFAULT_PORT;
    public static final int DEFAULT_TIMEOUT = 300000;

    private int timeout;
    private boolean useActiveData;

    public BapHostConfiguration(final String name, final String hostname, final String username, final String password,
                                final String remoteRootDir, final int port, final int timeout, final boolean useActiveData) {
        super(name, hostname, username, password, remoteRootDir, port);
        this.timeout = timeout;
        this.useActiveData = useActiveData;
    }

    protected final String getPassword() {
        return super.getPassword();
    }

    public int getTimeout() { return timeout; }
    public void setTimeout(final int timeout) { this.timeout = timeout; }

    public boolean isUseActiveData() { return useActiveData; }
    public void setUseActiveData(final boolean useActiveData) { this.useActiveData = useActiveData; }

    @Override
    public BapFtpClient createClient(final BPBuildInfo buildInfo) {
        final BapFtpClient client = new BapFtpClient(createFTPClient(), buildInfo);
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

    private void init(final BapFtpClient client) throws IOException {
        final FTPClient ftpClient = client.getFtpClient();
        final BPBuildInfo buildInfo = client.getBuildInfo();
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

    private void configureFTPClient(final FTPClient ftpClient) {
        ftpClient.setDefaultTimeout(timeout);
        ftpClient.setDataTimeout(timeout);
    }

    private void setRootDirectoryInClient(final BapFtpClient client) throws IOException {
        if (isDirectoryAbsolute(getRemoteRootDir())) {
            client.setAbsoluteRemoteRoot(getRemoteRootDir());
        } else {
            client.setAbsoluteRemoteRoot(getRootDirectoryFromPwd(client));
        }
    }

    private String getRootDirectoryFromPwd(final BapFtpClient client) throws IOException {
        final BPBuildInfo buildInfo = client.getBuildInfo();
        buildInfo.printIfVerbose(Messages.console_usingPwd());
        final String pwd = client.getFtpClient().printWorkingDirectory();
        if (!isDirectoryAbsolute(pwd))
            exception(client, Messages.exception_pwdNotAbsolute(pwd));
        return pwd;
    }

    private void login(final BapFtpClient client, final PrintCommandListener commandListener) throws IOException {
        final FTPClient ftpClient = client.getFtpClient();
        final BPBuildInfo buildInfo = client.getBuildInfo();
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

    private void connect(final BapFtpClient client) throws IOException {
        final FTPClient ftpClient = client.getFtpClient();
        ftpClient.connect(getHostname(), getPort());
        final int responseCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(responseCode)) {
            exception(client, Messages.exception_connectFailed(getHostname(), getPort(), responseCode));
        }
        setDataTransferMode(ftpClient);
    }

    private void setDataTransferMode(final FTPClient ftpClient) {
        if (useActiveData) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
    }

    protected HashCodeBuilder addToHashCode(final HashCodeBuilder builder) {
        return super.addToHashCode(builder)
                .append(useActiveData)
                .append(timeout);
    }

    protected EqualsBuilder addToEquals(final EqualsBuilder builder, final BapHostConfiguration that) {
        return super.addToEquals(builder, that)
                .append(useActiveData, that.useActiveData)
                .append(timeout, that.timeout);
    }

    protected ToStringBuilder addToToString(final ToStringBuilder builder) {
        return super.addToToString(builder)
                .append("useActiveData", useActiveData)
                .append("timeout", timeout);
    }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return addToEquals(new EqualsBuilder(), (BapHostConfiguration) that).isEquals();
    }

    public int hashCode() {
        return addToHashCode(new HashCodeBuilder()).toHashCode();
    }

    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }

}
