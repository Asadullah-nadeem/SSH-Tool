package org.example.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SFTPHandler {
    private static final Logger logger = LoggerFactory.getLogger(SFTPHandler.class);
    private static final int BUFFER_SIZE = 8192;

    public void uploadFile(Session session, String localPath, String remotePath)
            throws JSchException, SftpException, IOException {
        Path source = Paths.get(localPath);
        if (!Files.isRegularFile(source)) {
            throw new IOException("Source is not a file: " + localPath);
        }

        try (ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localPath))) {
            channel.connect();
            channel.put(bis, remotePath, ChannelSftp.OVERWRITE);
            logger.info("Uploaded: {} to {}", localPath, remotePath);
        }
    }

    public void downloadFile(Session session, String remotePath, String localPath)
            throws JSchException, SftpException, IOException {
        try (ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localPath))) {
            channel.connect();
            channel.get(remotePath, bos);
            logger.info("Downloaded: {} to {}", remotePath, localPath);
        }
    }
}