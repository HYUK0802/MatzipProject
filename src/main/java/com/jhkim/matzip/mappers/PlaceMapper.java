package com.jhkim.matzip.mappers;

import com.jhkim.matzip.entities.PlaceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlaceMapper {
    int insertPlace(PlaceEntity place);

    PlaceEntity selectPlaceByIndex(@Param(value = "index")int index);

    PlaceEntity[] selectPlacesInRangeNoThumbnail(@Param(value = "minLat") double minLat,
                                                @Param(value = "minLng") double minLng,
                                                @Param(value = "maxLat") double maxLat,
                                                @Param(value = "maxLng") double maxLng);
}
