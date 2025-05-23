package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositDAO {
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(DepositDAO.class);

    public DepositDAO() throws SQLException {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=DepositControl;user=LoginDeposit;password=deposit_pass;encrypt=true;trustServerCertificate=true;"
            );
        } catch (SQLException e) {
            logger.error("Не вдалось підключитись до бази даних", e);
            throw e;
        }
    }

    public void addDeposit(Deposit deposit) throws SQLException {
        String sql = "INSERT INTO Deposit (BankName, DepositName, InterestRate, TermMonths, EarlyWithdrawal, Replenishment) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            fillDepositStatement(stmt, deposit, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Помилка при додаванні депозиту", e);
            throw e;
        }
    }

    public void updateDeposit(Deposit deposit) throws SQLException {
        String sql = "UPDATE Deposit SET BankName=?, DepositName=?, InterestRate=?, TermMonths=?, EarlyWithdrawal=?, Replenishment=? WHERE ID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            fillDepositStatement(stmt, deposit, true);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Помилка при оновленні депозиту", e);
            throw e;
        }
    }

    public void deleteDeposit(int id) throws SQLException {
        String sql = "DELETE FROM Deposit WHERE ID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Помилка при видаленні депозиту id = {}", id, e);
            throw e;
        }
    }

    public List<Deposit> getAllDeposits() throws SQLException {
        List<Deposit> list = new ArrayList<>();
        String sql = "SELECT * FROM Deposit";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractDeposit(rs));
            }
        } catch (SQLException e) {
            logger.error("Помилка при отриманні всіх депозитів", e);
            throw e;
        }
        return list;
    }

    public Deposit getDepositById(int id) throws SQLException {
        String sql = "SELECT * FROM Deposit WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractDeposit(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка при отриманні депозиту за id = {}", id, e);
            throw e;
        }
    }

    public List<Deposit> searchByBankName(String bankName) throws SQLException {
        List<Deposit> list = new ArrayList<>();
        String sql = "SELECT * FROM Deposit WHERE BankName LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + bankName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractDeposit(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка при пошуку депозитів за банком '{}'", bankName, e);
            throw e;
        }
        return list;
    }

    private Deposit extractDeposit(ResultSet rs) throws SQLException {
        return new Deposit(
                rs.getInt("ID"),
                rs.getString("BankName"),
                rs.getString("DepositName"),
                rs.getDouble("InterestRate"),
                rs.getInt("TermMonths"),
                rs.getBoolean("EarlyWithdrawal"),
                rs.getBoolean("Replenishment")
        );
    }

    private void fillDepositStatement(PreparedStatement stmt, Deposit deposit, boolean includeId) throws SQLException {
        stmt.setString(1, deposit.getBankName());
        stmt.setString(2, deposit.getDepositName());
        stmt.setDouble(3, deposit.getInterestRate());
        stmt.setInt(4, deposit.getTermMonths());
        stmt.setBoolean(5, deposit.isEarlyWithdrawal());
        stmt.setBoolean(6, deposit.isReplenishment());
        if (includeId) {
            stmt.setInt(7, deposit.getId());
        }
    }
}
