package hudson.plugins.bap_ftp;

import hudson.FilePath;
import hudson.plugins.bap_publisher.BapPublisherException;
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
    
    @Test public void testChangeToInitialDirectory_success() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.changeToInitialDirectory());
        mockControl.verify();
    }
    
    @Test public void testChangeToInitialDirectory_fail() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.changeToInitialDirectory());
        mockControl.verify();
    }
    
    @Test public void testChangeToInitialDirectory_IOException() throws Exception {
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
    
    @Test public void testChangeToInitialDirectory_RuntimeException() throws Exception {
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
    
    @Test public void testChangeDirectory_success() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.changeDirectory(DIRECTORY));
        mockControl.verify();
    }
    
    @Test public void testChangeDirectory_fail() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.changeDirectory(DIRECTORY));
        mockControl.verify();
    }
    
    @Test public void testChangeDirectory_IOException() throws Exception {
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
    
    @Test public void testChangeDirectory_RuntimeException() throws Exception {
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
    
    @Test public void testMakeDirectory_success() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }
    
    @Test public void testMakeDirectory_fail() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }
    
    @Test public void testMakeDirectory_IOException() throws Exception {
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
    
    @Test public void testMakeDirectory_RuntimeException() throws Exception {
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
    
    @Test public void testSetBeginTransfers_ascii() throws Exception {
        expect(mockFTPClient.setFileType(FTP.ASCII_FILE_TYPE)).andReturn(true);
        mockControl.replay();
        bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", true, false, false));
        mockControl.verify();
    }
    
    @Test public void testSetBeginTransfers_binary() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        mockControl.replay();
        bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", false, false, false));
        mockControl.verify();
    }
    
    @Test public void testSetBeginTransfers_fail() throws Exception {
        String why = "123 Something went wrong!";
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(false);
        expect(mockFTPClient.getReplyString()).andReturn(why);
        mockControl.replay();
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", false, false, false));
            fail();
        } catch (BapPublisherException bpe) {
            assertTrue(bpe.getMessage().contains(why));
        }
        mockControl.verify();
    }
    
    @Test public void testSetBeginTransfers_IOException() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andThrow(IO_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", false, false, false));
            fail();
        } catch (BapPublisherException bpe) {
            assertSame(IO_EXCEPTION, bpe.getCause());
        }
        mockControl.verify();
    }
    
    @Test public void testSetBeginTransfers_RuntimeException() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();
        try {
            bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", false, false, false));
            fail();
        } catch (RuntimeException re) {
            assertSame(RUNTIME_EXCEPTION, re);
        }
        mockControl.verify();
    }
    
    @Test public void testTransferFile_success() throws Exception {
        TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream))).andReturn(true);
        mockControl.replay();
        bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream);
        mockControl.verify();
    }
    
    @Test public void testTransferFile_fail() throws Exception {
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
    
    @Test public void testTransferFile_IOException() throws Exception {
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
    
    @Test public void testTransferFile_RuntimeException() throws Exception {
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
    
    @Test public void testDisconnect_doesNothingIfNotConnected() {
        expect(mockFTPClient.isConnected()).andReturn(false);
        mockControl.replay();
        bapFtpClient.disconnect();
        mockControl.verify();
    }
    
    @Test public void testDisconnect_success() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        mockControl.replay();
        bapFtpClient.disconnect();
        mockControl.verify();
    }
    
    @Test public void testDisconnect_IOException() throws Exception {
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
    
    @Test public void testDisconnect_RuntimeException() throws Exception {
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
        BapFtpTransfer bapFtpTransfer = new BapFtpTransfer("", "", "", false, false, false);
        InputStream inputStream = mockControl.createMock(InputStream.class);
        FilePath filePath = new FilePath(new File("myFile"));
    }

}
