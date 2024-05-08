package com.reboot.playmoney;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
public class PlayMoneyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayMoneyApplication.class, args);
    }

}
