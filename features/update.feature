Feature: Update local project
  As developer I can update my local project with packages from a server so I do
  not have to handle dependencies on my own.

Background:
  Given the remote server is a mock of a complete simple server
  
Scenario: No config file is available
  Given there is no config file
  When I update the project
  Then the error "no configuration available" is displayed

Scenario: Update project in current directory
  Given the file "ybr.yml" contains
    """
    !ybr-client-configuration
    packages: 
    - .org.junit
    serverAddress: mockedserver:80
    targetPath: .
    """
  When I update the project
  Then there is a file named "junit.txt"

Scenario: Update project with differing target directory
  Given the file "ybr.yml" contains
    """
    !ybr-client-configuration
    packages: 
    - .org.junit
    serverAddress: mockedserver:80
    targetPath: lib/
    """
  When I update the project
  Then there is a file named "lib/junit.txt"