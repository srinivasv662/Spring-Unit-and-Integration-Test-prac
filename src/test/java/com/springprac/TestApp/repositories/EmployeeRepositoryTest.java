package com.springprac.TestApp.repositories;

import com.springprac.TestApp.TestContainerConfiguration;
import com.springprac.TestApp.entities.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestContainerConfiguration.class)
//@SpringBootTest
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
// Also you can remove the h2 database dependency --> as running both H2 and PostgreSQL via container is not needed -> So H2 is not needed.
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeAll
    static void setUpOnce() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
//                .id(1L)
//                org.springframework.orm.ObjectOptimisticLockingFailureException: Row was already updated or deleted by another transaction for entity [com.springprac.TestApp.entities.Employee with id '1']
//                Since 'id' generation is taken care by database/hibernate
                .name("Sri")
                .email("sri@gmail.com")
                .salary(100L)
                .build();
    }

    @Test
    void testFinaByEmail_whenEmailIsValid_thenReturnEmployee() {
//        employeeRepository.findByEmail("");

        // Arrange, Given
        employeeRepository.save(employee);

        // Act, When
        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());

        // Assert, Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFinaByEmail_whenEmailIsNotFound_thenReturnEmptyEmployee() {
        // Given
        String email = "notPresent.123@gmail.com";

        // When
        List<Employee> employeeList = employeeRepository.findByEmail(email);

        // Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();
//        assertThat(employeeList).isNotEmpty(); // --> Faulty test case
    }
}