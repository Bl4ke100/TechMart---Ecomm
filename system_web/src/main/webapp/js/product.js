const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get('id');
let currentProduct = null;

document.addEventListener('DOMContentLoaded', () => {
    requireRole('USER');
    setupShared();
    loadCart();
    
    if (productId) {
        loadSingleProduct(productId);
    } else {
        document.getElementById('product-container').innerHTML = '<h2 style="color:white; grid-column: span 2; text-align:center;">Product not found.</h2>';
    }
});

async function loadSingleProduct(id) {
    try {
        const response = await fetch(`${API_BASE}/products/${id}`);
        if (response.ok) {
            currentProduct = await response.json();
            renderProduct();
        } else {
            document.getElementById('product-container').innerHTML = '<h2 style="color:white; grid-column: span 2; text-align:center;">Product not found.</h2>';
        }
    } catch (e) {
        console.error(e);
    }
}

function renderProduct() {
    const container = document.getElementById('product-container');
    const imgUrl = `${API_BASE.replace('/api', '')}/api/images/${currentProduct.id}/`;
    
    container.innerHTML = `
        <div class="glass-panel" style="padding: 1rem;">
            <div class="carousel">
                <img src="${imgUrl}1.jpg" onerror="this.src='https://placehold.co/800x600?text=Image+1'" alt="Image 1">
                <img src="${imgUrl}2.jpg" onerror="this.src='https://placehold.co/800x600?text=Image+2'" alt="Image 2">
                <img src="${imgUrl}3.jpg" onerror="this.src='https://placehold.co/800x600?text=Image+3'" alt="Image 3">
            </div>
            <p style="text-align:center; color:#94a3b8; font-size: 0.9rem;">Scroll horizontally to view more images</p>
        </div>
        <div class="glass-panel" style="display: flex; flex-direction: column; justify-content: center;">
            <h1 style="font-size: 2.5rem; margin-bottom: 0.5rem; color: #fff;">${currentProduct.name}</h1>
            <h2 style="color: #4ade80; margin-bottom: 1.5rem; font-size: 2rem;">$${currentProduct.price.toFixed(2)}</h2>
            <p style="color: #cbd5e1; font-size: 1.1rem; line-height: 1.6; margin-bottom: 2rem;">${currentProduct.description}</p>
            
            <p style="color: ${currentProduct.inventoryCount > 0 ? '#10b981' : '#ef4444'}; font-weight:bold; margin-bottom: 1rem;">
                ${currentProduct.inventoryCount > 0 ? currentProduct.inventoryCount + ' in stock' : 'Out of Stock'}
            </p>
            
            <div style="display:flex; gap: 1rem; align-items:center; margin-bottom: 1rem;">
                <label for="qty" style="color:white; font-weight:bold;">Quantity:</label>
                <input type="number" id="qty" value="1" min="1" max="${currentProduct.inventoryCount}" style="width: 80px; padding: 10px; border-radius: 5px;" ${currentProduct.inventoryCount === 0 ? 'disabled' : ''}>
            </div>
            
            <div style="display:flex; gap: 1rem; margin-top: 1rem;">
                <button class="btn btn-primary" onclick="addToCartQty()" style="flex: 1;" ${currentProduct.inventoryCount === 0 ? 'disabled' : ''}>Add to Cart</button>
                <button class="btn" style="flex: 1; background: #ff3366; color: white;" onclick="buyNow()" ${currentProduct.inventoryCount === 0 ? 'disabled' : ''}>Buy Now</button>
            </div>
            <div id="action-msg" style="margin-top:1rem; text-align:center;"></div>
        </div>
    `;
}

window.addToCartQty = async function() {
    const qty = document.getElementById('qty').value;
    try {
        const response = await fetch(`${API_BASE}/cart/add?quantity=${qty}`, {
            method: 'POST',
            headers: { 'X-User': state.user, 'Content-Type': 'application/json' },
            body: JSON.stringify(currentProduct)
        });
        if (response.ok) {
            loadCart();
            const msg = document.getElementById('action-msg');
            msg.innerHTML = '<span style="color:#10b981">Added to Cart!</span>';
            setTimeout(() => msg.innerHTML='', 2000);
        }
    } catch (e) { console.error(e); }
}

window.buyNow = async function() {
    await addToCartQty();
    window.location.href = 'checkout.html';
}
