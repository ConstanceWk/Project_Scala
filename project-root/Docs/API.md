# API Documentation

Base URL: `/api`

## Endpoints
- `GET /api/books` - List all books
- `GET /api/books/available` - List available books
- `POST /api/books/search` - Search for a book (body: `{ "title": "..." }`)
- `POST /api/books/loan` - Borrow a book (body: `{ "userId": "...", "bookId": "..." }`)
- `POST /api/books/return` - Return a book (body: `{ "userId": "...", "bookId": "..." }`)
- `GET /api/users/{userId}/recommendations` - Get recommendations
- `GET /api/users` - List users
- `GET /api/transactions` - List transactions

## Response Format
All responses are wrapped in:
```
{
  "success": true/false,
  "data": ...,
  "message": "..."
}
```
