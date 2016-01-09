package com.github.ksoichiro.console.reporter.writer

import com.github.ksoichiro.console.reporter.config.JacocoReportConfig
import com.github.ksoichiro.console.reporter.report.JacocoReport
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

import static org.fusesource.jansi.Ansi.Color.GREEN
import static org.fusesource.jansi.Ansi.Color.RED
import static org.fusesource.jansi.Ansi.Color.YELLOW
import static org.fusesource.jansi.Ansi.ansi

class JacocoReportWriter implements ReportWriter<JacocoReport, JacocoReportConfig> {
    @Override
    void write(JacocoReport report, JacocoReportConfig config) {
        AnsiConsole.systemInstall()
        println ansi()
            .fg(styleForQuality(report.c0Coverage, config))
            .a(String.format("C0 Coverage: %.1f%%", report.c0Coverage))
            .reset()
        AnsiConsole.systemUninstall()
    }

    static Ansi.Color styleForQuality(float c0Coverage, JacocoReportConfig config) {
        if (config.thresholdFine <= c0Coverage) {
            return GREEN
        }
        if (config.thresholdWarning <= c0Coverage) {
            return YELLOW
        }
        return RED
    }
}
