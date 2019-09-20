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

import hudson.Util;
import hudson.model.Describable;
import hudson.util.Secret;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.BPBuildInfo;
import jenkins.plugins.publish_over.BPHostConfiguration;
import jenkins.plugins.publish_over.BapPublisherException;
import jenkins.plugins.publish_over_ftp.descriptor.BapFtpHostConfigurationDescriptor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@SuppressWarnings("PMD.TooManyMethods")
public class BapFtpHostConfiguration extends BPHostConfiguration<BapFtpClient, Object> implements Describable<BapFtpHostConfiguration> {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_PORT = FTP.DEFAULT_PORT;
    public static final int DEFAULT_TIMEOUT = 300000;

    private int timeout;
    private boolean useActiveData;
    private final String controlEncoding;
    private final boolean disableMakeNestedDirs;
    private final boolean disableRemoteVerification;
    private boolean useFtpOverTls;
    private boolean useImplicitTls;
    private String trustedCertificate;

    @DataBoundConstructor
    public BapFtpHostConfiguration(final String name, final String hostname, final String username, final String encryptedPassword,
                                   final String remoteRootDir, final int port, final int timeout, final boolean useActiveData,
                                   final String controlEncoding, final boolean disableMakeNestedDirs, final boolean disableRemoteVerification) {
        super(name, hostname, username, encryptedPassword, remoteRootDir, port);
        this.timeout = timeout;
        this.useActiveData = useActiveData;
        this.controlEncoding = Util.fixEmptyAndTrim(controlEncoding);
        this.disableMakeNestedDirs = disableMakeNestedDirs;
        this.disableRemoteVerification = disableRemoteVerification;
    }

    @DataBoundSetter
    public void setUseFtpOverTls(final boolean useFtpOverTls) {
        this.useFtpOverTls = useFtpOverTls;
    }

    @DataBoundSetter
    public void setUseImplicitTls(final boolean useImplicitTls) {
        this.useImplicitTls = useImplicitTls;
    }

    @DataBoundSetter
    public void setTrustedCertificate(final String trustedCertificate) {
        this.trustedCertificate = Util.fixEmptyAndTrim(trustedCertificate);
    }

    @Override
    protected final String getPassword() {
        return super.getPassword();
    }

    public int getTimeout() { return timeout; }
    public void setTimeout(final int timeout) { this.timeout = timeout; }

    public boolean isUseActiveData() { return useActiveData; }
    public void setUseActiveData(final boolean useActiveData) { this.useActiveData = useActiveData; }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public boolean isDisableMakeNestedDirs() {
        return disableMakeNestedDirs;
    }

    public boolean isDisableRemoteVerification() { return disableRemoteVerification; }

    public boolean isUseFtpOverTls() {
        return useFtpOverTls;
    }

    public boolean isUseImplicitTls() {
        return useImplicitTls;
    }

    public String getTrustedCertificate() {
        return trustedCertificate;
    }

    @Override
    public BapFtpClient createClient(final BPBuildInfo buildInfo) {
        final BapFtpClient client;
        try {
            client = new BapFtpClient(createFTPClient(), buildInfo);
            init(client);
        } catch (Exception e) {
            throw new BapPublisherException(Messages.exception_failedToCreateClient(
                    e.getClass().getName() + ": " + e.getLocalizedMessage()), e);
        }
        return client;
    }

    public FTPClient createFTPClient() throws GeneralSecurityException, FileNotFoundException, IOException {
        if (useFtpOverTls) {
            FTPSClient c = new FTPSClient(useImplicitTls);

            KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
            if (trustStorePath != null) {
                String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
                try (FileInputStream stream = new FileInputStream(trustStorePath)) {
                    if (trustStorePassword != null) {
                        ts.load(stream, trustStorePassword.toCharArray());
                    } else {
                        ts.load(stream, null);
                    }
                }
            } else {
                ts.load(null);
            }

            if (trustedCertificate != null) {
                InputStream certStream = new ByteArrayInputStream(trustedCertificate.getBytes());
                X509Certificate x509certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certStream);
                ts.setCertificateEntry(x509certificate.getSubjectDN().getName(), x509certificate);
            }

            c.setTrustManager(TrustManagerUtils.getDefaultTrustManager(ts));

            return c;
        }
        return new FTPClient();
    }

    private void init(final BapFtpClient client) throws IOException {
        final FTPClient ftpClient = client.getFtpClient();
        final BPBuildInfo buildInfo = client.getBuildInfo();
        client.setDisableMakeNestedDirs(disableMakeNestedDirs);
        client.setDisableRemoteVerification(disableRemoteVerification);
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
        if (controlEncoding != null) ftpClient.setControlEncoding(controlEncoding);
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
        final BapFtpCredentials overrideCredentials = (BapFtpCredentials) buildInfo.get(BPBuildInfo.OVERRIDE_CREDENTIALS_CONTEXT_KEY);
        final String username = overrideCredentials == null ? getUsername() : overrideCredentials.getUsername();
        final String password = overrideCredentials == null ? getPassword() : Secret.toString(overrideCredentials.getPassword());
        if (!ftpClient.login(username, password)) {
            exception(client, Messages.exception_logInFailed(username));
        }
        if (commandListener != null) {
            buildInfo.println(Messages.console_loggedInShowingCommunication());
            ftpClient.addProtocolCommandListener(commandListener);
        }
    }

    private void connect(final BapFtpClient client) throws IOException {
        final FTPClient ftpClient = client.getFtpClient();
        ftpClient.connect(getHostnameTrimmed(), getPort());
        final int responseCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(responseCode)) {
            exception(client, Messages.exception_connectFailed(getHostnameTrimmed(), getPort(), responseCode));
        }
        setDataTransferMode(ftpClient);
        setDataChannelProtection(ftpClient);
    }

    private void setDataTransferMode(final FTPClient ftpClient) {
        if (useActiveData) {
            ftpClient.enterLocalActiveMode();
        } else {
            ftpClient.enterLocalPassiveMode();
        }
    }

    private void setDataChannelProtection(FTPClient ftpClient) throws IOException {
        if (useImplicitTls && ftpClient instanceof FTPSClient) {
            FTPSClient ftpsClient = (FTPSClient) ftpClient;
            ftpsClient.execPBSZ(0);
            ftpsClient.execPROT("P");
        }
    }

    public BapFtpHostConfigurationDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(BapFtpHostConfigurationDescriptor.class);
    }

    @Override
    protected HashCodeBuilder addToHashCode(final HashCodeBuilder builder) {
        return super.addToHashCode(builder)
                .append(useActiveData)
                .append(timeout)
                .append(controlEncoding);
    }

    protected EqualsBuilder addToEquals(final EqualsBuilder builder, final BapFtpHostConfiguration that) {
        return super.addToEquals(builder, that)
                .append(useActiveData, that.useActiveData)
                .append(timeout, that.timeout)
                .append(controlEncoding, that.controlEncoding);
    }

    @Override
    protected ToStringBuilder addToToString(final ToStringBuilder builder) {
        return super.addToToString(builder)
                .append("useActiveData", useActiveData)
                .append("timeout", timeout)
                .append("controlEncoding", controlEncoding);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return addToEquals(new EqualsBuilder(), (BapFtpHostConfiguration) that).isEquals();
    }

    @Override
    public int hashCode() {
        return addToHashCode(new HashCodeBuilder()).toHashCode();
    }

    @Override
    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }

    @Override
    public Object readResolve() {
        return super.readResolve();
    }
}
