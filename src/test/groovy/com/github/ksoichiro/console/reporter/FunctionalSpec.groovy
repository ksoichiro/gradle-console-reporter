package com.github.ksoichiro.console.reporter

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FunctionalSpec extends Specification {
    static final String PLUGIN_ID = 'com.github.ksoichiro.console.reporter'

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    List<File> pluginClasspath

    void setup() {
        buildFile = testProjectDir.newFile("build.gradle")

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines()
            .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
            .collect { new File(it) }

        def srcMainDir = testProjectDir.newFolder("src", "main", "java", "com", "example")
        new File(srcMainDir, "A.java").text = """\
            |package com.example;
            |
            |public class A {
            |    public String greet() {
            |        System.out.println("debug log");
            |        return "Hello";
            |    }
            |}""".stripMargin().stripIndent()
        def srcTestDir = testProjectDir.newFolder("src", "test", "java", "com", "example")
        new File(srcTestDir, "ATest.java").text = """\
            |package com.example;
            |
            |import org.junit.Before;
            |import org.junit.Test;
            |
            |import static org.junit.Assert.*;
            |
            |public class ATest {
            |    A instance;
            |
            |    @Before
            |    public void setup() {
            |        instance = new A();
            |    }
            |
            |    @Test
            |    public void greet() {
            |        System.out.println("debug log in test");
            |        assertEquals("Hello!", instance.greet());
            |    }
            |}""".stripMargin().stripIndent()
    }

    def automaticallyExecutedAfterTestFailure() throws IOException {
        setup:
        def buildFileContent = """\
            |plugins {
            |    id '${PLUGIN_ID}'
            |}
            |apply plugin: 'java'
            |repositories {
            |    mavenCentral()
            |}
            |dependencies {
            |    testCompile 'junit:junit:4.11'
            |}
            |consoleReporter {
            |    junit {
            |        stdoutEnabled true
            |    }
            |}
            |""".stripMargin().stripIndent()
        buildFile.text = buildFileContent

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("test")
            .withPluginClasspath(pluginClasspath)
            .buildAndFail()
        def stdout = result.output

        then:
        stdout.contains("testsuite com.example.ATest:")
        TaskOutcome.FAILED == result.task(":test").getOutcome()
    }
}
