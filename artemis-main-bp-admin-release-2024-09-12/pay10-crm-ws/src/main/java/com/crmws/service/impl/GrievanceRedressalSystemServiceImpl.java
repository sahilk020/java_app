package com.crmws.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.entity.ResponseMessage;
import com.crmws.entity.ResponseMessageForGRS;
import com.crmws.service.GrievanceRedressalSystemService;
import com.google.gson.Gson;
import com.pay10.commons.dto.GrievanceRedressalSystemDto;
import com.pay10.commons.dto.GrsIssueHistoryDto;
import com.pay10.commons.repository.GrievanceRedressalSystemRepository;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class GrievanceRedressalSystemServiceImpl implements GrievanceRedressalSystemService {

	private static final Logger logger = LoggerFactory.getLogger(GrievanceRedressalSystemServiceImpl.class.getName());

	@Autowired
	private GrievanceRedressalSystemRepository grievanceRedressalSystemRepository;
	String filePath = PropertiesManager.propertiesMap.get("GRS_PATH");
	//String filePath = "D://bilkPayout//";

	@Autowired
	private UserDao userDao;

	@Override
	public ResponseMessage uploadDoc(String grsId, String emailId, MultipartFile file) {

		ResponseMessage message = new ResponseMessage();
		String apppath = PropertiesManager.propertiesMap.get("GRS_PATH");

		logger.info("apppath :   " + apppath);
		String fileName = null;
		String uniqueNumber = TransactionManager.getNewTransactionId();
		Path path = Paths.get(apppath, grsId, uniqueNumber);
		try {
			Files.createDirectories(path);
			fileName = StringUtils.cleanPath(file.getOriginalFilename());
			Path targetLocation = path.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			Path filePath = Paths.get(path.toString(), fileName);
			logger.info("storeFile:: file stored successfully. GRSID={}, Filepath={}", grsId, filePath);
			grievanceRedressalSystemRepository.insert(uniqueNumber, grsId, emailId, filePath);

			message.setRespmessage("Successfully Uploaded");
			message.setHttpStatus(HttpStatus.OK);

		} catch (IOException e) {
			message.setRespmessage("Failed To Uploaded");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in uploadDoc() :", e);
			e.printStackTrace();

		}

		return message;
	}

	@Override
	public ResponseMessage saveGrievance(GrievanceRedressalSystemDto dto) {
		ResponseMessage message = new ResponseMessage();
		boolean flag = true;
		logger.info(new Gson().toJson(dto));
		logger.info("GRS_ID : " + dto.getGrievanceRedressalSystemId() + "\t GRS_DESC : "
				+ dto.getGrievanceRedressalSystemDescription() + "\t GRS_TITTLE : "
				+ dto.getGrievanceRedressalSystemTittle() + "\t PG_REF_NUM : "
				+ dto.getGrievanceRedressalSystemPgrefNumber() + "\t CREATED BY : " + dto.getUserEmailId());
		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemDescription())) {
				message.setRespmessage("Grievance Description is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemTittle())) {
				message.setRespmessage("Grievance Title is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}

			if (flag) {
				boolean flag1 = true;
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemDescription().trim())) {
					message.setRespmessage("Grievance Description is Mandatory");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemTittle().trim())) {
					message.setRespmessage("Grievance Title is Mandatory");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}
				if (flag1) {

					if (dto.getFile() != null && !dto.getFile().isEmpty()) {
						dto.setFilename(System.currentTimeMillis() + "-" + dto.getFilename());
						byte[] fileBytes = Base64.getDecoder().decode(dto.getFile());
						File uploadDir = new File(filePath); // Change this path as needed
						if (!uploadDir.exists()) {
							uploadDir.mkdirs();
						}
						File uploadFile = new File(uploadDir, dto.getFilename());
						try (FileOutputStream os = new FileOutputStream(uploadFile)) {
							os.write(fileBytes);
						} catch (IOException e) {
							message.setRespmessage("Failed to upload file");
							message.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
							logger.error("Error occurred while uploading file", e);
							return message;
						}

					}

					if (grievanceRedressalSystemRepository
							.checkGrievanceIsExist(dto.getGrievanceRedressalSystemPgrefNumber().trim())) {
						boolean status = grievanceRedressalSystemRepository.saveRedressal(dto);

						if (status) {
							GrsIssueHistoryDto dtoGrsIssueHistoryDto = new GrsIssueHistoryDto();

							User user = userDao.findByEmailId(dto.getUserEmailId());
							dtoGrsIssueHistoryDto.setCreatedBy(user.getBusinessName());
							dtoGrsIssueHistoryDto.setDescription(dto.getGrievanceRedressalSystemDescription());
							dtoGrsIssueHistoryDto.setFile(dto.getFile());
							dtoGrsIssueHistoryDto.setFilename(dto.getFilename());
							dtoGrsIssueHistoryDto.setGrsId(dto.getGrievanceRedressalSystemId().trim());
							grievanceRedressalSystemRepository.saveDesHistory(dtoGrsIssueHistoryDto);
							message.setRespmessage("Grievance Successfully Created");
							message.setHttpStatus(HttpStatus.CREATED);
						} else {
							message.setRespmessage("Failed To Create Grievance please Try After Sometime");
							message.setHttpStatus(HttpStatus.BAD_REQUEST);
						}

					} else {
						message.setRespmessage("Grievance is already Created");
						message.setHttpStatus(HttpStatus.BAD_REQUEST);
					}

				}
			}
		} catch (Exception e) {
			message.setRespmessage("Failed To Create");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in saveGrievance() :", e);
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public ResponseMessage saveGrievanceOther(GrievanceRedressalSystemDto dto) {
		ResponseMessage message = new ResponseMessage();
		boolean flag = true;
		String fileName = null;

		if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
			String timestamp = String.valueOf(new Date().getTime());
			String randomNum = String.valueOf((int) Math.floor(Math.random() * 900000) + 100000);
			String uniqueNumber = timestamp + randomNum;
			dto.setGrievanceRedressalSystemId(uniqueNumber);
		}

		logger.info("GRS_ID : " + dto.getGrievanceRedressalSystemId() + "\t GRS_DESC : "
				+ dto.getGrievanceRedressalSystemDescription() + "\t GRS_TITTLE : "
				+ dto.getGrievanceRedressalSystemTittle() + "\t CREATED BY : " + dto.getUserEmailId());

		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemDescription())) {
				message.setRespmessage("Grievance Description is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemTittle())) {
				message.setRespmessage("Grievance Title is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
				message.setRespmessage("GRS ID Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}

			if (flag) {
				boolean flag1 = true;
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemDescription().trim())) {
					message.setRespmessage("Grievance Description is Mandatory");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemTittle().trim())) {
					message.setRespmessage("Grievance Title is Mandatory");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}

				if (flag1) {

					if (dto.getFile() != null && !dto.getFile().isEmpty()) {
						dto.setFilename(System.currentTimeMillis() + "-" + dto.getFilename());
						byte[] fileBytes = Base64.getDecoder().decode(dto.getFile());
						File uploadDir = new File(filePath); // Change this path as needed
						if (!uploadDir.exists()) {
							uploadDir.mkdirs();
						}
						File uploadFile = new File(uploadDir, dto.getFilename());
						try (FileOutputStream os = new FileOutputStream(uploadFile)) {
							os.write(fileBytes);
						} catch (IOException e) {
							message.setRespmessage("Failed to upload file");
							message.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
							logger.error("Error occurred while uploading file", e);
							return message;
						}

					}

					if (grievanceRedressalSystemRepository
							.checkGrievanceIsExistThroughGRSID(dto.getGrievanceRedressalSystemId().trim())) {
						boolean status = grievanceRedressalSystemRepository.saveRedressalOther(dto);
						if (status) {

							GrsIssueHistoryDto dtoGrsIssueHistoryDto = new GrsIssueHistoryDto();

							User user = userDao.findByEmailId(dto.getUserEmailId());
							dtoGrsIssueHistoryDto.setCreatedBy(user.getBusinessName());
							dtoGrsIssueHistoryDto.setDescription(dto.getGrievanceRedressalSystemDescription());
							dtoGrsIssueHistoryDto.setFile(dto.getFile());
							dtoGrsIssueHistoryDto.setFilename(dto.getFilename());
							dtoGrsIssueHistoryDto.setGrsId(dto.getGrievanceRedressalSystemId().trim());
							grievanceRedressalSystemRepository.saveDesHistory(dtoGrsIssueHistoryDto);
							JSONObject gson = new JSONObject();
							gson.put("Message", "Successfully Created!");
							gson.put("GRS_ID", dto.getGrievanceRedressalSystemId());
							gson.put("FileName", fileName != null ? fileName : "No file uploaded");

							message.setRespmessage(gson.toString());
							message.setHttpStatus(HttpStatus.CREATED);
						} else {
							message.setRespmessage("Failed To Create Grievance please Try After Sometime");
							message.setHttpStatus(HttpStatus.BAD_REQUEST);
						}
					} else {
						message.setRespmessage("Grievance is already Created");
						message.setHttpStatus(HttpStatus.BAD_REQUEST);
					}
				}
			}
		} catch (Exception e) {
			message.setRespmessage("Failed To Create");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.error("Exception Occurred in saveGrievanceOther()", e);
		}

		return message;
	}

	@Override
	public ResponseMessage saveGrievanceWebsite(GrievanceRedressalSystemDto dto) {
		ResponseMessage message = new ResponseMessage();
		boolean flag = true;
		if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
			String timestamp = String.valueOf(new Date().getTime());

			// Generate a random number between 100000 and 999999 (6-digit number)
			String randomNum = String.valueOf((int) Math.floor(Math.random() * 900000) + 100000);

			// Concatenate the timestamp and random number to get an 18-digit unique number
			String uniqueNumber = timestamp + randomNum;
			dto.setGrievanceRedressalSystemId(uniqueNumber);
		}
		logger.info("GRS_ID : " + dto.getGrievanceRedressalSystemId() + "\t GRS_DESC : "
				+ dto.getGrievanceRedressalSystemDescription() + "\t GRS_TITTLE : "
				+ dto.getGrievanceRedressalSystemTittle() + "\t CREATED BY : " + dto.getUserEmailId());

		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemDescription())) {
				message.setRespmessage("Grievance Description is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemTittle())) {
				message.setRespmessage("Grievance Tittle is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
				message.setRespmessage("GRS ID Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getCustomerName())) {
				message.setRespmessage("Customer Name Is Mandatory");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}

			if (flag) {
				boolean flag1 = true;
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemDescription().trim())) {
					message.setRespmessage("Grievance Description is Mandatory");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}

				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemTittle().trim())) {
					message.setRespmessage("Grievance Tittle is Mandatory");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}
				if (flag1) {
					if (grievanceRedressalSystemRepository
							.checkGrievanceIsExistThroughGRSID(dto.getGrievanceRedressalSystemId().trim())) {
						boolean status = grievanceRedressalSystemRepository.saveRedressalOther(dto);
						if (status) {
							JSONObject gson = new JSONObject();
							gson.put("Message", "SuccessFully Created!");
							gson.put("GRS ID", dto.getGrievanceRedressalSystemId());

							message.setRespmessage(gson.toString());
							message.setHttpStatus(HttpStatus.CREATED);
						} else {
							message.setRespmessage("Failed To Create Grievance please Try After Sometime");
							message.setHttpStatus(HttpStatus.BAD_REQUEST);
						}

					} else {
						message.setRespmessage("Grievance is already Created");
						message.setHttpStatus(HttpStatus.BAD_REQUEST);
					}

				}
			}
		} catch (Exception e) {
			message.setRespmessage("Failed To Create");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in saveGrievanceOther() :", e);
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public ResponseMessage closeGrievance(GrievanceRedressalSystemDto dto) {
		ResponseMessage message = new ResponseMessage();
		logger.info("GRS_ID : " + dto.getGrievanceRedressalSystemId() + "\t GRS_DESC : " + "\t Updated BY : "
				+ dto.getUserEmailId());

		boolean flag = true;

		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
				message.setRespmessage("GRS ID is Invalid");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}

			if (flag) {
				boolean flag1 = true;
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId().trim())) {
					message.setRespmessage("GRS ID is Invalid");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}
				if (flag1) {
					boolean status = grievanceRedressalSystemRepository.closeGrievance(dto);
					if (status) {
						message.setRespmessage("Grievance Successfully Closed");
						message.setHttpStatus(HttpStatus.CREATED);
					} else {
						message.setRespmessage("Failed To Close Grievance please Try After Sometime");
						message.setHttpStatus(HttpStatus.BAD_REQUEST);
					}
				}
			}

		} catch (Exception e) {
			message.setRespmessage("Failed To Close Grievance");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in close Grievance() :", e);
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public ResponseMessage reOpenGrievance(GrievanceRedressalSystemDto dto) {
		ResponseMessage message = new ResponseMessage();
		logger.info("GRS_ID : " + dto.getGrievanceRedressalSystemId() + "\t GRS_DESC : " + "\t Updated BY : "
				+ dto.getUserEmailId());

		boolean flag = true;

		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
				message.setRespmessage("GRS ID is Invalid");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}

			if (flag) {
				boolean flag1 = true;
				if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId().trim())) {
					message.setRespmessage("GRS ID is Invalid");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
					flag1 = false;
				}
				if (flag1) {
					boolean status = grievanceRedressalSystemRepository.reOpenGrievance(dto);
					if (status) {
						message.setRespmessage("Grievance Successfully Reopened");
						message.setHttpStatus(HttpStatus.CREATED);
					} else {
						message.setRespmessage("Failed To Reopen Grievance please Try After Sometime");
						message.setHttpStatus(HttpStatus.BAD_REQUEST);
					}
				}
			}

		} catch (Exception e) {
			message.setRespmessage("Failed To Reopen Grievance");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in close Grievance() :", e);
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public ResponseMessageForGRS findAllGRS(String payId, String status, String dateFrom, String dateTo) {
		ResponseMessageForGRS forGRS = new ResponseMessageForGRS();
		List<String> statusList = Arrays.asList("ALL", "NEW", "REOPENED", "INPROGRESS", "CLOSED");
		try {
			if (org.apache.commons.lang3.StringUtils.isNotBlank(payId)) {
				if (org.apache.commons.lang3.StringUtils.isNotBlank(status)) {
					if (statusList.contains(status.toUpperCase())) {
						if (org.apache.commons.lang3.StringUtils.isNotBlank(dateFrom)
								|| org.apache.commons.lang3.StringUtils.isNotBlank(dateTo)) {
							if (dateValidator(dateFrom) && dateValidator(dateTo)) {
								forGRS.setGrs(
										grievanceRedressalSystemRepository.getAllGrs(payId, status, dateFrom, dateTo));
								forGRS.setHttpStatus(HttpStatus.ACCEPTED);
							} else {
								forGRS.setRespmessage(
										"Date format is incorrect please provide valid date format i.e yyyy-MM-dd");
								forGRS.setHttpStatus(HttpStatus.BAD_REQUEST);
							}
						} else {
							forGRS.setRespmessage("Date Is Mandatory");
							forGRS.setHttpStatus(HttpStatus.BAD_REQUEST);
						}
					} else {
						forGRS.setRespmessage("Please Provide Valid Status");
						forGRS.setHttpStatus(HttpStatus.BAD_REQUEST);
					}
				} else {
					forGRS.setRespmessage("Status is Mandatory");
					forGRS.setHttpStatus(HttpStatus.BAD_REQUEST);

				}
			} else {
				forGRS.setRespmessage("PayId Is Mandatory");
				forGRS.setHttpStatus(HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			forGRS.setRespmessage("Failed To Fetch GRS Details");
			forGRS.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in findAllGRS() :", e);
			e.printStackTrace();
		}
		return forGRS;
	}

	private boolean dateValidator(String UTRDate) {
		logger.info("Deadline Date : " + UTRDate);
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			date = sdf.parse(UTRDate);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ResponseMessage inProcess(GrievanceRedressalSystemDto dto) {
		ResponseMessage message = new ResponseMessage();
		logger.info("GRS_ID : " + dto.getGrievanceRedressalSystemId() + "\t Updated BY : " + dto.getUserEmailId());
		try {
			boolean flag = true;
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getGrievanceRedressalSystemId())) {
				message.setRespmessage("GRS ID is Not Found");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(dto.getUserEmailId())) {
				message.setRespmessage("GRS ID is Not Found");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				flag = false;
			}
			if (flag) {
				boolean status = grievanceRedressalSystemRepository.inProcess(dto);
				if (status) {
					message.setRespmessage("SuccessFully Upldated To in Process");
					message.setHttpStatus(HttpStatus.CREATED);
				} else {
					message.setRespmessage("Failed Upldated To in Process");
					message.setHttpStatus(HttpStatus.BAD_REQUEST);
				}
			}
		} catch (Exception e) {
			message.setRespmessage("Failed To Fetch GRS Details");
			message.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in inProcess() :", e);
			e.printStackTrace();
		}
		return message;
	}

	public ResponseMessageForGRS findGRSHistoryById(String grsID) {
		ResponseMessageForGRS forGRS = new ResponseMessageForGRS();
		try {
			if (org.apache.commons.lang3.StringUtils.isNotBlank(grsID)) {
				forGRS.setGrs(grievanceRedressalSystemRepository.getGrsHistory(grsID));
				forGRS.setHttpStatus(HttpStatus.ACCEPTED);
			} else {
				forGRS.setRespmessage("GRS ID is Mandatory");
				forGRS.setHttpStatus(HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			forGRS.setRespmessage("Failed To Fetch GRS Details");
			forGRS.setHttpStatus(HttpStatus.EXPECTATION_FAILED);
			logger.info("Exception Occur in findAllGRS() :", e);
			e.printStackTrace();
		}
		return forGRS;
	}

	@Override
	public ResponseMessage saveDescription(GrsIssueHistoryDto dto) {
		ResponseMessage message = new ResponseMessage();
		try {

			if (dto.getFile() != null && !dto.getFile().isEmpty()) {
				if (dto.getFile() != null && !dto.getFile().isEmpty()) {
					dto.setFilename(System.currentTimeMillis() + "-" + dto.getFilename());
					byte[] fileBytes = Base64.getDecoder().decode(dto.getFile());
					File uploadDir = new File(filePath); // Change this path as needed
					if (!uploadDir.exists()) {
						uploadDir.mkdirs();
					}
					File uploadFile = new File(uploadDir, dto.getFilename());
					try (FileOutputStream os = new FileOutputStream(uploadFile)) {
						os.write(fileBytes);
					} catch (IOException e) {
						message.setRespmessage("Failed to upload file");
						message.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
						logger.error("Error in method saveDescription{}", e);
						return message;

					}

				}
			}
			if (grievanceRedressalSystemRepository.saveDesHistory(dto)) {
				JSONObject gson = new JSONObject();
				gson.put("Message", "Successfully Created!");
				gson.put("GRS_ID", dto.getGrsId());
				gson.put("FileName", dto.getFilename() != null ? dto.getFilename() : "No file uploaded");
				message.setRespmessage(gson.toString());
				message.setHttpStatus(HttpStatus.CREATED);
			} else {
				message.setRespmessage("Failed to save data!");
				message.setHttpStatus(HttpStatus.BAD_REQUEST);
				logger.error("Error occurred while saving data");
				return message;
			}

		} catch (Exception e) {
			// TODO: handle exception
			message.setRespmessage("Failed to upload file");
			message.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("Error in method saveDescription{}", e);
			return message;

		}
		return message;
	}

	@Override
	public List<GrsIssueHistoryDto> getDescription(String GRSID) {
		// TODO Auto-generated method stub
		return grievanceRedressalSystemRepository.getGrsDescHistory(GRSID);
	}

}
