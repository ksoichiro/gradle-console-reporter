# gradle-console-reporter

Gradle plugin to report various kinds of summaries to console.  
This might be useful when you use CI and if it cannot save artifacts.

## Usage

## Tasks

## Configurations

```gradle
consoleReporter {
    junit {
        // Set this property to false if you don't need JUnit report.
        // Default is true.
        enabled true

        // Set this property to false if you want to see console report always.
        // Default is true.
        reportOnFailure true

        // Set this property to false if you don't need stacktrace.
        // Default is true.
        stacktraceEnabled true
    }
}
```

## License

    Copyright 2015 Soichiro Kashima

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
