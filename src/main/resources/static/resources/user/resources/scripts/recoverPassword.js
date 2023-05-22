const recoverForm = document.getElementById('recoverForm');

recoverForm.onsubmit = e => {
    e.preventDefault();
    if (recoverForm['password'].value === '') {
        alert('비밀번호를 입력해 주세요.');
        recoverForm['password'].focus();
        return;
    }
    if (recoverForm['passwordCheck'].value === '') {
        alert('비밀번호를 한번 더 입력해 주세요.')
        recoverForm['passwordCheck'].focus();
        recoverForm['passwordCheck'].select();
        return;
    }
    if (recoverForm['password'].value !== recoverForm['passwordCheck'].value) {
        alert('비밀번호가 서로 일치하지 않습니다. 다시 한번 더 확인해 주세요.');
        recoverForm['password'].focus();
        recoverForm['password'].select();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', recoverForm['email'].value);
    formData.append('code', recoverForm['code'].value);
    formData.append('salt', recoverForm['salt'].value);
    formData.append('password', recoverForm['password'].value);
    xhr.open('PATCH', '/user/recoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject.result) {
                    case 'failure':
                        alert('올바르지 않은 접근이거나 링크가 더 이상 유효하지 않습니다.');
                        break;
                    case 'success':
                        alert('비밀번호를 재설정하였습니다. 확인을 누르면 첫 페이지로 이동합니다.');
                        location.href = '/';
                        break;
                    default:
                        alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};