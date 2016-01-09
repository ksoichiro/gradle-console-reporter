package com.github.ksoichiro.console.reporter

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ReportJacocoSpec extends Specification {
    static final String PLUGIN_ID = 'com.github.ksoichiro.console.reporter'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir
    PrintStream savedPrintStream
    PrintStream  printStream

    def setup() {
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
        savedPrintStream = System.out
        printStream = Mock(PrintStream)
        System.out = printStream
    }

    def cleanup() {
        System.out = savedPrintStream
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
        1 * printStream.println('C0 Coverage: 72.2%')
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
}
