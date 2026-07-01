const API_BASE = 'http://localhost:8080/system_web/api';
const state = {
    user: localStorage.getItem('techmartUser'),
    role: localStorage.getItem('techmartRole')
};

document.addEventListener('DOMContentLoaded', () => {
    if (!state.user || state.role !== 'ADMIN') { window.location.href = 'index.html'; return; }
    
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'index.html';
    });

    document.getElementById('add-product-form').addEventListener('submit', handleAddProduct);
    document.getElementById('edit-product-form').addEventListener('submit', handleEditProduct);
    loadProducts();
});

async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        if (response.ok) {
            const products = await response.json();
            document.getElementById('admin-products-table').innerHTML = products.map(p => `
                <tr>
                    <td>${p.id}</td>
                    <td>${p.name}</td>
                    <td>$${p.price.toFixed(2)}</td>
                    <td>${p.inventoryCount}</td>
                    <td>
                        <button class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-xs mr-2 transition" onclick='openEditModal(${JSON.stringify(p).replace(/'/g, "\\'")})'>Edit</button>
                        <button class="btn" style="background:#ef4444; padding:5px 10px;" onclick="deleteProduct(${p.id})">Delete</button>
                    </td>
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
            const createdProduct = await response.json();
            const newId = createdProduct.id;
            
            const f1 = document.getElementById('add-img1').files[0];
            const f2 = document.getElementById('add-img2').files[0];
            const f3 = document.getElementById('add-img3').files[0];

            if (f1 || f2 || f3) {
                msg.innerHTML = '<span style="color:#fbbf24;">Uploading images...</span>';
                const formData = new FormData();
                formData.append('productId', newId);
                if (f1) formData.append('image1', f1);
                if (f2) formData.append('image2', f2);
                if (f3) formData.append('image3', f3);

                const resImg = await fetch(`${API_BASE}/products/images`, {
                    method: 'POST',
                    headers: { 'X-Role': state.role },
                    body: formData
                });
                
                if (!resImg.ok) {
                    msg.innerHTML = '<span style="color:#ef4444;">Product added but image upload failed.</span>';
                    return;
                }
            }

            msg.innerHTML = '<span style="color:#10b981;">Product added successfully!</span>';
            form.reset();
            loadProducts();
            setTimeout(()=>msg.innerHTML='', 3000);
        } else {
            msg.innerHTML = '<span style="color:#ef4444;">Failed to add product</span>';
        }
    } catch (e) {
        msg.innerHTML = '<span style="color:red;">Network Error</span>';
    }
}

window.deleteProduct = async function(id) {
    if(!confirm('Are you sure you want to delete this product?')) return;
    try {
        const response = await fetch(`${API_BASE}/products/${id}`, {
            method: 'DELETE',
            headers: { 'X-Role': state.role }
        });
        if(response.ok) {
            loadProducts();
        } else {
            alert('Failed to delete product. It may be referenced in existing orders.');
        }
    } catch(e) { alert('Network Error'); }
}

window.openEditModal = function(product) {
    document.getElementById('edit-p-id').value = product.id;
    document.getElementById('edit-p-name').value = product.name;
    document.getElementById('edit-p-desc').value = product.description;
    document.getElementById('edit-p-price').value = product.price;
    document.getElementById('edit-p-inv').value = product.inventoryCount;
    document.getElementById('edit-msg').textContent = '';
    

    document.getElementById('img1').value = "";
    document.getElementById('img2').value = "";
    document.getElementById('img3').value = "";
    
    document.getElementById('edit-product-modal').classList.remove('hidden');
};

async function handleEditProduct(e) {
    e.preventDefault();
    const id = document.getElementById('edit-p-id').value;
    const msg = document.getElementById('edit-msg');
    msg.textContent = 'Saving changes...';
    msg.style.color = 'white';

    const product = {
        name: document.getElementById('edit-p-name').value,
        description: document.getElementById('edit-p-desc').value,
        price: parseFloat(document.getElementById('edit-p-price').value),
        inventoryCount: parseInt(document.getElementById('edit-p-inv').value)
    };

    try {

        const resText = await fetch(`${API_BASE}/products/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'X-Role': state.role },
            body: JSON.stringify(product)
        });

        if (!resText.ok) throw new Error("Failed to update product details");


        const f1 = document.getElementById('img1').files[0];
        const f2 = document.getElementById('img2').files[0];
        const f3 = document.getElementById('img3').files[0];

        if (f1 || f2 || f3) {
            msg.textContent = 'Uploading images...';
            const formData = new FormData();
            formData.append('productId', id);
            if (f1) formData.append('image1', f1);
            if (f2) formData.append('image2', f2);
            if (f3) formData.append('image3', f3);

            const resImg = await fetch(`${API_BASE}/products/images`, {
                method: 'POST',
                headers: { 'X-Role': state.role },
                body: formData
            });
            if (!resImg.ok) throw new Error("Failed to upload images");
        }

        msg.textContent = 'Product updated successfully!';
        msg.style.color = '#10b981';
        loadProducts();
        setTimeout(() => { document.getElementById('edit-product-modal').classList.add('hidden'); }, 1500);

    } catch(err) {
        msg.textContent = err.message || 'Network Error';
        msg.style.color = '#ef4444';
    }
}
