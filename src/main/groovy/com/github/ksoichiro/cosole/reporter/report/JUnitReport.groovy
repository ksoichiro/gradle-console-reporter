package com.github.ksoichiro.cosole.reporter.report

import com.github.ksoichiro.cosole.reporter.report.junit.JUnitTestsuite
import groovy.transform.ToString

@ToString
class JUnitReport implements Report {
    List<JUnitTestsuite> testsuites

    JUnitReport() {
        testsuites = []
    }
}
