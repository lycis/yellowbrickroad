Feature: Prepare a web server for hosting a repository
  As administrator I want to be able to prepare an existing web server for 
  hosting a repository so that developers can use it for their projects.

Scenario: Prepare a server in the current directory
Given the current directory is empty
When I prepare a server in the current directory
Then there is a directory named "repository"
Then there is a directory named "repository/com"
Then there is a directory named "repository/org"
Then there is a file named "manifest.yml"
Then the file named "manifest.yml" contains the default manifest
Then there is a file named "index.html"
Then the file named "index.html" contains the default index page

Scenario: Prepare in a different directory
Given a directory called "workdir" was created
And the directory "workdir" is empty
When I prepare a server in directory "workdir"
Then there is a directory named "workdir/repository"
Then there is a directory named "workdir/repository/com"
Then there is a directory named "workdir/repository/org"
Then there is a file named "workdir/manifest.yml"
Then the file named "workdir/manifest.yml" contains the default manifest
Then there is a file named "workdir/index.html"
Then the file named "workdir/index.html" contains the default index page

Scenario: Preparing a server in a non-existing target
When I prepare a server in directory "doesnotexist"
Then the error "target does not exist" is displayed