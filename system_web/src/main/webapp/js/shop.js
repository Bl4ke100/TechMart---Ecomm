let products = [];

document.addEventListener('DOMContentLoaded', () => {
    requireRole('USER');
    setupShared();
    loadCart();
    loadProducts();

    const searchInput = document.getElementById('search-input');
    const sortSelect = document.getElementById('sort-select');
    if (searchInput) searchInput.addEventListener('input', renderProducts);
    if (sortSelect) sortSelect.addEventListener('change', renderProducts);
});

async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        if (response.ok) {
            products = await response.json();
            renderProducts();
        }
    } catch (e) { console.error('Failed to load products', e); }
}

function renderProducts() {
    const grid = document.getElementById('products-grid');
    const searchInput = document.getElementById('search-input');
    const sortSelect = document.getElementById('sort-select');
    
    if (!grid) return;

    let filtered = [...products];

    if (searchInput && searchInput.value) {
        const term = searchInput.value.toLowerCase();
        filtered = filtered.filter(p => p.name.toLowerCase().includes(term) || p.description.toLowerCase().includes(term));
    }

    if (sortSelect) {
        if (sortSelect.value === 'price-asc') filtered.sort((a, b) => a.price - b.price);
        else if (sortSelect.value === 'price-desc') filtered.sort((a, b) => b.price - a.price);
        else if (sortSelect.value === 'name-asc') filtered.sort((a, b) => a.name.localeCompare(b.name));
    }

    if (filtered.length === 0) {
        grid.innerHTML = '<p class="text-gray-400 text-center col-span-full py-12">No products found.</p>';
        return;
    }

    grid.innerHTML = filtered.map(p => `
        <div class="bg-gray-800 rounded-2xl overflow-hidden shadow-lg border border-gray-700 hover:shadow-2xl transition-all duration-300 cursor-pointer group flex flex-col h-full hover:-translate-y-1" onclick="window.location.href='product.html?id=${p.id}'">
            <div class="h-48 w-full bg-cover bg-center group-hover:scale-105 transition duration-500 bg-white" style="background-image: url('${API_BASE.replace('/api', '')}/api/images/${p.id}/1.jpg'), url('https://placehold.co/400x300?text=${encodeURIComponent(p.name)}')"></div>
            <div class="p-6 flex flex-col flex-grow">
                <h3 class="text-xl font-bold text-white mb-2">${p.name}</h3>
                <p class="text-gray-400 text-sm mb-4 line-clamp-3 flex-grow">${p.description}</p>
                <div class="flex justify-between items-center mb-6">
                    <span class="text-2xl font-bold text-green-400">$${p.price.toFixed(2)}</span>
                    <span class="text-xs font-semibold px-2 py-1 rounded-full ${p.inventoryCount < 10 ? 'bg-red-500/20 text-red-400' : 'bg-gray-700 text-gray-300'}">${p.inventoryCount} left</span>
                </div>
                <button class="w-full bg-white text-gray-900 hover:bg-gray-200 font-bold py-3 rounded-lg transition mt-auto" onclick="event.stopPropagation(); addToCart(event, ${p.id})">Add to Cart</button>
            </div>
        </div>
    `).join('');
}

window.addToCart = async function(e, productId) {
    try {
        const product = products.find(p => p.id === productId);
        const response = await fetch(`${API_BASE}/cart/add?quantity=1`, {
            method: 'POST',
            headers: { 'X-User': state.user, 'Content-Type': 'application/json' },
            body: JSON.stringify(product)
        });
        if (response.ok) {
            loadCart();
            if (e && e.target) {
                const btn = e.target;
                btn.textContent = 'Added!';
                btn.style.background = '#10b981';
                setTimeout(() => { btn.textContent = 'Add to Cart'; btn.style.background = ''; }, 1000);
            }
        }
    } catch (e) { console.error(e); }
}
