package com.example.daily_cashbook.category;

public class IncomeCategory {

    private String incomeName;
    private int incomeImageId;

    public IncomeCategory(String incomeName, int incomeImageId) {
        this.incomeName = incomeName;
        this.incomeImageId = incomeImageId;
    }

    public String getIncomeName() {
        return incomeName;
    }

    public int getIncomeImageId() {
        return incomeImageId;
    }
}
