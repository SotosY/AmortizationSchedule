# Amortization Schedule

This repository provides RESTful APIs to create and interact with an
amortisation schedule for an asset being financed.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java 17.0.8.
- Gradle v7.6.3/Gradle Wrapper
- Git installed on your machine

## Getting Started

To get started with this project, you can clone the repository using the following command:

Clone the project

```bash
  git clone https://github.com/SotosY/AmortizationSchedule.git
```

Go to the project directory

```bash
  cd AmortizationSchedule
```

## Building the Project

To build the project, navigate to the project directory and run the following command:

Build project

```bash
  ./gradlew build
```

## Running Tests

To run the tests, use the following command:

```bash
  ./gradlew test
```
This will execute all the tests in the project.You can see more details about the tests here `AmortizationSchedule/build/reports/tests/test/index.html`

## Running the Application

To run the Spring Boot application, use the following command:

```bash
  ./gradlew bootRun
```

This will start the application locally. You can access it at `http://localhost:8080`.

## API documentation

You can find the API documentation at `http://localhost:8080/swagger-ui/index.html`.

### Create Amortization Schedule

- **Endpoint**: `/api/amortization-schedule/create`
- **Method**: POST
- **Description**: Creates a new amortization schedule based on the provided loan details.
- **Request Body**: JSON object representing loan details
    - `principal` (double): Principal amount of the loan.
    - `annualInterestRate` (double): Annual interest rate for the loan.
    - `termYears` (int): Term of the loan in years.
    - `startDate` (string): Start date of the loan in the format "YYYY-MM-DD".
- **Response**: Amortization schedule object

### Get All Amortization Schedules

- **Endpoint**: `/api/amortization-schedule/all`
- **Method**: GET
- **Description**: Retrieves all existing amortization schedules.
- **Response**: List of amortization schedule details objects

### Get Amortization Schedule by ID

- **Endpoint**: `/api/amortization-schedule/{id}`
- **Method**: GET
- **Description**: Retrieves a specific amortization schedule by its ID.
- **Path Parameter**:
    - `id` (Long): ID of the amortization schedule
- **Response**: Amortization schedule object if found, otherwise 404 Not Found

## H2 Database

You can access the database at `http://localhost:8080/h2-console`, using the credentials:

`Username:` `Admin`

`Password:` `password`