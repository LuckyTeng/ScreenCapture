package com.example.android.screencapture;

import java.io.Serializable;

public class BeanDemo implements Serializable {
    String Name;
    String age ;
    String address;

    public BeanDemo() {

    }

    public BeanDemo(String name, String age, String address) {
        this.Name = name;
        this.age = age;
        this.address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
