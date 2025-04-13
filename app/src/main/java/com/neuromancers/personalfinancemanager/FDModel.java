package com.neuromancers.personalfinancemanager;

public class FDModel {
    private long id;
    private double principal;
    private double rate;
    private int timeMonths;
    private String startDate;
    private String maturityDate;
    private double interest;
    private double maturityAmount;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public int getTimeMonths() { return timeMonths; }
    public void setTimeMonths(int timeMonths) { this.timeMonths = timeMonths; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getMaturityDate() { return maturityDate; }
    public void setMaturityDate(String maturityDate) { this.maturityDate = maturityDate; }

    public double getInterest() { return interest; }
    public void setInterest(double interest) { this.interest = interest; }

    public double getMaturityAmount() { return maturityAmount; }
    public void setMaturityAmount(double maturityAmount) { this.maturityAmount = maturityAmount; }
}
