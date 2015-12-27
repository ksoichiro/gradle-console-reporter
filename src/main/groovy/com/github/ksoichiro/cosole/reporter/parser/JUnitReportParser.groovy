package com.github.ksoichiro.cosole.reporter.parser

import com.github.ksoichiro.cosole.reporter.config.JUnitReportConfig
import com.github.ksoichiro.cosole.reporter.report.JUnitReport
import com.github.ksoichiro.cosole.reporter.report.junit.JUnitTestcase
import org.gradle.api.Project

class JUnitReportParser implements ReportParser<JUnitReport, JUnitReportConfig> {
    @Override
    JUnitReport parse(Project project, JUnitReportConfig config) {
        JUnitReport report = new JUnitReport()
        def testReportDir = project.file("${project.buildDir}/test-results")
        if (!testReportDir.exists()) {
            return report
        }
        project.fileTree(dir: testReportDir, includes: ['**/*.xml']).each {
            def rootNode = new XmlParser(false, false).parse(it)
            if (config.summaryEnabled) {
                report.testsuite.with {
                    name = rootNode.@name
                    tests = rootNode.@tests
                    skipped = rootNode.@skipped
                    failures = rootNode.@failures
                    errors = rootNode.@errors
                    time = rootNode.@time
                }
            }
            if (config.stdoutEnabled) {
                report.testsuite.systemOut = rootNode."system-out".text()
            }
            if (config.stderrEnabled) {
                report.testsuite.systemErr = rootNode."system-err".text()
            }
            rootNode.testcase?.each { testcase ->
                def t = new JUnitTestcase()
                t.with {
                    name = testcase.@name
                    classname = testcase.@classname
                    time = testcase.@time
                }
                if (testcase.failure) {
                    t.failure.type = testcase.failure.@type
                    t.failure.message = testcase.failure.@message
                    t.failure.description = testcase.failure.text()
                }
                report.testsuite.testcases += t
            }
        }
        report
    }
}
