package hudson.plugins.bap_ftp;

import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.plugins.bap_publisher.BPBuildInfo;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;

public class BapFtpHostConfigurationTest {
    
    private Map<String, String> envVars = new TreeMap<String, String>();
    private BPBuildInfo buildInfo = new BPBuildInfo(envVars, new FilePath(new File("aBaseDir")), Calendar.getInstance(), TaskListener.NULL, "");
    private IMocksControl mockControl = EasyMock.createStrictControl();
    FTPClient mockFTPClient = mockControl.createMock(FTPClient.class);
    private BapFtpHostConfiguration bapFtpHostConfiguration = new BapFtpHostConfigurationWithMockFTPClient();
    
    @Test public void testChangeToRootDir() throws Exception {
        testChangeToInitialDirectory("/");
    }

    @Test public void testChangeToRootDirWin() throws Exception {
        testChangeToInitialDirectory("\\");
    }

    @Test public void testChangeToRootDirLongerPath() throws Exception {
        testChangeToInitialDirectory("/this/is/my/root");
    }

    @Test public void testChangeToRootDirRelativePath() throws Exception {
        testChangeToInitialDirectory("this/is/my/rel/root", true);
    }

    @Test public void testNoChangeDirectoryRemoteDirNull() throws Exception {
        testNoChangeToInitialDirectory(null);
    }

    @Test public void testNoChangeDirectoryRemoteDirEmptyString() throws Exception {
        testNoChangeToInitialDirectory("");
    }

    @Test public void testNoChangeDirectoryRemoteDirOnlySpaceInString() throws Exception {
        testNoChangeToInitialDirectory("  ");
    }

    private void testNoChangeToInitialDirectory(String remoteRoot) throws Exception {
        bapFtpHostConfiguration.setRemoteRootDir(remoteRoot);
        expectConnectAndLogin();
        expect(mockFTPClient.printWorkingDirectory()).andReturn("/pub");

        BapFtpClient client = assertCreateSession();
        assertEquals("/pub", client.getAbsoluteRemoteRoot());
    }

    private void testChangeToInitialDirectory(String remoteRoot) throws Exception {
        testChangeToInitialDirectory(remoteRoot, false);
    }
    
    private void testChangeToInitialDirectory(String remoteRoot, boolean expectPwd) throws Exception {
        bapFtpHostConfiguration.setRemoteRootDir(remoteRoot);
        expectConnectAndLogin();   
        expect(mockFTPClient.changeWorkingDirectory(remoteRoot)).andReturn(true);
        if (expectPwd)
            expect(mockFTPClient.printWorkingDirectory()).andReturn("/" + remoteRoot);
        BapFtpClient client = assertCreateSession();
        if (!expectPwd)
            assertEquals(remoteRoot, client.getAbsoluteRemoteRoot());
    }

    @Test public void testSetActive() throws Exception {
        bapFtpHostConfiguration.setUseActiveData(true);
        expectConnectAndLogin();
        expect(mockFTPClient.printWorkingDirectory()).andReturn("/");
        assertCreateSession();
    }

    private void expectConnectAndLogin() throws Exception {
        mockFTPClient.setDefaultTimeout(bapFtpHostConfiguration.getTimeOut());
        mockFTPClient.setDataTimeout(bapFtpHostConfiguration.getTimeOut());
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
        BapFtpClient client = bapFtpHostConfiguration.createClient(buildInfo);
        mockControl.verify();
        return client;
    }

    private class BapFtpHostConfigurationWithMockFTPClient extends BapFtpHostConfiguration {
        private static final String TEST_CFG_NAME = "myTestConfig";
        private static final String TEST_HOSTNAME = "my.test.hostname";
        private static final String TEST_USERNAME = "myTestUsername";
        private static final String TEST_PASSWORD = "myTestPassword";
        BapFtpHostConfigurationWithMockFTPClient() {
            super(TEST_CFG_NAME, TEST_HOSTNAME, TEST_USERNAME, TEST_PASSWORD, "", DEFAULT_PORT, DEFAULT_TIMEOUT, false);
        }
        @Override
        public FTPClient createFTPClient() {
            return mockFTPClient;
        }
    }
    
}
