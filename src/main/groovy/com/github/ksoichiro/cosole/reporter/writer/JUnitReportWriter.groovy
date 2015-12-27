package com.github.ksoichiro.cosole.reporter.writer

import com.github.ksoichiro.cosole.reporter.config.JUnitReportConfig
import com.github.ksoichiro.cosole.reporter.report.JUnitReport

class JUnitReportWriter implements ReportWriter<JUnitReport, JUnitReportConfig> {
    @Override
    void write(JUnitReport report, JUnitReportConfig config) {
        report.testsuites.each { ts ->
            if (config.summaryEnabled) {
                println "${ts.name}: tests: ${ts.tests}, skipped: ${ts.skipped}, failures: ${ts.failures}, errors: ${ts.errors}, time: ${ts.time}"
            }
            if (config.stdoutEnabled) {
                ts.systemOut.eachLine {
                    println it
                }
            }
            if (config.stderrEnabled) {
                ts.systemErr.eachLine {
                    println it
                }
            }
            ts.testcases.each { testcase ->
                if (config.stacktraceEnabled) {
                    if (testcase.failure.description != null && !testcase.failure.description.isEmpty()) {
                        println "${testcase.classname} > ${testcase.name}: ${testcase.failure.description}"
                    }
                } else {
                    // Show message without stacktrace
                    def message = testcase.failure.message
                    if (message != null && !message.isEmpty()) {
                        // Remove '[' and ']'
                        (message =~ /^\[(.*)]$/).each { all, containedMessage ->
                            message = containedMessage
                        }
                        println "${testcase.classname} > ${testcase.name}: ${message}"
                    }
                }
            }
        }
    }
}
