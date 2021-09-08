package com.mubarak.ads.agent;

public class EmergencyList {
    private String id;
    private String tag;
    private String name;
    private String location;
    private String phone;
    private String type;
    private String status;
    private String number;
    private String time;


    public EmergencyList(String id, String tag, String name, String location, String phone, String type, String status, String number, String time){
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.type = type;
        this.status = status;
        this.number = number;
        this.time = time;


    }

    public String getId(){
        return id;
    }


    public String getTag(){
        return  tag;
    }

    public String getName(){
        return  name;
    }


    public String getLocation(){
        return  location;
    }

    public String getPhone(){
        return  phone;
    }

    public String getType(){
        return type;
    }

    public String getStatus(){
        return  status;
    }

    public String getNumber(){
        return  number;
    }
    public String getTime(){
        return time;
    }
}
