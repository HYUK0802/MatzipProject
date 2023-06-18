const mapElement = document.getElementById('map');
mapElement.geocoder = new kakao.maps.services.Geocoder();
mapElement.init = (params) => {
    mapElement.object = new kakao.maps.Map(mapElement, {
        center: new kakao.maps.LatLng(params.latitude, params.longitude),
        level: params.level
    });
    ['dragend', 'zoom_changed'].forEach(event => kakao.maps.event.addListener(mapElement.object, event, () => {
        loadPlaces();


        const center = mapElement.object.getCenter();
        mapElement.savePosition({
            latitude: center.Ma,
            longitude: center.La,
            level: mapElement.object.getLevel()
        });
        mapElement.geocoder.coord2Address(center.La, center.Ma, (result, status) => {
            if (status !== kakao.maps.services.Status.OK) {
                addForm['addressPrimary'].value = '';
                addForm['lat'].value = '';
                addForm['lng'].value = '';
                return;
            }
            listElement.addressGu.innerText = result[0]['address']['region_2depth_name'];
            listElement.addressDong.innerText = result[0]['address']['region_3depth_name'];
            if (addForm.classList.contains('visible')) {
                addForm['addressPrimary'].value = result[0]['address']['address_name'];
                addForm['lat'].value = center.Ma;
                addForm['lng'].value = center.La;

            }
        });
    }));
};

mapElement.savePosition = (params) => {
    localStorage.setItem('latitude', params.latitude);
    localStorage.setItem('longitude', params.longitude);
    localStorage.setItem('level', params.level);
};

if (localStorage.getItem('latitude') &&
    localStorage.getItem('longitude') &&
    localStorage.getItem('level')) {
    mapElement.init({
        latitude: parseFloat(localStorage.getItem('latitude')),
        longitude: parseFloat(localStorage.getItem('longitude')),
        level: parseInt(localStorage.getItem('level'))
    });
} else {
    navigator.geolocation.getCurrentPosition(e => {
        mapElement.init({
            latitude: e.coords.latitude,
            longitude: e.coords.longitude,
            level: 3
        });
    }, () => {
        mapElement.init({
            latitude: 35.8715411,
            longitude: 128.601505,
            level: 3
        });
    });
}
// data.data.forEach(item => {
//     mapElement.geocoder.addressSearch(item.GNG_CS, (result, status) => {
//         if (status === kakao.maps.services.Status.OK && result.length > 0 {
//             const hrs = item.MBZ_HR.split(' ~ ');
//             const hrFrom = hrs[0];
//             const hrTo = hrs.length > 1 ? hrs[1] : '';
//             if (hrRegExp.test(hrFrom) && hrRegExp.test(hrTo)) {
//                 map
//
//             }
//         })
//     })
// })