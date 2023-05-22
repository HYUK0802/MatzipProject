const registerForm = document.getElementById('registerForm');

registerForm.termWarning = registerForm.querySelector('[rel="termWarning"]');

registerForm.termWarning.show = (text) => {
    registerForm.termWarning.innerText = text;
    registerForm.termWarning.classList.add('visible');
};
registerForm.termWarning.hide = () => registerForm.termWarning.classList.remove('visible');

registerForm.emailWarning = registerForm.querySelector('[rel="emailWarning"]');
registerForm.emailWarning.show = (text) => {
    registerForm.emailWarning.innerText = text;
    registerForm.emailWarning.classList.add('visible');
};
registerForm.emailWarning.hide = () => registerForm.emailWarning.classList.remove('visible');

registerForm.passwordWarning = registerForm.querySelector('[rel="passwordWarning"]');
registerForm.passwordWarning.show = (text) => {
    registerForm.passwordWarning.innerText = text;
    registerForm.passwordWarning.classList.add('visible');
};
registerForm.passwordWarning.hide = () => registerForm.passwordWarning.classList.remove('visible');

registerForm.nicknameWarning = registerForm.querySelector('[rel="nicknameWarning"]');
registerForm.nicknameWarning.show = (text) => {
    registerForm.nicknameWarning.innerText = text;
    registerForm.nicknameWarning.classList.add('visible');
};
registerForm.nicknameWarning.hide = () => registerForm.nicknameWarning.classList.remove('visible');

registerForm.contactWarning = registerForm.querySelector('[rel="contactWarning"]');
registerForm.contactWarning.show = (text) => {
    registerForm.contactWarning.innerText = text;
    registerForm.contactWarning.classList.add('visible');
};
registerForm.contactWarning.hide = () => registerForm.contactWarning.classList.remove('visible');

registerForm.show = () => {
    registerForm['agreeServiceTerm'].checked = false;
    registerForm['agreePrivacyTerm'].checked = false;
    registerForm.termWarning.hide();

    registerForm['email'].value = '';
    registerForm.emailWarning.hide();

    registerForm['password'].value = '';
    registerForm['passwordCheck'].value = '';
    registerForm.passwordWarning.hide();

    registerForm['nickname'].value = '';
    registerForm.nicknameWarning.hide();

    registerForm['contact'].value = '';
    registerForm['contact'].removeAttribute('disabled');
    registerForm['contactSend'].removeAttribute('disabled');
    registerForm['contactCode'].value = '';
    registerForm['contactCode'].setAttribute('disabled', 'disabled');
    registerForm['contactVerify'].setAttribute('disabled', 'disabled');
    registerForm['contactSalt'].value = '';

    registerForm.classList.remove('step-1', 'step-2', 'step-3');
    registerForm.classList.add('step-1', 'visible');
};
registerForm.onsubmit = e => {
    e.preventDefault();
    if (registerForm.classList.contains('step-1')) {
        registerForm.termWarning.hide();
        if (!registerForm['agreeServiceTerm'].checked) {
            registerForm.termWarning.show('서비스 이용약관을 읽고 동의해 주세요.');
            return false;
        }
        if (!registerForm['agreePrivacyTerm'].checked) {
            registerForm.termWarning.show('개인정보 처리방침을 읽고 동의해 주세요.');
            return false;
        }
        registerForm.classList.remove('step-1');
        registerForm.classList.add('step-2');
        registerForm['email'].focus();
    }
    if (registerForm.classList.contains('step-2')) {
        if (registerForm['email'].value === '') {
            registerForm.emailWarning.show('이메일을 입력해 주세요.');
            registerForm['email'].focus();
            return;
        }
        if (!new RegExp('^(?=.{10,50}$)([\\da-zA-Z\\-_]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$').test(registerForm['email'].value)) {
            registerForm.emailWarning.show('올바른 이메일을 입력해 주세요.');
            registerForm['email'].focus();
            registerForm['email'].select();
            return;
        }
        if (registerForm['password'].value === '') {
            registerForm.passwordWarning.show('비밀번호를 입력해 주세요.');
            registerForm['password'].focus();
            return;
        }
        if (!new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]};:\'",<.>/?]{8,50})$').test(registerForm['password'].value)) {
            registerForm.passwordWarning.show('올바른 비밀번호를 입력해 주세요.');
            registerForm['password'].focus();
            registerForm['password'].select();
            return;
        }
        if (registerForm['passwordCheck'].value === '') {
            registerForm.passwordWarning.show('비밀번호를 다시 한번 더 입력해 주세요.');
            registerForm['passwordCheck'].focus();
            return;
        }
        if (registerForm['password'].value !== registerForm['passwordCheck'].value) {
            registerForm.passwordWarning.show('비밀번호가 서로 일치하지 않습니다. 다시 한번 더 확인해 주세요.');
            registerForm['passwordCheck'].focus();
            registerForm['passwordCheck'].select();
            return;
        }
        if (registerForm['nickname'].value === '') {
            registerForm.nicknameWarning.show('별명을 입력해 주세요.');
            registerForm['nickname'].focus();
            return;
        }
        if (!new RegExp('^([가-힣]{2,10})$').test(registerForm['nickname'].value)) {
            registerForm.nicknameWarning.show('올바른 별명을 입력해 주세요.');
            registerForm['nickname'].focus();
            registerForm['nickname'].select();
            return;
        }
        if (registerForm['contactSalt'].value === '') {
            registerForm.contactWarning.show('연락처 인증을 완료해 주세요.');
            return;
        }
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('email', registerForm['email'].value);
        formData.append('password', registerForm['password'].value);
        formData.append('nickname', registerForm['nickname'].value);
        formData.append('contact', registerForm['contact'].value);
        formData.append('code', registerForm['contactCode'].value);
        formData.append('salt', registerForm['contactSalt'].value);
        xhr.open('POST', '/user/register');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                registerForm.classList.remove('working');
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject.result) {
                        case 'failure':
                            registerForm.contactWarning.show('알 수 없는 이유로 가입하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                            break;
                        case 'failure_duplicate_email':
                            registerForm.emailWarning.show('해당 이메일은 이미 사용 중입니다.');
                            registerForm['email'].focus();
                            registerForm['email'].select();
                            break;
                        case 'failure_duplicate_nickname':
                            registerForm.nicknameWarning.show('해당 별명은 이미 사용 중입니다.');
                            registerForm['nickname'].focus();
                            registerForm['nickname'].select();
                            break;
                        case 'failure_duplicate_contact':
                            registerForm.contactWarning.show('해당 연락처는 이미 사용 중입니다.');
                            registerForm['contact'].focus();
                            registerForm['contact'].select();
                            break;
                        case 'success':
                            registerForm.classList.remove('step-2');
                            registerForm.classList.add('step-3');
                            break;
                        default:
                            registerForm.contactWarning.show('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                    }
                } else {
                    registerForm.contactWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            }
        };
        xhr.send(formData);
        registerForm.classList.add('working');
    }
    if (registerForm.classList.contains('step-3')) {
        coverElement.hide();
        registerForm.hide();
    }
};
registerForm['contactSend'].addEventListener('click', () => {
    registerForm.contactWarning.hide();
    if (registerForm['contact'].value === '') {
        registerForm.contactWarning.show('연락처를 입력해 주세요.');
        registerForm['contact'].focus();
        return;
    }
    if (!new RegExp('^(010)(\\d{8})$').test(registerForm['contact'].value)) {
        registerForm.contactWarning.show('올바른 연락처를 입력해 주세요.');
        registerForm['contact'].focus();
        registerForm['contact'].select();
        return;
    }
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/user/contactCode?contact=${registerForm['contact'].value}`);
    // /user/contactCode?contact=01012345678
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'failure_duplicate':
                        registerForm.contactWarning.show('해당 연락처는 이미 사용 중입니다.');
                        registerForm['contact'].focus();
                        registerForm['contact'].select();
                        break;
                    case 'success':
                        registerForm['contact'].setAttribute('disabled', 'disabled');
                        registerForm['contactSend'].setAttribute('disabled', 'disabled');
                        registerForm['contactCode'].removeAttribute('disabled');
                        registerForm['contactVerify'].removeAttribute('disabled');
                        registerForm['contactCode'].focus();
                        registerForm['contactSalt'].value = responseObject.salt;
                        registerForm.contactWarning.show('입력하신 연락처로 인증번호를 전송하였습니다. 5분 이내로 입력해 주세요.');
                        break;
                    default:
                        registerForm.contactWarning.show('서버가 알 수 없는 응답을 반환했습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                registerForm.contactWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send();
});
registerForm['contactVerify'].addEventListener('click', () => {
    registerForm.contactWarning.hide();
    if (registerForm['contactCode'].value === '') {
        registerForm.contactWarning.show('인증번호를 입력해 주세요.');
        registerForm['contactCode'].focus();
        return;
    }
    if (!new RegExp('^(\\d{6})$').test(registerForm['contactCode'].value)) {
        registerForm.contactWarning.show('올바른 인증번호를 입력해 주세요.');
        registerForm['contactCode'].focus();
        registerForm['contactCode'].select();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('contact', registerForm['contact'].value);
    formData.append('salt', registerForm['contactSalt'].value);
    formData.append('code', registerForm['contactCode'].value);
    xhr.open('PATCH', '/user/contactCode');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'failure_expired':
                        registerForm.contactWarning.show('해당 인증번호는 만료되었습니다. 처음부터 다시 진행해 주세요.');
                        break;
                    case 'success':
                        registerForm['contactCode'].setAttribute('disabled', 'disabled');
                        registerForm['contactVerify'].setAttribute('disabled', 'disabled');
                        registerForm.contactWarning.show('인증이 완료되었습니다.');
                        break;
                    default:
                        registerForm['contactCode'].focus();
                        registerForm['contactCode'].select();
                        registerForm.contactWarning.show('인증번호가 올바르지 않습니다. 다시 확인해 주세요.');
                }
            } else {
                registerForm.contactWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});
registerForm['email'].addEventListener('focusout', () => {
    registerForm.emailWarning.hide();
    if (registerForm['email'].value === '') {
        registerForm.emailWarning.show('이메일을 입력해 주세요.');
        return;
    }
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/user/emailCount?email=${registerForm['email'].value}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'duplicate':
                        registerForm.emailWarning.show('해당 이메일은 이미 사용 중입니다.');
                        break;
                    case 'okay':
                        registerForm.emailWarning.show('해당 이메일은 사용할 수 있습니다.');
                        break;
                    default:
                        registerForm.emailWarning.show('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                registerForm.emailWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send();
});
['password', 'passwordCheck'].forEach(name => {
    registerForm[name].addEventListener('focusout', () => {
        registerForm.passwordWarning.hide();
        if (registerForm['password'].value === '') {
            registerForm.passwordWarning.show('비밀번호를 입력해 주세요.');
            return;
        }
        if (registerForm['passwordCheck'].value === '') {
            registerForm.passwordWarning.show('비밀번호를 다시 한번 더 입력해 주세요.');
            return;
        }
        if (registerForm['password'].value !== registerForm['passwordCheck'].value) {
            registerForm.passwordWarning.show('비밀번호가 서로 일치하지 않습니다.');
            return;
        }
    });
});
registerForm['nickname'].addEventListener('focusout', () => {
    registerForm.nicknameWarning.hide();
    if (registerForm['nickname'].value === '') {
        registerForm.nicknameWarning.show('별명을 입력해 주세요.');
        return;
    }
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/user/nicknameCount?nickname=${registerForm['nickname'].value}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'duplicate':
                        registerForm.nicknameWarning.show('해당 별명은 이미 사용 중입니다.');
                        break;
                    case 'okay':
                        registerForm.nicknameWarning.show('해당 별명은 사용할 수 있습니다.');
                        break;
                    default:
                        registerForm.nicknameWarning.show('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                registerForm.nicknameWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send();
});
registerForm.querySelectorAll('[name="cancel"]').forEach(x => x.onclick = () => {
    coverElement.hide();
    registerForm.hide();
});