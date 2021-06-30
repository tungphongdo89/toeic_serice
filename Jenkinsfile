def buildNumber = env.BUILD_NUMBER as int
if (buildNumber > 1) milestone(buildNumber - 1)
milestone(buildNumber)

pipeline {
    agent any
    //options { disableConcurrentBuilds() }
    stages {
         stage('clean') {
            steps {
                sh "chmod +x mvnw"
                sh "./mvnw clean install"
            }
        }
         stage('Build') {
            steps {
                sh "./mvnw clean package -X -Dskip.unit.tests=true -DskipTests -Dskip.integration.tests=true"
                echo "launching ./mvnw spring-boot:run "
                sh "./mvnw spring-boot:run -Dpmd.skip=true -Dcpd.skip=true -Dfindbugs.skip=true || true"
            }
            post {
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
    }
}
