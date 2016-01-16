package com.example;

import java.lang.RuntimeException;

public class C {
    public String greet() {
        if (true) {
            throw new RuntimeException("This exception should break the test.");
        }
        return "Good morning";
    }
}
