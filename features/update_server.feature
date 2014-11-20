Feature: Updating a repository
  As administrator I want to automatically update the repository manifest so
  that I do not have to do it manually and thereby save a lot of time.

Scenario: Updating without a repository
  Given the current directory is empty
  When I update the server
  Then the error "target folder does not contain a yellow brick road server" is displayed
  Then the output shows 
    """
    Run 'prepare-server' to initialise a server structure.

    """