package service;

import model.Deposit;
import model.DepositDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class SearchDepositsService {
    private static final Logger logger = LoggerFactory.getLogger(SearchDepositsService.class);
    private final DepositDAO dao;

    public SearchDepositsService() throws SQLException {
        this.dao = new DepositDAO();
    }

    public List<Deposit> searchByBankName(String bankName) throws Exception {
        logger.info("Пошук депозитів у сервісі для банку: {}", bankName);
        return dao.searchByBankName(bankName);
    }
}
