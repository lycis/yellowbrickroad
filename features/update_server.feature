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

Scenario: Update with freshly initialised repository
Given the current directory contains a prepared server
When I update the server
Then no error is displayed
Then the manifest looks like
"""
!server-manifest
name: yellow-brick-road
admin: admin@example.com
repoStruct: 
   com: 
      nodeInformation: 
         name: com
   org: 
      nodeInformation: 
         name: org
   nodeInformation: 
      name: repository
type: simple
"""

Scenario: Update with available descriptions
Given the current directory contains a prepared server
Given the file "repository/com/description" contains
"""
commercial libraries
"""
Given the file "repository/org/description" contains
"""
free and open source libraries
"""
When I update the server
Then the manifest looks like
"""
!server-manifest
name: yellow-brick-road
admin: admin@example.com
repoStruct: 
   com: 
      nodeInformation: 
         name: com
         description: commercial libraries
   org: 
      nodeInformation: 
         name: org
         description: free and open source libraries
   nodeInformation: 
      name: repository
type: simple
"""

Scenario: Overwriting descriptions
Given the current directory contains a complex repository
And the file "repository/com/description" contains 
"""
overwritten com description
"""
And the file "repository/org/description" contains
"""
overwritten org description
"""
When I update the server
Then the repository entry .org has description
"""
overwritten org description
"""
And the repository entry .com has description
"""
overwritten com description
"""