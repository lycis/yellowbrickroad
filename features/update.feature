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
    - some.package.here
    serverAddress: mockedserver:80
    targetPath: .
    """
