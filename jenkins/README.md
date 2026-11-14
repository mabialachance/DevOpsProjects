# DevOpsProjects

Hands-on DevOps projects covering build automation, CI/CD, and container registries. 
All three projects run in a single Jenkins job (`java-maven-build`) and build on each other.

## Projects

1. [Java Maven App with Jenkins CI](#1-java-maven-app-with-jenkins-ci)
2. [Docker Image Push to Docker Hub](#2-docker-image-push-to-docker-hub)
3. [Docker Image Push to a Private Nexus Registry](#3-docker-image-push-to-a-private-nexus-registry)

## Overall flow

```
GitHub (jenkins-jobs) → Jenkins → mvn package → JAR → docker build
                                                         ├─→ Docker Hub (public)
                                                         └─→ Nexus (private)
```

## 1. Java Maven App with Jenkins CI

A Java 17 application built with Maven and packaged as a Spring Boot executable JAR. Jenkins builds it from the `jenkins-jobs` branch.

**Tech stack:** Java 17, Maven 3.9, JUnit 4 (Surefire 3.5.4), Spring Boot Maven Plugin 3.5.5, Jenkins

| Stage | Command | What it does |
|-------|---------|--------------|
| Test | `mvn test` | Compiles the sources and runs `AppTest` |
| Package | `mvn package` | Builds the JAR and repackages it with its dependencies |

**Output:** `target/java-maven-app-1.1.8.jar`

**Run locally**
```bash commands used
git clone https://github.com/mabialachance/DevOpsProjects.git
cd DevOpsProjects && git checkout jenkins-jobs
mvn test && mvn package
java -jar target/java-maven-app-1.1.8.jar
```

## 2. Jenkins build and pushed Docker Image to Docker Hub

After Maven packages the JAR, a Jenkins shell step builds a Docker image and pushes it to Docker Hub.

**Tech stack:** Docker, Amazon Corretto 17 (Alpine), Docker Hub

**Dockerfile summary**
- Base image: `amazoncorretto:17-alpine-jdk`
- Copies `target/java-maven-app-*.jar` into `/usr/app/`
- Sets `/usr/app` as the working directory

**Jenkins shell step**
```bash command used in Jenkins
docker build -t mabialachance/my-demo-app:jma-1.1 .
echo $PASSWORD | docker login -u mabialachance --password-stdin
docker push mabialachance/my-demo-app:jma-1.1
```

**Result:** `mabialachance/my-demo-app:jma-1.1` on Docker Hub
# Result when checking docker images
```` root@95d1aae8df18:/# docker images
i Info →   U  In Use
IMAGE                                  ID             DISK USAGE   CONTENT SIZE   EXTRA
192.168.4.74:8083/my-app:1.0           02473b719693        292MB         79.3MB
192.168.4.74:8083/my-demo-ap:jma-1.1   89f9de57ff0e        500MB          175MB
java-maven-app:1.0                     74bb4c546a6d        500MB          175MB
jenkins/jenkins:lts-jdk21              74401de6a6f9        712MB          244MB
jenkins/jenkins:lts-jdk25              415ab90d4e3b        997MB          314MB    U
mabialachance/my-demo-app:jma-1.0      a854c39f329b        500MB          175MB
mabialachance/my-demo-app:jma-1.1      1f4527ba2bd7        500MB          175MB
mongo-express:latest                   1b23d7976f02        287MB         59.8MB
mongo:latest                           bdcd178c7ef0       1.54GB          398MB
````

```bash 
docker pull mabialachance/my-demo-app:jma-1.1
```
---
## 3. Docker Image Push to a Private Nexus Registry

The same image is tagged with a private Nexus Docker registry address and pushed there instead of Docker Hub. This keeps images inside the private network.

**Tech stack:** Sonatype Nexus Repository (Docker hosted repository), Docker, Jenkins

**Registry:** `192.168.*.*:8083`

**Jenkins shell step**
```bash commands used in Jenkins
docker build -t 192.168.*.*:8083/my-demo-ap:jma-1.1 .
echo $PASSWORD | docker login -u nexus_admin --password-stdin 192.168.*.*:8083
docker push 192.168.*.*:8083/my-demo-ap:jma-1.1
```
**Result:** `192.168.*.*:8083/my-demo-ap:jma-1.1`

**What differs from Docker Hub**
- The registry host and port are part of the image name, so Docker knows where to push.
- `docker login` takes the registry address as an argument.
- Nexus credentials are stored in Jenkins and injected at build time, so the password is masked in the log (`****`).

**Prerequisites**
- A Docker (hosted) repository in Nexus with an HTTP connector on port `8083`.
- A Nexus user with push permission for that repository.
- If Nexus uses HTTP, add the registry to `insecure-registries` in the Docker daemon config on the Jenkins host:
  ```json
  { "insecure-registries": ["192.168.*.*:8083"] } #Nexus Repository Exposed IP address
  ```

**Pull from the registry**
```bash
docker login 192.168.*.*:8083
docker pull 192.168.*.*:8083/my-demo-ap:jma-1.1
```
``Notes`` The Ip address was changed and hidden on purpose.