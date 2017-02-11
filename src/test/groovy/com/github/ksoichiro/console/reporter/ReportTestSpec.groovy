package com.github.ksoichiro.console.reporter

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ReportTestSpec extends Specification {
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
    }

    def executeTask() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: 'java'
        project.apply plugin: PLUGIN_ID
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled true
                summaryEnabled true
                stdoutEnabled true
                stderrEnabled true
            }
        }
        def testReportDir = new File("${rootDir}/build/test-results")
        testReportDir.mkdirs()
        writeSampleReport(testReportDir)

        when:
        project.evaluate()
        project.tasks.test.execute()

        then:
        notThrown(Exception)
    }

    def executeTaskWithoutReport() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: 'java'
        project.apply plugin: PLUGIN_ID

        when:
        project.evaluate()
        project.tasks.test.execute()

        then:
        notThrown(Exception)
    }

    def executeTaskStacktraceDisabled() {
        setup:
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: 'java'
        project.apply plugin: PLUGIN_ID
        project.extensions."${ConsoleReporterExtension.NAME}".with {
            junit {
                enabled true
                stacktraceEnabled false
            }
        }
        def testReportDir = new File("${rootDir}/build/test-results")
        testReportDir.mkdirs()
        writeSampleReport(testReportDir)

        when:
        project.evaluate()
        project.tasks.test.execute()

        then:
        notThrown(Exception)
    }

    void writeSampleReport(File testReportDir) {
        new File("${testReportDir}/TEST-com.example.ExampleTest.xml").text = """\
            |<?xml version="1.0" encoding="UTF-8"?>
            |<testsuite name="com.example.ExampleTest" tests="2" skipped="0" failures="1" errors="0" timestamp="2015-12-26T13:55:30" hostname="localhost" time="2.418">
            |<properties/>
            |<testcase name="executeTask" classname="com.github.ksoichiro.console.reporter.PluginTest" time="2.374"/>
            |<testcase name="greet" classname="com.example.ATest" time="0.044">
            |    <failure message="org.junit.ComparisonFailure: expected:&lt;Hello[!]&gt; but was:&lt;Hello[]&gt;" type="org.junit.ComparisonFailure">org.junit.ComparisonFailure: expected:&lt;Hello[!]&gt; but was:&lt;Hello[]&gt;
            |\tat org.junit.Assert.assertEquals(Assert.java:115)
            |\tat org.junit.Assert.assertEquals(Assert.java:144)
            |\tat com.example.ATest.greet(ATest.java:18)
            |\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            |\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            |\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            |\tat java.lang.reflect.Method.invoke(Method.java:497)
            |\tat org.junit.runners.model.FrameworkMethod\$1.runReflectiveCall(FrameworkMethod.java:47)
            |\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
            |\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
            |\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
            |\tat org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
            |\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
            |\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
            |\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
            |\tat org.junit.runners.ParentRunner\$3.run(ParentRunner.java:238)
            |\tat org.junit.runners.ParentRunner\$1.schedule(ParentRunner.java:63)
            |\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
            |\tat org.junit.runners.ParentRunner.access\$000(ParentRunner.java:53)
            |\tat org.junit.runners.ParentRunner\$2.evaluate(ParentRunner.java:229)
            |\tat org.junit.runners.ParentRunner.run(ParentRunner.java:309)
            |\tat org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:105)
            |\tat org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:56)
            |\tat org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:64)
            |\tat org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:50)
            |\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            |\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            |\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            |\tat java.lang.reflect.Method.invoke(Method.java:497)
            |\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
            |\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
            |\tat org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
            |\tat org.gradle.messaging.dispatch.ProxyDispatchAdapter\$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
            |\tat com.sun.proxy.\$Proxy2.processTestClass(Unknown Source)
            |\tat org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:106)
            |\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            |\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            |\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            |\tat java.lang.reflect.Method.invoke(Method.java:497)
            |\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
            |\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
            |\tat org.gradle.messaging.remote.internal.hub.MessageHub\$Handler.run(MessageHub.java:360)
            |\tat org.gradle.internal.concurrent.ExecutorPolicy\$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:54)
            |\tat org.gradle.internal.concurrent.StoppableExecutorImpl\$1.run(StoppableExecutorImpl.java:40)
            |\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
            |\tat java.util.concurrent.ThreadPoolExecutor\$Worker.run(ThreadPoolExecutor.java:617)
            |\tat java.lang.Thread.run(Thread.java:745)
            |</failure>
            |  </testcase>
            |<system-out><![CDATA[Hello, world!
            |Hello, Gradle!
            |]]></system-out>
            |<system-err><![CDATA[Warning!
            |]]></system-err>
            |</testsuite>
            |""".stripMargin().stripIndent()
    }
}
