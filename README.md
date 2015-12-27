# gradle-console-reporter

> Gradle plugin to report various kinds of summaries to console.  

This might be useful when you use CI and if it cannot save artifacts.  
Currently, only JUnit test report is available.

## Usage

Just apply the plugin:

```
plugins {
    id 'com.github.ksoichiro.console.reporter' version '0.1.0'
}
```

When your tests fail, the plugin would print test failure details to the console.

```
➜  simple git:(master) ✗ ./gradlew test 
:compileJava UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:compileTestJava UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses UP-TO-DATE
:test

com.example.ATest > greet FAILED
    org.junit.ComparisonFailure at ATest.java:18

com.example.BTest > greet FAILED
    org.junit.ComparisonFailure at BTest.java:18

2 tests completed, 2 failed
:test FAILED
com.example.ATest > greet: org.junit.ComparisonFailure: expected:<Hello[!]> but was:<Hello[]>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at org.junit.Assert.assertEquals(Assert.java:144)
	at com.example.ATest.greet(ATest.java:18)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    ...
	at java.lang.Thread.run(Thread.java:745)

com.example.BTest > greet: org.junit.ComparisonFailure: expected:<Bye[!]> but was:<Bye[]>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at org.junit.Assert.assertEquals(Assert.java:144)
	at com.example.BTest.greet(BTest.java:18)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    ...
	at java.lang.Thread.run(Thread.java:745)

...
```

You can suppress stacktrace by configuring the plugin.  
See [configurations](#configurations) section for details.

## Tasks

### reportTest

Print JUnit test report.  
This task will be executed automatically after `test` task's failure by default, so you don't need to call it.

## Configurations

```gradle
consoleReporter {
    junit {
        // Set this property to false if you don't need JUnit report.
        // Default is true.
        enabled true

        // Set this property to false if you want to see console report always.
        // Default is true.
        reportOnFailure true

        // Set this property to false if you don't need stacktrace.
        // Default is true.
        stacktraceEnabled true
    }
}
```

## License

    Copyright 2015 Soichiro Kashima

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
