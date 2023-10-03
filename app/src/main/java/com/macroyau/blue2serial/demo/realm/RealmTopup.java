package com.macroyau.blue2serial.demo.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 2CLearning on 12/16/2017.
 */

public class RealmTopup extends RealmObject {

    @PrimaryKey
    private int id;
    private int client;
    private double topup_amount;
    private String description;
    private String created_at;
    private int created_by;

    public RealmTopup() {

    }

    public RealmTopup(int id, int client, double topup_amount, String description, String created_at, int created_by) {
        this.id = id;
        this.client = client;
        this.topup_amount = topup_amount;
        this.description = description;
        this.created_at = created_at;
        this.created_by = created_by;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient() {
        return client;
    }

    public void setClient(int client) {
        this.client = client;
    }

    public double getTopup_amount() {
        return topup_amount;
    }

    public void setTopup_amount(double topup_amount) {
        this.topup_amount = topup_amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }
}
