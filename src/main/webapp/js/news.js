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

function initNews() {
    if (!document.getElementById('newsFeed')) {
        return;
    }
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