function initAuth() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const xhr = new XMLHttpRequest();
            xhr.open('GET', '/api/auth/logout', true);
            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    window.location.href = 'index.html';
                }
            };
            xhr.send();
        });
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const messageDiv = document.getElementById('message');

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
                });
                const data = await response.json();
                if (data.success) {
                    window.location.href = '/main.html';
                } else {
                    messageDiv.className = 'message error';
                    messageDiv.textContent = data.error;
                    messageDiv.style.display = 'block';
                }
            } catch (error) {
                messageDiv.className = 'message error';
                messageDiv.textContent = 'Ошибка сервера';
                messageDiv.style.display = 'block';
            }
        });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const messageDiv = document.getElementById('message');

            if (password !== confirmPassword) {
                messageDiv.className = 'message error';
                messageDiv.textContent = 'Пароли не совпадают';
                messageDiv.style.display = 'block';
                return;
            }

            const firstName = document.getElementById('firstName').value;
            const lastName = document.getElementById('lastName').value;
            const email = document.getElementById('email').value;

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}&confirmPassword=${encodeURIComponent(confirmPassword)}`
                });
                const data = await response.json();
                if (data.success) {
                    messageDiv.className = 'message success';
                    messageDiv.textContent = 'Регистрация успешна! Проверьте email.';
                    messageDiv.style.display = 'block';
                    setTimeout(() => window.location.href = '/login.html', 3000);
                } else {
                    messageDiv.className = 'message error';
                    messageDiv.textContent = data.error;
                    messageDiv.style.display = 'block';
                }
            } catch (error) {
                messageDiv.className = 'message error';
                messageDiv.textContent = 'Ошибка сервера';
                messageDiv.style.display = 'block';
            }
        });
    }
}