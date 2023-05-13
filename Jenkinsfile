pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'jdk-17'
        // nodejs 'node'
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

        // stage('Build frontend') { 
        //     steps {
        //         dir('api') {
        //             sh "npm run build"
        //         }
        //     }
        // }
    }
}
