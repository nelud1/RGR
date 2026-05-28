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

function initProfile() {
    if (!document.getElementById('profileForm')) {
        return;
    }
    const profileForm = document.getElementById('profileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', updateProfile);
        loadProfile();
    }

    const avatarForm = document.getElementById('avatarForm');
    if (avatarForm) {
        avatarForm.addEventListener('submit', uploadAvatar);
    }
}