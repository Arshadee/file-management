package io.filemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = { "io.filemanagement" } )
@EnableJpaRepositories
@EnableRetry
public class FileManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagementApplication.class, args);
	}

}
