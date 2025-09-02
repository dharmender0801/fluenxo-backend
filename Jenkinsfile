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
                     sh 'mv target/lms-auth-service.war /opt/docker/pms-dev/tomcat/webapps/'
                }
            }
        }
    }
}
