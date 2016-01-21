package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.config.JacocoReportConfig
import com.github.ksoichiro.console.reporter.writer.JacocoReportWriter
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.fusesource.jansi.Ansi.Color.*

class ReportJacocoSpec extends Specification {
    static final String PLUGIN_ID = 'com.github.ksoichiro.console.reporter'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir

    def setup() {
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
    }

    def apply() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.apply plugin: PLUGIN_ID

        then:
        notThrown(Exception)
        project.tasks."${ReportJacocoTask.NAME}" instanceof ReportJacocoTask
    }

    def executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'jacoco'
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled false
            }
            jacoco {
                enabled true
            }
        }
        def testReportDir = new File("${rootDir}/build/reports/jacoco/test")
        testReportDir.mkdirs()
        writeSampleReport(testReportDir)

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.create('jacocoTestReport')])
        project.tasks."${ReportJacocoTask.NAME}".execute()

        then:
        notThrown(Exception)
    }

    def executeTaskWithoutReport() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'jacoco'

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.create('jacocoTestReport')])
        project.tasks."${ReportJacocoTask.NAME}".execute()

        then:
        notThrown(Exception)
    }

    def buildFailure() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'jacoco'
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled false
            }
            jacoco {
                enabled true
                failIfLessThanThresholdError true
                thresholdError 100
            }
        }
        def testReportDir = new File("${rootDir}/build/reports/jacoco/test")
        testReportDir.mkdirs()
        writeSampleReport(testReportDir)

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.create('jacocoTestReport')])
        project.tasks."${ReportJacocoTask.NAME}".execute()

        then:
        // buildFinish event is not fired when executing task manually...
        notThrown(GradleException)
    }

    void writeSampleReport(File testReportDir) {
        new File("${testReportDir}/jacocoTestReport.xml").text = """\
            |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.0//EN" "report.dtd">
            |<report name="jacoco">
            |    <sessioninfo id="hostname" start="1452009420512" dump="1452009422151"/>
            |    <package name="com/example">
            |        <class name="com/example/C">
            |            <method name="&lt;init&gt;" desc="()V" line="3">
            |                <counter type="INSTRUCTION" missed="3" covered="0"/>
            |                <counter type="LINE" missed="1" covered="0"/>
            |                <counter type="COMPLEXITY" missed="1" covered="0"/>
            |                <counter type="METHOD" missed="1" covered="0"/>
            |            </method>
            |            <method name="greet" desc="()Ljava/lang/String;" line="5">
            |                <counter type="INSTRUCTION" missed="2" covered="0"/>
            |                <counter type="LINE" missed="1" covered="0"/>
            |                <counter type="COMPLEXITY" missed="1" covered="0"/>
            |                <counter type="METHOD" missed="1" covered="0"/>
            |            </method>
            |            <counter type="INSTRUCTION" missed="5" covered="0"/>
            |            <counter type="LINE" missed="2" covered="0"/>
            |            <counter type="COMPLEXITY" missed="2" covered="0"/>
            |            <counter type="METHOD" missed="2" covered="0"/>
            |            <counter type="CLASS" missed="1" covered="0"/>
            |        </class>
            |        <class name="com/example/A">
            |            <method name="&lt;init&gt;" desc="()V" line="3">
            |                <counter type="INSTRUCTION" missed="0" covered="3"/>
            |                <counter type="LINE" missed="0" covered="1"/>
            |                <counter type="COMPLEXITY" missed="0" covered="1"/>
            |                <counter type="METHOD" missed="0" covered="1"/>
            |            </method>
            |            <method name="greet" desc="()Ljava/lang/String;" line="5">
            |                <counter type="INSTRUCTION" missed="0" covered="5"/>
            |                <counter type="LINE" missed="0" covered="2"/>
            |                <counter type="COMPLEXITY" missed="0" covered="1"/>
            |                <counter type="METHOD" missed="0" covered="1"/>
            |            </method>
            |            <counter type="INSTRUCTION" missed="0" covered="8"/>
            |            <counter type="LINE" missed="0" covered="3"/>
            |            <counter type="COMPLEXITY" missed="0" covered="2"/>
            |            <counter type="METHOD" missed="0" covered="2"/>
            |            <counter type="CLASS" missed="0" covered="1"/>
            |        </class>
            |        <class name="com/example/B">
            |            <method name="&lt;init&gt;" desc="()V" line="3">
            |                <counter type="INSTRUCTION" missed="0" covered="3"/>
            |                <counter type="LINE" missed="0" covered="1"/>
            |                <counter type="COMPLEXITY" missed="0" covered="1"/>
            |                <counter type="METHOD" missed="0" covered="1"/>
            |            </method>
            |            <method name="greet" desc="()Ljava/lang/String;" line="5">
            |                <counter type="INSTRUCTION" missed="0" covered="2"/>
            |                <counter type="LINE" missed="0" covered="1"/>
            |                <counter type="COMPLEXITY" missed="0" covered="1"/>
            |                <counter type="METHOD" missed="0" covered="1"/>
            |            </method>
            |            <counter type="INSTRUCTION" missed="0" covered="5"/>
            |            <counter type="LINE" missed="0" covered="2"/>
            |            <counter type="COMPLEXITY" missed="0" covered="2"/>
            |            <counter type="METHOD" missed="0" covered="2"/>
            |            <counter type="CLASS" missed="0" covered="1"/>
            |        </class>
            |        <sourcefile name="A.java">
            |            <line nr="3" mi="0" ci="3" mb="0" cb="0"/>
            |            <line nr="5" mi="0" ci="3" mb="0" cb="0"/>
            |            <line nr="6" mi="0" ci="2" mb="0" cb="0"/>
            |            <counter type="INSTRUCTION" missed="0" covered="8"/>
            |            <counter type="LINE" missed="0" covered="3"/>
            |            <counter type="COMPLEXITY" missed="0" covered="2"/>
            |            <counter type="METHOD" missed="0" covered="2"/>
            |            <counter type="CLASS" missed="0" covered="1"/>
            |        </sourcefile>
            |        <sourcefile name="B.java">
            |            <line nr="3" mi="0" ci="3" mb="0" cb="0"/>
            |            <line nr="5" mi="0" ci="2" mb="0" cb="0"/>
            |            <counter type="INSTRUCTION" missed="0" covered="5"/>
            |            <counter type="LINE" missed="0" covered="2"/>
            |            <counter type="COMPLEXITY" missed="0" covered="2"/>
            |            <counter type="METHOD" missed="0" covered="2"/>
            |            <counter type="CLASS" missed="0" covered="1"/>
            |        </sourcefile>
            |        <sourcefile name="C.java">
            |            <line nr="3" mi="3" ci="0" mb="0" cb="0"/>
            |            <line nr="5" mi="2" ci="0" mb="0" cb="0"/>
            |            <counter type="INSTRUCTION" missed="5" covered="0"/>
            |            <counter type="LINE" missed="2" covered="0"/>
            |            <counter type="COMPLEXITY" missed="2" covered="0"/>
            |            <counter type="METHOD" missed="2" covered="0"/>
            |            <counter type="CLASS" missed="1" covered="0"/>
            |        </sourcefile>
            |        <counter type="INSTRUCTION" missed="5" covered="13"/>
            |        <counter type="LINE" missed="2" covered="5"/>
            |        <counter type="COMPLEXITY" missed="2" covered="4"/>
            |        <counter type="METHOD" missed="2" covered="4"/>
            |        <counter type="CLASS" missed="1" covered="2"/>
            |    </package>
            |    <counter type="INSTRUCTION" missed="5" covered="13"/>
            |    <counter type="LINE" missed="2" covered="5"/>
            |    <counter type="COMPLEXITY" missed="2" covered="4"/>
            |    <counter type="METHOD" missed="2" covered="4"/>
            |    <counter type="CLASS" missed="1" covered="2"/>
            |</report>
            |""".stripMargin().stripIndent()
    }

    def styleForQuality() {
        setup:
        JacocoReportConfig config = new JacocoReportConfig()
        def writer = new JacocoReportWriter()
        writer.config = config

        when:
        config.thresholdFine = tf
        config.thresholdWarning = tw
        def actualColor = writer.styleForQuality(c0coverage)

        then:
        expectedColor == actualColor

        where:
        tf | tw | c0coverage || expectedColor
        90 | 70 | 72.2       || YELLOW
        50 | 40 | 72.2       || GREEN
        90 | 70 | 0          || RED
    }
}
