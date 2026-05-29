async function loadNewsFeed() {
    try {
        const response = await fetch('/api/news/feed');
        const newsList = await response.json();
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
    } catch (error) {
        console.error('Error loading news feed:', error);
    }
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

async function loadComments(newsId) {
    try {
        const response = await fetch('/api/news/' + newsId + '/comments');
        const comments = await response.json();
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
    } catch (error) {
        console.error('Error loading comments:', error);
    }
}

async function addComment(newsId) {
    const input = document.getElementById('comment-input-' + newsId);
    const content = input ? input.value : '';
    if (!content) return;

    try {
        const response = await fetch('/api/news/' + newsId + '/comment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'content=' + encodeURIComponent(content)
        });
        const data = await response.json();
        if (data.success) {
            if (input) input.value = '';
            loadComments(newsId);
        }
    } catch (error) {
        console.error('Error adding comment:', error);
    }
}

async function likeNews(newsId) {
    try {
        const response = await fetch('/api/news/' + newsId + '/like', {
            method: 'POST'
        });
        if (response.ok) {
            loadNewsFeed();
        }
    } catch (error) {
        console.error('Error liking news:', error);
    }
}

async function dislikeNews(newsId) {
    try {
        const response = await fetch('/api/news/' + newsId + '/dislike', {
            method: 'POST'
        });
        if (response.ok) {
            loadNewsFeed();
        }
    } catch (error) {
        console.error('Error disliking news:', error);
    }
}

async function deleteNews(newsId) {
    if (confirm('Удалить новость?')) {
        try {
            const response = await fetch('/api/news/' + newsId, {
                method: 'DELETE'
            });
            if (response.ok) {
                const newsElement = document.getElementById('news-' + newsId);
                if (newsElement) newsElement.remove();
            }
        } catch (error) {
            console.error('Error deleting news:', error);
        }
    }
}

function initNews() {
    if (!document.getElementById('newsFeed')) {
        return;
    }
    loadNewsFeed();

    const newsForm = document.getElementById('newsForm');
    if (newsForm) {
        newsForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const formData = new FormData();
            formData.append('title', document.getElementById('newsTitle').value);
            formData.append('content', document.getElementById('newsContent').value);
            formData.append('accessLevel', document.getElementById('accessLevel').value);
            const imageFile = document.getElementById('newsImage');
            if (imageFile && imageFile.files[0]) {
                formData.append('image', imageFile.files[0]);
            }

            try {
                const response = await fetch('/api/news/create', {
                    method: 'POST',
                    body: formData
                });
                const data = await response.json();
                if (data.success) {
                    document.getElementById('newsTitle').value = '';
                    document.getElementById('newsContent').value = '';
                    if (imageFile) imageFile.value = '';
                    showMessage('Новость опубликована', 'success', 'newsMessage');
                    loadNewsFeed();
                } else {
                    showMessage(data.error, 'error', 'newsMessage');
                }
            } catch (error) {
                showMessage('Ошибка сервера', 'error', 'newsMessage');
            }
        });
    }
}