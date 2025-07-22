// Global variables
let currentUser = null;
let cartItems = [];

// API Base URL
const API_BASE = '/api';

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
});

function initializeApp() {
    // Check if user is logged in (from localStorage)
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        updateUIForLoggedInUser();
        loadCartItems();
    }
    
    // Load products on initial load
    loadProducts();
}

function setupEventListeners() {
    // Login form
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    
    // Register form
    document.getElementById('register-form').addEventListener('submit', handleRegister);
    
    // Checkout form
    document.getElementById('checkout-form').addEventListener('submit', handleCheckout);
    
    // Add product form (admin)
    document.getElementById('add-product-form').addEventListener('submit', handleAddProduct);
    
    // Search input
    document.getElementById('search-input').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchProducts();
        }
    });
}

// Navigation functions
function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Show selected section
    document.getElementById(sectionId).classList.add('active');
    
    // Load section-specific data
    if (sectionId === 'products') {
        loadProducts();
    } else if (sectionId === 'cart') {
        loadCartItems();
    } else if (sectionId === 'admin') {
        loadAdminData();
    }
}

function showAdminTab(tabId) {
    // Hide all admin tabs
    document.querySelectorAll('.admin-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(`admin-${tabId}`).classList.add('active');
    event.target.classList.add('active');
    
    if (tabId === 'products') {
        loadAdminProducts();
    } else if (tabId === 'orders') {
        loadAdminOrders();
    }
}

// Authentication functions
async function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            currentUser = data.user;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            updateUIForLoggedInUser();
            showAlert('Login successful!', 'success');
            showSection('home');
            loadCartItems();
        } else {
            showAlert(data.error || 'Login failed', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const userData = {
        username: document.getElementById('register-username').value,
        email: document.getElementById('register-email').value,
        password: document.getElementById('register-password').value,
        fullName: document.getElementById('register-fullname').value,
        address: document.getElementById('register-address').value,
        phoneNumber: document.getElementById('register-phone').value
    };
    
    try {
        const response = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showAlert('Registration successful! Please login.', 'success');
            showSection('login');
            document.getElementById('register-form').reset();
        } else {
            showAlert(data.error || 'Registration failed', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

function logout() {
    currentUser = null;
    cartItems = [];
    localStorage.removeItem('currentUser');
    updateUIForLoggedOutUser();
    showSection('home');
    showAlert('Logged out successfully!', 'success');
}

function updateUIForLoggedInUser() {
    document.getElementById('login-link').style.display = 'none';
    document.getElementById('register-link').style.display = 'none';
    document.getElementById('logout-link').style.display = 'block';
    
    if (currentUser.role === 'ADMIN') {
        document.getElementById('admin-link').style.display = 'block';
    }
}

function updateUIForLoggedOutUser() {
    document.getElementById('login-link').style.display = 'block';
    document.getElementById('register-link').style.display = 'block';
    document.getElementById('logout-link').style.display = 'none';
    document.getElementById('admin-link').style.display = 'none';
}

// Product functions
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        showAlert('Failed to load products', 'error');
    }
}

async function searchProducts() {
    const keyword = document.getElementById('search-input').value.trim();
    if (!keyword) {
        loadProducts();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/products/search?keyword=${encodeURIComponent(keyword)}`);
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        showAlert('Search failed', 'error');
    }
}

function displayProducts(products) {
    const container = document.getElementById('products-container');
    
    if (products.length === 0) {
        container.innerHTML = '<p>No products found.</p>';
        return;
    }
    
    container.innerHTML = products.map(product => `
        <div class="product-card">
            <img src="${product.imageUrl || 'https://via.placeholder.com/250x200'}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p>${product.description || 'No description available'}</p>
            <div class="product-price">$${product.price}</div>
            <div class="product-stock">Stock: ${product.stockQuantity}</div>
            <button onclick="addToCart(${product.id})" class="btn btn-primary" 
                    ${product.stockQuantity === 0 ? 'disabled' : ''}>
                ${product.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
            </button>
        </div>
    `).join('');
}

// Cart functions
async function addToCart(productId) {
    if (!currentUser) {
        showAlert('Please login to add items to cart', 'error');
        showSection('login');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/cart/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: currentUser.id,
                productId: productId,
                quantity: 1
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showAlert('Product added to cart!', 'success');
            loadCartItems();
        } else {
            showAlert(data.error || 'Failed to add to cart', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

async function loadCartItems() {
    if (!currentUser) return;
    
    try {
        const response = await fetch(`${API_BASE}/cart/user/${currentUser.id}`);
        cartItems = await response.json();
        displayCartItems();
        updateCartCount();
        updateCartTotal();
    } catch (error) {
        console.error('Failed to load cart items:', error);
    }
}

function displayCartItems() {
    const container = document.getElementById('cart-container');
    
    if (cartItems.length === 0) {
        container.innerHTML = '<p>Your cart is empty.</p>';
        return;
    }
    
    container.innerHTML = cartItems.map(item => `
        <div class="cart-item">
            <div class="cart-item-info">
                <h4>${item.product.name}</h4>
                <p>Price: $${item.product.price}</p>
            </div>
            <div class="cart-item-controls">
                <input type="number" value="${item.quantity}" min="1" 
                       class="quantity-input" 
                       onchange="updateCartItemQuantity(${item.id}, this.value)">
                <button onclick="removeFromCart(${item.id})" class="btn">Remove</button>
            </div>
        </div>
    `).join('');
}

async function updateCartItemQuantity(cartItemId, quantity) {
    try {
        const response = await fetch(`${API_BASE}/cart/update/${cartItemId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ quantity: parseInt(quantity) })
        });
        
        if (response.ok) {
            loadCartItems();
        }
    } catch (error) {
        showAlert('Failed to update quantity', 'error');
    }
}

async function removeFromCart(cartItemId) {
    try {
        const response = await fetch(`${API_BASE}/cart/remove/${cartItemId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showAlert('Item removed from cart', 'success');
            loadCartItems();
        }
    } catch (error) {
        showAlert('Failed to remove item', 'error');
    }
}

function updateCartCount() {
    const count = cartItems.reduce((total, item) => total + item.quantity, 0);
    document.getElementById('cart-count').textContent = count;
}

async function updateCartTotal() {
    if (!currentUser) return;
    
    try {
        const response = await fetch(`${API_BASE}/cart/total/${currentUser.id}`);
        const data = await response.json();
        document.getElementById('cart-total').textContent = data.total.toFixed(2);
    } catch (error) {
        console.error('Failed to update cart total:', error);
    }
}

function checkout() {
    if (!currentUser) {
        showAlert('Please login to checkout', 'error');
        showSection('login');
        return;
    }
    
    if (cartItems.length === 0) {
        showAlert('Your cart is empty', 'error');
        return;
    }
    
    showSection('checkout');
}

async function handleCheckout(e) {
    e.preventDefault();
    
    const shippingAddress = document.getElementById('shipping-address').value;
    const paymentMethod = document.getElementById('payment-method').value;
    
    try {
        const response = await fetch(`${API_BASE}/orders/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: currentUser.id,
                shippingAddress: shippingAddress,
                paymentMethod: paymentMethod
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showAlert(`Order placed successfully! Order ID: ${data.orderId}`, 'success');
            document.getElementById('checkout-form').reset();
            loadCartItems(); // Refresh cart (should be empty now)
            showSection('home');
        } else {
            showAlert(data.error || 'Failed to place order', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

// Admin functions
async function loadAdminData() {
    if (!currentUser || currentUser.role !== 'ADMIN') {
        showAlert('Access denied', 'error');
        showSection('home');
        return;
    }
    
    loadAdminProducts();
}

async function loadAdminProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        const products = await response.json();
        displayAdminProducts(products);
    } catch (error) {
        showAlert('Failed to load products', 'error');
    }
}

function displayAdminProducts(products) {
    const container = document.getElementById('admin-products-list');
    
    container.innerHTML = products.map(product => `
        <div class="admin-product-item">
            <h4>${product.name}</h4>
            <p>Price: $${product.price} | Stock: ${product.stockQuantity}</p>
            <p>Category: ${product.category || 'N/A'} | Brand: ${product.brand || 'N/A'}</p>
            <div class="admin-product-actions">
                <button onclick="editProduct(${product.id})" class="btn">Edit</button>
                <button onclick="deleteProduct(${product.id})" class="btn" style="background: #e74c3c;">Delete</button>
            </div>
        </div>
    `).join('');
}

async function handleAddProduct(e) {
    e.preventDefault();
    
    const productData = {
        name: document.getElementById('product-name').value,
        description: document.getElementById('product-description').value,
        price: parseFloat(document.getElementById('product-price').value),
        stockQuantity: parseInt(document.getElementById('product-stock').value),
        category: document.getElementById('product-category').value,
        brand: document.getElementById('product-brand').value,
        imageUrl: document.getElementById('product-image').value
    };
    
    try {
        const response = await fetch(`${API_BASE}/products`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(productData)
        });
        
        if (response.ok) {
            showAlert('Product added successfully!', 'success');
            document.getElementById('add-product-form').reset();
            loadAdminProducts();
        } else {
            showAlert('Failed to add product', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

async function deleteProduct(productId) {
    if (!confirm('Are you sure you want to delete this product?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/products/${productId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showAlert('Product deleted successfully!', 'success');
            loadAdminProducts();
            loadProducts(); // Refresh main products view
        } else {
            showAlert('Failed to delete product', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

async function loadAdminOrders() {
    try {
        const response = await fetch(`${API_BASE}/orders/admin/all`);
        const orders = await response.json();
        displayAdminOrders(orders);
    } catch (error) {
        showAlert('Failed to load orders', 'error');
    }
}

function displayAdminOrders(orders) {
    const container = document.getElementById('admin-orders-list');
    
    if (orders.length === 0) {
        container.innerHTML = '<p>No orders found.</p>';
        return;
    }
    
    container.innerHTML = orders.map(order => `
        <div class="admin-order-item">
            <h4>Order #${order.id}</h4>
            <p>Customer: ${order.user.fullName} (${order.user.email})</p>
            <p>Date: ${new Date(order.orderDate).toLocaleDateString()}</p>
            <p>Status: ${order.status}</p>
            <p>Total: $${order.totalAmount}</p>
            <p>Shipping: ${order.shippingAddress}</p>
            <div class="admin-order-actions">
                <select onchange="updateOrderStatus(${order.id}, this.value)">
                    <option value="">Change Status</option>
                    <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                    <option value="CONFIRMED" ${order.status === 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                    <option value="SHIPPED" ${order.status === 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                    <option value="DELIVERED" ${order.status === 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                    <option value="CANCELLED" ${order.status === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                </select>
            </div>
        </div>
    `).join('');
}

async function updateOrderStatus(orderId, status) {
    if (!status) return;
    
    try {
        const response = await fetch(`${API_BASE}/orders/admin/status/${orderId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status: status })
        });
        
        if (response.ok) {
            showAlert('Order status updated successfully!', 'success');
            loadAdminOrders();
        } else {
            showAlert('Failed to update order status', 'error');
        }
    } catch (error) {
        showAlert('Network error. Please try again.', 'error');
    }
}

// Utility functions
function showAlert(message, type) {
    // Remove existing alerts
    const existingAlert = document.querySelector('.alert');
    if (existingAlert) {
        existingAlert.remove();
    }
    
    // Create new alert
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    
    // Insert at the top of main content
    const main = document.querySelector('main');
    main.insertBefore(alert, main.firstChild);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}