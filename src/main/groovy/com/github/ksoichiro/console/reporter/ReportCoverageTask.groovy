package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.config.CoverageReportConfig
import com.github.ksoichiro.console.reporter.parser.CoberturaReportParser
import com.github.ksoichiro.console.reporter.parser.IstanbulReportParser
import com.github.ksoichiro.console.reporter.parser.JacocoReportParser
import com.github.ksoichiro.console.reporter.report.CoverageReport
import com.github.ksoichiro.console.reporter.report.TotalReport
import com.github.ksoichiro.console.reporter.writer.CoverageReportWriter
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class ReportCoverageTask extends DefaultTask {
    public static String NAME = 'reportCoverage'
    ConsoleReporterExtension extension
    Map<Project, CoverageReport> reports = [:]
    Map<Project, CoverageReportConfig> reportConfigs = [:]

    ReportCoverageTask() {
        project.afterEvaluate {
            defineTaskGraph()
            extension = project.extensions."${ConsoleReporterExtension.NAME}"

            if (extension.jacoco.autoconfigureCoverageConfig) {
                // Enable XML report carefully and silently
                project.rootProject.allprojects.findAll { it.plugins.hasPlugin('jacoco') }.each {
                    // jacocoTestReport is not an extension
                    try {
                        def jacocoExtension = it.jacocoTestReport
                        if (jacocoExtension && jacocoExtension.reports?.xml != null) {
                            jacocoExtension.reports.xml.enabled = true
                        }
                    } catch (MissingPropertyException ignore) {
                    }
                }
            }
            if (extension.cobertura.autoconfigureCoverageConfig) {
                // Enable XML report carefully and silently
                project.rootProject.allprojects.findAll { it.extensions.findByName('cobertura') != null }.each {
                    def coberturaExtension = it.extensions.findByName('cobertura')
                    Set formats = coberturaExtension.coverageFormats
                    if (formats && !formats.contains('xml')) {
                        coberturaExtension.coverageFormats += 'xml'
                    }
                }
            }
        }
    }

    @TaskAction
    void exec() {
        if (extension.jacoco.enabled && project.rootProject.allprojects.any { it.plugins.hasPlugin('jacoco')}) {
            reportJacoco()
        }
        if (extension.cobertura.enabled && project.rootProject.allprojects.any { it.plugins.hasPlugin('net.saliman.cobertura')}) {
            reportCobertura()
        }
        if (extension.istanbul.enabled && project.rootProject.allprojects.any { it.plugins.hasPlugin('com.moowork.node')}) {
            reportIstanbul()
        }
        if (extension.total.enabled) {
            TotalReport totalReport = new TotalReport()
            totalReport.reports.addAll(reports.values())
            reports.put(project, totalReport)
        }
        new CoverageReportWriter().write(project, reports, reportConfigs)
        didWork = false
    }

    void reportJacoco() {
        reports.putAll(project.rootProject.allprojects
            .findAll { it.plugins.hasPlugin('jacoco') }
            .collectEntries {
            [it, new JacocoReportParser().parse(it, extension.jacoco)]
        })
        reportConfigs.putAll(project.rootProject.allprojects
            .findAll { it.plugins.hasPlugin('jacoco') }
            .collectEntries {
            [it, extension.jacoco]
        })
    }

    void reportCobertura() {
        reports.putAll(project.rootProject.allprojects
            .findAll { it.plugins.hasPlugin('net.saliman.cobertura') }
            .collectEntries {
            [it, new CoberturaReportParser().parse(it, extension.cobertura)]
        })
        reportConfigs.putAll(project.rootProject.allprojects
            .findAll { it.plugins.hasPlugin('net.saliman.cobertura') }
            .collectEntries {
            [it, extension.cobertura]
        })
    }

    void reportIstanbul() {
        reports.putAll(project.rootProject.allprojects
            .findAll { it.plugins.hasPlugin('com.moowork.node') }
            .collectEntries {
            [it, new IstanbulReportParser().parse(it, extension.istanbul)]
        })
        reportConfigs.putAll(project.rootProject.allprojects
            .findAll { it.plugins.hasPlugin('com.moowork.node') }
            .collectEntries {
            [it, extension.istanbul]
        })
    }

    def defineTaskGraph() {
        if (!rootProjectHasCheckTask()) {
            project.tasks.create 'check'
        }
        project.gradle.projectsEvaluated {
            project.rootProject.subprojects.tasks.flatten().findAll { it.name == 'check' }.each {
                project.rootProject.tasks.check.mustRunAfter it
            }
        }
        def thisTask = project.rootProject.tasks.find { it.class == this.class }
        project.rootProject.tasks.check.dependsOn thisTask
    }

    def rootProjectHasCheckTask() {
        project.rootProject.tasks.flatten().any { it.name == 'check' }
    }
}
