package in.nnisarg.ezmail.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import in.nnisarg.ezmail.email.config.EmailConfig;

@SpringBootApplication
@EnableConfigurationProperties(EmailConfig.class)
public class EmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailApplication.class, args);
	}

}
