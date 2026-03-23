# API Integration Guide

This document describes the current backend API, end-to-end business flows, sample requests and responses, and the Android parsing rules required to avoid Gson errors.

## 1. Response Format

All APIs use a unified wrapper:

Success:

```json
{
  "success": true,
  "message": "Short message",
  "data": {}
}
```

Error:

```json
{
  "success": false,
  "message": "Error message",
  "errors": []
}
```

## 2. Main Roles

- `ADMIN`
  - create users
  - create and update items
  - view all input invoices
  - view all orders
- `STAFF`
  - import stock for existing items
  - view items
  - view orders
  - confirm payment
  - cancel pending orders
- `CUSTOMER`
  - register account
  - view available items
  - add/update/remove cart items
  - checkout
  - view own orders
  - cancel own pending orders

## 3. Main Models

### User

```json
{
  "_id": "USER_ID",
  "fullName": "System Admin",
  "email": "admin@example.com",
  "role": "ADMIN",
  "status": "ACTIVE",
  "createdAt": "2026-03-23T10:00:00.000Z",
  "updatedAt": "2026-03-23T10:00:00.000Z"
}
```

### Item

```json
{
  "_id": "ITEM_ID",
  "name": "Paracetamol 500mg",
  "code": "MED-001",
  "category": "Pain Relief",
  "unit": "Box",
  "description": "Pain relief medicine",
  "basePrice": 15000,
  "salePrice": 22000,
  "isActive": true,
  "quantityInStock": 100,
  "expiryDate": "2026-12-31T00:00:00.000Z",
  "batchNumber": "BATCH-01",
  "createdAt": "2026-03-23T10:00:00.000Z",
  "updatedAt": "2026-03-23T10:00:00.000Z"
}
```

### InputInvoice

```json
{
  "_id": "INPUT_ID",
  "code": "IN-202603230001",
  "createdBy": {
    "_id": "USER_ID",
    "fullName": "Store Staff",
    "email": "staff@example.com",
    "role": "STAFF"
  },
  "status": "COMPLETED",
  "note": "Import from supplier A",
  "items": [
    {
      "itemId": {
        "_id": "ITEM_ID",
        "name": "Paracetamol 500mg",
        "code": "MED-001",
        "unit": "Box"
      },
      "name": "Paracetamol 500mg",
      "code": "MED-001",
      "unit": "Box",
      "quantity": 100,
      "salePrice": 22000,
      "expiryDate": "2026-12-31T00:00:00.000Z",
      "batchNumber": "BATCH-01"
    }
  ],
  "totalAmount": 2200000
}
```

### Cart

```json
{
  "_id": "CART_ID",
  "customerId": "USER_ID",
  "items": [
    {
      "itemId": {
        "_id": "ITEM_ID",
        "name": "Paracetamol 500mg",
        "code": "MED-001",
        "category": "Pain Relief",
        "unit": "Box",
        "description": "Pain relief medicine",
        "basePrice": 15000,
        "salePrice": 22000,
        "quantityInStock": 100,
        "isActive": true
      },
      "quantity": 2
    }
  ]
}
```

### Order

```json
{
  "_id": "ORDER_ID",
  "code": "ORD-202603230001",
  "customerId": {
    "_id": "USER_ID",
    "fullName": "Customer Demo",
    "email": "customer@example.com",
    "role": "CUSTOMER"
  },
  "confirmedBy": null,
  "status": "PENDING_CONFIRMATION",
  "note": "Please prepare quickly",
  "items": [
    {
      "itemId": {
        "_id": "ITEM_ID",
        "name": "Paracetamol 500mg",
        "code": "MED-001",
        "unit": "Box"
      },
      "name": "Paracetamol 500mg",
      "code": "MED-001",
      "quantity": 2,
      "salePrice": 22000
    }
  ],
  "totalAmount": 44000
}
```

## 4. Auth APIs

### POST `/api/auth/register`

Creates a customer account and returns a token.

Request:

```json
{
  "fullName": "Customer A",
  "email": "customer@gmail.com",
  "password": "123456"
}
```

Response:

```json
{
  "success": true,
  "message": "Customer registered successfully",
  "data": {
    "user": {
      "id": "USER_ID",
      "fullName": "Customer A",
      "email": "customer@gmail.com",
      "role": "CUSTOMER",
      "status": "ACTIVE"
    },
    "token": "JWT_TOKEN"
  }
}
```

### POST `/api/auth/login`

Request:

```json
{
  "email": "staff@example.com",
  "password": "123456"
}
```

Response:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "USER_ID",
      "fullName": "Store Staff",
      "email": "staff@example.com",
      "role": "STAFF",
      "status": "ACTIVE"
    },
    "token": "JWT_TOKEN"
  }
}
```

### GET `/api/auth/me`

Header:

```text
Authorization: Bearer JWT_TOKEN
```

Response: `data` is a single `User` object.

## 5. User APIs

### POST `/api/users`

Admin creates a new user.

Request:

```json
{
  "fullName": "Store Staff",
  "email": "staff2@example.com",
  "password": "123456",
  "role": "STAFF"
}
```

### GET `/api/users`

Response:

```json
{
  "success": true,
  "message": "Users fetched successfully",
  "data": {
    "items": [
      {
        "_id": "USER_ID",
        "fullName": "Store Staff",
        "email": "staff@example.com",
        "role": "STAFF",
        "status": "ACTIVE"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "totalItems": 3,
      "totalPages": 1
    }
  }
}
```

### GET `/api/users/:id`

Response: `data` is a single `User`.

### PATCH `/api/users/:id`

Request example:

```json
{
  "fullName": "Updated Staff Name"
}
```

### PATCH `/api/users/:id/status`

Request:

```json
{
  "status": "INACTIVE"
}
```

## 6. Item APIs

### POST `/api/items`

Admin creates an item. `basePrice` is the original/base price. `salePrice` is optional when creating.

Request:

```json
{
  "name": "Paracetamol 500mg",
  "code": "MED-001",
  "category": "Pain Relief",
  "unit": "Box",
  "description": "Pain relief medicine",
  "basePrice": 15000
}
```

### GET `/api/items`

Response:

```json
{
  "success": true,
  "message": "Items fetched successfully",
  "data": {
    "items": [
      {
        "_id": "ITEM_ID",
        "name": "Paracetamol 500mg",
        "code": "MED-001",
        "basePrice": 15000,
        "salePrice": 22000,
        "quantityInStock": 100
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "totalItems": 1,
      "totalPages": 1
    }
  }
}
```

### GET `/api/items/:id`

Response: `data` is a single `Item`.

### PATCH `/api/items/:id`

Request example:

```json
{
  "description": "Updated description",
  "basePrice": 16000
}
```

### PATCH `/api/items/:id/status`

Request:

```json
{
  "isActive": false
}
```

### GET `/api/items/public/available`

Public API for customer storefront.

Response:

```json
{
  "success": true,
  "message": "Available items fetched successfully",
  "data": [
    {
      "_id": "ITEM_ID",
      "name": "Paracetamol 500mg",
      "code": "MED-001",
      "salePrice": 22000,
      "quantityInStock": 100,
      "isActive": true
    }
  ]
}
```

Important:

- `GET /api/items` returns `data` as an object with `items` and `pagination`
- `GET /api/items/public/available` returns `data` as an array directly

## 7. Input Invoice APIs

Staff imports stock only for items that already exist.

### POST `/api/input-invoices`

Supports multiple items in one invoice.

Request:

```json
{
  "note": "Import from supplier A",
  "items": [
    {
      "itemId": "ITEM_ID_1",
      "quantity": 100,
      "salePrice": 22000,
      "expiryDate": "2026-12-31",
      "batchNumber": "BATCH-01"
    },
    {
      "itemId": "ITEM_ID_2",
      "quantity": 50,
      "salePrice": 18000,
      "expiryDate": "2027-03-15",
      "batchNumber": "BATCH-02"
    }
  ]
}
```

Behavior:

- every `itemId` must already exist
- stock increases immediately
- `item.salePrice` is updated from the invoice line
- one invoice can contain many items

### GET `/api/input-invoices`

Response:

```json
{
  "success": true,
  "message": "Input invoices fetched successfully",
  "data": {
    "items": [
      {
        "_id": "INPUT_ID",
        "code": "IN-202603230001",
        "status": "COMPLETED",
        "totalAmount": 3100000
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "totalItems": 1,
      "totalPages": 1
    }
  }
}
```

### GET `/api/input-invoices/:id`

Response: `data` is a single `InputInvoice`.

## 8. Cart APIs

Cart now validates stock immediately.

### GET `/api/cart`

Response:

```json
{
  "success": true,
  "message": "Cart fetched successfully",
  "data": {
    "_id": "CART_ID",
    "customerId": "USER_ID",
    "items": [
      {
        "itemId": {
          "_id": "ITEM_ID",
          "name": "Paracetamol 500mg",
          "code": "MED-001",
          "salePrice": 22000,
          "quantityInStock": 100
        },
        "quantity": 2
      }
    ]
  }
}
```

Important:

- `cart.items[].itemId` is an object, not a string

### POST `/api/cart`

Adds an item to the cart.

Request:

```json
{
  "itemId": "ITEM_ID",
  "quantity": 2
}
```

Behavior:

- if item is inactive or missing, returns error
- if requested quantity exceeds stock, returns error immediately
- if item already exists in cart, quantity is increased and validated again

Example error:

```json
{
  "success": false,
  "message": "Requested quantity for Paracetamol 500mg exceeds current stock (100)",
  "errors": null
}
```

### PATCH `/api/cart/:itemId`

Updates quantity for a single cart item.

Request:

```json
{
  "quantity": 5
}
```

Behavior:

- if new quantity exceeds stock, returns error immediately

### DELETE `/api/cart/:itemId`

Removes one item from the cart.

### DELETE `/api/cart`

Clears the whole cart.

## 9. Order APIs

### POST `/api/orders/checkout`

Creates one order from the entire cart.

Request:

```json
{
  "note": "Please prepare quickly"
}
```

Behavior:

- reads all cart items
- snapshots them into order lines
- calculates `totalAmount`
- clears cart after order creation

Response: `data` is a single `Order`.

### GET `/api/orders`

Response:

```json
{
  "success": true,
  "message": "Orders fetched successfully",
  "data": {
    "items": [
      {
        "_id": "ORDER_ID",
        "code": "ORD-202603230001",
        "status": "PENDING_CONFIRMATION",
        "totalAmount": 44000
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "totalItems": 1,
      "totalPages": 1
    }
  }
}
```

Rules:

- admin and staff can view all orders
- customer can only view their own orders

### GET `/api/orders/:id`

Response: `data` is a single `Order`.

### PATCH `/api/orders/:id/confirm`

Staff confirms payment.

Request:

```json
{
  "note": "Payment confirmed"
}
```

Behavior:

- checks stock again before final confirmation
- stock is deducted only after confirmation
- order becomes `CONFIRMED`

### PATCH `/api/orders/:id/cancel`

Request:

```json
{
  "note": "Customer changed mind"
}
```

Rules:

- customer or staff can cancel
- only pending orders can be cancelled

## 10. Complete End-to-End Flow

### Flow A: Admin creates items

1. Admin logs in using `POST /api/auth/login`
2. Admin creates staff/customer users if needed using `POST /api/users`
3. Admin creates items using `POST /api/items`

### Flow B: Staff imports stock

1. Staff logs in
2. Staff gets item list using `GET /api/items`
3. Staff creates one input invoice with one or many items using `POST /api/input-invoices`
4. Stock increases immediately
5. Sale price of imported items is updated

### Flow C: Customer shopping

1. Customer registers with `POST /api/auth/register` or logs in
2. Customer views storefront using `GET /api/items/public/available`
3. Customer adds one or many items to cart using `POST /api/cart`
4. If quantity exceeds stock, customer gets error immediately
5. Customer can:
   - view cart using `GET /api/cart`
   - update quantity using `PATCH /api/cart/:itemId`
   - remove one item using `DELETE /api/cart/:itemId`
   - clear all using `DELETE /api/cart`
6. Customer creates order using `POST /api/orders/checkout`

### Flow D: Staff confirms payment

1. Staff opens `GET /api/orders`
2. Staff opens `GET /api/orders/:id`
3. Staff confirms using `PATCH /api/orders/:id/confirm`
4. Stock is deducted after confirmation

## 11. Android Integration Rules

### Base URL

For Android Emulator:

```text
http://10.0.2.2:3000/api/
```

Do not use `localhost` inside Android Emulator.

### Cleartext HTTP

If backend is using HTTP locally, Android must allow cleartext traffic.

`AndroidManifest.xml`:

```xml
<application
    android:usesCleartextTraffic="true"
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
</application>
```

`res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

## 12. How To Avoid Gson Parsing Errors On Android

### Rule 1: Always use `ApiResponse<T>`

Backend wraps every response.

Correct Java generic wrapper:

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
```

### Rule 2: Do not assume `data` is always a list

Some APIs return:

```json
"data": {
  "items": [...],
  "pagination": {...}
}
```

Examples:

- `GET /api/items`
- `GET /api/users`
- `GET /api/orders`
- `GET /api/input-invoices`

These must map to:

```java
Call<ApiResponse<ItemListData>>
Call<ApiResponse<UserListData>>
Call<ApiResponse<OrderListData>>
Call<ApiResponse<InputInvoiceListData>>
```

Not:

```java
Call<ApiResponse<List<Item>>>
```

### Rule 3: Some APIs return `data` as array directly

Example:

- `GET /api/items/public/available`

This is valid:

```java
Call<ApiResponse<List<Item>>> getAvailableItems();
```

### Rule 4: Populated fields are objects, not strings

This is the most common bug.

Examples from backend:

- `cart.items[].itemId` is an `Item` object
- `order.customerId` is a `User` object
- `order.confirmedBy` is a `User` object or `null`
- `inputInvoice.createdBy` is a `User` object
- `inputInvoice.items[].itemId` is an `Item` object

If Android declares:

```java
private String itemId;
```

But backend returns:

```json
"itemId": {
  "_id": "ITEM_ID",
  "name": "Paracetamol 500mg"
}
```

Gson will fail with errors like:

- `Expected BEGIN_OBJECT but was STRING`
- `Expected a string but was BEGIN_OBJECT`

Correct Java model:

```java
public class CartItem {
    private Item itemId;
    private int quantity;

    public Item getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
}
```

And UI binding:

```java
holder.tvName.setText(cartItem.getItemId().getName());
holder.tvCode.setText(cartItem.getItemId().getCode());
holder.tvPrice.setText(String.valueOf(cartItem.getItemId().getSalePrice()));
```

### Rule 5: Create dedicated response models

Recommended Android models:

- `ApiResponse<T>`
- `Pagination`
- `User`
- `Item`
- `ItemListData`
- `UserListData`
- `InputInvoice`
- `InputInvoiceItem`
- `InputInvoiceListData`
- `CartResponseData`
- `CartItem`
- `Order`
- `OrderItem`
- `OrderListData`

### Example Java models

```java
public class Pagination {
    private int page;
    private int limit;
    private int totalItems;
    private int totalPages;
}
```

```java
public class ItemListData {
    private List<Item> items;
    private Pagination pagination;

    public List<Item> getItems() { return items; }
    public Pagination getPagination() { return pagination; }
}
```

```java
public class CartResponseData {
    private String _id;
    private String customerId;
    private List<CartItem> items;

    public List<CartItem> getItems() { return items; }
}
```

### Correct Retrofit examples

```java
Call<ApiResponse<List<Item>>> getAvailableItems();
```

```java
Call<ApiResponse<ItemListData>> getItems();
```

```java
Call<ApiResponse<CartResponseData>> getCart();
```

```java
Call<ApiResponse<OrderListData>> getOrders();
```

```java
Call<ApiResponse<InputInvoiceListData>> getInputInvoices();
```

## 13. Summary

- Admin creates items
- Staff imports stock for existing items and sets sale price
- Customer adds items to cart and gets stock errors immediately if quantity is too high
- Customer can remove one cart item or clear all cart
- Customer checks out
- Staff confirms payment
- Stock is deducted after confirmation
- Android must parse response models exactly as backend returns them

