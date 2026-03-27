package com.petg.blois;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@Slf4j
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RequiredArgsConstructor
public class Main implements CommandLineRunner {
    public static void main(String... args) {
        new SpringApplicationBuilder(Main.class)
                .run(args);
    }

    /**
     * args[0] task params
     *
     * @param args Paramètre du traitement
     */
    @Override
    public void run(String... args) {

    }
}
