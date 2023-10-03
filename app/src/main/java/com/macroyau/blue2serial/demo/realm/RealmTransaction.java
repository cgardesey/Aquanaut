package com.macroyau.blue2serial.demo.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 2CLearning on 12/16/2017.
 */

public class RealmTransaction extends RealmObject {

    @PrimaryKey
    private int id;
    private double volume;
    private double sale;
    private String description;
    private int client;
    private int device;
    private int branch;
    private String created_at;


    public RealmTransaction() {

    }

    public RealmTransaction(int id, double volume, double sale, String description, int client, int device, int branch, String created_at) {
        this.id = id;
        this.volume = volume;
        this.sale = sale;
        this.description = description;
        this.client = client;
        this.device = device;
        this.branch = branch;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getClient() {
        return client;
    }

    public void setClient(int client) {
        this.client = client;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
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
}
