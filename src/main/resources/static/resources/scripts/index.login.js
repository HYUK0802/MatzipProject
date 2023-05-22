const loginForm = document.getElementById('loginForm');
loginForm.emailWarning = loginForm.querySelector('[rel="emailWarning"]');
loginForm.emailWarning.show = (text) => {
    loginForm.emailWarning.innerText = text;
    loginForm.emailWarning.classList.add('visible');
};
loginForm.passwordWarning = loginForm.querySelector('[rel="passwordWarning"]');
loginForm.passwordWarning.show = (text) => {
    loginForm.passwordWarning.innerText = text;
    loginForm.passwordWarning.classList.add('visible');
};
loginForm.passwordWarning.hide = () => loginForm.passwordWarning.classList.remove('visible');
loginForm.show = () => {
    loginForm['email'].classList.remove('_invalid');
    loginForm.emailWarning.hide();
    loginForm['password'].classList.remove('_invalid');
    loginForm.passwordWarning.hide();
    loginForm.loginWarning.hide();
    loginForm['email'].value = '';
    loginForm['email'].focus();
    loginForm['password'].value = '';
    loginForm.classList.add('visible');
};
loginForm.loginWarning = loginForm.querySelector('[rel="loginWarning"]');
loginForm.loginWarning.show = (text) => {
    loginForm.loginWarning.innerText = text;
    loginForm.loginWarning.classList.add('visible');
};
loginForm.loginWarning.hide = () => loginForm.loginWarning.classList.remove('visible');
loginForm.onsubmit = e => {
    e.preventDefault();
    loginForm['email'].classList.remove('_invalid');
    loginForm.emailWarning.hide();
    loginForm['password'].classList.remove('_invalid');
    loginForm.passwordWarning.hide();
    loginForm.loginWarning.hide();
    if (loginForm['email'].value === '') {
        loginForm['email'].classList.add('_invalid');
        loginForm['email'].focus();
        loginForm.emailWarning.show('이메일을 입력해 주세요.');
        return false;
    }
    if (!new RegExp('^(?=.{10,50}$)([\\da-zA-Z\\-_\\.]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$').test(loginForm['email'].value)) {
        loginForm['email'].classList.add('_invalid');
        loginForm['email'].focus();
        loginForm['email'].select();
        loginForm.emailWarning.show('올바른 이메일을 입력해 주세요.');
        return false;
    }
    if (loginForm['password'].value === '') {
        loginForm['password'].classList.add('_invalid');
        loginForm['password'].focus();
        loginForm.passwordWarning.show('비밀번호를 입력해 주세요.');
        return false;
    }
    if (!new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]};:\'",<.>/?]{8,50})$').test(loginForm['password'].value)) {
        loginForm['password'].classList.add('_invalid');
        loginForm['password'].focus();
        loginForm['password'].select();
        loginForm.passwordWarning.show('올바른 비밀번호를 입력해 주세요.');
        return false;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', loginForm['email'].value);
    formData.append('password', loginForm['password'].value);
    xhr.open('POST', '/user/login');
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status >= 200 && xhr.status < 300) {
            const responseObject = JSON.parse(xhr.responseText);
            switch (responseObject.result) {
                case 'failure':
                    loginForm.loginWarning.show('이메일 혹은 비밀번호가 올바르지 않습니다.');
                    loginForm['email'].focus();
                    loginForm['email'].select();
                    break;
                case 'failure_suspended':
                    loginForm.loginWarning.show('해당 계정은 이용이 정지된 계정입니다. 관리자에게 문의해 주세요.');
                    break;
                case 'failure_email_not_verified':
                    loginForm.loginWarning.show('이메일 인증이 완료되지 않은 계정입니다. 이메일 인증 후 다시 시도해 주세요.');
                    break;
                case 'success':
                    location.href += '';
                    break;
                default:
                    loginForm.loginWarning.show('서버가 알 수 없는 응답을 반환했습니다. 관리자에게 문의해 주세요.');
            }
        } else {
            loginForm.loginWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }
    };
    xhr.send(formData);
};
loginForm.querySelectorAll('[name="cancel"]').forEach(x => x.onclick = () => {
    coverElement.hide();
    loginForm.hide();
});