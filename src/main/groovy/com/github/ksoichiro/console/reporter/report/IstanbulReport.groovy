package com.github.ksoichiro.console.reporter.report

import groovy.transform.ToString

@ToString
class IstanbulReport implements CoverageReport {
    float percentage

    @Override
    public float getC0Coverage() {
        percentage
    }
}
