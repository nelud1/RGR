let currentUserRole = '';
let currentUserId = null;

async function searchUsers(event) {
    event.preventDefault();

    const firstName = document.getElementById('searchFirstName').value;
    const lastName = document.getElementById('searchLastName').value;
    const city = document.getElementById('searchCity').value;
    const ageFrom = document.getElementById('searchAgeFrom').value;
    const ageTo = document.getElementById('searchAgeTo').value;

    let url = `/api/users/search?firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}&city=${encodeURIComponent(city)}`;
    if (ageFrom) url += `&ageFrom=${ageFrom}`;
    if (ageTo) url += `&ageTo=${ageTo}`;

    try {
        const response = await fetch(url);
        const users = await response.json();
        const container = document.getElementById('userResults');
        if (!container) return;
        container.innerHTML = '';

        if (users.length === 0) {
            container.innerHTML = '<p>Пользователи не найдены</p>';
        } else {
            for (const user of users) {
                renderUserCard(user);
            }
        }
    } catch (error) {
        if (document.getElementById('userResults')) {
            document.getElementById('userResults').innerHTML = '<p>Ошибка поиска</p>';
        }
    }
}

async function searchNews(event) {
    event.preventDefault();

    const keyword = document.getElementById('searchKeyword').value;

    try {
        const response = await fetch(`/api/news/search?keyword=${encodeURIComponent(keyword)}`);
        const newsList = await response.json();
        const container = document.getElementById('newsResults');
        if (!container) return;
        container.innerHTML = '';

        if (newsList.length === 0) {
            container.innerHTML = '<p>Новости не найдены</p>';
        } else {
            for (const news of newsList) {
                const html = `
                    <div class="news-item">
                        <h4>${escapeHtml(news.title)}</h4>
                        <p>${escapeHtml(news.content)}</p>
                        <small>Автор: ${escapeHtml(news.authorName)} | ${formatDate(news.createdAt)}</small>
                    </div>
                `;
                container.insertAdjacentHTML('beforeend', html);
            }
        }
    } catch (error) {
        if (document.getElementById('newsResults')) {
            document.getElementById('newsResults').innerHTML = '<p>Ошибка поиска новостей</p>';
        }
    }
}

function renderUserCard(user) {
    let ageHtml = '';
    if (user.age && user.age > 0) {
        ageHtml = `<p>Возраст: ${user.age}</p>`;
    }

    let adminButtons = '';
    if (currentUserRole === 'ADMIN' && user.id !== currentUserId) {
        if (user.role === 'MODERATOR') {
            adminButtons = `<button onclick="removeModerator(${user.id})" class="btn btn-small">Снять модератора</button>`;
        } else if (user.role === 'USER') {
            adminButtons = `<button onclick="makeModerator(${user.id})" class="btn btn-small">Назначить модератором</button>`;
        }
        adminButtons += `<button onclick="deleteUser(${user.id})" class="btn btn-danger btn-small">Удалить пользователя</button>`;
    }

    let roleText = '';
    if (user.role === 'ADMIN') roleText = 'Администратор';
    else if (user.role === 'MODERATOR') roleText = 'Модератор';
    else roleText = 'Пользователь';

    const cityText = user.city ? user.city : 'Не указан';

    const html = `
        <div class="user-card" id="user-${user.id}">
            <h4>${escapeHtml(user.firstName)} ${escapeHtml(user.lastName)}</h4>
            <p>Email: ${escapeHtml(user.email)}</p>
            <p>Город: ${escapeHtml(cityText)}</p>
            ${ageHtml}
            <p>Роль: ${roleText}</p>
            <button onclick="sendFriendRequest(${user.id})" class="btn btn-small">Добавить в друзья</button>
            <button onclick="openMessageWindow(${user.id}, '${escapeHtml(user.firstName)} ${escapeHtml(user.lastName)}')" class="btn btn-small">Написать сообщение</button>
            ${adminButtons}
        </div>
    `;

    const container = document.getElementById('userResults');
    if (container) container.insertAdjacentHTML('beforeend', html);
}

async function makeModerator(userId) {
    try {
        const response = await fetch('/api/admin/make-moderator', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `userId=${userId}`
        });
        const data = await response.json();
        if (data.success) {
            alert('Пользователь назначен модератором');
            document.getElementById('userSearchForm').dispatchEvent(new Event('submit'));
        } else {
            alert(data.error || 'Ошибка');
        }
    } catch (error) {
        alert('Ошибка сервера');
    }
}

async function removeModerator(userId) {
    try {
        const response = await fetch('/api/admin/remove-moderator', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `userId=${userId}`
        });
        const data = await response.json();
        if (data.success) {
            alert('Роль модератора снята');
            document.getElementById('userSearchForm').dispatchEvent(new Event('submit'));
        } else {
            alert(data.error || 'Ошибка');
        }
    } catch (error) {
        alert('Ошибка сервера');
    }
}

async function deleteUser(userId) {
    if (confirm('Удалить пользователя? Все его новости, комментарии и сообщения будут удалены.')) {
        try {
            const response = await fetch('/api/admin/delete-user', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `userId=${userId}`
            });
            const data = await response.json();
            if (data.success) {
                alert('Пользователь удален');
                document.getElementById('userSearchForm').dispatchEvent(new Event('submit'));
            } else {
                alert(data.error || 'Ошибка');
            }
        } catch (error) {
            alert('Ошибка сервера');
        }
    }
}

function initSearch() {
    if (!document.getElementById('userSearchForm')) {
        return;
    }

    const usersTabBtn = document.getElementById('usersTabBtn');
    const newsTabBtn = document.getElementById('newsTabBtn');
    const usersSearch = document.getElementById('usersSearch');
    const newsSearch = document.getElementById('newsSearch');

    if (usersTabBtn && newsTabBtn) {
        usersTabBtn.addEventListener('click', () => {
            usersTabBtn.classList.add('active');
            newsTabBtn.classList.remove('active');
            if (usersSearch) usersSearch.classList.add('active');
            if (newsSearch) newsSearch.classList.remove('active');
        });

        newsTabBtn.addEventListener('click', () => {
            newsTabBtn.classList.add('active');
            usersTabBtn.classList.remove('active');
            if (newsSearch) newsSearch.classList.add('active');
            if (usersSearch) usersSearch.classList.remove('active');
        });
    }

    const userSearchForm = document.getElementById('userSearchForm');
    if (userSearchForm) {
        userSearchForm.addEventListener('submit', searchUsers);
    }

    const newsSearchForm = document.getElementById('newsSearchForm');
    if (newsSearchForm) {
        newsSearchForm.addEventListener('submit', searchNews);
    }

    fetch('/api/auth/me')
        .then(response => response.json())
        .then(data => {
            if (!data.authenticated) {
                window.location.href = 'login.html';
            }
            currentUserRole = data.role || 'USER';
            currentUserId = data.userId;
        });
}