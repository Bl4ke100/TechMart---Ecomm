const API_BASE = 'http://localhost:8080/system_web/api';
let state = {
    user: localStorage.getItem('techmartUser'),
    role: localStorage.getItem('techmartRole')
};

document.addEventListener('DOMContentLoaded', () => {
    if (!state.user || state.role !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    document.getElementById('user-greeting').textContent = `Welcome, ${state.user}`;

    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'index.html';
    });

    document.getElementById('add-product-form').addEventListener('submit', handleAddProduct);

    loadAdminOrders();
});

async function loadAdminOrders() {
    try {
        const response = await fetch(`${API_BASE}/orders/all`, { headers: { 'X-Role': state.role } });
        if (response.ok) {
            const orders = await response.json();
            document.getElementById('admin-orders-table').innerHTML = orders.map(o => `
                <tr>
                    <td>#${o.orderId}</td>
                    <td>${o.username}</td>
                    <td>${o.productId}</td>
                    <td>${o.quantity}</td>
                    <td>${o.status}</td>
                </tr>
            `).join('');
        }
    } catch (e) { console.error(e); }
}

async function handleAddProduct(e) {
    e.preventDefault();
    const msg = document.getElementById('add-p-msg');
    const form = document.getElementById('add-product-form');
    
    const product = {
        name: document.getElementById('add-p-name').value,
        description: document.getElementById('add-p-desc').value,
        price: parseFloat(document.getElementById('add-p-price').value),
        inventoryCount: parseInt(document.getElementById('add-p-inv').value)
    };
    
    try {
        const response = await fetch(`${API_BASE}/products`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-Role': state.role },
            body: JSON.stringify(product)
        });
        
        if (response.ok) {
            msg.innerHTML = '<span style="color:green;">Product added!</span>';
            form.reset();
        } else {
            msg.innerHTML = '<span style="color:red;">Failed to add product</span>';
        }
    } catch (e) {
        msg.innerHTML = '<span style="color:red;">Network Error</span>';
    }
}
