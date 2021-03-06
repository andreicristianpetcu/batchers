package be.cegeka.batchers.taxcalculator.application.domain.generation;

import be.cegeka.batchers.taxcalculator.application.service.EmployeeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmployeeGenerator {
    @Value("${number.of.employees:13}")
    private Long numberOfEmployees;

    @Autowired
    private EmployeeGeneratorService employeeGeneratorService;

    public void resetEmployees() {
        employeeGeneratorService.resetEmployees(numberOfEmployees);
    }

    public Long getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Long numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }
}
