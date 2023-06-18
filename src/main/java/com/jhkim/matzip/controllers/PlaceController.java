package com.jhkim.matzip.controllers;

import com.jhkim.matzip.entities.PlaceEntity;
import com.jhkim.matzip.entities.UserEntity;
import com.jhkim.matzip.services.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping(value = "/place")
public class PlaceController {
    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PlaceEntity[] getIndex(@RequestParam(value = "minLat") double minLat,
                                  @RequestParam(value = "minLng") double minLng,
                                  @RequestParam(value = "maxLat") double maxLat,
                                  @RequestParam(value = "maxLng") double maxLng) {
        return this.placeService.getPlaces(minLat, minLng, maxLat, maxLng);
    }

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(@SessionAttribute(value = "user") UserEntity user,
                            PlaceEntity place,
                            @RequestParam(value = "thumbnailMultipart") MultipartFile thumbnailMultipart) throws IOException {
        place.setThumbnail(thumbnailMultipart.getBytes())
                .setThumbnailMime(thumbnailMultipart.getContentType())
                .setRegisteredBy(user.getEmail());
        boolean result = this.placeService.putPlace(place);
        return String.valueOf(result);
    }

    @RequestMapping(value = "thumbnail",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getThumbnail(@RequestParam(value = "index")int index) {
        PlaceEntity place = this.placeService.getPlace(index);
        ResponseEntity<byte[]> response;
        if (place == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(place.getThumbnail().length);
            headers.setContentType(MediaType.parseMediaType(place.getThumbnailMime()));
            response = new ResponseEntity<>(place.getThumbnail(), headers, HttpStatus.OK);
        }
        return response;
    }
}
