package com.mubarak.ads.user.ui.hospitals;

public class hospitalList {

    public String hospitalname;
    public String hospitalmail;
    public String hospitaltype;
    public String hospitaladdress;
    public String hospitalspecility;
    public String hospitalphone;





    public hospitalList(String hospitalname, String hospitalmail, String hospitaltype, String hospitaladdress, String hospitalspecility, String hospitalphone) {
        this.hospitalname = hospitalname;
        this.hospitalmail = hospitalmail;
        this.hospitaltype = hospitaltype;
        this.hospitaladdress = hospitaladdress;
        this.hospitalspecility = hospitalspecility;
        this.hospitalphone = hospitalphone;


    }


    public String getHospitalname() {
        return hospitalname;

    }
    public String getHospitalmail() {
        return hospitalmail;

    }
    public String getHospitaltype() {
        return hospitaltype;

    }




    public String getHospitaladdress() {
        return hospitaladdress;
    }

    public String getHospitalspecility(){
        return hospitalspecility;
    }
    public String getHospitalphone(){
        return hospitalphone;
    }

}


