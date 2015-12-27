package com.github.ksoichiro.cosole.reporter.report.junit

class JUnitTestsuite {
    String name
    String tests
    String skipped
    String failures
    String errors
    String time
    List<JUnitTestcase> testcases
    String systemOut
    String systemErr

    JUnitTestsuite() {
        testcases = []
    }
}
