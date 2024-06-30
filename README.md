# Ultimate Library Group - Library Inventory Management System

Welcome to the Ultimate Library Group Library Inventory Management System. Follow these instructions to set up the project on your local machine for development and testing purposes.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java JDK
- Visual Studio Code (VS Code)
- Java Extension for VS Code
- PostgreSQL

### Windows Setup

#### 1. Install Java JDK
Download and install the Java JDK from [Oracle JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or [AdoptOpenJDK](https://adoptopenjdk.net/).
- Set the environment variable `JAVA_HOME` to your JDK installation path.
- Update the `Path` environment variable to include `%JAVA_HOME%\bin`.

#### 2. Install Visual Studio Code
Download and install VS Code from [Visual Studio Code](https://code.visualstudio.com/).

#### 3. Install Java Extension Pack for VS Code
Open VS Code, go to the Extensions view by clicking on the square icon on the sidebar or pressing `Ctrl+Shift+X`, and search for "Java Extension Pack". Install it.

#### 4. Install PostgreSQL
Download and install PostgreSQL from [PostgreSQL Official Site](https://www.postgresql.org/download/windows/).
- During installation, set the password for the PostgreSQL database superuser (postgres) to `password` and the port to the default `5432`.
- Remember to select the option to install pgAdmin.

#### 5. Setup Database
- Open pgAdmin, connect to your PostgreSQL instance.
- Create a new database named `LIM`.
- Create a user named `root` with the password `password` and grant all privileges on the `LIM` database to this user.

### macOS Setup

#### 1. Install Java JDK
- Install Homebrew if not already installed: `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"`
- Install Java using Homebrew: `brew install openjdk@11`
- Add Java to your path in your `.bash_profile` or `.zshrc`: `export PATH="/usr/local/opt/openjdk@11/bin:$PATH"`

#### 2. Install Visual Studio Code
- Download and install from [Visual Studio Code](https://code.visualstudio.com/).

#### 3. Install Java Extension Pack for VS Code
- Same as Windows instructions.

#### 4. Install PostgreSQL
- Install PostgreSQL using Homebrew: `brew install postgresql`
- Start PostgreSQL service: `brew services start postgresql`

#### 5. Setup Database
- Use the terminal or pgAdmin to create a new database and user:  
```bash
  psql postgres -c "CREATE DATABASE LIM;"
  psql postgres -c "CREATE USER root WITH ENCRYPTED PASSWORD 'password';"
  psql postgres -c "GRANT ALL PRIVILEGES ON DATABASE LIM TO root;"
```

## Running the Project
After setting up the development environment, you can import the project into VS Code, navigate to the project directory in the terminal, and click run:	