package service;

import model.Deposit;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchDepositsServiceTest {

    private Deposit sampleDeposit1;
    private Deposit sampleDeposit2;

    @BeforeEach
    public void setUp() {
        sampleDeposit1 = mock(Deposit.class);
        sampleDeposit2 = mock(Deposit.class);
    }

    @Test
    public void testSearchByBankName_ReturnsExpectedResults() throws Exception {
        List<Deposit> expectedDeposits = Arrays.asList(sampleDeposit1, sampleDeposit2);

        try (MockedConstruction<DepositDAO> daoMock = mockConstruction(DepositDAO.class,
                (mock, context) -> when(mock.searchByBankName("TestBank")).thenReturn(expectedDeposits))) {

            SearchDepositsService service = new SearchDepositsService();

            List<Deposit> result = service.searchByBankName("TestBank");

            assertEquals(expectedDeposits, result);
            assertEquals(2, result.size());
        }
    }

    @Test
    public void testSearchByBankName_DAOThrowsException() throws SQLException {
        try (MockedConstruction<DepositDAO> daoMock = mockConstruction(DepositDAO.class,
                (mock, context) -> when(mock.searchByBankName("BadBank")).thenThrow(new SQLException("DB error")))) {

            SearchDepositsService service = new SearchDepositsService();

            Exception exception = assertThrows(Exception.class, () -> service.searchByBankName("BadBank"));
            assertTrue(exception.getMessage().contains("DB error"));
        }
    }
}
