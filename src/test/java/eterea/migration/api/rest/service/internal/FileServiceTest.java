package eterea.migration.api.rest.service.internal;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private FileService fileService;

    @Test
    void whenGetFile_andSftpSucceeds_thenFileIsDownloaded() throws Exception {
        // given
        when(environment.getProperty("app.hours-offset")).thenReturn("1");
        when(environment.getProperty("app.ftp-host")).thenReturn("localhost");
        when(environment.getProperty("app.ftp-user")).thenReturn("testuser");
        when(environment.getProperty("app.ftp-password")).thenReturn("password");
        when(environment.getProperty("app.local-path")).thenReturn("/tmp/");

        // This is the modern way to mock constructor calls with Mockito-inline
        try (MockedConstruction<JSch> mockedJsch = mockConstruction(JSch.class,
                (jschMock, context) -> {
                    Session sessionMock = mock(Session.class);
                    ChannelSftp channelSftpMock = mock(ChannelSftp.class);

                    when(jschMock.getSession(anyString(), anyString(), anyInt())).thenReturn(sessionMock);
                    when(sessionMock.openChannel("sftp")).thenReturn(channelSftpMock);
                })) {

            // when
            String result = fileService.getFile();

            // then
            // We expect one JSch object to be created
            assertThat(mockedJsch.constructed()).hasSize(1);
            JSch jschInstance = mockedJsch.constructed().get(0);
            Session sessionInstance = jschInstance.getSession("testuser", "localhost", 22);
            ChannelSftp channelInstance = (ChannelSftp) sessionInstance.openChannel("sftp");

            verify(sessionInstance, times(1)).connect();
            verify(channelInstance, times(1)).connect();
            verify(channelInstance, times(1)).get(anyString(), anyString());
            verify(channelInstance, times(1)).exit();
            verify(sessionInstance, times(1)).disconnect();

            assertThat(result).startsWith("/tmp/orders-");
            assertThat(result).endsWith(".json");
        }
    }
}