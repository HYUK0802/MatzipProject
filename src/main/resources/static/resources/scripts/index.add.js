const addForm = document.getElementById('addForm');
const addMatzip = document.getElementById('addMatzip');

addForm.thumbnailPreview = addForm.querySelector('[rel="thumbnailPreview"]');
addForm.emptyThumbnail = addForm.querySelector('[rel="emptyThumbnail"]');

addForm.emptyThumbnail = addForm.querySelector('[rel="emptyThumbnail"]');

addForm.show = function() {
    const center = mapElement.object.getCenter();
    mapElement.geocoder.coord2Address(center.La, center.Ma, function (result, status) {
        if (status === kakao.maps.services.Status.OK) {
            addForm['addressPrimary'].value = result[0]['address']['address_name'];
            addForm['lat'].value = center.Ma;
            addForm['lng'].value = center.La;
        } else {
            addForm['addressPrimary'].value = '';
            addForm['lat'].value = '';
            addForm['lng'].value = '';
        }
    });
    addForm['name'].value = '';
    addForm['contactFirst'].value = '';
    addForm['contactSecond'].value = '';
    addForm['contactThird'].value = '';
    addForm['description'].value = '';
    addForm['addressSecondary'].value = '';
    addForm['thumbnail'].value = '';
    addForm['opSun'].checked = true;
    addForm['openSun'].value = '';
    addForm['closeSun'].value = '';
    addForm['opMon'].checked = true;
    addForm['openMon'].value = '';
    addForm['closeMon'].value = '';
    addForm['opTue'].checked = true;
    addForm['openTue'].value = '';
    addForm['closeTue'].value = '';
    addForm['opWed'].checked = true;
    addForm['openWed'].value = '';
    addForm['closeWed'].value = '';
    addForm['opThu'].checked = true;
    addForm['openThu'].value = '';
    addForm['closeThu'].value = '';
    addForm['opFri'].checked = true;
    addForm['openFri'].value = '';
    addForm['closeFri'].value = '';
    addForm['opSat'].checked = true;
    addForm['openSat'].value = '';
    addForm['closeSat'].value = '';
    addForm['name'].focus();
    addForm.classList.add('visible');
    mapElement.classList.add('pinning');
    };

addForm.hide = function() {
    addForm.classList.remove('visible');
    mapElement.classList.remove('pinning');
};

addForm['cancel'].onclick = function() {
    addForm.hide();
};

addForm['thumbnail'].onchange = function (e) {
    if (addForm['thumbnail'].files.length === 0 ) {
        addForm.thumbnailPreview.style.backgroundImage = 'none';
        addForm.emptyThumbnail.show();
        return;
    }
    const fileReader = new FileReader();
    fileReader.onload = function(data) {
        addForm.thumbnailPreview.style.backgroundImage = `url("${data.target.result}")`;
        addForm.emptyThumbnail.hide();
    }
    fileReader.readAsDataURL(addForm['thumbnail'].files[0]);
};

addForm.thumbnailPreview.onclick = function (){
    addForm['thumbnail'].click();
};

addForm.onsubmit = function(e) {
    e.preventDefault();

    if (addForm['name'].value === '') {
        alert('이름을 입력해주세요.');
        addForm['name'].focus();
        return false;
    }
    if(addForm['contactFirst'].value === '') {
        alert('지역번호를 선택해주세요');
        addForm['contactFirst'].focus();
        return false;
    }
    if(addForm['contactSecond'].value === '') {
        alert('연락처를 입력해 주세요');
        addForm['contactSecond'].focus();
        return false;
    }
    if(addForm['contactThird'].value === '') {
        alert('연락처를 입력해 주세요');
        addForm['contactThird'].focus();
        return false;
    }
    if(addForm['description'].value === '') {
        alert('설명을 입력해 주세요');
        addForm['description'].focus();
        return false;
    }
    if(addForm['lat'].value === '' || addForm['lng'].value ==='' || addForm['addressPrimary'].value === '') {
        alert('위치가 올바르지 않거나 설정되지 않았습니다. 지도를 움직여 올바른 위치를 지정해 주세요');
        return false;
    }
    if (addForm['thumbnail'].files.length < 1) {
        alert('대표 이미지를 선택해 주세요');
        return false;
    }
    if(!addForm['opSun'].checked && !addForm['opMon'].checked && !addForm['opTue'].checked && !addForm['opWed'].checked && !addForm['opThu'].checked && !addForm['opFri'].checked && !addForm['opSat'].checked) {
        alert('주중 하루 이상 영업해야 합니다.');
        return false;
    }
    const dayEn = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
    const dayKo = ['일','월','화','수','목','금','토'];
    for(let i = 0; i < dayEn.length; i++) {
        if (addForm[`op${dayEn[i]}`].checked && (addForm[`open${dayEn[i]}`].value === '' || addForm[`close${dayEn[i]}`].value === '')) {
            alert(`${dayKo[i]}요일 영업 시간을 선택해주세요`);
            return false;
        }
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    const time = {};
    for (const day of dayEn) {
        const operates = addForm[`op${day}`].checked;  // 영업 여부를 input체크 여부로 판단
        const open = addForm[`open${day}`].value;       // 오픈시간
        const close = addForm[`close${day}`].value;     // 마감시간
        time[day] = {
            'operates' : operates,
            'open' : open,
            'close' : close
        };
        // Mon : { 골의 형식으로 됨
        //     operates: true,
        //         open: '09:30',
        //         close: '22:00'
        // }
    }
    formData.append('name',addForm['name'].value);
    formData.append('contactFirst', addForm['contactFirst'].value);
    formData.append('contactSecond', addForm['contactSecond'].value);
    formData.append('contactThird', addForm['contactThird'].value);
    formData.append('description', addForm['description'].value);
    formData.append('latitude', addForm['lat'].value);
    formData.append('longitude', addForm['lng'].value);
    formData.append('addressPrimary', addForm['addressPrimary'].value);
    formData.append('addressSecondary', addForm['addressSecondary'].value);
    formData.append('thumbnailMultipart', addForm['thumbnail'].files[0]);
    formData.append('time', JSON.stringify(time));
    // JSON.stringify(x) 는 전달된 배열 혹은 오브젝트인 x 를 문자열인 JSON으로 바꾸어줌. (JSON.parse 랑 반대임)
    xhr.open('POST','/place/' );
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300 ) {
                if(xhr.responseText === 'true'){
                    alert('굿');
                }else {
                    alert('낫 굿');
                }

            } else {
                alert('통신')

            }
        }
    };
    xhr.send(formData);
};


if (addMatzip){
    addMatzip.onclick = function() {
        addForm.show();

    };
}