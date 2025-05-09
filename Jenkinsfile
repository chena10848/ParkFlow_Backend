pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew build -x test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t parkflow-backend .'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy logic goes here...'
            }
        }
    }
}
