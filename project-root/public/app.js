// Configuration de l'API
const API_BASE = '/api';

// Variables globales
let currentBooks = [];
let allBooks = [];
let users = [];

// Initialisation de l'application
document.addEventListener('DOMContentLoaded', function() {
    console.log('📚 Application de bibliothèque initialisée');
    loadInitialData();
    setupEventListeners();
});

// Configuration des événements
function setupEventListeners() {
    // Recherche en temps réel
    const searchInput = document.getElementById('searchTitle');
    searchInput.addEventListener('input', debounce(handleSearchInput, 300));
    
    // Touche Entrée pour rechercher
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchBooks();
        }
    });
}

// Fonction de debounce pour éviter trop de requêtes
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

// Gestion de la recherche en temps réel
function handleSearchInput() {
    const searchTerm = document.getElementById('searchTitle').value.trim();
    if (searchTerm.length > 2) {
        searchBooks();
    } else if (searchTerm.length === 0) {
        loadAvailableBooks();
    }
}

// Chargement des données initiales
async function loadInitialData() {
    try {
        await Promise.all([
            loadUsers(),
            loadAvailableBooks(),
            loadStats()
        ]);
    } catch (error) {
        console.error('Erreur lors du chargement initial:', error);
        showAlert('Erreur lors du chargement des données', 'error');
    }
}

// Chargement des statistiques
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
        console.error('Erreur lors du chargement des statistiques:', error);
    }
}

// Affichage des statistiques
function displayStats(stats) {
    const statsContainer = document.getElementById('stats');
    statsContainer.innerHTML = `
        <div class="stat-card">
            <h3>${stats.totalBooks}</h3>
            <p>Total Livres</p>
        </div>
        <div class="stat-card">
            <h3>${stats.availableBooks}</h3>
            <p>Disponibles</p>
        </div>
        <div class="stat-card">
            <h3>${stats.totalUsers}</h3>
            <p>Utilisateurs</p>
        </div>
        <div class="stat-card">
            <h3>${stats.totalTransactions}</h3>
            <p>Transactions</p>
        </div>
    `;
}

// Chargement des utilisateurs
async function loadUsers() {
    try {
        const response = await fetch(`${API_BASE}/users`);
        const data = await response.json();
        
        if (data.success) {
            users = data.data;
            console.log(`Chargé ${users.length} utilisateurs`);
        }
    } catch (error) {
        console.error('Erreur lors du chargement des utilisateurs:', error);
    }
}

// Chargement de tous les livres
async function loadAllBooks() {
    showLoading();
    try {
        const response = await fetch(`${API_BASE}/books`);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            allBooks = data.data;
            displayBooks(currentBooks, 'Tous les Livres');
        } else {
            showAlert('Erreur lors du chargement des livres', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur de connexion au serveur', 'error');
    }
}

// Chargement des livres disponibles
async function loadAvailableBooks() {
    showLoading();
    try {
        const response = await fetch(`${API_BASE}/books/available`);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            displayBooks(currentBooks, 'Livres Disponibles');
        } else {
            showAlert('Erreur lors du chargement des livres disponibles', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur de connexion au serveur', 'error');
    }
}

// Recherche de livres
async function searchBooks() {
    const searchTerm = document.getElementById('searchTitle').value.trim();
    
    if (!searchTerm) {
        showAlert('Veuillez entrer un terme de recherche', 'error');
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
            displayBooks(currentBooks, `Résultats pour "${searchTerm}"`);
            if (currentBooks.length === 0) {
                showAlert('Aucun livre trouvé pour cette recherche', 'error');
            }
        } else {
            showAlert('Erreur lors de la recherche', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur de connexion au serveur', 'error');
    }
}

// Chargement des recommandations
async function loadRecommendations() {
    const userId = document.getElementById('userId').value.trim();
    
    if (!userId) {
        showAlert('Veuillez entrer votre ID utilisateur', 'error');
        return;
    }

    showLoading();
    try {
        const response = await fetch(`${API_BASE}/users/${userId}/recommendations`);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.data;
            displayBooks(currentBooks, `Recommandations pour ${userId}`);
            if (currentBooks.length === 0) {
                showAlert('Aucune recommandation disponible', 'error');
            }
        } else {
            showAlert('Erreur lors du chargement des recommandations', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur de connexion au serveur', 'error');
    }
}

// Emprunter un livre
async function loanBook(bookId) {
    const userId = document.getElementById('userId').value.trim();
    
    if (!userId) {
        showAlert('Veuillez entrer votre ID utilisateur', 'error');
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
            // Recharger les livres disponibles
            loadAvailableBooks();
            loadStats(); // Mettre à jour les statistiques
        } else {
            showAlert(data.message || 'Erreur lors de l\'emprunt', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur de connexion au serveur', 'error');
    }
}

// Retourner un livre
async function returnBook(bookId) {
    const userId = document.getElementById('userId').value.trim();
    
    if (!userId) {
        showAlert('Veuillez entrer votre ID utilisateur', 'error');
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
            // Recharger tous les livres pour voir le changement
            loadAllBooks();
            loadStats(); // Mettre à jour les statistiques
        } else {
            showAlert(data.message || 'Erreur lors du retour', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur de connexion au serveur', 'error');
    }
}

// Affichage des livres
function displayBooks(books, title) {
    const container = document.getElementById('booksContainer');
    const sectionTitle = document.getElementById('sectionTitle');
    
    sectionTitle.innerHTML = `<i class="fas fa-book-open"></i> ${title} (${books.length})`;
    
    if (books.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <i class="fas fa-book" style="font-size: 3rem; color: #ccc; margin-bottom: 20px;"></i>
                <p style="color: #666; font-size: 1.2rem;">Aucun livre trouvé</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = books.map(book => createBookCard(book)).join('');
}

// Création d'une carte de livre
function createBookCard(book) {
    const availabilityClass = book.available ? 'available' : 'unavailable';
    const availabilityText = book.available ? '✅ Disponible' : '❌ Emprunté';
    
    const actions = book.available 
        ? `<button class="btn" onclick="loanBook('${book.isbn}')">
               <i class="fas fa-hand-holding"></i> Emprunter
           </button>`
        : `<button class="btn btn-warning" onclick="returnBook('${book.isbn}')">
               <i class="fas fa-undo"></i> Retourner
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

// Affichage des alertes
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
    
    // Supprimer automatiquement après 5 secondes
    setTimeout(() => {
        if (alertDiv.parentElement) {
            alertDiv.remove();
        }
    }, 5000);
}

// Affichage du chargement
function showLoading() {
    const container = document.getElementById('booksContainer');
    container.innerHTML = `
        <div class="loading" style="grid-column: 1 / -1;">
            <i class="fas fa-spinner"></i>
            <p>Chargement en cours...</p>
        </div>
    `;
}

// Fonctions utilitaires
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR');
}

function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// Gestion des erreurs globales
window.addEventListener('error', function(e) {
    console.error('Erreur JavaScript:', e.error);
    showAlert('Une erreur inattendue s\'est produite', 'error');
});

// Console log pour le debugging
console.log('📚 Module JavaScript de gestion de bibliothèque chargé');
console.log('🔧 API disponible sur:', API_BASE);