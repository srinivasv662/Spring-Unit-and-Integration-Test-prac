package com.springprac.TestApp;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


//@SpringBootTest
@Slf4j
class TestAppApplicationTests {

    @BeforeEach
    void setUp() {
        log.info("Starting the method, setting up config");
    }

    @AfterEach
    void tearDown() {
        log.info("Tearing down the method");
    }

    @BeforeAll
    static void setUpOnce() {
        log.info("SetUp Once...");
    }

    @AfterAll
    static void tearDownOnce() {
        log.info("Tearing down all...");
    }


    @Test
//    @Disabled
	void testNumberOne() {
//        log.info("test one is run");
        int a = 5;
        int b = 3;

        int result = addTwoNumber(a, b);

//        Assertions.assertEquals(8, result);
//
//        Assertions.assertThat(result)
//                .isEqualTo(8)
//                .isCloseTo(9, Offset.offset(1));

//
//        Assertions.assertThat(result)
//                .isEqualTo(7)
//                .isCloseTo(9, Offset.offset(1));
//
        assertThat("Apple").
                isEqualTo("Apple")
                .startsWith("App")
                .endsWith("le")
                .hasSize(5);

//
//        assertThat("Apple").
//                isEqualTo("Appe")
//                .startsWith("pp")
//                .endsWith("l")
//                .hasSize(3);

        assertThat(List.of("apple", "banana")).contains("apple")
                .doesNotContain("orange").hasSize(2);

	}

    @Test
//    @DisplayName("displayTestNameTwo")
    void testNumberTwo() {
        log.info("test two is run");
    }

    @Test
    void testDivideTwoNumbers_whenDenominatorIsZero_ThenArithmeticException() {
        int a = 5;
        int b = 0;

//        assertThatThrownBy(() -> divideTwoNumbers(a, b))
//                .isInstanceOf(ArithmeticException.class);

//        assertThatThrownBy(() -> divideTwoNumbers(a, b))
//                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> divideTwoNumbers(a, b))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Tried to divide by zero");
//                .hasMessage("Tried to divide by");
    }

    int addTwoNumber(int a, int b) {
        return a + b;
    }

    double divideTwoNumbers(int a, int b) {
        try {
            return a / b;
        } catch (ArithmeticException e) {
            log.info("Arithmetic Exception occurred: " + e.getLocalizedMessage());
//            throw new ArithmeticException(e.getLocalizedMessage());
            throw new ArithmeticException("Tried to divide by zero");
        }
    }

}
