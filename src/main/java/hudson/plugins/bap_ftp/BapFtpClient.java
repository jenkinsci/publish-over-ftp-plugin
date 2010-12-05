package hudson.plugins.bap_ftp;

import hudson.FilePath;
import hudson.plugins.bap_publisher.BPBuildInfo;
import hudson.plugins.bap_publisher.BPDefaultClient;
import hudson.plugins.bap_publisher.BapPublisherException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

public class BapFtpClient extends BPDefaultClient<BapFtpTransfer> {
    
    private static final Log LOG = LogFactory.getLog(BapFtpClient.class);

    private BPBuildInfo buildInfo;
    private FTPClient ftpClient;
    private String absoluteRemoteRoot;

    public BapFtpClient(FTPClient ftpClient, BPBuildInfo buildInfo) {
        this.ftpClient = ftpClient;
        this.buildInfo = buildInfo;
    }

    public FTPClient getFtpClient() { return ftpClient; }
    public void setFtpClient(FTPClient ftpClient) { this.ftpClient = ftpClient; }

    public BPBuildInfo getBuildInfo() { return buildInfo; }
    public void setBuildInfo(BPBuildInfo buildInfo) { this.buildInfo = buildInfo; }

    public String getAbsoluteRemoteRoot() { return absoluteRemoteRoot; }
    public void setAbsoluteRemoteRoot(String absoluteRemoteRoot) { this.absoluteRemoteRoot = absoluteRemoteRoot; }

    public boolean changeToInitialDirectory() {
        return changeDirectory(absoluteRemoteRoot);
    }

    public boolean changeDirectory(String directory) {
        try {
            return ftpClient.changeWorkingDirectory(directory);
        } catch (IOException ioe) {
            throw new BapPublisherException(Messages.exception_cwdException(directory), ioe);
        }
    }

    public boolean makeDirectory(String directory) {
        try {
            return ftpClient.makeDirectory(directory);
        } catch (IOException ioe) {
            throw new BapPublisherException(Messages.exception_mkdirException(directory), ioe);
        }
    }

    public void beginTransfers(BapFtpTransfer transfer) {
        try {
            if(!setTransferMode(transfer)) {
                throw new BapPublisherException(Messages.exception_failedToSetTransferMode(ftpClient.getReplyString()));
            }
        } catch (IOException ioe) {
            throw new BapPublisherException(Messages.exception_exceptionSettingTransferMode(), ioe);
        }
    }

    public void transferFile(BapFtpTransfer client, FilePath filePath, InputStream content) throws IOException {
        if(!ftpClient.storeFile(filePath.getName(), content)) {
            throw new BapPublisherException(Messages.exception_failedToStoreFile(ftpClient.getReplyString()));
        }
    }

    public void disconnect() {
        if ((ftpClient != null) && ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException ioe) {
                throw new BapPublisherException(Messages.exception_exceptionOnDisconnect(ioe.getLocalizedMessage()), ioe);
            }
        }
    }

    public void disconnectQuietly() {
        try {
            disconnect();
        } catch (Throwable throwable) {
            LOG.warn(Messages.log_disconnectQuietly(), throwable);
        }
    }

    private boolean setTransferMode(BapFtpTransfer transfer) throws IOException {
        int fileType = transfer.isAsciiMode() ? FTP.ASCII_FILE_TYPE : FTP.BINARY_FILE_TYPE;
        return ftpClient.setFileType(fileType);
    }

}
