package com.github.ksoichiro.cosole.reporter

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginSpec extends Specification {
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
        project.tasks."${ReportTask.NAME}" instanceof ReportTask
    }

    def executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junitEnabled = true
        }
        def testReportDir = new File("${rootDir}/build/test-results")
        testReportDir.mkdirs()
        new File("${testReportDir}/TEST-com.example.ExampleTest.xml").text = """\
            |<?xml version="1.0" encoding="UTF-8"?>
            |<testsuite name="com.example.ExampleTest" tests="2" skipped="0" failures="0" errors="0" timestamp="2015-12-26T13:55:30" hostname="kashima-no-MacBook-Pro-2.local" time="2.418">
            |<properties/>
            |<testcase name="executeTask" classname="com.github.ksoichiro.cosole.reporter.PluginTest" time="2.374"/>
            |<testcase name="apply" classname="com.github.ksoichiro.cosole.reporter.PluginTest" time="0.044"/>
            |<system-out><![CDATA[Hello, world!
            |Hello, Gradle!
            |]]></system-out>
            |<system-err><![CDATA[]]></system-err>
            |</testsuite>
            |""".stripMargin().stripIndent()

        when:
        project.evaluate()
        project.tasks."${ReportTask.NAME}".execute()

        then:
        notThrown(Exception)
        1 * printStream.println('com.example.ExampleTest: tests: 2, skipped: 0, failures: 0, errors: 0, time: 2.418')
        1 * printStream.println('Hello, world!')
        1 * printStream.println('Hello, Gradle!')
    }
}
