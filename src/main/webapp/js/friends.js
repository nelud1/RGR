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

function initFriends() {
    if (!document.getElementById('friendsList')) {
        return;
    }
    loadFriends();
    loadFriendRequests();
}