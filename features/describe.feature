Feature: Describing packages
  As developer I want to be able to get a description about packages that are
  available on a server so I can query information about the promoted services.

Background:
  Given the remote server is a mocked simple server
  And the remote server returns a mocked manifest
  Given the current directory is empty
# just so the file is there
  And the default configuration was written to "ybr.yml"

Scenario: Describe a package
  When I request the description of package ".com.cpp.util.x32"
  Then no error is displayed
  And the output shows
  """
  .com.cpp.util.x32
  c++ utilities for 32bit architecture

  """

Scenario: Describe a package without using the correct package annotation (leading dot)
  When I request the description of package "com.cpp.util.x32"
  Then no error is displayed
  And the output shows
  """
  .com.cpp.util.x32
  c++ utilities for 32bit architecture

  """

Scenario: Try to get description of a non-existing package
  When I request the description of package "does.not.exist"
  Then no error is displayed
  And the output shows
  """
  .does.not.exist
  <not found>

  """