// Global variables
let currentUser = null;
let cartItems = [];

// API Base URL
const API_BASE = '/api';

// Safe placeholder image (base64 encoded SVG)
const PLACEHOLDER_IMAGE = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjI1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNiIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPk5vIEltYWdlPC90ZXh0Pjwvc3ZnPg==';

// Helper function to handle image errors safely
function handleImageError(img) {
    if (img.src !== PLACEHOLDER_IMAGE) {
        img.src = PLACEHOLDER_IMAGE;
    } else {
        img.style.display = 'none';
        const placeholder = document.createElement('div');
        placeholder.className = 'image-placeholder';
        placeholder.style.cssText = 'width: 100%; height: 200px; background: #f0f0f0; display: flex; align-items: center; justify-content: center; color: #999; font-size: 14px; border-radius: 5px;';
        placeholder.textContent = 'No Image Available';
        img.parentElement.appendChild(placeholder);
    }
}

// Initialize the application
document.addEventListener('DOMContentLoaded', function () {
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

    // Load products on home page
    loadHomeProducts();
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
    document.getElementById('search-input').addEventListener('keypress', function (e) {
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
    if (sectionId === 'home') {
        loadHomeProducts();
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

    if (tabId === 'dashboard') {
        loadDashboardStats();
    } else if (tabId === 'products') {
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

// Product functions - HOMEPAGE PRODUCTS
async function loadHomeProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        const products = await response.json();
        
        // Display featured products (first 4)
        const featuredProducts = products.slice(0, 4);
        displayHomeProducts(featuredProducts);
        
        // Display all products
        displayAllProducts(products);
    } catch (error) {
        showAlert('Failed to load products', 'error');
    }
}

function displayHomeProducts(products) {
    const container = document.getElementById('home-products-container');
    if (!container) return;
    
    container.innerHTML = products.map(product => `
        <div class="product-card">
            <div class="product-image">
                <img src="${product.imageUrl || 'https://via.placeholder.com/300x250/cccccc/666666?text=No+Image'}" 
                     alt="${product.name}"
                     onerror="this.src='https://via.placeholder.com/300x250/cccccc/666666?text=Image+Not+Found'">
                ${product.stockQuantity <= 5 && product.stockQuantity > 0 ? '<div class="low-stock-badge">Low Stock</div>' : ''}
                ${product.stockQuantity === 0 ? '<div class="out-of-stock-badge">Out of Stock</div>' : ''}
            </div>
            <div class="product-info">
                <h3>${product.name}</h3>
                <p class="product-description">${product.description || 'No description available'}</p>
                <div class="product-meta">
                    <span class="product-category">${product.category || 'Uncategorized'}</span>
                    ${product.brand ? `<span class="product-brand">${product.brand}</span>` : ''}
                </div>
                <div class="product-price">$${parseFloat(product.price).toFixed(2)}</div>
                <div class="product-stock">
                    ${product.stockQuantity > 0 ? `${product.stockQuantity} in stock` : 'Out of stock'}
                </div>
                <button onclick="addToCart(${product.id})" class="btn btn-primary add-to-cart-btn" 
                        ${product.stockQuantity === 0 ? 'disabled' : ''}>
                    ${product.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                </button>
            </div>
        </div>
    `).join('');
}

function displayAllProducts(products) {
    const container = document.getElementById('all-products-container');
    if (!container) return;
    
    container.innerHTML = products.map(product => `
        <div class="product-card" data-category="${product.category}">
            <div class="product-image">
                <img src="${product.imageUrl || 'https://via.placeholder.com/300x250/cccccc/666666?text=No+Image'}" 
                     alt="${product.name}"
                     onerror="this.src='https://via.placeholder.com/300x250/cccccc/666666?text=Image+Not+Found'">
                ${product.stockQuantity <= 5 && product.stockQuantity > 0 ? '<div class="low-stock-badge">Low Stock</div>' : ''}
                ${product.stockQuantity === 0 ? '<div class="out-of-stock-badge">Out of Stock</div>' : ''}
            </div>
            <div class="product-info">
                <h3>${product.name}</h3>
                <p class="product-description">${product.description || 'No description available'}</p>
                <div class="product-meta">
                    <span class="product-category">${product.category || 'Uncategorized'}</span>
                    ${product.brand ? `<span class="product-brand">${product.brand}</span>` : ''}
                </div>
                <div class="product-price">$${parseFloat(product.price).toFixed(2)}</div>
                <div class="product-stock">
                    ${product.stockQuantity > 0 ? `${product.stockQuantity} in stock` : 'Out of stock'}
                </div>
                <button onclick="addToCart(${product.id})" class="btn btn-primary add-to-cart-btn" 
                        ${product.stockQuantity === 0 ? 'disabled' : ''}>
                    ${product.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                </button>
            </div>
        </div>
    `).join('');
}

// Homepage specific functions
async function searchProductsFromHero() {
    const keyword = document.getElementById('hero-search-input').value.trim();
    if (!keyword) {
        loadHomeProducts();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/products/search?keyword=${encodeURIComponent(keyword)}`);
        const products = await response.json();
        displayAllProducts(products);
        
        // Scroll to products section
        document.getElementById('all-products-container').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        showAlert('Search failed', 'error');
    }
}

function filterByCategory(category) {
    filterProducts(category);
    // Scroll to products section
    document.getElementById('all-products-container').scrollIntoView({ behavior: 'smooth' });
}

function filterProducts(category) {
    const allCards = document.querySelectorAll('#all-products-container .product-card');
    const filterBtns = document.querySelectorAll('.filter-btn');
    
    // Update active filter button
    filterBtns.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Show/hide products based on category
    allCards.forEach(card => {
        if (category === 'all' || card.dataset.category === category) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

function scrollProducts(direction) {
    const container = document.getElementById('home-products-container');
    const scrollAmount = 300;
    
    if (direction === 'left') {
        container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
        container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
}

// Keep original loadProducts for backward compatibility
async function loadProducts() {
    loadHomeProducts();
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

// FIXED: Enhanced product display with proper image handling
function displayProducts(products) {
    const container = document.getElementById('products-container');

    if (products.length === 0) {
        container.innerHTML = '<div class="no-products"><p>No products found.</p></div>';
        return;
    }

    container.innerHTML = products.map(product => `
        <div class="product-card">
            <div class="product-image">
                <img src="${product.imageUrl || 'https://via.placeholder.com/300x250/cccccc/666666?text=No+Image'}" 
                     alt="${product.name}"
                     onerror="this.src='https://via.placeholder.com/300x250/cccccc/666666?text=Image+Not+Found'">
                ${product.stockQuantity <= 5 && product.stockQuantity > 0 ? '<div class="low-stock-badge">Low Stock</div>' : ''}
                ${product.stockQuantity === 0 ? '<div class="out-of-stock-badge">Out of Stock</div>' : ''}
            </div>
            <div class="product-info">
                <h3>${product.name}</h3>
                <p class="product-description">${product.description || 'No description available'}</p>
                <div class="product-meta">
                    <span class="product-category">${product.category || 'Uncategorized'}</span>
                    ${product.brand ? `<span class="product-brand">${product.brand}</span>` : ''}
                </div>
                <div class="product-price">$${parseFloat(product.price).toFixed(2)}</div>
                <div class="product-stock">
                    ${product.stockQuantity > 0 ? `${product.stockQuantity} in stock` : 'Out of stock'}
                </div>
                <button onclick="addToCart(${product.id})" class="btn btn-primary add-to-cart-btn" 
                        ${product.stockQuantity === 0 ? 'disabled' : ''}>
                    ${product.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                </button>
            </div>
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

// FIXED: Enhanced cart display with better formatting and images
function displayCartItems() {
    const container = document.getElementById('cart-container');

    if (cartItems.length === 0) {
        container.innerHTML = `
            <div class="empty-cart">
                <h3>Your cart is empty</h3>
                <p>Browse our products and add items to your cart!</p>
                <button onclick="showSection('products')" class="btn btn-primary">Start Shopping</button>
            </div>
        `;
        return;
    }

    container.innerHTML = cartItems.map(item => `
        <div class="cart-item">
            <div class="cart-item-image">
                <img src="${item.product.imageUrl || 'https://via.placeholder.com/80x80/cccccc/666666?text=No+Image'}" 
                     alt="${item.product.name}"
                     onerror="this.src='https://via.placeholder.com/80x80/cccccc/666666?text=No+Image'">
            </div>
            <div class="cart-item-info">
                <h4>${item.product.name}</h4>
                <p class="item-price">$${parseFloat(item.product.price).toFixed(2)}</p>
                <p class="item-category">${item.product.category || 'Uncategorized'}</p>
            </div>
            <div class="cart-item-controls">
                <div class="quantity-controls">
                    <button onclick="updateCartItemQuantity(${item.id}, ${item.quantity - 1})" class="qty-btn">-</button>
                    <input type="number" value="${item.quantity}" min="1" max="${item.product.stockQuantity}"
                           class="quantity-input" 
                           onchange="updateCartItemQuantity(${item.id}, this.value)">
                    <button onclick="updateCartItemQuantity(${item.id}, ${item.quantity + 1})" class="qty-btn">+</button>
                </div>
                <div class="item-total">$${(parseFloat(item.product.price) * item.quantity).toFixed(2)}</div>
                <button onclick="removeFromCart(${item.id})" class="btn btn-remove">Remove</button>
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
        document.getElementById('cart-total').textContent = parseFloat(data.total).toFixed(2);
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

    loadDashboardStats();
    loadAdminProducts();
}

async function loadDashboardStats() {
    try {
        const response = await fetch(`${API_BASE}/dashboard/stats`);
        const stats = await response.json();
        displayDashboardStats(stats);
    } catch (error) {
        console.error('Failed to load dashboard stats:', error);
    }
}

function displayDashboardStats(stats) {
    const container = document.getElementById('dashboard-stats');
    if (!container) return;

    container.innerHTML = `
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Total Users</h3>
                <div class="stat-number">${stats.totalUsers}</div>
            </div>
            <div class="stat-card">
                <h3>Total Products</h3>
                <div class="stat-number">${stats.totalProducts}</div>
            </div>
            <div class="stat-card">
                <h3>Total Orders</h3>
                <div class="stat-number">${stats.totalOrders}</div>
            </div>
            <div class="stat-card">
                <h3>Total Revenue</h3>
                <div class="stat-number">$${parseFloat(stats.totalRevenue).toFixed(2)}</div>
            </div>
            <div class="stat-card">
                <h3>Pending Orders</h3>
                <div class="stat-number">${stats.pendingOrders}</div>
            </div>
            <div class="stat-card">
                <h3>Low Stock Items</h3>
                <div class="stat-number">${stats.lowStockProducts}</div>
            </div>
        </div>
        
        <div class="recent-orders">
            <h3>Recent Orders</h3>
            <div class="orders-list">
                ${stats.recentOrders.map(order => `
                    <div class="order-item">
                        <span>Order #${order.id}</span>
                        <span>${order.customer}</span>
                        <span>$${parseFloat(order.amount).toFixed(2)}</span>
                        <span class="status-${order.status.toLowerCase()}">${order.status}</span>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
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

// FIXED: Admin product display with proper image handling
function displayAdminProducts(products) {
    const container = document.getElementById('admin-products-list');

    container.innerHTML = products.map(product => `
        <div class="admin-product-item">
            <div class="admin-product-image">
                <img src="${product.imageUrl || 'https://via.placeholder.com/60x60/cccccc/666666?text=No+Image'}" 
                     alt="${product.name}"
                     onerror="this.src='https://via.placeholder.com/60x60/cccccc/666666?text=No+Image'"
                     style="width: 60px; height: 60px; object-fit: cover; border-radius: 5px;">
            </div>
            <div class="admin-product-info">
                <h4>${product.name}</h4>
                <p>Price: $${parseFloat(product.price).toFixed(2)} | Stock: ${product.stockQuantity}</p>
                <p>Category: ${product.category || 'N/A'} | Brand: ${product.brand || 'N/A'}</p>
            </div>
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
            loadProducts(); // Refresh main products view
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
            <p>Total: $${parseFloat(order.totalAmount).toFixed(2)}</p>
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

function editProduct(productId) {
    showAlert('Edit functionality coming soon!', 'info');
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