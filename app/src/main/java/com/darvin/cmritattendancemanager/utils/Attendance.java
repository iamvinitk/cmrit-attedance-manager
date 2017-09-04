package com.darvin.cmritattendancemanager.utils;

/**
 * Created by Vinit Kanani on 17-Aug-17.
 */

public class Attendance {
    private String mSubject = null;
    private String mAttended = null;
    private String mTotal = null;

    public Attendance(String Subject, String Attended, String Total){
        mSubject = Subject;
        mAttended = Attended;
        mTotal = Total;
    }

    public String getmSubject() {
        return mSubject;
    }

    public String getmAttended() {
        return mAttended;
    }

    public String getmTotal() {
        return mTotal;
    }
}
