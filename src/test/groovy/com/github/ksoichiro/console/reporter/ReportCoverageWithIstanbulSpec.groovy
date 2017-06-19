package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.config.IstanbulReportConfig
import com.github.ksoichiro.console.reporter.report.IstanbulReport
import com.github.ksoichiro.console.reporter.writer.IstanbulReportWriter
import org.gradle.api.Project

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ReportCoverageWithIstanbulSpec extends Specification {
    static final String PLUGIN_ID = 'com.bti360.console.reporter'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir

    def setup() {
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
    }

    def cleanup() {
    }

    def apply() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.apply plugin: PLUGIN_ID

        then:
        notThrown(Exception)
        project.tasks."${ReportCoverageTask.NAME}" instanceof ReportCoverageTask
    }

    def executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'com.moowork.node'
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled false
            }
            istanbul {
                enabled true
            }
        }
        def testReportDir = new File("${rootDir}/coverage/")
        testReportDir.mkdirs()
        writeSampleReport(testReportDir)

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.nodeSetup])
        project.tasks."${ReportCoverageTask.NAME}".execute()

        then:
        notThrown(Exception)
    }

    def executeTaskWithoutReport() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'com.moowork.node'

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.nodeSetup])
        project.tasks."${ReportCoverageTask.NAME}".execute()

        then:
        notThrown(Exception)
    }

    void writeSampleReport(File testReportDir) {
        new File("${testReportDir}/coverage-summary.json").text = """\
            |{
            |  "total":{
            |     "lines":{
            |        "total":20,
            |        "covered":20,
            |        "skipped":0,
            |        "pct":100
            |     },
            |     "statements":{
            |        "total":22,
            |        "covered":22,
            |        "skipped":0,
            |        "pct":100
            |     },
            |     "functions":{
            |        "total":3,
            |        "covered":3,
            |        "skipped":0,
            |        "pct":100
            |     },
            |     "branches":{
            |        "total":0,
            |        "covered":0,
            |        "skipped":0,
            |        "pct":100
            |    }
            |  },
            |}
            |""".stripMargin().stripIndent()
    }

    def messageColor() {
        setup:
        Project project = ProjectBuilder.builder().build()
        IstanbulReportConfig config = new IstanbulReportConfig()
        def writer = new IstanbulReportWriter()
        IstanbulReport report = new IstanbulReport(percentage: 80)
        writer.config = config
        writer.reports = [(project as Project): report]

        when:
        config.colorEnabled = enabled
        def actual = writer.toAnsi(message, report)

        then:
        expected == actual

        where:
        enabled | message || expected
        false   | 'a'     || 'a'
        true    | 'a'     || 'a' // This is not right, but the color is suppressed by Ansi on test
    }
}
