package com.ssafy.live.model.dto;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private int mno;
    private String name;
    private String email;
    private String password;

    public Member() {
    }

    public Member(String name, String email, String password) {
        this(0, name, email, password);
    }

    public Member(int mno, String name, String email, String password) {
        this.mno = mno;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getMno() {
        return mno;
    }

    public void setMno(int mno) {
        this.mno = mno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Member [mno=" + mno + ", name=" + name + ", email=" + email + ", password=" + password + "]";
    }

}
