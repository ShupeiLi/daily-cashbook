package com.example.daily_cashbook.category;

public class ExpenditureCategory {

    private String expenditureName;
    private int expenditureImageId;

    public ExpenditureCategory(String expenditureName, int expenditureImageId) {
        this.expenditureName = expenditureName;
        this.expenditureImageId = expenditureImageId;
    }

    public String getExpenditureName() {
        return expenditureName;
    }

    public int getExpenditureImageId() {
        return expenditureImageId;
    }

}
