package org.example.ssh;

import com.jcraft.jsch.*;
import java.io.*;

public class SFTPManager {

    public void uploadFile(Session session, String localPath, String remotePath) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.put(localPath, remotePath);
        channel.disconnect();
    }

    public void downloadFile(Session session, String remotePath, String localPath) throws Exception {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.get(remotePath, localPath);
        channel.disconnect();
    }
}
