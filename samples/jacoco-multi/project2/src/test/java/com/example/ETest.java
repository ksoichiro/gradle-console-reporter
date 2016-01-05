package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ETest {
    E instance;

    @Before
    public void setup() {
        instance = new E();
    }

    @Test
    public void greet() {
        assertEquals("Bye", instance.greet());
    }
}
