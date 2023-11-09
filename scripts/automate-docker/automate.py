import os
import subprocess

# Maven clean and install
def run_maven():
    try:
        os.system('mvn clean')
        os.system('mvn install')
    except subprocess.CalledProcessError as e:
        print(f"There is an error while maven operations execution: {e}")
        exit(1)

# Create docker image
def build_docker_image():
    try:
        subprocess.run(["docker", "build", "-t", "truck-company-service:1.0.0", "."], check=True)
    except subprocess.CalledProcessError as e:
        print(f"An error occured while docker image creation: {e}")
        exit(1)

# Docker Compose Start
def start_docker_compose():
    try:
        subprocess.run(["docker-compose", "up", "-d"], check=True)
    except subprocess.CalledProcessError as e:
        print(f"An error occured while docker-compose: {e}")
        exit(1)

if __name__ == "__main__":
    # Maven operations
    run_maven()

    # Docker image creation
    build_docker_image()

    # Starting Docker Compose
    start_docker_compose()
