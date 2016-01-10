package com.github.ksoichiro.console.reporter.report

import com.github.ksoichiro.console.reporter.report.jacoco.JacocoCounter
import com.github.ksoichiro.console.reporter.report.jacoco.JacocoCounterType
import groovy.transform.ToString

@ToString
class JacocoReport implements CoverageReport {
    List<JacocoCounter> counters

    JacocoReport() {
        counters = []
    }

    @Override
    public float getC0Coverage() {
        JacocoCounter instructionCounter = counters.find { it.type == JacocoCounterType.INSTRUCTION }
        if (!instructionCounter) {
            return 0
        }
        100.0 * instructionCounter.covered / (instructionCounter.missed + instructionCounter.covered)
    }
}
