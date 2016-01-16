package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import com.github.ksoichiro.console.reporter.report.JUnitReport
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.gradle.api.Project

import static org.fusesource.jansi.Ansi.ansi

class JUnitReportWriter implements ReportWriter<JUnitReport, JUnitReportConfig> {
    public static final String INDENT = "  "

    @Override
    void write(Project project, JUnitReport report, JUnitReportConfig config) {
        AnsiConsole.systemInstall()
        report.testsuites.findAll { it.testcases.any { it.failed} }.eachWithIndex { ts, i ->
            // Insert newline between test suites
            if (0 < i) {
                println()
            }
            printlnWithIndent(0, "testsuite ${toCyan(ts.name)}:")
            if (config.summaryEnabled) {
                printlnWithIndent(1, "tests: ${ts.tests}, skipped: ${ts.skipped}, failures: ${ts.failures}, errors: ${ts.errors}, time: ${ts.time}")
            }
            if (config.stdoutEnabled) {
                if (ts.systemOut) {
                    printlnWithIndent(1, "stdout:")
                    ts.systemOut.eachLine {
                        printlnWithIndent(2, toGray(it))
                    }
                }
            }
            if (config.stderrEnabled) {
                if (ts.systemErr) {
                    printlnWithIndent(1, "stderr:")
                    ts.systemErr.eachLine {
                        printlnWithIndent(2, toGray(it))
                    }
                }
            }
            ts.testcases.findAll { it.failed }.each { testcase ->
                if (config.stacktraceEnabled) {
                    if (testcase.failure.description != null && !testcase.failure.description.isEmpty()) {
                        printlnWithIndent(1, "testcase ${toCyan(testcase.classname)} > ${toMagenta(testcase.name)}: ${testcase.failure.exceptionMessage()}")
                        def limitToSuppress = -1
                        testcase.failure.exceptionStacktrace()?.eachWithIndex { it, stIdx ->
                            if (limitToSuppress != 0) {
                                printlnWithIndent(2, highlightStacktrace(it, testcase.classname))
                                if (shouldHighlight(it, testcase.classname)) {
                                    if (limitToSuppress < 0) {
                                        limitToSuppress = 1 + 5
                                    }
                                    printPartialSource(project, testcase.classname, it)
                                }
                                if (0 < limitToSuppress) {
                                    limitToSuppress--
                                    if (limitToSuppress == 0) {
                                        printlnWithIndent(2, toGray("..."))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Show message without stacktrace
                    def message = testcase.failure.message
                    if (message != null && !message.isEmpty()) {
                        // Remove '[' and ']'
                        (message =~ /^\[(.*)]$/).each { all, containedMessage ->
                            message = containedMessage
                        }
                        printlnWithIndent(1, "testcase ${toCyan(testcase.classname)} > ${toMagenta(testcase.name)}: ${message}")
                    }
                }
            }
        }
        AnsiConsole.systemUninstall()
    }

    static def printPartialSource(Project project, String classname, String stacktraceLine) {
        int lineNumber = -1
        // Extract line number: at com.example.CTest.greet(CTest.java:18)
        (stacktraceLine =~ /\(.*:([0-9]*)\)$/).each { all, ln ->
            lineNumber = ln.toInteger()
        }
        if (lineNumber == -1) {
            // Not found (might be a native method or unknown source)
            return
        }
        // Remove inner class name (e.g. @Enclosed test)
        String targetClassname = classname.replaceAll('\\$.*', "")
        String srcFilePath = null
        project.sourceSets.each { type ->
            def tmp = type.allSource.find {
                (it as String).replaceAll("/", ".").replaceAll("\\\\", ".").contains(targetClassname)
            }
            if (tmp) {
                srcFilePath = tmp
            }
        }
        if (srcFilePath) {
            def lines = new File(srcFilePath).readLines()
            def beforeLines = 1
            def afterLines = 1
            def first = (1 <= lineNumber - beforeLines) ? lineNumber - beforeLines : 1
            def last = (lineNumber + afterLines <= lines.size()) ? lineNumber + afterLines : lines.size()
            printlnWithIndent(3, "")
            (first .. last).each { ln ->
                def indicator = " "
                def srcLine = lines.get(ln - 1)
                if (ln == lineNumber) {
                    indicator = ">"
                    srcLine = toMagenta(srcLine)
                }
                printlnWithIndent(3, "${ln}: ${indicator} ${srcLine}")
            }
            printlnWithIndent(3, "")
        }
    }

    static Ansi toGray(def line) {
        ansi().fgBright(Ansi.Color.BLACK).a(line).reset()
    }

    static Ansi toCyan(def line) {
        ansi().fg(Ansi.Color.CYAN).a(line).reset()
    }

    static Ansi toMagenta(def line) {
        ansi().fg(Ansi.Color.MAGENTA).a(line).reset()
    }

    static boolean shouldHighlight(String line, String expr) {
        line.replace('\t', '').contains(expr)
    }

    static Ansi highlightStacktrace(String line, String expr) {
        def edited = line.replace('\t', '')
        if (shouldHighlight(line, expr)) {
            ansi().fg(Ansi.Color.RED).a(edited).reset()
        } else {
            toGray(edited)
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
