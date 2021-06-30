package com.migi.toeic.service.impl;

import com.migi.toeic.constants.Constants;
import com.migi.toeic.dto.*;
import com.migi.toeic.respositories.CategoryTestRepository;
import com.migi.toeic.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ImportDataServiceImpl implements ImportDataService {

	@Autowired
	TestService testService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	TopicService topicService;

	@Autowired
	CategoryTestRepository categoryTestRepository;

	@Autowired
	QuestionAnswerListeningService questionAnswerListeningService;

	@Autowired
	QuestionAnswerReadingService questionAnswerReadingService;

	@Autowired
	private Environment env;

	HashMap<String, TopicDTO> checkExist;
	HashMap<String, CategoryDTO> checkExistCategory;

	@Override
	@Transactional
	public String importDataPart1(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);

			DataFormatter formatter = new DataFormatter();
			int count = 0;
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();
			for (Row row : sheet) {
				count++;
				if (count > 2) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(20).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(19).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					String blankValueA = row.getCell(9).getStringCellValue();
					String blankValueB = row.getCell(12).getStringCellValue();
					String blankValueC = row.getCell(15).getStringCellValue();
					String blankValueD = row.getCell(18).getStringCellValue();

					Long topicIdForPart1 = 0l;
					Long topicIdForPart1_1 = 0l;
					if (StringUtils.isNotBlank(blankValueA)) {
						topic.setPartTopicCode("PART1.1");
						topicIdForPart1_1 = checkExistTopic(topic, "PART1.1", "LISTENING_FILL_UNIT");
					}
					topic.setPartTopicCode("PART1");
					topicIdForPart1 = checkExistTopic(topic, "PART1", "LISTENING_UNIT");

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = row.getCell(20).getStringCellValue();
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(resutlLevelCode);
					String sentenceNo = convertValue(row.getCell(3));
					categoryDTO.setArraySentenceNo(sentenceNo);
					Long categoryIdForPart1_1 = 0l;

					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();

					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setTestId(testId);
					if (StringUtils.isNotBlank(blankValueA)) {
						categoryDTO.setPartCode("PART1.1");
						categoryDTO.setTypeCode(5l);
						categoryIdForPart1_1 = checkExistCategory(categoryDTO, topicIdForPart1_1, testName);
						categoryTestDTO.setPart("PART1.1");
						categoryTestDTO.setCategoryId(categoryIdForPart1_1);
						saveCategoryTest(categoryTestDTO);
					}
					categoryDTO.setPartCode("PART1");
					categoryDTO.setTypeCode(2l);
					Long categoryIdForPart1 = checkExistCategory(categoryDTO, topicIdForPart1, testName);
					categoryTestDTO.setPart("PART1");
					categoryTestDTO.setCategoryId(categoryIdForPart1);
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestDTO.setPathFile(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + env.getProperty("fulltest") + "/" + categoryName + "-" + testName + "-" + env.getProperty("fulltestMp3"));
					saveCategoryTest(categoryTestDTO);

					// Lưu thông tin chi tiết câu hỏi
					QuestionAnswerListeningDTO answerListeningDTO = new QuestionAnswerListeningDTO();
					if (StringUtils.isNotBlank(blankValueA)) {
						answerListeningDTO.setParentId(categoryIdForPart1_1);
						//cat chuoi blank cho phan bai nghe dien tu
						String questionName = mergeQuestion(blankValueA, blankValueB, blankValueC, blankValueD, 4);
						answerListeningDTO.setName(questionName);
						String dataForPart1_1 = splitAnswer(questionName, "");
						answerListeningDTO.setAnswer(dataForPart1_1);
						answerListeningDTO.setAnswersToChoose("NULL |");
						answerListeningDTO.setNumOfAnswer(1l);
						answerListeningDTO.setTestId(testId);
						saveDetailQuestion(answerListeningDTO, row, categoryName, testId, 5, 4, 8, 11, 14, 17, 3, 21, 22, 23, 24, 0, "PART1");
					}
					//khong co blank thi la phan bai nghe toeic
					answerListeningDTO.setParentId(categoryIdForPart1);
					answerListeningDTO.setName("bai nghe toeic");
					answerListeningDTO.setAnswer(row.getCell(6).getStringCellValue());
					answerListeningDTO.setNumOfAnswer(4l);
					answerListeningDTO.setAnswersToChoose(answersToChose(4));
					answerListeningDTO.setTestId(testId);
					saveDetailQuestion(answerListeningDTO, row, categoryName, testId, 5, 4, 8, 11, 14, 17, 3, 21, 22, 23, 24, 0, "PART1");
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}

	public Long checkExistTopic(TopicDTO topicData, String part, String type) {
		List<TopicDTO> lstTopic = topicService.getListTopicByName(topicData);
		TopicDTO topicDTO = checkExist.get(topicData.getTopicName() + "_" + topicData.getPartTopicCode());
		Long topicId;
		if (lstTopic.size() == 0 && null == topicDTO) {
			TopicDTO topic = new TopicDTO();
			topic.setTopicName(topicData.getTopicName());
			topic.setStatus(1l);
			topic.setCreatedTime(new Date());
			topic.setCode("TOPIC_" + new Date().getTime());
			//kiem tra topic thuoc bai nghe dien tu hay bai nghe toeic

			topic.setPartTopicCode(part);
			topic.setTypeTopicCode(type);
			topic.setLastUpdate(new Date());
			topicId = topicService.insert(topic);
			topic.setTopicId(topicId);
			checkExist.put(topic.getTopicName() + "_" + topic.getPartTopicCode(), topic);
		} else {
			if (lstTopic.size() == 0) {
				topicId = topicDTO.getTopicId();
			} else {
				topicId = lstTopic.get(0).getTopicId();
			}
		}
		return topicId;
	}

	public Long checkExistCategory(CategoryDTO categoryDTO, Long topicId, String testName) {
		List<CategoryDTO> lst = categoryService.getListCategoryByName(categoryDTO);
		CategoryDTO categoryDTOtmp = checkExistCategory.get(categoryDTO.getNameCategory() + "_" + categoryDTO.getLevelCode() + "_" + categoryDTO.getPartCode() + "_" + categoryDTO.getArraySentenceNo() + "_" + categoryDTO.getTypeCode());
		if (StringUtils.isNotBlank(testName)) {
			categoryDTOtmp = checkExistCategory.get(categoryDTO.getNameCategory() + "_" + categoryDTO.getLevelCode() + "_" + categoryDTO.getPartCode() + "_" + categoryDTO.getArraySentenceNo() + "_" + categoryDTO.getTypeCode() + "_" + testName);
		}
		Long categoryId;
		if (lst.size() == 0 && null == categoryDTOtmp) {
			categoryDTO.setTypeCode(categoryDTO.getTypeCode());
			categoryDTO.setParentId(topicId);
			categoryDTO.setStatus(1l);
			categoryDTO.setCreatedDate(new Date());
			categoryId = categoryService.insertData(categoryDTO);
			categoryDTO.setCategoryId(categoryId);
			if (StringUtils.isNotBlank(testName)) {
				checkExistCategory.put(categoryDTO.getNameCategory() + "_" + categoryDTO.getLevelCode() + "_" + categoryDTO.getPartCode() + "_" + categoryDTO.getArraySentenceNo() + "_" + categoryDTO.getTypeCode() + "_" + testName, categoryDTO);
			} else {
				checkExistCategory.put(categoryDTO.getNameCategory() + "_" + categoryDTO.getLevelCode() + "_" + categoryDTO.getPartCode() + "_" + categoryDTO.getArraySentenceNo() + "_" + categoryDTO.getTypeCode(), categoryDTO);
			}
		} else {
			if (lst.size() == 0) {
				categoryId = categoryDTOtmp.getCategoryId();
			} else {
				categoryId = lst.get(0).getCategoryId();
			}
		}
		return categoryId;
	}

	public void saveCategoryTest(CategoryTestDTO categoryTestDTO) {
		categoryTestRepository.insert(categoryTestDTO.toModel());
	}

	public void saveDetailQuestion(QuestionAnswerListeningDTO answerListeningDTO, Row row, String categoryName, Long testId, int vitri1, int vitri2, int vitri3, int vitri4, int vitri5, int vitri6, int vitri7, int vitri8, int vitri9, int vitri10, int vitri11, int vitri12, String part) {
		answerListeningDTO.setStatus(1l);
		Map<String, String> resultData = convertDescription(row, part);
		answerListeningDTO.setDescription(resultData.get("AnswerWithTrans"));//thieu data
		answerListeningDTO.setTypeFile1("FILE");
		answerListeningDTO.setTypeFile2("FILE");
		answerListeningDTO.setPathFile1(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + part + "/" + row.getCell(vitri1).getStringCellValue());
		if (vitri2 != 0) {
			answerListeningDTO.setPathFile2(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/" + row.getCell(vitri2).getStringCellValue());
		}
		answerListeningDTO.setTranscript(resultData.get("AnswerNotTrans"));

		if (!"PART2".equalsIgnoreCase(part)) {
			answerListeningDTO.setTranslatingQuesA(row.getCell(vitri3).getStringCellValue());
			answerListeningDTO.setTranslatingQuesB(row.getCell(vitri4).getStringCellValue());
			answerListeningDTO.setTranslatingQuesC(row.getCell(vitri5).getStringCellValue());
			answerListeningDTO.setTranslatingQuestion("");
			answerListeningDTO.setTranslatingQuesD(row.getCell(vitri6).getStringCellValue());
		} else {
			answerListeningDTO.setTranslatingQuestion(row.getCell(vitri3).getStringCellValue());
			answerListeningDTO.setTranslatingQuesA(row.getCell(vitri4).getStringCellValue());
			answerListeningDTO.setTranslatingQuesB(row.getCell(vitri5).getStringCellValue());
			answerListeningDTO.setTranslatingQuesC(row.getCell(vitri6).getStringCellValue());
		}
		answerListeningDTO.setSentenceNo(StringUtils.isNotBlank(row.getCell(vitri7).getStringCellValue()) ? Long.parseLong(row.getCell(vitri7).getStringCellValue()) : null);
		answerListeningDTO.setPathFileQuesA(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + part + "/" + row.getCell(vitri8).getStringCellValue());
		answerListeningDTO.setPathFileQuesB(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + part + "/" + row.getCell(vitri9).getStringCellValue());
		answerListeningDTO.setPathFileQuesC(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + part + "/" + row.getCell(vitri10).getStringCellValue());
		if (vitri12 != 0) {
			answerListeningDTO.setPathFileQues(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + part + "/" + row.getCell(vitri12).getStringCellValue());
		}
		if (vitri11 != 0) {
			answerListeningDTO.setPathFileQuesD(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + part + "/" + row.getCell(vitri11).getStringCellValue());
		}
		if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
			answerListeningDTO.setTestId(testId);
		}
		questionAnswerListeningService.insertData(answerListeningDTO);
	}

	@Override
	@Transactional
	public String importDataPart2(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			DataFormatter formatter = new DataFormatter();
			int count = 0;

			for (Row row : sheet) {
				count++;
				if (count > 2) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(19).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(18).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					String blankValueA = row.getCell(8).getStringCellValue();
					String blankValueB = row.getCell(11).getStringCellValue();
					String blankValueC = row.getCell(14).getStringCellValue();
					String blankValueD = row.getCell(17).getStringCellValue();

					Long topicIdForPart2 = 0l;
					Long topicIdForPart2_1 = 0l;
					if (StringUtils.isNotBlank(blankValueA)) {
						topic.setPartTopicCode("PART2.1");
						topicIdForPart2_1 = checkExistTopic(topic, "PART2.1", "LISTENING_FILL_UNIT");
					}
					topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
					topicIdForPart2 = checkExistTopic(topic, "PART2", "LISTENING_UNIT");


					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = row.getCell(19).getStringCellValue();
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(resutlLevelCode);
					String sentenceNo = convertValue(row.getCell(3));
					categoryDTO.setArraySentenceNo(sentenceNo);
					Long categoryIdForPart2_1 = 0l;
					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestDTO.setPathFile(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + env.getProperty("fulltest") + "/" + categoryName + "-" + testName + "-" + env.getProperty("fulltestMp3"));
					categoryTestDTO.setTestId(testId);
					if (StringUtils.isNotBlank(blankValueA)) {
						categoryDTO.setPartCode("PART2.1");
						categoryDTO.setTypeCode(5l);
						categoryIdForPart2_1 = checkExistCategory(categoryDTO, topicIdForPart2_1, testName);
						categoryTestDTO.setPart("PART2.1");
						categoryTestDTO.setCategoryId(categoryIdForPart2_1);
						saveCategoryTest(categoryTestDTO);
					}
					categoryDTO.setPartCode("PART2");
					categoryDTO.setTypeCode(2l);
					Long categoryIdForPart2 = checkExistCategory(categoryDTO, topicIdForPart2, testName);
					categoryTestDTO.setPart("PART2");
					categoryTestDTO.setCategoryId(categoryIdForPart2);
					saveCategoryTest(categoryTestDTO);

					// Lưu thông tin chi tiết câu hỏi
					QuestionAnswerListeningDTO answerListeningDTO = new QuestionAnswerListeningDTO();
					if (StringUtils.isNotBlank(blankValueA)) {
						answerListeningDTO.setParentId(categoryIdForPart2_1);
						//cat chuoi blank cho phan bai nghe dien tu
						String questionName = mergeQuestion(blankValueA, blankValueB, blankValueC, blankValueD, 4);
						answerListeningDTO.setName(questionName);
						String dataForPart2_1 = splitAnswer(questionName, "");
						answerListeningDTO.setAnswer(dataForPart2_1);
						answerListeningDTO.setAnswersToChoose("NULL |");
						answerListeningDTO.setNumOfAnswer(1l);
						saveDetailQuestion(answerListeningDTO, row, categoryName, testId, 4, 0, 7, 10, 13, 16, 3, 20, 21, 22, 0, 23, "PART2");
					}
					//khong co blank thi la phan bai nghe toeic
					answerListeningDTO.setParentId(categoryIdForPart2);
					answerListeningDTO.setName("bai nghe toeic part 2");
					answerListeningDTO.setAnswer(row.getCell(5).getStringCellValue());
					answerListeningDTO.setNumOfAnswer(3l);
					answerListeningDTO.setAnswersToChoose(answersToChose(3));
					saveDetailQuestion(answerListeningDTO, row, categoryName, testId, 4, 0, 7, 10, 13, 16, 3, 20, 21, 22, 0, 23, "PART2");
				}
			}
			workbook.close();

		} catch (
				NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (
				Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}


	@Override
	@Transactional
	public String importDataPart3(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			DataFormatter formatter = new DataFormatter();
			int count = 0;

			for (Row row : sheet) {
				count++;
				if (count > 3) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(43).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(2l);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(42).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					String blankValue = row.getCell(41).getStringCellValue();
					Long topicIdForPart3 = 0l;
					Long topicIdForPart3_1 = 0l;
					if (StringUtils.isNotBlank(blankValue)) {
						topic.setPartTopicCode("PART3.1");
						topicIdForPart3_1 = checkExistTopic(topic, "PART3.1", "LISTENING_FILL_UNIT");
					}
					topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
					topicIdForPart3 = checkExistTopic(topic, "PART3", "LISTENING_UNIT");

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = row.getCell(43).getStringCellValue();
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(resutlLevelCode);
					String sentenceNo = row.getCell(3).getStringCellValue();
					categoryDTO.setArraySentenceNo(sentenceNo);

					Long categoryIdForPart3_1 = 0l;
					Long categoryIdForPart3 = 0l;
					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestDTO.setPathFile(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + env.getProperty("fulltest") + "/" + categoryName + "-" + testName + "-" + env.getProperty("fulltestMp3"));
					categoryTestDTO.setTestId(testId);

					categoryDTO.setPathFile1(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/PART3/" + row.getCell(4).getStringCellValue());
					String pathImg = row.getCell(38).getStringCellValue();
					categoryDTO.setPathFile2(StringUtils.isNotBlank(pathImg) ? env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/" + pathImg : null);
					String trans = StringUtils.isNotBlank(row.getCell(40).getStringCellValue()) ? row.getCell(40).getStringCellValue() + "\n|\n" + row.getCell(39).getStringCellValue() : row.getCell(39).getStringCellValue() + "\n|\n";
					categoryDTO.setTranscript(trans);//thieu data
					if (StringUtils.isNotBlank(blankValue)) {
						categoryDTO.setPartCode("PART3.1");
						categoryDTO.setTypeCode(5l);
						categoryIdForPart3_1 = checkExistCategory(categoryDTO, topicIdForPart3_1, testName);
						categoryTestDTO.setPart("PART3.1");
						categoryTestDTO.setCategoryId(categoryIdForPart3_1);
						saveCategoryTest(categoryTestDTO);
					}
					categoryDTO.setPartCode("PART3");
					categoryDTO.setTypeCode(2l);
					categoryIdForPart3 = checkExistCategory(categoryDTO, topicIdForPart3, testName);
					categoryTestDTO.setPart("PART3");
					categoryTestDTO.setCategoryId(categoryIdForPart3);
					saveCategoryTest(categoryTestDTO);

					// Lưu thông tin chi tiết câu hỏi
					String[] arrResult = splitCharacter(row.getCell(3).getStringCellValue(), "-");
					int start = 0;
					int firstArr = Integer.parseInt(arrResult[0]);
					int location = 0;
					int endArr = Integer.parseInt(arrResult[1]);
					for (int i = firstArr; i <= endArr; i++) {
						QuestionAnswerListeningDTO answerListeningDTO = new QuestionAnswerListeningDTO();
						if (start > 0) {
							location = start * 10;
						}

						if (StringUtils.isNotBlank(blankValue)) {
							answerListeningDTO.setParentId(categoryIdForPart3_1);
							//cat chuoi blank cho phan bai nghe dien tu
							answerListeningDTO.setName(blankValue);
							String dataForPart1_1 = splitAnswer(blankValue, "");
							boolean checkNotAnswer = false;
							if (StringUtils.isBlank(dataForPart1_1)) {
								checkNotAnswer = true;
							}
							if (!checkNotAnswer) {
								answerListeningDTO.setAnswer(dataForPart1_1);
								answerListeningDTO.setAnswersToChoose("NULL |");
								answerListeningDTO.setNumOfAnswer(1l);
								answerListeningDTO.setStatus(1l);
								answerListeningDTO.setSentenceNo(Long.valueOf(i));
								if (categoryName.toUpperCase().contains("ETS")) {
									answerListeningDTO.setTestId(testId);
								}
								questionAnswerListeningService.insertData(answerListeningDTO);
							}
						}
						//khong co blank thi la phan bai nghe toeic
						answerListeningDTO.setParentId(categoryIdForPart3);
						answerListeningDTO.setName(row.getCell(8 + location).getStringCellValue());
						if (StringUtils.isBlank(row.getCell(5 + start).getStringCellValue())) {
							log.error("Hya la loi o day   tim lam gi" + categoryName + "_" + sentenceNo);
						}
						answerListeningDTO.setAnswer(row.getCell(5 + start).getStringCellValue());
						answerListeningDTO.setNumOfAnswer(4l);
						String answerToChooseA = convertValue(row.getCell(10 + location));
						String answerToChooseB = convertValue(row.getCell(12 + location));
						String answerToChooseC = convertValue(row.getCell(14 + location));
						String answerToChooseD = convertValue(row.getCell(16 + location));
						answerListeningDTO.setAnswersToChoose(answerToChooseA + "|" + answerToChooseB + "|" + answerToChooseC + "|" + answerToChooseD + "|");
						answerListeningDTO.setTranslatingQuestion(StringUtils.isNotBlank(row.getCell(9 + location).getStringCellValue()) ? row.getCell(9 + location).getStringCellValue() : env.getProperty("no_trans"));
						answerListeningDTO.setTranslatingQuesA(row.getCell(11 + location).getStringCellValue());
						answerListeningDTO.setTranslatingQuesB(row.getCell(13 + location).getStringCellValue());
						answerListeningDTO.setTranslatingQuesC(row.getCell(15 + location).getStringCellValue());
						answerListeningDTO.setTranslatingQuesD(row.getCell(17 + location).getStringCellValue());
						answerListeningDTO.setPathFileQues(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/PART3/" + row.getCell(44 + start).getStringCellValue());

						answerListeningDTO.setStatus(1l);
						answerListeningDTO.setSentenceNo(Long.valueOf(i));
						if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
							answerListeningDTO.setTestId(testId);
						}
						questionAnswerListeningService.insertData(answerListeningDTO);
						start++;
					}
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}

	@Override
	@Transactional
	public String importDataPart4(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			DataFormatter formatter = new DataFormatter();
			int count = 0;

			for (Row row : sheet) {
				count++;
				if (count > 3) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(43).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(42).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					String blankValue = row.getCell(41).getStringCellValue();
					Long topicIdForPart4 = 0l;
					Long topicIdForPart4_1 = 0l;

					if (StringUtils.isNotBlank(blankValue)) {
						topic.setPartTopicCode("PART4.1");
						topicIdForPart4_1 = checkExistTopic(topic, "PART4.1", "LISTENING_FILL_UNIT");
					}
					topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
					topicIdForPart4 = checkExistTopic(topic, "PART4", "LISTENING_UNIT");

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = row.getCell(43).getStringCellValue();
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(resutlLevelCode);
					String sentenceNo = row.getCell(3).getStringCellValue();
					categoryDTO.setArraySentenceNo(sentenceNo);

					Long categoryIdForPart4_1 = 0l;
					Long categoryIdForPart4 = 0l;
					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setTestId(testId);
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestDTO.setPathFile(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/" + env.getProperty("fulltest") + "/" + categoryName + "-" + testName + "-" + env.getProperty("fulltestMp3"));
					categoryDTO.setPathFile1(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/PART4/" + row.getCell(4).getStringCellValue());
					String pathImg = row.getCell(38).getStringCellValue();
					categoryDTO.setPathFile2(StringUtils.isNotBlank(pathImg) ? env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/" + pathImg : null);
					String trans = StringUtils.isNotBlank(row.getCell(40).getStringCellValue()) ? row.getCell(40).getStringCellValue() + "\n|\n" + row.getCell(39).getStringCellValue() : row.getCell(39).getStringCellValue() + "\n|\n";
					categoryDTO.setTranscript(trans);//thieu data

					if (StringUtils.isNotBlank(blankValue)) {
						categoryDTO.setPartCode("PART4.1");
						categoryDTO.setTypeCode(5l);
						categoryIdForPart4_1 = checkExistCategory(categoryDTO, topicIdForPart4_1, testName);
						categoryTestDTO.setPart("PART4.1");
						categoryTestDTO.setCategoryId(categoryIdForPart4_1);
						saveCategoryTest(categoryTestDTO);
					}
					categoryDTO.setPartCode("PART4");
					categoryDTO.setTypeCode(2l);
					categoryIdForPart4 = checkExistCategory(categoryDTO, topicIdForPart4, testName);
					categoryTestDTO.setPart("PART4");
					categoryTestDTO.setCategoryId(categoryIdForPart4);
					saveCategoryTest(categoryTestDTO);

					// Lưu thông tin chi tiết câu hỏi
					String[] arrResult = splitCharacter(row.getCell(3).getStringCellValue(), "-");
					int start = 0;
					int firstArr = Integer.parseInt(arrResult[0]);
					int endArr = Integer.parseInt(arrResult[1]);
					int location = 0;
					for (int i = firstArr; i <= endArr; i++) {
						QuestionAnswerListeningDTO answerListeningDTO = new QuestionAnswerListeningDTO();
						if (start > 0) {
							location = start * 10;
						}

						if (StringUtils.isNotBlank(blankValue)) {
							answerListeningDTO.setParentId(categoryIdForPart4_1);
							//cat chuoi blank cho phan bai nghe dien tu
							answerListeningDTO.setName(blankValue);
							String dataForPart1_1 = splitAnswer(blankValue, "");
							answerListeningDTO.setAnswer(dataForPart1_1);
							answerListeningDTO.setAnswersToChoose("NULL |");
							answerListeningDTO.setNumOfAnswer(1l);
							if (categoryName.toUpperCase().contains("ETS")) {
								answerListeningDTO.setTestId(testId);
							}
							answerListeningDTO.setStatus(1l);
							answerListeningDTO.setSentenceNo(Long.valueOf(i));
							questionAnswerListeningService.insertData(answerListeningDTO);
						}
						//khong co blank thi la phan bai nghe toeic
						answerListeningDTO.setParentId(categoryIdForPart4);
						answerListeningDTO.setNumOfAnswer(4l);
						answerListeningDTO.setName(row.getCell(8 + location).getStringCellValue());
						answerListeningDTO.setAnswer(row.getCell(5 + start).getStringCellValue());
						String answerToChooseA = convertValue(row.getCell(10 + location));
						String answerToChooseB = convertValue(row.getCell(12 + location));
						String answerToChooseC = convertValue(row.getCell(14 + location));
						String answerToChooseD = convertValue(row.getCell(16 + location));
						answerListeningDTO.setAnswersToChoose(answerToChooseA + "|" + answerToChooseB + "|" + answerToChooseC + "|" + answerToChooseD + "|");
						answerListeningDTO.setTranslatingQuestion(StringUtils.isNotBlank(row.getCell(9 + location).getStringCellValue()) ? row.getCell(9 + location).getStringCellValue() : env.getProperty("no_trans"));
						answerListeningDTO.setTranslatingQuesA(row.getCell(11 + location).getStringCellValue());
						answerListeningDTO.setTranslatingQuesB(row.getCell(13 + location).getStringCellValue());
						answerListeningDTO.setTranslatingQuesC(row.getCell(15 + location).getStringCellValue());
						answerListeningDTO.setTranslatingQuesD(row.getCell(17 + location).getStringCellValue());
						answerListeningDTO.setPathFileQues(env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("audio") + "/PART4/" + row.getCell(44 + start).getStringCellValue());
						if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
							answerListeningDTO.setTestId(testId);
						}
						answerListeningDTO.setStatus(1l);
						answerListeningDTO.setSentenceNo(Long.valueOf(i));
						questionAnswerListeningService.insertData(answerListeningDTO);
						start++;
					}
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}


	@Override
	@Transactional
	public String importDataPart5(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			DataFormatter formatter = new DataFormatter();
			int count = 0;

			for (Row row : sheet) {
				count++;
				if (count > 2) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(13).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(12).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
					List<TopicDTO> lstTopic = topicService.getListTopicByName(topic);
					String description = row.getCell(5).getStringCellValue();
					TopicDTO topicDTO = checkExist.get(topicName + "_" + topic.getPartTopicCode());
					Long topicId;
					if (lstTopic.size() == 0 && null == topicDTO) {
						topic = new TopicDTO();
						topic.setTopicName(topicName);
						topic.setStatus(1l);
						topic.setCreatedTime(new Date());
						topic.setCode("TOPIC_" + new Date().getTime());
						//kiem tra topic thuoc bai nghe dien tu hay bai nghe toeic
						topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
						topic.setTypeTopicCode("READING_UNIT");
						topic.setLastUpdate(new Date());
						topicId = topicService.insert(topic);
						topic.setTopicId(topicId);
						checkExist.put(topicName + "_" + topic.getPartTopicCode(), topic);
					} else {
						if (lstTopic.size() == 0) {
							topicId = topicDTO.getTopicId();
						} else {
							topicId = lstTopic.get(0).getTopicId();
						}
					}

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = convertValue(row.getCell(13));
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(StringUtils.isNotBlank(resutlLevelCode) ? resutlLevelCode.trim().toUpperCase() : null);
					List<CategoryDTO> lst = categoryService.getListCategoryByName(categoryDTO);
					CategoryDTO categoryDTOtmp = checkExistCategory.get(categoryName + "_" + levelCode);
					Long categoryId;
					if (lst.size() == 0 && null == categoryDTOtmp) {
						categoryDTO.setTypeCode(1l);//Bai doc la 1
						categoryDTO.setArraySentenceNo(row.getCell(3).getStringCellValue());
						categoryDTO.setParentId(topicId);
						categoryDTO.setStatus(1l);
						categoryDTO.setCreatedDate(new Date());
						categoryId = categoryService.insertData(categoryDTO);
					} else {
						if (lst.size() == 0) {
							categoryId = categoryDTOtmp.getCategoryId();
						} else {
							categoryId = lst.get(0).getCategoryId();
						}
					}

					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setCategoryId(categoryId);
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setPart("PART5");
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestDTO.setTestId(testId);
					categoryTestRepository.insert(categoryTestDTO.toModel());

					// Lưu thông tin chi tiết câu hỏi
					QuestionAnswersReadingDTO answersReadingDTO = new QuestionAnswersReadingDTO();
					answersReadingDTO.setParentId(categoryId);

					answersReadingDTO.setName(row.getCell(4).getStringCellValue());
					answersReadingDTO.setAnswer(row.getCell(7).getStringCellValue());
					answersReadingDTO.setAnswersToChoose(mergeAnswerPart5(row));
					answersReadingDTO.setNumOfAnswer(4l);
					answersReadingDTO.setStatus(1l);
					answersReadingDTO.setDescription(StringUtils.isNotBlank(description) ? description : null);
					answersReadingDTO.setTranslatingQuestion(StringUtils.isNotBlank(row.getCell(6).getStringCellValue()) ? row.getCell(6).getStringCellValue() : env.getProperty("no_trans"));
					answersReadingDTO.setSentenceNo(StringUtils.isNotBlank(row.getCell(3).getStringCellValue()) ? Long.parseLong(row.getCell(3).getStringCellValue()) : null);
					if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
						answersReadingDTO.setTestId(testId);
					}
					questionAnswerReadingService.insertData(answersReadingDTO);
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}

	@Override
	@Transactional
	public String importDataPart6(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			int count = 0;
			for (Row row : sheet) {
				count++;
				if (count > 3) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(47).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(46).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
					List<TopicDTO> lstTopic = topicService.getListTopicByName(topic);
					TopicDTO topicDTO = checkExist.get(topicName + "_" + topic.getPartTopicCode());
					Long topicId;
					if (lstTopic.size() == 0 && null == topicDTO) {
						topic = new TopicDTO();
						topic.setTopicName(topicName);
						topic.setStatus(1l);
						topic.setCreatedTime(new Date());
						topic.setCode("TOPIC_" + new Date().getTime());
						//kiem tra topic thuoc bai nghe dien tu hay bai nghe toeic
						topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
						topic.setTypeTopicCode("READING_UNIT");
						topic.setLastUpdate(new Date());
						topicId = topicService.insert(topic);
						topic.setTopicId(topicId);
						checkExist.put(topicName + "_" + topic.getPartTopicCode(), topic);
					} else {
						if (lstTopic.size() == 0) {
							topicId = topicDTO.getTopicId();
						} else {
							topicId = lstTopic.get(0).getTopicId();
						}
					}

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = convertValue(row.getCell(47));
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(StringUtils.isNotBlank(resutlLevelCode) ? resutlLevelCode.trim().toUpperCase() : null);
					List<CategoryDTO> lst = categoryService.getListCategoryByName(categoryDTO);
					CategoryDTO categoryDTOtmp = checkExistCategory.get(categoryName + "_" + levelCode);
					Long categoryId;
					if (lst.size() == 0 && null == categoryDTOtmp) {
						categoryDTO.setTypeCode(1l);//Bai doc la 1
						categoryDTO.setParentId(topicId);
						categoryDTO.setTypeFile1("TEXT");
						categoryDTO.setArraySentenceNo(row.getCell(3).getStringCellValue());
						categoryDTO.setPathFile1(StringUtils.isNotBlank(row.getCell(5).getStringCellValue()) ? row.getCell(5).getStringCellValue() : "");
						categoryDTO.setStatus(1l);
						categoryDTO.setCreatedDate(new Date());
						categoryId = categoryService.insertData(categoryDTO);
					} else {
						if (lst.size() == 0) {
							categoryId = categoryDTOtmp.getCategoryId();
						} else {
							categoryId = lst.get(0).getCategoryId();
						}
					}

					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setCategoryId(categoryId);
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setPart("PART6");
					categoryTestDTO.setTestId(testId);
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestRepository.insert(categoryTestDTO.toModel());

					// Lưu thông tin chi tiết câu hỏi
					String[] arrResult = splitCharacter(row.getCell(3).getStringCellValue(), "-");
					int start = 0;
					int firstArr = Integer.parseInt(arrResult[0]);
					int endArr = Integer.parseInt(arrResult[1]);
					int location = 0;
					for (int i = firstArr; i <= endArr; i++) {
						QuestionAnswersReadingDTO answersReadingDTO = new QuestionAnswersReadingDTO();
						answersReadingDTO.setParentId(categoryId);
						if (start > 0) {
							location = start * 9;
						}
						//khong co blank thi la phan bai nghe toeic
						answersReadingDTO.setName("(" + i + ")");
						answersReadingDTO.setAnswer(row.getCell(6 + start).getStringCellValue());
						answersReadingDTO.setNumOfAnswer(4l);
						answersReadingDTO.setStatus(1l);

						String answerToChooseA = convertValue(row.getCell(10 + location));
						String answerToChooseB = convertValue(row.getCell(12 + location));
						String answerToChooseC = convertValue(row.getCell(14 + location));
						String answerToChooseD = convertValue(row.getCell(16 + location));
						answersReadingDTO.setAnswersToChoose(answerToChooseA + "|" + answerToChooseB + "|" + answerToChooseC + "|" + answerToChooseD + "|");
						answersReadingDTO.setTranslatingQuesA(StringUtils.isNotBlank(row.getCell(11 + location).getStringCellValue()) ? row.getCell(11 + location).getStringCellValue() : "");
						answersReadingDTO.setTranslatingQuesB(StringUtils.isNotBlank(row.getCell(13 + location).getStringCellValue()) ? row.getCell(13 + location).getStringCellValue() : "");
						answersReadingDTO.setTranslatingQuesC(StringUtils.isNotBlank(row.getCell(15 + location).getStringCellValue()) ? row.getCell(15 + location).getStringCellValue() : "");
						answersReadingDTO.setTranslatingQuesD(StringUtils.isNotBlank(row.getCell(17 + location).getStringCellValue()) ? row.getCell(17 + location).getStringCellValue() : "");
						answersReadingDTO.setDescription(StringUtils.isNotBlank(convertValue(row.getCell(18 + location))) ? convertValue(row.getCell(18 + location)) : "");
						answersReadingDTO.setSentenceNo(Long.valueOf(i));
						questionAnswerReadingService.insertData(answersReadingDTO);
						if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
							answersReadingDTO.setTestId(testId);
						}
						start++;
					}
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}

	@Override
	@Transactional
	public String importDataPart7(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			int count = 0;
			for (Row row : sheet) {
				count++;
				if (count > 3) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(62).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(61).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
					List<TopicDTO> lstTopic = topicService.getListTopicByName(topic);
					TopicDTO topicDTO = checkExist.get(topicName + "_" + topic.getPartTopicCode());
					Long topicId;
					if (lstTopic.size() == 0 && null == topicDTO) {
						topic = new TopicDTO();
						topic.setTopicName(topicName);
						topic.setStatus(1l);
						topic.setCreatedTime(new Date());
						topic.setCode("TOPIC_" + new Date().getTime());
						//kiem tra topic thuoc bai nghe dien tu hay bai nghe toeic
						topic.setPartTopicCode(row.getCell(2).getStringCellValue().toUpperCase());
						topic.setTypeTopicCode("READING_UNIT");
						topic.setLastUpdate(new Date());
						topicId = topicService.insert(topic);
						topic.setTopicId(topicId);
						checkExist.put(topicName + "_" + topic.getPartTopicCode(), topic);
					} else {
						if (lstTopic.size() == 0) {
							topicId = topicDTO.getTopicId();
						} else {
							topicId = lstTopic.get(0).getTopicId();
						}
					}

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = convertValue(row.getCell(62));
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(StringUtils.isNotBlank(resutlLevelCode) ? resutlLevelCode.trim().toUpperCase() : null);
					List<CategoryDTO> lst = categoryService.getListCategoryByName(categoryDTO);
					CategoryDTO categoryDTOtmp = checkExistCategory.get(categoryName + "_" + levelCode);
					Long categoryId;
					if (lst.size() == 0 && null == categoryDTOtmp) {
						categoryDTO.setTypeCode(1l);//Bai doc la 1
						categoryDTO.setParentId(topicId);
						categoryDTO.setTypeFile1("FILE");
						categoryDTO.setArraySentenceNo(row.getCell(3).getStringCellValue());
						categoryDTO.setPathFile1(StringUtils.isNotBlank(row.getCell(4).getStringCellValue()) ? env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/PART7/" + row.getCell(4).getStringCellValue() : "");
						categoryDTO.setStatus(1l);
						categoryDTO.setCreatedDate(new Date());
						categoryId = categoryService.insertData(categoryDTO);
					} else {
						if (lst.size() == 0) {
							categoryId = categoryDTOtmp.getCategoryId();
						} else {
							categoryId = lst.get(0).getCategoryId();
						}
					}

					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setCategoryId(categoryId);
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setPart("PART7");
					categoryTestDTO.setTestId(testId);
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestRepository.insert(categoryTestDTO.toModel());

					// Lưu thông tin chi tiết câu hỏi
					String[] arrResult = splitCharacter(row.getCell(3).getStringCellValue(), "-");
					int start = 0;
					int firstArr = Integer.parseInt(arrResult[0]);
					int endArr = Integer.parseInt(arrResult[1]);
					int location = 0;
					for (int i = firstArr; i <= endArr; i++) {
						QuestionAnswersReadingDTO answersReadingDTO = new QuestionAnswersReadingDTO();
						answersReadingDTO.setParentId(categoryId);
						if (start > 0) {
							location = start * 10;
						}
						//khong co blank thi la phan bai nghe toeic
						answersReadingDTO.setName(convertValue(row.getCell(11 + location)));
						answersReadingDTO.setAnswer(row.getCell(6 + start).getStringCellValue());
						answersReadingDTO.setNumOfAnswer(4l);
						answersReadingDTO.setStatus(1l);

						String answerToChooseA = convertValue(row.getCell(13 + location));
						String answerToChooseB = convertValue(row.getCell(15 + location));
						String answerToChooseC = convertValue(row.getCell(17 + location));
						String answerToChooseD = convertValue(row.getCell(19 + location));
						answersReadingDTO.setAnswersToChoose(answerToChooseA + "|" + answerToChooseB + "|" + answerToChooseC + "|" + answerToChooseD + "|");
						answersReadingDTO.setTranslatingQuestion(StringUtils.isNotBlank(row.getCell(12 + location).getStringCellValue()) ? row.getCell(12 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesA(StringUtils.isNotBlank(row.getCell(14 + location).getStringCellValue()) ? row.getCell(14 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesB(StringUtils.isNotBlank(row.getCell(16 + location).getStringCellValue()) ? row.getCell(16 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesC(StringUtils.isNotBlank(row.getCell(18 + location).getStringCellValue()) ? row.getCell(18 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesD(StringUtils.isNotBlank(row.getCell(20 + location).getStringCellValue()) ? row.getCell(20 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setSentenceNo(Long.valueOf(i));
						questionAnswerReadingService.insertData(answersReadingDTO);
						if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
							answersReadingDTO.setTestId(testId);
						}
						start++;
					}
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}

	@Override
	@Transactional
	public String importDataPart8(String fileInput) {
		try {

			FileInputStream excelFile = new FileInputStream(new File(fileInput));

			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			checkExist = new HashMap<>();
			checkExistCategory = new HashMap<>();

			int count = 0;
			for (Row row : sheet) {
				count++;
				if (count > 3) {
					// bóc tách data ở đây
					//kiểm tra bài test đã ton tai hay chua. không sưa dôi neu ton tai
					TestDTO testDTO = new TestDTO();
					String testName = row.getCell(1).getStringCellValue();
					testDTO.setName(testName);
					testDTO = testService.findDetailByName(testDTO);
					Long testId;
					if (null == testDTO) {
						testDTO = new TestDTO();
						testDTO.setName(row.getCell(1).getStringCellValue());
						testDTO.setType(1l);
						String rankCode = row.getCell(63).getStringCellValue();
						switch (rankCode.trim().toUpperCase()) {
							case Constants.LEVEL_CODE_VN.EASY:
								testDTO.setRankCode(1l);
								break;
							case Constants.LEVEL_CODE_VN.MEDIUM:
								testDTO.setRankCode(2l);
								break;
							case Constants.LEVEL_CODE_VN.DIFFICULT:
								testDTO.setRankCode(3l);
								break;
							default:
								testDTO.setRankCode(null);
								break;
						}
						testDTO.setTiming(7200l);//set 120 min lam bai
						testDTO.setStatus(1l);
						testDTO.setCreate_time(new Date());
						testDTO.setPathFile1("");
						testDTO.setTypeFile1("LINK");
						testId = testService.insertTest(testDTO);
					} else {
						testId = testDTO.getId();
					}

					// kiểm tra topicName đã tồn tại trong db hay chưa?
					String topicName = row.getCell(62).getStringCellValue();
					TopicDTO topic = new TopicDTO();
					topic.setTopicName(topicName);
					String partCode = "PART8";
					topic.setPartTopicCode(partCode);
					List<TopicDTO> lstTopic = topicService.getListTopicByName(topic);
					TopicDTO topicDTO = checkExist.get(topicName + "_" + topic.getPartTopicCode());
					Long topicId;
					if (lstTopic.size() == 0 && null == topicDTO) {
						topic = new TopicDTO();
						topic.setTopicName(topicName);
						topic.setStatus(1l);
						topic.setCreatedTime(new Date());
						topic.setCode("TOPIC_" + new Date().getTime());
						//kiem tra topic thuoc bai nghe dien tu hay bai nghe toeic
						topic.setPartTopicCode("PART8");
						topic.setTypeTopicCode("READING_UNIT");
						topic.setLastUpdate(new Date());
						topicId = topicService.insert(topic);
						topic.setTopicId(topicId);
						checkExist.put(topicName + "_" + topic.getPartTopicCode(), topic);
					} else {
						if (lstTopic.size() == 0) {
							topicId = topicDTO.getTopicId();
						} else {
							topicId = lstTopic.get(0).getTopicId();
						}
					}

					// Gán data vào bảng category con
					CategoryDTO categoryDTO = new CategoryDTO();
					String categoryName = row.getCell(0).getStringCellValue();
					categoryDTO.setNameCategory(categoryName);
					String levelCode = convertValue(row.getCell(63));
					String resutlLevelCode = convertLevelCode(levelCode);
					categoryDTO.setLevelCode(StringUtils.isNotBlank(resutlLevelCode) ? resutlLevelCode.trim().toUpperCase() : null);
					List<CategoryDTO> lst = categoryService.getListCategoryByName(categoryDTO);
					CategoryDTO categoryDTOtmp = checkExistCategory.get(categoryName + "_" + levelCode);
					Long categoryId;
					if (lst.size() == 0 && null == categoryDTOtmp) {
						categoryDTO.setTypeCode(1l);//Bai doc la 1
						categoryDTO.setParentId(topicId);
						categoryDTO.setTypeFile1("FILE");
						categoryDTO.setArraySentenceNo(row.getCell(3).getStringCellValue());
						categoryDTO.setPathFile1(StringUtils.isNotBlank(row.getCell(4).getStringCellValue()) ? env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/PART7/" + row.getCell(4).getStringCellValue() : "");
						categoryDTO.setTypeFile2("FILE");
						categoryDTO.setPathFile2(StringUtils.isNotBlank(row.getCell(5).getStringCellValue()) ? env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/PART7/" + row.getCell(5).getStringCellValue() : "");
						categoryDTO.setTypeFile3("FILE");
						categoryDTO.setPathFile3(StringUtils.isNotBlank(row.getCell(6).getStringCellValue()) ? env.getProperty("addressUploadAmazons3") + "/" + env.getProperty("image") + "/PART7/" + row.getCell(6).getStringCellValue() : "");
						categoryDTO.setStatus(1l);
						categoryDTO.setCreatedDate(new Date());
						categoryId = categoryService.insertData(categoryDTO);
					} else {
						if (lst.size() == 0) {
							categoryId = categoryDTOtmp.getCategoryId();
						} else {
							categoryId = lst.get(0).getCategoryId();
						}
					}

					CategoryTestDTO categoryTestDTO = new CategoryTestDTO();
					categoryTestDTO.setCategoryId(categoryId);
					categoryTestDTO.setStatus(1l);
					categoryTestDTO.setPart("PART8");
					categoryTestDTO.setCategoryName(categoryName);
					categoryTestDTO.setTestId(testId);
					categoryTestRepository.insert(categoryTestDTO.toModel());

					// Lưu thông tin chi tiết câu hỏi
					String[] arrResult = splitCharacter(row.getCell(3).getStringCellValue(), "-");
					int start = 0;
					int firstArr = Integer.parseInt(arrResult[0]);
					int endArr = Integer.parseInt(arrResult[1]);
					int location = 0;
					for (int i = firstArr; i <= endArr; i++) {
						QuestionAnswersReadingDTO answersReadingDTO = new QuestionAnswersReadingDTO();
						answersReadingDTO.setParentId(categoryId);
						if (start > 0) {
							location = start * 10;
						}
						//khong co blank thi la phan bai nghe toeic
						answersReadingDTO.setName(convertValue(row.getCell(12 + location)));
						answersReadingDTO.setAnswer(row.getCell(7 + start).getStringCellValue());
						answersReadingDTO.setNumOfAnswer(4l);
						answersReadingDTO.setStatus(1l);

						String answerToChooseA = convertValue(row.getCell(14 + location));
						String answerToChooseB = convertValue(row.getCell(16 + location));
						String answerToChooseC = convertValue(row.getCell(18 + location));
						String answerToChooseD = convertValue(row.getCell(20 + location));
						answersReadingDTO.setAnswersToChoose(answerToChooseA + "|" + answerToChooseB + "|" + answerToChooseC + "|" + answerToChooseD + "|");
						answersReadingDTO.setTranslatingQuestion(StringUtils.isNotBlank(row.getCell(13 + location).getStringCellValue()) ? row.getCell(13 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesA(StringUtils.isNotBlank(row.getCell(15 + location).getStringCellValue()) ? row.getCell(15 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesB(StringUtils.isNotBlank(row.getCell(17 + location).getStringCellValue()) ? row.getCell(17 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesC(StringUtils.isNotBlank(row.getCell(19 + location).getStringCellValue()) ? row.getCell(19 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setTranslatingQuesD(StringUtils.isNotBlank(row.getCell(21 + location).getStringCellValue()) ? row.getCell(21 + location).getStringCellValue() : env.getProperty("no_trans"));
						answersReadingDTO.setSentenceNo(Long.valueOf(i));
						questionAnswerReadingService.insertData(answersReadingDTO);
						if (!(categoryName.toUpperCase().contains("LC") && categoryName.toUpperCase().contains("OLD"))) {
							answersReadingDTO.setTestId(testId);
						}
						start++;
					}
				}
			}
			workbook.close();

		} catch (NullPointerException pointerException) {
			// pointerException.printStackTrace();
			log.error(pointerException.getMessage(), pointerException);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return "Success";
	}

	public Map<String, String> convertDescription(Row row, String part) {
		Map<String, String> resultData = new HashMap<>();
		// \n
		String awA, awB, awC, awD, result1, result2;
		if ("PART1".equalsIgnoreCase(part)) {
			awA = row.getCell(7).getStringCellValue();
			awB = row.getCell(10).getStringCellValue();
			awC = row.getCell(13).getStringCellValue();
			awD = row.getCell(16).getStringCellValue();
			result1 = awA + "\n" + awB + "\n" + awC + "\n" + awD;
			String awATrans = awA + " = " + row.getCell(8).getStringCellValue();
			String awBTrans = awB + " = " + row.getCell(11).getStringCellValue();
			String awCTrans = awC + " = " + row.getCell(14).getStringCellValue();
			String awDTrans = awD + " = " + row.getCell(17).getStringCellValue();
			result2 = awATrans + "\n" + awBTrans + "\n" + awCTrans + "\n" + awDTrans;
		} else {
			awA = row.getCell(9).getStringCellValue();
			awB = row.getCell(12).getStringCellValue();
			awC = row.getCell(15).getStringCellValue();
			result1 = awA + "\n" + awB + "\n" + awC;
			String awATrans = awA + " = " + row.getCell(10).getStringCellValue();
			String awBTrans = awB + " = " + row.getCell(13).getStringCellValue();
			String awCTrans = awC + " = " + row.getCell(16).getStringCellValue();
			result2 = awATrans + "\n" + awBTrans + "\n" + awCTrans;
		}

		resultData.put("AnswerNotTrans", result1);
		resultData.put("AnswerWithTrans", result2);
		return resultData;
	}

	public String convertTranscriptPart2(Row row) {
		// \n
		String result1;
		String awA = row.getCell(8).getStringCellValue();
		String awB = row.getCell(10).getStringCellValue();
		String awC = row.getCell(13).getStringCellValue();
		String awD = row.getCell(16).getStringCellValue();
		result1 = awA + "\n" + awB + "\n" + awC + "\n" + awD;
		return result1;
	}

	public String mergeAnswerPart5(Row row) {
		// \n
		String result1;
		String awA = row.getCell(8).getStringCellValue();
		String awB = row.getCell(9).getStringCellValue();
		String awC = row.getCell(10).getStringCellValue();
		String awD = row.getCell(11).getStringCellValue();
		result1 = awA + "|" + awB + "|" + awC + "|" + awD + "|" ;
		return result1;
	}

	public static String splitAnswer(String data, String dataEnd) {
		String result = "";
		String getIndexValue;
		if (data.contains("<")) {
			getIndexValue = data.substring(data.indexOf("<") + 1, data.indexOf(">"));
			if (StringUtils.isBlank(dataEnd)) {
				dataEnd = getIndexValue + "|";
			} else {
				dataEnd = dataEnd + getIndexValue + "|";
			}

			result = data.replaceFirst("<" + getIndexValue + ">", "");
		}
		if (result.contains("<")) {
			dataEnd = splitAnswer(result, dataEnd);
		}
		return dataEnd;
	}

	public String[] splitCharacter(String arr, String character) {
		String[] arrResult = arr.split(character);
		return arrResult;
	}

	public String convertLevelCode(String levelCode) {
		String result = "";
		switch (levelCode) {
			case Constants.LEVEL_CODE_VN.EASY:
				result = Constants.LEVEL_CODE.EASY;
				break;
			case Constants.LEVEL_CODE_VN.MEDIUM:
				result = Constants.LEVEL_CODE.MEDIUM;
				break;
			case Constants.LEVEL_CODE_VN.DIFFICULT:
				result = Constants.LEVEL_CODE.DIFFICULT;
				break;
			default:
				result = null;
				break;
		}
		return result;
	}

	public String answersToChose(int number) {
		String result;
		switch (number) {
			case 2:
				result = "A|B|";
				break;
			case 3:
				result = "A|B|C|";
				break;
			case 4:
				result = "A|B|C|D|";
				break;
			case 5:
				result = "A|B|C|D|E|";
				break;
			case 6:
				result = "A|B|C|D|E|F|";
				break;
			default:
				result = "A|";
				break;

		}
		return result;
	}

	public String mergeQuestion(String textA, String textB, String textC, String textD, int numberQuestion) {
		String result;
		switch (numberQuestion) {
			case 1:
				result = textA;
				break;
			case 2:
				result = textA + "\n" + textB;
				break;
			case 3:
				result = textA + "\n" + textB + "\n" + textC;
				break;
			case 4:
				result = textA + "\n" + textB + "\n" + textC + "\n" + textD;
				break;
			default:
				result = "";
				break;
		}
		return result;
	}

	public String convertValue(Cell cell) {
		String result;
		switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				result = String.valueOf(cell.getNumericCellValue());
				break;
			case STRING:
				result = cell.getStringCellValue();
				break;
			default:
				result = "";
				break;
		}
		return result;
	}

	public static void main(String[] args) {
		String a = "Is there a code to <unlock> the <door>, or is it <open>?\n" +
				"It should be <open>.\n" +
				"There's a <clock> in here.\n" +
				"Just around the <corner>.";
		System.out.println("gia trị ==> " + splitAnswer(a, ""));
	}
}
