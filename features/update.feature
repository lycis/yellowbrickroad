Feature: Update local project
  Updating the local project will check if the local libraries as defined 
  in the config file are up to date and present. If not they will be fetched
  from the server.
  
Scenario: No config file is available
  Given there is no config file
  When I update the project
  Then the error "no configuration available" is displayed
  