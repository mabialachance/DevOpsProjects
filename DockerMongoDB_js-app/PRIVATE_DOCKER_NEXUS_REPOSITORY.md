# Steps commands to used to successfully complete the project:

# Create a Private Docker Registry Using Nexus Repository Manager

## Project Overview

This project demonstrates how to create a private Docker registry using Sonatype Nexus Repository Manager.
It covers creating a Docker Hosted repository, configuring authentication and permissions, connecting Docker Desktop on Windows, and pushing Docker images to Nexus.

## Prerequisites

- Ubuntu Linux server
- Sonatype Nexus Repository Manager
- Docker installed on Windows
- Docker Desktop running
- Nexus administrator account
- Network access to the Nexus server

## Architecture

```text
Windows Desktop
      |
      | Docker Build & Tag
      |
      v
  Docker Image
      |
      | Docker Login
      |
      v
Nexus Repository Manager
      |
      | HTTP :8083
      |
      v
 Docker Hosted Repository
      |
      v
 Private Docker Registry
```

## 1. Create a Private Docker Registry

### Step 1: Access Nexus

Open your browser and navigate to:
```
http://192.168.x.x:8082
```
Log in using your Nexus administrator credentials.

Navigate to:

**Server Administration and Configuration → Repositories**

### Step 2: Create a Docker Hosted Repository

1. Click **Create repository**.
2. Select **docker (hosted)**.
3. Configure the repository:

| Name | docker-hosted |
| Online | Enabled |
| Blob Store | mystorage |
| Deployment Policy | Allow redeploy (optional) |

4. Select a Blob Store under Storage.
5. Save the configuration.

### Step 3: Configure the Docker Connector

1. Open the `docker-hosted` repository.
2. Locate the HTTP connector configuration.
3. Enable HTTP.
4. Set the port to: ```8083 not the same as nexus port```


5. Save the configuration.

The Docker registry URL is: ``` http://192.168.x.x:8083 ```

## 2. Configure Nexus Security

### Step 4: Create a Docker Role

Navigate to:

**Security → Roles → Create role**

Configure:

| Role ID | nx-docker |
| Name | nx-docker |
| Description | Docker Hosted repository access |

Add the following privilege:```nx-repository-view-docker-docker-hosted-*``` Apply and save the role.

### Step 5: Assign the Role to a User

1. Navigate to **Security → Users**.
2. Select an existing Nexus user.
3. Edit the user.
4. Assign the `nx-docker` role.
5. Save the changes.

The user now has the assigned Docker repository permissions.

### Step 6: Activate Docker Bearer Token Realm

1. Navigate to **Security → Realms**.
2. Find `Docker Bearer Token Realm`.
3. Move it to the Active Realms section.
4. Save the configuration.

This enables Docker-compatible authentication with Nexus.

## 3. Configure the Linux Firewall

### Step 7: Open Port 8083

On the Linux Nexus server, execute: ```bash sudo ufw allow 8083/tcp ``` or allow port 8083 if using Digital Ocean interface

Check the firewall:```bash sudo ufw status```

Verify that port 8083 is allowed.

Notes: ```For production, restrict access to trusted client IP addresses and configure HTTPS.```

## 4. Configure Docker Desktop on Windows

### Step 8: Configure an Insecure Registry

Since this example uses HTTP, Docker Desktop must be configured to accept the private registry.

1. Open Docker Desktop.
2. Navigate to **Settings → Docker Engine**.
3. Add the following configuration to the existing JSON:

```json
{
  "insecure-registries": [
    "192.168.x.x:8083"
  ]
}
```

4. Replace the IP address with your Nexus server IP.
5. Click **Apply & Restart**.

**Note:** For production environments, HTTPS with a trusted TLS certificate is recommended.

## 5. Connect Docker Desktop to Nexus

### Step 9: Test the Connection

Open PowerShell or Git Bash:``` bash curl http://192.168.x.x:8083/v2/ ```

### Step 10: Authenticate to Nexus

Run: ```bash docker login 192.168.x.x:8083 ```

Enter your Nexus username and password.

Expected result:

```text
Login Succeeded
```
Docker Desktop is now authenticated to the private Nexus registry.

## 6. Build and Tag a Docker Image

### Step 11: Build an Image

Navigate to your project directory containing the Dockerfile:

```bash
cd ~/DevOps/Project
```

Build the Docker image:

```bash
docker build -t my-app:1.0 .
```

Verify the image: ```docker images ```

### Step 12: Tag the Image

Tag the image with the Nexus registry address: ```bash docker tag my-app:1.0 192.168.x.x:8083/my-app:1.0 ```

Verify: ```docker images```

The image should now have two tags:
```text
my-app:1.0
192.168.x.x:8083/my-app:1.0 
```

## 7. Push Docker Image to Nexus

### Step 13: Docker Login

Authenticate to the Nexus registry:
``` docker login 192.168.x.x:8083 ```

### Step 14: Push the Image

Upload the image:
``` docker push 192.168.x.x:8083/my-app:1.0 ```

Docker will upload the image layers to the Nexus Docker Hosted repository.

## 8. Verify the Docker Image in Nexus

### Step 15: Verify the Image

1. Open the Nexus Web UI:

``` http://192.168.x.x:8082 ```

2. Navigate to **Browse**.
3. Select `docker-hosted`.
4. Locate the uploaded image.
5. Verify the image name and tag.

Expected repository structure:

```text
docker-hosted/
    |
    └── my-app/
          |
          └── 1.0
```

### Step 16: Pull the Image

Test downloading the image from Nexus:

```docker pull 192.168.x.x:8083/my-app:1.0 ```

Verify:

``` docker images ```

## 9. Docker Commands Reference

| Build | `docker build -t my-app:1.0 .` |
| Tag | `docker tag my-app:1.0 192.168.x.x:8083/my-app:1.0` |
| Login | `docker login 192.168.x.x:8083` |
| Push | `docker push 192.168.x.x:8083/my-app:1.0` |
| Pull | `docker pull 192.168.x.x:8083/my-app:1.0` |
| List images | `docker images` |
| Logout | `docker logout 192.168.x.x:8083` |

## 10. Troubleshooting methods used:

| Error | Solution |
|---|---|
| Connection refused | Verify Nexus is running and port 8083 is enabled. |
| Unauthorized | Verify credentials and Docker Bearer Token Realm. |
| Access denied | Check Nexus user permissions and assigned role. |
| HTTP response to HTTPS client | Configure Docker Desktop insecure registries. |
| Port unavailable | Check whether another application uses port 8083. |
| Image push denied | Verify repository deployment permissions. |
| Repository not found | Verify the repository name and Docker connector. |

Useful Linux troubleshooting commands used:

```
sudo ss -ltnp | grep 8083

sudo ufw status

sudo systemctl status nexus
```

## 11. Project Summary

In this project, I successfully configured a private Docker registry using Sonatype Nexus Repository Manager.

The following tasks were completed:

1. Created a Docker Hosted repository.
2. Configured the Docker connector on port 8083.
3. Created a dedicated Nexus Docker role.
4. Assigned repository permissions to a Nexus user.
5. Activated Docker Bearer Token Realm.
6. Configured the Linux firewall.
7. Connected Windows Docker Desktop to Nexus.
8. Built and tagged a Docker image.
9. Authenticated to the private registry.
10. Pushed and pulled Docker images.

## Technologies Used

- Docker
- Docker Desktop
- Sonatype Nexus Repository Manager
- Ubuntu Linux
- Windows
- UFW Firewall
- Docker CLI

## Author

**GitHub:** https://github.com/mabialachance

**Project:** Private Docker Registry Using Nexus Repository Manager
