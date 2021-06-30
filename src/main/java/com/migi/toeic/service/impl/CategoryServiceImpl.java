package com.migi.toeic.service.impl;

import com.migi.toeic.authen.model.RequestPractice;
import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.base.common.DataDTO;
import com.migi.toeic.base.common.FileUtils;
import com.migi.toeic.base.common.amazons3.UpFileAmazon;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.Category;
import com.migi.toeic.model.DetailsHistory;
import com.migi.toeic.respositories.*;
import com.migi.toeic.service.CategoryService;
import com.migi.toeic.utils.DateUtil;
import com.migi.toeic.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service

@AllArgsConstructor
@NoArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	private ApParamRepository apParamRepository;
	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	QuestionAnswerListeningRepository q_a_ListeningRepository;

	@Autowired
	QuestionAnswerReadingRepository q_a_ReadingRepository;

	@Autowired
	QuestionAnswerTranslationAVRepository q_a_TranslationAVRepository;

	@Autowired
	QuestionAnswerTranslationVARepository q_a_TranslationVARepository;
	@Autowired
	HistoryPracticesRepository historyPracticesRepository;
	@Autowired
	DetailHistoryReadingRepository detailHistoryReadingRepository;
    @Autowired
    DetailHistoryReadingSingleRepository detailHistoryReadingSingleRepository;

    @Autowired
	DetailHistoryLisSingleRepository detailHistoryLisSingleRepository;

    @Autowired
	DetailHistoryListenFillRepository detailHistoryListenFillRepository;

    @Autowired
	DetailHistoryListeningRepository detailHistoryListeningRepository;

    @Autowired
	DetailHistoryMinitestRepository detailHistoryMinitestRepository;

    @Autowired
	DetailHistoryRepository detailHistoryRepository;


	@Override
	public DataListDTO getListCategory(CategoryDTO obj) throws Exception {
		if (obj.getCreatedDateFrom() != null) {
			obj.setCreatedDateFromString(DateUtil.convertTimeDisplay(obj.getCreatedDateFrom()));
		}
		if (obj.getCreatedDateTo() != null) {
			obj.setCreatedDateToString(DateUtil.convertTimeDisplay(obj.getCreatedDateTo()));
		}

		ResultDataDTO resultDto = categoryRepository.getListCategory(obj);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Override
	public CategoryDTO getDetail(Long id) {
		if (id == null) {
			throw new BusinessException(MessageUtils.getMessage("error_miss_info"));
		}
		CategoryDTO categoryDTO = categoryRepository.findByFiled("categoryId", id).toModel();
		List<QuestionAnswersDTO> lstQuestion = categoryRepository.getListQuestionByCategory(id);

		for (QuestionAnswersDTO question : lstQuestion) {
			List<QuestionAnswersDTO> listAnswerToChoose = new ArrayList<QuestionAnswersDTO>();
			List<String> listCorrectAnswers = new ArrayList<String>();

			if (question.getAnswersToChoose() == null) {
				question.setTypeQuestion(2l);
			} else {
				question.setTypeQuestion(1l);
			}

			if (question.getTypeQuestion() == 1) {
				String[] answerToChoose = question.getAnswersToChoose().split("[|]");
				if (question.getStartTime() == null) {
					StringBuilder startTm = new StringBuilder("");
					for (int i = 0; i < answerToChoose.length; i++) {
						startTm.append(" |");
					}
					question.setStartTime(startTm.toString());
				}
				String[] startTime = question.getStartTime().split("[|]");

				for (int i = 0; i < answerToChoose.length; i++) {
					QuestionAnswersDTO qe = new QuestionAnswersDTO();
					qe.setAnswer(answerToChoose[i]);
					qe.setStartTime(startTime[i]);
					listAnswerToChoose.add(qe);
				}
				String[] answers = question.getAnswer().split("[|]");
				for (int i = 0; i < answers.length; i++) {
					listCorrectAnswers.add(answers[i]);
				}
				question.setListAnswerToChoose(listAnswerToChoose);
				question.setListCorrectAnswers(listCorrectAnswers);

			} else {
				if (question.getAnswer() != null) {

					String[] answers = question.getAnswer().split("[|]");
					for (int i = 0; i < answers.length; i++) {
						listCorrectAnswers.add(answers[i]);
					}
				}
				question.setListCorrectAnswers(listCorrectAnswers);
			}
		}
		categoryDTO.setListQuestion(lstQuestion);


		TopicDTO topicDTO = topicRepository.findByFiled("id", categoryDTO.getParentId()).toModel();

		if (topicDTO.getPartTopicCode() != null) {
			ApParamDTO apParamDTO = apParamRepository.findByFiled("code", topicDTO.getPartTopicCode()).toModel();
			categoryDTO.setNamePart(apParamDTO.getName());
		}

		categoryDTO.setPart(topicDTO.getPartTopicCode());
		categoryDTO.setNameTopic(topicDTO.getTopicName());

		return categoryDTO;
	}

	@Override
	public String createCategory(CategoryDTO obj) {
		UpFileAmazon upFileAmazon = new UpFileAmazon();
		FileUtils fileUtils = new FileUtils();

		LocalDateTime localDateTime = LocalDateTime.now();
		Date createDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));

		if (obj.getPathFile1() != null && obj.getPathFile1() != "") {
			obj.setPathFile1(obj.getPathFile1());
		}
		if (obj.getPathFile2() != null && obj.getPathFile2() != "") {
			obj.setPathFile2(obj.getPathFile2());
		}

		obj.setCreatedDate(createDate);
		Long idCateInsert = categoryRepository.insert(obj.toModel());

		// -------------------------------------------Upload file đề bài-------------------------------------------
		try {
			CategoryDTO cateInserted = categoryRepository.findByFiled("categoryId", idCateInsert).toModel();
			String pathFileCate1 = "";
			String pathFileCate2 = "";
			if (obj.getFileUpload1() != null) {
				pathFileCate1 = upFileAmazon.UpLoadFile(obj.getFileUpload1(), idCateInsert, null);
				cateInserted.setPathFile1(pathFileCate1);
			}
			if (obj.getFileUpload2() != null) {
				pathFileCate2 = upFileAmazon.UpLoadFile(obj.getFileUpload2(), idCateInsert, null);
				cateInserted.setPathFile2(pathFileCate2);
			}
			categoryRepository.update(cateInserted.toModel());
		} catch (Exception e) {
			throw new BusinessException("Lỗi upload file đề bài có ID : " + idCateInsert);
		}

		if (obj.getTypeCode() == 1) {
			for (QuestionAnswersDTO question : obj.getListQuestion()) {
				QuestionAnswersReadingDTO q_a_ReadingDTO = new QuestionAnswersReadingDTO();
				q_a_ReadingDTO.setName(question.getName());
				q_a_ReadingDTO.setParentId(idCateInsert);
				q_a_ReadingDTO.setStatus(1L);
				q_a_ReadingDTO.setDescription(question.getDescription());
				q_a_ReadingDTO.setScore(question.getScore());

				if (question.getListCorrectAnswers().size() > question.getListAnswerToChoose().size()) {
					throw new BusinessException(MessageUtils.getMessage("error_length_listCorrectAnswer"));
				}

				if (question.getListAnswerToChoose() != null) {
					String answersToChoose = "";
					for (QuestionAnswersDTO ques : question.getListAnswerToChoose()) {
						answersToChoose += ques.getAnswer().toUpperCase() + " |";
					}
					q_a_ReadingDTO.setAnswersToChoose(answersToChoose);
					q_a_ReadingDTO.setNumOfAnswer(Long.valueOf(question.getListAnswerToChoose().size()));
				}

				if (question.getListCorrectAnswers() != null) {
					String correctAnswer = "";
					for (String answer : question.getListCorrectAnswers()) {
						correctAnswer += answer.toUpperCase() + " |";
					}
					q_a_ReadingDTO.setAnswer(correctAnswer);
				}
				Long idQuestion = q_a_ReadingRepository.insert(q_a_ReadingDTO.toModel());
			}
		} else if (obj.getTypeCode() == 2 || obj.getTypeCode() == 5) {
			for (QuestionAnswersDTO question : obj.getListQuestion()) {
				QuestionAnswerListeningDTO q_a_ListeningDTO = new QuestionAnswerListeningDTO();
				q_a_ListeningDTO.setName(question.getName());
				q_a_ListeningDTO.setParentId(idCateInsert);
				q_a_ListeningDTO.setStatus(1L);
				q_a_ListeningDTO.setDescription(question.getDescription());
				q_a_ListeningDTO.setScore(question.getScore());
				q_a_ListeningDTO.setTypeFile1(question.getTypeFile1());
				q_a_ListeningDTO.setTypeFile2(question.getTypeFile2());

				if (question.getPathFile1() != null && question.getPathFile1() != "") {
					q_a_ListeningDTO.setPathFile1(question.getPathFile1());
				}
				if (question.getPathFile2() != null && question.getPathFile2() != "") {
					q_a_ListeningDTO.setPathFile2(question.getPathFile2());
				}

				if (question.getListCorrectAnswers().size() > question.getListAnswerToChoose().size() && question.getTypeQuestion() == 1) {
					throw new BusinessException(MessageUtils.getMessage("error_length_listCorrectAnswer"));
				}

				if (question.getListAnswerToChoose() != null) {
					String answersToChoose = "";
					String startTime = "";
					for (QuestionAnswersDTO ques : question.getListAnswerToChoose()) {
						answersToChoose += ques.getAnswer().toUpperCase() + " |";
						startTime += ques.getStartTime() + " |";
					}
					q_a_ListeningDTO.setAnswersToChoose(answersToChoose);
					q_a_ListeningDTO.setNumOfAnswer(Long.valueOf(question.getListAnswerToChoose().size()));
					q_a_ListeningDTO.setStartTime(startTime);
				}

				if (question.getListCorrectAnswers() != null) {
					String correctAnswer = "";
					for (String answer : question.getListCorrectAnswers()) {
						correctAnswer = correctAnswer + answer.toUpperCase() + " |";
					}
					q_a_ListeningDTO.setAnswer(correctAnswer);
				}
				Long idQuestion = q_a_ListeningRepository.insert(q_a_ListeningDTO.toModel());

				// -------------------------------------------Upload file câu hỏi-------------------------------------------
				try {
					QuestionAnswerListeningDTO qaListeningDTO = q_a_ListeningRepository.findByFiled("id", idQuestion).toModel();
					String pathFile1 = "";
					String pathFile2 = "";
					if (question.getFileUpload1() != null) {
						pathFile1 = upFileAmazon.UpLoadFile(question.getFileUpload1(), idCateInsert, idQuestion);
						qaListeningDTO.setPathFile1(pathFile1);
					}

					if (question.getFileUpload2() != null) {
						pathFile2 = upFileAmazon.UpLoadFile(question.getFileUpload2(), idCateInsert, idQuestion);
						qaListeningDTO.setPathFile2(pathFile2);
					}
					q_a_ListeningRepository.update(qaListeningDTO.toModel());
				} catch (Exception e) {
					throw new BusinessException("Lỗi upload file câu hỏi có ID : " + idQuestion);
				}
			}
		} else if (obj.getTypeCode() == 3) {
			for (QuestionAnswersDTO question : obj.getListQuestion()) {
				QuestionAnswersTranslationAVDTO q_a_TranAVDTO = new QuestionAnswersTranslationAVDTO();
				q_a_TranAVDTO.setName(question.getName());
				q_a_TranAVDTO.setParentId(idCateInsert);
				q_a_TranAVDTO.setStatus(1L);
				q_a_TranAVDTO.setDescription(question.getDescription());
				q_a_TranAVDTO.setScore(question.getScore());

				if (question.getListCorrectAnswers() != null) {
					String correctAnswer = "";
					for (String answer : question.getListCorrectAnswers()) {
						correctAnswer = answer;
					}
					q_a_TranAVDTO.setAnswer(correctAnswer);
				}
				q_a_TranslationAVRepository.insert(q_a_TranAVDTO.toModel());
			}
		} else {
			for (QuestionAnswersDTO question : obj.getListQuestion()) {
				QuestionAnswersTranslationVADTO q_a_TranVADTO = new QuestionAnswersTranslationVADTO();
				q_a_TranVADTO.setName(question.getName());
				q_a_TranVADTO.setParentId(idCateInsert);
				q_a_TranVADTO.setStatus(1L);
				q_a_TranVADTO.setDescription(question.getDescription());
				q_a_TranVADTO.setScore(question.getScore());

				if (question.getListCorrectAnswers() != null) {
					String correctAnswer = "";
					for (String answer : question.getListCorrectAnswers()) {
						correctAnswer = answer;
					}
					q_a_TranVADTO.setAnswer(correctAnswer);
				}
				q_a_TranslationVARepository.insert(q_a_TranVADTO.toModel());
			}
		}

		return MessageUtils.getMessage("create_category_success");
	}

	public void insertListQuestionReading(CategoryDTO obj, List<QuestionAnswersDTO> lstQuestion) {
		for (QuestionAnswersDTO question : lstQuestion) {
			QuestionAnswersReadingDTO q_a_ReadingDTO = new QuestionAnswersReadingDTO();
			q_a_ReadingDTO.setName(question.getName());
			q_a_ReadingDTO.setParentId(obj.getCategoryId());
			q_a_ReadingDTO.setStatus(1L);
			q_a_ReadingDTO.setDescription(question.getDescription());
			q_a_ReadingDTO.setScore(question.getScore());

			if (question.getListCorrectAnswers().size() > question.getListAnswerToChoose().size()) {
				throw new BusinessException(MessageUtils.getMessage("error_length_listCorrectAnswer"));
			}

			if (question.getListAnswerToChoose() != null) {
				String answersToChoose = "";
				for (QuestionAnswersDTO ques : question.getListAnswerToChoose()) {
					answersToChoose += ques.getAnswer().toUpperCase() + " |";
				}
				q_a_ReadingDTO.setAnswersToChoose(answersToChoose);
				q_a_ReadingDTO.setNumOfAnswer(Long.valueOf(question.getListAnswerToChoose().size()));
			}

			if (question.getListCorrectAnswers() != null) {
				String correctAnswer = "";
				for (String answer : question.getListCorrectAnswers()) {
					correctAnswer += answer.toUpperCase() + " |";
				}
				q_a_ReadingDTO.setAnswer(correctAnswer);
			}
			Long idQuestion = q_a_ReadingRepository.insert(q_a_ReadingDTO.toModel());
		}
	}

	public void updateListQuestionReading(List<QuestionAnswersDTO> lstQuestion) {
		for (QuestionAnswersDTO question : lstQuestion) {
			if (question.getListCorrectAnswers().size() > question.getListAnswerToChoose().size()) {
				throw new BusinessException(MessageUtils.getMessage("error_length_listCorrectAnswer"));
			}
			try {
				QuestionAnswersReadingDTO q_a_ReadingDTO = q_a_ReadingRepository.findByFiled("id", question.getId()).toModel();
				q_a_ReadingDTO.setName(question.getName());
				q_a_ReadingDTO.setParentId(question.getParentId());
				q_a_ReadingDTO.setStatus(question.getStatus());
				q_a_ReadingDTO.setDescription(question.getDescription());
				q_a_ReadingDTO.setScore(question.getScore());

				if (question.getListAnswerToChoose() != null) {
					String answersToChoose = "";
					for (QuestionAnswersDTO ques : question.getListAnswerToChoose()) {
						answersToChoose += ques.getAnswer().toUpperCase() + " |";
					}
					q_a_ReadingDTO.setAnswersToChoose(answersToChoose);
					q_a_ReadingDTO.setNumOfAnswer(Long.valueOf(question.getListAnswerToChoose().size()));
				}

				if (question.getListCorrectAnswers() != null) {
					String correctAnswer = "";
					for (String answer : question.getListCorrectAnswers()) {
						correctAnswer += answer.toUpperCase() + " |";
					}
					q_a_ReadingDTO.setAnswer(correctAnswer);
				}
				q_a_ReadingRepository.update(q_a_ReadingDTO.toModel());
			} catch (Exception e) {
				throw new BusinessException(MessageUtils.getMessage("error_question_not_exist"));
			}
		}
	}

	public void insertListQuestionListening(CategoryDTO obj, List<QuestionAnswersDTO> lstQuestion) {
		FileUtils fileUtils = new FileUtils();

		UpFileAmazon upFileAmazon = new UpFileAmazon();
		for (QuestionAnswersDTO question : lstQuestion) {
//            if(question.getListCorrectAnswers().size() > question.getListAnswerToChoose().size()){
//                throw new BusinessException(MessageUtils.getMessage("error_length_listCorrectAnswer"));
//            }
			QuestionAnswerListeningDTO q_a_ListeningDTO = new QuestionAnswerListeningDTO();
			q_a_ListeningDTO.setName(question.getName());
			q_a_ListeningDTO.setParentId(obj.getCategoryId());
			q_a_ListeningDTO.setStatus(1L);
			q_a_ListeningDTO.setDescription(question.getDescription());
			q_a_ListeningDTO.setScore(question.getScore());
			q_a_ListeningDTO.setTypeFile1(question.getTypeFile1());
			q_a_ListeningDTO.setTypeFile2(question.getTypeFile2());

			if (question.getPathFile1() != null && question.getPathFile1() != "") {
				q_a_ListeningDTO.setPathFile1(question.getPathFile1());
			}
			if (question.getPathFile2() != null && question.getPathFile2() != "") {
				q_a_ListeningDTO.setPathFile2(question.getPathFile2());
			}

			if (question.getListAnswerToChoose() != null) {
				String answersToChoose = "";
				String startTime = "";
				for (QuestionAnswersDTO ques : question.getListAnswerToChoose()) {
					answersToChoose += ques.getAnswer().toUpperCase() + " |";
					startTime += ques.getStartTime() + " |";
				}
				q_a_ListeningDTO.setAnswersToChoose(answersToChoose);
				q_a_ListeningDTO.setNumOfAnswer(Long.valueOf(question.getListAnswerToChoose().size()));
				q_a_ListeningDTO.setStartTime(startTime);
			}

			if (question.getListCorrectAnswers() != null) {
				String correctAnswer = "";
				for (String answer : question.getListCorrectAnswers()) {
					correctAnswer = answer.toUpperCase() + " |";
				}
				q_a_ListeningDTO.setAnswer(correctAnswer);
			}
			Long idQuestion = q_a_ListeningRepository.insert(q_a_ListeningDTO.toModel());

			// -------------------------------------------Upload file câu hỏi-------------------------------------------
			try {
				QuestionAnswerListeningDTO qaListeningDTO = q_a_ListeningRepository.findByFiled("id", idQuestion).toModel();
				String pathFile1 = "";
				String pathFile2 = "";
				if (question.getFileUpload1() != null) {
					pathFile1 = upFileAmazon.UpLoadFile(question.getFileUpload1(), obj.getCategoryId(), idQuestion);
					qaListeningDTO.setPathFile1(pathFile1);
				}

				if (question.getFileUpload2() != null) {
					pathFile2 = upFileAmazon.UpLoadFile(question.getFileUpload2(), obj.getCategoryId(), idQuestion);
					qaListeningDTO.setPathFile2(pathFile2);
				}
				q_a_ListeningRepository.update(qaListeningDTO.toModel());
			} catch (Exception e) {
				throw new BusinessException("Lỗi upload file câu hỏi có ID : " + idQuestion);
			}
		}
	}

	public void updateListQuestionListening(List<QuestionAnswersDTO> lstQuestion) {
		FileUtils fileUtils = new FileUtils();
		UpFileAmazon upFileAmazon = new UpFileAmazon();
		for (QuestionAnswersDTO question : lstQuestion) {
//            if (question.getListCorrectAnswers().size() > question.getListAnswerToChoose().size()) {
//                throw new BusinessException(MessageUtils.getMessage("error_length_listCorrectAnswer"));
//            }
			try {
				QuestionAnswerListeningDTO q_a_ListeningDTO = q_a_ListeningRepository.findByFiled("id", question.getId()).toModel();
				q_a_ListeningDTO.setName(question.getName());
				q_a_ListeningDTO.setParentId(question.getParentId());
				q_a_ListeningDTO.setStatus(question.getStatus());
				q_a_ListeningDTO.setDescription(question.getDescription());
				q_a_ListeningDTO.setScore(question.getScore());
				q_a_ListeningDTO.setTypeFile1(question.getTypeFile1());
				q_a_ListeningDTO.setTypeFile2(question.getTypeFile2());
				q_a_ListeningDTO.setTranscript(question.getTranscript());

				if (question.getPathFile1() != null && question.getPathFile1() != "") {
					q_a_ListeningDTO.setPathFile1(question.getPathFile1());
				}
				if (question.getPathFile2() != null && question.getPathFile2() != "") {
					q_a_ListeningDTO.setPathFile2(question.getPathFile2());
				}

				// -------------------------------------------Upload file câu hỏi-------------------------------------------
				String pathFile1 = "";
				String pathFile2 = "";
				if (question.getFileUpload1() != null) {
					pathFile1 = upFileAmazon.UpLoadFile(question.getFileUpload1(), question.getParentId(), question.getId());
					q_a_ListeningDTO.setPathFile1(pathFile1);
				}

				if (question.getFileUpload2() != null) {
					pathFile2 = upFileAmazon.UpLoadFile(question.getFileUpload2(), question.getParentId(), question.getId());
					q_a_ListeningDTO.setPathFile2(pathFile2);
				}

                if (question.getListAnswerToChoose() != null) {
                    String answersToChoose = "";
                    String startTime = "";
                    for (QuestionAnswersDTO ques : question.getListAnswerToChoose()) {
                        answersToChoose += ques.getAnswer().toUpperCase() + " |";
                        startTime += ques.getStartTime() + " |";
                    }
                    q_a_ListeningDTO.setAnswersToChoose(answersToChoose);
                    q_a_ListeningDTO.setNumOfAnswer(Long.valueOf(question.getListAnswerToChoose().size()));
                    q_a_ListeningDTO.setStartTime(startTime);
                }

                if (question.getListCorrectAnswers() != null) {
                    String correctAnswer = "";
                    for (String answer : question.getListCorrectAnswers()) {
                        correctAnswer += answer.toUpperCase() + " |";
                    }
                    q_a_ListeningDTO.setAnswer(correctAnswer);
                }
                q_a_ListeningRepository.update(q_a_ListeningDTO.toModel());
            }catch (Exception e) {
                throw new BusinessException(MessageUtils.getMessage("error_question_not_exist"));
            }

        }
    }

    public void insertListQuestionTranAV(CategoryDTO obj , List<QuestionAnswersDTO> lstQuestion){
        for (QuestionAnswersDTO question : lstQuestion) {
            QuestionAnswersTranslationAVDTO q_a_TranAVDTO = new QuestionAnswersTranslationAVDTO();
            q_a_TranAVDTO.setName(question.getName());
            q_a_TranAVDTO.setParentId(obj.getCategoryId());
            q_a_TranAVDTO.setStatus(1L);
            q_a_TranAVDTO.setDescription(question.getDescription());
            q_a_TranAVDTO.setScore(question.getScore());

            if(question.getListCorrectAnswers() != null){
                String correctAnswer = "";
                for (String answer : question.getListCorrectAnswers()) {
                    correctAnswer = answer;
                }
                q_a_TranAVDTO.setAnswer(correctAnswer);
            }
            q_a_TranslationAVRepository.insert(q_a_TranAVDTO.toModel());
        }
    }

    public void updateListQuestionTranAV(List<QuestionAnswersDTO> lstQuestion){
        for (QuestionAnswersDTO question : lstQuestion) {
            try{
                QuestionAnswersTranslationAVDTO q_a_TranAVDTO = q_a_TranslationAVRepository.findByFiled("id" , question.getId()).toModel();
                q_a_TranAVDTO.setName(question.getName());
                q_a_TranAVDTO.setParentId(question.getParentId());
                q_a_TranAVDTO.setStatus(question.getStatus());
                q_a_TranAVDTO.setDescription(question.getDescription());
                q_a_TranAVDTO.setScore(question.getScore());

                if(question.getListCorrectAnswers() != null){
                    String correctAnswer = "";
                    for (String answer : question.getListCorrectAnswers()) {
                        correctAnswer = answer;
                    }
                    q_a_TranAVDTO.setAnswer(correctAnswer);
                }
                q_a_TranslationAVRepository.update(q_a_TranAVDTO.toModel());
            }catch (Exception e) {
                throw new BusinessException(MessageUtils.getMessage("error_question_not_exist"));
            }
        }
    }

    public void insertListQuestionTranVA(CategoryDTO obj , List<QuestionAnswersDTO> lstQuestion){
        for (QuestionAnswersDTO question : lstQuestion) {
            QuestionAnswersTranslationVADTO q_a_TranVADTO = new QuestionAnswersTranslationVADTO();
            q_a_TranVADTO.setName(question.getName());
            q_a_TranVADTO.setParentId(obj.getCategoryId());
            q_a_TranVADTO.setStatus(1L);
            q_a_TranVADTO.setDescription(question.getDescription());
            q_a_TranVADTO.setScore(question.getScore());

            if(question.getListCorrectAnswers() != null){
                String correctAnswer = "";
                for (String answer : question.getListCorrectAnswers()) {
                    correctAnswer = answer;
                }
                q_a_TranVADTO.setAnswer(correctAnswer);
            }
            q_a_TranslationVARepository.insert(q_a_TranVADTO.toModel());
        }
    }

    public void updateListQuestionTranVA(List<QuestionAnswersDTO> lstQuestion){
        for (QuestionAnswersDTO question : lstQuestion) {
            try{
                QuestionAnswersTranslationVADTO q_a_TranVADTO = q_a_TranslationVARepository.findByFiled("id" , question.getId()).toModel();
                q_a_TranVADTO.setName(question.getName());
                q_a_TranVADTO.setParentId(question.getParentId());
                q_a_TranVADTO.setStatus(question.getStatus());
                q_a_TranVADTO.setDescription(question.getDescription());
                q_a_TranVADTO.setScore(question.getScore());

                if(question.getListCorrectAnswers() != null){
                    String correctAnswer = "";
                    for (String answer : question.getListCorrectAnswers()) {
                        correctAnswer = answer;
                    }
                    q_a_TranVADTO.setAnswer(correctAnswer);
                }
                q_a_TranslationVARepository.update(q_a_TranVADTO.toModel());
            }catch (Exception e) {
                throw new BusinessException(MessageUtils.getMessage("error_question_not_exist"));
            }
        }
    }

    @Override
    public String updateCategory(CategoryDTO obj) {
        UpFileAmazon upFileAmazon = new UpFileAmazon();
        FileUtils fileUtils = new FileUtils();
        LocalDateTime localDateTime = LocalDateTime.now();
        Date updateDate = Date.from(localDateTime.toInstant(ZoneOffset.UTC));

        try {
            CategoryDTO categoryDTO = categoryRepository.findByFiled("categoryId" , obj.getCategoryId()).toModel();
            categoryDTO.setNameCategory(obj.getNameCategory());
            categoryDTO.setStatus(obj.getStatus());
            categoryDTO.setParentId(obj.getParentId());
            categoryDTO.setTypeCode(obj.getTypeCode());
            categoryDTO.setLevelCode(obj.getLevelCode());
            categoryDTO.setTypeFile1(obj.getTypeFile1());
            categoryDTO.setTypeFile2(obj.getTypeFile2());
            categoryDTO.setTranscript(obj.getTranscript());
            categoryDTO.setUpdatedDate(updateDate);

            if(obj.getPathFile1() != null && obj.getPathFile1() != ""){
                categoryDTO.setPathFile1(obj.getPathFile1());
            }
            if(obj.getPathFile2() != null && obj.getPathFile2() != ""){
                categoryDTO.setPathFile2(obj.getPathFile2());
            }
            // -------------------------------------------Upload file đề bài-------------------------------------------
            String pathFile1 = "";
            String pathFile2 = "";
            if (obj.getFileUpload1() != null) {
                pathFile1 = upFileAmazon.UpLoadFile(obj.getFileUpload1() , obj.getCategoryId() , null);
                categoryDTO.setPathFile1(pathFile1);
            }

            if (obj.getFileUpload2() != null) {
                pathFile2 = upFileAmazon.UpLoadFile(obj.getFileUpload2() , obj.getCategoryId() , null);
                categoryDTO.setPathFile2(pathFile2);
            }
            categoryRepository.update(categoryDTO.toModel());
        }catch (Exception e) {
            throw new BusinessException(MessageUtils.getMessage("error_category_not_exist"));
        }

        // ----------------------------------------------------------------------------------------------------------------------

        // Thêm sửa xóa câu hỏi
        List<QuestionAnswersDTO> lstQuestionCreate = new ArrayList<QuestionAnswersDTO>();
        List<QuestionAnswersDTO> lstQuestionEdit = new ArrayList<QuestionAnswersDTO>();
        List<QuestionAnswersDTO> lstQuestionDelete = new ArrayList<QuestionAnswersDTO>();

        Map<Long, QuestionAnswersDTO> mapFE = new HashMap<>();
        for (QuestionAnswersDTO question : obj.getListQuestion()) {
            if (question.getId() == null) {
                lstQuestionCreate.add(question);
            } else {
                mapFE.put(question.getId(), question);
            }
        }

        Map<Long, QuestionAnswersDTO> mapDB = new HashMap<>();
        for (QuestionAnswersDTO question : categoryRepository.getListQuestionByCategory(obj.getCategoryId())) {
            mapDB.put(question.getId(), question);
        }

        // -------------check edit , delete--------------
        for (Map.Entry<Long, QuestionAnswersDTO> question : mapFE.entrySet()) {
            if (mapDB.containsKey(question.getKey())) {
                if (mapDB.get(question.getKey()).equals(mapFE.get(question.getKey())) == false) {
                    lstQuestionEdit.add(question.getValue());
                }
            }
        }
        for (Map.Entry<Long, QuestionAnswersDTO> question : mapDB.entrySet()) {
            if (mapFE.containsKey(question.getKey()) == false) {
                lstQuestionDelete.add(question.getValue());
            }
        }


        if (obj.getTypeCode() == 1) {
            insertListQuestionReading(obj , lstQuestionCreate);
            for (QuestionAnswersDTO question : lstQuestionDelete) {
                q_a_ReadingRepository.delete(question.toModelReading());
            }
            updateListQuestionReading(lstQuestionEdit);

        } else if (obj.getTypeCode() == 2) {
            insertListQuestionListening(obj, lstQuestionCreate);
            for (QuestionAnswersDTO question : lstQuestionDelete) {
                q_a_ListeningRepository.delete(question.toModelListening());
            }
            updateListQuestionListening(lstQuestionEdit);
        } else if(obj.getTypeCode() == 5){
            insertListQuestionListening(obj, lstQuestionCreate);
            for (QuestionAnswersDTO question : lstQuestionDelete) {
                q_a_ListeningRepository.delete(question.toModelListening());
            }
            updateListQuestionListening(lstQuestionEdit);
        } else if (obj.getTypeCode() == 3) {
            insertListQuestionTranAV(obj , lstQuestionCreate);
            for (QuestionAnswersDTO question : lstQuestionDelete) {
                q_a_TranslationAVRepository.delete(question.toModelAV());
            }
            updateListQuestionTranAV(lstQuestionEdit);
        } else {
            insertListQuestionTranVA(obj , lstQuestionCreate);
            for (QuestionAnswersDTO question : lstQuestionDelete) {
                q_a_TranslationVARepository.delete(question.toModelVA());
            }
            updateListQuestionTranVA(lstQuestionEdit);
        }
        return MessageUtils.getMessage("update_category_success");
    }

    @Override
    public String deleteCategory(CategoryDTO obj) throws BusinessException {

		//check category nằm trong bảng detail_history_lis_listening
		List<DetailHistoryLisSingleDTO> listDetailHistoryLisSingleDTOS =
				detailHistoryLisSingleRepository.checkForDeletingCategory(obj.getCategoryId());
        if(listDetailHistoryLisSingleDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category_for_test"));
		}

		//check category nằm trong bảng detail_history_listen_fill
		List<DetailHistoryListenFillDTO> listDetailHistoryListenFillDTOS =
				detailHistoryListenFillRepository.checkForDeletingCategory(obj.getCategoryId());
		if(listDetailHistoryListenFillDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category_for_test"));
		}

		//check category nằm trong bảng detail_history_listening
		List<DetailHistoryListeningDTO> listDetailHistoryListeningDTOS =
				detailHistoryListeningRepository.checkForDeletingCategory(obj.getCategoryId());
		if(listDetailHistoryListeningDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category_for_test"));
		}

		//check category nằm trong bảng detail_history_read_single
		List<DetailHistoryReadingSingleDTO> listDetailHistoryReadingSingleDTOS =
				detailHistoryReadingSingleRepository.checkForDeletingCategory(obj.getCategoryId());
		if(listDetailHistoryReadingSingleDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category_for_test"));
		}

		//check category nằm trong bảng detail_history_reading
		List<DetailHistoryReadingDTO> listDetailHistoryReadingDTOS =
				detailHistoryReadingRepository.checkForDeletingCategory(obj.getCategoryId());
		if(listDetailHistoryReadingDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category_for_test"));
		}

		//check category nằm trong bảng detail_history_minitest
		List<DetailHistoryMinitestDTO> listDetailHistoryMinitestDTOS =
				detailHistoryMinitestRepository.checkForDeletingCategory(obj.getCategoryId());
		if(listDetailHistoryMinitestDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category_for_test"));
		}

		//check category nằm trong bảng detail_history_minitest
		List<DetailHistoryDTO> listDetailHistoryDTOS =
				detailHistoryRepository.checkForDeletingCategory(obj.getCategoryId());
		if(listDetailHistoryDTOS.size() > 0){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category"));
		}

		try{
			q_a_ListeningRepository.deleteQuestionByCategory(obj.getCategoryId());
		}
		catch (Exception e){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category"));
		}

		try{
			q_a_ReadingRepository.deleteQuestionByCategory(obj.getCategoryId());
		}
		catch (Exception e){
			throw new BusinessException(MessageUtils.getMessage("error_delete_category"));
		}

		categoryRepository.deleteCategoryById(obj.getCategoryId());


//		//Xóa đề bài và câu hỏi sau khi đã check
//		List<QuestionAnswerListeningDTO> listQuestionAnswerListeningDTOS =
//				q_a_ListeningRepository.getListQuetionByCategoryId(obj.getCategoryId());
//		for (QuestionAnswerListeningDTO questionAnswerListeningDTO: listQuestionAnswerListeningDTOS) {
//			q_a_ListeningRepository.delete(questionAnswerListeningDTO.toModel());
//		}
//
//		List<QuestionAnswersReadingDTO> listQuestionAnswerReadingDTOS =
//				q_a_ReadingRepository.getListQuetionByCategoryId(obj.getCategoryId());
//		for (QuestionAnswersReadingDTO questionAnswersReadingDTO: listQuestionAnswerReadingDTOS) {
//			q_a_ReadingRepository.delete(questionAnswersReadingDTO.toModel());
//		}
//
//		categoryRepository.delete(obj.toModel());
//
        return MessageUtils.getMessage("delete_category_success");
    }

    public List<CategoryDTO> getLevelCode() {
        return categoryRepository.getLevelCode();
    }

    public DataDTO getData(DataInputDTO dataInputDTO) {
        DataDTO dataDTORes = new DataDTO();
        dataDTORes.setGetLevelCode(apParamRepository.getLevelCode());
        dataDTORes.setLstNameTopic(topicRepository.getTopicName(dataInputDTO.getPart_topic_code(), dataInputDTO.getType_topic_code()));
        dataDTORes.setLstPartTopic(apParamRepository.getPartByParentCode(dataInputDTO.getParentCode()));
        dataDTORes.setLstTypeTopic(apParamRepository.getType());
        return dataDTORes;
    }

    @Override
    public List<CategoryDTO> getListTypeDataInput() {
        List<CategoryDTO> listTypeDataInput = categoryRepository.getListTypeDataInput();
        return listTypeDataInput;
    }

    @Override
    public List<CategoryDTO> getListTypeLevel() {
        List<CategoryDTO> listTypeLevel = categoryRepository.getListTypeLevel();
        return listTypeLevel;
    }

    //Chuyển các giá trị loại câu hỏi sang đúng định dạng trong db
    public Long typeCodeCsvToModel(String typeCode, List<ApParamDTO> lstType) {
        for (ApParamDTO item : lstType) {
            if (typeCode.equals(item.getName())) {
                return Long.valueOf(item.getValue());
            }
        }
        return null;
    }

    //chuyển các giá trị của phần theo đúng trong db
    public String partCsvToModel(String part, List<ApParamDTO> lstPart) {
        for (ApParamDTO item : lstPart) {
            if (part.equals(item.getName())) {
                return item.getValue();
            }
        }
        return null;
    }

    //chuyển các phương án theo đúng định dạng trong db
    public String answerToChooseToModel(RowCategoryFromCsvDTO row) {
        StringBuilder answersToChoose = new StringBuilder("");
        if (row.getAnswerToChoose1() != null && !row.getAnswerToChoose1().trim().equals("")) {
            answersToChoose.append(row.getAnswerToChoose1());
        }
        if (row.getAnswerToChoose2() != null && !row.getAnswerToChoose2().trim().equals("")) {
            answersToChoose.append("|" + row.getAnswerToChoose2());
        }
        if (row.getAnswerToChoose3() != null && !row.getAnswerToChoose3().trim().equals("")) {
            answersToChoose.append("|" + row.getAnswerToChoose3());
        }
        if (row.getAnswerToChoose4() != null && !row.getAnswerToChoose4().trim().equals("")) {
            answersToChoose.append("|" + row.getAnswerToChoose4());
        }
        if (answersToChoose.toString().startsWith("|")) {
            answersToChoose.deleteCharAt(0);
        }
        if (answersToChoose.length() > 0) {
            return answersToChoose.toString();
        }
        return null;
    }

    //chuyển các thời gian của phương án theo đúng định dạng trong db
    public String startTimeToModel(RowCategoryFromCsvDTO row) {
        StringBuilder startTime = new StringBuilder("");
        //nếu có phương án ở vị trí tương ứng thì nối giá trị của ô thời gian phương án
        if (row.getAnswerToChoose1() != null && !row.getAnswerToChoose1().trim().equals("")) {
            startTime.append(row.getStartTime1());
        }
        if (row.getAnswerToChoose2() != null && !row.getAnswerToChoose2().trim().equals("")) {
            startTime.append("|" + row.getStartTime2());
        }
        if (row.getAnswerToChoose3() != null && !row.getAnswerToChoose3().trim().equals("")) {
            startTime.append("|" + row.getStartTime3());
        }
        if (row.getAnswerToChoose4() != null && !row.getAnswerToChoose4().trim().equals("")) {
            startTime.append("|" + row.getStartTime4());
        }
        if (startTime.toString().startsWith("|")) {
            startTime.deleteCharAt(0);
        }
        if (startTime.length() > 0) {
            return startTime.toString();
        }
        return null;
    }

    public String correctAnswerToModel(RowCategoryFromCsvDTO row) {
        StringBuilder correctAnswers = new StringBuilder("");
        if (row.getAnswer1() != null && !row.getAnswer1().trim().equals("")) {
            correctAnswers.append(row.getAnswer1());
        }
        if (row.getAnswer2() != null && !row.getAnswer2().trim().equals("")) {
            correctAnswers.append("|" + row.getAnswer2());
        }
        if (row.getAnswer3() != null && !row.getAnswer3().trim().equals("")) {
            correctAnswers.append("|" + row.getAnswer3());
        }
        if (correctAnswers.toString().startsWith("|")) {
            correctAnswers.deleteCharAt(0);
        }
        if (correctAnswers.length() > 0) {
            return correctAnswers.toString();
        }
        return null;
    }

    //xử lí các trường thuộc câu hỏi trong rawData
    public QuestionAnswersDTO readQuestionFromRowCsv(RowCategoryFromCsvDTO row, CategoryDTO cate) {
        QuestionAnswersDTO question = new QuestionAnswersDTO();
        question.setTypeQuestion(row.getTypeQuestion().equals("Có") ? 1l : 0l);
        question.setScore(row.getScore());
        question.setName(row.getName());
        question.setTypeFile1(row.getTypeFile1());
        question.setTypeFile2(row.getTypeFile2());

        /* Nội dung dữ liệu câu hỏi 1 và 2 cần confirm lại với BA*/

        question.setTranscript(row.getTranscriptQuestion());
        //nếu là loại câu hỏi trắc nghiệm của loại bài có phương án
        if (cate.getTypeCode() != 3l
                && cate.getTypeCode() != 4l
                && row.getTypeQuestion().equals("Có")) {
            question.setAnswersToChoose(answerToChooseToModel(row));
            question.setStartTime(startTimeToModel(row));
        }
        question.setAnswer(correctAnswerToModel(row));
        question.setDescription(row.getDescription());
        return question;
    }

    @Override
    public List<CategoryDTO> readFileCsv(MultipartFile file) {
        FileUtils fu = new FileUtils();
        List<CategoryDTO> lstCategories = new ArrayList<>();
        List<ApParamDTO> lstTypeCode = apParamRepository.getType();
        List<ApParamDTO> lstPart = apParamRepository.getPartByParentCode(null);
        try {
            InputStream fStream = file.getInputStream();
            XSSFWorkbook wb = new XSSFWorkbook(fStream);
            for(PackagePart fileInCell : wb.getAllEmbedds()){
                fu.storeFile2(9999l,9999l,fileInCell.getContentType(),fileInCell.getPartName().getName(),fileInCell.getSize(),fileInCell.getInputStream());
            }
//            XSSFSheet sheet = wb.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//            for (int rowNum = 0 ;rows.hasNext();rowNum++){
//                Row row = rows.next();
//                Iterator<Cell> cells = row.cellIterator();
//                for (int colNum = 0;cells.hasNext();colNum++){
//                    Cell cell = cells.next();
//                }
//            }


//            XlsxReader reader = new XlsxReader();
//
//            //đọc file excel thành 1 list các đối tượng RowCategoryFromCsvDTO
//            List<RowCategoryFromCsvDTO> lstRowCategory = reader.read(RowCategoryFromCsvDTO.class, fStream);
//
//            //Sắp xếp các phần tử trong list raw theo tên đề bài(1) và câu hỏi(2)
//            Collections.sort(lstRowCategory);
//
//            /*Phân tích từng đối tượng RowCategoryFromCsvDTO sang thành CategoryDTO
//            Duyệt từng phần tử trong RowCategoryFromCsvDTO*/
//            for (int i = 0; i < lstRowCategory.size(); i++) {
//                if (
//                    /*nếu list kết quả chưa có gì*/
//                        lstCategories.size() <= 0 ||
//                                /* hoặc nếu tên đề bài của đối tượng hiện tại khác của đối tượng trước đó*/
//                                !lstRowCategory.get(i).compareTwoRow(lstRowCategory.get(i - 1))
//                ) {
//                    //tạo ra 1 đề bài mới và thêm các thuộc tính cho đề bài mới
//                    CategoryDTO cate = new CategoryDTO();
//                    cate.setNameCategory(lstRowCategory.get(i).getNameCategory());
//                    cate.setLevelCode(lstRowCategory.get(i).getLevelCode());
//                    cate.setStatus(lstRowCategory.get(i).getStatus().equals("Có") ? 1l : 0l);
//                    cate.setTypeCode(typeCodeCsvToModel(lstRowCategory.get(i).getTypeCode(), lstTypeCode));
//                    cate.setPart(partCsvToModel(lstRowCategory.get(i).getPart(), lstPart));
//                    cate.setNameTopic(lstRowCategory.get(i).getNameTopic());
//                    cate.setTypeFile1(lstRowCategory.get(i).getTypeFile1());
//                    cate.setTypeFile2(lstRowCategory.get(i).getTypeFile2());
//
//                    /*Nội dung dữ liệu 1 và Nội dung dữ liệu 2 chưa xác định
//                    sẽ lấy như nào nên để sau*/
//
//                    cate.setTranscript(lstRowCategory.get(i).getTranscript());
//
//                    //Trường điểm mục tiêu hiện chưa có trong bảng category nên đợi confirm lại với team
//
//
//                    List<QuestionAnswersDTO> lstQuestion = new ArrayList<>();
//                    cate.setListQuestion(lstQuestion);
//                    lstCategories.add(cate);
//                }
//                //thêm câu hỏi vào đề bài cuối cùng trong list đề bài
//                lstCategories.get(lstCategories.size() - 1).getListQuestion()
//                        .add(readQuestionFromRowCsv(lstRowCategory.get(i), lstCategories.get(lstCategories.size() - 1)));
//            }
        } catch (IOException | OpenXML4JException e) {
            e.printStackTrace();
        }
        return lstCategories;
    }

    @Autowired
    QuestionAnswerReadingRepository questionAnswerReadingRepository;
    public List<QuestionOfReadingAndComplitingDTO> getListQuestionOfReadingAndCompliting(RequestPractice requestPractice) {
        if (requestPractice.getLevelCode().equals("EASY")) {
            List<QuestionOfReadingAndComplitingDTO> lstEasy = categoryRepository.getListQuestionOfReadingAndCompliting(requestPractice.getTopicName(), "EASY", 3);
            for (int i = 0; i < lstEasy.size(); i++) {
                ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(lstEasy.get(i).getIdCategory());
                lstEasy.get(i).setListAnswerToChoose(data.getData());
            }
            return lstEasy;
        }
        if (requestPractice.getLevelCode().equals("MEDIUM")) {
            List<QuestionOfReadingAndComplitingDTO> lstEasy =
                    categoryRepository.getListQuestionOfReadingAndCompliting(requestPractice.getTopicName(), "EASY", 2);
            for (int i = 0; i < lstEasy.size(); i++) {
                ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(lstEasy.get(i).getIdCategory());
                lstEasy.get(i).setListAnswerToChoose(data.getData());
            }
            List<QuestionOfReadingAndComplitingDTO> lstMedium =
                    categoryRepository.getListQuestionOfReadingAndCompliting(requestPractice.getTopicName(), "MEDIUM", 1);
            for (int i = 0; i < lstMedium.size(); i++) {
                ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(lstMedium.get(i).getIdCategory());
                lstMedium.get(i).setListAnswerToChoose(data.getData());
            }
            lstEasy.addAll(lstMedium);
            return lstEasy;
        }
        if(requestPractice.getLevelCode().equals("DIFFICULT")){
            List<QuestionOfReadingAndComplitingDTO> lstEasy =
                    categoryRepository.getListQuestionOfReadingAndCompliting(requestPractice.getTopicName(),"EASY",1);
            for (int i = 0; i < lstEasy.size(); i++) {
                ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(lstEasy.get(i).getIdCategory());
                lstEasy.get(i).setListAnswerToChoose(data.getData());
            }
            List<QuestionOfReadingAndComplitingDTO> lstMedium =
                    categoryRepository.getListQuestionOfReadingAndCompliting(requestPractice.getTopicName(),"MEDIUM",1);
            for (int i = 0; i < lstMedium.size(); i++) {
                ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(lstMedium.get(i).getIdCategory());
                lstMedium.get(i).setListAnswerToChoose(data.getData());
            }
            List<QuestionOfReadingAndComplitingDTO> lstDifficult =
                    categoryRepository.getListQuestionOfReadingAndCompliting(requestPractice.getTopicName(),"DIFFICULT",1);
            for (int i = 0; i < lstDifficult.size(); i++) {
                ResultDataDTO data = questionAnswerReadingRepository.getListQuestionsOfRead(lstDifficult.get(i).getIdCategory());
                lstDifficult.get(i).setListAnswerToChoose(data.getData());
            }
            lstEasy.addAll(lstMedium);
            lstEasy.addAll(lstDifficult);
            return lstEasy;
        }
        return null;
    }

    public List<QuestionAnswersDTO> getResultQuestionOfReadingAndCompliting(CategoryReadingChoosenDTO obj){
        List<QuestionAnswersDTO> lst = new ArrayList<>();
        int numberCorrect = 0;
        for(QuestionAnswersDTO questionAnswersDTO : obj.getListQuestionAnswer()){
            QuestionAnswersDTO result = questionAnswerReadingRepository.getDetailById(questionAnswersDTO.getId());
            if(result != null){
                result.setUserChoose(questionAnswersDTO.getUserChoose());
                String[] a = result.getAnswersToChoose().toLowerCase().split("[\\|]");
				int indexChoose;
				if(questionAnswersDTO.getUserChoose().equalsIgnoreCase("A")){
					indexChoose = 0;
				}else if(questionAnswersDTO.getUserChoose().equalsIgnoreCase("B")){
					indexChoose = 1;
				} else if(questionAnswersDTO.getUserChoose().equalsIgnoreCase("C")){
					indexChoose = 2;
				} else {
					indexChoose = 3;
				}
				int indexAnswer;
				if(result.getAnswer().equalsIgnoreCase("A")){
					indexAnswer = 0;
				}else if(result.getAnswer().equalsIgnoreCase("B")){
					indexAnswer = 1;
				} else if(result.getAnswer().equalsIgnoreCase("C")){
					indexAnswer = 2;
				} else {
					indexAnswer = 3;
				}
                for(int i=0; i< a.length; i++){
                    if(i == indexChoose  &&  indexChoose == indexAnswer ){
                        result.setIndexCorrect(Long.valueOf(i));
                    }else if(i == indexChoose && indexChoose != indexAnswer ){
                        result.setIndexInCorrect(Long.valueOf(i));
                    }else if(i == indexAnswer){
                        result.setIndexCorrect(Long.valueOf(i));
                    }
                }
                if(result.getIndexCorrect() != null && result.getIndexInCorrect() == null){
                    numberCorrect++;
                }
                result.setNumberCorrect(Long.valueOf(numberCorrect));
            }
            lst.add(result);
        }
        return lst;
    }

	@Transactional
	public String createHistoryReadingAndCompliting(HistoryPracticesDTO historyPracticesDTO) {
		try {
//			LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//			Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
//            HistoryPracticesDTO obj = new HistoryPracticesDTO();
			historyPracticesDTO.setCreateDate(new Date());
//            obj.setTopicId(historyPracticesDTO.getTopicId());
//            obj.setTypeCode(historyPracticesDTO.getTypeCode());
//            obj.setPart(historyPracticesDTO.getPart());
//            obj.setLevelCode(historyPracticesDTO.getLevelCode());
//            obj.setNumberCorrect(historyPracticesDTO.getNumberCorrect());
//            obj.setUserId(historyPracticesDTO.getUserId());
			Long idHistory = historyPracticesRepository.insert(historyPracticesDTO.toModel());
			try {
				createDetailHistoryReading(historyPracticesDTO.getLstCategoryReadingChoose(), idHistory);
			} catch (Exception e) {
				throw new BusinessException("create_history_reading_completed_passage_fail");
			}
			return MessageUtils.getMessage("create_history_reading_completed_passage_success");
		} catch (Exception e) {
			throw new BusinessException("create_history_reading_completed_passage_fail");
		}
	}

	public void createDetailHistoryReading(List<CategoryReadingChoosenDTO> lstCategoryReadingChoose, Long idHistory) {
		for (CategoryReadingChoosenDTO categoryReadingChoosenDTO : lstCategoryReadingChoose) {
			for (QuestionAnswersDTO questionAnswersDTO : categoryReadingChoosenDTO.getListQuestionAnswer()) {
				DetailHistoryReadingDTO detailHistoryReadingDTO = new DetailHistoryReadingDTO();
				detailHistoryReadingDTO.setParentId(idHistory);
				detailHistoryReadingDTO.setQuestionId(questionAnswersDTO.getId());
				detailHistoryReadingDTO.setCategoryId(questionAnswersDTO.getParentId());
				detailHistoryReadingDTO.setUserChoose(questionAnswersDTO.getUserChoose());
				if (questionAnswersDTO.getIndexInCorrect() != null) {
					detailHistoryReadingDTO.setIndexInCorrect(questionAnswersDTO.getIndexInCorrect().toString());
				} else {
					detailHistoryReadingDTO.setIndexInCorrect("");
				}
				if (questionAnswersDTO.getIndexCorrect() != null) {
					detailHistoryReadingDTO.setIndexCorrect(questionAnswersDTO.getIndexCorrect().toString());
				} else {
					detailHistoryReadingDTO.setIndexCorrect("");
				}
				Long idDetail = detailHistoryReadingRepository.insert(detailHistoryReadingDTO.toModel());
			}
		}
	}

	@Override
	public CategoryDTO getDetailByName(Long id) {
		Category category = categoryRepository.findByFiled("categoryId", id);
		return null != category ? category.toModel() : null;
	}

	@Override
	public List<CategoryDTO> getListCategoryByName(CategoryDTO categoryDTO) {
		List<CategoryDTO> lst = categoryRepository.getCategoryByNameAndLevel(categoryDTO);
		return lst;
	}

	@Override
	@Transactional
	public Long insertData(CategoryDTO obj) {
		return categoryRepository.insert(obj.toModel());
	}
    public String createHistoryReadingSingle(HistoryPracticesDTO historyPracticesDTO){
        try{
//            LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//            Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
            HistoryPracticesDTO obj = new HistoryPracticesDTO();
            obj.setCreateDate(new Date());
            obj.setTopicId(historyPracticesDTO.getTopicId());
            obj.setTypeCode(historyPracticesDTO.getTypeCode());
            obj.setPart(historyPracticesDTO.getPart());
            obj.setLevelCode(historyPracticesDTO.getLevelCode());
            obj.setNumberCorrect(historyPracticesDTO.getNumberCorrect());
            obj.setUserId(historyPracticesDTO.getUserId());
            Long idHistory = historyPracticesRepository.insert(obj.toModel());
            try{
                createDetailHistoryReadingSingle(historyPracticesDTO.getLstCategoryReadingChoose(), idHistory);
            } catch (Exception e){
				if(historyPracticesDTO.getPart().equalsIgnoreCase("part7"))
                	throw new BusinessException("create_history_reading_single_fail");
				throw new BusinessException("create_history_reading_dual_fail");
            }
			if(historyPracticesDTO.getPart().equalsIgnoreCase("part7"))
				return MessageUtils.getMessage("create_history_reading_single_success");
			return MessageUtils.getMessage("create_history_reading_dual_success");
        } catch (Exception e){
				throw new BusinessException("error_save_result");
        }
    }

    public void createDetailHistoryReadingSingle(List<CategoryReadingChoosenDTO> lstCategoryReadingChoose, Long idHistory){
        for(CategoryReadingChoosenDTO categoryReadingChoosenDTO : lstCategoryReadingChoose){
            for(CategoryDTO questionAnswersDTO : categoryReadingChoosenDTO.getListQuestionAnswerCategory()){
                DetailHistoryReadingSingleDTO detailHistoryReadingDTO = new DetailHistoryReadingSingleDTO();
                detailHistoryReadingDTO.setParentId(idHistory);
                detailHistoryReadingDTO.setQuestionId(questionAnswersDTO.getId());
                detailHistoryReadingDTO.setCategoryId(questionAnswersDTO.getCategoryId());
                detailHistoryReadingDTO.setUserChoose(questionAnswersDTO.getUserChoose());
                if(questionAnswersDTO.getIndexInCorrect() != null){
                    detailHistoryReadingDTO.setIndexInCorrect(questionAnswersDTO.getIndexInCorrect().toString());
                } else {
                    detailHistoryReadingDTO.setIndexInCorrect("");
                }
                if(questionAnswersDTO.getIndexCorrect() != null){
                    detailHistoryReadingDTO.setIndexCorrect(questionAnswersDTO.getIndexCorrect().toString());
                } else {
                    detailHistoryReadingDTO.setIndexCorrect("");
                }
                Long idDetail = detailHistoryReadingSingleRepository.insert(detailHistoryReadingDTO.toModel());
            }
        }
    }
}
