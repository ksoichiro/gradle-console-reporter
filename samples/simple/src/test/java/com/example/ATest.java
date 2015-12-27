package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ATest {
    A instance;

    @Before
    public void setup() {
        instance = new A();
    }

    @Test
    public void greet() {
        System.out.println("debug log in test");
        assertEquals("Hello!", instance.greet());
    }

    @Test
    public void greet2() {
        System.out.println("debug log in test2");
        assertEquals("Hello", instance.greet());
    }

    @Test
    public void greet3() {
        System.err.println("debug error log in test3");
        assertEquals("Hello.", instance.greet());
    }
}
