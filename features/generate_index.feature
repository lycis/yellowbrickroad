Feature: Generate index file from rules
  As an administrator I am able to create an index file based on a set of rules.

Background:
  Given the file "junit.jar" contains
    """
    foo
    """
  And the file "foo.jar" contains
    """
    bar
    """
  And the file "bar.jar" contains
    """
    foo
    """
  And the file "c.so" contains
    """
    a c++ shared library
    """
  And the file "c_on-windows.dll" contains
    """
    a c++ dll
    """
#currently disabled
#Scenario: Include all files
#  Given the file "index_rules" contains
#    """
#    + .*
#    """
#  When I generate an index based on rules
#  Then no error is displayed
#  Then the index contains junit.jar
#  And the index contains foo.jar
#  And the index contains bar.jar
#  And the index contains c.so
#  And the index contains c_on-windows.dll