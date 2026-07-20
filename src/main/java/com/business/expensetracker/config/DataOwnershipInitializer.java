package com.business.expensetracker.config;

import com.business.expensetracker.entity.*;
import com.business.expensetracker.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

/** Assigns records created before user isolation to the bootstrap administrator once. */
@Component
@Order(2)
public class DataOwnershipInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ExpenseRepository expenseRepository;
    private final SaleRepository saleRepository;
    private final ReturnRepository returnRepository;
    private final SaleMonthlyRepository saleMonthlyRepository;
    private final ReturnMonthlyRepository returnMonthlyRepository;

    @Value("${admin.username:admin}")
    private String adminUsername;

    public DataOwnershipInitializer(UserRepository userRepository, ProductRepository productRepository,
            ExpenseRepository expenseRepository, SaleRepository saleRepository, ReturnRepository returnRepository,
            SaleMonthlyRepository saleMonthlyRepository, ReturnMonthlyRepository returnMonthlyRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.expenseRepository = expenseRepository;
        this.saleRepository = saleRepository;
        this.returnRepository = returnRepository;
        this.saleMonthlyRepository = saleMonthlyRepository;
        this.returnMonthlyRepository = returnMonthlyRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User admin = userRepository.findByUsername(adminUsername).orElse(null);
        if (admin == null) return;
        productRepository.assignUnownedRecords(admin);
        expenseRepository.assignUnownedRecords(admin);
        saleRepository.assignUnownedRecords(admin);
        returnRepository.assignUnownedRecords(admin);
        saleMonthlyRepository.assignUnownedRecords(admin);
        returnMonthlyRepository.assignUnownedRecords(admin);
    }
}
