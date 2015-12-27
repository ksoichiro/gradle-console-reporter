package com.github.ksoichiro.cosole.reporter.writer

import com.github.ksoichiro.cosole.reporter.config.JUnitReportConfig
import com.github.ksoichiro.cosole.reporter.report.JUnitReport

class JUnitReportWriter implements ReportWriter<JUnitReport, JUnitReportConfig> {
    @Override
    void write(JUnitReport report, JUnitReportConfig config) {
        report.testsuite.with {
            if (config.summaryEnabled) {
                println "${name}: tests: ${tests}, skipped: ${skipped}, failures: ${failures}, errors: ${errors}, time: ${time}"
            }
            if (config.stdoutEnabled) {
                systemOut.eachLine {
                    println it
                }
            }
            if (config.stderrEnabled) {
                systemErr.eachLine {
                    println it
                }
            }
            testcases.each { testcase ->
                testcase.with {
                    if (config.stacktraceEnabled) {
                        if (failure.description != null && !failure.description.isEmpty()) {
                            println "${classname} > ${name}: ${failure.description}"
                        }
                    } else {
                        // Show message without stacktrace
                        def message = failure.message
                        if (message != null && !message.isEmpty()) {
                            // Remove '[' and ']'
                            (message =~ /^\[(.*)]$/).each { all, containedMessage ->
                                message = containedMessage
                            }
                            println "${classname} > ${name}: ${message}"
                        }
                    }
                }
            }
        }
    }
}
