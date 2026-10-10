package com.springprac.TestApp.controllers;

import com.springprac.TestApp.TestContainerConfiguration;
import com.springprac.TestApp.dto.EmployeeDto;
import com.springprac.TestApp.entities.Employee;
import com.springprac.TestApp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

//@AutoConfigureWebTestClient(timeout="100000")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(TestContainerConfiguration.class)
//@AutoConfigureTestDatabase(
//        replace = AutoConfigureTestDatabase.Replace.NONE
//)
class EmployeeControllerTestIT extends AbstractIntegrationClass{

//    @BeforeAll
//    static void setUpOnce() {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
//    }

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

//    @Autowired
//    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

//    private Employee testEmployee;
//
//    private EmployeeDto testEmployeeDto;

    @BeforeEach
    void setUp() {
//        testEmployee = Employee.builder()
////                .id(1L)
//                .email("sri@gmail.com")
//                .name("Sri")
//                .salary(200L)
//                .build();
//
//        testEmployeeDto = EmployeeDto.builder()
////                .id(1L)
//                .email("sri@gmail.com")
//                .name("Sri")
//                .salary(200L)
//                .build();

        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_success() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        EmployeeDto expectedDto = EmployeeDto.builder()
                .id(savedEmployee.getId())
                .email(savedEmployee.getEmail())
                .name(savedEmployee.getName())
                .salary(savedEmployee.getSalary())
                .build();

//        webTestClient.get()
//                .uri("/employees/{id}", savedEmployee.getId())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(EmployeeDto.class)
////                .isEqualTo(testEmployeeDto);
//                .isEqualTo(expectedDto);
////                .value(employeeDto -> {
////                    assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
////                    assertThat(employeeDto.getId()).isEqualTo(savedEmployee.getId());
////                });

        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
//                .isEqualTo(testEmployeeDto);
//                .isEqualTo(expectedDto);
                .jsonPath("$.id").isEqualTo(savedEmployee.getId())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());
//                .value(employeeDto -> {
//                    assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
//                    assertThat(employeeDto.getId()).isEqualTo(savedEmployee.getId());
//                });

    }

    @Test
    void testGetEmployeeById_failure() {

        webTestClient.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void testCreateEmployee_whenEmployeeAlreadyExists_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        EmployeeDto expectedDto = EmployeeDto.builder()
                .id(savedEmployee.getId())
                .email(savedEmployee.getEmail())
                .name(savedEmployee.getName())
                .salary(savedEmployee.getSalary())
                .build();

        webTestClient.post()
                .uri("/employees")
//                .bodyValue(testEmployeeDto)
                .bodyValue(expectedDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee() {
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoestNotExists_thenThrowException() {
        webTestClient.put()
                .uri("/employees/999")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        EmployeeDto expectedDto = EmployeeDto.builder()
                .id(savedEmployee.getId())
                .email(savedEmployee.getEmail())
                .name(savedEmployee.getName())
                .salary(savedEmployee.getSalary())
                .build();
        expectedDto.setName("Random Name");
        expectedDto.setEmail("random@gmail.com");

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(expectedDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        EmployeeDto expectedDto = EmployeeDto.builder()
                .id(savedEmployee.getId())
                .email(savedEmployee.getEmail())
                .name(savedEmployee.getName())
                .salary(savedEmployee.getSalary())
                .build();
        expectedDto.setName("Random Name");
        expectedDto.setSalary(250L);

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(expectedDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(expectedDto);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {

        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

}