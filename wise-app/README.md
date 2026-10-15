# WISE App (Movie Rent)

## Standard Prerequisites

* **Docker**
* **Docker Compose**

## Other Prerequisites (Dev Mode)

* **Java JDK 21 (Eclipse Temurin is recommended)**
* **Apache Maven 3.9+**

## Getting Started

### 1. Environment Configuration

Create an `.env` file in the root folder and, starting from the `env.example` template, customizes these variables (it is not mandatory to change all the values):

- **PG_DB:** PostgreSQL database name
- **PG_PORT:** Host port mapping to the database container port
- **PG_USER:** PostgreSQL user 
- **PG_PASSWORD:** PostgreSQL password
- **APP_DB_USER:** The app user connecting to the database
- **APP_DB_PASS:** The app user's password for connecting to the database
- **TZ:** System time zone
- **PGTZ:** Time zone for PostgreSQL
- **APP_PORT:** Host port mapping to the Tomcat container port
- **APP_ARTIFACT_ID / APP_VERSION:** Used by `docker-compose.dev.yml` to get your locally generated `.war` file (see `pom.xml`)
- **JWT_SECRET:** Secret key for JWT generation (at least 32 characters recommended)
- **UPLOAD_PATH:** Absolute path inside the container for storing uploaded images (recommended: `/usr/local/tomcat/uploads`)
- **MAIL_USER:** SMTP username (email address)
- **MAIL_PASS:** This should not be your email account password. If you use Gmail, you can generate an "App Password" from your Google Account security settings (in the 2-Step Verification section)
- **MAIL_HOST:** SMTP server hostname (e.g., `smtp.gmail.com`)
- **MAIL_PORT:** SMTP server port (e.g., `587`)

### Some ready-made data to try

  ```env
  JWT_SECRET=62d78754ae1493f93eb94d6bc730fb93064a06cc080b192a44b7f3036b43d1a3
  ```

### Pre-existing Demo Accounts

If you don't want to register, you can log in using one of these predefined accounts:

| Role | Email | Password |
| :--- | :--- | :--- |
| Admin | admin@gmail.com | 12345678 |
| Staff | staff@gmail.com | 12345678 |
| Standard User | prova@gmail.com | 12345678 |

### Note
  
  To simulate the payment, here is a test credit card number:
  ```
  4242 4242 4242 4242
  ```

### 2. Initial Movie Poster Images (Optional)

By default, the system already contains some movies. To view their associated posters:

1. Go to the [latest release](../../releases/latest) of this repository and download `uploads.zip` from the release assets
2. Ensure that the `data/uploads` folder exists in the root directory (if it doesn't exist yet, simply create it)
3. Extract the contents of `uploads.zip` directly into `data/uploads/`  

**Note:** If you skip this part, the app will continue to function normally. However, existing movie entries will display placeholder or invalid images until you upload new ones. You can also perform the extraction described in step 3 after launching the app, since Docker automatically creates the `data/uploads` folder.

### 3. Running the Application

Depending on your needs, you can choose between two modes.

### Option A: Full Automated Build

Ideal if you want to run the application without installing anything (Java/Maven are not required). This mode uses a multi-stage `Dockerfile`: it compiles the source code inside a temporary container and then moves the result to a container running Tomcat.

From the root folder, choose the command that fits your needs:

- **Standard Run (Recommended):**

  ```bash
  docker compose up -d --build
  ```

- **Quick Restart:** 

  ```bash
  docker compose up -d
  ```

- **Fresh Start (Troubleshooting):**
  
  ```bash
  docker compose build --no-cache
  ```

### Option B: Local Development

Ideal for rapid and dynamic development. In this mode, you explicitly compile the .war file and mount it on the container running Tomcat. Requires a local installation of Maven and JDK.

  ```bash
  - mvn clean package
  - docker compose -f docker-compose.dev.yml up -d
  ```

### 4. Accessing the application

Once the application is up and running, you can access it at `http://localhost:8080`.

### 5. Troubleshooting

### Email Issues (TLS)

Sometimes antiviruses or firewalls try to block TLS when sending emails. If you encounter problems with email delivery, try disabling them temporarily to verify if they are causing the issue.

### Windows Only: Entrypoint Scripts Error (CRLF vs LF)

Windows uses CRLF as the end-of-line character, while Linux/Docker systems expect LF. We've included a `.gitattributes` file that should fix this. If containers with entrypoints fail to start, open your editor, select the faulty file, and change the format (bottom right) from CRLF to LF. Then save the file.

### Stop the Application

To stop and remove containers, launch:

  ```bash
  docker compose down
  ```

### Full reset

If you modify the `schema.sql` file and want to re-initialize the database from scratch, you must remove the persistent volumes (this will delete the data stored in the database!).

  ```bash
  docker compose down -v
  ```
