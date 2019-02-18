package com.grayraccoon.usersmssample.domain.dto;

public class Users {

    private String id;

    private String name;

    public Users() {}

    public String getId() {
        return id;
    }

    public Users(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
