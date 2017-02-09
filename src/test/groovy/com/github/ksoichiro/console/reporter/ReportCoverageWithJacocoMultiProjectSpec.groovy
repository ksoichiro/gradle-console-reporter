package com.github.ksoichiro.console.reporter

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ReportCoverageWithJacocoMultiProjectSpec extends Specification {
    static final String PLUGIN_ID = 'com.github.ksoichiro.console.reporter'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir
    File project1Dir
    File project2Dir

    def setup() {
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
        project1Dir = new File(rootDir, "project1")
        if (!project1Dir.exists()) {
            project1Dir.mkdir()
        }
        project2Dir = new File(rootDir, "project2")
        if (!project2Dir.exists()) {
            project2Dir.mkdir()
        }
    }

    def executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        Project project1 = ProjectBuilder.builder().withName(":project1").withParent(project).withProjectDir(project1Dir).build()
        Project project2 = ProjectBuilder.builder().withName(":project2").withParent(project).withProjectDir(project2Dir).build()
        project.apply plugin: PLUGIN_ID
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled false
            }
            jacoco {
                enabled true
            }
        }
        [project1, project2].each { p ->
            p.apply plugin: 'java'
            p.apply plugin: 'jacoco'
        }

        def testReport1Dir = new File("${project1Dir}/build/reports/jacoco/test")
        testReport1Dir.mkdirs()
        writeSampleReport1(testReport1Dir)
        def testReport2Dir = new File("${project2Dir}/build/reports/jacoco/test")
        testReport2Dir.mkdirs()
        writeSampleReport2(testReport2Dir)

        when:
        project.evaluate()
        project1.evaluate()
        project2.evaluate()
        project1.gradle.taskGraph.addTasks([project1.tasks.jacocoTestReport])
        project2.gradle.taskGraph.addTasks([project2.tasks.jacocoTestReport])
        project.tasks."${ReportCoverageTask.NAME}".execute()

        then:
        notThrown(Exception)
    }

    void writeSampleReport1(File testReportDir) {
        new File("${testReportDir}/jacocoTestReport.xml").text = """\
            |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.0//EN" "report.dtd">
            |<report name="project1">
            |<sessioninfo id="localhost" start="1453381882172" dump="1453381883690"/>
            |<package name="com/example">
            |<class name="com/example/C">
            |<method name="&lt;init&gt;" desc="()V" line="3">
            |<counter type="INSTRUCTION" missed="3" covered="0"/>
            |<counter type="LINE" missed="1" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<method name="greet" desc="()Ljava/lang/String;" line="5">
            |<counter type="INSTRUCTION" missed="2" covered="0"/>
            |<counter type="LINE" missed="1" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<counter type="INSTRUCTION" missed="5" covered="0"/>
            |<counter type="LINE" missed="2" covered="0"/>
            |<counter type="COMPLEXITY" missed="2" covered="0"/>
            |<counter type="METHOD" missed="2" covered="0"/>
            |<counter type="CLASS" missed="1" covered="0"/>
            |</class>
            |<class name="com/example/A">
            |<method name="&lt;init&gt;" desc="()V" line="3">
            |<counter type="INSTRUCTION" missed="0" covered="3"/>
            |<counter type="LINE" missed="0" covered="1"/>
            |<counter type="COMPLEXITY" missed="0" covered="1"/>
            |<counter type="METHOD" missed="0" covered="1"/>
            |</method>
            |<method name="greet" desc="()Ljava/lang/String;" line="5">
            |<counter type="INSTRUCTION" missed="0" covered="5"/>
            |<counter type="LINE" missed="0" covered="2"/>
            |<counter type="COMPLEXITY" missed="0" covered="1"/>
            |<counter type="METHOD" missed="0" covered="1"/>
            |</method>
            |<counter type="INSTRUCTION" missed="0" covered="8"/>
            |<counter type="LINE" missed="0" covered="3"/>
            |<counter type="COMPLEXITY" missed="0" covered="2"/>
            |<counter type="METHOD" missed="0" covered="2"/>
            |<counter type="CLASS" missed="0" covered="1"/>
            |</class>
            |<class name="com/example/B">
            |<method name="&lt;init&gt;" desc="()V" line="3">
            |<counter type="INSTRUCTION" missed="0" covered="3"/>
            |<counter type="LINE" missed="0" covered="1"/>
            |<counter type="COMPLEXITY" missed="0" covered="1"/>
            |<counter type="METHOD" missed="0" covered="1"/>
            |</method>
            |<method name="greet" desc="()Ljava/lang/String;" line="5">
            |<counter type="INSTRUCTION" missed="0" covered="2"/>
            |<counter type="LINE" missed="0" covered="1"/>
            |<counter type="COMPLEXITY" missed="0" covered="1"/>
            |<counter type="METHOD" missed="0" covered="1"/>
            |</method>
            |<counter type="INSTRUCTION" missed="0" covered="5"/>
            |<counter type="LINE" missed="0" covered="2"/>
            |<counter type="COMPLEXITY" missed="0" covered="2"/>
            |<counter type="METHOD" missed="0" covered="2"/>
            |<counter type="CLASS" missed="0" covered="1"/>
            |</class>
            |<sourcefile name="A.java">
            |<line nr="3" mi="0" ci="3" mb="0" cb="0"/>
            |<line nr="5" mi="0" ci="3" mb="0" cb="0"/>
            |<line nr="6" mi="0" ci="2" mb="0" cb="0"/>
            |<counter type="INSTRUCTION" missed="0" covered="8"/>
            |<counter type="LINE" missed="0" covered="3"/>
            |<counter type="COMPLEXITY" missed="0" covered="2"/>
            |<counter type="METHOD" missed="0" covered="2"/>
            |<counter type="CLASS" missed="0" covered="1"/>
            |</sourcefile>
            |<sourcefile name="B.java">
            |<line nr="3" mi="0" ci="3" mb="0" cb="0"/>
            |<line nr="5" mi="0" ci="2" mb="0" cb="0"/>
            |<counter type="INSTRUCTION" missed="0" covered="5"/>
            |<counter type="LINE" missed="0" covered="2"/>
            |<counter type="COMPLEXITY" missed="0" covered="2"/>
            |<counter type="METHOD" missed="0" covered="2"/>
            |<counter type="CLASS" missed="0" covered="1"/>
            |</sourcefile>
            |<sourcefile name="C.java">
            |<line nr="3" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="5" mi="2" ci="0" mb="0" cb="0"/>
            |<counter type="INSTRUCTION" missed="5" covered="0"/>
            |<counter type="LINE" missed="2" covered="0"/>
            |<counter type="COMPLEXITY" missed="2" covered="0"/>
            |<counter type="METHOD" missed="2" covered="0"/>
            |<counter type="CLASS" missed="1" covered="0"/>
            |</sourcefile>
            |<counter type="INSTRUCTION" missed="5" covered="13"/>
            |<counter type="LINE" missed="2" covered="5"/>
            |<counter type="COMPLEXITY" missed="2" covered="4"/>
            |<counter type="METHOD" missed="2" covered="4"/>
            |<counter type="CLASS" missed="1" covered="2"/>
            |</package>
            |<counter type="INSTRUCTION" missed="5" covered="13"/>
            |<counter type="LINE" missed="2" covered="5"/>
            |<counter type="COMPLEXITY" missed="2" covered="4"/>
            |<counter type="METHOD" missed="2" covered="4"/>
            |<counter type="CLASS" missed="1" covered="2"/>
            |</report>
            |""".stripMargin().stripIndent()
    }

    void writeSampleReport2(File testReportDir) {
        new File("${testReportDir}/jacocoTestReport.xml").text = """\
            |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.0//EN" "report.dtd">
            |<report name="project2-with-long-name">
            |<sessioninfo id="kashima-no-MacBook-Pro-2.local-a85d9aad" start="1453381885912" dump="1453381887260"/>
            |<package name="com/example">
            |<class name="com/example/D">
            |<method name="&lt;init&gt;" desc="()V" line="3">
            |<counter type="INSTRUCTION" missed="0" covered="3"/>
            |<counter type="LINE" missed="0" covered="1"/>
            |<counter type="COMPLEXITY" missed="0" covered="1"/>
            |<counter type="METHOD" missed="0" covered="1"/>
            |</method>
            |<method name="greet" desc="()Ljava/lang/String;" line="5">
            |<counter type="INSTRUCTION" missed="62" covered="0"/>
            |<counter type="LINE" missed="21" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<counter type="INSTRUCTION" missed="62" covered="3"/>
            |<counter type="LINE" missed="21" covered="1"/>
            |<counter type="COMPLEXITY" missed="1" covered="1"/>
            |<counter type="METHOD" missed="1" covered="1"/>
            |<counter type="CLASS" missed="0" covered="1"/>
            |</class>
            |<class name="com/example/E">
            |<method name="&lt;init&gt;" desc="()V" line="3">
            |<counter type="INSTRUCTION" missed="3" covered="0"/>
            |<counter type="LINE" missed="1" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<method name="greet" desc="()Ljava/lang/String;" line="5">
            |<counter type="INSTRUCTION" missed="2" covered="0"/>
            |<counter type="LINE" missed="1" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<counter type="INSTRUCTION" missed="5" covered="0"/>
            |<counter type="LINE" missed="2" covered="0"/>
            |<counter type="COMPLEXITY" missed="2" covered="0"/>
            |<counter type="METHOD" missed="2" covered="0"/>
            |<counter type="CLASS" missed="1" covered="0"/>
            |</class>
            |<class name="com/example/F">
            |<method name="&lt;init&gt;" desc="()V" line="3">
            |<counter type="INSTRUCTION" missed="3" covered="0"/>
            |<counter type="LINE" missed="1" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<method name="greet" desc="()Ljava/lang/String;" line="5">
            |<counter type="INSTRUCTION" missed="2" covered="0"/>
            |<counter type="LINE" missed="1" covered="0"/>
            |<counter type="COMPLEXITY" missed="1" covered="0"/>
            |<counter type="METHOD" missed="1" covered="0"/>
            |</method>
            |<counter type="INSTRUCTION" missed="5" covered="0"/>
            |<counter type="LINE" missed="2" covered="0"/>
            |<counter type="COMPLEXITY" missed="2" covered="0"/>
            |<counter type="METHOD" missed="2" covered="0"/>
            |<counter type="CLASS" missed="1" covered="0"/>
            |</class>
            |<sourcefile name="F.java">
            |<line nr="3" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="5" mi="2" ci="0" mb="0" cb="0"/>
            |<counter type="INSTRUCTION" missed="5" covered="0"/>
            |<counter type="LINE" missed="2" covered="0"/>
            |<counter type="COMPLEXITY" missed="2" covered="0"/>
            |<counter type="METHOD" missed="2" covered="0"/>
            |<counter type="CLASS" missed="1" covered="0"/>
            |</sourcefile>
            |<sourcefile name="E.java">
            |<line nr="3" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="5" mi="2" ci="0" mb="0" cb="0"/>
            |<counter type="INSTRUCTION" missed="5" covered="0"/>
            |<counter type="LINE" missed="2" covered="0"/>
            |<counter type="COMPLEXITY" missed="2" covered="0"/>
            |<counter type="METHOD" missed="2" covered="0"/>
            |<counter type="CLASS" missed="1" covered="0"/>
            |</sourcefile>
            |<sourcefile name="D.java">
            |<line nr="3" mi="0" ci="3" mb="0" cb="0"/>
            |<line nr="5" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="6" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="7" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="8" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="9" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="10" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="11" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="12" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="13" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="14" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="15" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="16" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="17" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="18" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="19" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="20" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="21" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="22" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="23" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="24" mi="3" ci="0" mb="0" cb="0"/>
            |<line nr="25" mi="2" ci="0" mb="0" cb="0"/>
            |<counter type="INSTRUCTION" missed="62" covered="3"/>
            |<counter type="LINE" missed="21" covered="1"/>
            |<counter type="COMPLEXITY" missed="1" covered="1"/>
            |<counter type="METHOD" missed="1" covered="1"/>
            |<counter type="CLASS" missed="0" covered="1"/>
            |</sourcefile>
            |<counter type="INSTRUCTION" missed="72" covered="3"/>
            |<counter type="LINE" missed="25" covered="1"/>
            |<counter type="COMPLEXITY" missed="5" covered="1"/>
            |<counter type="METHOD" missed="5" covered="1"/>
            |<counter type="CLASS" missed="2" covered="1"/>
            |</package>
            |<counter type="INSTRUCTION" missed="72" covered="3"/>
            |<counter type="LINE" missed="25" covered="1"/>
            |<counter type="COMPLEXITY" missed="5" covered="1"/>
            |<counter type="METHOD" missed="5" covered="1"/>
            |<counter type="CLASS" missed="2" covered="1"/>
            |</report>
            |""".stripMargin().stripIndent()
    }
}
