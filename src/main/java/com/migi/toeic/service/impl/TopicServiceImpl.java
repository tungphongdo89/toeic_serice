package com.migi.toeic.service.impl;

import com.google.common.collect.Lists;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.dto.TopicDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.Topic;
import com.migi.toeic.respositories.CategoryRepository;
import com.migi.toeic.respositories.TopicRepository;
import com.migi.toeic.service.TopicService;
import com.migi.toeic.utils.MessageUtils;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public DataListDTO getListTopic(TopicDTO obj) {
        if (null != obj.getTopicName()) {
            String topicName = obj.getTopicName().trim();
            obj.setTopicName(topicName);
        }

        ResultDataDTO resultDto = topicRepository.getListTopic(obj);
        DataListDTO data = new DataListDTO();
        data.setData(resultDto.getData());
        data.setTotal(resultDto.getTotal());
        data.setSize(resultDto.getTotal());
        data.setStart(1);
        return data;
    }

    @Override
    public TopicDTO getDetail(String code) {
        if (code == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        return topicRepository.getDetail(code);
    }

    @SuppressWarnings("unused")
    @Override
    public String createTopic(TopicDTO obj, HttpServletRequest request) {
        if (obj.getTopicName() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }
        TopicDTO topic = topicRepository.checkTopicExist(obj);
        if (null != topic) {
            throw new BusinessException(MessageUtils.getMessage("error_exit_topic_name"));
        }

//        LocalDateTime localDateTime = LocalDateTime.now();
//        Date createDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));

        RandomString gen = new RandomString(4);
        String code = gen.nextString();
        TopicDTO topic2 = topicRepository.checkTopicCode(code);

        while (null != topic2) {
            gen = new RandomString(4);
            code = gen.nextString();
            topic2 = topicRepository.checkTopicCode(code);
        }

        obj.setCode(code);
        obj.setCreatedTime(new Date());
        obj.setLastUpdate(new Date());
        obj.setStatus((long) 1);
        topicRepository.insert(obj.toModel());
        return MessageUtils.getMessage("create_topic_success");
    }

    @Override
    public String updateTopic(TopicDTO obj, HttpServletRequest request) {


        if (!topicRepository.isCheckTopicName(obj)) {
            throw new BusinessException(MessageUtils.getMessage("error_exit_topic_name"));

        }
        TopicDTO topic2 = topicRepository.getDetail(obj.getCode());

//        LocalDateTime localDateTime = LocalDateTime.now();
//        Date updateDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));

        if (obj.getTopicName() == null) {
            throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
        }

        if (!topic2.getTopicName().equalsIgnoreCase(obj.getTopicName())) {

                obj.setLastUpdate(new Date());
                obj.setStatus((long) 1);

        } else {
            obj.setLastUpdate(new Date());
            obj.setStatus((long) 1);
        }

        topicRepository.update(obj.toModel());
        return MessageUtils.getMessage("update_topic_success");

    }

    @Override
    public String deleteTopic(TopicDTO obj, HttpServletRequest request) {


        Topic topic = topicRepository.findByFiled("topicId", obj.getTopicId());
        if (null == topic) {
            throw new BusinessException(MessageUtils.getMessage("error_not_exit_topic"));
        }
        if (!categoryRepository.isCheckTopic(topic.getTopicId())) {
            throw new BusinessException(MessageUtils.getMessage("error_not_exit_category"));

        }
        topicRepository.delete(topic);
        return MessageUtils.getMessage("delete_topic_success");
    }

    @Override
    public List<List<TopicDTO>> getListTopicByPartAndLevel(String part, String value) {
        List<List<TopicDTO>> listTopicByPartAndLevel = Lists.newArrayList();
        listTopicByPartAndLevel.add(topicRepository.getListTopicEasyByPart(part, value));
        listTopicByPartAndLevel.add(topicRepository.getListTopicMediumByPart(part, value));
        listTopicByPartAndLevel.add(topicRepository.getListTopicDifficultByPart(part, value));
        return listTopicByPartAndLevel;
    }

    @Override
    public List<TopicDTO> getListTopicByPart(String part) {
        List<TopicDTO> listTopicByPart = topicRepository.getListTopicByPart(part);
        return listTopicByPart;
    }

    @Override
    public List<TopicDTO> getListTypeExercise() {
        List<TopicDTO> listTypeExercise = topicRepository.getListTypeExercise();
        return listTypeExercise;
    }

    @Override
    public List<TopicDTO> getListPartExercise(String typeExercie) {
        List<TopicDTO> listPartExercise = topicRepository.getListPartExercise(typeExercie);
        return listPartExercise;
    }

    @Override
    public List<TopicDTO> getListPractices() {
        List<TopicDTO> listPractices = topicRepository.getListPractices();
        return listPractices;
    }

    @Override
    public List<TopicDTO> getListTopicName(String part_topic_code, String type_topic_code) {
        return topicRepository.getTopicName(part_topic_code, type_topic_code);
    }

    @Override
    public List<TopicDTO> getListTopicByName(TopicDTO topicDTO) {
        List<TopicDTO> lst = topicRepository.getTopicByNameAndPart(topicDTO);
        return lst;
    }

    @Override
    public TopicDTO getTopicByName(String code) {
        Topic topic = topicRepository.findByFiled("code", code);
        return null != topic ? topic.toModel() : null;
    }

    @Override
    @Transactional
    public Long insert(TopicDTO obj) {
        return topicRepository.insert(obj.toModel());
    }

}

