package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import com.github.ksoichiro.console.reporter.report.JUnitReport
import com.github.ksoichiro.console.reporter.report.junit.JUnitTestcase
import com.github.ksoichiro.console.reporter.report.junit.JUnitTestsuite
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.gradle.api.Project

import static org.fusesource.jansi.Ansi.ansi

class JUnitReportWriter implements ReportWriter<JUnitReport, JUnitReportConfig> {
    public static final String INDENT = "  "
    Project project
    Map<String, File> sourceMap
    JUnitReportConfig config
    boolean colorEnabled

    @Override
    void write(Project project, JUnitReport report, JUnitReportConfig config) {
        this.project = project
        this.config = config
        this.colorEnabled = config.colorEnabled
        AnsiConsole.systemInstall()
        report.testsuites.findAll { it.testcases.any { it.failed} }.eachWithIndex { ts, i ->
            // Insert newline between test suites
            if (0 < i) {
                println()
            }
            writeTestsuite(ts)
        }
        AnsiConsole.systemUninstall()
    }

    def writeTestsuite(JUnitTestsuite ts) {
        printlnWithIndent(0, "testsuite ${toCyan(ts.name)}:")
        writeSummary(ts)
        writeSystemOut(ts)
        writeSystemErr(ts)
        ts.testcases.findAll { it.failed }.each { testcase ->
            writeTestcase(testcase)
        }
    }

    def writeSummary(JUnitTestsuite ts) {
        if (config.summaryEnabled) {
            printlnWithIndent(1, "tests: ${ts.tests}, skipped: ${ts.skipped}, failures: ${ts.failures}, errors: ${ts.errors}, time: ${ts.time}")
        }
    }

    def writeSystemOut(JUnitTestsuite ts) {
        if (!config.stdoutEnabled || !ts.systemOut) {
            return
        }
        printlnWithIndent(1, "stdout:")
        ts.systemOut.eachLine {
            printlnWithIndent(2, toGray(it))
        }
    }

    def writeSystemErr(JUnitTestsuite ts) {
        if (!config.stderrEnabled || !ts.systemErr) {
            return
        }
        printlnWithIndent(1, "stderr:")
        ts.systemErr.eachLine {
            printlnWithIndent(2, toGray(it))
        }
    }

    def writeTestcase(JUnitTestcase testcase) {
        if (config.stacktraceEnabled) {
            writeTestcaseWithStacktrace(testcase)
        } else {
            writeTestcaseWithoutStacktrace(testcase)
        }
    }

    def writeTestcaseWithStacktrace(JUnitTestcase testcase) {
        if (testcase.failure.description == null || testcase.failure.description.isEmpty()) {
            return
        }
        printlnWithIndent(1, "testcase ${toCyan(testcase.classname)} > ${toMagenta(testcase.name)}: ${testcase.failure.exceptionMessage()}")
        def limitToSuppress = -1
        testcase.failure.exceptionStacktrace()?.each {
            if (limitToSuppress != 0) {
                printlnWithIndent(2, highlightStacktrace(it, testcase.classname))
                if (shouldHighlight(it, testcase.classname)) {
                    if (limitToSuppress < 0) {
                        limitToSuppress = 1 + 5
                    }
                    printPartialSource(testcase.classname, it)
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

    def writeTestcaseWithoutStacktrace(JUnitTestcase testcase) {
        def message = testcase.failure.message
        if (message == null || message.isEmpty()) {
            return
        }
        message = stripBrackets(message)
        printlnWithIndent(1, "testcase ${toCyan(testcase.classname)} > ${toMagenta(testcase.name)}: ${message}")
    }

    def printPartialSource(String classname, String stacktraceLine) {
        int lineNumber = getLineNumberFromStacktraceLine(stacktraceLine)
        if (lineNumber == -1) {
            // Not found (might be a native method or unknown source)
            return
        }
        File srcFile = findSourceFile(classname)
        if (!srcFile) {
            return
        }
        def lines = srcFile.readLines()
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

    def findSourceFile(String classname) {
        collectSourceFiles()
        String targetClassname = getExternalClassname(classname)
        sourceMap.find { k, v ->
            k.contains(targetClassname)
        }?.value
    }

    def collectSourceFiles() {
        if (sourceMap) {
            return
        }
        sourceMap = project.sourceSets.collect {
            it.allSource.asList()
        }.flatten().collectEntries {
            [(it.absolutePath as String).replaceAll("\\\\", "/").replaceAll("/", "."), it]
        }
    }

    def toGray(def line) {
        if (colorEnabled) {
            ansi().fgBright(Ansi.Color.BLACK).a(line).reset()
        } else {
            line
        }
    }

    def toCyan(def line) {
        if (colorEnabled) {
            ansi().fg(Ansi.Color.CYAN).a(line).reset()
        } else {
            line
        }
    }

    def toMagenta(def line) {
        if (colorEnabled) {
            ansi().fg(Ansi.Color.MAGENTA).a(line).reset()
        } else {
            line
        }
    }

    def highlightStacktrace(String line, String expr) {
        def edited = line.replace('\t', '')
        if (colorEnabled) {
            if (shouldHighlight(line, expr)) {
                ansi().fg(Ansi.Color.RED).a(edited).reset()
            } else {
                toGray(edited)
            }
        } else {
            edited
        }
    }

    /** Extract line number: at com.example.CTest.greet(CTest.java:18) */
    static def getLineNumberFromStacktraceLine(String stacktraceLine) {
        def lineNumber = -1
        (stacktraceLine =~ /\(.*:([0-9]*)\)$/).each { all, ln ->
            lineNumber = ln.toInteger()
        }
        lineNumber
    }

    /** Remove inner class name (e.g. &#064;Enclosed test) */
    static def getExternalClassname(String classname) {
        classname.replaceAll('\\$.*', "")
    }

    /** Remove '[' and ']' from input */
    static def stripBrackets(def input) {
        def output = input
        (output =~ /^\[(.*)]$/).each { all, contained ->
            output = contained
        }
        output
    }

    static boolean shouldHighlight(String line, String expr) {
        line.replace('\t', '').contains(expr)
    }

    static void printlnWithIndent(int level, def line) {
        String indent = ''
        level.times {
            indent += INDENT
        }
        println "${indent}${line}"
    }
}
