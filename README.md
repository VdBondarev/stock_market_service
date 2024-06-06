# Hi there!!! STOCK MARKET SERVICE is welcoming you!

# STOCK MARKET SERVICE API README

## Testing

- **API has both unit and integration tests.**

## Project structure

This Spring Boot application follows the most common structure with such **main layers** as:
- repository (for working with database).
- service (for business logic implementation).
- controller (for accepting clients' requests and getting responses to them).

Also, it has other **important layers** such as:
- mapper (for converting models for different purposes).
- exception (GlobalExceptionHandler for getting proper messages about errors).
- dto (for managing sensitive info about models and better representation of it).
- config.
- security (for implementing Spring Security).

## Setup Instructions

To set up and run the project locally, follow these steps:

1. Clone the repository.
2. Ensure you have Java 11 installed.
3. Ensure you have Maven installed.
4. Ensure you have Docker up and running.
5. Create the database configuration in the `.env` file. ([see an example in this file](.envSample))
6. Build the project using Maven: `mvn clean package` (it will create required jar-archive).
7. Build the image using Docker: `docker-compose build`.
8. Run the application using Docker: `docker-compose up` (to test, send requests to port pointed in your .env file as SPRING_LOCAL_PORT).


## Example of requests and set up in video format
[You can see an example of requests and how to run the application by following this link](https://www.loom.com/share/129250c6b6c14b8985656582dd91724a?sid=2f8c0ef2-9334-4e59-acc3-687a42c0cae9)
