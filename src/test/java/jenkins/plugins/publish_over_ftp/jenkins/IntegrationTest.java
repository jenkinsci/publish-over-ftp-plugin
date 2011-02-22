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

package jenkins.plugins.publish_over_ftp.jenkins;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import jenkins.plugins.publish_over_ftp.BapFtpHostConfiguration;
import jenkins.plugins.publish_over_ftp.BapFtpPublisher;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.BapFtpTransfer;
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

//    @TODO test that we get the expected result when in a promotion

    public void testIntegration() throws Exception {
        final FTPClient mockFTPClient = mock(FTPClient.class);
        int port = 21;
        int timeout = 3000;
        BapFtpHostConfiguration testHostConfig = new BapFtpHostConfiguration("testConfig", "testHostname", "testUsername", "testPassword",
                                                                             "/testRemoteRoot", port, timeout, false) {
            @Override
            public FTPClient createFTPClient() {
                return mockFTPClient;
            }
        };
        new JenkinsTestHelper().setGlobalConfig(testHostConfig);
        final String dirToIgnore = "target";
        BapFtpTransfer transfer = new BapFtpTransfer("**/*", "sub-home", dirToIgnore, true, false, false);
        BapFtpPublisher publisher = new BapFtpPublisher(testHostConfig.getName(), false, Collections.singletonList(transfer));
        BapFtpPublisherPlugin plugin = new BapFtpPublisherPlugin(Collections.singletonList(publisher), false, false, false, "master");

        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(plugin);
        final String buildDirectory = "build-dir";
        final String buildFileName = "file.txt";
        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
                            throws InterruptedException, IOException {
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
        when(mockFTPClient.storeFile(eq(buildFileName), (InputStream) anyObject())).thenReturn(true);

        assertBuildStatusSuccess(project.scheduleBuild2(0).get());

        verify(mockFTPClient).connect(testHostConfig.getHostname(), testHostConfig.getPort());
        verify(mockFTPClient).storeFile(eq(buildFileName), (InputStream) anyObject());
        verify(mockFTPClient).setDefaultTimeout(testHostConfig.getTimeout());
        verify(mockFTPClient).setDataTimeout(testHostConfig.getTimeout());
    }

}
