document.addEventListener('DOMContentLoaded', () => {
    requireRole('USER');
    setupShared();
    

    const checkoutForm = document.getElementById('checkout-form');
    if (checkoutForm) checkoutForm.addEventListener('submit', handleCheckoutProcess);

    loadCheckoutCart();
});

async function loadCheckoutCart() {
    try {
        const response = await fetch(`${API_BASE}/cart`, { headers: { 'X-User': state.user } });
        if (response.ok) {
            const cartItems = await response.json();
            const list = document.getElementById('checkout-items');
            let total = 0;
            if (cartItems.length === 0) {
                list.innerHTML = '<p>Your cart is empty.</p>';
            } else {
                list.innerHTML = cartItems.map(item => {
                    total += item.price;
                    return `<div style="display:flex; justify-content:space-between; margin-bottom: 0.5rem;">
                        <span>${item.name}</span>
                        <span>$${item.price.toFixed(2)}</span>
                    </div>`;
                }).join('');
            }
            document.getElementById('checkout-total').textContent = `$${total.toFixed(2)}`;
            if (cartItems.length === 0) {
                document.getElementById('place-order-btn').disabled = true;
            }
        }
    } catch (e) { console.error(e); }
}

async function handleCheckoutProcess(e) {
    e.preventDefault();
    

    const modal = document.getElementById('payment-modal');
    const title = document.getElementById('pay-title');
    const status = document.getElementById('pay-status');
    const spinner = document.getElementById('pay-spinner');
    
    modal.style.display = 'flex';
    title.textContent = 'Secure Payment Gateway';
    status.textContent = 'Verifying Credit Card Details...';
    
    await new Promise(r => setTimeout(r, 1500));
    status.textContent = 'Processing Transaction...';
    
    await new Promise(r => setTimeout(r, 1500));
    
    try {
        const response = await fetch(`${API_BASE}/cart/checkout`, {
            method: 'POST',
            headers: { 'X-User': state.user }
        });
        
        if (response.ok) {
            spinner.style.display = 'none';
            title.textContent = 'Payment Successful!';
            status.innerHTML = '<span style="color:#10b981; font-size: 1.2rem;">Transaction Approved. Redirecting...</span>';
            setTimeout(() => { window.location.href = 'home.html'; }, 2000);
        } else {
            spinner.style.display = 'none';
            title.textContent = 'Payment Declined';
            status.innerHTML = '<span style="color:#ef4444">Transaction failed. Please try again.</span>';
            setTimeout(() => { modal.style.display = 'none'; spinner.style.display = 'block'; }, 3000);
        }
    } catch (error) {
        spinner.style.display = 'none';
        title.textContent = 'Connection Error';
        status.innerHTML = '<span style="color:#ef4444">Network error during payment.</span>';
        setTimeout(() => { modal.style.display = 'none'; spinner.style.display = 'block'; }, 3000);
    }
}
