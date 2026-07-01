document.addEventListener('DOMContentLoaded', () => {
    requireRole('USER');
    setupShared();
    loadCart();
    
    loadProfileDetails();
    loadUserOrders();
    
    document.getElementById('profile-form').addEventListener('submit', handleProfileUpdate);
});

async function loadProfileDetails() {
    try {
        const response = await fetch(`${API_BASE}/users/self/${state.user}`, { headers: { 'X-User': state.user } });
        if (response.ok) {
            const data = await response.json();
            document.getElementById('prof-username').value = data.username;
            document.getElementById('prof-email').value = data.email;
        }
    } catch (e) { console.error(e); }
}

async function handleProfileUpdate(e) {
    e.preventDefault();
    const email = document.getElementById('prof-email').value;
    const password = document.getElementById('prof-password').value;
    const msg = document.getElementById('prof-msg');
    
    try {
        const response = await fetch(`${API_BASE}/users/self/${state.user}`, {
            method: 'PUT',
            headers: { 'X-User': state.user, 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (response.ok) {
            msg.innerHTML = '<span style="color:#10b981">Profile updated successfully!</span>';
            document.getElementById('prof-password').value = '';
        } else {
            msg.innerHTML = '<span style="color:#ef4444">Failed to update profile.</span>';
        }
    } catch (e) { msg.innerHTML = '<span style="color:#ef4444">Network error.</span>'; }
}

window.deleteAccount = async function() {
    if (!confirm('Are you absolutely sure you want to delete your account? This cannot be undone.')) return;
    
    try {
        const response = await fetch(`${API_BASE}/users/self/${state.user}`, {
            method: 'DELETE',
            headers: { 'X-User': state.user }
        });
        if (response.ok) {
            alert('Account deleted. Goodbye!');
            localStorage.clear();
            window.location.href = 'index.html';
        } else {
            alert('Failed to delete account.');
        }
    } catch (e) { alert('Network error.'); }
}
