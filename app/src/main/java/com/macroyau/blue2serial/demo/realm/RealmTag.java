package com.macroyau.blue2serial.demo.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 2CLearning on 12/16/2017.
 */

public class RealmTag extends RealmObject {

    @PrimaryKey
    private int id;
    private String status;
    private String tag_uid;
    private String client;

    public RealmTag() {

    }

    public RealmTag(int id, String status, String tag_uid, String client) {
        this.id = id;
        this.status = status;
        this.tag_uid = tag_uid;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTag_uid() {
        return tag_uid;
    }

    public void setTag_uid(String tag_uid) {
        this.tag_uid = tag_uid;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
