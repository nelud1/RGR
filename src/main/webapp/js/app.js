function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU') + ' ' + date.toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'});
}

function escapeHtml(text) {
    if (!text) return '';
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function showMessage(text, type, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.textContent = text;
    container.className = 'message ' + type;
    container.style.display = 'block';
    setTimeout(() => {
        container.style.display = 'none';
    }, 5000);
}

function logout() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/api/auth/logout', true);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            window.location.href = 'index.html';
        }
    };
    xhr.send();
}

function loadNewsFeed() {
    fetch('/api/news/feed')
        .then(response => response.json())
        .then(newsList => {
            const container = document.getElementById('newsFeed');
            if (!container) return;
            container.innerHTML = '';
            if (newsList.length === 0) {
                container.innerHTML = '<p>Нет новостей</p>';
            } else {
                for (let i = 0; i < newsList.length; i++) {
                    renderNewsItem(newsList[i]);
                }
            }
        });
}

function renderNewsItem(news) {
    const container = document.getElementById('newsFeed');
    if (!container) return;

    let newsHtml = '<div class="news-item" id="news-' + news.id + '">' +
        '<h4>' + escapeHtml(news.title) + '</h4>' +
        '<p>' + escapeHtml(news.content) + '</p>';

    if (news.imagePath && news.imagePath !== 'null') {
        newsHtml += '<img src="' + news.imagePath + '" style="max-width:100%; max-height:300px; margin:10px 0;">';
    }

    newsHtml += '<small>Автор: ' + escapeHtml(news.authorName) + ' | ' + formatDate(news.createdAt) + '</small>' +
        '<div class="news-actions">' +
            '<button onclick="likeNews(' + news.id + ')" class="like-btn">👍 ' + (news.likesCount || 0) + '</button>' +
            '<button onclick="dislikeNews(' + news.id + ')" class="dislike-btn">👎 ' + (news.dislikesCount || 0) + '</button>';

    if (news.canDelete) {
        newsHtml += '<button onclick="deleteNews(' + news.id + ')" class="btn btn-danger btn-small">Удалить</button>';
    }

    newsHtml += '</div>' +
        '<div class="comments-section">' +
            '<div id="comments-' + news.id + '"></div>' +
            '<div class="comment-form">' +
                '<input type="text" id="comment-input-' + news.id + '" placeholder="Написать комментарий..." maxlength="100">' +
                '<button onclick="addComment(' + news.id + ')" class="btn btn-primary btn-small">Отправить</button>' +
            '</div>' +
        '</div>' +
    '</div>';

    container.insertAdjacentHTML('beforeend', newsHtml);
    loadComments(news.id);
}

function loadComments(newsId) {
    fetch('/api/news/' + newsId + '/comments')
        .then(response => response.json())
        .then(comments => {
            const container = document.getElementById('comments-' + newsId);
            if (!container) return;
            container.innerHTML = '';
            for (let i = 0; i < comments.length; i++) {
                const comment = comments[i];
                const commentHtml = '<div class="comment">' +
                    '<strong>' + escapeHtml(comment.authorName) + ':</strong> ' + escapeHtml(comment.content) +
                    '<br><small>' + formatDate(comment.createdAt) + '</small>' +
                '</div>';
                container.insertAdjacentHTML('beforeend', commentHtml);
            }
        });
}

function addComment(newsId) {
    const input = document.getElementById('comment-input-' + newsId);
    const content = input ? input.value : '';
    if (!content) return;

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/news/' + newsId + '/comment', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);
            if (data.success) {
                if (input) input.value = '';
                loadComments(newsId);
            }
        }
    };
    xhr.send('content=' + encodeURIComponent(content));
}

function likeNews(newsId) {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/news/' + newsId + '/like', true);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            loadNewsFeed();
        }
    };
    xhr.send();
}

function dislikeNews(newsId) {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/news/' + newsId + '/dislike', true);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            loadNewsFeed();
        }
    };
    xhr.send();
}

function deleteNews(newsId) {
    if (confirm('Удалить новость?')) {
        const xhr = new XMLHttpRequest();
        xhr.open('DELETE', '/api/news/' + newsId, true);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === 4 && xhr.status === 200) {
                const newsElement = document.getElementById('news-' + newsId);
                if (newsElement) newsElement.remove();
            }
        };
        xhr.send();
    }
}

function loadProfile() {
    fetch('/api/profile')
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                window.location.href = '/login.html';
                return;
            }

            const firstName = document.getElementById('firstName');
            const lastName = document.getElementById('lastName');
            const email = document.getElementById('email');
            const birthDate = document.getElementById('birthDate');
            const gender = document.getElementById('gender');
            const city = document.getElementById('city');
            const about = document.getElementById('about');
            const avatar = document.getElementById('avatar');

            if (firstName) firstName.value = data.firstName || '';
            if (lastName) lastName.value = data.lastName || '';
            if (email) email.value = data.email || '';
            if (birthDate) birthDate.value = data.birthDate || '';
            if (gender) gender.value = data.gender || '';
            if (city) city.value = data.city || '';
            if (about) about.value = data.about || '';

            if (avatar) {
                avatar.src = data.avatarPath || '/images/default-avatar.png';
            }
        });
}

function updateProfile(event) {
    event.preventDefault();

    const firstName = document.getElementById('firstName') ? document.getElementById('firstName').value : '';
    const lastName = document.getElementById('lastName') ? document.getElementById('lastName').value : '';
    const birthDate = document.getElementById('birthDate') ? document.getElementById('birthDate').value : '';
    const gender = document.getElementById('gender') ? document.getElementById('gender').value : '';
    const city = document.getElementById('city') ? document.getElementById('city').value : '';
    const about = document.getElementById('about') ? document.getElementById('about').value : '';

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/profile/update', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);
            if (data.success) {
                showMessage('Профиль сохранен', 'success', 'message');
            } else {
                showMessage(data.error, 'error', 'message');
            }
        }
    };
    xhr.send('firstName=' + encodeURIComponent(firstName) +
             '&lastName=' + encodeURIComponent(lastName) +
             '&birthDate=' + encodeURIComponent(birthDate) +
             '&gender=' + encodeURIComponent(gender) +
             '&city=' + encodeURIComponent(city) +
             '&about=' + encodeURIComponent(about));
}

function uploadAvatar(event) {
    event.preventDefault();

    const fileInput = document.getElementById('avatarFile');
    if (!fileInput || !fileInput.files[0]) return;

    const formData = new FormData();
    formData.append('avatar', fileInput.files[0]);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/profile/upload-avatar', true);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);
            if (data.success) {
                showMessage('Аватар загружен', 'success', 'avatarMessage');
                const avatar = document.getElementById('avatar');
                if (avatar) {
                    avatar.src = data.avatarPath + '?t=' + new Date().getTime();
                }
            } else {
                showMessage(data.error, 'error', 'avatarMessage');
            }
        }
    };
    xhr.send(formData);
}

function loadFriends() {
    fetch('/api/friends/list')
        .then(response => response.json())
        .then(friends => {
            const container = document.getElementById('friendsList');
            if (!container) return;
            container.innerHTML = '';
            if (friends.length === 0) {
                container.innerHTML = '<p>Нет друзей</p>';
            } else {
                for (let i = 0; i < friends.length; i++) {
                    const friend = friends[i];
                    const html = '<div class="friend-item" id="friend-' + friend.id + '">' +
                        '<span>' + escapeHtml(friend.name) + '</span>' +
                        '<button onclick="openMessageWindow(' + friend.id + ', \'' + escapeHtml(friend.name) + '\')" class="btn btn-small">Сообщение</button>' +
                        '<button onclick="removeFriend(' + friend.id + ')" class="btn btn-danger btn-small">Удалить из друзей</button>' +
                    '</div>';
                    container.insertAdjacentHTML('beforeend', html);
                }
            }
        });
}

function loadFriendRequests() {
    fetch('/api/friends/requests')
        .then(response => response.json())
        .then(requests => {
            const container = document.getElementById('requestsList');
            if (!container) return;
            container.innerHTML = '';
            if (requests.length === 0) {
                container.innerHTML = '<p>Нет входящих заявок</p>';
            } else {
                for (let i = 0; i < requests.length; i++) {
                    const req = requests[i];
                    const fromName = req.fromName || ('Пользователь ' + req.fromUserId);
                    const html = '<div class="request-item" id="request-' + req.fromUserId + '">' +
                        '<span>' + escapeHtml(fromName) + '</span>' +
                        '<button onclick="acceptFriendRequest(' + req.fromUserId + ')" class="btn btn-small btn-primary">Принять</button>' +
                    '</div>';
                    container.insertAdjacentHTML('beforeend', html);
                }
            }
        });
}

function acceptFriendRequest(fromUserId) {
    if (confirm('Принять заявку в друзья?')) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/friends/accept', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === 4 && xhr.status === 200) {
                const data = JSON.parse(xhr.responseText);
                if (data.success) {
                    const requestElement = document.getElementById('request-' + fromUserId);
                    if (requestElement) requestElement.remove();
                    loadFriends();
                    alert('Заявка принята');
                } else {
                    alert(data.error || 'Ошибка');
                }
            }
        };
        xhr.send('fromUserId=' + fromUserId);
    }
}

function removeFriend(friendId) {
    if (confirm('Удалить пользователя из друзей?')) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/friends/remove', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === 4 && xhr.status === 200) {
                const data = JSON.parse(xhr.responseText);
                if (data.success) {
                    const friendElement = document.getElementById('friend-' + friendId);
                    if (friendElement) friendElement.remove();
                    alert('Друг удален');
                    loadFriends();
                } else {
                    alert(data.error || 'Ошибка');
                }
            }
        };
        xhr.send('friendId=' + friendId);
    }
}

function sendFriendRequest(toUserId) {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/api/friends/request', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);
            alert(data.success ? 'Заявка отправлена' : (data.error || 'Ошибка'));
        }
    };
    xhr.send('toUserId=' + toUserId);
}

function openMessageWindow(userId, userName) {
    window.location.href = 'messages.html?with=' + userId;
}

function checkAuth() {
    fetch('/api/auth/me')
        .then(response => response.json())
        .then(data => {
            if (!data.authenticated && !window.location.pathname.includes('login.html') && !window.location.pathname.includes('register.html') && !window.location.pathname.includes('index.html')) {
                window.location.href = 'login.html';
            }
        });
}

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();

    const profileForm = document.getElementById('profileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', updateProfile);
        loadProfile();
    }

    const avatarForm = document.getElementById('avatarForm');
    if (avatarForm) {
        avatarForm.addEventListener('submit', uploadAvatar);
    }

    if (document.getElementById('newsFeed')) {
        loadNewsFeed();

        const newsForm = document.getElementById('newsForm');
        if (newsForm) {
            newsForm.addEventListener('submit', (e) => {
                e.preventDefault();

                const formData = new FormData();
                formData.append('title', document.getElementById('newsTitle').value);
                formData.append('content', document.getElementById('newsContent').value);
                formData.append('accessLevel', document.getElementById('accessLevel').value);
                const imageFile = document.getElementById('newsImage');
                if (imageFile && imageFile.files[0]) {
                    formData.append('image', imageFile.files[0]);
                }

                const xhr = new XMLHttpRequest();
                xhr.open('POST', '/api/news/create', true);
                xhr.onreadystatechange = () => {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        const data = JSON.parse(xhr.responseText);
                        if (data.success) {
                            document.getElementById('newsTitle').value = '';
                            document.getElementById('newsContent').value = '';
                            if (imageFile) imageFile.value = '';
                            showMessage('Новость опубликована', 'success', 'newsMessage');
                            loadNewsFeed();
                        } else {
                            showMessage(data.error, 'error', 'newsMessage');
                        }
                    }
                };
                xhr.send(formData);
            });
        }
    }

    if (document.getElementById('friendsList')) {
        loadFriends();
        loadFriendRequests();
    }
});