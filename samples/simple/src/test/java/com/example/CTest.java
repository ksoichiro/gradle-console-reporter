package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CTest {
    C instance;

    @Before
    public void setup() {
        instance = new C();
    }

    @Test
    public void greet() {
        assertEquals("Good morning", instance.greet());
    }
}
