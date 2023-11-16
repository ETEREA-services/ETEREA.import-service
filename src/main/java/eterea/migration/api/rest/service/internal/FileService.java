package eterea.migration.api.rest.service.internal;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FileService {

    public String getFile() {

        LocalDateTime time = LocalDateTime.now().minusHours(4);
        int year = time.getYear();
        int month = time.getMonthValue();
        int day = time.getDayOfMonth();
        int hour = time.getHour();
        String filename = "orders-" + year + "-" + month + "-" + day + "-" + String.format("%02d", hour) + ".json";

        String ftpHost = "119.8.73.151";
        int ftpPort = 22;
        String ftpUser = "ftp-agencia";
        String ftpPassword = "Agn123321Ines";
        String remoteFilePath = "/home/ftp-agencia/" + filename;
        filename = "/data/temp/" + filename;
        log.info("filename={}", filename);

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
