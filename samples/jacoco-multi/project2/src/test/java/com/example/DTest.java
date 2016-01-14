package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DTest {
    D instance;

    @Before
    public void setup() {
        instance = new D();
    }

    @Test
    public void greet() {
        System.out.println("To show low coverage, this test does nothing.");
    }
}
