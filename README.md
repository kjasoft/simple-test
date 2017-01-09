# simple-test
Minimalistic web application for testing students

## How it works
Applications starts a web server, then opens its page in the browser. On that page student enters his/her name, answers questions and receives a mark.
List of questions is loaded from configuration file data.json
Student results are written to results.log
Application can be run with maven command:
`mvn clean compile exec:java -Dexec.mainClass=com.kja.questions.App`
