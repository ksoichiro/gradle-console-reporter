package com.github.ksoichiro.cosole.reporter.report.junit

import groovy.transform.ToString

@ToString
class JUnitTestcase {
    String classname
    String name
    String time
    JUnitFailure failure

    JUnitTestcase() {
        failure = new JUnitFailure()
    }
}
