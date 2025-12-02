spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

  # SMTP Configuration (Reads from Environment Variables)
  mail:
    host: ${SMTP_HOST}
    port: ${SMTP_PORT}
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: 8080

springdoc:
  swagger-ui:
    path: /swagger-ui.html

app:
{{JwtConfig}}

  admin:
    email: ${ADMIN_EMAIL}
    password: ${ADMIN_PASSWORD}

  mail:
    from: ${MAIL_FROM}
    verification-base-url: http://localhost:8080