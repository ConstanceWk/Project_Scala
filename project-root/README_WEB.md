# 📚 Library Management System - Web Interface

This project transforms the console application LibraryApp into a modern web interface with a REST API.

## 🚀 Features

The web interface offers the same features as the original console application:

- **Book search** by title
- **Book loan** with availability validation
- **Book return** with status update
- **Personalized recommendations** based on loan history
- **View available books**
- **Real-time statistics** (number of books, users, transactions)

## 🏗️ Architecture

### Backend (Scala)
- **LibraryServer.scala**: HTTP server with REST API (Akka HTTP)
- **LibraryCatalog.scala**: Business logic (same as original)
- **Models**: Book, User, Transaction (same as original)
- **JsonIO**: Data persistence (same as original)

### Frontend (HTML/CSS/JavaScript)
- **index.html**: Modern, responsive user interface
- **app.js**: Client logic and API communication
- **Modern design**: CSS3 with gradients, animations, and responsive interface

## 🔧 Usage

### Starting the Server

```bash
cd project-root
sbt run
```

The server starts at http://localhost:8080

### Stopping the Server (Killing the Process)

To stop the server, you need to kill the process running on the port (usually 8080). Here is how to do it on macOS/Linux:

1. Find the process ID (PID) using the following command:
   ```bash
   lsof -i :8080
   ```
   This will show the PID of the process using port 8080.
2. Kill the process by running:
   ```bash
   kill -9 <PID>
   ```
   Replace `<PID>` with the actual number from the previous command.

This will stop the server and free up the port for future use.

### User Interface

1. **Login**: Enter your user ID
2. **Search**: Type a book title in the search bar
3. **Available actions**:
   - Click "Available Books"
   - Click "My Recommendations" (requires user ID)
   - Click "All Books"
4. **Loans/Returns**: Use the buttons on each book

### REST API

The REST API is available at `/api` with the following endpoints:

- `GET /api/books` - List all books
- `GET /api/books/available` - List available books
- `POST /api/books/search` - Search for books
- `POST /api/books/loan` - Loan a book
- `POST /api/books/return` - Return a book
- `GET /api/users/{userId}/recommendations` - Recommendations
- `GET /api/users` - List users
- `GET /api/transactions` - List transactions

## 🖥️ Console Application Option

You can also use the original console version of the Library Management System if you prefer working in the terminal.

### Running the Console Application

1. Open a terminal and navigate to the project directory:
   ```bash
   cd project-root
   ```
2. Run the console application:
   ```bash
   sbt "runMain LibraryApp"
   ```
   This will launch the interactive console interface.

### Choosing Your Mode
- **Web Interface**: Modern, graphical, accessible from any browser.
- **Console Application**: Text-based, interactive in the terminal.

Both modes use the same business logic and data, so you can choose the experience that suits you best.

## 📊 Advantages of the Web Interface

### Compared to the console application:

1. **Modern interface**: Attractive and professional design
2. **Ease of use**: Intuitive navigation with buttons and forms
3. **Real-time**: Automatic statistics updates
4. **Accessibility**: Usable from any browser
5. **Responsive**: Adapts to mobile and tablets
6. **Real-time search**: Search as you type
7. **Visual feedback**: Success/error alerts, animations
8. **Multi-user**: Multiple users can access simultaneously

### Enhanced features:

- **Real-time statistics**: Visual counters at the top of the page
- **Smart search**: Automatic search after 3 characters
- **Graphical interface**: Book cards with detailed information
- **Error handling**: Clear and informative error messages
- **Performance**: Asynchronous data loading

## 🔄 Console vs Web Comparison

| Feature         | Console         | Web                        |
|----------------|----------------|----------------------------|
| Search         | Manual input    | Real-time + suggestions    |
| Navigation     | Text menu       | Graphical interface        |
| Display        | Simple text     | Visual cards with status   |
| Statistics     | None            | Real-time dashboard        |
| Multi-user     | No              | Yes (concurrent)           |
| Accessibility  | Terminal only   | Web browser                |
| Responsive     | No              | Yes (mobile/desktop)       |

## 🎯 Key Implementation Points

1. **Preservation of business logic**: LibraryCatalog remains unchanged
2. **Complete REST API**: All necessary endpoints
3. **State management**: Automatic reload after changes
4. **Client-side validation**: Checks before sending requests
5. **Modern design**: Professional interface with animations

The web interface transforms the user experience while maintaining the robustness and reliability of the original business logic.
