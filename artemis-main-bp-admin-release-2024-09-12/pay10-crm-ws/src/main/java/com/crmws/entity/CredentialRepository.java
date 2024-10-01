package com.crmws.entity;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.warrenstrange.googleauth.ICredentialRepository;
import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

@Component

public class CredentialRepository implements ICredentialRepository {
	@Autowired
	UserDao userDao;

	@Override
	public String getSecretKey(String userName) {
		User user = userDao.findByEmailId(userName);
		if (user != null) {
			if (StringUtils.isNotBlank(user.getGoogle2FASecretkey())) {
				String secretkey = user.getGoogle2FASecretkey();
				return secretkey;
			} else {
				return "User not registered on google authenticator please register first";
			}
		} else {
			return "User Not Found.";
		}

//		System.out.println(usersKeys.get(userName).getSecretKey());
//		return usersKeys.get(userName).getSecretKey();
	}

	@Override

	public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
		User user = userDao.findByEmailId(userName);
		user.setGoogle2FASecretkey(secretKey);
		userDao.update(user);

		// usersKeys.put(userName, new UserTOTP(userName, secretKey, validationCode,
		// scratchCodes));
	}

}