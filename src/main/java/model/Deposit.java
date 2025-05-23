package model;

public class Deposit {
    private int id;
    private String bankName;
    private String depositName;
    private double interestRate;
    private int termMonths;
    private boolean earlyWithdrawal;
    private boolean replenishment;

    public Deposit(int id, String bankName, String depositName, double interestRate, int termMonths, boolean earlyWithdrawal, boolean replenishment) {
        this.id = id;
        this.bankName = bankName;
        this.depositName = depositName;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.earlyWithdrawal = earlyWithdrawal;
        this.replenishment = replenishment;
    }

    public Deposit() {
    }

    public Deposit(String s, String s1, double v, int i, boolean b) {
    }

    public Deposit(String testBank, String testDeposit, double v, int i, boolean b, boolean b1) {
    }

    public int getId() { return id; }
    public String getBankName() { return bankName; }
    public String getDepositName() { return depositName; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public boolean isEarlyWithdrawal() { return earlyWithdrawal; }
    public boolean isReplenishment() { return replenishment; }

    public void setId(int id) { this.id = id; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setDepositName(String depositName) { this.depositName = depositName; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }
    public void setEarlyWithdrawal(boolean earlyWithdrawal) { this.earlyWithdrawal = earlyWithdrawal; }
    public void setReplenishment(boolean replenishment) { this.replenishment = replenishment; }
}
