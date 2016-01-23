package com.github.ksoichiro.console.reporter.writer

import spock.lang.Specification


class JUnitReportWriterSpec extends Specification {
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
}
