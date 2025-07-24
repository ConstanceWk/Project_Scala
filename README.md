# 📚 Library Management System – Scala 3 Project

This is a functional **Library Management System** built using **Scala 3**. The project consists of:

- A modern web-based interface (HTML/CSS/JavaScript)
- A RESTful API powered by **Akka HTTP**
- A persistent data layer using **JSON** serialization with **Circe**
- A robust test suite using **ScalaTest** and **ScalaCheck**

It demonstrates Scala 3 programming principles including algebraic data types (ADTs), functional error handling, immutability, higher-order functions, and modular architecture.

---

## Features

- **Search books** by title
- **Borrow** books (only if available)
- **Return** borrowed books
- **Get recommendations** based on past borrow history
- **View available books**
- **Real-time statistics** (total books, users, and transactions)
- **Modern web interface** with live updates and animations

---

## Project Architecture

### Backend (Scala 3)

- `models/` – Case classes and traits for `Book`, `User`, and `Transaction`
- `services/LibraryCatalog.scala` – Business logic
- `utils/JsonIO.scala` – File I/O and JSON persistence
- `LibraryApp.scala` – Console-based application
- `LibraryServer.scala` – RESTful API using Akka HTTP

### Frontend

- `public/index.html` – Modern UI layout with sidebar and book cards
- `public/app.js` – Client-side JavaScript for interacting with the API
- Responsive design with CSS gradients, cards, and alerts

---

## 🛠 Setup Instructions

### Requirements

- **JDK 17+**
- **sbt 1.11.2+**

> If you're using VSCode, install the **Metals plugin** for Scala 3 support.

---

## Usage

### Starting the Console Application

```bash
sbt "runMain LibraryApp"
```

### Starting the App server

```bash
cd project-root
sbt run
```

The server starts on http://localhost:8080

### User interface

1. **Login**: Enter your user ID
2. **Search**: Type a book title in the search bar
3. **Available actions**:
   - Click on "Available Books"
   - Click on "My Recommendations" (requires user ID)
   - Click on "All Books"
4. **Borrow/Return**: Use the buttons on each book

---

## REST API Endpoints

The REST API is available at `/api` with the following endpoints:

| Method | Endpoint                              | Description              |
| ------ | ------------------------------------- | ------------------------ |
| GET    | `/api/books`                          | Returns all books        |
| GET    | `/api/books/available`                | Available books only     |
| POST   | `/api/books/search`                   | Search books by title    |
| POST   | `/api/books/loan`                     | Borrow a book            |
| POST   | `/api/books/return`                   | Return a borrowed book   |
| GET    | `/api/users/{userId}/recommendations` | Personalized suggestions |
| GET    | `/api/users`                          | All users                |
| GET    | `/api/transactions`                   | Transaction history      |

---

## Testing

This project includes:

- **Unit Tests** – for `LibraryCatalog` logic
- **Property-based Tests** – for book loan behavior
- **JSON I/O Tests** – to verify file reading/writing
- **Test Coverage** – run `sbt coverage test` to generate reports

To run all tests:

```bash
sbt test
```

---

## Web Interface Advantages

### Compared to the console application:

1. **Modern interface**: Attractive, professional design
2. **Ease of use**: Intuitive navigation with buttons and forms
3. **Real-time**: Automatic statistics updates
4. **Accessibility**: Usable from any browser
5. **Responsive**: Adapts to mobiles and tablets
6. **Real-time search**: Search as you type
7. **Visual feedback**: Success/error alerts, animations

### Improved features:

- **Real-time statistics**: Visual counters at top of page
- **Smart search**: Automatic search after 3 characters
- **Graphical interface**: Book cards with detailed information
- **Error handling**: Clear, informative error messages
- **Performance**: Asynchronous data loading

## Console vs Web Comparison

| Feature       | Console       | Web                      |
| ------------- | ------------- | ------------------------ |
| Search        | Manual input  | Real-time + suggestions  |
| Navigation    | Text menu     | Graphical interface      |
| Display       | Plain text    | Visual cards with status |
| Statistics    | None          | Real-time dashboard      |
| Multi-user    | No            | Yes (concurrent)         |
| Accessibility | Terminal only | Web browser              |
| Responsive    | No            | Yes (mobile/desktop)     |

## Technologies & Libraries

- Scala 3 — functional programming and new syntax
- Akka HTTP — REST API server
- Circe — JSON encoding/decoding
- ScalaTest — unit testing
- ScalaCheck — property-based testing
- HTML/CSS/JavaScript — for the frontend

## Authors

- WALUSIAK Constance
- LAVERGNE Louise
- BOUDAOUD Amira
- LABIDURIE Maelwenn
- COIFFE Albane

## Additional Documentation

You can find more details in:

- `project-root/Docs` – where you can find the different ReadMe files and design document explaining everything done in the application in details 

---

## Conclusion

This web-based library management system successfully modernizes the original console application while maintaining all core functionality. Key achievements include:

1. **Preserved business logic**: LibraryCatalog remains identical
2. **Complete REST API**: All necessary endpoints
3. **State management**: Automatic reload after changes
4. **Client-side validation**: Checks before sending requests
5. **Modern design**: Professional interface with animations

The system is now ready for deployment or further enhancement with additional features like:

- User authentication
- Advanced analytics
- Mobile app integration
