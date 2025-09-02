pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
               script{
                    echo 'Pulling...'
                    sh 'mvn clean install -DskipTests'
               }
            }
        }
        stage('Deploy') {
            steps {
                script{
                     sh 'mv target/backend-service.war /opt/tomcat/webapps/'
                }
            }
        }
    }
}
