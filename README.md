## How to run
### Start the containers
Run `docker compose up` in the project root (so in the same directory with *compose.yaml*):
```
$ docker compose up
```

### Start the containers and rebuild images 
Run `docker compose up --build` to start the containers and rebuild images. 
Use this command during *development*, i.e., if there are *code changes*.
```
$ docker compose up --build
```

### Stop and remove containers
Run `docker-compose down` to stop and remove containers created by `up`:
```
$ docker-compose down
```

### Stop and remove containers including volumes
Run `docker compose down --volumes` to stop and remove containers including volumes (e.g. database) 
created by `up`:
```
$ docker compose down --volumes
```

### Stop and remove **all** containers
To stop and remove **all** containers, run the following commands: 
```
$ docker stop $(docker ps -a -q)
$ docker rm $(docker ps -a -q)
```



# API Documentation

## Introduction

Welcome to the API documentation for our application. This document provides details about the available endpoints, their methods, request parameters, and responses.

## Authentication

Some endpoints require authentication using a JWT (JSON Web Token) in the `Authorization` header. To authenticate, include the token in the header using the Bearer scheme:

```
Authorization: Bearer <JWT Token>
```

## Error Responses

In case of an error, the API will return a JSON response with the following structure:

```json
{
  "error": true,
  "message": "Error message here"
}
```

## Endpoints

### `/auth/login`

- Method: `POST`
- Description: Authenticates a user and generates a JWT token.
- Request Parameters:
  - `username`: The username of the user.
  - `password`: The password of the user.
- Response:
  - Returns a response containing the authentication token.

### `/api/categories`

- Method: `POST`
- Description: Creates a new category.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `name`: The name of the category.
- Response:
  - Returns the created category object.

- Method: `PUT`
- Description: Updates an existing category.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `categoryId`: The ID of the category to update.
  - `name`: The updated name of the category.
- Response:
  - Returns the updated category object.

- Method: `DELETE`
- Description: Deletes an existing category.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `categoryId`: The ID of the category to delete.
- Response:
  - Returns a success message.

### `/api/products`

- Method: `POST`
- Description: Creates a new product.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `name`: The name of the product.
  - `price`: The price of the product.
- Response:
  - Returns the created product object.

- Method: `PUT`
- Description: Updates an existing product.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `productId`: The ID of the product to update.
  - `name`: The updated name of the product.
  - `price`: The updated price of the product.
- Response:
  - Returns the updated product object.

- Method: `DELETE`
- Description: Deletes an existing product.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `productId`: The ID of the product to delete.
- Response:
  - Returns a success message.

### `/api/order-items`

- Method: `POST`
- Description: Creates a new order item.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `productId`: The ID of the product for the order item.
  - `quantity`: The quantity of the product.
- Response:
  - Returns the created order item object.

- Method: `PUT`
- Description: Updates an existing order item.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `orderItemId`: The ID of the order item to update.
  - `quantity`: The updated quantity of the product.
- Response:
  - Returns the updated order item object.

- Method: `DELETE`
- Description: Deletes an existing order item.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `orderItemId`: The ID of the order item to delete.
- Response:
  - Returns a success message.

### `/api/orders`

- Method: `POST`
- Description: Creates a new order.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `...`: Other required parameters for the order.
- Response:
  - Returns the created order object.

- Method: `PUT`
- Description: Updates an existing order.
- Authentication Required: Yes, authenticated users can access this endpoint. Only users with ADMIN authority can update the order.
- Request Parameters:
  - `orderId`: The ID of the order to update.
  - `...`: Other parameters to update in the order.
- Response:
  - Returns the updated order object.

- Method: `DELETE`
- Description: Deletes an existing order.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `orderId`: The ID of the order to delete.
- Response:
  - Returns a success message.

### `/api/users`

- Method: `POST`
- Description: Creates a new user.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `...`: User registration parameters.
- Response:
  - Returns the created user object.

- Method: `PUT`
- Description: Updates an existing user.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `userId`: The ID of the user to update.
  - `...`: Other parameters to update in the user.
- Response:
  - Returns the updated user object.

- Method: `DELETE`
- Description: Deletes an existing user.
- Authentication Required: Yes, only users with ADMIN authority can access this endpoint.
- Request Parameters:
  - `userId`: The ID of the user to delete.
- Response:
  - Returns a success message.

### `/api/reviews`

- Method: `POST`
- Description: Creates a new review.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `...`: Parameters for the review.
- Response:
  - Returns the created review object.

- Method: `PUT`
- Description: Updates an existing review.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `reviewId`: The ID of the review to update.
  - `...`: Other parameters to update in the review.
- Response:
  - Returns the updated review object.

- Method: `DELETE`
- Description: Deletes an existing review.
- Authentication Required: Yes, authenticated users can access this endpoint.
- Request Parameters:
  - `reviewId`: The ID of the review to delete.
- Response:
  - Returns a success message.

##
##

# Authentication Controller Documentation

The Authentication Controller handles user authentication and registration for the online store application. It provides endpoints for user login and registration. This documentation provides an overview of the Authentication Controller and its available endpoints.

## Endpoints

### Login User

- URL: `/auth/login`
- Method: POST
- Description: Authenticates a user and generates a JWT token.
- Request Parameters:
  - `username`: The username of the user.
  - `password`: The password of the user.
- Constraints:
  - `username`: must be unique
  - `email`: must be unique
- Response: Returns a response containing the authentication token.
- Example Response:
```json
{
    "error": false,
    "message": "Logged In",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Register User

- URL: `/auth/register`
- Method: POST
- Description: Registers a new user.
- Request Parameters:
  - `first_name`: The first name of the user.
  - `last_name`: The last name of the user.
  - `username`: The username of the user.
  - `email`: The email address of the user.
  - `password`: The password of the user.
- Response: Returns a response containing the registered user details and authentication token.
- Example Response:
```json
{
    "error": false,
    "username": "john123",
    "message": "Account created successfully",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Error Handling

The Authentication Controller handles exceptions and provides appropriate error responses. If the login credentials are invalid or the user is disabled, a 401 (UNAUTHORIZED) status is returned with an error message. Other exceptions are handled by returning a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The Authentication Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the Authentication Controller endpoints.

## Dependencies

The Authentication Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.
- JWT Token Util: Provides utilities for JWT token generation and validation.
- BCryptPasswordEncoder: Provides password encoding functionality using the BCrypt hashing algorithm.

##
##

# Category Controller Documentation

The Category Controller is responsible for handling HTTP requests related to categories in the online store application. It provides endpoints to perform CRUD (Create, Read, Update, Delete) operations on categories and retrieve category information. This documentation provides an overview of the Category Controller and its available endpoints.

## Endpoints

### Get All Categories

- URL: `/api/categories`
- Method: GET
- Description: Retrieves all categories.
- Response: Returns a list of CategoryDto objects representing the categories.
- Example Response:
```json
[
    {
        "id": 1,
        "name": "Electronics",
        "productIds": [1, 2, 3]
    },
    {
        "id": 2,
        "name": "Clothing",
        "productIds": [4, 5, 6]
    }
]
```

### Get Category by ID

- URL: `/api/categories/{categoryId}`
- Method: GET
- Description: Retrieves a category by its ID.
- Path Variable:
    - `categoryId`: The ID of the category to retrieve.
- Response: Returns a CategoryDto object representing the category.
- Example Response:
```json
{
    "id": 1,
    "name": "Electronics",
    "productIds": [1, 2, 3]
}
```

### Add Category

- URL: `/api/categories`
- Method: POST
- Description: Adds a new category.
- Request Body: Category object representing the category to be added.
- Response: Returns the created CategoryDto object representing the added category.
- Example Request Body:
```json
{
    "name": "Home Appliances"
}
```
- Example Response:
```json
{
    "id": 3,
    "name": "Home Appliances",
    "productIds": []
}
```

### Update Category

- URL: `/api/categories/{categoryId}`
- Method: PUT
- Description: Updates an existing category.
- Path Variable:
    - `categoryId`: The ID of the category to update.
- Request Body: Category object representing the updated category.
- Response: Returns the updated CategoryDto object representing the updated category.
- Example Request Body:
```json
{
    "name": "Books"
}
```
- Example Response:
```json
{
    "id": 2,
    "name": "Books",
    "productIds": [4, 5, 6]
}
```

### Delete Category

- URL: `/api/categories/{categoryId}`
- Method: DELETE
- Description: Deletes a category by its ID.
- Path Variable:
    - `categoryId`: The ID of the category to delete.
- Response: Returns a success message indicating the deletion.
- Example Response:
```text
Category deleted
```

### Populate Categories

- URL: `/api/categories/populate`
- Method: POST
- Description: Populates categories for testing purposes.
- Response: Returns a success message indicating the successful population of categories.
- Example Response:
```text
Categories populated
```

## Error Handling

The Category Controller handles several exceptions and provides appropriate error responses. The following exceptions are handled:

- `InvalidOrderStatusException`: Returns a 400 (BAD_REQUEST) status with an error message.
- `ProductNotFoundException`: Returns a 404 (NOT_FOUND) status with an error message.
- `OrderNotFoundException`: Returns a 404 (NOT_FOUND) status with an error message.
- `OrderItemNotFoundException`: Returns a 404 (NOT_FOUND) status with an error message.
- `ReviewNotFoundException`: Returns a 404 (NOT_FOUND) status with an error message.
- `ValidatorException`: Returns a 404 (NOT_FOUND) status with an error message.
- Other exceptions: Returns a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The Category Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the Category Controller endpoints.

## Dependencies

The Category Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.
- Faker: Used to generate random category names for testing purposes.

##
##

# Product Controller Documentation

The Product Controller is responsible for handling HTTP requests related to products in the online store application. It provides endpoints to perform CRUD (Create, Read, Update, Delete) operations on products, retrieve product information, manage product categories, and populate the database with fake data. This documentation provides an overview of the Product Controller and its available endpoints.

## Endpoints

### Get All Products

- URL: `/api/products`
- Method: GET
- Description: Retrieves all products.
- Response: Returns a list of ProductDto objects representing the products.
- Example Response:
```json
[
    {
        "id": 1,
        "name": "Product 1",
        "description": "Description of product 1",
        "price": 19.99,
        "unitsInStock": 10,
        "discountPercentage": 0.0,
        "createTime": "2023-07-11T10:30:00Z",
        "updateTime": "2023-07-11T11:15:00Z",
        "orderItems": [1, 2],
        "categoryId": 2,
        "categoryName": "Category 2",
        "userId": 1,
        "reviewsId": [1, 2],
        "imagesName": ["image1.jpg", "image2.jpg"]
    },
    {
        "id": 2,
        "name": "Product 2",
        "description": "Description of product 2",
        "price": 29.99,
        "unitsInStock": 15,
        "discountPercentage": 0.1,
        "createTime": "2023-07-10T09:45:00Z",
        "updateTime": "2023-07-11T08:20:00Z",
        "orderItems": [3],
        "categoryId": 1,
        "categoryName": "Category 1",
        "userId": 2,
        "reviewsId": [3],
        "imagesName": ["image3.jpg"]
    }
]
```

### Get Product by ID

- URL: `/api/products/{productId}`
- Method: GET
- Description: Retrieves a product by its ID.
- Path Variable:
  - `productId`: The ID of the product to retrieve.
- Response: Returns a ProductDto object representing the product.
- Example Response:
```json
{
    "id": 1,
    "name": "Product 1",
    "description": "Description of product 1",
    "price": 19.99,
    "unitsInStock": 10,
    "discountPercentage": 0.0,
    "createTime": "2023-07-11T10:30:00Z",
    "updateTime": "2023-07-11T11:15:00Z",
    "orderItems": [1, 2],
    "categoryId": 2,
    "categoryName": "Category 2",
    "userId": 1,
    "reviewsId": [1, 2],
    "imagesName": ["image1.jpg", "image2.jpg"]
}
```

### Get All Products with Category (Lazy)

- URL: `/api/products/lazy`
- Method: GET
- Description: Retrieves all products with their associated categories using lazy loading.
- Response: Returns a list of ProductDtoWithCategory objects representing the products with their associated categories.
- Example Response:
```json
[
    {
        "id": 1,
        "name": "Product 1",
        "description": "Description of product 1",
        "price": 19.99,
        "unitsInStock": 10,
        "discountPercentage": 0.0,
        "categoryId": 2,
        "categoryName": "Category 2"
    },
    {
        "id": 2,
        "name": "Product 2",
        "description": "Description of product 2",
        "price": 29.99,
        "unitsInStock": 15,
        "discountPercentage": 0.1,
        "categoryId": 1,
        "categoryName": "Category 1"
    }
]
```

### Add Product

- URL: `/api/products/category/{categoryId}`
- Method: POST
- Description: Adds a new product with the specified category.
- Path Variable:
  - `categoryId`: The ID of the category associated with the product.
- Request Body: ProductRequestDto object representing the product to be added, including an optional image file.
- Response: Returns the created ProductDto object representing the added product.
- Example Request Body:
```json
{
    "name": "Product 3",
    "description": "Description of product 3",
    "price": 39.99,
    "unitsInStock": 5,
    "discountPercentage": 0.2,
    "image": "<image file>"
}
```
- Example Response:
```json
{
    "id": 3,
    "name": "Product 3",
    "description": "Description of product 3",
    "price": 39.99,
    "unitsInStock": 5,
    "discountPercentage": 0.2,
    "createTime": "2023-07-11T14:45:00Z",
    "updateTime": "2023-07-11T14:45:00Z",
    "orderItems": [],
    "categoryId": 2,
    "categoryName": "Category 2",
    "userId": 1,
    "reviewsId": [],
    "imagesName": ["image3.jpg"]
}
```

### Update Product

- URL: `/api/products/{productId}`
- Method: PUT
- Description: Updates an existing product.
- Path Variable:
  - `productId`: The ID of the product to update.
- Request Body: ProductRequestDto object representing the updated product.
- Response: Returns the updated ProductDto object representing the updated product.
- Example Request Body:
```json
{
    "name": "Updated Product",
    "description": "Updated description",
    "price": 49.99,
    "unitsInStock": 8,
    "discountPercentage": 0.15
}
```
- Example Response:
```json
{
    "id": 1,
    "name": "Updated Product",
    "description": "Updated description",
    "price": 49.99,
    "unitsInStock": 8,
    "discountPercentage": 0.15,
    "createTime": "2023-07-11T10:30:00Z",
    "updateTime": "2023-07-11T15:20:00Z",
    "orderItems": [1, 2],
    "categoryId": 2,
    "categoryName": "Category 2",
    "userId": 1,
    "reviewsId": [1, 2],
    "imagesName": ["image1.jpg", "image2.jpg"]
}
```

### Delete Product by ID

- URL: `/api/products/{productId}`
- Method: DELETE
- Description: Deletes a product by its ID.
- Path Variable:
  - `productId`: The ID of the product to delete.
- Response: Returns a success message indicating the

deletion.
- Example Response:
```text
Product with id 1 deleted
```

### Get Paginated Products

- URL: `/api/products/display`
- Method: GET
- Description: Retrieves paginated products.
- Request Parameters:
  - `pageNumber` (optional): The page number to retrieve (default: 0).
  - `pageSize` (optional): The number of products per page (default: 10).
  - `sortBy` (optional): The field to sort the products by (default: "id").
- Response: Returns a PaginatedProductResponse object containing the paginated products, total number of items, and number of pages.
- Example Response:
```json
{
    "products": [
        {
            "id": 1,
            "name": "Product 1",
            "description": "Description of product 1",
            "price": 19.99,
            "unitsInStock": 10,
            "discountPercentage": 0.0,
            "createTime": "2023-07-11T10:30:00Z",
            "updateTime": "2023-07-11T11:15:00Z",
            "orderItems": [1, 2],
            "categoryId": 2,
            "categoryName": "Category 2",
            "userId": 1,
            "reviewsId": [1, 2],
            "imagesName": ["image1.jpg", "image2.jpg"]
        },
        {
            "id": 2,
            "name": "Product 2",
            "description": "Description of product 2",
            "price": 29.99,
            "unitsInStock": 15,
            "discountPercentage": 0.1,
            "createTime": "2023-07-10T09:45:00Z",
            "updateTime": "2023-07-11T08:20:00Z",
            "orderItems": [3],
            "categoryId": 1,
            "categoryName": "Category 1",
            "userId": 2,
            "reviewsId": [3],
            "imagesName": ["image3.jpg"]
        }
    ],
    "numberOfItems": 2,
    "numberOfPages": 1
}
```

### Populate Database with Fake Data

- URL: `/api/products/populate`
- Method: POST
- Description: Populates the database with fake product data for testing purposes.
- Response: Returns a success message indicating the successful population of the database.
- Example Response:
```text
Database populated with fake data
```

### Get Products by Category

- URL: `/api/products/category`
- Method: GET
- Description: Retrieves products belonging to a specific category.
- Request Parameters:
  - `categoryId`: The ID of the category to filter products.
- Response: Returns a list of ProductDto objects representing the products in the specified category.
- Example Response:
```json
[
    {
        "id": 1,
        "name": "Product 1",
        "description": "Description of product 1",
        "price": 19.99,
        "unitsInStock": 10,
        "discountPercentage": 0.0,
        "createTime": "2023-07-11T10:30:00Z",
        "updateTime": "2023-07-11T11:15:00Z",
        "orderItems": [1, 2],
        "categoryId": 2,
        "categoryName": "Category 2",
        "userId": 1,
        "reviewsId": [1, 2],
        "imagesName": ["image1.jpg", "image2.jpg"]
    },
    {
        "id": 3,
        "name": "Product 3",
        "description": "Description of product 3",
        "price": 29.99,
        "unitsInStock": 15,
        "discountPercentage": 0.1,
        "createTime": "2023-07-10T09:45:00Z",
        "updateTime": "2023-07-11T08:20:00Z",
        "orderItems": [3],
        "categoryId": 2,
        "categoryName": "Category 2",
        "userId": 2,
        "reviewsId": [3],
        "imagesName": ["image3.jpg"]
    }
]
```

## Error Handling

The Product Controller handles several exceptions and provides appropriate error responses. The following exceptions are handled:

- `ProductNotFoundException`: Returns a 404 (NOT_FOUND) status with an error message.
- Other exceptions: Returns a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The Product Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the Product Controller endpoints.

## Dependencies

The Product Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.
- Lombok: Simplifies the creation of DTOs and entities with automatic generation of getters, setters, and other boilerplate code.
- Jakarta Validation API: Provides validation constraints for request DTOs.
- Faker: Used to generate fake product data for testing purposes.
- Hibernate Annotations: Provides annotations for defining JPA entities and relationships.

##
##


# Review Controller Documentation

The Review Controller is responsible for handling HTTP requests related to product reviews in the online store application. It provides endpoints to retrieve reviews, add a new review, update an existing review, and delete a review. This documentation provides an overview of the Review Controller and its available endpoints.

## Endpoints

### Get All Reviews

- URL: `/api/reviews`
- Method: GET
- Description: Retrieves all reviews.
- Response: Returns a list of ReviewDto objects representing the reviews.
- Example Response:
```json
[
    {
        "id": 1,
        "rating": 4.5,
        "title": "Great product",
        "comment": "I love this product!",
        "date": "2022-01-01T10:30:00",
        "productId": 1,
        "userId": 1
    },
    {
        "id": 2,
        "rating": 3.0,
        "title": "Average product",
        "comment": "It's okay, nothing special.",
        "date": "2022-02-01T14:45:00",
        "productId": 2,
        "userId": 2
    }
]
```

### Get Review by ID

- URL: `/api/reviews/{reviewId}`
- Method: GET
- Description: Retrieves a review by its ID.
- Path Variable:
  - `reviewId`: The ID of the review to retrieve.
- Response: Returns a ReviewDto object representing the review.
- Example Response:
```json
{
    "id": 1,
    "rating": 4.5,
    "title": "Great product",
    "comment": "I love this product!",
    "date": "2022-01-01T10:30:00",
    "productId": 1,
    "userId": 1
}
```

### Save Review

- URL: `/api/reviews/save/{productId}`
- Method: POST
- Description: Saves a new review for the specified product.
- Path Variable:
  - `productId`: The ID of the product for which the review is being added.
- Request Body: ReviewRequestDto object representing the review to be saved.
- Response: Returns the created ReviewDto object representing the added review.
- Example Request Body:
```json
{
    "rating": 4.5,
    "title": "Great product",
    "comment": "I love this product!"
}
```
- Example Response:
```json
{
    "id": 1,
    "rating": 4.5,
    "title": "Great product",
    "comment": "I love this product!",
    "date": "2022-01-01T10:30:00",
    "productId": 1,
    "userId": 1
}
```

### Update Review

- URL: `/api/reviews/{reviewId}`
- Method: PUT
- Description: Updates an existing review.
- Path Variable:
  - `reviewId`: The ID of the review to update.
- Request Body: ReviewRequestDto object representing the updated review.
- Response: Returns the updated ReviewDto object representing the updated review.
- Example Request Body:
```json
{
    "rating": 4.0,
    "title": "Updated review",
    "comment": "This product is even better now!"
}
```
- Example Response:
```json
{
    "id": 1,
    "rating": 4.0,
    "title": "Updated review",
    "comment": "This product is even better now!",
    "date": "2022-01-01T10:30:00",
    "productId": 1,
    "userId": 1
}
```

### Delete Review by ID

- URL: `/api/reviews/{reviewId}`
- Method: DELETE
- Description: Deletes a review by its ID.
- Path Variable:
  - `reviewId`: The ID of the review to delete.
- Response: Returns a success message indicating the deletion.
- Example Response:
```text
Review with id = 1 deleted
```

## Error Handling

The Review Controller handles exceptions and provides appropriate error responses. If a review is not found, a 404 (NOT_FOUND) status is returned with an error message. Other exceptions are handled by returning a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The Review Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the Review Controller endpoints.

## Dependencies

The Review Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.

## 
## 

# Order Item Controller Documentation

The Order Item Controller handles HTTP requests related to order items in the online store application. It provides endpoints to retrieve order items, add an order item, delete an order item, and modify the quantity of an order item. This documentation provides an overview of the Order Item Controller and its available endpoints.

## Endpoints

### Get All Order Items

- URL: `/api/orderItems`
- Method: GET
- Description: Retrieves all order items.
- Response: Returns a list of OrderItemDto objects representing the order items.
- Example Response:
```json
[
    {
        "id": 1,
        "quantity": 2,
        "productId": 1,
        "orderId": 1
    },
    {
        "id": 2,
        "quantity": 1,
        "productId": 2,
        "orderId": 1
    }
]
```
### Get Order Item by ID

- URL: `/api/orderItems/{orderItemId}`
- Method: GET
- Description: Retrieves an order item by its ID.
- Path Variable:
  - `orderItemId`: The ID of the order item to retrieve.
- Response: Returns an OrderItemDto object representing the order item.
- Example Response:
```json
{
    "id": 1,
    "quantity": 2,
    "productId": 1,
    "orderId": 1
}
```

### Get All Order Items with Product

- URL: `/api/orderItems/lazy`
- Method: GET
- Description: Retrieves all order items with product details.
- Response: Returns a list of OrderItemWithProductDto objects representing the order items with product details.
- Example Response:
```json
[
    {
        "id": 1,
        "quantity": 2,
        "productId": 1
    },
    {
        "id": 2,
        "quantity": 1,
        "productId": 2
    }
]
```

### Add Order Item

- URL: `/api/orderItems/{productId}`
- Method: POST
- Description: Adds an order item for the specified product.
- Path Variable:
  - `productId`: The ID of the product for which the order item is being added.
- Request Parameters:
  - `quantity`: The quantity of the order item.
- Response: Returns the created OrderItemDto object representing the added order item.
- Example Response:
```json
{
    "id": 1,
    "quantity": 2,
    "productId": 1,
    "orderId": 1
}
```

### Delete Order Item by ID

- URL: `/api/orderItems/{id}`
- Method: DELETE
- Description: Deletes an order item by its ID.
- Path Variable:
  - `id`: The ID of the order item to delete.
- Response: Returns a success message indicating the deletion.
- Example Response:
```text
OrderItem deleted
```

### Modify Order Item Quantity

- URL: `/api/orderItems/{id}/quantity`
- Method: PUT
- Description: Modifies the quantity of an order item.
- Path Variable:
  - `id`: The ID of the order item to modify.
- Request Parameter:
  - `quantity`: The new quantity of the order item.
- Response: Returns a success message indicating the modification.
- Example Response:
```text
Quantity modified
```

## Error Handling

The Order Item Controller handles exceptions and provides appropriate error responses. If an order item is not found, a 404 (NOT_FOUND) status is returned with an error message. Other exceptions are handled by returning a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The Order Item Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the Order Item Controller endpoints.

## Dependencies

The Order Item Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.

## 
## 

# Order Controller Documentation

The Order Controller handles HTTP requests related to orders in the online store application. It provides endpoints to retrieve orders, add items to an order, delete an order, update the status of an order, and retrieve orders for a specific user. This documentation provides an overview of the Order Controller and its available endpoints.

## Endpoints

### Get All Orders

- URL: `/api/orders`
- Method: GET
- Description: Retrieves all orders.
- Response: Returns a list of OrderDto objects representing the orders.
- Example Response:
```json
[
    {
        "id": 1,
        "deliveryPrice": 10.0,
        "startDate": "2023-07-07",
        "deliveryDate": "2023-07-10",
        "status": "PLACED",
        "userId": 1,
        "orderItems": [1, 2]
    },
    {
        "id": 2,
        "deliveryPrice": 12.0,
        "startDate": "2023-07-06",
        "deliveryDate": "2023-07-09",
        "status": "SHIPPED",
        "userId": 2,
        "orderItems": [3, 4]
    }
]
```

### Add Item to Order

- URL: `/api/orders/{orderItemId}`
- Method: POST
- Description: Adds an item to the order with the specified order item ID.
- Path Variable:
  - `orderItemId`: The ID of the order item to add to the order.
- Response: Returns the updated OrderDto object representing the order.
- Example Response:
```json
{
    "id": 1,
    "deliveryPrice": 10.0,
    "startDate": "2023-07-07",
    "deliveryDate": "2023-07-10",
    "status": "PLACED",
    "userId": 1,
    "orderItems": [1, 2, 5]
}
```

### Delete Order by ID

- URL: `/api/orders/{id}`
- Method: DELETE
- Description: Deletes an order by its ID.
- Path Variable:
  - `id`: The ID of the order to delete.
- Response: Returns a success message indicating the deletion.
- Example Response:
```text
Order deleted
```

### Update Order Status

- URL: `/api/orders/{orderId}`
- Method: PUT
- Description: Updates the status of an order.
- Path Variable:
  - `orderId`: The ID of the order to update.
- Request Parameter:
  - `status`: The new status of the order.
- Response: Returns the updated OrderDto object representing the order.
- Example Response:
```json
{
    "id": 1,
    "deliveryPrice": 10.0,
    "startDate": "2023-07-07",
    "deliveryDate": "2023-07-10",
    "status": "SHIPPED",
    "userId": 1,
    "orderItems": [1, 2, 5]
}
```

### Get All Orders by User

- URL: `/api/orders/me`
- Method: GET
- Description: Retrieves all orders for the currently logged-in user.
- Response: Returns a list of OrderDto objects representing the orders.
- Example Response:
```json
[
    {
        "id": 1,
        "deliveryPrice": 10.0,
        "startDate": "2023-07-07",
        "deliveryDate": "2023-07-10",
        "status": "PLACED",
        "userId": 1,
        "orderItems": [1, 2]
    },
    {
        "id": 3,
        "deliveryPrice": 15.0,
        "startDate": "2023-07-06",
        "deliveryDate": "2023-07-09",
        "status": "DELIVERED",
        "userId": 1,
        "orderItems": [6, 7, 8]
    }
]
```

## Error Handling

The Order Controller handles exceptions and provides appropriate error responses. If an order is not found, a 404 (NOT_FOUND) status is returned with an error message. Other exceptions are handled by returning a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The Order Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the Order Controller endpoints.

## Dependencies

The Order Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.


## 
## 

# AppUser Controller Documentation

The AppUser Controller handles HTTP requests related to app users in the online store application. It provides endpoints to retrieve app users, delete an app user by username, and update an app user's password. This documentation provides an overview of the AppUser Controller and its available endpoints.

## Endpoints

### Get All Users

- URL: `/api/users`
- Method: GET
- Description: Retrieves all app users.
- Response: Returns a list of AppUserDto objects representing the app users.
- Example Response:
```json
[
    {
        "id": 1,
        "username": "john123",
        "email": "john@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "role": "USER",
        "orders": [1, 2],
        "products": [3, 4]
    },
    {
        "id": 2,
        "username": "jane456",
        "email": "jane@example.com",
        "firstName": "Jane",
        "lastName": "Smith",
        "role": "USER",
        "orders": [5, 6],
        "products": [7, 8]
    }
]
```

### Delete User by Username

- URL: `/api/users/{username}`
- Method: DELETE
- Description: Deletes an app user by their username.
- Path Variable:
  - `username`: The username of the app user to delete.
- Response: Returns a success message indicating the deletion.
- Example Response:
```text
User deleted
```

### Update User by Username

- URL: `/api/users/{username}`
- Method: PUT
- Description: Updates the password of an app user.
- Path Variable:
  - `username`: The username of the app user to update.
- Request Parameter:
  - `password`: The new password for the app user.
- Response: Returns a success message indicating the update.
- Example Response:
```text
User updated
```

## Error Handling

The AppUser Controller handles exceptions and provides appropriate error responses. If an app user is not found, a 404 (NOT_FOUND) status is returned with an error message. Other exceptions are handled by returning a 500 (INTERNAL_SERVER_ERROR) status with the corresponding exception message.

## Cross-Origin Resource Sharing (CORS)

The AppUser Controller allows cross-origin requests through the `@CrossOrigin("*")` annotation. This allows clients from different origins to access the AppUser Controller endpoints.

## Dependencies

The AppUser Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.

## 
## 

# Image Controller Documentation

The Image Controller is responsible for handling HTTP requests related to images in the online store application. It provides endpoints to upload, download, and retrieve images. This documentation provides an overview of the Image Controller and its available endpoints.

## Endpoints

### Upload Image

- URL: `/api/images/upload/{productId}`
- Method: POST
- Description: Uploads an image for a product with the specified product ID.
- Path Variable:
  - `productId`: The ID of the product associated with the image.
- Request Parameter:
  - `imageFile`: The image file to be uploaded.
- Response: Returns a ResponseEntity containing the updated ProductDto object representing the product with the uploaded image.
- Example Response:
```json
{
    "id": 1,
    "name": "Product 1",
    "description": "Description of product 1",
    "price": 19.99,
    "unitsInStock": 10,
    "discountPercentage": 0.0,
    "orderItems": [1, 2],
    "categoryId": 2,
    "userId": 1,
    "reviewsId": [1, 2],
    "imagesName": ["image1.jpg", "image2.jpg"]
}
```

### Download Image by Name

- URL: `/api/images/download`
- Method: GET
- Description: Downloads an image by its name.
- Request Parameter:
  - `name`: The name of the image to download.
- Response: Returns the image file with the specified name.
- Example Response: Returns the image file.

### Get Image by ID

- URL: `/api/images/getById`
- Method: GET
- Description: Retrieves an image by its ID.
- Request Parameter:
  - `id`: The ID of the image to retrieve.
- Response: Returns an ImageModelDto object representing the image.
- Example Response:
```json
{
    "id": 1,
    "name": "image1.jpg",
    "type": "image/jpeg",
    "picByte": "..."
}
```

### Get Image by Name

- URL: `/api/images/getByName`
- Method: GET
- Description: Retrieves an image by its name.
- Request Parameter:
  - `name`: The name of the image to retrieve.
- Response: Returns an ImageModelDto object representing the image.
- Example Response:
```json
{
    "id": 1,
    "name": "image1.jpg",
    "type": "image/jpeg",
    "picByte": "..."
}
```

## Dependencies

The Image Controller relies on the following dependencies:
- Spring Framework: Provides the necessary infrastructure for building the web application.
- Lombok: Simplifies the creation of DTOs and entities with automatic generation of getters, setters, and other boilerplate code.
- ImageConverter: Converts between ImageModel and ImageModelDto objects.
- ProductConverter: Converts between Product and ProductDto objects.
- ImageService: Provides the business logic for handling images.