package com.github.ksoichiro.console.reporter.parser

import com.github.ksoichiro.console.reporter.config.IstanbulReportConfig
import com.github.ksoichiro.console.reporter.report.IstanbulReport
import groovy.json.JsonSlurper
import org.gradle.api.Project

class IstanbulReportParser implements ReportParser<IstanbulReport, IstanbulReportConfig> {
    @Override
    IstanbulReport parse(Project project, IstanbulReportConfig config) {
        IstanbulReport report = new IstanbulReport()
        if (config.onlyWhenCoverageTaskExecuted) {
            return report
        }
        def testReportFile = config.reportFile ?: project.file("${project.projectDir}/coverage/coverage-summary.json")
        if (!testReportFile.exists()) {
            return report
        }
        def rootNode = new JsonSlurper().parseText(testReportFile.text)
        report.percentage = Float.valueOf(rootNode.total.lines.pct)
        report
    }
}
