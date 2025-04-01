# Swift Data API

## 📌 About project
Swift Data API is an application for managing SWIFT codes, allowing users to retrieve, add, and delete SWIFT codes stored in a database. The system also includes functionality to parse and store 
SWIFT codes from a CSV file upon startup.


## 🚀 Setup and Installation
### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose
- PostgreSQL

### 1. Cloning the Repository
```sh
git clone https://github.com/mateusz-plaska/SwiftDataAPI.git
cd SwiftDataAPI
```

### 2. Configuring Environment Variables
Create a `.env` file in the root directory with the following values:
```
DB_NAME=your_db_name
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```
Look at the example configuration [example .env file](.env.example).


## 🏃 Running the application on Docker

### 1. Run
With Docker installed run the following command from the project's root directory:
```sh
docker compose up -d
```
This will:
- Build the Spring Boot application.
- Start the PostgreSQL database container.
- Run the application in a container.

### 2. Exit

To stop application run:
```sh
docker compose stop
```

You can start it again with:
```sh
docker compose start
```

Or remove application containers:
```sh
docker compose down
```

## 🏗️ Testing
Run unit and integration tests with:
```sh
mvn test
```

## ✨ Features
- Parses a CSV file with SWIFT codes and loads it into a database.
- Provides REST API endpoints for retrieving and managing SWIFT codes.
- Ensures validation of SWIFT codes before inserting them into the database.
- Automatically deletes all branch records when a headquarter SWIFT code is removed.
- Uses Docker for containerized deployment.


## 🔍 API Endpoints
### 📖 Retrieve SWIFT Code by ID
```http
GET /v1/swift-codes/{swift-code}
```

### 🌍 Retrieve SWIFT Codes by Country
```http
GET /v1/swift-codes/country/{countryISO2code}
```

### ➕ Create a New SWIFT Code
```http
POST /v1/swift-codes/
Content-Type: application/json

{
    "address": "123 Street, City",
    "bankName": "Bank Name",
    "countryISO2": "PL",
    "countryName": "Poland",
    "isHeadquarter": true,
    "swiftCode": "CODE1234XXX",
}

```

### 🗑️ Delete a SWIFT Code
```http
DELETE /v1/swift-codes/{swift-code}
```
Note: If the SWIFT code represents a headquarter, all related branches will also be deleted.
  

## 🛠️ Assumptions and business logic
- Headquarter and Branch Relationship:
  - SWIFT code must have exactly 11 characters.
  - The system determines if a SWIFT code is a headquarter by checking if the code ends with "XXX".
  - The headquarter_code is defined as the first 8 characters of the SWIFT code.
  - This means that all branch records associated with a headquarter have the same headquarter_code.
  - In addition, a headquarter is considered as one of its branches because its headquarter_code matches the first 8 characters of its SWIFT code.  

- Record deletion:
  - The deletion method in the service deletes a record with the specified SWIFT code.
  - In addition, if the record being deleted is a headquarter (headquarter), all related branches (branch) are also deleted - identification is done by the headquarter_code field.

- Data parsing:
  - Data parsing is called at application startup.
  - The parser reads the CSV file, parses the records and writes them to the database.
  - In case of parsing errors, the record is skipped and the error is logged.

- Validation:
  - In addition to the standard validation (@Valid), the application uses a custom validator that verifies the consistency of the data (e.g., whether the SWIFT code ends with “XXX” for the registered
    office and whether the countryName matches the countryISO2).

- Global Exception Handler:
  - The application uses a global exception handler that captures and formats error responses in a standardized and descriptive format (ErrorWrapper).

## 👨‍💻 Stack
- Java
- Spring Boot
- PostgreSQL
- Docker
- Maven

