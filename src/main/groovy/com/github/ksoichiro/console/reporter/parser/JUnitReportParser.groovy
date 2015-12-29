package com.github.ksoichiro.console.reporter.parser

import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import com.github.ksoichiro.console.reporter.report.JUnitReport
import com.github.ksoichiro.console.reporter.report.junit.JUnitTestcase
import com.github.ksoichiro.console.reporter.report.junit.JUnitTestsuite
import org.gradle.api.Project

class JUnitReportParser implements ReportParser<JUnitReport, JUnitReportConfig> {
    @Override
    JUnitReport parse(Project project, JUnitReportConfig config) {
        JUnitReport report = new JUnitReport()
        def testReportDir = project.file("${project.buildDir}/test-results")
        if (!testReportDir.exists()) {
            return report
        }
        project.fileTree(dir: testReportDir, includes: ['**/*.xml']).each { File file ->
            def testsuite = new JUnitTestsuite()
            report.testsuites += testsuite
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
        report
    }
}
