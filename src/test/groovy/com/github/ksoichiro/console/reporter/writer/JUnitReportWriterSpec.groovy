package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.JUnitReportConfig
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification


class JUnitReportWriterSpec extends Specification {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File rootDir

    def getExternalClassname() {
        expect:
        expected == JUnitReportWriter.getExternalClassname(classname)

        where:
        classname                                 || expected
        "com.example.C"                           || "com.example.C"
        "com.example.EnclosedTest\$Enclosed2Test" || "com.example.EnclosedTest"
        "com.example.External\$Inner\$Nested"     || "com.example.External"
    }

    def stripBrackets() {
        expect:
        expected == JUnitReportWriter.stripBrackets(input)

        where:
        input                  || expected
        "[foo]"                || "foo"
        "[exception: message]" || "exception: message"
        "already stripped"     || "already stripped"
    }

    def extractClassname() {
        expect:
        expected == JUnitReportWriter.extractClassname(stacktraceLine)

        where:
        stacktraceLine                                                             || expected
        "\tat com.example.C.greet(C.java:8)"                                       || "com.example.C"
        "\tat com.example.EnclosedTest\$Enclosed2Test.greet(EnclosedTest.java:36)" || "com.example.EnclosedTest\$Enclosed2Test"
        "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)"         || "sun.reflect.NativeMethodAccessorImpl"
        "\tat sun.reflect.NativeMethodAccessorImpl.invoke0"                        || null // unknown format
    }

    def writePartialSourceDisabled() {
        setup:
        def writer = Spy(JUnitReportWriter)
        writer.config = new JUnitReportConfig(partialSourceInsertionEnabled: false)

        when:
        writer.writePartialSource("\tat com.example.C.greet(C.java:8)")

        then:
        notThrown(Exception)
        0 * writer.printlnWithIndent(_, _)
    }

    def writePartialSource() {
        setup:
        GroovySpy(JUnitReportWriter, global: true)
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
        def srcMainDir = testProjectDir.newFolder("src", "main", "java", "com", "example")
        new File("${srcMainDir}/C.java").text = """\
            |package com.example;
            |
            |import java.lang.RuntimeException;
            |
            |public class C {
            |    public String greet() {
            |        if (true) {
            |            throw new RuntimeException("This exception should break the test.");
            |        }
            |        return "Good morning";
            |    }
            |}""".stripMargin().stripIndent()
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: 'java'
        project.evaluate()

        def writer = new JUnitReportWriter()
        writer.project = project
        writer.config = new JUnitReportConfig(partialSourceInsertionEnabled: true)

        when:
        writer.writePartialSource("\tat com.example.C.greet(C.java:8)")

        then:
        notThrown(Exception)
        (1.._) * JUnitReportWriter.printlnWithIndent(_, _)
    }

    def collectSourceFiles() {
        setup:
        rootDir = testProjectDir.root
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
        def srcMainDir = testProjectDir.newFolder("src", "main", "java", "com", "example")
        new File("${srcMainDir}/C.java").text = """\
            |package com.example;
            |
            |import java.lang.RuntimeException;
            |
            |public class C {
            |    public String greet() {
            |        if (true) {
            |            throw new RuntimeException("This exception should break the test.");
            |        }
            |        return "Good morning";
            |    }
            |}""".stripMargin().stripIndent()
        Project project = ProjectBuilder.builder().withProjectDir(rootDir).build()
        project.apply plugin: 'java'
        project.evaluate()

        def writer = new JUnitReportWriter(project: project)

        when:
        writer.collectSourceFiles()
        def sourceMap1 = writer.sourceMap.clone()
        writer.collectSourceFiles()
        def sourceMap2 = writer.sourceMap.clone()

        then:
        sourceMap1 == sourceMap2
    }
}
