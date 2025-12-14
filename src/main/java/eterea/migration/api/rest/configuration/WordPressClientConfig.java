package eterea.migration.api.rest.configuration;

import eterea.migration.api.rest.client.WordPressApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WordPressClientConfig {

   private final Environment environment;

   public WordPressClientConfig(Environment environment) {
      this.environment = environment;
   }

   @Bean(name = "wordPressApiClientSite1")
   public WordPressApiClient wordPressApiClientSite1() {
      String baseUrl = environment.getProperty("app.wordpress.site1.base-url");
      String username = environment.getProperty("app.wordpress.site1.username");
      String password = environment.getProperty("app.wordpress.site1.password");
      String siteName = environment.getProperty("app.wordpress.site1.name", "site1");
      return new WordPressApiClient(baseUrl, username, password, siteName);
   }

   @Bean(name = "wordPressApiClientSite2")
   public WordPressApiClient wordPressApiClientSite2() {
      String baseUrl = environment.getProperty("app.wordpress.site2.base-url");
      String username = environment.getProperty("app.wordpress.site2.username");
      String password = environment.getProperty("app.wordpress.site2.password");
      String siteName = environment.getProperty("app.wordpress.site2.name", "site2");
      return new WordPressApiClient(baseUrl, username, password, siteName);
   }
}
