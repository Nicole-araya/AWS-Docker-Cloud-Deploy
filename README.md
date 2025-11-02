# 🚀 Microservice

This mini-project demonstrates the full lifecycle of a mission-critical microservice, from development and containerization to consistent deployment and operational monitoring in the cloud.

## 🎯 Key Skills Demonstrated

- Automated deployment using Docker on AWS EC2.
- Creation of a **Java 17 (Spring Boot)** microservice with health and data endpoints. 
- SSH access, package management (`dnf`), service control (`systemctl`). 
- Multi-stage Docker build using optimized **Amazon Corretto Alpine** images. 
- Configuration of an **AWS Security Group** (Firewall) to control SSH (22) and HTTP (80) traffic. 
- Setup of a **CloudWatch Alarm** for system performance management.

## 🏗️ 1. Architecture and Tech Stack

The architecture uses containerization to ensure portability and relies on core AWS Free Tier services for infrastructure.

| Component | Technology / Tool |
| :--- | :--- |
| **Microservice** | Java 17 (Spring Boot) |
| **Container Image**| Docker (`amazoncorretto:17.0.17-alpine3.22`) |
| **Image Registry** | Docker Hub (`nicoleabing/microserviciotest-icon:latest`) |
| **Cloud Host** | AWS EC2 (t3.micro - Free Tier) |
| **OS** | Amazon Linux 2023 - AMI |
| **Monitoring** | AWS CloudWatch |

## 📦 2. Containerization and Build Process

The `Dockerfile` employs a multi-stage approach.

### Dockerfile

```dockerfile
# STAGE 1: BUILD (Compiling the JAR)
FROM amazoncorretto:17.0.17-alpine3.22 AS builder 
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw clean package -DskipTests

# STAGE 2: RUN 
FROM amazoncorretto:17.0.17-alpine3.22 
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```
### Local Commands
1.  **Build the Image (from project root):**
    ```bash
    docker build -t nicoleabing/microserviciotest-icon:latest .
    ```
2.  **Run Local Test:**
   It starts a container using the image and maps the ports for local testing.
    ```bash
    docker run -d -p 8080:8080 -e HOSTNAME="Docker-Desktop-Local" --name microservicio-test nicoleabing/microserviciotest-icon:latest
    ```
4.  **Push to Docker Hub:**
    ```bash
    docker push nicoleabing/microserviciotest-icon:latest
    ```

## ☁️ 3. AWS EC2 Deployment 

### Network Setup

An AWS Security Group was configured to enforce the necessary firewall rules, only exposing required ports:

| Traffic | Protocol | Port | Source | Purpose |
| :--- | :--- | :--- | :--- | :--- |
| **SSH** | TCP | 22 | My IP | Secure administrative access. |
| **HTTP** | TCP | 80 | `0.0.0.0/0` | Public access to the microservice. |

### Deployment Script

The deployment was executed via SSH on the **Amazon Linux 2023 - AMI** instance.

The session was initiated using the following command syntax. Be sure to replace `[KEY_PATH/KEY_FILENAME].pem` with the full, correct path to your SSH key file and `[PUBLIC_IP]` with the EC2 instance's public IP address:
```bash
ssh -i [KEY_PATH/KEY_FILENAME].pem ec2-user@[PUBLIC_IP]
```

Once connected:

```bash
# Install Docker dependencies
sudo dnf install docker -y
sudo systemctl start docker

# Deploy the Container
# Mapea host port 80 to container port 8080.
sudo docker run -d -p 80:8080 \
-e HOSTNAME="$(hostname) - AWS-EC2" \
--name microservicio-cloud nicoleabing/microserviciotest-icon:latest
```

## ✅ 4. Verification and Monitoring

### Portability Test

The `/data` endpoint proves that the application successfully reads its execution environment (the container's `HOSTNAME`), confirming that the code is agnostic to the host and that containerization is successful.

| Environment | Test URL | Verified Outcome |
| :--- | :--- | :--- |
| **Local** (Docker Desktop) | `http://localhost:8080/data` | `Microservicio corriendo en el Host: Docker-Desktop-Local` |
| **Cloud** (EC2 Public IP) | `http://[PUBLIC_IP]/data` | `Microservicio corriendo en el Host: [EC2_ID] - AWS-EC2` |

![Docker Data Endpoint](https://github.com/Nicole-araya/AWS-Docker-Cloud-Deploy/blob/main/images/docker_desktop.png?raw=true)
![AWS EC2 Data Endpoint](https://github.com/Nicole-araya/AWS-Docker-Cloud-Deploy/blob/main/images/aws_ec2.png?raw=true)

The `/health` endpoint was verified in both environments with success (`{"status": "UP"}`).

### CloudWatch Monitoring

A critical operational requirement was met by setting up a monitoring alarm:

* **Metric:** `CPUUtilization`
* **Threshold:** Average **Greater than (>) 80%** over a 5-minute period.
* **Action:** Triggers an email notification via an SNS Topic, simulating an automated alert to the operations team.
![CloudWatch_Metrics](https://github.com/Nicole-araya/AWS-Docker-Cloud-Deploy/blob/main/images/alarm_metrics.png?raw=true)

## ⚠️ 5. Project Cleanup (Cost Management)

All AWS resources were **terminated** immediately after testing to ensure no residual charges were incurred.
