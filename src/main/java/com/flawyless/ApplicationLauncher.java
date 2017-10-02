package com.flawyless;

import com.flawyless.model.Card;
import com.flawyless.repository.CardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApplicationLauncher {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationLauncher.class, args);
    }

    @Bean
    public CommandLineRunner addSampleData(CardRepository cardRepository) {
        return args -> {
            // add some sample data
            cardRepository.save(new Card("summary_0", "description_0"));
            cardRepository.save(new Card("summary_1"));
            cardRepository.save(new Card("summary_2", "description_2"));

            cardRepository.findAll().forEach(System.out::println);
        };
    }
}
