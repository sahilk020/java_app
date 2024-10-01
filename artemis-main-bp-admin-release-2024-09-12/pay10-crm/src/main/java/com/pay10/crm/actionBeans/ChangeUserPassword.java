package com.pay10.crm.actionBeans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.collections4.trie.UnmodifiableTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserRecordsDao;
import com.pay10.commons.util.MerchantKeySaltService;

/**
 * @author ISHA
 *
 */
@Service
public class ChangeUserPassword {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserRecordsDao userRecordsDao;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private CheckOldPassword checkOldPassword;
	
	@Autowired 
	MerchantKeySaltService merchantKeySaltService;
	
	private static Logger logger = LoggerFactory.getLogger(ChangeUserPassword.class.getName());

	public ResponseObject changePassword(String emailId, String oldPassword, String newPassword) throws Exception {
		String salt;
		String matchString = null;
		String newPasswordString = newPassword;
		User user = new User();
		ResponseObject responseObject = new ResponseObject();

		//user = userDao.find(emailId);
		user = userDao.findByEmailId(emailId);

		oldPassword = (PasswordHasher.hashPassword(oldPassword, user.getPayId()));
		newPassword = (PasswordHasher.hashPassword(newPassword, user.getPayId()));

		Trie<String, String> trie = new UnmodifiableTrie<>(new PatriciaTrie<>(fillMap()));

		ArrayList<String> arrayList = extractDictMatches(trie, newPasswordString);
		int arraySize = arrayList.size();

		if (!newPasswordString.isEmpty()) {
			for (int i = 0; i < arraySize; i++) {
				if (newPasswordString.contains(arrayList.get(i))) {
					responseObject.setResponseCode(ErrorType.INVALID_PASSWORDCOMPLEXITY.getResponseCode());
					return responseObject;
				}
			}
		}

		if (!(oldPassword.equals(user.getPassword()))) {
			responseObject.setResponseCode(ErrorType.PASSWORD_MISMATCH.getResponseCode());
			return responseObject;
		} else if (newPassword.equals(oldPassword)) {
			responseObject.setResponseCode(ErrorType.OLD_PASSWORD_MATCH.getResponseCode());
			return responseObject;
		}
		if (checkOldPassword.isUsedPassword(newPassword, user.getEmailId())) {
			responseObject.setResponseCode(ErrorType.REPEATOLD_PASSWORD_MATCH.getResponseCode());
			return responseObject;
		}
		if (newPasswordString.contains(emailId)) {
			responseObject.setResponseCode(ErrorType.EMAIL_NEWPASSWORD_MATCH.getResponseCode());
			return responseObject;
		}
		if (newPasswordString.contains(user.getPayId())) {
			responseObject.setResponseCode(ErrorType.PAYID_NEWPASSWORD_MATCH.getResponseCode());
			return responseObject;
		}
		//salt = SaltFactory.getSaltProperty(user);
		salt = merchantKeySaltService.getMerchantKeySalt(user.getPayId()).getSalt();
		if (newPasswordString.contains(salt)) {
			responseObject.setResponseCode(ErrorType.SALT_NEWPASSWORD_MATCH.getResponseCode());
			return responseObject;
		}

		userRecordsDao.createDetails(emailId, oldPassword, user.getPayId());
		user.setPassword(newPassword);
		user.setPasswordExpired(false);
		userDao.update(user);
		responseObject.setResponseCode(ErrorType.PASSWORD_CHANGED.getResponseCode());
		// Sending Email for CRM password change notification
		emailControllerServiceProvider.passwordChange(responseObject, emailId);

		return responseObject;
	}

	private static Map<String, String> fillMap() {
		String filePath = System.getenv("BPGATE_PROPS")+"common-password-combinations.txt";
		HashMap<String, String> map = new HashMap<String, String>();

		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",", 2);
				if (parts.length >= 2) {
					String key = parts[1];
					String value = parts[0];
					map.put(key, value);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info("Exception in ChangeUserPassword"+e);
		}
		return map;
	}

	private static ArrayList<String> extractDictMatches(Trie<String, String> trie, String pwd) {
		return IntStream.range(0, pwd.length()).collect(ArrayList::new, (objects, i) -> {
			String suffix = pwd.substring(i);
			IntStream.rangeClosed(0, suffix.length()).forEach(j -> {
				String suffixCut = suffix.substring(0, j);
				if (suffixCut.length() > 2) {
					if (trie.containsKey(suffixCut)) {
						objects.add(suffixCut);
					}
				}
			});
		}, (objects, i) -> {
		});
	}

}
