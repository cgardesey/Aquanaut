package com.macroyau.blue2serial.demo.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 2CLearning on 12/16/2017.
 */

public class RealmUser extends RealmObject {

    @PrimaryKey
    private int id;
    private String first_name;
    private String last_name;
    private String address;
    private String phone;
    private String emergency_phone;
    private String email;
    private String ghana_card;
    private String date_of_birth;
    private String role;
    private int company;
    private int branch;
    private String created_at;

    private RealmCompany realmCompany;
    private RealmBranch realmBranch;

    public RealmUser() {

    }

    public RealmUser(int id, String first_name, String last_name, String address, String phone, String emergency_phone, String email, String ghana_card, String date_of_birth, String role, int company, int branch, String created_at) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.phone = phone;
        this.emergency_phone = emergency_phone;
        this.email = email;
        this.ghana_card = ghana_card;
        this.date_of_birth = date_of_birth;
        this.role = role;
        this.company = company;
        this.branch = branch;
        this.created_at = created_at;
    }

    public RealmUser(int id, String first_name, String last_name, String address, String phone, String emergency_phone, String email, String ghana_card, String date_of_birth, String role, int company, int branch, String created_at, RealmCompany realmCompany, RealmBranch realmBranch) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.phone = phone;
        this.emergency_phone = emergency_phone;
        this.email = email;
        this.ghana_card = ghana_card;
        this.date_of_birth = date_of_birth;
        this.role = role;
        this.company = company;
        this.branch = branch;
        this.created_at = created_at;
        this.realmCompany = realmCompany;
        this.realmBranch = realmBranch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergency_phone() {
        return emergency_phone;
    }

    public void setEmergency_phone(String emergency_phone) {
        this.emergency_phone = emergency_phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGhana_card() {
        return ghana_card;
    }

    public void setGhana_card(String ghana_card) {
        this.ghana_card = ghana_card;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getCompany() {
        return company;
    }

    public void setCompany(int company) {
        this.company = company;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public RealmCompany getRealmCompany() {
        return realmCompany;
    }

    public void setRealmCompany(RealmCompany realmCompany) {
        this.realmCompany = realmCompany;
    }

    public RealmBranch getRealmBranch() {
        return realmBranch;
    }

    public void setRealmBranch(RealmBranch realmBranch) {
        this.realmBranch = realmBranch;
    }
}
