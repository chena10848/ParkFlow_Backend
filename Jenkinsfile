pipeline {
    agent any

    environment {
        IMAGE_NAME = "parkflow-backend"
        DOCKER_TAG = "latest"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo '⚙️ 開始建置並執行單元測試...'
                sh 'chmod +x gradlew'
                sh './gradlew clean test build --info'
            }
        }

        stage('Test Report') {
            steps {
                echo '🧪 匯入測試報告...'
                // Jenkins 預設會讀取這個路徑下的 JUnit XML
                junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 建立 Docker 映像檔...'
                sh 'docker build -t $IMAGE_NAME:$DOCKER_TAG .'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 正在啟動容器...'
                sh '''
                    docker stop $IMAGE_NAME || true
                    docker rm $IMAGE_NAME || true
                    docker run -d --name $IMAGE_NAME -p 8082:8080 $IMAGE_NAME:$DOCKER_TAG
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline 執行成功！'
        }
        failure {
            echo '❌ Pipeline 執行失敗，請檢查錯誤訊息'
        }
    }
}
