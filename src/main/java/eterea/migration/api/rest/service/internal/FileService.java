package eterea.migration.api.rest.service.internal;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FileService {

    private final Environment environment;

    @Autowired
    public FileService(Environment environment) {
        this.environment = environment;
    }

    public String getFile() {

        Integer hoursOffset = Integer.valueOf(environment.getProperty("app.hours-offset"));
        LocalDateTime time = LocalDateTime.now().minusHours(hoursOffset);
        int year = time.getYear();
        int month = time.getMonthValue();
        int day = time.getDayOfMonth();
        int hour = time.getHour();
        String filename = "orders-" + year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + "-" + String.format("%02d", hour) + ".json";

        String ftpHost = environment.getProperty("app.ftp-host");
        int ftpPort = 22;
        String ftpUser = environment.getProperty("app.ftp-user");
        String ftpPassword = environment.getProperty("app.ftp-password");
        String remoteFilePath = "/home/ftp-agencia/" + filename;
        filename = environment.getProperty("app.local-path") + filename;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(ftpUser, ftpHost, ftpPort);
            session.setPassword(ftpPassword);

            // Configurar StrictHostKeyChecking para evitar la verificación del host
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(remoteFilePath, filename);

            sftpChannel.exit();
            session.disconnect();
            System.out.println("Archivo descargado exitosamente.");
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }

        return filename;
    }

}
