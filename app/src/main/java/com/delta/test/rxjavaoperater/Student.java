package com.delta.test.rxjavaoperater;

import java.util.List;

/**
 * Created by Shufeng.Wu on 2017/7/3.
 */

public class Student {

    /**
     * name : hehe
     * course : ["maths","english"]
     */

    private String name;
    private List<String> course;

    public Student(String name, List<String> course) {
        this.name = name;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCourse() {
        return course;
    }

    public void setCourse(List<String> course) {
        this.course = course;
    }
}
