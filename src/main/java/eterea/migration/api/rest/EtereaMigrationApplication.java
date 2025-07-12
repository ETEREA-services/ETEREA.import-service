package eterea.migration.api.rest;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = RefreshAutoConfiguration.class)
@EnableDiscoveryClient
public class EtereaMigrationApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EtereaMigrationApplication.class)
				.web(WebApplicationType.SERVLET)
				.run(args);
	}

}
