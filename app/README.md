# Medicine Store Backend

Simplified single-store backend for managing medicines, stock import, cart, and staff-confirmed checkout.

## Simplified Flow

- `ADMIN` creates users and items
- `STAFF` imports stock through input invoices for existing items
- stock increases immediately when an input invoice is created
- `CUSTOMER` browses available items, adds them to cart, and creates an order
- `STAFF` confirms payment
- stock decreases only when the order is confirmed
- if customer adds or updates cart quantity above stock, API returns error immediately

## Roles

- `ADMIN`: create users, create/update items, view all input invoices and orders
- `STAFF`: import stock, view items, view orders, confirm or cancel customer orders
- `CUSTOMER`: view available items, manage cart, create order, view own orders

## Main Models

- `User`
- `Item`
- `InputInvoice`
- `Cart`
- `Order`

## Main APIs

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Users

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/:id`
- `PATCH /api/users/:id`
- `PATCH /api/users/:id/status`

### Items

- `POST /api/items`
- `GET /api/items`
- `GET /api/items/:id`
- `PATCH /api/items/:id`
- `PATCH /api/items/:id/status`
- `GET /api/items/public/available`

### Input Invoices

- `POST /api/input-invoices`
- `GET /api/input-invoices`
- `GET /api/input-invoices/:id`

Example import request:

```json
{
  "note": "Import from supplier A",
  "items": [
    {
      "itemId": "PUT_EXISTING_ITEM_ID_HERE",
      "quantity": 100,
      "salePrice": 22000,
      "expiryDate": "2026-12-31",
      "batchNumber": "BATCH-01"
    }
  ]
}
```

Rule:

- item must already exist
- only admin creates items
- staff only imports quantity for existing items
- admin sets `basePrice` on item
- staff sets `salePrice` when importing stock

Example import request with multiple items:

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

Create item example:

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

### Cart

- `GET /api/cart`
- `POST /api/cart`
- `PATCH /api/cart/:itemId`
- `DELETE /api/cart/:itemId`
- `DELETE /api/cart`

Cart rules:

- add to cart fails immediately if requested quantity is greater than current stock
- update cart quantity fails immediately if new quantity is greater than current stock
- `DELETE /api/cart/:itemId` removes one item only
- `DELETE /api/cart` clears the whole cart

### Orders

- `POST /api/orders/checkout`
- `GET /api/orders`
- `GET /api/orders/:id`
- `PATCH /api/orders/:id/confirm`
- `PATCH /api/orders/:id/cancel`

## Install and Run

```bash
npm install
```

Create `.env` from `.env.example`, then run:

```bash
npm run seed
npm run dev
```

Swagger:

- `http://localhost:3000/api-docs`

## Default Accounts

- Admin: `admin@example.com` / `123456`
- Staff: `staff@example.com` / `123456`
- Customer: `customer@example.com` / `123456`

## Example Business Flow

1. Admin logs in and creates items with `basePrice`.
2. Staff creates an input invoice using existing `itemId` values and sets `salePrice`.
3. Customer registers with `fullName`, `email`, and `password`.
4. Customer views `/api/items/public/available`.
5. Customer adds items to cart.
6. Customer checks out to create an order.
7. Staff confirms the order.
8. Stock is reduced after confirmation.

Customer register example:

```json
{
  "fullName": "Customer A",
  "email": "customer2@example.com",
  "password": "123456"
}
```
