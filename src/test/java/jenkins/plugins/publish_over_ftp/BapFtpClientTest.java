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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.same;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.plugins.publish_over.BapPublisherException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.TooManyMethods"})
class BapFtpClientTest {

    private static final Logger BFTP_CLIENT_LOGGER = Logger.getLogger(BapFtpClient.class.getCanonicalName());
    private static final String REMOTE_ROOT = "/my/remote/root";
    private static final String DIRECTORY = "a/directory";
    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException();
    private static final IOException IO_EXCEPTION = new IOException();
    private static Level originalLogLevel;

    private final transient IMocksControl mockControl = EasyMock.createStrictControl();
    private final transient FTPClient mockFTPClient = mockControl.createMock(FTPClient.class);
    private final transient BapFtpClient bapFtpClient = new BapFtpClient(mockFTPClient, null);

    @BeforeAll
    static void beforeAll() {
        originalLogLevel = BFTP_CLIENT_LOGGER.getLevel();
        BFTP_CLIENT_LOGGER.setLevel(Level.OFF);
    }

    @AfterAll
    static void afterAll() {
        BFTP_CLIENT_LOGGER.setLevel(originalLogLevel);
    }

    @BeforeEach
    void beforeEach() {
        bapFtpClient.setAbsoluteRemoteRoot(REMOTE_ROOT);
    }

    @Test
    void testChangeToInitialDirectorySuccess() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.changeToInitialDirectory());
        mockControl.verify();
    }

    @Test
    void testChangeToInitialDirectoryFail() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.changeToInitialDirectory());
        mockControl.verify();
    }

    @Test
    void testChangeToInitialDirectoryIOException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andThrow(IO_EXCEPTION);
        mockControl.replay();

        BapPublisherException bpe = assertThrows(BapPublisherException.class, bapFtpClient::changeToInitialDirectory);
        assertSame(IO_EXCEPTION, bpe.getCause());
        assertTrue(bpe.getMessage().contains(REMOTE_ROOT));

        mockControl.verify();
    }

    @Test
    void testChangeToInitialDirectoryRuntimeException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(REMOTE_ROOT)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();

        RuntimeException re = assertThrows(RuntimeException.class, bapFtpClient::changeToInitialDirectory);
        assertSame(RUNTIME_EXCEPTION, re);

        mockControl.verify();
    }

    @Test
    void testChangeDirectorySuccess() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.changeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test
    void testChangeDirectoryFail() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.changeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test
    void testChangeDirectoryIOException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andThrow(IO_EXCEPTION);
        mockControl.replay();

        BapPublisherException bpe =
                assertThrows(BapPublisherException.class, () -> bapFtpClient.changeDirectory(DIRECTORY));
        assertSame(IO_EXCEPTION, bpe.getCause());
        assertTrue(bpe.getMessage().contains(DIRECTORY));

        mockControl.verify();
    }

    @Test
    void testChangeDirectoryRuntimeException() throws Exception {
        expect(mockFTPClient.changeWorkingDirectory(DIRECTORY)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();

        RuntimeException re = assertThrows(RuntimeException.class, () -> bapFtpClient.changeDirectory(DIRECTORY));
        assertSame(RUNTIME_EXCEPTION, re);

        mockControl.verify();
    }

    @Test
    void testMakeDirectorySuccess() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(true);
        mockControl.replay();
        assertTrue(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test
    void testMakeDirectoryFail() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test
    void testMakeDirectoryWillAttemptNested() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andReturn(false);
        mockControl.replay();
        assertFalse(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test
    void testMakeDirectoryWillNotAttemptNested() {
        bapFtpClient.setDisableMakeNestedDirs(true);
        mockControl.replay();
        assertFalse(bapFtpClient.makeDirectory(DIRECTORY));
        mockControl.verify();
    }

    @Test
    void testMakeDirectoryIOException() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andThrow(IO_EXCEPTION);
        mockControl.replay();

        BapPublisherException bpe =
                assertThrows(BapPublisherException.class, () -> bapFtpClient.makeDirectory(DIRECTORY));
        assertSame(IO_EXCEPTION, bpe.getCause());
        assertTrue(bpe.getMessage().contains(DIRECTORY));

        mockControl.verify();
    }

    @Test
    void testMakeDirectoryRuntimeException() throws Exception {
        expect(mockFTPClient.makeDirectory(DIRECTORY)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();

        RuntimeException re = assertThrows(RuntimeException.class, () -> bapFtpClient.makeDirectory(DIRECTORY));
        assertSame(RUNTIME_EXCEPTION, re);

        mockControl.verify();
    }

    @Test
    void testBeginTransfersAscii() throws Exception {
        expect(mockFTPClient.setFileType(FTP.ASCII_FILE_TYPE)).andReturn(true);
        mockControl.replay();
        bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", true, false, false));
        mockControl.verify();
    }

    @Test
    void testBeginTransfersBinary() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        mockControl.replay();
        bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false));
        mockControl.verify();
    }

    @Test
    void testBeginTransfersFail() throws Exception {
        final String why = "123 Something went wrong!";
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(false);
        expect(mockFTPClient.getReplyString()).andReturn(why);
        mockControl.replay();

        BapPublisherException bpe = assertThrows(
                BapPublisherException.class,
                () -> bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false)));
        assertTrue(bpe.getMessage().contains(why));

        mockControl.verify();
    }

    @Test
    void testBeginTransfersIOException() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andThrow(IO_EXCEPTION);
        mockControl.replay();

        BapPublisherException bpe = assertThrows(
                BapPublisherException.class,
                () -> bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false)));
        assertSame(IO_EXCEPTION, bpe.getCause());

        mockControl.verify();
    }

    @Test
    void testBeginTransfersRuntimeException() throws Exception {
        expect(mockFTPClient.setFileType(FTP.BINARY_FILE_TYPE)).andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();

        RuntimeException re = assertThrows(
                RuntimeException.class,
                () -> bapFtpClient.beginTransfers(new BapFtpTransfer("*", "", "", false, false, false)));
        assertSame(RUNTIME_EXCEPTION, re);

        mockControl.verify();
    }

    @Test
    void testSetBeginTransfersFailIfNoSourceFiles() throws Exception {
        BapPublisherException bpe = assertThrows(
                BapPublisherException.class,
                () -> bapFtpClient.beginTransfers(new BapFtpTransfer("", "", "", false, false, false)));
        assertEquals(Messages.exception_noSourceFiles(), bpe.getMessage());
    }

    @Test
    void testTransferFileSuccess() throws Exception {
        final TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream)))
                .andReturn(true);
        mockControl.replay();
        bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream);
        mockControl.verify();
    }

    @Test
    void testTransferFileFail() throws Exception {
        final String why = "123 Something went wrong!";
        final TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream)))
                .andReturn(false);
        expect(mockFTPClient.getReplyString()).andReturn(why);
        mockControl.replay();

        BapPublisherException bpe = assertThrows(
                BapPublisherException.class,
                () -> bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream));
        assertTrue(bpe.getMessage().contains(why));

        mockControl.verify();
    }

    @Test
    void testTransferFileIOException() throws Exception {
        final TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream)))
                .andThrow(IO_EXCEPTION);
        mockControl.replay();

        IOException ioe = assertThrows(
                IOException.class,
                () -> bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream));
        assertSame(IO_EXCEPTION, ioe);

        mockControl.verify();
    }

    @Test
    void testTransferFileRuntimeException() throws Exception {
        final TransferFileArgs args = createTestArgs();
        expect(mockFTPClient.storeFile(eq(args.filePath.getName()), same(args.inputStream)))
                .andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();

        RuntimeException re = assertThrows(
                RuntimeException.class,
                () -> bapFtpClient.transferFile(args.bapFtpTransfer, args.filePath, args.inputStream));
        assertSame(RUNTIME_EXCEPTION, re);

        mockControl.verify();
    }

    @Test
    void testDisconnectDoesNothingIfNotConnected() {
        expect(mockFTPClient.isConnected()).andReturn(false);
        mockControl.replay();
        bapFtpClient.disconnect();
        mockControl.verify();
    }

    @Test
    void testDisconnectSuccess() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        mockControl.replay();
        bapFtpClient.disconnect();
        mockControl.verify();
    }

    @Test
    void testDisconnectIOException() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        expectLastCall().andThrow(IO_EXCEPTION);
        mockControl.replay();

        BapPublisherException bpe = assertThrows(BapPublisherException.class, bapFtpClient::disconnect);
        assertSame(IO_EXCEPTION, bpe.getCause());

        mockControl.verify();
    }

    @Test
    void testDisconnectRuntimeException() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        expectLastCall().andThrow(RUNTIME_EXCEPTION);
        mockControl.replay();

        RuntimeException re = assertThrows(RuntimeException.class, bapFtpClient::disconnect);
        assertSame(RUNTIME_EXCEPTION, re);

        mockControl.verify();
    }

    @Test
    void testDisconnectQuietly() throws Exception {
        expect(mockFTPClient.isConnected()).andReturn(true);
        mockFTPClient.disconnect();
        expectLastCall().andThrow(IO_EXCEPTION);
        mockControl.replay();
        bapFtpClient.disconnectQuietly();
        mockControl.verify();
    }

    @Test
    void testDeleteTreeDeletesFiles() throws Exception {
        mockFTPClient.setListHiddenFiles(true);
        final FTPListParseEngine mockListEngine = mockControl.createMock(FTPListParseEngine.class);
        expect(mockFTPClient.hasFeature("MLST")).andReturn(false);
        expect(mockFTPClient.initiateListParsing()).andReturn(mockListEngine);
        expectDeleteFiles(mockListEngine, "file1", "file2", "anotherOne");
        expect(mockListEngine.hasNext()).andReturn(false);
        mockControl.replay();
        bapFtpClient.deleteTree();
        mockControl.verify();
    }

    @Test
    void testDeleteTreeDeletesFilesMLST() throws Exception {
        mockFTPClient.setListHiddenFiles(true);
        final FTPFile[] files = new FTPFile[3];
        final String[] fileNames = new String[] {"file1", "file2", "anotherOne"};
        for (int i = 0; i < files.length; i++) {
            files[i] = mock(FTPFile.class);
            when(files[i].getName()).thenReturn(fileNames[i]);
        }
        expect(mockFTPClient.hasFeature("MLST")).andReturn(true);
        expect(mockFTPClient.mlistDir()).andReturn(files);
        for (String fileName : fileNames) {
            expect(mockFTPClient.deleteFile(fileName)).andReturn(true);
        }
        mockControl.replay();
        bapFtpClient.deleteTree();
        mockControl.verify();
    }

    @Test
    void testDeleteTreeIgnoresCurrentDirAndParentDirEntries() throws Exception {
        mockFTPClient.setListHiddenFiles(true);
        final FTPListParseEngine mockListEngine = mockControl.createMock(FTPListParseEngine.class);
        expect(mockFTPClient.hasFeature("MLST")).andReturn(false);
        expect(mockFTPClient.initiateListParsing()).andReturn(mockListEngine);
        expectFtpFile(mockListEngine, ".");
        expectFtpFile(mockListEngine, "..");
        expectDeleteFiles(mockListEngine, "file1", "file2", "anotherOne");
        expect(mockListEngine.hasNext()).andReturn(false);
        mockControl.replay();
        bapFtpClient.deleteTree();
        mockControl.verify();
    }

    @Test
    void testDeleteTreeDeletesDirectoryWithFiles() throws Exception {
        mockFTPClient.setListHiddenFiles(true);
        final FTPListParseEngine mockListEngine = mockControl.createMock(FTPListParseEngine.class);
        expect(mockFTPClient.hasFeature("MLST")).andReturn(false);
        expect(mockFTPClient.initiateListParsing()).andReturn(mockListEngine);
        final String dirname = "directory";
        expectDirectory(mockListEngine, dirname);
        expect(mockFTPClient.changeWorkingDirectory(dirname)).andReturn(true);

        final FTPListParseEngine mockListEngineSubDir = mockControl.createMock(FTPListParseEngine.class);
        expect(mockFTPClient.hasFeature("MLST")).andReturn(false);
        expect(mockFTPClient.initiateListParsing()).andReturn(mockListEngineSubDir);
        expectDeleteFiles(mockListEngineSubDir, "file1", "file2", "anotherOne");
        expect(mockListEngineSubDir.hasNext()).andReturn(false);

        expect(mockFTPClient.changeToParentDirectory()).andReturn(true);
        expect(mockFTPClient.removeDirectory(dirname)).andReturn(true);
        expect(mockListEngine.hasNext()).andReturn(false);
        mockControl.replay();
        bapFtpClient.deleteTree();
        mockControl.verify();
    }

    private void expectDeleteFiles(final FTPListParseEngine mockListEngine, final String... filenames)
            throws Exception {
        for (final String filename : filenames) {
            expectFile(mockListEngine, filename);
            expect(mockFTPClient.deleteFile(filename)).andReturn(true);
        }
    }

    private FTPFile expectFile(final FTPListParseEngine mockListEngine, final String filename) {
        final FTPFile file = expectFtpFile(mockListEngine, filename);
        expect(file.isDirectory()).andReturn(false);
        return file;
    }

    private FTPFile expectDirectory(final FTPListParseEngine mockListEngine, final String dirname) {
        final FTPFile dir = expectFtpFile(mockListEngine, dirname);
        expect(dir.isDirectory()).andReturn(true);
        return dir;
    }

    private FTPFile expectFtpFile(final FTPListParseEngine mockListEngine, final String filename) {
        expect(mockListEngine.hasNext()).andReturn(true);
        final FTPFile file = mockControl.createMock(FTPFile.class);
        expect(mockListEngine.getNext(1)).andReturn(new FTPFile[] {file});
        expect(file.getName()).andReturn(filename);
        return file;
    }

    private TransferFileArgs createTestArgs() {
        return new TransferFileArgs();
    }

    private class TransferFileArgs {
        private final BapFtpTransfer bapFtpTransfer = new BapFtpTransfer("", "", "", false, false, false);
        private final InputStream inputStream = mockControl.createMock(InputStream.class);
        private final FilePath filePath = new FilePath(new File("myFile"));
    }
}
