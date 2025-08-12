# Modumart API Documentation

This document provides comprehensive API documentation for all modules in the Modumart e-commerce application.

## Base URL

```
http://localhost:8080
```

## Common Response Formats

### Success Response
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### Error Response  
```json
{
  "success": false,
  "error": "Error message",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

## 1. Catalog Module API

### Products Endpoints

#### GET /api/catalog/products
Get all products with optional filtering.

**Query Parameters:**
- `category` (optional): Filter by product category
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter
- `inStock` (optional): Filter by stock availability (boolean)

**Response:**
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": 99.99,
    "stock": 50,
    "category": "Electronics"
  }
]
```

#### GET /api/catalog/products/{id}
Get product by ID.

**Response:**
```json
{
  "id": 1,
  "name": "Product Name", 
  "description": "Product description",
  "price": 99.99,
  "stock": 50,
  "category": "Electronics"
}
```

#### POST /api/catalog/products
Create a new product.

**Request Body:**
```json
{
  "name": "New Product",
  "description": "Product description", 
  "price": 149.99,
  "stock": 25,
  "category": "Electronics"
}
```

#### PUT /api/catalog/products/{id}
Update an existing product.

**Request Body:** Same as POST

#### DELETE /api/catalog/products/{id}
Delete a product.

#### PUT /api/catalog/products/{id}/stock
Update product stock quantity.

**Request Body:**
```json
{
  "quantity": 100
}
```

---

## 2. Customers Module API

### Customer Endpoints

#### GET /api/customers
Get all customers.

**Response:**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "address": "123 Main St",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

#### GET /api/customers/{id}
Get customer by ID.

#### POST /api/customers
Register a new customer.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com", 
  "phone": "123-456-7890",
  "address": "123 Main St"
}
```

#### PUT /api/customers/{id}
Update customer information.

#### DELETE /api/customers/{id}
Delete a customer.

---

## 3. Orders Module API

### Order Endpoints

#### GET /api/orders
Get all orders with optional filtering.

**Query Parameters:**
- `customerId` (optional): Filter by customer
- `status` (optional): Filter by order status (PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED)
- `limit` (optional): Limit number of results (default: 10)

**Response:**
```json
[
  {
    "id": 1,
    "customerId": 1,
    "status": "CONFIRMED",
    "totalAmount": 299.98,
    "orderDate": "2024-01-01T10:00:00Z",
    "lastModified": "2024-01-01T10:30:00Z",
    "shippingAddress": "123 Main St",
    "items": [
      {
        "productId": 1,
        "productName": "Product Name",
        "quantity": 2,
        "unitPrice": 149.99
      }
    ]
  }
]
```

#### GET /api/orders/{id}
Get order by ID.

#### POST /api/orders
Create a new order.

**Request Body:**
```json
{
  "customerId": 1,
  "shippingAddress": "123 Main St",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 149.99
    }
  ]
}
```

#### PUT /api/orders/{id}/status
Update order status.

**Request Body:**
```json
{
  "status": "CONFIRMED"
}
```

#### PUT /api/orders/{id}/cancel
Cancel an order.

**Request Body:**
```json
{
  "reason": "Customer requested cancellation"
}
```

#### GET /api/orders/customer/{customerId}
Get all orders for a specific customer.

#### GET /api/orders/recent
Get recent orders (default: last 10).

---

## 4. Payments Module API

### Payment Endpoints

#### GET /api/payments
Get all payments with optional filtering.

**Query Parameters:**
- `orderId` (optional): Filter by order ID
- `status` (optional): Filter by payment status (PENDING, COMPLETED, FAILED, REFUNDED)

**Response:**
```json
[
  {
    "id": 1,
    "orderId": 1,
    "amount": 299.98,
    "method": "CREDIT_CARD",
    "status": "COMPLETED", 
    "transactionId": "txn_123456",
    "processedAt": "2024-01-01T10:30:00Z",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

#### GET /api/payments/{id}
Get payment by ID.

#### POST /api/payments
Initiate a payment.

**Request Body:**
```json
{
  "orderId": 1,
  "amount": 299.98,
  "method": "CREDIT_CARD",
  "cardDetails": {
    "cardNumber": "****-****-****-1234",
    "expiryMonth": 12,
    "expiryYear": 2025,
    "cvv": "***"
  }
}
```

#### PUT /api/payments/{id}/complete
Complete a payment.

#### PUT /api/payments/{id}/fail
Mark payment as failed.

**Request Body:**
```json
{
  "reason": "Insufficient funds"
}
```

#### POST /api/payments/{id}/refund
Refund a payment.

**Request Body:**
```json
{
  "amount": 299.98,
  "reason": "Customer return"
}
```

#### GET /api/payments/order/{orderId}
Get payments for a specific order.

---

## 5. Shipping Module API

### Shipment Endpoints

#### GET /api/shipping/shipments
Get all shipments with optional filtering.

**Query Parameters:**
- `orderId` (optional): Filter by order ID
- `status` (optional): Filter by shipment status
- `carrier` (optional): Filter by shipping carrier

**Response:**
```json
[
  {
    "id": 1,
    "orderId": 1,
    "trackingNumber": "TRK123456789",
    "carrier": "UPS",
    "status": "IN_TRANSIT",
    "shippingAddress": "123 Main St",
    "estimatedDelivery": "2024-01-05T18:00:00Z",
    "actualDelivery": null,
    "createdAt": "2024-01-02T09:00:00Z"
  }
]
```

#### GET /api/shipping/shipments/{id}
Get shipment by ID.

#### POST /api/shipping/shipments
Create a new shipment.

**Request Body:**
```json
{
  "orderId": 1,
  "carrier": "UPS",
  "shippingAddress": "123 Main St",
  "estimatedDelivery": "2024-01-05T18:00:00Z"
}
```

#### PUT /api/shipping/shipments/{id}/status
Update shipment status.

**Request Body:**
```json
{
  "status": "SHIPPED",
  "trackingNumber": "TRK123456789"
}
```

#### GET /api/shipping/shipments/track/{trackingNumber}
Track shipment by tracking number.

#### GET /api/shipping/shipments/order/{orderId}  
Get shipments for a specific order.

#### PUT /api/shipping/shipments/{id}/deliver
Mark shipment as delivered.

---

## 6. Notifications Module API

### Notification Endpoints

#### GET /api/notifications
Get all notifications with optional filtering.

**Query Parameters:**
- `customerId` (optional): Filter by customer ID
- `type` (optional): Filter by notification type (EMAIL, SMS, PUSH)
- `status` (optional): Filter by status (PENDING, SENT, FAILED)

**Response:**
```json
[
  {
    "id": 1,
    "customerId": 1,
    "type": "EMAIL",
    "channel": "EMAIL",
    "title": "Order Confirmation",
    "message": "Your order #1 has been confirmed",
    "status": "SENT",
    "sentAt": "2024-01-01T10:05:00Z",
    "createdAt": "2024-01-01T10:00:00Z",
    "metadata": {
      "orderId": 1,
      "eventType": "OrderConfirmedEvent"
    }
  }
]
```

#### GET /api/notifications/{id}
Get notification by ID.

#### POST /api/notifications/send
Send a custom notification.

**Request Body:**
```json
{
  "customerId": 1,
  "type": "ORDER_UPDATE",
  "channel": "EMAIL",
  "title": "Custom Notification",
  "message": "Custom message content",
  "metadata": {
    "orderId": 1
  }
}
```

#### GET /api/notifications/customer/{customerId}
Get notifications for a specific customer.

#### PUT /api/notifications/{id}/retry
Retry a failed notification.

---

## 7. Event Monitoring API

### Event Management Endpoints

#### GET /api/events/monitoring
Get event monitoring statistics.

**Response:**
```json
{
  "totalEventsProcessed": 245,
  "eventsInLastHour": 12,
  "eventTypes": 8
}
```

#### GET /api/events/metrics
Get detailed event metrics by type.

**Response:**
```json
{
  "OrderCreatedEvent": 45,
  "OrderConfirmedEvent": 40,
  "PaymentCompletedEvent": 38,
  "ShipmentCreatedEvent": 35,
  "NotificationSentEvent": 87
}
```

#### GET /api/events/info
Get general event system information.

---

## Error Codes

| Code | Description |
|------|-------------|
| 400 | Bad Request - Invalid request parameters |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Business rule violation |
| 422 | Unprocessable Entity - Validation error |
| 500 | Internal Server Error - System error |

## Authentication

Currently, the API does not require authentication (demo purposes). In a production environment, you would implement:

- JWT-based authentication
- Role-based authorization (CUSTOMER, ADMIN, SYSTEM)
- API key management for service-to-service calls

## Rate Limiting

No rate limiting is currently implemented. For production:

- Implement rate limiting per IP/user
- Different limits for different endpoints
- Graceful degradation under load

## Pagination

For endpoints returning lists, implement standard pagination:

**Query Parameters:**
- `page` (default: 0): Page number
- `size` (default: 20): Page size
- `sort` (optional): Sort field and direction

**Response Headers:**
- `X-Total-Count`: Total number of items
- `X-Page-Count`: Total number of pages