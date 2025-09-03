FROM openjdk:21
COPY target/register-form.jar register-form.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","register-form.jar"]
