const API_BASE = '/api';

let currentBooks = [];
let allBooks = [];
let users = [];

document.addEventListener('DOMContentLoaded', function() {
    console.log('📚 Library application initialized');
    loadInitialData();
    setupEventListeners();
});

function setupEventListeners() {
    const searchInput = document.getElementById('searchTitle');
    searchInput.addEventListener('input', debounce(handleSearchInput, 300));
    
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchBooks();
        }
    });
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function handleSearchInput() {
    const searchTerm = document.getElementById('searchTitle').value.trim();
    if (searchTerm.length > 2) {
        searchBooks();
    } else if (searchTerm.length === 0) {
        loadAvailableBooks();
    }
}

async function loadInitialData() {
    try {
        await Promise.all([
            loadUsers(),
            loadAvailableBooks(),
            loadStats()
        ]);
    } catch (error) {
        console.error('Error during initial load:', error);
        showAlert('Error loading data', 'error');
    }
}

async function loadStats() {
    try {
        const [booksResponse, usersResponse, transactionsResponse] = await Promise.all([
            fetch(`${API_BASE}/books`),
            fetch(`${API_BASE}/users`),
            fetch(`${API_BASE}/transactions`)
        ]);

        const booksData = await booksResponse.json();
        const usersData = await usersResponse.json();
        const transactionsData = await transactionsResponse.json();

        if (booksData.success && usersData.success && transactionsData.success) {
            const totalBooks = booksData.data.length;
            const availableBooks = booksData.data.filter(book => book.available).length;
            const totalUsers = usersData.data.length;
            const totalTransactions = transactionsData.data.length;

            displayStats({
                totalBooks,
                availableBooks,
                totalUsers,
                totalTransactions
            });
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

function displayStats(stats) {
    const statsContainer = document.getElementById('stats');
    statsContainer.innerHTML = `
        <div class="stat-card">
            <h3>${stats.totalBooks}</h3>
            <p>Total Books</p>
        </div>
        <div class="stat-card">
            <h3>${stats.availableBooks}</h3>
            <p>Available</p>
        </div>
        <div class="stat-card">
            <h3>${stats.totalUsers}</h3>
            <p>Users</p>
        </div>
        <div class="stat-card">
            <h3>${stats.totalTransactions}</h3>
            <p>Transactions</p>
        </div>
    `;
}

async function loadUsers() {
    try {
        const response = await fetch(`${API_BASE}/users`);
        const data = await response.json();
        
        if (data.success) {
            users = data.data;
            console.log(`Loaded ${users.length} users`);
        }
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

async function loadAllBooks() {
    showLoading();
    try {
        const response = await fetch(`${API_BASE}/books`);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            allBooks = data.data;
            displayBooks(currentBooks, 'All Books');
        } else {
            showAlert('Error loading books', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error connecting to server', 'error');
    }
}

async function loadAvailableBooks() {
    showLoading();
    try {
        const response = await fetch(`${API_BASE}/books/available`);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            displayBooks(currentBooks, 'Available Books');
        } else {
            showAlert('Error loading available books', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error connecting to server', 'error');
    }
}

async function searchBooks() {
    const searchTerm = document.getElementById('searchTitle').value.trim();
    
    if (!searchTerm) {
        showAlert('Please enter a search term', 'error');
        return;
    }

    showLoading();
    try {
        const response = await fetch(`${API_BASE}/books/search`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title: searchTerm })
        });
        
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            displayBooks(currentBooks, `Results for "${searchTerm}"`);
            if (currentBooks.length === 0) {
                showAlert('No books found for this search', 'error');
            }
        } else {
            showAlert('Error during search', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error connecting to server', 'error');
    }
}

async function loadRecommendations() {
    const userId = document.getElementById('userId').value.trim();
    
    if (!userId) {
        showAlert('Please enter your user ID', 'error');
        return;
    }

    showLoading();
    try {
        const response = await fetch(`${API_BASE}/users/${userId}/recommendations`);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            displayBooks(currentBooks, `Recommendations for ${userId}`);
            if (currentBooks.length === 0) {
                showAlert('No recommendations available', 'error');
            }
        } else {
            showAlert('Error loading recommendations', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error connecting to server', 'error');
    }
}

async function loanBook(bookId) {
    const userId = document.getElementById('userId').value.trim();
    
    if (!userId) {
        showAlert('Please enter your user ID', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/books/loan`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                bookId: bookId
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showAlert(data.message, 'success');
            loadAvailableBooks();
            loadStats(); 
        } else {
            showAlert(data.message || 'Error during loan', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error connecting to server', 'error');
    }
}

async function returnBook(bookId) {
    const userId = document.getElementById('userId').value.trim();
    
    if (!userId) {
        showAlert('Please enter your user ID', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/books/return`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                bookId: bookId
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showAlert(data.message, 'success');
            
            loadAllBooks();
            loadStats(); 
        } else {
            showAlert(data.message || 'Error during return', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error connecting to server', 'error');
    }
}

function displayBooks(books, title) {
    const container = document.getElementById('booksContainer');
    const sectionTitle = document.getElementById('sectionTitle');
    
    sectionTitle.innerHTML = `<i class="fas fa-book-open"></i> ${title} (${books.length})`;
    if (books.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <i class="fas fa-book" style="font-size: 3rem; color: #ccc; margin-bottom: 20px;"></i>
                <p style="color: #666; font-size: 1.2rem;">No books found</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = books.map(book => createBookCard(book)).join('');
}

function createBookCard(book) {
    const availabilityClass = book.available ? 'available' : 'unavailable';
    const availabilityText = book.available ? '✅ Available' : '❌ Loaned';
    
    const actions = book.available 
        ? `<button class="btn" onclick="loanBook('${book.isbn}')">
               <i class="fas fa-hand-holding"></i> Borrow
           </button>`
        : `<button class="btn btn-warning" onclick="returnBook('${book.isbn}')">
               <i class="fas fa-undo"></i> Return
           </button>`;
    
    return `
        <div class="book-card">
            <h4>${book.title}</h4>
            <div class="book-info">
                <p><strong>Auteur(s):</strong> ${book.authors.join(', ')}</p>
                <p><strong>Année:</strong> ${book.publicationYear}</p>
                <p><strong>Genre:</strong> ${book.genre}</p>
                <p><strong>ISBN:</strong> ${book.isbn}</p>
                <span class="availability ${availabilityClass}">${availabilityText}</span>
            </div>
            <div class="book-actions">
                ${actions}
            </div>
        </div>
    `;
}

function showAlert(message, type) {
    const alertsContainer = document.getElementById('alerts');
    const alertClass = type === 'success' ? 'alert-success' : 'alert-error';
    const icon = type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle';
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert ${alertClass}`;
    alertDiv.innerHTML = `
        <i class="${icon}"></i> ${message}
        <button onclick="this.parentElement.remove()" style="float: right; background: none; border: none; font-size: 1.2rem; cursor: pointer;">&times;</button>
    `;
    
    alertsContainer.appendChild(alertDiv);
    
    setTimeout(() => {
        if (alertDiv.parentElement) {
            alertDiv.remove();
        }
    }, 5000);
}

function showLoading() {
    const container = document.getElementById('booksContainer');
    container.innerHTML = `
        <div class="loading" style="grid-column: 1 / -1;">
            <i class="fas fa-spinner"></i>
            <p>Loading...</p>
        </div>
    `;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR');
}

function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

window.addEventListener('error', function(e) {
    console.error('JavaScript Error:', e.error);
    showAlert('An unexpected error occurred', 'error');
});

console.log('📚 Library management JavaScript module loaded');
console.log('🔧 API available at:', API_BASE);