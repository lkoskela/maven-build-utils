# DESCRIPTION

_maven-build-utils_ is a collection of build-related utilities in the form 
of plugins and extensions for Maven 3.x. Currently, the provided features
are:

* Summary of durations for each goal within a phase
* Summary of durations for each build phase within a project
* Summary of durations for each module in a multi-module project

That's it (for now).

# USAGE

The only thing you need to do in order to start using maven-build-utils
is to add the following build extension snippet into your project's POM:

    <project>
        ...
        <build>
            <extensions>
                <extension>
                    <groupId>com.github.lkoskela</groupId>
                    <artifactId>maven-build-utils</artifactId>
                    <version>1.2</version>
                </extension>
            </extensions>
        </build>
        ...
    </project>

With this in your POM, running any kind of goals and phases, your build output
should include a summary that looks a bit like this:

    [INFO] ----- BUILD STEP DURATIONS -----------------
    [INFO] *project-1                                             5,5s  28%
    [INFO]   clean                                                0,0s   2%
    [INFO]     maven-clean-plugin:clean                           0,0s 100%
    [INFO]   process-resources                                    0,7s  12%
    [INFO]     maven-resources-plugin:resources                   0,7s 100%
    [INFO]   compile                                              1,0s  18%
    [INFO]     maven-compiler-plugin:compile                      1,0s 100%
    [INFO]   process-test-resources                               0,0s   0%
    [INFO]     maven-resources-plugin:testResources               0,0s 100%
    [INFO]   test-compile                                         0,5s   8%
    [INFO]     maven-compiler-plugin:testCompile                  0,5s 100%
    [INFO]   test                                                 3,3s  60%
    [INFO]     maven-surefire-plugin:test                         3,3s 100%
    [INFO] *project-2                                             0,9s   4%
    [INFO]   clean                                                0,0s   2%
    [INFO]     maven-clean-plugin:clean                           0,0s 100%
    [INFO]   process-resources                                    0,0s   0%
    [INFO]     maven-resources-plugin:resources                   0,0s 100%
    [INFO]   compile                                              0,1s  15%
    [INFO]     maven-compiler-plugin:compile                      0,1s 100%
    [INFO]   process-test-resources                               0,0s   0%
    [INFO]     maven-resources-plugin:testResources               0,0s 100%
    [INFO]   test-compile                                         0,1s  15%
    [INFO]     maven-compiler-plugin:testCompile                  0,1s 100%
    [INFO]   test                                                 0,6s  66%
    [INFO]     maven-surefire-plugin:test                         0,6s 100%

As you can see, the summary lists the total duration of each build lifecycle
phase that was executed (generate-sources, generate-resources, etc.) as well
as the durations of each goal executed as part of those phases.

The percentages are included for easier overview of where the hotspots are:

* The listed percentage for a _project_ represents that project's contribution
  to the whole build's execution (mostly useful in a multi-module project).
* The listed percentage for a _phase_ represents that phase's contribution
  to the project's execution.
* The listed percentage for a _goal_ represents that goal's contribution
  to the phase's execution.

Good luck!

# LICENSE

This code is licensed under the Apache License Version 2.0, January 2004.
The full license text is available at [apache.org](http://www.apache.org/licenses/LICENSE-2.0.txt)
