const API_BASE = 'http://localhost:8080/system_web/api';
const state = {
    user: localStorage.getItem('techmartUser'),
    role: localStorage.getItem('techmartRole'),
    cart: []
};

function requireRole(role) {
    if (!state.user || state.role !== role) {
        window.location.href = 'index.html';
    }
}

function setupShared() {

    const greeting = document.getElementById('user-greeting');
    if (greeting && state.user) {
        greeting.innerHTML = `Welcome, ${state.user} ${state.role === 'ADMIN' ? '<span class="badge" style="background:#ff3366;color:white;padding:2px 6px;border-radius:4px;font-size:12px;margin-left:8px;">Admin</span>' : ''}`;
    }


    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.clear();
            window.location.href = 'index.html';
        });
    }


    const cartBtn = document.getElementById('cart-btn');
    const cartModal = document.getElementById('cart-modal');
    if (cartBtn && cartModal) {
        cartBtn.addEventListener('click', () => { cartModal.style.display = 'flex'; loadCart(); });
        const closeCart = cartModal.querySelector('.close-btn');
        if (closeCart) closeCart.addEventListener('click', () => cartModal.style.display = 'none');
    }


    const profileBtn = document.getElementById('nav-profile-btn');
    if (profileBtn) {
        profileBtn.addEventListener('click', () => {
            window.location.href = 'profile.html';
        });
    }


    const checkoutBtn = document.getElementById('checkout-btn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', () => {
            window.location.href = 'checkout.html';
        });
    }


    window.addEventListener('click', (e) => {
        if (cartModal && e.target === cartModal) cartModal.style.display = 'none';
    });
}


async function loadCart() {
    if (!state.user) return;
    try {
        const response = await fetch(`${API_BASE}/cart`, { headers: { 'X-User': state.user } });
        if (response.ok) {
            state.cart = await response.json();
            const countEl = document.getElementById('cart-count');
            if (countEl) countEl.textContent = state.cart.length;
            renderCart();
        }
    } catch (e) { console.error(e); }
}

function renderCart() {
    const list = document.getElementById('cart-items-list');
    const totalEl = document.getElementById('cart-total-price');
    if (!list || !totalEl) return;

    let total = 0;
    if (state.cart.length === 0) {
        list.innerHTML = '<p class="text-center text-gray-400 py-8">Your cart is empty.</p>';
    } else {
        list.innerHTML = state.cart.map(item => {
            total += item.price;
            return `
                <div class="flex justify-between items-center p-3 bg-gray-700/30 rounded-lg border border-gray-700">
                    <div>
                        <h4 class="text-white font-semibold">${item.name}</h4>
                    </div>
                    <div class="flex items-center gap-4">
                        <span class="text-green-400 font-bold">$${item.price.toFixed(2)}</span>
                        <button onclick="removeFromCart(${item.id})" class="text-red-400 hover:text-red-300 text-xl font-bold transition">&times;</button>
                    </div>
                </div>
            `;
        }).join('');
    }
    totalEl.textContent = `$${total.toFixed(2)}`;
}

window.removeFromCart = async function(productId) {
    if (!state.user) return;
    try {
        const response = await fetch(`${API_BASE}/cart/${productId}`, {
            method: 'DELETE',
            headers: { 'X-User': state.user }
        });
        if (response.ok) {
            loadCart(); // Refresh cart
        }
    } catch (e) {
        console.error("Failed to remove item", e);
    }
}

async function loadUserOrders() {
    if (!state.user) return;
    try {
        const response = await fetch(`${API_BASE}/orders`, { headers: { 'X-User': state.user } });
        if (response.ok) {
            const orders = await response.json();
            const table = document.getElementById('user-orders-table');
            if (table) {
                table.innerHTML = orders.map(o => `
                    <tr class="border-b border-gray-700 hover:bg-gray-700/30 transition">
                        <td class="py-3 px-4 text-gray-300 font-medium">#${o.orderId}</td>
                        <td class="py-3 px-4 text-blue-400">Prod #${o.productId}</td>
                        <td class="py-3 px-4 text-gray-300">${o.quantity}</td>
                        <td class="py-3 px-4 text-gray-400 text-sm">${new Date(o.orderDate.replace(/\[.*?\]/, '')).toLocaleDateString()}</td>
                        <td class="py-3 px-4"><span class="bg-green-500/10 text-green-400 px-2.5 py-1 rounded-full text-xs font-semibold uppercase">${o.status}</span></td>
                    </tr>
                `).join('');
            }
        }
    } catch (e) { console.error(e); }
}
