package com.github.ksoichiro.console.reporter.parser

import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import com.github.ksoichiro.console.reporter.report.JUnitReport
import com.github.ksoichiro.console.reporter.report.junit.JUnitTestcase
import com.github.ksoichiro.console.reporter.report.junit.JUnitTestsuite
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class JUnitReportParser implements ReportParser<JUnitReport, JUnitReportConfig> {
    private final Set<Test> testTasks;

    JUnitReportParser(Set<Test> testTasks) {
        this.testTasks = testTasks;
    }

    @Override
    JUnitReport parse(Project project, JUnitReportConfig config) {
        JUnitReport report = new JUnitReport()

        report.testsuites += testTasks
                .collect { t -> t.getReports().getJunitXml().getDestination() }
                .findAll { dir -> dir.exists() }
                .collect { dir -> getTestSuiteReport(project, config, dir) }

        report
    }

    private JUnitTestsuite getTestSuiteReport(Project project, config, File testReportDir) {
        def testsuite = new JUnitTestsuite()
        project.fileTree(dir: testReportDir, includes: ['**/*.xml']).each { File file ->
            testsuite.with {
                def rootNode = new XmlParser(false, false).parse(file)
                name = rootNode.@name
                tests = rootNode.@tests
                skipped = rootNode.@skipped
                failures = rootNode.@failures
                errors = rootNode.@errors
                time = rootNode.@time
                if (config.stdoutEnabled) {
                    systemOut = rootNode."system-out".text()
                }
                if (config.stderrEnabled) {
                    systemErr = rootNode."system-err".text()
                }
                rootNode.testcase?.each { testcase ->
                    def t = new JUnitTestcase()
                    t.with {
                        name = testcase.@name
                        classname = testcase.@classname
                        time = testcase.@time
                    }
                    if (testcase.failure) {
                        t.failed = true
                        t.failure.with {
                            type = testcase.failure.@type
                            message = testcase.failure.@message
                            description = testcase.failure.text()
                        }
                    }
                    testcases += t
                }
            }
        }
        testsuite
    }
}
