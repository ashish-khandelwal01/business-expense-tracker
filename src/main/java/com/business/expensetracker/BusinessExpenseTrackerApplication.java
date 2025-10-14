package com.business.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BusinessExpenseTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessExpenseTrackerApplication.class, args);
    }
}
