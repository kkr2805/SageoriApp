package com.hgtech.sageoriapp;

import java.io.Serializable;
import java.util.Date;

public class SearchParams implements Serializable {

    public boolean checkMachineID;
    public boolean checkMemberID;
    public boolean checkDate;
    public boolean checkDateStart;

    public int machineID;
    public int memberID;
    public Date createdDate;
    public Date createdDateStart;
    public Date createdDateEnd;

    SearchParams(){
        checkMachineID = false;
        checkMemberID = false;
        checkDate = false;
        checkDateStart = false;

        machineID = -1;
        int memberID = -1;
        createdDate = null;
        createdDateStart = null;
        createdDateEnd = null;
    }

}
