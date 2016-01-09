# gradle-console-reporter

[![Build Status](https://travis-ci.org/ksoichiro/gradle-console-reporter.svg?branch=master)](https://travis-ci.org/ksoichiro/gradle-console-reporter)
[![Build status](https://ci.appveyor.com/api/projects/status/y4e3vxvgfhg0xjef?svg=true)](https://ci.appveyor.com/project/ksoichiro/gradle-console-reporter)
[![Coverage Status](https://coveralls.io/repos/ksoichiro/gradle-console-reporter/badge.svg?branch=master&service=github)](https://coveralls.io/github/ksoichiro/gradle-console-reporter?branch=master)

> Gradle plugin to report various kinds of summaries to console.  

This plugin will aggregate test reports and show them to console.  
It's useful when you use CI services that don't save artifacts.

Currently, only JUnit test report is available.

## Usage

Just apply the plugin:

```gradle
plugins {
    id 'com.github.ksoichiro.console.reporter' version '0.1.3'
}
```

When your tests fail, the plugin would print test failure details to the console.

```console
$ ./gradlew test
:compileJava UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:compileTestJava UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses UP-TO-DATE
:test

com.example.ATest > greet3 FAILED
    org.junit.ComparisonFailure at ATest.java:31

com.example.ATest > greet FAILED
    org.junit.ComparisonFailure at ATest.java:19

com.example.BTest > greet FAILED
    org.junit.ComparisonFailure at BTest.java:18

5 tests completed, 3 failed
:test FAILED
testsuite com.example.ATest:
  stdout:
    debug log in test2
    debug log
    debug log
    debug log in test
    debug log
  stderr:
    debug error log in test3
  testcase com.example.ATest > greet3: org.junit.ComparisonFailure: expected:<Hello[.]> but was:<Hello[]>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at org.junit.Assert.assertEquals(Assert.java:144)
	at com.example.ATest.greet3(ATest.java:31)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    ...
	at java.lang.Thread.run(Thread.java:745)

  testcase com.example.ATest > greet: org.junit.ComparisonFailure: expected:<Hello[!]> but was:<Hello[]>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at org.junit.Assert.assertEquals(Assert.java:144)
	at com.example.ATest.greet(ATest.java:19)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    ...
	at java.lang.Thread.run(Thread.java:745)

testsuite com.example.BTest:
  testcase com.example.BTest > greet: org.junit.ComparisonFailure: expected:<Bye[!]> but was:<Bye[]>
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

If you're using JaCoCo gradle plugin, you can see the coverage
at the end of the jacocoTestReport task:

```console
$ ./gradlew jacocoTestReport
:compileJava
:processResources
:classes
:jacocoTestReport
C0 Coverage: 72.2%

```

## Tasks

### reportTest

Print JUnit test report.  
This task will be executed automatically after `test` task's failure by default, so you don't need to call it.

### reportJacoco

Print JaCoCo coverage report.
This task will be executed automatically after `jacocoTestReport` task by default, so you don't need to call it.

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

        // Set this property to true if you want to see test summary.
        // Default is false.
        summaryEnabled true

        // Set this property to true if you want to see standard output.
        // Default is false.
        stdoutEnabled true

        // Set this property to true if you want to see standard error.
        // Default is false.
        stderrEnabled true

        // Set this property to false if you don't need stacktrace.
        // Default is true.
        stacktraceEnabled true
    }

    jacoco {
        // Set this property to false if you don't need JaCoCo report.
        // Default is true.
        // Even if this is true, reporting will not work
        // without applying jacoco plugin.
        enabled true

        // Set this property to false if you want to see console report always.
        onlyWhenJacocoTaskExecuted true
        // Set this property to your custom JacocoReport type task name, if you need.
        // Default is 'jacocoTestReport'.
        jacocoTaskName 'jacocoTestReport'

        // Set this property to your JaCoCo report XML file.
        // Default is null, which means
        // ${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml
        // will be parsed.
        reportFile

        // Set this property to a certain C0 coverage percentage.
        // When the coverage is greater than or equals to this value,
        // the coverage will be shown with green color.
        // Default is 90.
        thresholdFine 90

        // Set this property to a certain C0 coverage percentage.
        // When the coverage is greater than or equals to this value,
        // the coverage will be shown with yellow color.
        // (When the coverage is less than this value, result will be red.)
        // Default is 70.
        thresholdWarning 70
    }
}

// You need to set xml.enabled to true
// if you want to print report for JaCoCo.
jacocoTestReport {
    reports {
        xml.enabled true
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
