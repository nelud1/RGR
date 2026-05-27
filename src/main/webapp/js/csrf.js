function getCsrfToken() {
    var csrfToken = null;
    var cookies = document.cookie.split(';');
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i].trim();
        if (cookie.startsWith('XSRF-TOKEN=')) {
            csrfToken = cookie.substring('XSRF-TOKEN='.length);
            break;
        }
    }
    return csrfToken;
}

function addCsrfToken(xhr) {
    var token = getCsrfToken();
    if (token) {
        xhr.setRequestHeader('X-XSRF-TOKEN', token);
    }
}

if (typeof $ !== 'undefined') {
    $(document).ajaxSend(function(event, xhr, options) {
        if (options.type !== 'GET') {
            addCsrfToken(xhr);
        }
    });
}