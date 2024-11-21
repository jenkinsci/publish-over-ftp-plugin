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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import jenkins.plugins.publish_over_ftp.BapFtpHostConfiguration;
import jenkins.plugins.publish_over_ftp.BapFtpPublisher;
import jenkins.plugins.publish_over_ftp.BapFtpPublisherPlugin;
import jenkins.plugins.publish_over_ftp.BapFtpTransfer;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class IntegrationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private static final String TEST_PASSWORD = "testPassword";

    //    @TODO test that we get the expected result when in a promotion

    @Test
    public void testIntegration() throws Exception {
        final FTPClient mockFTPClient = mock(FTPClient.class);
        final int port = 21;
        final int timeout = 3000;
        final BapFtpHostConfiguration testHostConfig =
                new BapFtpHostConfiguration(
                        "testConfig",
                        "testHostname",
                        "testUsername",
                        TEST_PASSWORD,
                        "/testRemoteRoot",
                        port,
                        timeout,
                        false,
                        null,
                        false,
                        false) {
                    @Override
                    public FTPClient createFTPClient() {
                        return mockFTPClient;
                    }
                };
        new JenkinsTestHelper().setGlobalConfig(testHostConfig);
        final String dirToIgnore = "target";
        final BapFtpTransfer transfer = new BapFtpTransfer(
                "**/*", null, "sub-home", dirToIgnore, true, false, false, false, false, false, null);
        final ArrayList<BapFtpTransfer> transfers = new ArrayList<>(Collections.singletonList(transfer));
        final BapFtpPublisher publisher =
                new BapFtpPublisher(testHostConfig.getName(), false, transfers, false, false, null, null, null);
        final ArrayList<BapFtpPublisher> publishers = new ArrayList<>(Collections.singletonList(publisher));
        final BapFtpPublisherPlugin plugin = new BapFtpPublisherPlugin(publishers, false, false, false, "master", null);

        final FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(plugin);
        final String buildDirectory = "build-dir";
        final String buildFileName = "file.txt";
        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(
                    final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
                    throws InterruptedException, IOException {
                final FilePath dir = build.getWorkspace().child(dirToIgnore).child(buildDirectory);
                dir.mkdirs();
                dir.child(buildFileName).write("Helloooooo", "UTF-8");
                build.setResult(Result.SUCCESS);
                return true;
            }
        });

        when(mockFTPClient.getReplyCode()).thenReturn(FTPReply.SERVICE_READY);
        when(mockFTPClient.login(testHostConfig.getUsername(), TEST_PASSWORD)).thenReturn(true);
        when(mockFTPClient.changeWorkingDirectory(testHostConfig.getRemoteRootDir()))
                .thenReturn(true);
        when(mockFTPClient.setFileType(FTPClient.ASCII_FILE_TYPE)).thenReturn(true);
        when(mockFTPClient.changeWorkingDirectory(transfer.getRemoteDirectory()))
                .thenReturn(false)
                .thenReturn(true);
        when(mockFTPClient.makeDirectory(transfer.getRemoteDirectory())).thenReturn(true);
        when(mockFTPClient.changeWorkingDirectory(buildDirectory))
                .thenReturn(false)
                .thenReturn(true);
        when(mockFTPClient.makeDirectory(buildDirectory)).thenReturn(true);
        when(mockFTPClient.storeFile(eq(buildFileName), any())).thenReturn(true);

        j.assertBuildStatusSuccess(project.scheduleBuild2(0).get());

        verify(mockFTPClient).connect(testHostConfig.getHostname(), testHostConfig.getPort());
        verify(mockFTPClient).storeFile(eq(buildFileName), any());
        verify(mockFTPClient).setDefaultTimeout(testHostConfig.getTimeout());
        verify(mockFTPClient).setDataTimeout(testHostConfig.getTimeout());
    }
}
