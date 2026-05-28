let currentDialogUserId = null;
let messageCheckInterval = null;

function formatMessageDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date >= today) {
        return date.toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'});
    } else if (date >= yesterday) {
        return 'Вчера';
    } else {
        return date.toLocaleDateString('ru-RU', {day: '2-digit', month:'2-digit', year:'2-digit'});
    }
}

async function loadDialogs() {
    try {
        const response = await fetch('/api/messages/dialogs');
        const dialogs = await response.json();
        const container = document.getElementById('dialogsList');
        if (!container) return;
        container.innerHTML = '';

        if (dialogs.length === 0) {
            container.innerHTML = '<p class="no-dialogs">Нет диалогов</p>';
        } else {
            for (const dialog of dialogs) {
                const unreadBadge = dialog.unreadCount > 0 ? `<span class="unread-badge">${dialog.unreadCount}</span>` : '';
                const activeClass = (currentDialogUserId === dialog.userId) ? 'active' : '';
                const html = `
                    <div class="dialog-item ${activeClass}" data-user-id="${dialog.userId}" data-user-name="${escapeHtml(dialog.userName)}">
                        <div class="dialog-avatar">
                            ${dialog.avatarPath ? `<img src="${dialog.avatarPath}">` : `<div class="default-avatar">${dialog.userName.charAt(0).toUpperCase()}</div>`}
                        </div>
                        <div class="dialog-info">
                            <div class="dialog-name">${escapeHtml(dialog.userName)}</div>
                            <div class="dialog-last-message">${escapeHtml(dialog.lastMessage)}</div>
                        </div>
                        <div class="dialog-meta">
                            <div class="dialog-date">${formatMessageDate(dialog.lastMessageDate)}</div>
                            ${unreadBadge}
                        </div>
                    </div>
                `;
                container.insertAdjacentHTML('beforeend', html);
            }

            document.querySelectorAll('.dialog-item').forEach(item => {
                item.addEventListener('click', () => {
                    const userId = parseInt(item.dataset.userId);
                    const userName = item.dataset.userName;
                    openDialog(userId, userName);
                });
            });
        }
    } catch (error) {
        console.error('Error loading dialogs:', error);
        if (document.getElementById('dialogsList')) {
            document.getElementById('dialogsList').innerHTML = '<p class="no-dialogs">Ошибка загрузки диалогов</p>';
        }
    }
}

function openDialog(userId, userName) {
    currentDialogUserId = userId;

    const dialogHeader = document.getElementById('dialogHeader');
    const dialogArea = document.getElementById('dialogArea');
    const emptyDialog = document.getElementById('emptyDialog');

    if (dialogHeader) dialogHeader.innerHTML = `<h3>${escapeHtml(userName)}</h3>`;
    if (dialogArea) dialogArea.style.display = 'flex';
    if (emptyDialog) emptyDialog.style.display = 'none';

    document.querySelectorAll('.dialog-item').forEach(item => {
        item.classList.remove('active');
        if (parseInt(item.dataset.userId) === userId) {
            item.classList.add('active');
        }
    });

    loadMessages();

    if (messageCheckInterval) {
        clearInterval(messageCheckInterval);
    }
    messageCheckInterval = setInterval(loadMessages, 5000);
}

async function loadMessages() {
    if (!currentDialogUserId) return;

    try {
        const response = await fetch(`/api/messages/dialog/${currentDialogUserId}`);
        const messages = await response.json();
        const container = document.getElementById('messagesList');
        if (!container) return;
        container.innerHTML = '';

        if (!messages || messages.length === 0) {
            container.innerHTML = '<p class="no-messages">Нет сообщений. Напишите что-нибудь!</p>';
        } else {
            for (const msg of messages) {
                const messageClass = msg.isMine ? 'message mine' : 'message theirs';
                const html = `
                    <div class="${messageClass}">
                        <div class="message-text">${escapeHtml(msg.body)}</div>
                        <div class="message-time">${formatMessageDate(msg.createdAt)}</div>
                    </div>
                `;
                container.insertAdjacentHTML('beforeend', html);
            }
            container.scrollTop = container.scrollHeight;
        }
        loadDialogs();
    } catch (error) {
        console.error('Error loading messages:', error);
        if (document.getElementById('messagesList')) {
            document.getElementById('messagesList').innerHTML = '<p class="no-messages">Ошибка загрузки сообщений</p>';
        }
    }
}

async function sendCurrentMessage() {
    const content = document.getElementById('messageContent').value;
    if (!content || !content.trim()) {
        alert('Введите сообщение');
        return;
    }

    try {
        const response = await fetch('/api/messages/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `receiverId=${currentDialogUserId}&subject=&content=${encodeURIComponent(content)}`
        });

        const data = await response.json();

        if (data.success) {
            document.getElementById('messageContent').value = '';
            loadMessages();
        } else {
            alert(data.error || 'Ошибка отправки');
        }
    } catch (error) {
        alert('Ошибка сервера');
    }
}

function initMessages() {
    if (!document.getElementById('dialogsList')) {
        return;
    }
    const sendBtn = document.getElementById('sendMessageBtn');
    if (sendBtn) {
        sendBtn.addEventListener('click', sendCurrentMessage);
    }

    loadDialogs();

    const urlParams = new URLSearchParams(window.location.search);
    const withUserId = urlParams.get('with');
    if (withUserId) {
        fetch(`/api/users/${withUserId}`)
            .then(response => response.json())
            .then(user => {
                if (!user.error) {
                    openDialog(withUserId, user.firstName + ' ' + user.lastName);
                }
            })
            .catch(error => console.log('Пользователь не найден'));
    }
}

window.addEventListener('beforeunload', () => {
    if (messageCheckInterval) {
        clearInterval(messageCheckInterval);
    }
});