// 북동 남서 방향 좌표 얻어오기 카카오 문서
const placeList = document.getElementById('placeList');

let places = [];
let placeMakers = [];

function loadPlaces() {
    function createListItem(place) {
        const htmlText = `
        <li class="item">
            <div class="spec-container">
                <div class="name-container">
                    <span class="name">${place['name']}</span>
                </div>
                <div class="op-container">
                    <span class="op-flag">나중에</span>
                    <span class="op-time">나중에</span>
                </div>
                <div class="address-container">
                    <span class="address">${place['addressPrimary']}</span>
                </div>
            </div>
            <div class="image-container">
                <img alt="" class="image" src="/place/thumbnail?index=${place['index']}">
                <span class="count">?</span>
            </div>
        </li>`;
        return new DOMParser().parseFromString(htmlText, 'text/html').querySelector('li');

        // const domParser = new DOMParser();
        // const dom = domParser.parseFromString(htmlText, 'text/html');
        // const listItem = dom.querySelector('li');
        // return listItem;
    }


    const sw = mapElement.object.getBounds().getSouthWest();
    const ne = mapElement.object.getBounds().getNorthEast();
    //sw/ne의 Ma -> latitude 랑 비교
    //sw/ne의 La -> longtitude 랑 비교
    console.log(sw);
    console.log(ne);

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/place/?minLat=${sw.Ma}&minLng=${sw.La}&maxLat=${ne.Ma}&maxLng=${ne.La}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const incomingPlaces = JSON.parse(xhr.responseText);
                placeList.querySelectorAll(':scope > .item').forEach(item => item.remove());
                // 마커 지우기
                for (const placeMaker of placeMakers) {
                    placeMaker.setMap(null);
                }
                placeMakers = [];
                // 마커 지우기=============================
                const places = JSON.parse(xhr.responseText);
                for (const place of places) {
                    const listItem = createListItem(place);
                    listItem.onclick = function() {
                        detail.show(place);
                    }
                    placeList.append(listItem);

                    const position = new kakao.maps.LatLng(place['latitude'], place['longitude']);
                    const marker = new kakao.maps.Marker({
                        'position': position
                    });
                    kakao.maps.event.addListener(marker, 'click', function(){
                        detail.show(place);
                    });
                    marker.setMap(mapElement.object); // 맵에 마커 찍기
                    placeMakers.push(marker);
                }
            } else {

            }
        }
    };
    xhr.send();
}

const detail = document.getElementById('detail');
detail.thumbnail = detail.querySelector('[rel="thumbnail"]');
detail.name = detail.querySelector('[rel="name"]');
detail.contact = detail.querySelector('[rel="contact"]');
detail.address = detail.querySelector('[rel="address"]');
detail.description = detail.querySelector('[rel="description"]');
detail.time = detail.querySelector('[rel="time"]');
const detailClose = document.getElementById('detailClose');

detail.show = function (place) {
    detail.thumbnail.setAttribute('src', `/place/thumbnail?index=${place['index']}`);
    detail.name.innerText = place['name'];
    detail.contact.innerText = `${place['contactFirst']}-${place['contactSecond']}-${place['contactThird']}`;
    detail.address.innerText = `${place['addressPrimary']} ${place['addressSecondary']}`;
    detail.description.innerText = place['description'];

    const daysEn = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    const daysKo = ['일', '월', '화', '수', '목', '금', '토'];
    for (const day of daysEn) {
        const dayObject = JSON.parse(place['time'])[day];
        const tr = detail.time.querySelector(`[data-day="${day}"]`);
        if (dayObject['operates'] === true) {
            tr.classList.remove('off');
            tr.innerHTML = `<th>${daysKo[daysEn.indexOf(day)]}</th>
                            <td>${dayObject['open']}</td>
                            <td>${dayObject['close']}</td>`
        }else {
            tr.classList.add('off');
            tr.innerHTML = `<th>${daysKo[daysEn.indexOf(day)]}</th><td colspan="2">휴업</td>`;
        }
    }
    detail.classList.add('visible');
};

detailClose.onclick = function () {
    detail.hide()
};