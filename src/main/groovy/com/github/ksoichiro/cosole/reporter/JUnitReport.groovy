package com.github.ksoichiro.cosole.reporter

class JUnitReport {
    boolean enabled = true
    boolean reportOnFailure = true
    boolean stacktraceEnabled = true

    def methodMissing(String name, args) {
        this."$name" = args[0]
    }
}
