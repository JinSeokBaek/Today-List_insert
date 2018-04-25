package com.group13.www.today_list_insert;

/**
 * Created by admin on 2017-06-17.
 */

public class Bus {
    private String busID;
    private String busName;
   //private String busMobile;
   public Bus(String busID,String busName){
       this.busID=busID;
       this.busName=busName;
   }
   public String getBusID(){
        return busID;
    }
    public String getBusName(){
        return busName;
    }
}
