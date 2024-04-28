package eterea.migration.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EtereaMigrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(EtereaMigrationApplication.class, args);
	}

}
