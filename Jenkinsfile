pipeline {
    agent any

    environment {
        IMAGE_NAME = "parkflow-backend"
        CONTAINER_NAME = "parkflow-backend"
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew build -x test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME .'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker rm -f $CONTAINER_NAME || true'
                sh 'docker run -d -p 8081:8080 --name $CONTAINER_NAME $IMAGE_NAME'
            }
        }
    }
}
