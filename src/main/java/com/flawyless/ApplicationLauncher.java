package com.flawyless;

import com.flawyless.model.Card;
import com.flawyless.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootApplication
public class ApplicationLauncher {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationLauncher.class, args);
    }

    @Bean
    public CommandLineRunner addTestData(@Autowired CardService cardService) {
        return args -> {
            cardService.saveCard(new Card("test_summary_0", "test_desc_0"));
            cardService.saveCard(new Card("test_summary_1", "test_desc_1"));
        };
    }
}
