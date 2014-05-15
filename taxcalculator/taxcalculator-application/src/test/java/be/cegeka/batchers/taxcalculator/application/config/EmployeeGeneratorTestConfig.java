package be.cegeka.batchers.taxcalculator.application.config;

import be.cegeka.batchers.taxcalculator.application.ApplicationInitializer;
import org.springframework.context.annotation.Bean;

public class EmployeeGeneratorTestConfig extends EmployeeGeneratorConfig {

    @Bean
    @Override
    public ApplicationInitializer applicationInitializer() {
        ApplicationInitializer applicationInitializer = super.applicationInitializer();
        applicationInitializer.setGenerateEmployees(false);

        return applicationInitializer;
    }
}