# DESCRIPTION

_maven-build-utils_ is a collection of build-related utilities in the form 
of plugins and extensions for Maven 3.x. Currently, the provided features
are:

* Summary of durations for each goal within a phase
* Summary of durations for each build phase within a project
* Summary of durations for each module in a multi-module project

That's it (for now).

# USAGE

In order to start using maven-build-utils there are three things you need
to add to your project's POM:

    <project>
        ...
        <build>
            <extensions>
                <extension>
                    <groupId>com.github.lkoskela</groupId>
                    <artifactId>maven-build-utils</artifactId>
                    <version>1.5-SNAPSHOT</version>
                </extension>
            </extensions>
        </build>
        ...
        <properties>
            <maven-build-utils.activationProfiles>perfstats</maven-build-utils.activationProfiles>
        </properties>
        ...
        <profiles>
            <profile>
                <id>perfstats</id>
                <properties>
                	<maven-build-utils.activate-timeline>true</maven-build-utils.activate-timeline>
                </properties>
            </profile>
        </profiles>
        ...
    </project>

The first bit to add is the build extension itself. The second bit to add is
the name(s) of the _activation profiles_ that maven-build-utils should run with.
If you'll want to bind maven-build-utils to more than one profile, separate them
with commas. The third bit is the actual profile, which obviously has to exist
for any of this to make sense.

Running your build with these things in your POM, nothing should've changed. It is
only when you run Maven with the particular activation profile(s) configured for
maven-build-utils that you should see any difference in build output. When you want 
maven-build-utils to produce the statistics for the build, invoke the configured
profile with the _-P_ flag, e.g.:

    mvn test -P perfstats

With the right profile activated while running any kind of goals and phases, your 
build output should conclude with a summary that looks a bit like this (for a 
multi-module project):

    [INFO] ------------------------- BUILD STEP DURATIONS -------------------------
    [INFO] PROJECT                                                    DURATION     
    [INFO] | PHASE                                                       PERCENTAGE
    [INFO] | | GOAL                                                          |    |
    [INFO] | | |                                                             |    |
    [INFO] 
    [INFO] *parent                                                        0,9s   5%
    [INFO]   generate-resources                                           0,9s 100%
    [INFO]     maven-remote-resources-plugin:process                      0,9s 100%
    [INFO] 
    [INFO] *module1                                                       6,0s  37%
    [INFO]   generate-sources                                             1,7s  28%
    [INFO]     modello-maven-plugin:java                                  1,2s  71%
    [INFO]     modello-maven-plugin:xpp3-reader                           0,2s  12%
    [INFO]     modello-maven-plugin:xpp3-extended-reader                  0,1s   7%
    [INFO]     modello-maven-plugin:xpp3-writer                           0,2s   8%
    [INFO]   generate-resources                                           0,1s   0%
    [INFO]     maven-remote-resources-plugin:process                      0,1s 100%
    [INFO]   process-resources                                            0,4s   6%
    [INFO]     maven-resources-plugin:resources                           0,4s 100%
    [INFO]   compile                                                      3,8s  63%
    [INFO]     maven-compiler-plugin:compile                              3,8s 100%
    [INFO] 
    [INFO] *module2                                                       0,9s   5%
    [INFO]   generate-sources                                             0,1s  16%
    [INFO]     modello-maven-plugin:java                                  0,1s  55%
    [INFO]     modello-maven-plugin:xpp3-reader                           0,0s  25%
    [INFO]     modello-maven-plugin:xpp3-writer                           0,0s  19%
    [INFO]   generate-resources                                           0,0s   5%
    [INFO]     maven-remote-resources-plugin:process                      0,0s 100%
    [INFO]   process-resources                                            0,0s   1%
    [INFO]     maven-resources-plugin:resources                           0,0s 100%
    [INFO]   compile                                                      0,7s  77%
    [INFO]     maven-compiler-plugin:compile                              0,7s 100%
    [INFO]
    [INFO] ...

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

If you'd like to produce this output by default without passing the specific
profile on the command line, you can activate the profile by default in your
POM like so:

    <profiles>
        <profile>
	        <id>perfstats</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
    </profiles>

# TIMELINE

If you have activated the timeline build with the property "maven-build-utils.activate-timeline", 
an HTML page is generated picturing a view of your build.

    open target/timeline.html
    
# CONFIGURATION

The report is written to the console by default. If you'd prefer to direct
it to a file, you can do that with a system property like so:

    mvn -Dduration.output=file ...

This will direct the output to "target/durations.log" under the execution
directory. If you would prefer a different destination for the log file, 
you can override it with another system property:

    mvn -Dduration.output=file -Dduration.output.file=/tmp/perf.log ...

Good luck!

# LICENSE

This code is licensed under the Apache License Version 2.0, January 2004.
The full license text is available at [apache.org](http://www.apache.org/licenses/LICENSE-2.0.txt)
