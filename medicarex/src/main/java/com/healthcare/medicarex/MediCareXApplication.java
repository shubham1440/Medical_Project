package com.healthcare.medicarex;

import com.healthcare.config.properties.SecurityProperties;
import com.healthcare.dto.request.RegisterRequest;
import com.healthcare.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
@EnableAsync
@EntityScan(basePackages = "com.healthcare.models")
@EnableJpaRepositories(basePackages = "com.healthcare.repo")
@ComponentScan(basePackages = {"com.healthcare"})
@AllArgsConstructor
public class MediCareXApplication
//        implements CommandLineRunner
{

    private final AuthService authService;

	public static void main(String[] args) {
		SpringApplication.run(MediCareXApplication.class, args);
	}

//    @Override
//    public void run(String... args) throws Exception {
//        RegisterRequest request = RegisterRequest.builder()
//                .email("shubhamkaushik007@gopalhospital.com")
//                .password("SecurePassword@123")
//                .firstName("Shubham")
//                .lastName("Kaushik")
//                .phone("+91123456789")
//                .role("ADMIN")
//                .build();
//
//        try {
//            authService.register(request);
//            System.out.println(" Admin record executed and saved successfully!");
//        } catch (Exception e) {
//            System.out.println(STR." Failed to execute record: \{e.getMessage()}");
//        }
//    }
}
