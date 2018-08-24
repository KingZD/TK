package com.example.tk.ui.widget;

public class RuleBean {
    private String mRuleDate;
    private long mRuleTime;

    RuleBean() {
    }

    public RuleBean(String mRuleDate, long mRuleTime) {
        this.mRuleDate = mRuleDate;
        this.mRuleTime = mRuleTime;
    }

    public String getDate() {
        return mRuleDate;
    }

    public long getTime() {
        return mRuleTime;
    }
}
