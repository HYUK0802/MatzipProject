const listElement = document.getElementById('list');
listElement.addressGu = listElement.querySelector('[rel="addressGu"]');
listElement.addressDong = listElement.querySelector('[rel="addressDong"]');

const loadingElement = document.getElementById('loading');
const coverElement = document.getElementById('cover');
coverElement.show = (f) => {
    coverElement.classList.add('visible');
    coverElement.onclick = f;
}

window.addEventListener('load', () => {
    loadingElement.hide();
});

document.body.querySelectorAll('[data-action]').forEach(element => {
    element.addEventListener('click', e => {
        const action = element.dataset.action;
        switch (action) {
            case 'logout':
                location.href = '/user/logout';
                break;
            case 'showLogin':
                coverElement.show(() => {
                    coverElement.hide();
                    loginForm.hide();
                });
                loginForm.show();
                break;
            case 'showRecover':
                e.preventDefault();
                loginForm.hide();
                coverElement.show(() => {
                    coverElement.hide();
                    recoverForm.hide();
                });
                recoverForm.show();
                recoverForm['eContact'].focus();
                break;
            case 'showRegister':
                e.preventDefault();
                coverElement.show(() => {
                    coverElement.hide();
                    registerForm.hide();
                });
                loginForm.hide();
                registerForm.show();
                break;
        }
    });
});