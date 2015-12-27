package com.github.ksoichiro.cosole.reporter.report

import com.github.ksoichiro.cosole.reporter.report.junit.JUnitTestsuite

class JUnitReport implements Report {
    JUnitTestsuite testsuite

    JUnitReport() {
        testsuite = new JUnitTestsuite()
    }
}
