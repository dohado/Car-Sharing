package com.dohado.carsharing.dao.model;

public class Version {

    private int id;
    private String version;

    public Version(int id, String version) {
        this.id = id;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
