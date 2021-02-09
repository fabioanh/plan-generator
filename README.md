# plan-generator
REST Web service application to generate a loan payment plan.

## Invocation
The web service exposes the controller resource `/generate` for the resource `/plans`. This endpoint supports a post request giving the basic parameters requierd for the loan.

To see a full documentation please follow the [documentation link](api-guide.html)

The application is a spring-boot application. In order to easily run it with maven please use the command `mvn spring-boot:run`

The generated artifact is a self contained application that can be run with a command similar to the next one `java -jar target/plan-generator-0.0.1-SNAPSHOT.jar` (after generating the artifact `mvn clean package`). This would deploy the application in the port `8080`
