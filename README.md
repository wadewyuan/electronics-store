# About
This is the repo for the technical assessment, requirements defined in [Bullish_technical_assessment.pdf](Bullish_technical_assessment.pdf)

# Environment
- Java 17
- Gradle 8.1.1
- SpringBoot 3.1.1
- H2 Database (in-memory storage)

# Getting Started
1. Make sure Java 17 is installed
2. Clone this repo to local
3. Open command line in the repo's directory, run `./gradlew bootRun`
4. The application will be running on `8080` port
5. To run test, `./gradlew test`

# Major Endpoints
## Products
### Create Product
Request:

| Method | URL       | Headers                        | Body                                | Params |
|--------|-----------|--------------------------------|-------------------------------------|--------|
| POST   | /products | Content-Type: application/json | {"name": "Product A, "price": 9.99} |        |

Response: `201 Created`
```json
{
	"id": 1,
	"name": "Product A",
	"price": 10.0,
	"createdTimestamp": "2023-06-29 18:25:55"
}
```
### View Product
Request:

| Method | URL                   | Headers | Body | Params |
|--------|-----------------------|---------|------|--------|
| GET    | /products/{productId} |         |      |        |

Response: `200 OK`
```json
{
	"id": 1,
	"name": "Product A",
	"price": 10.00,
	"createdTimestamp": "2023-06-29 18:25:55"
}
```
### Remove Product
Request:

| Method | URL                   | Headers | Body | Params |
|--------|-----------------------|---------|------|--------|
| DELETE | /products/{productId} |         |      |        |

Response: `200 OK`
## Discounts
### Add Simple Discount
Request:

| Method | URL                                           | Headers | Body | Params                          |
|--------|-----------------------------------------------|---------|------|---------------------------------|
| POST   | /discounts/simpleDiscount/product/{productId} |         |      | requiredQuantity, percentageOff |

_Note: `requiredQuantity` is the number of products to buy in order to get the discount, `percentageOff` is the discount percentage. This endpoint can be used to input a simple discount to buy `x` and the other `y` get `z` percent off._

Response:
```json
{
	"id": 1,
	"requiredProduct": {
		"id": 1,
		"name": "Product A",
		"price": 10.0,
		"createdTimestamp": "2023-06-29 18:25:47"
	},
	"requiredQuantity": 1,
	"targetProduct": {
		"id": 1,
		"name": "Product A",
		"price": 10.0,
		"createdTimestamp": "2023-06-29 18:25:47"
	},
	"discountType": "PERCENTAGE",
	"discountValue": 30.0,
	"enabled": true
}
```
## Shopping Cart
### New Shopping Cart
Request:

| Method | URL             | Headers                        | Body          | Params |
|--------|-----------------|--------------------------------|---------------|--------|
| POST   | /shopping-carts | Content-Type: application/json | {"items": []} |        |

Response: `201 Created`
```json
{
	"id": 1,
	"items": []
}
```
### Add Product to Cart
Request:

| Method | URL                                              | Headers | Body | Params   |
|--------|--------------------------------------------------|---------|------|----------|
| PUT    | /shopping-carts/{shoppingCartId}/add/{productId} |         |      | quantity |

Response: `200 OK`
```json
{
	"id": 1,
	"items": [
		{
			"id": 1,
			"product": {
				"id": 1,
				"name": "Product A",
				"price": 10.00,
				"createdTimestamp": "2023-06-30 13:47:47"
			},
			"quantity": 2
		}
	]
}
```
### Remove Product from Cart
Request:

| Method | URL                                                 | Headers | Body | Params   |
|--------|-----------------------------------------------------|---------|------|----------|
| PUT    | /shopping-carts/{shoppingCartId}/remove/{productId} |         |      | quantity |

Response:
```json
{
	"id": 1,
	"items": [
		{
			"id": 1,
			"product": {
				"id": 1,
				"name": "Product A",
				"price": 10.00,
				"createdTimestamp": "2023-06-30 13:47:47"
			},
			"quantity": 1
		}
	]
}
```
## Calculate Total Amount of Cart
Request:

| Method | URL                                        | Headers | Body | Params |
|--------|--------------------------------------------|---------|------|--------|
| GET    | /shopping-carts/{shoppingCartId}/calculate |         |      |        |

Response:
```json
{
	"id": 1,
	"items": [
		{
			"id": 1,
			"product": {
				"id": 2,
				"name": "Product B",
				"price": 30.0,
				"createdTimestamp": "2023-06-29 18:25:55"
			},
			"quantity": 1
		},
		{
			"id": 2,
			"product": {
				"id": 1,
				"name": "Product A",
				"price": 10.0,
				"createdTimestamp": "2023-06-29 18:25:47"
			},
			"quantity": 1
		}
	],
	"discountAmount": 8.0,
	"totalAmount": 40.0,
	"finalAmount": 32.0
}
```
_Note: In this implementation, multiple discount deals can be enabled for one product, the calculation logic would only take the discount with maximum amount into count._