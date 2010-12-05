package hudson.plugins.bap_ftp.hudson;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.plugins.bap_ftp.BapFtpHostConfiguration;
import hudson.plugins.bap_ftp.BapFtpPublisher;
import hudson.plugins.bap_ftp.BapFtpPublisherPlugin;
import hudson.plugins.bap_ftp.BapFtpTransfer;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class IntegrationTest extends HudsonTestCase {
    
    public void testIntegration() throws Exception {
        final FTPClient mockFTPClient = mock(FTPClient.class);
        BapFtpHostConfiguration testHostConfig = new BapFtpHostConfiguration("testConfig", "testHostname", "testUsername", "testPassword", "/testRemoteRoot", 21, 3000, false) {
            @Override
            public FTPClient createFTPClient() {
                return mockFTPClient;
            }
        };
        new HudsonTestHelper().setGlobalConfig(testHostConfig);
        final String dirToIgnore = "target";
        BapFtpTransfer transfer = new BapFtpTransfer("**/*", "sub-home", dirToIgnore, true, false, false);
        BapFtpPublisher publisher = new BapFtpPublisher(testHostConfig.getName(), false, Collections.singletonList(transfer));
        BapFtpPublisherPlugin plugin = new BapFtpPublisherPlugin(Collections.singletonList(publisher), false, false, false);

        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(plugin);
        final String buildDirectory = "build-dir";
        final String buildFileName = "file.txt";
        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
                FilePath dir = build.getWorkspace().child(dirToIgnore).child(buildDirectory);
                dir.mkdirs();
                dir.child(buildFileName).write("Helloooooo", "UTF-8");
                build.setResult(Result.SUCCESS);
                return true;
            }
        });
        
        when(mockFTPClient.getReplyCode()).thenReturn(FTPReply.SERVICE_READY);
        when(mockFTPClient.login(testHostConfig.getUsername(), testHostConfig.getPassword())).thenReturn(true);
        when(mockFTPClient.changeWorkingDirectory(testHostConfig.getRemoteRootDir())).thenReturn(true);
        when(mockFTPClient.setFileType(FTPClient.ASCII_FILE_TYPE)).thenReturn(true);
        when(mockFTPClient.changeWorkingDirectory(transfer.getRemoteDirectory())).thenReturn(false).thenReturn(true);
        when(mockFTPClient.makeDirectory(transfer.getRemoteDirectory())).thenReturn(true);
        when(mockFTPClient.changeWorkingDirectory(buildDirectory)).thenReturn(false).thenReturn(true);
        when(mockFTPClient.makeDirectory(buildDirectory)).thenReturn(true);
        when(mockFTPClient.storeFile(eq(buildFileName), (InputStream)anyObject())).thenReturn(true);
        
        assertBuildStatusSuccess(project.scheduleBuild2(0).get());
        
        verify(mockFTPClient).connect(testHostConfig.getHostname(), testHostConfig.getPort());
        verify(mockFTPClient).storeFile(eq(buildFileName), (InputStream)anyObject());
        verify(mockFTPClient).setDefaultTimeout(testHostConfig.getTimeout());
        verify(mockFTPClient).setDataTimeout(testHostConfig.getTimeout());
    }
    
}
