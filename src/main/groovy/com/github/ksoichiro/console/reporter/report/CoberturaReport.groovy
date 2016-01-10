package com.github.ksoichiro.console.reporter.report

import groovy.transform.ToString

@ToString
class CoberturaReport implements CoverageReport {
    float lineRate

    @Override
    public float getC0Coverage() {
        100.0 * lineRate
    }
}
