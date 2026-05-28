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

function openMessageWindow(userId, userName) {
    window.location.href = 'messages.html?with=' + userId;
}

document.addEventListener('DOMContentLoaded', () => {
    if (typeof initAuth === 'function') initAuth();
    if (typeof initNews === 'function') initNews();
    if (typeof initProfile === 'function') initProfile();
    if (typeof initFriends === 'function') initFriends();
    if (typeof initMessages === 'function') initMessages();
    if (typeof initSearch === 'function') initSearch();
});