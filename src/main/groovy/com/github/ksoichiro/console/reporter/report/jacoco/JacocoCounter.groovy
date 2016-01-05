package com.github.ksoichiro.console.reporter.report.jacoco

import groovy.transform.ToString

@ToString
class JacocoCounter {
    JacocoCounterType type
    int missed
    int covered
}
