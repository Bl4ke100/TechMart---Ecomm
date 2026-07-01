const API_BASE = 'http://localhost:8080/system_web/api';
const state = {
    user: localStorage.getItem('techmartUser'),
    role: localStorage.getItem('techmartRole')
};

document.addEventListener('DOMContentLoaded', () => {
    if (!state.user || state.role !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    document.getElementById('user-greeting').textContent = `Welcome, Admin`;
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'index.html';
    });

    loadDashboardMetrics();
    document.getElementById('export-csv-btn').addEventListener('click', exportToCSV);
});

let allOrders = [];

async function loadDashboardMetrics() {
    try {
        const [ordersRes, usersRes, productsRes] = await Promise.all([
            fetch(`${API_BASE}/orders/all`, { headers: { 'X-Role': state.role } }),
            fetch(`${API_BASE}/users`, { headers: { 'X-Role': state.role } }),
            fetch(`${API_BASE}/products`)
        ]);

        if (ordersRes.ok) {
            allOrders = await ordersRes.json();
            const el = document.getElementById('total-orders');
            el.textContent = allOrders.length;
            el.classList.remove('animate-pulse');
            
            const recentOrders = allOrders.slice(-10).reverse();
            document.getElementById('recent-orders-list').innerHTML = recentOrders.map(o => `
                <tr class="hover:bg-gray-700/30 transition">
                    <td class="py-3 px-4 text-gray-300 font-medium">#${o.orderId}</td>
                    <td class="py-3 px-4 text-blue-400 font-medium">@${o.username}</td>
                    <td class="py-3 px-4 text-gray-300">${o.productId}</td>
                    <td class="py-3 px-4 text-gray-300">${o.quantity}</td>
                    <td class="py-3 px-4 text-gray-500 text-xs">${new Date(o.orderDate).toLocaleString()}</td>
                    <td class="py-3 px-4 text-right">
                        <span class="bg-green-500/10 text-green-400 px-2.5 py-1 rounded-full text-xs font-semibold">${o.status}</span>
                    </td>
                </tr>
            `).join('');
        }
        
        if (usersRes.ok) {
            const users = await usersRes.json();
            const el = document.getElementById('total-users');
            el.textContent = users.length;
            el.classList.remove('animate-pulse');
        }
        
        if (productsRes.ok) {
            const products = await productsRes.json();
            const el = document.getElementById('total-products');
            el.textContent = products.length;
            el.classList.remove('animate-pulse');
        }
    } catch (e) { console.error(e); }
}

function exportToCSV() {
    if (allOrders.length === 0) {
        alert("No orders to export!");
        return;
    }
    const headers = ["Order ID", "Product ID", "Username", "Quantity", "Date", "Status"];
    const rows = allOrders.map(o => [o.orderId, o.productId, o.username, o.quantity, new Date(o.orderDate).toISOString(), o.status]);
    
    let csvContent = "data:text/csv;charset=utf-8," + headers.join(",") + "\n" + rows.map(e => e.join(",")).join("\n");
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "orders_report.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
