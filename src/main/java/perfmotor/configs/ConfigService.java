package perfmotor.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigService {

  /**
   * Generating rest template with custom configurations.
   *
   * @return Configured RestTemplate
   */
  @Bean
  public RestTemplate configureRestTemplate() {
    return new RestTemplateBuilder()
//        .setConnectTimeout(configProperties.getRestService().getConnectTimeout())
//        .setReadTimeout(configProperties.getRestService().getReadTimeout())
        .build();
  }
}
