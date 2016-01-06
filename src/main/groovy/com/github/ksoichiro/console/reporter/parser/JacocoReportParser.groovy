package com.github.ksoichiro.console.reporter.parser

import com.github.ksoichiro.console.reporter.config.JacocoReportConfig
import com.github.ksoichiro.console.reporter.report.JacocoReport
import com.github.ksoichiro.console.reporter.report.jacoco.JacocoCounter
import com.github.ksoichiro.console.reporter.report.jacoco.JacocoCounterType
import org.gradle.api.Project

class JacocoReportParser implements ReportParser<JacocoReport, JacocoReportConfig> {
    @Override
    JacocoReport parse(Project project, JacocoReportConfig config) {
        JacocoReport report = new JacocoReport()
        def testReportFile = config.reportFile ?: project.file("${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        if (!testReportFile.exists()) {
            return report
        }
        def rootNode = new XmlParser(false, false).parseText(
            testReportFile.text.replaceAll("<!DOCTYPE[^>]*>", ""))
        rootNode.counter.each { counter ->
            try {
                JacocoCounter jacocoCounter = new JacocoCounter()
                jacocoCounter.missed = Integer.valueOf(counter.@missed as String)
                jacocoCounter.covered = Integer.valueOf(counter.@covered as String)
                jacocoCounter.type = JacocoCounterType.valueOf(counter.@type as String)
                report.counters += jacocoCounter
            } catch (ignore) {
            }
        }
        report
    }
}
