def buildJar() {
    echo "building the application..."
    sh 'pwd'
} 

def buildImage() {
    echo "building the docker image..."

    withCredentials([
            file(credentialsId: 'env_test_aws', variable: 'env_test_aws'),
        ]) {
            writeFile file: '.env', text: readFile(env_test_aws)
        }

    sh 'docker compose -f docker-compose.prod-build.yml build web nginx-proxy'

    def IMAGE_django_web = env.IMAGE_django_web
    def IMAGE_nginx_proxy = env.IMAGE_nginx_proxy
    withCredentials([usernamePassword(credentialsId: 'DockerHub', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {

        sh 'echo $PASSWORD | docker login -u $USERNAME --password-stdin'
        sh "docker push ${IMAGE_nginx_proxy}"
        sh "docker push ${IMAGE_django_web}"
    }
} 

def deployApp() {
    echo 'deploying the application...'

    def IMAGE_django_web = env.IMAGE_django_web
    def IMAGE_nginx_proxy = env.IMAGE_nginx_proxy
    def EC2_PUBLIC_IP = env.EC2_PUBLIC_IP

    withCredentials([
            file(credentialsId: 'env_test_aws', variable: 'env_test_aws'),
        ]) {
            writeFile file: '.env', text: readFile(env_test_aws)
        }

    def USERNAME = "ec2-user"
    def shellCmd = "bash ./app/server-cmds.sh ${IMAGE_django_web} ${IMAGE_nginx_proxy}"
    def ec2instans = "ec2-user@${EC2_PUBLIC_IP}"
    println "IP Address: $ec2instans"
    sshagent(['ec2-jekins']) {
        sh "scp -o StrictHostKeyChecking=no .env ${ec2instans}:/home/${USERNAME}/app"
        sh "scp -o StrictHostKeyChecking=no docker-compose.prod-deploy.yml ${ec2instans}:/home/${USERNAME}/app"
        sh "cd jenkins && scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2instans}:/home/${USERNAME}/app"
        sh "ssh -o StrictHostKeyChecking=no ${ec2instans} ${shellCmd}"
    }
} 

return this