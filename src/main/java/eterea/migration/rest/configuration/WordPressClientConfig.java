package eterea.migration.rest.configuration;

import eterea.migration.rest.client.WordPressApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WordPressClientConfig {

   private final Environment environment;

   public WordPressClientConfig(Environment environment) {
      this.environment = environment;
   }

   @Bean(name = "wordPressApiClient")
   public WordPressApiClient wordPressApiClient() {
      String baseUrl = environment.getProperty("app.wordpress.site.base-url");
      String username = environment.getProperty("app.wordpress.site.username");
      String password = environment.getProperty("app.wordpress.site.password");
      String siteName = environment.getProperty("app.wordpress.site.name", "site");
      return new WordPressApiClient(baseUrl, username, password, siteName);
   }
}
