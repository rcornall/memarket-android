package com.robthecornallgmail.memarket.Util;

public class UserObject {
    private Integer ID;
    private String username;
    private String email;
    private Integer money;
    public void setID(Integer _id) {this.ID = _id;}
    public void setUsername(String _username) {this.username = _username;}
    public void setEmail(String _email) {this.email = _email;}
    public void setMoney(Integer _money) {this.money = _money;}
    public void subtractMoney(Integer _money) {this.money = this.money - _money;}
    public void addMoney(Integer _money) {this.money = this.money + _money;}

    public Integer getID() {return this.ID;}
    public String  getUsername() {return this.username;}
    public String  getEmail() {return this.email;}
    public Integer getMoney() {return this.money;}
}
