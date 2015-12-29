package com.github.ksoichiro.console.reporter.report

import com.github.ksoichiro.console.reporter.report.junit.JUnitTestsuite
import groovy.transform.ToString

@ToString
class JUnitReport implements Report {
    List<JUnitTestsuite> testsuites

    JUnitReport() {
        testsuites = []
    }
}
