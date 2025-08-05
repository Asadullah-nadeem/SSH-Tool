package org.example.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localPath))) {
                channel.put(bis, remotePath, ChannelSftp.OVERWRITE);
                logger.info("Uploaded: {} to {}", localPath, remotePath);
            }
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    public void downloadFile(Session session, String remotePath, String localPath)
            throws JSchException, SftpException, IOException {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localPath))) {
                channel.get(remotePath, bos);
                logger.info("Downloaded: {} to {}", remotePath, localPath);
            }
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }
}