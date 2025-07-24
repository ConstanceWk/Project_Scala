# API Documentation

Base URL: `/api`

## Endpoints
- `GET /api/books` - List all books
- `GET /api/books/available` - List available books
- `POST /api/books/search` - Search for a book (body: `{ "title": "..." }`)
- `POST /api/books/loan` - Borrow a book (body: `{ "userId": "...", "bookId": "..." }`)
- `POST /api/books/return` - Return a book (body: `{ "userId": "...", "bookId": "..." }`)
- `POST /api/books/reserve` - Reserve a book (body: `{ "userId": "...", "bookId": "..." }`)
- `GET /api/users/{userId}/recommendations` - Get recommendations
- `GET /api/users/{userId}/reservations` - List reservations for a user
- `GET /api/users` - List users
- `GET /api/transactions` - List transactions
- `GET /api/statistics/genres` - Top 3 genres (returns list of `[genre, count]`)
- `GET /api/statistics/authors` - Top 3 authors (returns list of `[author, count]`)

## Response Format
All responses are wrapped in:
```
{
  "success": true/false,
  "data": ...,
  "message": "..."
}
```

## Example API Calls

### Reserve a Book
```
curl -X POST http://localhost:8080/api/books/reserve \
  -H "Content-Type: application/json" \
  -d '{"userId": "stu1", "bookId": "111"}'
```

### List Reservations for a User
```
curl http://localhost:8080/api/users/stu1/reservations
```

### Get Top 3 Genres
```
curl http://localhost:8080/api/statistics/genres
```

### Get Top 3 Authors
```
curl http://localhost:8080/api/statistics/authors
```
