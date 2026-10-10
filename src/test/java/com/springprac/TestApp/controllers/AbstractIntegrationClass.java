package com.springprac.TestApp.controllers;

import com.springprac.TestApp.TestContainerConfiguration;
import com.springprac.TestApp.dto.EmployeeDto;
import com.springprac.TestApp.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient(timeout="100000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
public class AbstractIntegrationClass {

    @Autowired
    WebTestClient webTestClient;

    Employee testEmployee = Employee.builder()
//                .id(1L)
            .email("sri@gmail.com")
                .name("Sri")
                .salary(200L)
                .build();

    EmployeeDto testEmployeeDto = EmployeeDto.builder()
//                .id(1L)
            .email("sri@gmail.com")
                .name("Sri")
                .salary(200L)
                .build();
}
