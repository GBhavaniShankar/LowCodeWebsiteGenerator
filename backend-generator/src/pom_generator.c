#include "pom_generator.h"
#include "file_util.h"
#include <stdio.h>

void generate_pom(const AppConfig *cfg, const char *root)
{
    char path[512];
    snprintf(path, sizeof(path), "%s/pom.xml", root);
    ensure_dir(root);

    FILE *f = fopen(path, "w");
    if (!f)
    {
        perror("pom.xml");
        return;
    }

    const char *artifact = cfg->project_name ? cfg->project_name : "demo-app";

    fprintf(f,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
            "  <modelVersion>4.0.0</modelVersion>\n"
            "\n"
            "  <groupId>com.example</groupId>\n"
            "  <artifactId>%s</artifactId>\n"
            "  <version>0.0.1-SNAPSHOT</version>\n"
            "  <name>%s</name>\n"
            "\n"
            "  <properties>\n"
            "    <java.version>17</java.version>\n"
            "    <spring-boot.version>3.3.0</spring-boot.version>\n"
            "  </properties>\n"
            "\n"
            "  <dependencyManagement>\n"
            "    <dependencies>\n"
            "      <dependency>\n"
            "        <groupId>org.springframework.boot</groupId>\n"
            "        <artifactId>spring-boot-dependencies</artifactId>\n"
            "        <version>${spring-boot.version}</version>\n"
            "        <type>pom</type>\n"
            "        <scope>import</scope>\n"
            "      </dependency>\n"
            "    </dependencies>\n"
            "  </dependencyManagement>\n"
            "\n"
            "  <dependencies>\n"
            "    <dependency>\n"
            "      <groupId>org.springframework.boot</groupId>\n"
            "      <artifactId>spring-boot-starter-web</artifactId>\n"
            "    </dependency>\n"
            "\n"
            "    <dependency>\n"
            "      <groupId>org.springframework.boot</groupId>\n"
            "      <artifactId>spring-boot-starter-security</artifactId>\n"
            "    </dependency>\n"
            "\n"
            "    <dependency>\n"
            "      <groupId>org.springframework.boot</groupId>\n"
            "      <artifactId>spring-boot-starter-data-jpa</artifactId>\n"
            "    </dependency>\n"
            "\n"
            "    <dependency>\n"
            "      <groupId>com.h2database</groupId>\n"
            "      <artifactId>h2</artifactId>\n"
            "      <scope>runtime</scope>\n"
            "    </dependency>\n"
            "\n"
            "    <!-- Swagger/OpenAPI -->\n"
            "    <dependency>\n"
            "      <groupId>org.springdoc</groupId>\n"
            "      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>\n"
            "      <version>2.5.0</version>\n"
            "    </dependency>\n",
            artifact, artifact);

    if (cfg->auth_type == AUTH_JWT)
    {
        fprintf(f,
                "\n"
                "    <!-- JWT -->\n"
                "    <dependency>\n"
                "      <groupId>io.jsonwebtoken</groupId>\n"
                "      <artifactId>jjwt-api</artifactId>\n"
                "      <version>0.11.5</version>\n"
                "    </dependency>\n"
                "    <dependency>\n"
                "      <groupId>io.jsonwebtoken</groupId>\n"
                "      <artifactId>jjwt-impl</artifactId>\n"
                "      <version>0.11.5</version>\n"
                "      <scope>runtime</scope>\n"
                "    </dependency>\n"
                "    <dependency>\n"
                "      <groupId>io.jsonwebtoken</groupId>\n"
                "      <artifactId>jjwt-jackson</artifactId>\n"
                "      <version>0.11.5</version>\n"
                "      <scope>runtime</scope>\n"
                "    </dependency>\n");
    }

    if (cfg->mail_verification)
    {
        fprintf(f,
                "\n"
                "    <!-- Mail -->\n"
                "    <dependency>\n"
                "      <groupId>org.springframework.boot</groupId>\n"
                "      <artifactId>spring-boot-starter-mail</artifactId>\n"
                "    </dependency>\n");
    }

    fprintf(f,
            "  </dependencies>\n"
            "\n"
            "  <build>\n"
            "    <plugins>\n"
            "      <!-- Java 17 -->\n"
            "      <plugin>\n"
            "        <groupId>org.apache.maven.plugins</groupId>\n"
            "        <artifactId>maven-compiler-plugin</artifactId>\n"
            "        <version>3.11.0</version>\n"
            "        <configuration>\n"
            "          <source>17</source>\n"
            "          <target>17</target>\n"
            "        </configuration>\n"
            "      </plugin>\n"
            "\n"
            "      <!-- Spring Boot plugin -->\n"
            "      <plugin>\n"
            "        <groupId>org.springframework.boot</groupId>\n"
            "        <artifactId>spring-boot-maven-plugin</artifactId>\n"
            "        <version>${spring-boot.version}</version>\n"
            "      </plugin>\n"
            "    </plugins>\n"
            "  </build>\n"
            "\n"
            "</project>\n");

    fclose(f);
}
