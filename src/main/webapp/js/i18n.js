let currentLocale = 'ru';
let translations = {};

async function loadTranslations(locale) {
    try {
        const response = await fetch(`/api/i18n/messages?lang=${locale}`);
        translations = await response.json();
        currentLocale = locale;
        applyTranslations();
        return translations;
    } catch (error) {
        console.error('Error loading translations:', error);
        return {};
    }
}

function t(key, params = {}) {
    let text = translations[key] || key;
    for (const [param, value] of Object.entries(params)) {
        text = text.replace(`{${param}}`, value);
    }
    return text;
}

function applyTranslations() {
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        if (key && translations[key]) {
            element.textContent = t(key);
        }
    });

    document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
        const key = element.getAttribute('data-i18n-placeholder');
        if (key && translations[key]) {
            element.placeholder = t(key);
        }
    });
}

function setPageLanguage(locale) {
    document.cookie = `lang=${locale}; path=/; max-age=2592000`;
    window.location.reload();
}

function getCurrentLocale() {
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'lang') {
            return value;
        }
    }
    return 'ru';
}

document.addEventListener('DOMContentLoaded', async () => {
    const locale = getCurrentLocale();
    await loadTranslations(locale);
});