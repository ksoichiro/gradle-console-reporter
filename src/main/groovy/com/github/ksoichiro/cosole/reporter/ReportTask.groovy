package com.github.ksoichiro.cosole.reporter

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ReportTask extends DefaultTask {
    public static String NAME = 'report'
    ConsoleReporterExtension extension

    ReportTask() {
        project.afterEvaluate {
            extension = project.extensions."${ConsoleReporterExtension.NAME}"
        }
    }

    @TaskAction
    void exec() {
        if (extension.junitEnabled) {
            reportJUnit()
        }
    }

    void reportJUnit() {
        def testReportDir = project.file("${project.buildDir}/test-results")
        if (testReportDir.exists()) {
            project.fileTree(dir: testReportDir, includes: ['**/*.xml']).each {
                def rootNode = new XmlParser().parse(it)
                println "${rootNode.@name}: tests: ${rootNode.@tests}, skipped: ${rootNode.@skipped}, failures: ${rootNode.@failures}, errors: ${rootNode.@errors}"
            }
/*
    Example:
    build/test-results/TEST-com.github.ksoichiro.cosole.reporter.PluginTest.xml
        <?xml version="1.0" encoding="UTF-8"?>
        <testsuite name="com.github.ksoichiro.cosole.reporter.PluginTest" tests="2" skipped="0" failures="0" errors="0" timestamp="2015-12-26T13:55:30" hostname="kashima-no-MacBook-Pro-2.local" time="2.418">
        <properties/>
        <testcase name="executeTask" classname="com.github.ksoichiro.cosole.reporter.PluginTest" time="2.374"/>
        <testcase name="apply" classname="com.github.ksoichiro.cosole.reporter.PluginTest" time="0.044"/>
        <system-out><![CDATA[Test
Test2
]]></system-out>
  <system-err><![CDATA[]]></system-err>
        </testsuite>
 */
        }
    }
}
