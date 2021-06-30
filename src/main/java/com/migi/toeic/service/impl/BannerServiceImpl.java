package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.base.common.amazons3.UpFileAmazon;
import com.migi.toeic.dto.BannerDTO;
import com.migi.toeic.respositories.BannerRepository;
import com.migi.toeic.service.BannerService;
import com.migi.toeic.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.utils.MessageUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {
    @Autowired
    BannerRepository bannerRepository;

    @Override
    public DataListDTO getBanners(BannerDTO obj) {
        ResultDataDTO resultDto = bannerRepository.getBanners(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public DataListDTO getBannersActive(BannerDTO obj) {
        ResultDataDTO resultDto = bannerRepository.getBannersActive(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public DataListDTO getBannersActive() {
        ResultDataDTO resultDto = bannerRepository.getBannersActive();
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public DataListDTO getBannersNotActive(BannerDTO bannerDTO) {
        if (bannerDTO.getCreateFrom() != null) {
            String x = DateUtil.convertTimeDisplay(bannerDTO.getCreateFrom());
            bannerDTO.setCreateFromString(x);
        }
        if (bannerDTO.getCreateTo() != null) {
            String x = DateUtil.convertTimeDisplay(bannerDTO.getCreateTo());
            bannerDTO.setCreateToString(x);
        }
        if (bannerDTO.getCreateFrom() != null && bannerDTO.getCreateTo() != null) {
            if (DateUtil.compareDate(bannerDTO.getCreateFrom(), bannerDTO.getCreateTo()) == 0) {
                throw new BusinessException("Tạo banner từ phải nhỏ hơn thời gian tạo banner đến");
            }
            bannerDTO.setCreateFromString(DateUtil.convertTimeDisplay(bannerDTO.getCreateFrom()));
            bannerDTO.setCreateToString(DateUtil.convertTimeDisplay(bannerDTO.getCreateTo()));
        }
        ResultDataDTO resultDto = bannerRepository.getBannersNotActive(bannerDTO);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public DataListDTO getListBanner() {
        ResultDataDTO resultDto = bannerRepository.getListBanner();
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public BannerDTO getDetail(Long id) throws BusinessException {
        if (id == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return bannerRepository.getDetail(id);
    }

    @Override
    public String createBanner(BannerDTO obj) throws BusinessException {
        UpFileAmazon upFileAmazon = new UpFileAmazon();
        LocalDateTime localDateTime = LocalDateTime.now();
        Date createDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        List<BannerDTO> bannerDTO = bannerRepository.checkBannerName(obj.getBannerName().trim());
        obj.setCreatedTime(createDate);
        obj.setLastUpdate(createDate);
        if (obj.getBannerName() == null  || StringUtils.isBlank(obj.getBannerName()) ) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info_banner"));
        }
        if(obj.getFileUpload() == null){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info_uploadfile"));
        }
        if (bannerDTO.size() != 0) {
            throw new BusinessException(MessageUtils.getMessage("error_exit_banner_name"));
        }
        if ((obj.getStatus() == 1) && (obj.getOrderBanner() < 1 || obj.getOrderBanner() > 5)) {
            throw new BusinessException(MessageUtils.getMessage("not_selected_banner"));
        }
        List<BannerDTO> oldOrderBannerList = bannerRepository.checkOrderBanner(obj.getOrderBanner());
        if ((obj.getStatus() == 1) && (1 <= obj.getOrderBanner() && obj.getOrderBanner() <= 5) && oldOrderBannerList.size()!=0) {
                if (oldOrderBannerList.size() == 1) {
                    BannerDTO oldOrderBanner = oldOrderBannerList.get(0);
                    oldOrderBanner.setOrderBanner(0l);
                    oldOrderBanner.setStatus(0l);
                    oldOrderBanner.setLastUpdate(createDate);
                    bannerRepository.update(oldOrderBanner.toModel());
                }else {
                    throw new BusinessException(MessageUtils.getMessage("create_orderbanner_false"));
                }
        }
        try{
                obj.setPathImage(upFileAmazon.UpLoadFile(obj.getFileUpload() , null , null));
        }catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        obj.setBannerName(obj.getBannerName().trim());
        try {
            Long result = bannerRepository.insert(obj.toModel());
            if (result != null){
                return MessageUtils.getMessage("create_banner_success");
            }else{
                throw new BusinessException( MessageUtils.getMessage("create_banner_false"));
            }
        }catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    @Override
    public String updateBanner(BannerDTO obj) throws BusinessException {
        UpFileAmazon upFileAmazon = new UpFileAmazon();
        BannerDTO bannerDTO = bannerRepository.getDetail(obj.getBannerId());
        // BannerDTO checkOldBanner = bannerRepository.checkOrderBanner(obj.getOrderBanner());
        LocalDateTime localDateTime = LocalDateTime.now();
        Date createDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        List<BannerDTO> bannerDTOList = bannerRepository.checkBannerName(obj.getBannerName().trim());
        if (null == bannerDTO) {
            throw new BusinessException(MessageUtils.getMessage("error_banner_not_exist"));
        }
        obj.setCreatedTime(bannerDTO.getCreatedTime());
        obj.setLastUpdate(createDate);
        if (obj.getBannerName() == null || StringUtils.isBlank(obj.getBannerName()) ) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info_banner"));
        }
        if(obj.getPathImage() == null && obj.getFileUpload() == null ){
            throw new BusinessException(MessageUtils.getMessage("error_miss_info_uploadfile"));
        }
        if (bannerDTOList.size() != 0 && obj.getBannerName().trim().equals(bannerDTO.getBannerName()) == false) {
            throw new BusinessException(MessageUtils.getMessage("error_exit_banner_name"));
        }
        List<BannerDTO> oldOrderBannerList = bannerRepository.checkOrderBanner(obj.getOrderBanner());
        if ((obj.getStatus() == 1) && (1 <= obj.getOrderBanner() && obj.getOrderBanner() <= 5) && oldOrderBannerList.size()!=0) {
            if (oldOrderBannerList.size() == 1) {
                BannerDTO oldOrderBanner = oldOrderBannerList.get(0);
                oldOrderBanner.setOrderBanner(0l);
                oldOrderBanner.setStatus(0l);
                oldOrderBanner.setLastUpdate(createDate);
                bannerRepository.update(oldOrderBanner.toModel());
            }else {
                throw new BusinessException(MessageUtils.getMessage("create_orderbanner_false"));
            }
        }
//      if(null != checkOldBanner){
//      if(checkOldBanner.getStatus()==1){
//            checkOldBanner.setOrderBanner(Integer.toUnsignedLong(0));
//            checkOldBanner.setStatus(Integer.toUnsignedLong(0));
//            bannerRepository.update(checkOldBanner.toModel());
//        }
//        }

        try {
            if (obj.getFileUpload() != null) {
                obj.setPathImage(upFileAmazon.UpLoadFile(obj.getFileUpload(), null, null));
            }
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.getMessage("error_banner_path_img"));
        }
        obj.setBannerName(obj.getBannerName().trim());
        bannerRepository.update(obj.toModel());
        return MessageUtils.getMessage("update_banner_success");
    }

    @Override
    public String deleteBanner(BannerDTO obj) {
        BannerDTO bannerDTO = bannerRepository.getDetail(obj.getBannerId());
        if (null == bannerDTO) {
            throw new BusinessException(MessageUtils.getMessage("error_banner_not_exist"));
        }
        if (bannerDTO.getStatus() == 1) {
            throw new BusinessException(MessageUtils.getMessage("delete_error__banner_not_exist"));
        }
        bannerRepository.delete(bannerDTO.toModel());
        return MessageUtils.getMessage("delete_banner_success");
    }

    @Override
    public String updateOrderBanner(List<BannerDTO> listBanner) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date createDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        for (int i = 0; i < listBanner.size(); i++) {
            BannerDTO bannerDTO = bannerRepository.findByFiled("bannerId", listBanner.get(i).getBannerId()).toModel();
            if (bannerDTO != null) {
                bannerDTO.setOrderBanner(Long.valueOf(i + 1));
                bannerDTO.setLastUpdate(createDate);
                bannerRepository.update(bannerDTO.toModel());
            }
        }
        return MessageUtils.getMessage("update_banner_success");
    }
}

