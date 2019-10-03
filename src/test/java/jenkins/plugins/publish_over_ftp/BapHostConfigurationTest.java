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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import hudson.FilePath;
import hudson.model.TaskListener;

import java.io.File;
import java.io.IOException;

import jenkins.plugins.publish_over.BPBuildInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

@SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "PMD.TooManyMethods" })
public class BapHostConfigurationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @BeforeClass
    public static void before() {
        MySecretHelper.setSecretKey();
    }

    @AfterClass
    public static void after() {
        MySecretHelper.clearSecretKey();
    }

    @Before
    public void initializeHostConfig() {
        this.bapFtpHostConfiguration = new BapFtpHostConfigurationWithMockFTPClient(mockFTPClient);
    }

    private final transient BPBuildInfo buildInfo = new BPBuildInfo(TaskListener.NULL, "", new FilePath(new File("")), null, null);
    private final transient IMocksControl mockControl = EasyMock.createStrictControl();
    private final transient FTPClient mockFTPClient = mockControl.createMock(FTPClient.class);
    private transient BapFtpHostConfiguration bapFtpHostConfiguration;

    @Test
    public void testChangeToRootDir() throws Exception {
        assertChangeToInitialDirectory("/");
    }

    @Test
    public void testChangeToRootDirWin() throws Exception {
        assertChangeToInitialDirectory("\\");
    }

    @Test
    public void testChangeToRootDirLongerPath() throws Exception {
        assertChangeToInitialDirectory("/this/is/my/root");
    }

    @Test
    public void testChangeToRootDirRelativePath() throws Exception {
        assertChangeToInitialDirectory("this/is/my/rel/root", true);
    }

    @Test
    public void testNoChangeDirectoryRemoteDirNull() throws Exception {
        assertNoChangeToInitialDirectory(null);
    }

    @Test
    public void testNoChangeDirectoryRemoteDirEmptyString() throws Exception {
        assertNoChangeToInitialDirectory("");
    }

    @Test
    public void testNoChangeDirectoryRemoteDirOnlySpaceInString() throws Exception {
        assertNoChangeToInitialDirectory("  ");
    }

    private void assertNoChangeToInitialDirectory(final String remoteRoot) throws Exception {
        this.bapFtpHostConfiguration = new BapFtpHostConfigurationWithMockFTPClient(mockFTPClient);
        bapFtpHostConfiguration.setRemoteRootDir(remoteRoot);
        expectConnectAndLogin();
        expect(mockFTPClient.printWorkingDirectory()).andReturn("/pub");

        final BapFtpClient client = assertCreateSession();
        assertEquals("/pub", client.getAbsoluteRemoteRoot());
    }

    private void assertChangeToInitialDirectory(final String remoteRoot) throws Exception {
        assertChangeToInitialDirectory(remoteRoot, false);
    }

    private void assertChangeToInitialDirectory(final String remoteRoot, final boolean expectPwd) throws Exception {
        this.bapFtpHostConfiguration = new BapFtpHostConfigurationWithMockFTPClient(mockFTPClient);
        bapFtpHostConfiguration.setRemoteRootDir(remoteRoot);
        expectConnectAndLogin();
        expect(mockFTPClient.changeWorkingDirectory(remoteRoot)).andReturn(true);
        if (expectPwd)
            expect(mockFTPClient.printWorkingDirectory()).andReturn("/" + remoteRoot);
        final BapFtpClient client = assertCreateSession();
        if (!expectPwd)
            assertEquals(remoteRoot, client.getAbsoluteRemoteRoot());
    }

    @Test
    public void testSetActive() throws Exception {
        this.bapFtpHostConfiguration = new BapFtpHostConfigurationWithMockFTPClient(mockFTPClient);
        bapFtpHostConfiguration.setUseActiveData(true);
        expectConnectAndLogin();
        expect(mockFTPClient.printWorkingDirectory()).andReturn("/");
        assertCreateSession();
    }

    @Test
    public void testDisableMakeNestedDirs() throws Exception {
        bapFtpHostConfiguration = new BapFtpHostConfigurationWithMockFTPClient(mockFTPClient, true);
        expectConnectAndLogin();
        expect(mockFTPClient.printWorkingDirectory()).andReturn("/");
        final BapFtpClient client = assertCreateSession();
        mockControl.reset();
        mockControl.replay();
        assertFalse(client.makeDirectory("more/than/one"));
        mockControl.verify();
    }

    private void expectConnectAndLogin() throws Exception {
        mockFTPClient.setDefaultTimeout(bapFtpHostConfiguration.getTimeout());
        mockFTPClient.setDataTimeout(bapFtpHostConfiguration.getTimeout());
        mockFTPClient.connect(bapFtpHostConfiguration.getHostname(), bapFtpHostConfiguration.getPort());
        expect(mockFTPClient.getReplyCode()).andReturn(FTPReply.SERVICE_READY);
        if (bapFtpHostConfiguration.isUseActiveData()) {
            mockFTPClient.enterLocalActiveMode();
        } else {
            mockFTPClient.enterLocalPassiveMode();
        }
        expect(mockFTPClient.login(bapFtpHostConfiguration.getUsername(), bapFtpHostConfiguration.getPassword())).andReturn(true);
    }

    private BapFtpClient assertCreateSession() throws IOException {
        mockControl.replay();
        final BapFtpClient client = bapFtpHostConfiguration.createClient(buildInfo);
        mockControl.verify();
        return client;
    }

    private static class BapFtpHostConfigurationWithMockFTPClient extends BapFtpHostConfiguration {
        private static final long serialVersionUID = 1L;
        private static final String TEST_CFG_NAME = "myTestConfig";
        private static final String TEST_HOSTNAME = "my.test.hostname";
        private static final String TEST_USERNAME = "myTestUsername";
        private static final String TEST_PASSWORD = "myTestPassword";
        private final transient FTPClient ftpClient;

        BapFtpHostConfigurationWithMockFTPClient(final FTPClient ftpClient, final boolean disableMakeNestedDirs) {
            super(TEST_CFG_NAME, TEST_HOSTNAME, TEST_USERNAME, TEST_PASSWORD, "", DEFAULT_PORT, DEFAULT_TIMEOUT, false, null, disableMakeNestedDirs, false);
            this.ftpClient = ftpClient;
        }

        BapFtpHostConfigurationWithMockFTPClient(final FTPClient ftpClient) {
            this(ftpClient, false);
        }

        @Override
        public FTPClient createFTPClient() {
            return ftpClient;
        }

        @Override
        public Object readResolve() {
            return super.readResolve();
        }
    }

}
