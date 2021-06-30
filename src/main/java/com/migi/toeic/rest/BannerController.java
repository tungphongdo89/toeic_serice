package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.BannerDTO;
import com.migi.toeic.service.impl.BannerServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(description = "API BANNER MANAGEMENT")

@RequestMapping("/v1/banner")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BannerController {
    @Autowired
    BannerServiceImpl bannerServiceImpl;

    @PostMapping(value = "/doSearch")
    public ResponseEntity<?> doSearch(@RequestBody BannerDTO obj) {
        DataListDTO data = bannerServiceImpl.getBanners(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping(value = "/getBannersActive")
    public ResponseEntity<?> getBannersActive(@RequestBody BannerDTO obj) {
        DataListDTO data = bannerServiceImpl.getBannersActive(obj);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/getBannersActive")
	public ResponseEntity<?> getBannersActive() {
		DataListDTO data = bannerServiceImpl.getBannersActive();
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@PostMapping(value = "/getBannersNotActive")
	public ResponseEntity<?> getBannersNotActive(@RequestBody BannerDTO obj) {
		DataListDTO data = bannerServiceImpl.getBannersNotActive(obj);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getListBanner")
	public ResponseEntity<?> getListBanner()  {
		DataListDTO dataListDTO = bannerServiceImpl.getListBanner();
		return new ResponseEntity<>(dataListDTO, HttpStatus.OK);
	}
    
    @PostMapping(value = "/getDetail")
    public ResponseEntity<?> getDetail(@RequestBody BannerDTO obj) {
        BannerDTO banner = bannerServiceImpl.getDetail(obj.getBannerId());
        return new ResponseEntity<>(banner, HttpStatus.OK);
    }

    @PostMapping(value = "/create" ,consumes = "multipart/form-data")
    public ResponseEntity<?> create(@ModelAttribute  BannerDTO obj) {
        return new ResponseEntity<>(bannerServiceImpl.createBanner(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<?> update(@ModelAttribute BannerDTO obj) {
        return new ResponseEntity<>(bannerServiceImpl.updateBanner(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestBody BannerDTO obj) {
        return new ResponseEntity<>(bannerServiceImpl.deleteBanner(obj), HttpStatus.OK);
    }

    @PostMapping(value = "/updateOrderBanner")
    public ResponseEntity<?> updateOrderBanner(@RequestBody List<BannerDTO> listBanner){
        return new ResponseEntity<>(bannerServiceImpl.updateOrderBanner(listBanner), HttpStatus.OK);
    }
}

