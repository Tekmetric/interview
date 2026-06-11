# Dev tools
## Spotless plugin for auto-formatting
https://github.com/diffplug/spotless
Similar to Prettier on the FE — auto-formats backend code rather than just flagging style issues like checkstyle. 
Especially useful with AI agents, which tend to be inconsistent with code style.

## Palantir formatting
https://github.com/palantir/palantir-java-format
Specific format to use for spotless. Easier to read lambda/builder code than the ubiquitous google format

## Git build hook plugin
https://github.com/rudikershaw/git-build-hook
Installs a pre-commit git hook on `mvn install` that runs `mvn spotless:apply` to auto-format staged code before each commit.

## Sonar for static analysis
sonarqube plugin for IDEs
https://plugins.jetbrains.com/plugin/7973-sonarqube-for-ide

## Springdoc OpenAPI for API browser
https://springdoc.org/
API browser at `/swagger-ui.html`.
Automatic docs only. Avoiding overly verbose swagger annotations for now.

## IDEA built in http client
Files in http-client for local testing can target any environment.
Requires ultimate license. Could add to .gitignore in a team setting if desired.

## Standard mvn and springboot plugins for build/test/run
surefire, failsafe, build-helper, spring-boot-maven-plugin

# Future enhancements
## Jacoco code coverage
## Sonarqube for remote analysis(local community edition for fun, real-world would be SonarCloud)
https://github.com/SonarSource/sonar-scanner-maven
## OpenApi spec for external clients.