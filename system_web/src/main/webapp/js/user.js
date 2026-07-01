const API_BASE = 'http://localhost:8080/system_web/api';
let state = {
    user: localStorage.getItem('techmartUser'),
    role: localStorage.getItem('techmartRole'),
    products: [],
    cart: []
};

document.addEventListener('DOMContentLoaded', () => {
    if (!state.user || state.role !== 'USER') {
        window.location.href = 'index.html';
        return;
    }

    document.getElementById('user-greeting').textContent = `Welcome, ${state.user}`;

    const views = {
        dashboard: document.getElementById('dashboard-view'),
        profile: document.getElementById('profile-view')
    };

    const navShop = document.getElementById('nav-shop-btn');
    const navProfile = document.getElementById('nav-profile-btn');
    
    function showShop() {
        views.profile.classList.remove('active');
        views.dashboard.classList.add('active');
        navShop.style.display = 'none';
        navProfile.style.display = 'inline-block';
        loadProducts();
    }
    
    function showProfile() {
        views.dashboard.classList.remove('active');
        views.profile.classList.add('active');
        navProfile.style.display = 'none';
        navShop.style.display = 'inline-block';
        loadUserOrders();
    }

    navShop.addEventListener('click', showShop);
    navProfile.addEventListener('click', showProfile);

    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'index.html';
    });


    const cartModal = document.getElementById('cart-modal');
    document.getElementById('cart-btn').addEventListener('click', () => { cartModal.style.display = 'flex'; loadCart(); });
    document.querySelector('.close-btn').addEventListener('click', () => cartModal.style.display = 'none');
    window.addEventListener('click', (e) => { if (e.target === cartModal) cartModal.style.display = 'none'; });

    document.getElementById('checkout-btn').addEventListener('click', handleCheckout);


    showShop();
    loadCart();
});


async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        if (response.ok) {
            state.products = await response.json();
            renderProducts();
        }
    } catch (e) { console.error('Failed to load products', e); }
}

function renderProducts() {
    const grid = document.getElementById('products-grid');
    grid.innerHTML = state.products.map(p => `
        <div class="product-card glass-panel">
            <div class="product-image"></div>
            <div class="product-info">
                <h3>${p.name}</h3>
                <p>${p.description}</p>
                <div class="product-meta">
                    <span class="price">$${p.price.toFixed(2)}</span>
                    <span class="stock ${p.inventoryCount < 10 ? 'low' : ''}">${p.inventoryCount} left</span>
                </div>
                <button class="btn btn-primary" onclick="addToCart(${p.id})" style="width:100%; margin-top:1rem;">Add to Cart</button>
            </div>
        </div>
    `).join('');
}

window.addToCart = async function(productId) {
    try {
        const response = await fetch(`${API_BASE}/cart/add?productId=${productId}&quantity=1`, {
            method: 'POST',
            headers: { 'X-User': state.user }
        });
        if (response.ok) {
            loadCart();
            const btn = event.target;
            btn.textContent = 'Added!';
            btn.style.background = '#10b981';
            setTimeout(() => { btn.textContent = 'Add to Cart'; btn.style.background = ''; }, 1000);
        }
    } catch (e) { console.error(e); }
}

async function loadCart() {
    try {
        const response = await fetch(`${API_BASE}/cart`, { headers: { 'X-User': state.user } });
        if (response.ok) {
            state.cart = await response.json();
            document.getElementById('cart-count').textContent = state.cart.length;
            renderCart();
        }
    } catch (e) { console.error(e); }
}

function renderCart() {
    const list = document.getElementById('cart-items-list');
    let total = 0;
    
    if (state.cart.length === 0) {
        list.innerHTML = '<p style="text-align:center;color:#94a3b8;padding:2rem;">Your cart is empty.</p>';
    } else {
        list.innerHTML = state.cart.map(item => {
            total += item.price;
            return `
                <div class="cart-item">
                    <div class="cart-item-details">
                        <h4>${item.name}</h4>
                        <span class="cart-item-price">$${item.price.toFixed(2)}</span>
                    </div>
                </div>
            `;
        }).join('');
    }
    
    document.getElementById('cart-total-price').textContent = `$${total.toFixed(2)}`;
}

async function handleCheckout() {
    if (state.cart.length === 0) return;
    const btn = document.getElementById('checkout-btn');
    const msg = document.getElementById('checkout-msg');
    btn.disabled = true;
    btn.innerHTML = '<div class="spinner"></div> Processing...';
    msg.textContent = '';
    
    try {
        const response = await fetch(`${API_BASE}/cart/checkout`, {
            method: 'POST',
            headers: { 'X-User': state.user }
        });
        
        if (response.ok) {
            msg.innerHTML = '<span style="color:#10b981">Order placed successfully!</span>';
            state.cart = [];
            loadCart();
            setTimeout(() => { document.getElementById('cart-modal').style.display = 'none'; }, 2000);
        } else {
            msg.innerHTML = '<span style="color:#ef4444">Checkout failed.</span>';
        }
    } catch (error) {
        msg.innerHTML = '<span style="color:#ef4444">Network error during checkout.</span>';
    } finally {
        btn.disabled = false;
        btn.textContent = 'Proceed to Checkout';
    }
}

async function loadUserOrders() {
    try {
        const response = await fetch(`${API_BASE}/orders`, { headers: { 'X-User': state.user } });
        if (response.ok) {
            const orders = await response.json();
            document.getElementById('user-orders-table').innerHTML = orders.map(o => `
                <tr>
                    <td>#${o.orderId}</td>
                    <td>${o.productId}</td>
                    <td>${o.quantity}</td>
                    <td>${new Date(o.orderDate).toLocaleString()}</td>
                    <td><span class="badge" style="background:#4ade80;color:#064e3b;padding:2px 6px;border-radius:4px;">${o.status}</span></td>
                </tr>
            `).join('');
        }
    } catch (e) { console.error(e); }
}
