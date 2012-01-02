# DESCRIPTION

_maven-build-utils_ is a collection of build-related utilities in the form 
of plugins and extensions for Maven 3.x. Currently, the provided features
are:

* A summary of durations for each build phase/goal

That's it (for now).

# USAGE

The only thing you need to do in order to start using maven-build-utils
is to add the following build extension snippet into your project's POM:

  <project>
    ...
    <build>
      <extensions>
        <extension>
          <groupId>com.lassekoskela.maven</groupId>
          <artifactId>maven-build-utils</artifactId>
          <version>1.0-SNAPSHOT</version>
        </extension>
      </extensions>
    </build>
    ...
  </project>

That's all. With this in your POM, running any kind of goals and phases,
your build output should include a summary that looks a bit like this:

  [INFO] ----- BUILD STEP DURATIONS -----------------
  [INFO] [generate-sources                                      1,0s   2%]
  [INFO]   modello-maven-plugin:java                            0,9s  92% 
  [INFO]   modello-maven-plugin:xpp3-reader                     0,0s   4% 
  [INFO]   modello-maven-plugin:xpp3-writer                     0,0s   3% 
  [INFO] [generate-resources                                    2,7s   5%]
  [INFO]   maven-remote-resources-plugin:process                2,0s  72% 
  [INFO]   buildnumber-maven-plugin:create                      0,7s  27% 
  [INFO] [process-resources                                     1,6s   3%]
  [INFO]   maven-resources-plugin:resources                     1,6s 100% 
  [INFO] [compile                                               2,5s   5%]
  [INFO]   maven-compiler-plugin:compile                        2,5s 100% 
  [INFO] [process-classes                                      15,0s  30%]
  [INFO]   animal-sniffer-maven-plugin:check                   12,1s  80% 
  [INFO]   plexus-component-metadata:generate-metadata          2,9s  19% 
  [INFO] [process-test-resources                                0,7s   1%]
  [INFO]   maven-resources-plugin:testResources                 0,7s 100% 
  [INFO] [test-compile                                          0,1s   0%]
  [INFO]   maven-compiler-plugin:testCompile                    0,1s 100% 
  [INFO] [process-test-classes                                  1,0s   1%]
  [INFO]   plexus-component-metadata:generate-test-metadata     1,0s 100% 
  [INFO] [test                                                 23,3s  48%]
  [INFO]   maven-surefire-plugin:test                          23,3s 100% 
  [INFO] [package                                               0,5s   1%]
  [INFO]   maven-jar-plugin:jar                                 0,5s 100% 

As you can see, the summary lists the total duration of each build lifecycle
phase that was executed (generate-sources, generate-resources, etc.) as well
as the durations of each goal executed as part of those phases.

The percentages are included for easier overview of where the hotspots are:

* The listed percentage for a _phase_ represents that phase's contribution
  to the whole build's execution.
* The listed percentage for a _goal_ represents that goal's contribution
  to the phase's execution.

# LICENSE

This code is licensed under the Apache License Version 2.0, January 2004.
The full license text is available at [apache.org](http://www.apache.org/licenses/LICENSE-2.0.txt)
