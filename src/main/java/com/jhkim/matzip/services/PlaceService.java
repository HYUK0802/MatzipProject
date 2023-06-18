package com.jhkim.matzip.services;

import com.jhkim.matzip.entities.PlaceEntity;
import com.jhkim.matzip.mappers.PlaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PlaceService {
    private final PlaceMapper placeMapper;

    @Autowired
    public PlaceService(PlaceMapper placeMapper) {
        this.placeMapper = placeMapper;
    }

    public PlaceEntity getPlace(int index) {
        return this.placeMapper.selectPlaceByIndex(index);
    }

    // 좌표거리 내에
    public PlaceEntity[] getPlaces(double minLat, double minLng, double maxLat, double maxLng){
        return this.placeMapper.selectPlacesInRangeNoThumbnail(minLat,minLng,maxLat,maxLng);
    }

    public boolean putPlace(PlaceEntity place){
        place.setRegisteredAt(new Date())
                .setDeleted(false);
        return this.placeMapper.insertPlace(place) > 0;
        }

}
