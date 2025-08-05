package org.example.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class SSHCommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(SSHCommandExecutor.class);
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int EXEC_TIMEOUT = 30;

    public String executeCommand(Session session, String command) throws JSchException, IOException, InterruptedException {
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);

            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();

            channel.setOutputStream(outputBuffer);
            channel.setErrStream(errorBuffer);
            channel.connect(CONNECT_TIMEOUT);

            // Wait for command completion with timeout
            long startTime = System.currentTimeMillis();
            while (!channel.isClosed()) {
                if (System.currentTimeMillis() - startTime > TimeUnit.SECONDS.toMillis(EXEC_TIMEOUT)) {
                    throw new IOException("Command execution timed out");
                }
                Thread.sleep(100);
            }

            String result = outputBuffer.toString(StandardCharsets.UTF_8);
            String errors = errorBuffer.toString(StandardCharsets.UTF_8);

            if (!errors.isEmpty()) {
                logger.error("Command error output: {}", errors);
                throw new IOException("Command execution failed: " + errors);
            }

            return result;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}