package be.cegeka.batchers.taxcalculator.batch.repositories;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.repositories.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationTestBuilder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;

public class TaxCalculationRepositoryTest extends AbstractBatchRepositoryIntegrationTest {

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayCheckRepository payCheckRepository;

    private Employee gigel;
    private Employee ionel;
    private TaxCalculation gigelJanuary;
    private TaxCalculation gigelFebruary;
    private TaxCalculation ionelJanuary;
    private TaxCalculation ionelFebruary;

    @Test
    public void testFind() {
        setupGigelAndIonel();

        //ACT
        List<TaxCalculation> byYearAndMonth = taxCalculationRepository.find(2014, 1, 1L);

        //ASSERT
        assertThat(byYearAndMonth).containsOnly(gigelJanuary, ionelJanuary);
    }

    @Test
    public void testFindByEmployee() {
        setupGigelAndIonel();

        //ACT
        List<TaxCalculation> byEmployee = taxCalculationRepository.findByEmployee(ionel);

        //ASSERT
        assertThat(byEmployee).containsOnly(ionelJanuary, ionelFebruary);
    }

    @Test(expected = PersistenceException.class)
    public void cannotHaveDuplicateCalculation() {
        setupGigelAndIonel();

        TaxCalculation gigelJanuary2 = new TaxCalculationTestBuilder().withEmployee(gigel).withMonth(1).withTax(15.0).build();

        taxCalculationRepository.save(gigelJanuary2);
    }

    @Test
    public void givenNoEmployees_whenGettingSum_theSumIsZero() {
        assertThat(taxCalculationRepository.getSuccessSum(2014, 5)).isEqualTo(Money.zero(CurrencyUnit.EUR));
        assertThat(taxCalculationRepository.getFailedSum(2014, 5)).isEqualTo(Money.zero(CurrencyUnit.EUR));
    }

    @Test
    public void givenOneEmployeeWithTaxAndPaycheck_whenGettingSum_theSumIsCorrect() {
        //ARRANGE
        Employee employee = anEmployee();
        employeeRepository.save(employee);

        TaxCalculation tax = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .build();
        taxCalculationRepository.save(tax);

        PayCheck payCheck = PayCheck.from(1l, tax, null);
        payCheckRepository.save(payCheck);

        //ACT
        Money successSum = taxCalculationRepository.getSuccessSum(tax.getYear(), tax.getMonth());
        Money failedSum = taxCalculationRepository.getFailedSum(tax.getYear(), tax.getMonth());

        //ASSERT
        assertThat(successSum).isEqualTo(tax.getTax());
        assertThat(failedSum).isEqualTo(Money.zero(CurrencyUnit.EUR));
    }

    @Test
    public void givenOneEmployeeWithTaxAndNoPaycheck_whenGettingSum_theSumIsCorrect() {
        //ARRANGE
        Employee employee = anEmployee();
        employeeRepository.save(employee);

        TaxCalculation tax = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .build();
        taxCalculationRepository.save(tax);

        //ACT
        Money successSum = taxCalculationRepository.getSuccessSum(tax.getYear(), tax.getMonth());
        Money failedSum = taxCalculationRepository.getFailedSum(tax.getYear(), tax.getMonth());

        //ASSERT
        assertThat(successSum).isEqualTo(Money.zero(CurrencyUnit.EUR));
        assertThat(failedSum).isEqualTo(tax.getTax());
    }

    private void setupGigelAndIonel() {
        gigel = anEmployee();
        ionel = anEmployee();

        employeeRepository.save(gigel);
        employeeRepository.save(ionel);

        gigelJanuary = new TaxCalculationTestBuilder().withEmployee(gigel).withMonth(1).withTax(10.0).build();
        gigelFebruary = new TaxCalculationTestBuilder().withEmployee(gigel).withMonth(2).withTax(10.0).build();

        ionelJanuary = new TaxCalculationTestBuilder().withEmployee(ionel).withMonth(1).withTax(12.0).build();
        ionelFebruary = new TaxCalculationTestBuilder().withEmployee(ionel).withMonth(2).withTax(13.0).build();

        List<TaxCalculation> taxes = Arrays.asList(gigelJanuary, gigelFebruary, ionelJanuary, ionelFebruary);
        taxes.forEach(taxCalculationRepository::save);
    }
}
