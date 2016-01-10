package com.github.ksoichiro.console.reporter.parser

import com.github.ksoichiro.console.reporter.config.CoberturaReportConfig
import com.github.ksoichiro.console.reporter.report.CoberturaReport
import org.gradle.api.Project

class CoberturaReportParser implements ReportParser<CoberturaReport, CoberturaReportConfig> {
    @Override
    CoberturaReport parse(Project project, CoberturaReportConfig config) {
        CoberturaReport report = new CoberturaReport()
        if (config.onlyWhenCoverageTaskExecuted
            && !project.gradle.taskGraph.hasTask("${project.path == ':' ? '' : project.path}:${config.coverageTaskName}")) {
            return report
        }
        def testReportFile = config.reportFile ?: project.file("${project.buildDir}/reports/cobertura/coverage.xml")
        if (!testReportFile.exists()) {
            return report
        }
        def rootNode = new XmlParser(false, false).parseText(
            testReportFile.text.replaceAll("<!DOCTYPE[^>]*>", ""))
        report.lineRate = Float.valueOf(rootNode.@"line-rate" as String)
        report
    }
}
