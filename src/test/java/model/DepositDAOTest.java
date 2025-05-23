package model;

import org.junit.jupiter.api.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.List;

class DepositDAOTest {

    @Mock private Connection mockConnection;

    @Mock private PreparedStatement mockPreparedStatement;

    @Mock private Statement mockStatement;

    @Mock private ResultSet mockResultSet;

    private DepositDAO depositDAO;
    private Deposit testDeposit;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString()))
                    .thenReturn(mockConnection);

            depositDAO = new DepositDAO();
        }
        testDeposit = new Deposit(1, "ПриватБанк", "Депозит Плюс", 15.5, 12, true, false);
    }

    @Test
    void testAddDeposit_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> depositDAO.addDeposit(testDeposit));

        verify(mockConnection).prepareStatement(
                "INSERT INTO Deposit (BankName, DepositName, InterestRate, TermMonths, EarlyWithdrawal, Replenishment) VALUES (?, ?, ?, ?, ?, ?)"
        );
        verify(mockPreparedStatement).setString(1, "ПриватБанк");
        verify(mockPreparedStatement).setString(2, "Депозит Плюс");
        verify(mockPreparedStatement).setDouble(3, 15.5);
        verify(mockPreparedStatement).setInt(4, 12);
        verify(mockPreparedStatement).setBoolean(5, true);
        verify(mockPreparedStatement).setBoolean(6, false);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).close();
    }

    @Test
    void testAddDeposit_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        SQLException exception = assertThrows(SQLException.class, () -> {
            depositDAO.addDeposit(testDeposit);
        });
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testUpdateDeposit_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> depositDAO.updateDeposit(testDeposit));

        verify(mockConnection).prepareStatement(
                "UPDATE Deposit SET BankName=?, DepositName=?, InterestRate=?, TermMonths=?, EarlyWithdrawal=?, Replenishment=? WHERE ID=?"
        );
        verify(mockPreparedStatement).setString(1, "ПриватБанк");
        verify(mockPreparedStatement).setString(2, "Депозит Плюс");
        verify(mockPreparedStatement).setDouble(3, 15.5);
        verify(mockPreparedStatement).setInt(4, 12);
        verify(mockPreparedStatement).setBoolean(5, true);
        verify(mockPreparedStatement).setBoolean(6, false);
        verify(mockPreparedStatement).setInt(7, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateDeposit_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Update failed"));

        SQLException exception = assertThrows(SQLException.class, () -> {
            depositDAO.updateDeposit(testDeposit);
        });

        assertEquals("Update failed", exception.getMessage());
    }

    @Test
    void testDeleteDeposit_Success() throws SQLException {
        int depositId = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> depositDAO.deleteDeposit(depositId));

        verify(mockConnection).prepareStatement("DELETE FROM Deposit WHERE ID=?");
        verify(mockPreparedStatement).setInt(1, depositId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteDeposit_SQLException() throws SQLException {
        int depositId = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Delete failed"));

        SQLException exception = assertThrows(SQLException.class, () -> {
            depositDAO.deleteDeposit(depositId);
        });

        assertEquals("Delete failed", exception.getMessage());
    }

    @Test
    void testGetAllDeposits_Success() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("ID")).thenReturn(1, 2);
        when(mockResultSet.getString("BankName")).thenReturn("ПриватБанк", "Ощадбанк");
        when(mockResultSet.getString("DepositName")).thenReturn("Депозит Плюс", "Стандартний");
        when(mockResultSet.getDouble("InterestRate")).thenReturn(15.5, 12.0);
        when(mockResultSet.getInt("TermMonths")).thenReturn(12, 24);
        when(mockResultSet.getBoolean("EarlyWithdrawal")).thenReturn(true, false);
        when(mockResultSet.getBoolean("Replenishment")).thenReturn(false, true);

        List<Deposit> deposits = depositDAO.getAllDeposits();

        assertNotNull(deposits);
        assertEquals(2, deposits.size());

        Deposit first = deposits.getFirst();
        assertEquals(1, first.getId());
        assertEquals("ПриватБанк", first.getBankName());
        assertEquals("Депозит Плюс", first.getDepositName());
        assertEquals(15.5, first.getInterestRate());
        assertEquals(12, first.getTermMonths());
        assertTrue(first.isEarlyWithdrawal());
        assertFalse(first.isReplenishment());

        verify(mockConnection).createStatement();
        verify(mockStatement).executeQuery("SELECT * FROM Deposit");
    }

    @Test
    void testGetAllDeposits_EmptyResult() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Deposit> deposits = depositDAO.getAllDeposits();

        assertNotNull(deposits);
        assertTrue(deposits.isEmpty());
    }

    @Test
    void testGetAllDeposits_SQLException() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Query failed"));

        SQLException exception = assertThrows(SQLException.class, () -> {
            depositDAO.getAllDeposits();
        });

        assertEquals("Query failed", exception.getMessage());
    }

    @Test
    void testGetDepositById_Found() throws SQLException {
        int depositId = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        setupMockResultSetForTestDeposit();

        Deposit result = depositDAO.getDepositById(depositId);

        assertNotNull(result);
        assertEquals(testDeposit.getId(), result.getId());
        assertEquals(testDeposit.getBankName(), result.getBankName());
        assertEquals(testDeposit.getDepositName(), result.getDepositName());

        verify(mockConnection).prepareStatement("SELECT * FROM Deposit WHERE ID = ?");
        verify(mockPreparedStatement).setInt(1, depositId);
    }

    @Test
    void testGetDepositById_NotFound() throws SQLException {
        int depositId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Deposit result = depositDAO.getDepositById(depositId);

        assertNull(result);
        verify(mockPreparedStatement).setInt(1, depositId);
    }

    @Test
    void testGetDepositById_SQLException() throws SQLException {
        int depositId = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query by ID failed"));

        SQLException exception = assertThrows(SQLException.class, () -> {
            depositDAO.getDepositById(depositId);
        });

        assertEquals("Query by ID failed", exception.getMessage());
    }

    @Test
    void testSearchByBankName_Found() throws SQLException {
        String bankName = "Приват";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        setupMockResultSetForTestDeposit();

        List<Deposit> results = depositDAO.searchByBankName(bankName);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDeposit.getBankName(), results.getFirst().getBankName());

        verify(mockConnection).prepareStatement("SELECT * FROM Deposit WHERE BankName LIKE ?");
        verify(mockPreparedStatement).setString(1, "%" + bankName + "%");
    }

    @Test
    void testSearchByBankName_NotFound() throws SQLException {
        String bankName = "НеіснуючийБанк";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Deposit> results = depositDAO.searchByBankName(bankName);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchByBankName_SQLException() throws SQLException {
        String bankName = "Приват";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Search failed"));

        SQLException exception = assertThrows(SQLException.class, () -> {
            depositDAO.searchByBankName(bankName);
        });

        assertEquals("Search failed", exception.getMessage());
    }

    @Test
    void testConstructor_ConnectionFailure() {
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString()))
                    .thenAnswer(invocation -> {
                        throw new SQLException("Connection failed");
                    });

            SQLException exception = assertThrows(SQLException.class, DepositDAO::new);
            assertEquals("Connection failed", exception.getMessage());
        }
    }

    @Test
    void testFillDepositStatement_WithId() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        depositDAO.updateDeposit(testDeposit);

        InOrder inOrder = inOrder(mockPreparedStatement);
        inOrder.verify(mockPreparedStatement).setString(1, "ПриватБанк");
        inOrder.verify(mockPreparedStatement).setString(2, "Депозит Плюс");
        inOrder.verify(mockPreparedStatement).setDouble(3, 15.5);
        inOrder.verify(mockPreparedStatement).setInt(4, 12);
        inOrder.verify(mockPreparedStatement).setBoolean(5, true);
        inOrder.verify(mockPreparedStatement).setBoolean(6, false);
        inOrder.verify(mockPreparedStatement).setInt(7, 1);
    }

    @Test
    void testFillDepositStatement_WithoutId() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> depositDAO.addDeposit(testDeposit));

        verify(mockPreparedStatement).setString(1, testDeposit.getBankName());
        verify(mockPreparedStatement).setString(2, testDeposit.getDepositName());
        verify(mockPreparedStatement).setDouble(3, testDeposit.getInterestRate());
        verify(mockPreparedStatement).setInt(4, testDeposit.getTermMonths());
        verify(mockPreparedStatement).setBoolean(5, testDeposit.isEarlyWithdrawal());
        verify(mockPreparedStatement).setBoolean(6, testDeposit.isReplenishment());
    }

    private void setupMockResultSetForTestDeposit() throws SQLException {
        when(mockResultSet.getInt("ID")).thenReturn(testDeposit.getId());
        when(mockResultSet.getString("BankName")).thenReturn(testDeposit.getBankName());
        when(mockResultSet.getString("DepositName")).thenReturn(testDeposit.getDepositName());
        when(mockResultSet.getDouble("InterestRate")).thenReturn(testDeposit.getInterestRate());
        when(mockResultSet.getInt("TermMonths")).thenReturn(testDeposit.getTermMonths());
        when(mockResultSet.getBoolean("EarlyWithdrawal")).thenReturn(testDeposit.isEarlyWithdrawal());
        when(mockResultSet.getBoolean("Replenishment")).thenReturn(testDeposit.isReplenishment());
    }

    @AfterEach
    void tearDown() {
        reset(mockConnection, mockPreparedStatement, mockStatement, mockResultSet);
    }
}

