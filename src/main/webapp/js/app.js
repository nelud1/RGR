function showMessage(text, type, container = '#message') {
    $(container).removeClass('success error').addClass(type).text(text).show();
    setTimeout(() => $(container).hide(), 5000);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU') + ' ' + date.toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'});
}

function checkAuth() {
    window.currentUser = window.currentUser || { authenticated: true, isAdmin: true };
    if ($('#newsFeed').length) {
        loadNewsFeed();
    }
}

function logout() {
    $.ajax({
        url: 'api/auth/logout',
        method: 'GET',
        success: function() {
            window.location.href = 'index.html';
        }
    });
}

function loadNewsFeed() {
    $.ajax({
        url: 'api/news/feed',
        method: 'GET',
        success: function(newsList) {
            $('#newsFeed').empty();
            if (newsList.length === 0) {
                $('#newsFeed').html('<p>Нет новостей</p>');
            } else {
                newsList.forEach(function(news) {
                    renderNewsItem(news);
                });
            }
        }
    });
}

function renderNewsItem(news) {
    var newsHtml = `
        <div class="news-item" id="news-${news.id}">
            <h4>${news.title}</h4>
            <p>${news.content}</p>
            <small>Автор: ${news.authorName} | ${formatDate(news.createdAt)}</small>
            <div class="news-actions">
                <button onclick="likeNews(${news.id})" class="like-btn">
                    👍 ${news.likesCount || 0}
                </button>
                <button onclick="dislikeNews(${news.id})" class="dislike-btn">
                    👎 ${news.dislikesCount || 0}
                </button>`;
    
    if (window.currentUser && window.currentUser.isAdmin) {
        newsHtml += `<button onclick="deleteNews(${news.id})" class="btn btn-danger btn-small">Удалить</button>`;
    }
    
    newsHtml += `
            </div>
            <div class="comments-section">
                <div id="comments-${news.id}"></div>
                <div class="comment-form">
                    <input type="text" id="comment-input-${news.id}" placeholder="Написать комментарий..." maxlength="100">
                    <button onclick="addComment(${news.id})" class="btn btn-primary btn-small">Отправить</button>
                </div>
            </div>
        </div>`;
    
    $('#newsFeed').append(newsHtml);
    loadComments(news.id);
}

function loadComments(newsId) {
    $.ajax({
        url: `api/news/${newsId}/comments`,
        method: 'GET',
        success: function(comments) {
            var container = $(`#comments-${newsId}`);
            container.empty();
            comments.forEach(function(comment) {
                var commentHtml = `
                    <div class="comment">
                        <strong>${comment.authorName}:</strong> ${comment.content}
                        <br><small>${formatDate(comment.createdAt)}</small>
                    </div>`;
                container.append(commentHtml);
            });
        }
    });
}

function addComment(newsId) {
    var content = $(`#comment-input-${newsId}`).val();
    if (!content) return;
    
    $.ajax({
        url: `api/news/${newsId}/comment`,
        method: 'POST',
        data: { content: content },
        success: function(data) {
            if (data.success) {
                $(`#comment-input-${newsId}`).val('');
                loadComments(newsId);
            }
        }
    });
}

function likeNews(newsId) {
    $.ajax({
        url: `api/news/${newsId}/like`,
        method: 'POST',
        success: function(data) {
            if (data.success) {
                loadNewsFeed();
            }
        }
    });
}

function dislikeNews(newsId) {
    $.ajax({
        url: `api/news/${newsId}/dislike`,
        method: 'POST',
        success: function(data) {
            if (data.success) {
                loadNewsFeed();
            }
        }
    });
}

function deleteNews(newsId) {
    if (confirm('Вы уверены, что хотите удалить эту новость?')) {
        $.ajax({
            url: `api/news/${newsId}`,
            method: 'DELETE',
            success: function(data) {
                if (data.success) {
                    $(`#news-${newsId}`).remove();
                }
            }
        });
    }
}
