package com.darvin.cmritattendancemanager.utils;

public class Internals {
    private String mSubject = null;
    private String mFirstInternal = null;
    private String mSecondInternal = null;
    private String mThirdInternal = null;
    private String mTotalMarks = null;
    public Internals(String Subject, String FirstInternal, String SecondInternal, String ThirdInternal, String TotalMarks ){
        mSubject = Subject;
        mFirstInternal = FirstInternal;
        mSecondInternal = SecondInternal;
        mThirdInternal = ThirdInternal;
        mTotalMarks = TotalMarks;
    }

    public String getmSubject() {
        return mSubject;
    }

    public String getmFirstInternal() {
        return mFirstInternal;
    }

    public String getmSecondInternal() {
        return mSecondInternal;
    }

    public String getmThirdInternal() {
        return mThirdInternal;
    }

    public String getmTotalMarks() {
        return mTotalMarks;
    }
}
