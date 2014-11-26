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

Scenario Outline: Initialise in a different directory with specific filename
Given a directory named "<target>" was created
When I execute initialisation with "-f <file> <target>"
Then there is a file named "<target>/<file>"
And the file "<target>/<file>" contains the default configuration

Examples:
    | target                 | file             |
    | somedir                | ybr-config.yml   |
    | parent/child           | ybr.yml          |
    | somedir/subdir/workdir | foobarconfigfile |