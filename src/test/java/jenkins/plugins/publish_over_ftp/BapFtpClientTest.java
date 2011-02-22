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

import hudson.FilePath;
import jenkins.plugins.publish_over.BapPublisherException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class BapFtpClientTest {

    private static final String REMOTE_ROOT = "/my/remote/root";
    private static final String DIRECTORY = "a/directory";
    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException();
    private static final IOException IO_EXCEPTION = new IOException();
    private static Level originalLogLevel;

    @BeforeClass
    public static void before() {
        String packageName = getLoggerName();
        originalLogLevel = Logger.getLogger(packageName).getLevel();
        Logger.getLogger(packageName).setLevel(Level.OFF);
    }

    @AfterClass
    public static void after() {
        Logger.getLogger(getLoggerName()).setLevel(originalLogLevel);
    }

    private static String getLoggerName() {
        return BapFtpClient.class.getCanonicalName();
    }
    private IMocksControl mockControl = EasyMock.createStrictControl();
    private FTPClient mockFTPClient = mockControl.createMock(FTPClient.class);
    private BapFtpClient bapFtpClient = new BapFtpClient(mockFTPClient, null);

    @Before
    public void setUp() throws Exception {
        bapFtpClient.setAbsoluteRemoteRoot(REMOTE_ROOT);
    }

    @Test public void testChangeToInitialDirectorySuccess() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.changeToInitialDirectory());
        mockControl.verify();
    }

    @Test public void testChangeToInitialDirectoryFail() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.changeToInitialDirectory());
        mockControl.verify();
    }

    @Test public void testChangeToInitialDirectoryIOException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.changeToInitialDirectory();
            fail();
        } catch (BapPublisherException bpe) {
            assertSame(IO_EXCEPTION, bpe.getCause());
            assertTrue(bpe.getMessage().contains(REMOTE_ROOT));
        }
        mockControl.verify();
    }

    @Test public void testChangeToInitialDirectoryRuntimeException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.changeToInitialDirectory();
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }

    @Test public void testChangeDirectorySuccess() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.changeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test public void testChangeDirectoryFail() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.changeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test public void testChangeDirectoryIOException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.changeDirectory(DIRECTORY);
            fail();
        } catch (BapPublisherException bpe) {
            assertSame(IO_EXCEPTION, bpe.getCause());
            assertTrue(bpe.getMessage().contains(DIRECTORY));
        }
        mockControl.verify();
    }

    @Test public void testChangeDirectoryRuntimeException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.changeDirectory(DIRECTORY);
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }

    @Test public void testMakeDirectorySuccess() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test public void testMakeDirectoryFail() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test public void testMakeDirectoryIOException() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.makeDirectory(DIRECTORY);
            fail();
        } catch (BapPublisherException bpe) {
            assertSame(IO_EXCEPTION, bpe.getCause());
            assertTrue(bpe.getMessage().contains(DIRECTORY));
        }
        mockControl.verify();
    }

    @Test public void testMakeDirectoryRuntimeException() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.makeDirectory(DIRECTORY);
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }

    @Test public void testBeginTransfersAscii() throws Exception {
        expect(mockFTPClient.setFileType(FTP.ASCII_FILE_TYPE)).andReturn(true);
        mockControl.replay();
        bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", true, false, false));
        mockControl.verify();
    }

    @Test public void testBeginTransfersBinary() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        mockControl.replay();
        bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false));
        mockControl.verify();
    }

    @Test public void testBeginTransfersFail() throws Exception {
        String why = "123 Something went wrong!";
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(false);
        expect(mockFTPClient.getReplyString()).andReturn(why);
        mockControl.replay();
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false));
            fail();
        } catch (BapPublisherException bpe) {
            assertTrue(bpe.getMessage().contains(why));
        }
        mockControl.verify();
    }

    @Test public void testBeginTransfersIOException() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false));
            fail();
        } catch (BapPublisherException bpe) {
            assertSame(IO_EXCEPTION, bpe.getCause());
        }
        mockControl.verify();
    }

    @Test public void testBeginTransfersRuntimeException() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false));
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }

    @Test public void testSetBeginTransfersFailIfNoSourceFiles() throws Exception {
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", false, false, false));
            fail();
        } catch (BapPublisherException bpe) {
            assertEquals(Messages.exception_noSourceFiles(), bpe.getMessage());
        }
    }

    @Test public void testTransferFileSuccess() throws Exception {
        TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream))).andReturn(true);
        mockControl.replay();
        bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream);
        mockControl.verify();
    }

    @Test public void testTransferFileFail() throws Exception {
        String why = "123 Something went wrong!";
        TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream))).andReturn(false);
        expect(mockFTPClient.getReplyString()).andReturn(why);
        mockControl.replay();
        try {
            bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream);
            fail();
        } catch (BapPublisherException bpe) {
            assertTrue(bpe.getMessage().contains(why));
        }
        mockControl.verify();
    }

    @Test public void testTransferFileIOException() throws Exception {
        TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream))).andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream);
            fail();
        } catch (IOException ioe) {
            assertSame(IO_EXCEPTION, ioe);
        }
        mockControl.verify();
    }

    @Test public void testTransferFileRuntimeException() throws Exception {
        TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream))).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream);
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }

    @Test public void testDisconnectDoesNothingIfNotConnected() {
        expect(mockFTPClient.isConnected()).andReturn(false);
        mockControl.replay();
        bapFtpClient.disconnect();
        mockControl.verify();
    }

    @Test public void testDisconnectSuccess() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        mockControl.replay();
        bapFtpClient.disconnect();
        mockControl.verify();
    }

    @Test public void testDisconnectIOException() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        expectLastCall().andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.disconnect();
            fail();
        } catch (BapPublisherException bpe) {
            assertSame(IO_EXCEPTION, bpe.getCause());
        }
        mockControl.verify();
    }

    @Test public void testDisconnectRuntimeException() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        expectLastCall().andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.disconnect();
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }

    @Test public void testDisconnectQuietly() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        expectLastCall().andThrow(IO_EXCEPTION);
        mockControl.replay();
        bapFtpClient.disconnectQuietly();
        mockControl.verify();
    }

    private TransferFileArgs createTestArgs() {
        return new TransferFileArgs();
    }

    private class TransferFileArgs {
        private BapFtpTransfer bapFtpTransfer = new BapFtpTransfer("", "", "", false, false, false);
        private InputStream inputStream = mockControl.createMock(InputStream.class);
        private FilePath filePath = new FilePath(new File("myFile"));
    }

}
