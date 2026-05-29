async function loadFriends() {
    try {
        const response = await fetch('/api/friends/list');
        const friends = await response.json();
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
    } catch (error) {
        console.error('Error loading friends:', error);
    }
}

async function loadFriendRequests() {
    try {
        const response = await fetch('/api/friends/requests');
        const requests = await response.json();
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
    } catch (error) {
        console.error('Error loading requests:', error);
    }
}

async function acceptFriendRequest(fromUserId) {
    if (confirm('Принять заявку в друзья?')) {
        try {
            const response = await fetch('/api/friends/accept', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `fromUserId=${fromUserId}`
            });
            const data = await response.json();
            if (data.success) {
                const requestElement = document.getElementById('request-' + fromUserId);
                if (requestElement) requestElement.remove();
                await loadFriends();
                alert('Заявка принята');
            } else {
                alert(data.error || 'Ошибка');
            }
        } catch (error) {
            alert('Ошибка сервера');
        }
    }
}

async function removeFriend(friendId) {
    if (confirm('Удалить пользователя из друзей?')) {
        try {
            const response = await fetch('/api/friends/remove', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `friendId=${friendId}`
            });
            const data = await response.json();
            if (data.success) {
                const friendElement = document.getElementById('friend-' + friendId);
                if (friendElement) friendElement.remove();
                alert('Друг удален');
                await loadFriends();
            } else {
                alert(data.error || 'Ошибка');
            }
        } catch (error) {
            alert('Ошибка сервера');
        }
    }
}

async function sendFriendRequest(toUserId) {
    try {
        const response = await fetch('/api/friends/request', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `toUserId=${toUserId}`
        });
        const data = await response.json();
        alert(data.success ? 'Заявка отправлена' : (data.error || 'Ошибка'));
    } catch (error) {
        alert('Ошибка сервера');
    }
}

function initFriends() {
    if (!document.getElementById('friendsList')) {
        return;
    }
    loadFriends();
    loadFriendRequests();
}