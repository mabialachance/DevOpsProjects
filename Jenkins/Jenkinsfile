@Library('jenkins-shared-library') _

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('increment version') {
            steps {
                dir('Jenkins') {
                    script {
                        echo 'incrementing app version...'
                        sh 'mvn build-helper:parse-version versions:set \
                            -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                            versions:commit'
                        def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                        def version = matcher[0][1]
                        env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                    }
                }
            }
        }
        stage('build app') {
            steps {
                dir('Jenkins') {
                    echo 'building the application...'
                    sh 'mvn clean package'
                }
            }
        }
        stage('build image') {
            steps {
                dir('Jenkins') {
                    script {
                        buildImage("mabialachance/my-demo-app:${IMAGE_NAME}")
                        dockerLogin()
                        dockerPush("mabialachance/my-demo-app:${IMAGE_NAME}")
                    }
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    echo 'deploying docker image...'
                }
            }
        }
        stage('commit version update') {
            steps {
                script {
                    sh 'git config --global user.email "jenkins@example.com"'
                    sh 'git config --global user.name "jenkins"'

                    sh 'git status'
                    sh 'git branch'

                    sh 'git add Jenkins/pom.xml'
                    sh 'git commit -m "ci: version bump"'

                    withCredentials([gitUsernamePassword(
                        credentialsId: 'github-credentials',
                        gitToolName: 'Default'
                    )]) {
                        sh 'git fetch origin jenkins-jobs'
                        sh 'git rebase FETCH_HEAD'
                        sh 'git push origin HEAD:refs/heads/jenkins-jobs'
                    }
                }
            }
        }
    }
}
