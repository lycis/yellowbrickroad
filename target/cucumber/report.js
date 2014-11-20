$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("update.feature");
formatter.feature({
  "line": 1,
  "name": "Update local project",
  "description": "Updating the local project will check if the local libraries as defined \r\nin the config file are up to date and present. If not they will be fetched\r\nfrom the server.",
  "id": "update-local-project",
  "keyword": "Feature"
});
});