package com.github.ksoichiro.cosole.reporter

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.Test

class ReportTestTask extends DefaultTask {
    public static String NAME = 'reportTest'
    ConsoleReporterExtension extension

    ReportTestTask() {
        project.afterEvaluate {
            extension = project.extensions."${ConsoleReporterExtension.NAME}"

            if (extension.junitReportOnFailure) {
                project.gradle.taskGraph.afterTask { Task task, TaskState state ->
                    if (task instanceof Test && state.failure) {
                        execute()
                    }
                }
            }
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
                def rootNode = new XmlParser(false, false).parse(it)
                println "${rootNode.@name}: tests: ${rootNode.@tests}, skipped: ${rootNode.@skipped}, failures: ${rootNode.@failures}, errors: ${rootNode.@errors}, time: ${rootNode.@time}"
                rootNode."system-out".text().eachLine {
                    println it
                }
                rootNode."system-err".text().eachLine {
                    println it
                }
                rootNode.testcase?.each { testcase ->
                    if (testcase.failure) {
                        if (extension.junitReportStacktrace) {
                            println "${testcase.@classname} > ${testcase.@name}: ${testcase.failure.text()}"
                        } else {
                            // Show message without stacktrace
                            def message = testcase.failure.@message
                            // Remove '[' and ']'
                            (message =~ /^\[(.*)]$/).each { all, containedMessage ->
                                message = containedMessage
                            }
                            println "${testcase.@classname} > ${testcase.@name}: ${message}"
                        }
                    }
                }
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
/*
    Example2:
<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="com.example.BTest" tests="1" skipped="0" failures="1" errors="0" timestamp="2015-12-27T00:06:51" hostname="kashima-no-MacBook-Pro-2.local" time="0.006">
  <properties/>
  <testcase name="greet" classname="com.example.BTest" time="0.006">
    <failure message="org.junit.ComparisonFailure: expected:&lt;Bye[!]&gt; but was:&lt;Bye[]&gt;" type="org.junit.ComparisonFailure">org.junit.ComparisonFailure: expected:&lt;Bye[!]&gt; but was:&lt;Bye[]&gt;
	at org.junit.Assert.assertEquals(Assert.java:115)
	at org.junit.Assert.assertEquals(Assert.java:144)
	at com.example.BTest.greet(BTest.java:18)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:309)
	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:105)
	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:56)
	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:64)
	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:50)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
	at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
	at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
	at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:106)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
	at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
	at org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:360)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:54)
	at org.gradle.internal.concurrent.StoppableExecutorImpl$1.run(StoppableExecutorImpl.java:40)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
</failure>
  </testcase>
  <system-out><![CDATA[]]></system-out>
  <system-err><![CDATA[]]></system-err>
</testsuite>
*/
        }
    }
}
