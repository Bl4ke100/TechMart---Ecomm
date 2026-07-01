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

    loadUsers();
});

async function loadUsers() {
    try {
        const response = await fetch(`${API_BASE}/users`, { headers: { 'X-Role': state.role } });
        if (response.ok) {
            const users = await response.json();
            document.getElementById('admin-users-table').innerHTML = users.map(u => `
                <tr>
                    <td>${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.email}</td>
                    <td><span class="badge" style="background:${u.role==='ADMIN'?'#ff3366':'#3b82f6'};color:white;padding:2px 6px;border-radius:4px;">${u.role}</span></td>
                    <td>
                        ${u.role !== 'ADMIN' ? `<button class="btn" style="background:#ef4444; padding:5px 10px;" onclick="deleteUser(${u.id})">Delete</button>` : ''}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) { console.error(e); }
}

window.deleteUser = async function(id) {
    if(!confirm('Are you sure you want to delete this user?')) return;
    try {
        const response = await fetch(`${API_BASE}/users/${id}`, {
            method: 'DELETE',
            headers: { 'X-Role': state.role }
        });
        if(response.ok) {
            loadUsers();
        } else {
            alert('Failed to delete user.');
        }
    } catch(e) { alert('Network Error'); }
}
