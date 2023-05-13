pipeline {
    agent any

    tools {
        maven "M3"
    }

    stages {
        stage('Build backend') {
            steps {
                dir('api') {
                    sh "./mvnw clean compile"
                }
            }
        }

        stage('Test backend') {
            steps {
                dir('api') {
                    sh "./mvnw test"
                }
            }
        }

        stage('Build frontend') { 
            steps {
                dir('api') {
                    sh "npm run build"
                }
            }
        }

        stage('Run') {
            steps {
                sh "docker-compose up -d"
            }
        }
    }
}
