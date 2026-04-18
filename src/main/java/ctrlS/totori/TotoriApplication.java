package ctrlS.totori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TotoriApplication {

	public static void main(String[] args) {
		SpringApplication.run(TotoriApplication.class, args);
	}

}
