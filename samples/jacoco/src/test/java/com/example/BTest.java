package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BTest {
    B instance;

    @Before
    public void setup() {
        instance = new B();
    }

    @Test
    public void greet() {
        assertEquals("Bye", instance.greet());
    }
}
