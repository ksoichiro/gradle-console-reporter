package com.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class EnclosedTest {
    public static class Enclosed1Test {
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

    public static class Enclosed2Test {
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
}
