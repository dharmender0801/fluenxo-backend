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
                     sh 'sshpass -e -v scp -o StrictHostKeyChecking=no  target/lms-auth-service.war jenkins@pms-dev.apollosupplychain.com:/opt/docker/pms-dev/tomcat/webapps/'
                }
            }
        }
    }
}