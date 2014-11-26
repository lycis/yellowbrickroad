Feature: Initialisation of local project
  As developer I want to be able to automatically create a configuration file
  for my local project so I do not have to write it from scratch.

Scenario: Initialisation in current directory
Given the current directory is empty
When I execute an initialisation
Then there is a file named "ybr-config.yml"
And the file "ybr-config.yml" contains the default configuration

Scenario: Explicitly initialise in workdir
Given the current directory is empty
When I execute an initialisation with "."
Then there is a file named "ybr-config.yml"
And the file "ybr-config.yml" contains the default configuration

Scenario: Initialise in a different directory
Given a directory named "somedir/subdir/workhere" was created
When I execute initialisation with "somedir/subdir/workhere"
Then there is a file named "somedir/subdir/workhere/ybr-config.yml"
And the file "somedir/subdir/workhere/ybr-config.yml" contains the default configuration