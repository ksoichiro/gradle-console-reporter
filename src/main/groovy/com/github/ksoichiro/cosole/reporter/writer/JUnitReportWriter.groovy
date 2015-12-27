package com.github.ksoichiro.cosole.reporter.writer

import com.github.ksoichiro.cosole.reporter.config.JUnitReportConfig
import com.github.ksoichiro.cosole.reporter.report.JUnitReport

class JUnitReportWriter implements ReportWriter<JUnitReport, JUnitReportConfig> {
    public static final String INDENT = "  "

    @Override
    void write(JUnitReport report, JUnitReportConfig config) {
        report.testsuites.each { ts ->
            printlnWithIndent(0, "testsuite ${ts.name}:")
            if (config.summaryEnabled) {
                printlnWithIndent(1, "tests: ${ts.tests}, skipped: ${ts.skipped}, failures: ${ts.failures}, errors: ${ts.errors}, time: ${ts.time}")
            }
            if (config.stdoutEnabled) {
                if (ts.systemOut) {
                    printlnWithIndent(1, "stdout:")
                    ts.systemOut.eachLine {
                        printlnWithIndent(2, it)
                    }
                }
            }
            if (config.stderrEnabled) {
                if (ts.systemErr) {
                    printlnWithIndent(1, "stderr:")
                    ts.systemErr.eachLine {
                        printlnWithIndent(2, it)
                    }
                }
            }
            ts.testcases.findAll { it.failed }.each { testcase ->
                if (config.stacktraceEnabled) {
                    if (testcase.failure.description != null && !testcase.failure.description.isEmpty()) {
                        printlnWithIndent(1, "testcase ${testcase.classname} > ${testcase.name}: ${testcase.failure.description}")
                    }
                } else {
                    // Show message without stacktrace
                    def message = testcase.failure.message
                    if (message != null && !message.isEmpty()) {
                        // Remove '[' and ']'
                        (message =~ /^\[(.*)]$/).each { all, containedMessage ->
                            message = containedMessage
                        }
                        printlnWithIndent(1, "testcase ${testcase.classname} > ${testcase.name}: ${message}")
                    }
                }
            }
        }
    }

    static void printlnWithIndent(int level, def line) {
        String indent = ''
        level.times {
            indent += INDENT
        }
        println "${indent}${line}"
    }
}
