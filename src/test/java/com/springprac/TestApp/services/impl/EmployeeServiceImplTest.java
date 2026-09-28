package com.springprac.TestApp.services.impl;

import com.springprac.TestApp.TestContainerConfiguration;
import com.springprac.TestApp.dto.EmployeeDto;
import com.springprac.TestApp.entities.Employee;
import com.springprac.TestApp.exceptions.ResourceNotFoundException;
import com.springprac.TestApp.repositories.EmployeeRepository;
import com.springprac.TestApp.services.EmployeeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(TestContainerConfiguration.class)
//@DataJpaTest
//@SpringBootTest  // At least this works, but it will start the entire application context.
// this would kinda act as a integration testing rather than a unit testing which should be done in
// isolation of that particular class and method.
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

//    @Autowired
//    private EmployeeService employeeService;

//    @InjectMocks
//    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

//    @Mock
    @Spy
    private ModelMapper modelMapper;

    private Employee mockEmployee;

    private EmployeeDto mockEmployeeDto;

    @BeforeAll
    static void setUpOnce() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @BeforeEach
    void setup() {
        mockEmployee = Employee.builder()
                .id(1L)
                .email("sri@gmail.com")
                .name("Sri")
                .salary(200L)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIdIsPresent_thenReturnEmployeeDto() {
//        employeeService.getEmployeeById(1L);

        // assign
        Long id = 1L;
//        Employee mockEmployee = Employee.builder()
//                .id(id)
//                .email("sri@gmail.com")
//                .name("Sri")
//                .salary(200L)
//                .build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee)); // stubbing

        // act
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);

        // assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository).findById(id);
//        verify(employeeRepository).save(null);
//        verify(employeeRepository).save(mockEmployee);
//        verify(employeeRepository).findById(2L);
//        verify(employeeRepository, times(2)).findById(id);
        verify(employeeRepository, atLeast(1)).findById(id);
//        verify(employeeRepository, atLeast(2)).findById(id);
//        verify(employeeRepository, atLeast(5)).findById(id);
        verify(employeeRepository, only()).findById(id);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIsNotPresent_thenThrowException() {
        // arrange
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // act and assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_whenValidEmployee_thenCreateNewEmployee() {
//        assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

//        Act
        EmployeeDto employeeDto = employeeService.createNewEmployee(mockEmployeeDto);

//        Asset
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
//        verify(employeeRepository).save(any(Employee.class));

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());
    }

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowExeption() {
        // arrange
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        // act and assert
        assertThatThrownBy(() -> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: " + mockEmployee.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExist_thenThrowException() {
        // arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // act and assert
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException() {
        // arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Radom");
        mockEmployeeDto.setEmail("random@gmail.com");

        // act and assert
        assertThatThrownBy(() -> employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee() {
        // arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Radom");
        mockEmployeeDto.setSalary(199L);

        Employee newEmployee = modelMapper.map(mockEmployeeDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        // act
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto);

        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());

    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExist_thenThrowException() {
        // arrange
        when(employeeRepository.existsById(1L)).thenReturn(false);

        // act and assert
        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + 1L);

        verify(employeeRepository, never()).deleteById(1L);
    }

    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee() {
        // arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        // act
        // employeeService.deleteEmployee(1L);

        // or act and assert
        assertThatCode(() -> employeeService.deleteEmployee(1L))
                .doesNotThrowAnyExceptionExcept();

        verify(employeeRepository).deleteById(1L);
    }

}