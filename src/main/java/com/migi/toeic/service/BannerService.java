package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.BannerDTO;
import com.migi.toeic.exception.BusinessException;
import org.apache.cxf.Bus;

import java.util.List;

public interface BannerService {
    public DataListDTO getBanners(BannerDTO obj) throws BusinessException;
    public DataListDTO getBannersActive(BannerDTO obj) throws BusinessException;
    public DataListDTO getBannersActive() throws BusinessException;
    public DataListDTO getBannersNotActive(BannerDTO bannerDTO) throws BusinessException;
    public BannerDTO getDetail(Long id) throws BusinessException;
    public String createBanner(BannerDTO obj) throws BusinessException;
    public String updateBanner(BannerDTO obj) throws BusinessException;
    public String deleteBanner(BannerDTO obj) throws BusinessException;
    public DataListDTO getListBanner() throws BusinessException;
    public String updateOrderBanner(List<BannerDTO> listBanner) throws BusinessException;
}
