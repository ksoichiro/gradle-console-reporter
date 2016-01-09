package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.config.CoberturaReportConfig
import com.github.ksoichiro.console.reporter.writer.CoberturaReportWriter
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.fusesource.jansi.Ansi.Color.*

class ReportCoberturaSpec extends Specification {
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
        project.tasks."${ReportCoberturaTask.NAME}" instanceof ReportCoberturaTask
    }

    def executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'net.saliman.cobertura'
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled false
            }
            cobertura {
                enabled true
            }
        }
        def testReportDir = new File("${rootDir}/build/reports/cobertura")
        testReportDir.mkdirs()
        writeSampleReport(testReportDir)

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.cobertura])
        project.tasks."${ReportCoberturaTask.NAME}".execute()

        then:
        notThrown(Exception)
        // With jansi, this will cause error
        //1 * printStream.println('C0 Coverage: 72.2%')
    }

    def executeTaskWithoutReport() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: PLUGIN_ID
        project.apply plugin: 'net.saliman.cobertura'

        when:
        project.evaluate()
        project.gradle.taskGraph.addTasks([project.tasks.cobertura])
        project.tasks."${ReportCoberturaTask.NAME}".execute()

        then:
        notThrown(Exception)
    }

    void writeSampleReport(File testReportDir) {
        new File("${testReportDir}/coverage.xml").text = """\
            |<?xml version="1.0"?>
            |<!DOCTYPE coverage SYSTEM "http://cobertura.sourceforge.net/xml/coverage-04.dtd">
            |
            |<coverage line-rate="0.7142857142857143" branch-rate="1.0" lines-covered="5" lines-valid="7" branches-covered="0" branches-valid="0" complexity="1.0" version="2.1.1" timestamp="1452338735527">
            |    <sources>
            |        <source>/Users/developer/example/src/main/java</source>
            |    </sources>
            |    <packages>
            |        <package name="com.example" line-rate="0.7142857142857143" branch-rate="1.0" complexity="1.0">
            |            <classes>
            |                <class name="com.example.A" filename="com/example/A.java" line-rate="1.0" branch-rate="1.0" complexity="1.0">
            |                    <methods>
            |                        <method name="&lt;init&gt;" signature="()V" line-rate="1.0" branch-rate="1.0" complexity="0">
            |                            <lines>
            |                                <line number="3" hits="1" branch="false"/>
            |                            </lines>
            |                        </method>
            |                        <method name="greet" signature="()Ljava/lang/String;" line-rate="1.0" branch-rate="1.0" complexity="0">
            |                            <lines>
            |                                <line number="5" hits="1" branch="false"/>
            |                                <line number="6" hits="1" branch="false"/>
            |                            </lines>
            |                        </method>
            |                    </methods>
            |                    <lines>
            |                        <line number="3" hits="1" branch="false"/>
            |                        <line number="5" hits="1" branch="false"/>
            |                        <line number="6" hits="1" branch="false"/>
            |                    </lines>
            |                </class>
            |                <class name="com.example.B" filename="com/example/B.java" line-rate="1.0" branch-rate="1.0" complexity="1.0">
            |                    <methods>
            |                        <method name="&lt;init&gt;" signature="()V" line-rate="1.0" branch-rate="1.0" complexity="0">
            |                            <lines>
            |                                <line number="3" hits="1" branch="false"/>
            |                            </lines>
            |                        </method>
            |                        <method name="greet" signature="()Ljava/lang/String;" line-rate="1.0" branch-rate="1.0" complexity="0">
            |                            <lines>
            |                                <line number="5" hits="1" branch="false"/>
            |                            </lines>
            |                        </method>
            |                    </methods>
            |                    <lines>
            |                        <line number="3" hits="1" branch="false"/>
            |                        <line number="5" hits="1" branch="false"/>
            |                    </lines>
            |                </class>
            |                <class name="com.example.C" filename="com/example/C.java" line-rate="0.0" branch-rate="1.0" complexity="1.0">
            |                    <methods>
            |                        <method name="&lt;init&gt;" signature="()V" line-rate="0.0" branch-rate="1.0" complexity="0">
            |                            <lines>
            |                                <line number="3" hits="0" branch="false"/>
            |                            </lines>
            |                        </method>
            |                        <method name="greet" signature="()Ljava/lang/String;" line-rate="0.0" branch-rate="1.0" complexity="0">
            |                            <lines>
            |                                <line number="5" hits="0" branch="false"/>
            |                            </lines>
            |                        </method>
            |                    </methods>
            |                    <lines>
            |                        <line number="3" hits="0" branch="false"/>
            |                        <line number="5" hits="0" branch="false"/>
            |                    </lines>
            |                </class>
            |            </classes>
            |        </package>
            |    </packages>
            |</coverage>
            |""".stripMargin().stripIndent()
    }

    def styleForQuality() {
        setup:
        CoberturaReportConfig config = new CoberturaReportConfig()

        when:
        config.thresholdFine = tf
        config.thresholdWarning = tw
        def actualColor = CoberturaReportWriter.styleForQuality(c0coverage, config)

        then:
        expectedColor == actualColor

        where:
        tf | tw | c0coverage || expectedColor
        90 | 70 | 72.2       || YELLOW
        50 | 40 | 72.2       || GREEN
        90 | 70 | 0          || RED
    }
}
