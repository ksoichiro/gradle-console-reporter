package com.github.ksoichiro.console.reporter.writer

import spock.lang.Specification

class CoverageReportWriterSpec extends Specification {
    def writeResult() {
        when:
        CoverageReportWriter.writeResult(headers, result).call()

        then:
        notThrown(Exception)

        where:
        headers | result
        ["", "Coverage summary:"] | "project1: 70.0%"
        null                      | "project1: 70.0%"
        ["Summary:"]              | "project1: 70.0%"
    }
}
