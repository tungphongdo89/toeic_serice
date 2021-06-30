package com.migi.toeic.service;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.dto.TopicDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TopicService {
	DataListDTO getListTopic(TopicDTO obj);

	TopicDTO getDetail(String code);

	String createTopic(TopicDTO obj, HttpServletRequest request);

	String updateTopic(TopicDTO obj, HttpServletRequest request);

	String deleteTopic(TopicDTO obj, HttpServletRequest request);

	List<List<TopicDTO>> getListTopicByPartAndLevel(String part, String value);

	List<TopicDTO> getListTopicByPart(String part);

	List<TopicDTO> getListTypeExercise();

	List<TopicDTO> getListPartExercise(String typeExercie);

	List<TopicDTO> getListPractices();

	List<TopicDTO> getListTopicName(String part_topic_code, String type_topic_code);

	List<TopicDTO> getListTopicByName(TopicDTO topicDTO);

	TopicDTO getTopicByName(String code);

	Long insert(TopicDTO obj);
}
