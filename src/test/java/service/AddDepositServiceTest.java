package service;

import model.Deposit;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddDepositServiceTest {

    private AddDepositService service;

    @BeforeEach
    void setUp() {
        service = Mockito.spy(new AddDepositService());
        Mockito.doNothing().when(service).showAlert(Mockito.any(), Mockito.anyString());
    }

    @Test
    void testAddDeposit_successful() throws SQLException {
        try (MockedConstruction<DepositDAO> mocked = Mockito.mockConstruction(DepositDAO.class,
                (mock, context) -> Mockito.doNothing().when(mock).addDeposit(Mockito.any(Deposit.class)))) {

            boolean result = service.addDeposit("Bank", "Savings", "5.5", "12", true, false);
            assertTrue(result);
        }
    }

    @Test
    void testAddDeposit_emptyFields() throws SQLException {
        boolean result = service.addDeposit("", "Savings", "5.5", "12", true, false);
        assertFalse(result);
    }

    @Test
    void testAddDeposit_invalidNumberFormat() throws SQLException {
        boolean result = service.addDeposit("Bank", "Savings", "abc", "12", true, false);
        assertFalse(result);
    }

    @Test
    void testAddDeposit_negativeInterestRate() throws SQLException {
        boolean result = service.addDeposit("Bank", "Savings", "-1", "12", true, false);
        assertFalse(result);
    }

    @Test
    void testAddDeposit_zeroTerm() throws SQLException {
        boolean result = service.addDeposit("Bank", "Savings", "5", "0", true, false);
        assertFalse(result);
    }

    @Test
    void testAddDeposit_DAOThrowsException() throws SQLException {
        try (MockedConstruction<DepositDAO> mocked = Mockito.mockConstruction(DepositDAO.class,
                (mock, context) -> Mockito.doThrow(new SQLException("DB error")).when(mock).addDeposit(Mockito.any()))) {

            boolean result = service.addDeposit("Bank", "Deposit", "5.0", "12", false, true);
            assertFalse(result);
        }
    }
}
