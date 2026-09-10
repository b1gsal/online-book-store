# Online books store backend API

## This is a backend api for online book store that covers full books purchase life cycle from looking catalog and book's filtering to managing shopping cart and placing orders

## Technologies and Tools:
### Java 17, SpringBoot 4, Spring Security, JWT, Spring Data JPA, Liquibase, MySQl, Docker, Docker Compose, MapStruct, Swagger, TestContainers, JUnit 5, Mockito, MockMvc, Maven, Lombok 

## Features and API Endpoints

## 1. [AuthenticationController.java](src/main/java/mate/academy/onlinebookstore/controller/AuthenticationController.java)

### Registration and login by getting JWT token

![img_1.png](images/user.png)

## 2. [BookController.java](src/main/java/mate/academy/onlinebookstore/controller/BookController.java) & [CategoryController.java](src/main/java/mate/academy/onlinebookstore/controller/CategoryController.java)

### CRUD operation for book and category. Pagination, sorting and dynamic search by JPA Specification. Only administration can create change and remove.

![img.png](images/book.png)
![img.png](images/category.png)

## 3. [ShoppingCartController.java](src/main/java/mate/academy/onlinebookstore/controller/ShoppingCartController.java)

## Add books to shopping cart, updating quantity, remove books from shopping cart.

![img.png](images/ShoppingCart.png)

## 4. [OrderController.java](src/main/java/mate/academy/onlinebookstore/controller/OrderController.java)

## Place order by items in shopping cart, order's history, change order's status by administration

![img.png](images/order.png)

## How to run
**Copy [.env.template](.env.template) to your own .env and fill empty line**
### Run by docker: 
**Use in terminal ```docker-compose up --build```, this starts two containers (mysql, app), it uses [docker-compose.yml](docker-compose.yml)**
### Run by [OnlineBookStoreApplication.java](src/main/java/mate/academy/onlinebookstore/OnlineBookStoreApplication.java)
**When you start app by this method only mysql container was up, and it uses local code, it uses [docker-compose-dev.yml](docker-compose-dev.yml)**

## Challenges and Solutions

### For stateless authentication use JWT token. For testing use testcontainers instead of real or h2 db. Use two docker compose files. [docker-compose-dev.yml](docker-compose-dev.yml) for local developing, it uses only mysql container and starts when app starts by [OnlineBookStoreApplication.java](src/main/java/mate/academy/onlinebookstore/OnlineBookStoreApplication.java). [docker-compose.yml](docker-compose.yml) has mysql and application container, starts by ```docker-compose up```.

## Loom Video Walkthrough: [Watch the demo](https://www.loom.com/share/387bc14f39444753b6b054bee26ed957)
