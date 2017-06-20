package com.github.ksoichiro.console.reporter.report

class TotalReport implements CoverageReport {
    List<CoverageReport> reports = []

    @Override
    float getC0Coverage() {
        return (float) reports.sum { it.c0Coverage } / reports.size()
    }
}
