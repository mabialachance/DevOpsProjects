##### build the project

Technology Stack
Java version 25 – Application development.
Gradle – Build automation and dependency management using IntelliJ.
Created Sonatype Nexus Repository – Artifact management remote and locally using port # 
Git & GitHub – Source code management to manage and share project
PowerShell and git bash– Command-line environment to build and configure application

# Build a Java application that will be stored in Nexus Repository

Maven_project/
│
├── Dockerfile
├── README.md
├── build.gradle
├── gradle.properties
├── settings.gradle
│
└── src/
└── main/
├── java/
│   └── com/
│       └── example/
│           └── App.java
│
└── resources/
├── application.properties
└── logback.xml

Commands used to build the application.

# To build the application I used the following commands:
    gradle build 
# To publish the application to Nexus Repository use:
    gradle publish



