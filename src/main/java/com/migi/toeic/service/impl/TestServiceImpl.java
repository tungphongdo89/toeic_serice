package com.migi.toeic.service.impl;

import com.migi.toeic.base.DataListDTO;
import com.migi.toeic.base.ResultDataDTO;
import com.migi.toeic.base.common.amazons3.UpFileAmazon;
import com.migi.toeic.dto.*;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.model.History;
import com.migi.toeic.model.Test;
import com.migi.toeic.respositories.*;
import com.migi.toeic.service.CategoryTestService;
import com.migi.toeic.service.TestService;
import com.migi.toeic.utils.DateUtil;
import com.migi.toeic.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@PropertySource("classpath:i18n/messages.properties")
public class TestServiceImpl implements TestService {

    @Autowired
    private Environment env;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private CategoryTestRepository categoryTestRepository;
    @Autowired
    private QuestionAnswerListeningRepository questionAnswerListeningRepository;
    @Autowired
    private QuestionAnswerReadingRepository questionAnswerReadingRepository;
    @Autowired
    private ApParamRepository apParamRepository;
    @Autowired
    private DetailHistoryRepository detailHistoryRepository;
    @Autowired
    private UserRepository userRepository;

	@Autowired
	private CategoryTestService categoryTestService;


	@Override
	public DataListDTO doSearch(TestDTO testDTO) {
		if (testDTO.getStatus() != null && testDTO.getStatus() == 2) {
			testDTO.setStatus(null);
		}
		if (testDTO.getCreateFrom() != null) {
			String x = DateUtil.convertTimeDisplay(testDTO.getCreateFrom());
			testDTO.setCreateFromString(x);
		}
		if (testDTO.getCreateTo() != null) {
			String x = DateUtil.convertTimeDisplay(testDTO.getCreateTo());
			testDTO.setCreateToString(x);
		}
		if (testDTO.getCreateFrom() != null && testDTO.getCreateTo() != null) {
			if (DateUtil.compareDate(testDTO.getCreateFrom(), testDTO.getCreateTo()) == 0) {
				throw new BusinessException("Tạo bài thi từ phải nhỏ hơn thời gian tạo bài thi đến");
			}
			testDTO.setCreateFromString(DateUtil.convertTimeDisplay(testDTO.getCreateFrom()));
			testDTO.setCreateToString(DateUtil.convertTimeDisplay(testDTO.getCreateTo()));
		}
		ResultDataDTO resultDto = testRepository.doSearch(testDTO);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	public boolean checkId(Long id, TestDTO testDTO) {
		if (id == null) {
			return true;
		} else {
			TestDTO testDTO1 = testRepository.findByFiled("id", (Long) testDTO.getId()).toModel();
			if (testDTO1 != null) {
				return true;
			} else
				return false;
		}
	}

	@Override
	public Long insertTest(TestDTO testDTO) {
		Long id = null;

		try {
			id = testRepository.insert(testDTO.toModel());
		} catch (Exception e) {
			throw new BusinessException(env.getProperty("INSERT_ERROR"));
		}
		return id;
	}

	@Override
	@Transactional
	public String deleteTest(TestDTO testDTO) throws Exception {
		//Sửa lại sau khi thiết kế lại bảng trung gian
		Test test = testRepository.find(testDTO.getId());
		if (null == test) {
			throw new BusinessException(MessageUtils.getMessage("delete_test_false"));
		}
		List<History> history = historyRepository.find("testId", testDTO.getId());
		if (history.size() > 0) {
			throw new BusinessException(MessageUtils.getMessage("delete_test_false_lb"));
		}
		CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
		categoryTestDTO.setTestId(testDTO.getId());
		try {
			categoryTestDTO.setCategoryName(testDTO.getCategoryName());
			categoryTestService.deleteCategoryTest(categoryTestDTO);
		} catch (Exception ex) {
			return MessageUtils.getMessage("delete_test_false_other");
		}
		return MessageUtils.getMessage("delete_test_success");
	}

	@Override
	public void updateTest(TestDTO testDTO) {
		try {
			if (checkId(testDTO.getId().longValue(), testDTO)) {
				testRepository.update(testDTO.toModel());
			}
		} catch (Exception e) {
			throw new BusinessException(env.getProperty("UPDATE_ERROR"));
		}
	}

	@Transactional
	@Override
	public TestDTO getDetailTest(TestDTO testDTO) {
		List<CategoryDTO> lstCategory = testRepository.getCategoryByTestId(testDTO.getId());
		TestDTO test = testRepository.getTestById(testDTO);
		test.setLst(lstCategory);
		for (int i = 0; i < lstCategory.size(); i++) {
			float countScore = 0;
			List<QuestionAnswersDTO> lstQ = categoryRepository.getListQuestionByCategory(lstCategory.get(i).getCategoryId());
			for (QuestionAnswersDTO quest : lstQ) {
				countScore += quest.getScore();
			}
			lstCategory.get(i).setCountScore((long) countScore);
			lstCategory.get(i).setCountQuestion((long) lstQ.size());
		}
		if (test != null) {
			return test;
		} else {
			throw new BusinessException(env.getProperty("test_id_exist"));
		}
	}

	@Override
	public DataListDTO getCompletedStudent(TestDTO testDTO) {
		ResultDataDTO resultDto = testRepository.getStudentByTestId(testDTO);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Override
	public DataListDTO getQuestionReading(TestDTO testDTO) {
		ResultDataDTO resultDto = testRepository.getQuestionReadingByTestId(testDTO);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Override
	public DataListDTO getQuestionListening(TestDTO testDTO) {
		ResultDataDTO resultDto = testRepository.getQuestionListeningByTestId(testDTO);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Override
	public DataListDTO getResultReading(TestDTO testDTO) {
		ResultDataDTO resultDto = testRepository.getQuestionReadingByTestId(testDTO);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Override
	public DataListDTO getResultListening(TestDTO testDTO) {
		ResultDataDTO resultDto = testRepository.getQuestionListeningByTestId(testDTO);
		DataListDTO data = new DataListDTO();
		data.setData(resultDto.getData());
		data.setTotal(resultDto.getTotal());
		data.setSize(resultDto.getTotal());
		data.setStart(1);
		return data;
	}

	@Transactional
	@Override
	public String createTest(TestDTO test) {
		test.setType(1l);
		test.setRankCode(2l);
		test.setTiming(0l);
		test.setCreate_time(new Date());
		UpFileAmazon ufa = new UpFileAmazon();
		if (test.getTypeFile1().equals("FILE")) {
			//uploadfie
			try {
				String pathFile1 = ufa.UpLoadFile(test.getFileUpdate1(), null, null);
				test.setPathFile1(pathFile1);
			} catch (Exception e) {
				throw new BusinessException(env.getProperty("error_save_file_upload"));
			}
		}
		if (test.getTypeFile2().equals("FILE")) {
			//uploadFile
			try {
				String pathFile2 = ufa.UpLoadFile(test.getFileUpdate2(), null, null);
				test.setPathFile2(pathFile2);
			} catch (Exception e) {
				throw new BusinessException(env.getProperty("error_save_file_upload"));
			}
		}
		Long id = testRepository.insert(test.toModel());
		for (CategoryDTO cate : test.getLst()) {
			CategoryDTO temp = categoryRepository.find(cate.getCategoryId()).toModel();
			if (temp != null) {
				CategoryTestDTO cateTest = new CategoryTestDTO();
				cateTest.setTestId(id);
				cateTest.setCategoryId(cate.getCategoryId());
				cateTest.setStatus(1l);
				categoryTestRepository.insert(cateTest.toModel());
			} else {
				throw new BusinessException(env.getProperty("create_test_error_does_not_exist_category"));
			}
		}
		return env.getProperty("create_test_success");
	}

	@Override
	public DataListDTO getListCategoryTypeReadAndListen() {
		List<CategoryDTO> lstCate = categoryRepository.getListCategoryWithTotalScoreAndTotalQuestion(1l);
		lstCate.addAll(categoryRepository.getListCategoryWithTotalScoreAndTotalQuestion(2l));
		DataListDTO result = new DataListDTO();
		result.setData(lstCate);
		result.setTotal(lstCate.size());
		result.setSize(lstCate.size());
		result.setStart(1);
		return result;
	}

	@Override
	public List<MinitestDTO> getDetailFullTestById(TestDTO testDTO) {
		List<MinitestDTO> lst = new ArrayList<>();
		long sttQuestion = 0;
		String nameCategory = "";
		String nameTest = "";
		if (testDTO.getName() != null) {
			String[] cutName = testDTO.getName().split(" - ");
			nameCategory = cutName[0];
			nameTest = cutName[1];
		}

		MinitestDTO minitestDTOPart1 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart1 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART1");
		if (listCategoryPart1.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart1) {
				List<QuestionMinitestDTO> listQuestionPart1 = questionAnswerListeningRepository.
						getQuestionListeningByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart1 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart1) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart1.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart1);
			}
		}
		List<CategoryTestDTO> lstCategoryTestPart1 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(testDTO.getId(), nameCategory, "PART1");
		if (lstCategoryTestPart1.size() != 0) {
			minitestDTOPart1.setPathFile(lstCategoryTestPart1.get(0).getPathFile());
		}
		minitestDTOPart1.setPartName("PART1");
		minitestDTOPart1.setListCategoryMinitestDTOS(listCategoryPart1);
		lst.add(minitestDTOPart1);

		//---------------------------------------------------------------
		MinitestDTO minitestDTOPart2 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart2 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART2");
		if (listCategoryPart2.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart2) {
				List<QuestionMinitestDTO> listQuestionPart2 = questionAnswerListeningRepository.
						getQuestionListeningByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart2 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart2) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart2.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart2);
			}
		}
		List<CategoryTestDTO> lstCategoryTestPart2 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(testDTO.getId(), nameCategory, "PART2");
		if (lstCategoryTestPart2.size() != 0) {
			minitestDTOPart2.setPathFile(lstCategoryTestPart2.get(0).getPathFile());
		}
		minitestDTOPart2.setPartName("PART2");

		minitestDTOPart2.setListCategoryMinitestDTOS(listCategoryPart2);
		lst.add(minitestDTOPart2);

		//------------------------------------------------------------------
		MinitestDTO minitestDTOPart3 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart3 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART3");
		if (listCategoryPart3.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart3) {
				List<QuestionMinitestDTO> listQuestionPart3 = questionAnswerListeningRepository.
						getQuestionListeningByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart3 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart3) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart3.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart3);
			}
		}
		List<CategoryTestDTO> lstCategoryTestPart3 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(testDTO.getId(), nameCategory, "PART3");
		if (lstCategoryTestPart3.size() != 0) {
			minitestDTOPart3.setPathFile(lstCategoryTestPart3.get(0).getPathFile());
		}
		minitestDTOPart3.setPartName("PART3");
		minitestDTOPart3.setListCategoryMinitestDTOS(listCategoryPart3);
		lst.add(minitestDTOPart3);

		//------------------------------------------------------------------------
		MinitestDTO minitestDTOPart4 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart4 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART4");
		if (listCategoryPart4.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart4) {
				List<QuestionMinitestDTO> listQuestionPart4 = questionAnswerListeningRepository.
						getQuestionListeningByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart4 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart4) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart4.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart4);
			}
		}
		List<CategoryTestDTO> lstCategoryTestPart4 = categoryTestRepository.getListCategoryTestByTestIdAndCategoryName(testDTO.getId(), nameCategory, "PART4");
		if (lstCategoryTestPart4.size() != 0) {
			minitestDTOPart4.setPathFile(lstCategoryTestPart4.get(0).getPathFile());
		}
		minitestDTOPart4.setPartName("PART4");
		minitestDTOPart4.setListCategoryMinitestDTOS(listCategoryPart4);
		lst.add(minitestDTOPart4);

		//-----------------------------------------------------------------------------
		MinitestDTO minitestDTOPart5 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart5 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART5");
		if (listCategoryPart5.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart5) {
				List<QuestionMinitestDTO> listQuestionPart5 = questionAnswerReadingRepository
						.getQuestionReadingByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart5 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart5) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart5.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart5);
			}
		}
		minitestDTOPart5.setPartName("PART5");
		minitestDTOPart5.setListCategoryMinitestDTOS(listCategoryPart5);
		lst.add(minitestDTOPart5);

		//-------------------------------------------------------------------------------------
		MinitestDTO minitestDTOPart6 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart6 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART6");
		if (listCategoryPart6.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart6) {
				List<QuestionMinitestDTO> listQuestionPart6 = questionAnswerReadingRepository
						.getQuestionReadingByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart6 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart6) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart6.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart6);
			}
		}
		minitestDTOPart6.setPartName("PART6");
		minitestDTOPart6.setListCategoryMinitestDTOS(listCategoryPart6);
		lst.add(minitestDTOPart6);

		//------------------------------------------------------------------------------------
		MinitestDTO minitestDTOPart7 = new MinitestDTO();
		List<CategoryMinitestDTO> listCategoryPart7 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART7");
		List<CategoryMinitestDTO> listCategoryPart8 = testRepository.getListCategoryByTestId(testDTO, nameCategory, "PART8");
		listCategoryPart7.addAll(listCategoryPart8);
		if (listCategoryPart7.size() != 0) {
			for (CategoryMinitestDTO categoryMinitestDTO : listCategoryPart7) {
				List<QuestionMinitestDTO> listQuestionPart7 = questionAnswerReadingRepository
						.getQuestionReadingByCategoryId(categoryMinitestDTO.getCategoryId(), categoryMinitestDTO.getTestId());
				List<QuestionMinitestDTO> listQuestionListeningPart7 = new ArrayList<>();
				for (QuestionMinitestDTO questionMinitestDTO : listQuestionPart7) {
					sttQuestion = sttQuestion + 1;
					questionMinitestDTO.setStt(sttQuestion);
					listQuestionListeningPart7.add(questionMinitestDTO);
				}
				categoryMinitestDTO.setListQuestionMinitestDTOS(listQuestionListeningPart7);
			}
		}
		minitestDTOPart7.setPartName("PART7");
		minitestDTOPart7.setListCategoryMinitestDTOS(listCategoryPart7);
		lst.add(minitestDTOPart7);

		return lst;
	}

	public MinitestDTO convertFormatTestListening(List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChildRoot) {
		MinitestDTO minitestDTOPart = new MinitestDTO();
		int totalQuestion = 0;
		int totalCorectAnswer = 0;
		List<CategoryMinitestDTO> lisCategoryMinitestDTOsPart = new ArrayList<>();
		CategoryMinitestDTO categoryMinitestDTOPart = new CategoryMinitestDTO();

		List<CategoryListeningChoosenDTO> lstCategoryListeningChooseDTO = new ArrayList<>();
		for (int i = 0; i < listQuestionListeningChoosenDTOSChildRoot.size(); i++) {
			if (lstCategoryListeningChooseDTO.size() == 0 ||
					!listQuestionListeningChoosenDTOSChildRoot.get(i).getCategoryId().equals(lstCategoryListeningChooseDTO.get(lstCategoryListeningChooseDTO.size() - 1).getCategoryId())) {
				CategoryListeningChoosenDTO categoryListeningChoosenDTOChild = new CategoryListeningChoosenDTO();

				List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChild = new ArrayList<>();
				listQuestionListeningChoosenDTOSChild.add(listQuestionListeningChoosenDTOSChildRoot.get(i));
				categoryListeningChoosenDTOChild.setListQuestionListeningChooseDTO(listQuestionListeningChoosenDTOSChild);
				categoryListeningChoosenDTOChild.setCategoryId(listQuestionListeningChoosenDTOSChildRoot.get(i).getCategoryId());
				lstCategoryListeningChooseDTO.add(categoryListeningChoosenDTOChild);

			} else if (listQuestionListeningChoosenDTOSChildRoot.get(i).getCategoryId().equals(lstCategoryListeningChooseDTO.get(lstCategoryListeningChooseDTO.size() - 1).getCategoryId())) {
				lstCategoryListeningChooseDTO.get(lstCategoryListeningChooseDTO.size() - 1).getListQuestionListeningChooseDTO().add(listQuestionListeningChoosenDTOSChildRoot.get(i));
			}
		}

		for (CategoryListeningChoosenDTO categoryListeningChoosenDTO : lstCategoryListeningChooseDTO) {
			List<QuestionMinitestDTO> listQuestionMinitestDTOsPart = new ArrayList<>();
			for (QuestionListeningChoosenDTO questionListeningChoosenDTO : categoryListeningChoosenDTO.getListQuestionListeningChooseDTO()) {

				categoryMinitestDTOPart = categoryRepository.getCategoryById(questionListeningChoosenDTO.getCategoryId());

				QuestionMinitestDTO ques = questionAnswerListeningRepository.getResultQuestionListeningById(questionListeningChoosenDTO.getId());

				if (questionListeningChoosenDTO.getAnswerChoosen().trim().equalsIgnoreCase(ques.getAnswer())) {
					ques.setIndexCorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer());
					ques.setIndexIncorrectAnswer((long) -1);
					ques.setStt(questionListeningChoosenDTO.getStt());
					totalQuestion++;
					totalCorectAnswer++;
				} else {
					long indexCorrectAnswer = -1;
					if (ques.getAnswer().trim().equalsIgnoreCase("A")) {
						indexCorrectAnswer = 0;
					} else if (ques.getAnswer().trim().equalsIgnoreCase("B")) {
						indexCorrectAnswer = 1;
					} else if (ques.getAnswer().trim().equalsIgnoreCase("C")) {
						indexCorrectAnswer = 2;
					} else if (ques.getAnswer().trim().equalsIgnoreCase("D")) {
						indexCorrectAnswer = 3;
					}
					ques.setIndexCorrectAnswer((indexCorrectAnswer));
					ques.setIndexIncorrectAnswer(questionListeningChoosenDTO.getIndexSubAnswer());
					ques.setStt(questionListeningChoosenDTO.getStt());
					totalQuestion++;
				}
				listQuestionMinitestDTOsPart.add(ques);
			}
			categoryMinitestDTOPart.setListQuestionMinitestDTOS(listQuestionMinitestDTOsPart);
			lisCategoryMinitestDTOsPart.add(categoryMinitestDTOPart);
		}

		minitestDTOPart.setTotalQuestion(totalQuestion);
		minitestDTOPart.setTotalCorectAnswer(totalCorectAnswer);
		minitestDTOPart.setListCategoryMinitestDTOS(lisCategoryMinitestDTOsPart);
		return minitestDTOPart;
	}

	public MinitestDTO convertFormatTestReading(List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChildRoot) {
		MinitestDTO minitestDTOPart = new MinitestDTO();
		int totalQuestion = 0;
		int totalCorectAnswer = 0;
		List<CategoryMinitestDTO> lisCategoryMinitestDTOsPart = new ArrayList<>();
		CategoryMinitestDTO categoryMinitestDTOPart = new CategoryMinitestDTO();

		List<CategoryReadingChoosenDTO> lstCategoryReadingChooseDTO = new ArrayList<>();
		for (int i = 0; i < listQuestionReadingChoosenDTOSChildRoot.size(); i++) {
			if (lstCategoryReadingChooseDTO.size() == 0 ||
					!listQuestionReadingChoosenDTOSChildRoot.get(i).getCategoryId().equals(lstCategoryReadingChooseDTO.get(lstCategoryReadingChooseDTO.size() - 1).getCategoryId())) {
				CategoryReadingChoosenDTO categoryReadingChoosenDTOChild = new CategoryReadingChoosenDTO();

				List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChild = new ArrayList<>();
				listQuestionReadingChoosenDTOSChild.add(listQuestionReadingChoosenDTOSChildRoot.get(i));
				categoryReadingChoosenDTOChild.setListQuestionReadingChooseDTO(listQuestionReadingChoosenDTOSChild);
				categoryReadingChoosenDTOChild.setCategoryId(listQuestionReadingChoosenDTOSChildRoot.get(i).getCategoryId());
				lstCategoryReadingChooseDTO.add(categoryReadingChoosenDTOChild);

			} else if (listQuestionReadingChoosenDTOSChildRoot.get(i).getCategoryId().equals(lstCategoryReadingChooseDTO.get(lstCategoryReadingChooseDTO.size() - 1).getCategoryId())) {
				lstCategoryReadingChooseDTO.get(lstCategoryReadingChooseDTO.size() - 1).getListQuestionReadingChooseDTO().add(listQuestionReadingChoosenDTOSChildRoot.get(i));
			}
		}

		for (CategoryReadingChoosenDTO categoryReadingChoosenDTO : lstCategoryReadingChooseDTO) {
			List<QuestionMinitestDTO> listQuestionMinitestDTOsPart1 = new ArrayList<>();
			for (QuestionReadingChoosenDTO questionReadingChoosenDTO : categoryReadingChoosenDTO.getListQuestionReadingChooseDTO()) {
				categoryMinitestDTOPart = categoryRepository.getCategoryById(questionReadingChoosenDTO.getCategoryId());

				QuestionMinitestDTO ques = questionAnswerReadingRepository.getResultQuestionReadingById(questionReadingChoosenDTO.getId());

				if (questionReadingChoosenDTO.getAnswerChoosen().trim().equals(ques.getAnswer())) {
					ques.setIndexCorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer());
					ques.setIndexIncorrectAnswer((long) -1);
					ques.setStt(questionReadingChoosenDTO.getStt());
					totalQuestion++;
					totalCorectAnswer++;
				} else {
					long indexCorrectAnswer = -1;
					if (ques.getAnswer().trim().equalsIgnoreCase("A")) {
						indexCorrectAnswer = 0;
					} else if (ques.getAnswer().trim().equalsIgnoreCase("B")) {
						indexCorrectAnswer = 1;
					} else if (ques.getAnswer().trim().equalsIgnoreCase("C")) {
						indexCorrectAnswer = 2;
					} else if (ques.getAnswer().trim().equalsIgnoreCase("D")) {
						indexCorrectAnswer = 3;
					}
					ques.setIndexCorrectAnswer((indexCorrectAnswer));
					ques.setIndexIncorrectAnswer(questionReadingChoosenDTO.getIndexSubAnswer());
					ques.setStt(questionReadingChoosenDTO.getStt());
					totalQuestion++;
				}
				listQuestionMinitestDTOsPart1.add(ques);
			}
			categoryMinitestDTOPart.setListQuestionMinitestDTOS(listQuestionMinitestDTOsPart1);
			lisCategoryMinitestDTOsPart.add(categoryMinitestDTOPart);
		}

		minitestDTOPart.setTotalQuestion(totalQuestion);
		minitestDTOPart.setTotalCorectAnswer(totalCorectAnswer);
		minitestDTOPart.setListCategoryMinitestDTOS(lisCategoryMinitestDTOsPart);
		return minitestDTOPart;
	}

	@Override
	public List<MinitestDTO> getResultDetailFullTestById(MinitestSubmitAnswerDTO minitestSubmitAnswerDTO) {
		List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOS = minitestSubmitAnswerDTO.getListQuestionListeningChoosenDTOS();
		List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOS = minitestSubmitAnswerDTO.getListQuestionReadingChoosenDTOS();
		List<MinitestDTO> lst = new ArrayList<>();

		List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChildRoot1 = new ArrayList<>();
		List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChildRoot2 = new ArrayList<>();
		List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChildRoot3 = new ArrayList<>();
		List<QuestionListeningChoosenDTO> listQuestionListeningChoosenDTOSChildRoot4 = new ArrayList<>();
		List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChildRoot5 = new ArrayList<>();
		List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChildRoot6 = new ArrayList<>();
		List<QuestionReadingChoosenDTO> listQuestionReadingChoosenDTOSChildRoot7 = new ArrayList<>();
		for (QuestionListeningChoosenDTO questionListeningChoosenDTO : listQuestionListeningChoosenDTOS) {
			if (questionListeningChoosenDTO.getPartName().equalsIgnoreCase("PART1")) {
				listQuestionListeningChoosenDTOSChildRoot1.add(questionListeningChoosenDTO);
			}
			if (questionListeningChoosenDTO.getPartName().equalsIgnoreCase("PART2")) {
				listQuestionListeningChoosenDTOSChildRoot2.add(questionListeningChoosenDTO);
			}
			if (questionListeningChoosenDTO.getPartName().equalsIgnoreCase("PART3")) {
				listQuestionListeningChoosenDTOSChildRoot3.add(questionListeningChoosenDTO);
			}
			if (questionListeningChoosenDTO.getPartName().equalsIgnoreCase("PART4")) {
				listQuestionListeningChoosenDTOSChildRoot4.add(questionListeningChoosenDTO);
			}

		}
		for (QuestionReadingChoosenDTO questionReadingChoosenDTO : listQuestionReadingChoosenDTOS) {
			if (questionReadingChoosenDTO.getPartName().equalsIgnoreCase("PART5")) {
				listQuestionReadingChoosenDTOSChildRoot5.add(questionReadingChoosenDTO);
			}
			if (questionReadingChoosenDTO.getPartName().equalsIgnoreCase("PART6")) {
				listQuestionReadingChoosenDTOSChildRoot6.add(questionReadingChoosenDTO);
			}
			if (questionReadingChoosenDTO.getPartName().equalsIgnoreCase("PART7")) {
				listQuestionReadingChoosenDTOSChildRoot7.add(questionReadingChoosenDTO);
			}
		}
		MinitestDTO minitestDTOPart1 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRoot1);
		minitestDTOPart1.setPartName("PART1");
		correctPart(minitestDTOPart1);

		MinitestDTO minitestDTOPart2 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRoot2);
		minitestDTOPart2.setPartName("PART2");
		correctPart(minitestDTOPart2);

		MinitestDTO minitestDTOPart3 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRoot3);
		minitestDTOPart3.setPartName("PART3");
		correctPart(minitestDTOPart3);

		MinitestDTO minitestDTOPart4 = convertFormatTestListening(listQuestionListeningChoosenDTOSChildRoot4);
		minitestDTOPart4.setPartName("PART4");
		correctPart(minitestDTOPart4);

		MinitestDTO minitestDTOPart5 = convertFormatTestReading(listQuestionReadingChoosenDTOSChildRoot5);
		minitestDTOPart5.setPartName("PART5");
		correctPart(minitestDTOPart5);

		MinitestDTO minitestDTOPart6 = convertFormatTestReading(listQuestionReadingChoosenDTOSChildRoot6);
		minitestDTOPart6.setPartName("PART6");
		correctPart(minitestDTOPart6);

		MinitestDTO minitestDTOPart7 = convertFormatTestReading(listQuestionReadingChoosenDTOSChildRoot7);
		minitestDTOPart7.setPartName("PART7");
		correctPart(minitestDTOPart7);

		int sumCorrectAnswerListening = minitestDTOPart1.getTotalCorectAnswer() + minitestDTOPart2.getTotalCorectAnswer() + minitestDTOPart3.getTotalCorectAnswer()
				+ minitestDTOPart4.getTotalCorectAnswer();
		int sumCorrectAnswerReading = minitestDTOPart5.getTotalCorectAnswer() + minitestDTOPart6.getTotalCorectAnswer()
				+ minitestDTOPart7.getTotalCorectAnswer();
		ApParamDTO apParamDTOListening = apParamRepository.getScoreListening();
		String value = apParamDTOListening.getValue();

		String[] scoreArrayListening = value.split("\\|");
		String sumScoreStringListening = scoreArrayListening[sumCorrectAnswerListening].trim();
		int sumScoreListening = Integer.parseInt(sumScoreStringListening);
		minitestDTOPart1.setSumScore(sumScoreListening);

		ApParamDTO apParamDTOReading = apParamRepository.getScoreReading();
		String valueReading = apParamDTOReading.getValue();

		String[] scoreArrayReading = valueReading.split("\\|");
		String sumScoreStringReading = scoreArrayReading[sumCorrectAnswerReading].trim();
		int sumScoreReading = Integer.parseInt(sumScoreStringReading);
		minitestDTOPart5.setSumScore(sumScoreReading);

		long sumScore = sumScoreListening + sumScoreReading;

		if (minitestDTOPart1 != null && minitestDTOPart2 != null && minitestDTOPart3 != null &&
				minitestDTOPart4 != null && minitestDTOPart5 != null && minitestDTOPart6 != null && minitestDTOPart7 != null) {
			lst.add(minitestDTOPart1);
			lst.add(minitestDTOPart2);
			lst.add(minitestDTOPart3);
			lst.add(minitestDTOPart4);
			lst.add(minitestDTOPart5);
			lst.add(minitestDTOPart6);
			lst.add(minitestDTOPart7);
		}
		Long idHistory = createHistory(minitestSubmitAnswerDTO, sumScore, sumScoreListening, sumScoreReading,
				minitestDTOPart1, minitestDTOPart2, minitestDTOPart3, minitestDTOPart4, minitestDTOPart5, minitestDTOPart6, minitestDTOPart7);
		createDetailHistory(idHistory, minitestDTOPart1);
		createDetailHistory(idHistory, minitestDTOPart2);
		createDetailHistory(idHistory, minitestDTOPart3);
		createDetailHistory(idHistory, minitestDTOPart4);
		createDetailHistory(idHistory, minitestDTOPart5);
		createDetailHistory(idHistory, minitestDTOPart6);
		createDetailHistory(idHistory, minitestDTOPart7);
		return lst;
	}

	public void createDetailHistory(Long idHistory, MinitestDTO minitestDTO) {
		for (int i = 0; i < minitestDTO.getListCategoryMinitestDTOS().size(); i++) {
			for (int j = 0; j < minitestDTO.getListCategoryMinitestDTOS().get(i).getListQuestionMinitestDTOS().size(); j++) {
				DetailHistoryDTO detailHistoryDTO = new DetailHistoryDTO();
				detailHistoryDTO.setParentId(idHistory);
				detailHistoryDTO.setCategoryId(minitestDTO.getListCategoryMinitestDTOS().get(i).getCategoryId());
				detailHistoryDTO.setPart(minitestDTO.getPartName());
				detailHistoryDTO.setTypeTest(1l);
				detailHistoryDTO.setQuestionId(minitestDTO.getListCategoryMinitestDTOS().get(i).getListQuestionMinitestDTOS().get(j).getId());
				detailHistoryDTO.setIndexCorrect(minitestDTO.getListCategoryMinitestDTOS().get(i).getListQuestionMinitestDTOS().get(j).getIndexCorrectAnswer());
				detailHistoryDTO.setIndexIncorrect(minitestDTO.getListCategoryMinitestDTOS().get(i).getListQuestionMinitestDTOS().get(j).getIndexIncorrectAnswer());
				Long idDetailHistory = detailHistoryRepository.insert(detailHistoryDTO.toModel());
			}
		}
	}

    public Long createHistory(MinitestSubmitAnswerDTO minitestSubmitAnswerDTO,Long sumScore,int sumScoreListening,int sumScoreReading,
                              MinitestDTO minitestDTOPart1,MinitestDTO minitestDTOPart2,MinitestDTO minitestDTOPart3,MinitestDTO minitestDTOPart4,
                              MinitestDTO minitestDTOPart5,MinitestDTO minitestDTOPart6,MinitestDTO minitestDTOPart7){
//        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//        Date createDate = Date.from(localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setTestId(minitestSubmitAnswerDTO.getTestId());
        historyDTO.setUserId(minitestSubmitAnswerDTO.getUserId());
        historyDTO.setTotalTime(minitestSubmitAnswerDTO.getTotalTime());

        historyDTO.setUserShowName(minitestSubmitAnswerDTO.getUserShowName());
        historyDTO.setStatus(1l);
        historyDTO.setTotalScore(sumScore);
        historyDTO.setListeningScore(Long.valueOf(sumScoreListening));
        historyDTO.setReadingScore(Long.valueOf(sumScoreReading));
        historyDTO.setCreateDate(new Date());
        historyDTO.setPart1(correctPart(minitestDTOPart1));
        historyDTO.setPart2(correctPart(minitestDTOPart2));
        historyDTO.setPart3(correctPart(minitestDTOPart3));
        historyDTO.setPart4(correctPart(minitestDTOPart4));
        historyDTO.setPart5(correctPart(minitestDTOPart5));
        historyDTO.setPart6(correctPart(minitestDTOPart6));
        historyDTO.setPart7(correctPart(minitestDTOPart7));
        historyDTO.setTestName(minitestSubmitAnswerDTO.getTestName());
        Long idHistory = historyRepository.insert(historyDTO.toModel());

        SysUserDTO userDTO = userRepository.getUserById(minitestSubmitAnswerDTO.getUserId());
        userDTO.setCurrentScore(sumScore);
        userRepository.update(userDTO.toModel());
        return idHistory;
    }

	public String correctPart(MinitestDTO minitestDTOPart) {
		int totalQuestion = minitestDTOPart.getTotalQuestion();
		int totalCorectAnswer = minitestDTOPart.getTotalCorectAnswer();
		String correctPart = String.valueOf(totalCorectAnswer) + "/" + String.valueOf(totalQuestion);
		return correctPart;
	}

	public String cutString(String s) {
//        String[] parts = s.split("|");
//        String part1 = parts[0]; // 004
//        String part2 = parts[1]; // 034556
//        return part1.trim();
		s = s.substring(0, s.length() - 1);
		return s.trim();
	}


	@Override
	public TestDTO findDetailByName(TestDTO obj) {
		Test dto = testRepository.findByFiled("name", obj.getName());
		return null != dto ? dto.toModel() : null;
	}

	@Override
	public List<TestDTO> getListNameTest() {
		List<TestDTO> listNameTest = new ArrayList<>();
		List<CategoryDTO> listNameCategory = testRepository.getListNameCategoryOfTest();
		if (listNameCategory.size() != 0) {
			for (CategoryDTO categoryDTO : listNameCategory) {
				List<TestDTO> listName = testRepository.getListNameTestByNameCategory(categoryDTO);
				listNameTest.addAll(listName);
			}
		}
		return listNameTest;
	}

}
