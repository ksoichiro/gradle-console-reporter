package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.CoverageReportConfig
import com.github.ksoichiro.console.reporter.report.CoverageReport
import com.github.ksoichiro.console.reporter.util.Colorizer
import org.gradle.api.GradleException
import org.gradle.api.Project

class CoverageReportWriter extends ReportWriter<CoverageReport, CoverageReportConfig> {

    static write(Project project, Map<Project, CoverageReport> reports, Map<Project, CoverageReportConfig> reportConfigs) {
        printOutCoverageReport(project, reports, reportConfigs)
        checkForThresholdFailure(reports, reportConfigs, project)
    }

    static printOutCoverageReport(Project project, Map<Project, CoverageReport> reports, Map<Project, CoverageReportConfig> reportConfigs) {
        project.gradle.buildFinished {
            println ""
            println "Coverage summary:"
            def maxLength = reports.keySet().max { it.name.length() }.name.length()
            reports.each { Project p, CoverageReport report ->
                println(
                    toAnsi("${adjustedProjectName(p, maxLength)} ${rightAlignedCoverage(report.c0Coverage)}",
                        report,
                        reportConfigs.get(p)))
            }
        }
    }

    static Map<Project, CoverageReport> checkForThresholdFailure(Map<Project, CoverageReport> reports, Map<Project, CoverageReportConfig> reportConfigs, project) {
        reports.each { Project p, CoverageReport report ->
            if (reportConfigs.get(p)?.failIfLessThanThresholdError) {
                if (report.c0Coverage < reportConfigs.get(p).thresholdError) {
                    project.gradle.buildFinished {
                        throw new GradleException(reportConfigs.get(p).brokenCoverageErrorMessage)
                    }
                }
            }
        }
    }

    static toAnsi(message, CoverageReport report, CoverageReportConfig config) {
        if (config?.colorEnabled) {
            styleForQuality(report.c0Coverage, message, config)
        } else {
            message
        }
    }

    static styleForQuality(float c0Coverage, message, CoverageReportConfig config) {
        if (config.thresholdFine <= c0Coverage) {
            return Colorizer.green(message)
        }
        if (config.thresholdWarning <= c0Coverage) {
            return Colorizer.yellow(message)
        }
        return Colorizer.red(message)
    }

    static adjustedProjectName(Project p, maxLength) {
        def padding = ""
        (maxLength - p.name.length()).times { padding += " " }
        "${p.name}:${padding}"
    }

    static rightAlignedCoverage(float coverage) {
        "${sprintf("%.1f", coverage).padLeft("100.0".length(), ' ')}%"
    }
}
