package com.github.ksoichiro.console.reporter.report.junit

import groovy.transform.ToString

@ToString
class JUnitFailure {
    String message
    String type
    String description

    String exceptionMessage() {
        description?.readLines()?.get(0) ?: message
    }

    List<String> exceptionStacktrace() {
        def lines = description.readLines()
        lines.remove(0)
        lines
    }
}
