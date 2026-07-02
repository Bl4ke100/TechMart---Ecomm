const API_BASE = 'http://localhost:8080/system_web/api';

document.addEventListener('DOMContentLoaded', () => {

    const role = localStorage.getItem('techmartRole');
    if (role === 'ADMIN') window.location.href = 'admin_dashboard.html';
    else if (role === 'USER') window.location.href = 'home.html';

    const loginForm = document.getElementById('login-form');
    const regForm = document.getElementById('register-form');
    const authError = document.getElementById('auth-error');
    const regError = document.getElementById('reg-error');
    
    document.getElementById('show-register').addEventListener('click', (e) => {
        e.preventDefault();
        loginForm.style.display = 'none';
        regForm.style.display = 'block';
    });

    document.getElementById('show-login').addEventListener('click', (e) => {
        e.preventDefault();
        regForm.style.display = 'none';
        loginForm.style.display = 'block';
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        authError.textContent = '';
        authError.classList.add('hidden');
        
        try {
            const response = await fetch(`${API_BASE}/auth`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            
            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('techmartUser', data.username);
                localStorage.setItem('techmartRole', data.role);
                window.location.href = data.role === 'ADMIN' ? 'admin_dashboard.html' : 'home.html';
            } else {
                try {
                    const errData = await response.json();
                    authError.textContent = errData.error || 'Invalid credentials.';
                    authError.classList.remove('hidden');
                } catch(e) {
                    authError.textContent = 'Invalid credentials.';
                    authError.classList.remove('hidden');
                }
            }
        } catch (error) {
            authError.textContent = 'Network error. Make sure server is running.';
            authError.classList.remove('hidden');
        }
    });

    regForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('reg-username').value;
        const email = document.getElementById('reg-email').value;
        const password = document.getElementById('reg-password').value;
        
        regError.textContent = '';
        regError.style.color = 'red';
        regError.classList.add('hidden');
        
        try {
            const response = await fetch(`${API_BASE}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });
            
            if (response.ok) {
                regError.style.color = 'green';
                regError.textContent = 'Success! Please login.';
                regError.classList.remove('hidden');
                setTimeout(() => document.getElementById('show-login').click(), 1500);
            } else {
                regError.textContent = 'Username already exists or invalid data.';
                regError.classList.remove('hidden');
            }
        } catch (error) {
            regError.textContent = 'Network error.';
            regError.classList.remove('hidden');
        }
    });
});
