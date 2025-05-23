package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DepositTest {

    @Test
    public void testConstructorAndGetters() {
        Deposit deposit = new Deposit(1, "Test Bank", "Super Save", 5.5, 12, true, false);

        assertEquals(1, deposit.getId());
        assertEquals("Test Bank", deposit.getBankName());
        assertEquals("Super Save", deposit.getDepositName());
        assertEquals(5.5, deposit.getInterestRate());
        assertEquals(12, deposit.getTermMonths());
        assertTrue(deposit.isEarlyWithdrawal());
        assertFalse(deposit.isReplenishment());
    }

    @Test
    public void testSetters() {
        Deposit deposit = new Deposit();
        deposit.setId(2);
        deposit.setBankName("Bank XYZ");
        deposit.setDepositName("Growth Plan");
        deposit.setInterestRate(3.2);
        deposit.setTermMonths(24);
        deposit.setEarlyWithdrawal(false);
        deposit.setReplenishment(true);

        assertEquals(2, deposit.getId());
        assertEquals("Bank XYZ", deposit.getBankName());
        assertEquals("Growth Plan", deposit.getDepositName());
        assertEquals(3.2, deposit.getInterestRate());
        assertEquals(24, deposit.getTermMonths());
        assertFalse(deposit.isEarlyWithdrawal());
        assertTrue(deposit.isReplenishment());
    }
}
