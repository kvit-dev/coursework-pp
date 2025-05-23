package service;

import model.Deposit;
import model.DepositDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainServiceTest {

    private DepositDAO mockDAO;
    private MainService mainService;

    private static class TestableMainService extends MainService {
        private final DepositDAO dao;

        public TestableMainService(DepositDAO dao) throws SQLException {
            this.dao = dao;
        }

        @Override
        public List<Deposit> loadAllDeposits() throws Exception {
            return dao.getAllDeposits();
        }

        @Override
        public List<Deposit> searchDeposits(String bankName) throws Exception {
            if (bankName == null || bankName.isEmpty()) {
                return dao.getAllDeposits();
            }
            return dao.searchByBankName(bankName);
        }

        @Override
        public List<Deposit> sortDepositsByTerm(List<Deposit> deposits) {
            return super.sortDepositsByTerm(deposits);
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        mockDAO = mock(DepositDAO.class);
        mainService = new TestableMainService(mockDAO);
    }

    @Test
    public void testLoadAllDeposits() throws Exception {
        Deposit d1 = mock(Deposit.class);
        Deposit d2 = mock(Deposit.class);
        when(mockDAO.getAllDeposits()).thenReturn(Arrays.asList(d1, d2));

        List<Deposit> result = mainService.loadAllDeposits();

        assertEquals(2, result.size());
        verify(mockDAO, times(1)).getAllDeposits();
    }

    @Test
    public void testSearchDeposits_WithBankName() throws Exception {
        Deposit d1 = mock(Deposit.class);
        when(d1.getBankName()).thenReturn("Test Bank");
        when(mockDAO.searchByBankName("Test Bank")).thenReturn(List.of(d1));

        List<Deposit> result = mainService.searchDeposits("Test Bank");

        assertEquals(1, result.size());
        assertEquals("Test Bank", result.get(0).getBankName());
    }

    @Test
    public void testSearchDeposits_EmptyString() throws Exception {
        Deposit d1 = mock(Deposit.class);
        when(mockDAO.getAllDeposits()).thenReturn(List.of(d1));

        List<Deposit> result = mainService.searchDeposits("");

        assertEquals(1, result.size());
        verify(mockDAO).getAllDeposits();
    }

    @Test
    public void testSortDepositsByTerm() {
        Deposit d1 = mock(Deposit.class);
        Deposit d2 = mock(Deposit.class);
        Deposit d3 = mock(Deposit.class);

        when(d1.getTermMonths()).thenReturn(12);
        when(d2.getTermMonths()).thenReturn(6);
        when(d3.getTermMonths()).thenReturn(24);

        List<Deposit> sorted = mainService.sortDepositsByTerm(List.of(d1, d2, d3));

        assertEquals(6, sorted.get(0).getTermMonths());
        assertEquals(12, sorted.get(1).getTermMonths());
        assertEquals(24, sorted.get(2).getTermMonths());
    }
}
