package com.pay10.commons.user;

import static com.pay10.commons.common.EncryptDecrypt.decryptData;
import static com.pay10.commons.common.EncryptDecrypt.encryptData;
import static com.pay10.commons.common.EncryptDecrypt.getRandomDEKKey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.DataAccessObject;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Acquirer;
import com.pay10.commons.util.Agent;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.OrderIdType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SubAdmin;
import com.pay10.commons.util.SubSuperAdmin;
import com.pay10.commons.util.UserStatusType;

@Component("userDao")
//@Scope(value = "prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class UserDao extends HibernateAbstractDao {
    // String mainKey ;
    String mainKey = PropertiesManager.propertiesMap.get("MAIN_KEY_FOR_ALL_MERCHANT");

    private static Logger logger = LoggerFactory.getLogger(UserDao.class.getName());

    public UserDao() {
        super();
    }

    private static final String getCompleteUserWithPayIdQuery = "from User U where U.payId = :payId";
    private static final String getCompleteUserWithEmailIdQuery = "from User U where U.emailId = :emailId";
    private static final String getUserTableWithPayId = "select new map (emailId as emailId, password as password, payId as payId, accHolderName as accHolderName, "
            + "accountNo as accountNo, firstName as firstName, lastName as lastName, accountValidationKey as accountValidationKey, activationDate as activationDate,"
            + " address as address, amountOfTransactions as amountOfTransactions, bankName as bankName, branchName as branchName, businessModel as businessModel, businessName as businessName, cin as cin, comments as comments, companyName as companyName, "
            + "contactPerson as contactPerson, merchantType as merchantType, resellerId as resellerId, productDetail as productDetail, registrationDate as registrationDate,mobile as mobile, transactionSmsFlag as transactionSmsFlag, telephoneNo as telephoneNo, fax as fax, "
            + " city as city, state as state, country as country, postalCode as postalCode, modeType as modeType, whiteListIpAddress as whiteListIpAddress, ifscCode as ifscCode, currency as currency, panCard as panCard, "
            + "uploadePhoto as uploadePhoto, uploadedPanCard as uploadedPanCard, uploadedPhotoIdProof as uploadedPhotoIdProof, uploadedContractDocument as uploadedContractDocument, emailValidationFlag as emailValidationFlag, organisationType as organisationType, website as website,"
            + " multiCurrency as multiCurrency, businessModel as businessModel, operationAddress as operationAddress, operationState as operationState, operationCity as operationCity, operationPostalCode as operationPostalCode, dateOfEstablishment as dateOfEstablishment, pan as pan, panName as panName,"
            + " noOfTransactions as noOfTransactions, attemptTrasacation as attemptTrasacation, transactionEmailId as transactionEmailId, transactionEmailerFlag as transactionEmailerFlag, expressPayFlag as expressPayFlag, merchantHostedFlag as merchantHostedFlag, "
            + "iframePaymentFlag as iframePaymentFlag, transactionAuthenticationEmailFlag as transactionAuthenticationEmailFlag, transactionCustomerEmailFlag as transactionCustomerEmailFlag, refundTransactionCustomerEmailFlag as refundTransactionCustomerEmailFlag, refundTransactionMerchantEmailFlag as refundTransactionMerchantEmailFlag, transactionFailedAlertFlag as transactionFailedAlertFlag,"
            + "retryTransactionCustomeFlag as retryTransactionCustomeFlag, surchargeFlag as surchargeFlag, parentPayId as parentPayId, userStatus as userStatus, userType as userType, industryCategory as industryCategory, industrySubCategory as industrySubCategory,"
            + "extraRefundLimit as extraRefundLimit, defaultCurrency as defaultCurrency, amexSellerId as amexSellerId, mCC as mCC, defaultLanguage as defaultLanguage, "
            + "emailExpiryTime as emailExpiryTime , lastActionName as lastActionName, merchantGstNo as merchantGstNo, updateDate as updateDate, updatedBy as updatedBy, allowDuplicateOrderId as allowDuplicateOrderId, transactionMobileNo as transactionMobileNo, skipOrderIdForRefund as skipOrderIdForRefund, transactionSms as transactionSms, paymentMessageSlab as paymentMessageSlab, allowSaleDuplicate as allowSaleDuplicate, allowRefundDuplicate as allowRefundDuplicate, allowSaleInRefund as allowSaleInRefund, allowRefundInSale as allowRefundInSale, settlementNamingConvention as settlementNamingConvention, refundValidationNamingConvention as refundValidationNamingConvention, onBoardDocList as onBoardDocList, enableWhiteLabelUrl as enableWhiteLabelUrl, whiteLabelUrl as whiteLabelUrl, cardSaveParam as cardSaveParam, enableAutoRefundPostSettlement as enableAutoRefundPostSettlement, passwordExpired as passwordExpired, notificationApiEnableFlag as notificationApiEnableFlag, notificaionApi as notificaionApi, paymentLink as paymentLink) "
            + "from User U where U.payId = :payId1";
    private final static String queryAdminList = "select payId, businessName, emailId, userStatus,Mobile,registrationDate,userType from User where (userType='ADMIN') order by payId ";
    private final static String querymerchantList = "Select emailId from User U where ((U.userType = '"
            + UserType.MERCHANT + "') or (U.userType = '" + UserType.RESELLER + "') ) and U.userStatus='"
            + UserStatusType.ACTIVE + "' order by emailId";
    private static final String getNotificationEmailerUserDetail = "from NotificationEmailer N where N.payId = :payId";
    private static final String getCompleteUserWithUuIdQuery = "from User U where U.uuId = :uuid";

    private Connection getConnection() throws SQLException {
        return DataAccessObject.getBasicConnection();
    }

    public void create(User user) throws DataAccessLayerException {
        /*
         * 1- get random -enckY 2- encrypt(encKy) -encryptedKey--> this will be stored
         * in DB 3- mobile ko encrypt karo using enckY
         */
        // String enckey = "lmm;mpgtrgrgsddb";//PropertiesManager.propertiesMap.get(?"")
        // added by vijaylakshmi
        String merchantKey = getRandomDEKKey(16);
        String encryptedMerchantKey = encryptData(merchantKey, mainKey);
        user.setEncKey(encryptedMerchantKey);

        if (StringUtils.isNotBlank(user.getMobile())) {
            String encyptedMobile = encryptData(user.getMobile(), merchantKey);
            user.setMobile(encyptedMobile);
        }
        if (StringUtils.isNotBlank(user.getAccountNo())) {
            String encyptedAccountNo = encryptData(user.getAccountNo(), merchantKey);
            user.setAccountNo(encyptedAccountNo);
        }
        if (StringUtils.isNotBlank(user.getCin())) {
            String encyptedCin = encryptData(user.getCin(), merchantKey);
            user.setCin(encyptedCin);
        }

        if (StringUtils.isNotBlank(user.getPanCard())) {
            String encyptedPanCard = encryptData(user.getPanCard(), merchantKey);
            user.setPanCard(encyptedPanCard);
        }
        if (StringUtils.isNotBlank(user.getMerchantGstNo())) {
            String encyptedGST = encryptData(user.getMerchantGstNo(), merchantKey);
            user.setMerchantGstNo(encyptedGST);
        }
        super.save(user);
    }
    /*
     * public void createEmailerFalg(NotificationEmailer userFE)throws
     * DataAccessLayerException { super.save(userFE); }
     */

    public void delete(User User) throws DataAccessLayerException {
        super.delete(User);
    }

    public User find(Long id) throws DataAccessLayerException {
        return (User) super.find(User.class, id);
    }

    public User find(String name) throws DataAccessLayerException {
        return (User) super.find(User.class, name);
    }

    @SuppressWarnings("rawtypes")
    public List findAll() throws DataAccessLayerException {
        return super.findAll(User.class);
    }

    public User findPayId(String payId1) {

        User user = findByPayId(payId1);

        if (user != null) {
			return convertIntoPlainText(user);
		} 
		return null;
    }

    public User findPayId1(String payId1) {
        logger.info("findPayId1, payId+{}", payId1);
        User user = findByPayId(payId1);

        return user;

    }

    public void update(User user) throws DataAccessLayerException {
        // String mainKey = "lmm;mpgtrgrgsddb";
        logger.info("Inserting User : {}", user);

        /*
         * 1- get EncKey from user object then decrypt encyKey using main key 1- get
         * mobile from user obj then encrypt using DEC and set dycrpted mobile in user
         * object Do same for all required fileds
         */
        String merchantKey = user.getEncKey();
        String decryptedEnckey = decryptData(merchantKey, mainKey);
        String enccryptedMo = encryptData(user.getMobile(), decryptedEnckey);
        logger.info("Decrypt: {}", user.getMobile());
        logger.info("Encrypt: {}", enccryptedMo);
//		try{
//			throw new RuntimeException("Dummy Error for encryption");
//		}catch (RuntimeException e){
//			logger.error("Dummy error", e);
//		}

        user.setMobile(enccryptedMo);


        if (StringUtils.isNotBlank(user.getAccountNo())) {
            String encyptedAccountNo = encryptData(user.getAccountNo(), decryptedEnckey);
            user.setAccountNo(encyptedAccountNo);
        }
        if (StringUtils.isNotBlank(user.getCin())) {
            String encyptedCin = encryptData(user.getCin(), decryptedEnckey);
            user.setCin(encyptedCin);
        }

        if (StringUtils.isNotBlank(user.getPanCard())) {
            String encyptedPanCard = encryptData(user.getPanCard(), decryptedEnckey);
            user.setPanCard(encyptedPanCard);
        }
        if (StringUtils.isNotBlank(user.getMerchantGstNo())) {
            String encyptedGST = encryptData(user.getMerchantGstNo(), decryptedEnckey);
            user.setMerchantGstNo(encyptedGST);
        }
        super.saveOrUpdate(user);
    }

    /*
     * public void updateNotificationEamiler(NotificationEmailer user) throws
     * DataAccessLayerException { super.saveOrUpdate(user); }
     */

    public void updateEmailValidation(String accountValidationKey, UserStatusType userStatus,
                                      boolean emailValidationFlag) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            session.createQuery(
                            "update User U set U.userStatus = :userStatus, U.emailValidationFlag = :emailValidationFlag"
                                    + " where U.accountValidationKey = :accountValidationKey")
                    .setParameter("userStatus", userStatus).setParameter("emailValidationFlag", emailValidationFlag)
                    .setParameter("accountValidationKey", accountValidationKey).executeUpdate();
            tx.commit();

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
    }

    public void updateAccountValidationKey(String accountValidationKey, String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            session.createQuery("update User U set U.accountValidationKey = :accountValidationKey"
                            + ",U.emailValidationFlag=0 where U.payId = :payId")
                    .setParameter("accountValidationKey", accountValidationKey).setParameter("payId", payId)
                    .executeUpdate();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
    }

    public void enterEmailExpiryTime(Date emailExpiryTime, String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            session.createQuery("UPDATE User U set U.emailExpiryTime = :emailExpiryTime"
                            + ",U.emailValidationFlag=0 where U.payId = :payId")
                    .setParameter("emailExpiryTime", emailExpiryTime).setParameter("payId", payId).executeUpdate();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
    }

//    private User convertIntoPlainText(User user) {// MerchantDetails merchant = allMerchant.get(i);
//        String merchantKey = decryptData(user.getEncKey(), mainKey);
//        String decryptMobile = decryptData(user.getMobile(), merchantKey);
//
//		user.setMobile(enccryptedMo);
//
//
//		if (StringUtils.isNotBlank(user.getAccountNo())) {
//			String encyptedAccountNo = encryptData(user.getAccountNo(), decryptedEnckey);
//			user.setAccountNo(encyptedAccountNo);
//		}
//		if (StringUtils.isNotBlank(user.getCin())) {
//			String encyptedCin = encryptData(user.getCin(), decryptedEnckey);
//			user.setCin(encyptedCin);
//		}
//
//		if (StringUtils.isNotBlank(user.getPanCard())) {
//			String encyptedPanCard = encryptData(user.getPanCard(), decryptedEnckey);
//			user.setPanCard(encyptedPanCard);
//		}
//		if (StringUtils.isNotBlank(user.getMerchantGstNo())) {
//			String encyptedGST = encryptData(user.getMerchantGstNo(), decryptedEnckey);
//			user.setMerchantGstNo(encyptedGST);
//		}
//		super.saveOrUpdate(user);
//	}

	/*
	 * public void updateNotificationEamiler(NotificationEmailer user) throws
	 * DataAccessLayerException { super.saveOrUpdate(user); }
	 */

//	public void updateEmailValidation(String accountValidationKey, UserStatusType userStatus,
//			boolean emailValidationFlag) {
//
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//
//		try {
//			session.createQuery(
//					"update User U set U.userStatus = :userStatus, U.emailValidationFlag = :emailValidationFlag"
//							+ " where U.accountValidationKey = :accountValidationKey")
//					.setParameter("userStatus", userStatus).setParameter("emailValidationFlag", emailValidationFlag)
//					.setParameter("accountValidationKey", accountValidationKey).executeUpdate();
//			tx.commit();
//
//		} catch (ObjectNotFoundException objectNotFound) {
//			handleException(objectNotFound, tx);
//		} catch (HibernateException hibernateException) {
//			handleException(hibernateException, tx);
//		} finally {
//			autoClose(session);
//		}
//	}



	public void CheckactiveEmailNotificationFlag(String emailId,String payId) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			session.createQuery(
							"update User U set U.activeEmailNotificationFlag =1 where U.emailId = :emailId and U.payId=:payId" )
					.setParameter("emailId", emailId).setParameter("payId", payId).executeUpdate();
			tx.commit();

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
	}

//	public void updateAccountValidationKey(String accountValidationKey, String payId) {
//
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//
//		try {
//			session.createQuery("update User U set U.accountValidationKey = :accountValidationKey"
//					+ ",U.emailValidationFlag=0 where U.payId = :payId")
//					.setParameter("accountValidationKey", accountValidationKey).setParameter("payId", payId)
//					.executeUpdate();
//			tx.commit();
//		} catch (ObjectNotFoundException objectNotFound) {
//			handleException(objectNotFound, tx);
//		} catch (HibernateException hibernateException) {
//			handleException(hibernateException, tx);
//		} finally {
//			autoClose(session);
//		}
//	}

//	public void enterEmailExpiryTime(Date emailExpiryTime, String payId) {
//
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//
//		try {
//			session.createQuery("UPDATE User U set U.emailExpiryTime = :emailExpiryTime"
//					+ ",U.emailValidationFlag=0 where U.payId = :payId")
//					.setParameter("emailExpiryTime", emailExpiryTime).setParameter("payId", payId).executeUpdate();
//			tx.commit();
//		} catch (ObjectNotFoundException objectNotFound) {
//			handleException(objectNotFound, tx);
//		} catch (HibernateException hibernateException) {
//			handleException(hibernateException, tx);
//		} finally {
//			autoClose(session);
//		}
//	}

	private User convertIntoPlainText(User user) {// MerchantDetails merchant = allMerchant.get(i);
		String merchantKey = decryptData(user.getEncKey(), mainKey);
		String decryptMobile = decryptData(user.getMobile(), merchantKey);

//		logger.info("Encrypted Mobile : " + user.getMobile() + ", Decrypted Mobile :" + decryptMobile);
        user.setMobile(decryptMobile);
        // String decryptEncKey=decryptData(merchant.getEncKey(),merchantKey);
        String decryptCin = decryptData(user.getCin(), merchantKey);
        user.setCin(decryptCin);
        String decryptPancard = decryptData(user.getPanCard(), merchantKey);
        user.setPanCard(decryptPancard);
        String decryptGst = decryptData(user.getMerchantGstNo(), merchantKey);
        user.setMerchantGstNo(decryptGst);
        String decryptAccountNo = decryptData(user.getAccountNo(), merchantKey);
        user.setAccountNo(decryptAccountNo);
        return user;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getMerchantActive(String emailId) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {

            if (emailId.equals("ALL")) {
                List<Object[]> merchantListRaw = session.createQuery(
                                "Select emailId, payId, businessName  from User U where U.userType = '" + UserType.MERCHANT
                                        + "' and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                        .getResultList();

                for (Object[] objects : merchantListRaw) {
                    Merchants merchant = new Merchants();
                    merchant.setEmailId((String) objects[0]);
                    merchant.setPayId((String) objects[1]);
                    merchant.setBusinessName((String) objects[2]);
                    merchantsList.add(merchant);
                }
            } else {
                List<Object[]> merchantListRaw = session.createQuery(
                                "Select emailId, payId, businessName from User U where U.emailId = :emailId and U.userType = '"
                                        + UserType.MERCHANT + "' and U.userStatus='" + UserStatusType.ACTIVE + "'")
                        .setParameter("emailId", emailId).setCacheable(true).getResultList();

                for (Object[] objects : merchantListRaw) {
                    Merchants merchant = new Merchants();
                    merchant.setEmailId((String) objects[0]);
                    merchant.setPayId((String) objects[1]);
                    merchant.setBusinessName((String) objects[2]);
                    merchantsList.add(merchant);
                }
            }

            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    /*
     * public NotificationEmailer findByEmailerByPayId(String payId) {
     *
     * Session session = HibernateSessionProvider.getSession(); Transaction tx =
     * session.beginTransaction();
     *
     * NotificationEmailer responseUser = null; try { responseUser =
     * (NotificationEmailer) session.createQuery(getNotificationEmailerUserDetail)
     * .setParameter("payId", payId).setCacheable(true) .getSingleResult();
     * tx.commit();
     *
     * return responseUser; }catch (NoResultException noResultException){ return
     * null; }catch (ObjectNotFoundException objectNotFound) {
     * handleException(objectNotFound,tx); } catch (HibernateException
     * hibernateException) { logger.error("error"+hibernateException);
     * handleException(hibernateException,tx); } finally { autoClose(session); }
     * return responseUser; }
     */

    public User findByPayId(String payId1) {

        User responseUser = null;
        Transaction tx = null;
        try (Session session = HibernateSessionProvider.getSession();) {
            tx = session.beginTransaction();
            responseUser = (User) session.createQuery(getCompleteUserWithPayIdQuery).setParameter("payId", payId1).setCacheable(true)
                    .getSingleResult();

            tx.commit();
            // userMap.put(payId1, responseUser);
            session.close();
            return responseUser;
        } catch (NoResultException noResultException) {
        	 logger.error("noResultException" + noResultException);
            noResultException.printStackTrace();
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
        	 logger.error("objectNotFound" + objectNotFound);
            handleException(objectNotFound, tx);
            objectNotFound.printStackTrace();
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
            handleException(hibernateException, tx);
            hibernateException.printStackTrace();
        } catch (Exception e) {
            logger.error("error " + e);
            e.printStackTrace();
        }
        return responseUser;
    }

    /* deepak */
    public boolean getUserObjFlag(String payId) {
        Session session = HibernateSessionProvider.getSession();
        boolean userObjectflag = false;
        try {
            Query query;
            query = session.createQuery("SELECT notificationApiEnableFlag FROM User WHERE payId=:payId");
            query.setParameter("payId", payId);
            userObjectflag = (boolean) query.getSingleResult();
        } finally {
            autoClose(session);
        }
        return userObjectflag;
    }

    protected Object getUserObj(String payId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        Object userObject = null;
        try {
            userObject = session.createQuery(getUserTableWithPayId).setParameter("payId1", payId).setCacheable(true)
                    .getSingleResult();

            tx.commit();

        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userObject;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserObjMap(String payId) {

        Map<String, Object> userDetailsMap = null;
        Object userObject = getUserObj(payId);

        if (null != userObject) {
            userDetailsMap = (Map<String, Object>) userObject;
        }
        return userDetailsMap;
    }

    public User getUserClass(String payId) {
        User responseUser = new User();
        Map<String, Object> userDetailsMap = getUserObjMap(payId);
        if (null == userDetailsMap) {
            return null;
        } else {
            responseUser.setEmailId((String) userDetailsMap.get(CrmFieldType.EMAILID.getName()));
            responseUser.setModeType((ModeType) userDetailsMap.get(CrmFieldConstants.MODE_TYPE.getValue()));
            responseUser.setAccHolderName((String) userDetailsMap.get(CrmFieldType.ACC_HOLDER_NAME.getName()));
            responseUser.setAccountNo((String) userDetailsMap.get(CrmFieldType.ACCOUNT_NO.getName()));
            responseUser.setAccountValidationKey(
                    (String) userDetailsMap.get(CrmFieldType.ACCOUNT_VALIDATION_KEY.getName()));
            responseUser.setActivationDate((Date) userDetailsMap.get(CrmFieldType.ACTIVATION_DATE.getName()));
            responseUser.setAddress((String) userDetailsMap.get(CrmFieldType.ADDRESS.getName()));
            responseUser.setAmountOfTransactions(
                    (String) userDetailsMap.get(CrmFieldType.AMOUNT_OF_TRANSACTIONS.getName()));
            responseUser.setAttemptTrasacation((String) userDetailsMap.get(CrmFieldType.ATTEMPT_TRASACATION.getName()));
            responseUser.setBankName((String) userDetailsMap.get(CrmFieldType.BANK_NAME.getName()));
            responseUser.setBranchName((String) userDetailsMap.get(CrmFieldType.BRANCH_NAME.getName()));
            responseUser.setBusinessModel((String) userDetailsMap.get(CrmFieldType.BUSINESSMODEL.getName()));
            responseUser.setBusinessName((String) userDetailsMap.get(CrmFieldType.BUSINESS_NAME.getName()));
            /*
             * responseUser.setBusinessType((BusinessType) userDetailsMap
             * .get(CrmFieldType.BUSINESS_TYPE.getName()));
             */
            responseUser.setCin((String) userDetailsMap.get(CrmFieldType.CIN.getName()));
            responseUser.setCity((String) userDetailsMap.get(CrmFieldType.CITY.getName()));
            responseUser.setComments((String) userDetailsMap.get(CrmFieldType.COMMENTS.getName()));
            responseUser.setCompanyName((String) userDetailsMap.get(CrmFieldType.COMPANY_NAME.getName()));
            responseUser.setContactPerson((String) userDetailsMap.get(CrmFieldType.CONTACT_PERSON.getName()));
            responseUser.setCountry((String) userDetailsMap.get(CrmFieldType.COUNTRY.getName()));
            responseUser.setCurrency((String) userDetailsMap.get(CrmFieldType.CURRENCY.getName()));
            responseUser
                    .setDateOfEstablishment((String) userDetailsMap.get(CrmFieldType.DATE_OF_ESTABLISHMENT.getName()));
            responseUser.setEmailValidationFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.EMAIL_VALIDATION_FLAG.getValue()));
            responseUser.setExpressPayFlag((boolean) userDetailsMap.get(CrmFieldConstants.EXPRESS_PAY_FLAG.getValue()));
            responseUser.setFax((String) userDetailsMap.get(CrmFieldType.FAX.getName()));
            responseUser.setFirstName((String) userDetailsMap.get(CrmFieldType.FIRSTNAME.getName()));
            responseUser.setIframePaymentFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.IFRAME_PAYMENT_FLAG.getValue()));
            responseUser.setIfscCode((String) userDetailsMap.get(CrmFieldType.IFSC_CODE.getName()));
            responseUser.setLastName((String) userDetailsMap.get(CrmFieldType.LASTNAME.getName()));
            responseUser.setMerchantHostedFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.MERCHANT_HOSTED_FALAG.getValue()));
            responseUser.setMerchantType((String) userDetailsMap.get(CrmFieldType.MERCHANT_TYPE.getName()));
            responseUser.setMobile((String) userDetailsMap.get(CrmFieldType.MOBILE.getName()));
            responseUser.setMultiCurrency((String) userDetailsMap.get(CrmFieldType.MULTICURRENCY.getName()));
            responseUser.setNoOfTransactions((String) userDetailsMap.get(CrmFieldType.NO_OF_TRANSACTIONS.getName()));
            responseUser.setOperationAddress((String) userDetailsMap.get(CrmFieldType.OPERATIONADDRESS.getName()));
            responseUser.setOperationCity((String) userDetailsMap.get(CrmFieldType.OPERATION_CITY.getName()));
            responseUser
                    .setOperationPostalCode((String) userDetailsMap.get(CrmFieldType.OPERATION_POSTAL_CODE.getName()));
            responseUser.setOperationState((String) userDetailsMap.get(CrmFieldType.PPERATION_STATE.getName()));
            responseUser.setOrganisationType((String) userDetailsMap.get(CrmFieldType.ORGANIZATIONTYPE.getName()));
            responseUser.setPan((String) userDetailsMap.get(CrmFieldType.PAN.getName()));
            responseUser.setPanCard((String) userDetailsMap.get(CrmFieldType.PANCARD.getName()));
            responseUser.setPanName((String) userDetailsMap.get(CrmFieldType.PANNAME.getName()));
            responseUser.setParentPayId((String) userDetailsMap.get(CrmFieldType.PARENT_PAY_ID.getName()));
            responseUser.setPassword((String) userDetailsMap.get(CrmFieldType.PASSWORD.getName()));
            responseUser.setPayId((String) userDetailsMap.get(CrmFieldType.PAY_ID.getName()));
            responseUser.setPostalCode((String) userDetailsMap.get(CrmFieldType.POSTALCODE.getName()));
            responseUser.setProductDetail((String) userDetailsMap.get(CrmFieldType.PRODUCT_DETAIL.getName()));
            responseUser.setRegistrationDate((Date) userDetailsMap.get(CrmFieldType.REGISTRATION_DATE.getName()));
            responseUser.setResellerId((String) userDetailsMap.get(CrmFieldType.RESELLER_ID.getName()));
            responseUser.setState((String) userDetailsMap.get(CrmFieldType.STATE.getName()));
            responseUser.setRetryTransactionCustomeFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.RETRY_TRANSACTION_FLAG.getValue()));
            responseUser.setTelephoneNo((String) userDetailsMap.get(CrmFieldType.TELEPHONE_NO.getName()));
            responseUser.setTransactionAuthenticationEmailFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.TRANSACTION_AUTHENTICATION_EMAIL_FLAG.getValue()));
            responseUser.setTransactionCustomerEmailFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.TRANSACTION_CUSTOMER_EMAIL_FLAG.getValue()));
            responseUser.setRefundTransactionCustomerEmailFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.REFUND_TXN_CUSTOMER_EMAIL_FLAG.getValue()));
            responseUser.setRefundTransactionMerchantEmailFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.REFUND_TXN_MERCHANT_EMAIL_FLAG.getValue()));
            responseUser.setTransactionFailedAlertFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.TXN_FAILED_ALERT_FLAG.getValue()));
            responseUser.setNotificationApiEnableFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.NOTIFICATION_API_ENABLE_FLAG.getValue()));
            responseUser.setNotificaionApi((String) userDetailsMap.get(CrmFieldConstants.NOTIFICATION_API.getValue()));
            responseUser.setPaymentLink((String) userDetailsMap.get(CrmFieldConstants.PAYMENT_LINK.getValue()));
            responseUser.setTransactionEmailerFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.TRANSACTION_EMAILER_FLAG.getValue()));
            responseUser
                    .setTransactionEmailId((String) userDetailsMap.get(CrmFieldType.TRANSACTION_EMAIL_ID.getName()));
            responseUser.setTransactionSmsFlag(
                    (boolean) userDetailsMap.get(CrmFieldConstants.TRANSACTION_SMS_FLAG.getValue()));
            responseUser.setUploadedContractDocument(
                    (String) userDetailsMap.get(CrmFieldType.UPLOADE_CONTRACT_DOCUMENT.getName()));
            responseUser.setUploadedPanCard((String) userDetailsMap.get(CrmFieldType.UPLOADE_PAN_CARD.getName()));
            responseUser
                    .setUploadedPhotoIdProof((String) userDetailsMap.get(CrmFieldType.UPLOADE_PHOTOID_PROOF.getName()));
            responseUser.setUploadePhoto((String) userDetailsMap.get(CrmFieldType.UPLOADE_PHOTO.getName()));
            responseUser.setUserStatus((UserStatusType) userDetailsMap.get(CrmFieldType.USERSTATUS.getName()));
            responseUser.setUserType((UserType) userDetailsMap.get(CrmFieldConstants.USER_TYPE.getValue()));
            responseUser.setWebsite((String) userDetailsMap.get(CrmFieldType.WEBSITE.getName()));
            responseUser.setWhiteListIpAddress((String) userDetailsMap.get(CrmFieldType.WHITE_LIST_IPADDRES.getName()));
            responseUser.setExtraRefundLimit((float) userDetailsMap.get(CrmFieldType.EXTRA_REFUND_LIMIT.getName()));
            responseUser.setDefaultCurrency((String) userDetailsMap.get(CrmFieldType.DEFAULT_CURRENCY.getName()));
            responseUser.setAmexSellerId((String) userDetailsMap.get(CrmFieldType.AMEX_SELLER_ID.getName()));
            responseUser.setmCC((String) userDetailsMap.get(CrmFieldType.MCC.getName()));
            responseUser.setSurchargeFlag((boolean) userDetailsMap.get(CrmFieldConstants.SURCHARGE_FLAG.getValue()));
            // Encrypted key
            responseUser.setEncKey((String) userDetailsMap.get("encrypt"));
            responseUser.setIndustryCategory((String) userDetailsMap.get(CrmFieldType.INDUSTRY_CATEGORY.getName()));
            responseUser
                    .setIndustrySubCategory((String) userDetailsMap.get(CrmFieldType.INDUSTRY_SUB_CATEGORY.getName()));
            responseUser.setDefaultLanguage((String) userDetailsMap.get(CrmFieldType.DEFAULT_LANGUAGE.getName()));
            responseUser.setEmailExpiryTime((Date) userDetailsMap.get(CrmFieldType.EMAIL_EXPIRY_TIME.getName()));
            responseUser.setLastActionName((String) userDetailsMap.get(CrmFieldType.LAST_ACTION_NAME.getName()));
            responseUser.setMerchantGstNo((String) userDetailsMap.get(CrmFieldType.MERCHANT_GST_NUMBER.getName()));
            responseUser.setUpdateDate((Date) userDetailsMap.get(CrmFieldType.UPDATE_DATE.getName()));
            responseUser.setUpdatedBy((String) userDetailsMap.get(CrmFieldType.UPDATED_BY.getName()));

            responseUser.setAllowDuplicateOrderId(
                    (OrderIdType) userDetailsMap.get(CrmFieldConstants.ALLOW_DUPLICATE_ORDER_ID.getValue()));
            responseUser.setSkipOrderIdForRefund(
                    (boolean) userDetailsMap.get(CrmFieldConstants.SKIP_ORDER_ID_FOR_REFUND.getValue()));
            responseUser.setTransactionSms((String) userDetailsMap.get(CrmFieldType.TRANSACTION_SMS.getName()));
            responseUser
                    .setPaymentMessageSlab((String) userDetailsMap.get(CrmFieldType.PAYMENT_MESSAGE_SLAB.getName()));
            responseUser.setAllowSaleDuplicate(
                    (boolean) userDetailsMap.get(CrmFieldConstants.ALLOW_SALE_DUPLICATE.getValue()));
            responseUser.setAllowRefundDuplicate(
                    (boolean) userDetailsMap.get(CrmFieldConstants.ALLOW_REFUND_DUPLICATE.getValue()));
            responseUser.setAllowSaleInRefund(
                    (boolean) userDetailsMap.get(CrmFieldConstants.ALLOW_SALE_IN_REFUND.getValue()));
            responseUser.setSettlementNamingConvention(
                    (String) userDetailsMap.get(CrmFieldType.SETTLEMENT_NAMING_CONVENTION.getName()));
            responseUser.setRefundValidationNamingConvention(
                    (String) userDetailsMap.get(CrmFieldType.REFUNDVALIDATION_NAMING_CONVENTION.getName()));
            responseUser.setEnableWhiteLabelUrl(
                    (boolean) userDetailsMap.get(CrmFieldConstants.ENABLE_WHITE_LABEL_URL.getValue()));
            responseUser.setWhiteLabelUrl((String) userDetailsMap.get(CrmFieldType.WHITE_LABEL_URL.getName()));
            responseUser.setCardSaveParam((String) userDetailsMap.get(CrmFieldType.CARD_SAVE_PARAM.getName()));
            responseUser.setEnableAutoRefundPostSettlement(
                    (boolean) userDetailsMap.get(CrmFieldConstants.ENABLE_AUTO_REFUND_POST_SETTLEMENT.getValue()));
            responseUser.setPasswordExpired(
                    (boolean) userDetailsMap.get(CrmFieldConstants.HAS_PASSWORD_EXPIRED.getValue()));
        }
        return responseUser;
    }

    @SuppressWarnings("unchecked")
    public User findByAccountValidationKey(String accountValidationKey) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        User responseUser = null;
        try {
            List<User> users = session.createQuery("from User U where U.accountValidationKey = :accountValidationKey")
                    .setParameter("accountValidationKey", accountValidationKey).getResultList();
            for (User user : users) {
                responseUser = user;
                break;
            }
            tx.commit();

            return responseUser;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }

        return responseUser;
    }

    public List<Merchants> getMerchantActiveList(String segmentName, long roleId) throws DataAccessLayerException {
        return getMerchantActive(segmentName, "", roleId);
    }

    @SuppressWarnings("unchecked")
    protected List<Merchants> getMerchantActive(String segmentName, String value, long roleId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = null;
            if (roleId == 1 || StringUtils.equalsIgnoreCase(segmentName, "default")) {
                merchantListRaw = session.createQuery(
                                "Select emailId, payId, businessName from User U where U.userType = '" + UserType.MERCHANT
                                        + "' and U.userStatus='" + UserStatusType.ACTIVE + "' or U.userStatus='"
                                        + UserStatusType.TRANSACTION_BLOCKED + "' order by businessName")
                        .getResultList();
            } else {
                merchantListRaw = session
                        .createQuery("Select emailId, payId, businessName from User U where U.userType = '"
                                + UserType.MERCHANT + "' and U.segment='" + segmentName + "' and (U.userStatus='"
                                + UserStatusType.ACTIVE + "' or U.userStatus='" + UserStatusType.TRANSACTION_BLOCKED
                                + "') order by businessName")
                        .getResultList();
            }

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    /*
     * @SuppressWarnings("rawtypes") public List getMerchantActiveList() throws
     * DataAccessLayerException { return getMerchantActive(); }
     */

    /*
     * @SuppressWarnings("unchecked") protected List<Merchants> getMerchantActive()
     * { Session session = HibernateSessionProvider.getSession(); Transaction tx =
     * session.beginTransaction();
     *
     * List<Merchants> merchantsList = new ArrayList<Merchants>(); try {
     * List<Object[]> merchantListRaw = session.
     * createQuery("Select emailId, payId, businessName from User U where U.userType = '"
     * + UserType.MERCHANT + "' and U.userStatus='" + UserStatusType.ACTIVE +
     * "' or U.userStatus='" + UserStatusType.TRANSACTION_BLOCKED +
     * "' order by businessName").getResultList();
     *
     * for (Object[] objects : merchantListRaw) { Merchants merchant = new
     * Merchants(); merchant.setEmailId((String) objects[0]);
     * merchant.setPayId((String) objects[1]); merchant.setBusinessName((String)
     * objects[2]); merchantsList.add(merchant); } tx.commit();
     *
     * return merchantsList; } catch (ObjectNotFoundException objectNotFound) {
     * handleException(objectNotFound, tx); } catch (HibernateException
     * hibernateException) { handleException(hibernateException, tx); } finally {
     * autoClose(session); } return merchantsList; }
     */

    @SuppressWarnings("rawtypes")
    public List getMerchantList(String segment, long roleId) throws DataAccessLayerException {
        return getMerchants(segment, roleId);
    }

    @SuppressWarnings("unchecked")
    protected List<Merchants> getMerchants(String segmentName, long roleId) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            // List<Object[]> merchantListRaw = session.createQuery("Select emailId, payId,
            // businessName from User U where U.userType ='" + UserType.MERCHANT + "' and
            // U.segment='" + segmentName + "' and U.userStatus <> 'DELETED' order by
            // businessName").getResultList();
            List<Object[]> merchantListRaw = null;
            if (roleId == 1 || StringUtils.equalsIgnoreCase(segmentName, "default")) {
                merchantListRaw = session
                        .createQuery("Select emailId, payId, businessName from User U where U.userType ='"
                                + UserType.MERCHANT + "' and U.userStatus <> 'DELETED' order by businessName")
                        .getResultList();
            } else {
                merchantListRaw = session
                        .createQuery("Select emailId, payId, businessName from User U where U.userType ='"
                                + UserType.MERCHANT + "' and U.segment='" + segmentName
                                + "' and U.userStatus <> 'DELETED' order by businessName")
                        .getResultList();
            }
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    /*
     * @SuppressWarnings("rawtypes") public List getMerchantList() throws
     * DataAccessLayerException { return getMerchants(); }
     */

    /*
     * @SuppressWarnings("unchecked") protected List<Merchants> getMerchants() {
     * List<Merchants> merchantsList = new ArrayList<Merchants>();
     *
     * Session session = HibernateSessionProvider.getSession(); Transaction tx =
     * session.beginTransaction();
     *
     * try { List<Object[]> merchantListRaw = session.
     * createQuery("Select emailId, payId, businessName from User U where U.userType = '"
     * + UserType.MERCHANT + "' order by businessName").getResultList();
     *
     * for (Object[] objects : merchantListRaw) { Merchants merchant = new
     * Merchants(); merchant.setEmailId((String) objects[0]);
     * merchant.setPayId((String) objects[1]); merchant.setBusinessName((String)
     * objects[2]); merchantsList.add(merchant); } tx.commit();
     *
     * return merchantsList; } catch (ObjectNotFoundException objectNotFound) {
     * handleException(objectNotFound, tx); } catch (HibernateException
     * hibernateException) { handleException(hibernateException, tx); } finally {
     * autoClose(session); } return merchantsList; }
     */

    /*
     * @SuppressWarnings("unchecked") public List<MerchantDetails>
     * getMerchantDetails(String roleType) { List<MerchantDetails> merchantsList =
     * new ArrayList<MerchantDetails>();
     *
     * Session session = HibernateSessionProvider.getSession(); Transaction tx =
     * session.beginTransaction();
     *
     * try { List<Object[]> merchantListRaw = session.
     * createQuery("Select payId, businessName, emailId, userStatus,mobile,registrationDate,userType,encKey from User U where U.userType='MERCHANT'order by U.registrationDate asc"
     * ).getResultList();
     *
     * for (Object[] objects : merchantListRaw) {
     *
     * MerchantDetails merchant = new MerchantDetails(); merchant.setPayId((String)
     * objects[0]); merchant.setBusinessName((String) objects[1]);
     * merchant.setEmailId((String) objects[2]);
     * merchant.setRegistrationDate(String.valueOf(objects[5]));
     * merchant.setUserType(String.valueOf(objects[6])); merchant.setEncKey((String)
     * objects[7]); merchant.setMobile((String) objects[4]); String merchantKey =
     * decryptData(merchant.getEncKey(), mainKey);
     * merchant.setMobile(decryptData(merchant.getMobile(), merchantKey)); String
     * status = (String.valueOf(objects[3]));
     *
     * if (status != null) { UserStatusType userStatus =
     * UserStatusType.valueOf(status); merchant.setStatus(userStatus); }
     *
     * merchantsList.add(merchant); } tx.commit(); return filterByRoleType(roleType,
     * merchantsList); } catch (ObjectNotFoundException objectNotFound) {
     * handleException(objectNotFound, tx); } catch (HibernateException
     * hibernateException) { handleException(hibernateException, tx); } finally {
     * autoClose(session); } return merchantsList; }
     */

    @SuppressWarnings("unchecked")
    public List<MerchantDetails> getMerchantDetails(String roleType, String segmentName, long roleId) {
        List<MerchantDetails> merchantsList = new ArrayList<MerchantDetails>();

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            // List<Object[]> merchantListRaw = session.createQuery("Select payId,
            // businessName, emailId, userStatus,mobile,registrationDate,userType,encKey
            // from User U where U.userType='MERCHANT'order by U.registrationDate
            // asc").getResultList();
            List<Object[]> merchantListRaw = null;
            if (roleId == 1 || StringUtils.equalsIgnoreCase(segmentName, "default")) {
//				logger.info("Segment Name : "+segmentName);
                merchantListRaw = session.createQuery(
                                "Select payId, businessName, emailId, userStatus,mobile,registrationDate,userType,encKey from User U where U.userType='MERCHANT' and U.userStatus!='DELETED' order by U.registrationDate asc")
                        .getResultList();
            } else {
//				logger.info("Segment Name : "+segmentName);
                merchantListRaw = session.createQuery(
                                "Select payId, businessName, emailId, userStatus,mobile,registrationDate,userType,encKey from User U where U.userType='MERCHANT' and U.segment='"
                                        + segmentName + "' and U.userStatus!='DELETED' order by U.registrationDate asc")
                        .getResultList();
            }
            for (Object[] objects : merchantListRaw) {
//				logger.info("Merchant List Object :"+new Gson().toJson(objects));
                MerchantDetails merchant = new MerchantDetails();
                merchant.setPayId((String) objects[0]);
                merchant.setBusinessName((String) objects[1]);
                merchant.setEmailId((String) objects[2]);
                Date date = (Date) objects[5];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // logger.info("Registration date..."+objects[4]);
                String regiDate = sdf.format(date);
                merchant.setRegistrationDate(regiDate);
                merchant.setUserType(String.valueOf(objects[6]));
                merchant.setEncKey((String) objects[7]);
                merchant.setMobile((String) objects[4]);
                String merchantKey = decryptData(merchant.getEncKey(), mainKey);
                merchant.setMobile(decryptData(merchant.getMobile(), merchantKey));
                String status = (String.valueOf(objects[3]));

                if (status != null) {
                    UserStatusType userStatus = UserStatusType.valueOf(status);
                    merchant.setStatus(userStatus);
                }

//				logger.info("Merchant :"+merchant);
                merchantsList.add(merchant);
            }
//			logger.info("Merchant List :"+merchantsList);
            tx.commit();
            return filterByRoleType(roleType, merchantsList);
        } catch (ObjectNotFoundException objectNotFound) {
            logger.error("Error", objectNotFound);
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("Error", hibernateException);
            handleException(hibernateException, tx);
        } catch (Exception e) {
//			logger.info("Exception",e);
            e.printStackTrace();
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    /*
     * @SuppressWarnings("unchecked") public List<MerchantDetails>
     * getMerchantDetails() { List<MerchantDetails> merchantsList = new
     * ArrayList<MerchantDetails>();
     *
     * Session session = HibernateSessionProvider.getSession(); Transaction tx =
     * session.beginTransaction();
     *
     * try { List<Object[]> merchantListRaw = session.
     * createQuery("Select payId, businessName, emailId,mobile,registrationDate,userStatus,userType from User U where U.userType='MERCHANT'order by U.registrationDate asc"
     * ).getResultList();
     *
     * for(Object[] objects : merchantListRaw) {
     *
     * MerchantDetails merchant = new MerchantDetails(); merchant.setPayId((String)
     * objects[0]); merchant.setBusinessName((String) objects[1]);
     * merchant.setEmailId((String) objects[2]); merchant.setMobile((String)
     * objects[3]); Date date = (Date) objects[4];
     * merchant.setRegistrationDate(date.toString());
     *
     * UserStatusType ust = (UserStatusType) objects[5]; merchant.setStatus(ust);
     *
     * UserType userType = (UserType) objects[5];
     * merchant.setUserType(userType.name()); merchantsList.add(merchant); }
     * tx.commit();
     *
     * return merchantsList; } catch (ObjectNotFoundException objectNotFound) {
     * handleException(objectNotFound,tx); } catch (HibernateException
     * hibernateException) { handleException(hibernateException,tx); } finally {
     * autoClose(session); } return merchantsList; }
     */

    /*
     * @SuppressWarnings("rawtypes") public List getActiveMerchantList() throws
     * DataAccessLayerException { return getActiveMerchants(); }
     */

    /*
     * @SuppressWarnings("unchecked") protected List<Merchants> getActiveMerchants()
     * { Session session = HibernateSessionProvider.getSession(); Transaction tx =
     * session.beginTransaction();
     *
     * List<Merchants> merchantsList = new ArrayList<Merchants>(); try {
     * List<Object[]> merchantListRaw = session.
     * createQuery("Select emailId, payId, businessName from User U where ((U.userType = '"
     * + UserType.MERCHANT + "') or (U.userType = '" + UserType.RESELLER +
     * "') ) and U.userStatus='" + UserStatusType.ACTIVE +
     * "' order by businessName").getResultList();
     *
     * for (Object[] objects : merchantListRaw) { Merchants merchant = new
     * Merchants(); merchant.setEmailId((String) objects[0]);
     * merchant.setPayId((String) objects[1]); merchant.setBusinessName((String)
     * objects[2]); merchantsList.add(merchant); } tx.commit();
     *
     * return merchantsList; } catch (ObjectNotFoundException objectNotFound) {
     * handleException(objectNotFound, tx); } catch (HibernateException
     * hibernateException) { handleException(hibernateException, tx); } finally {
     * autoClose(session); } return merchantsList; }
     */

    @SuppressWarnings("rawtypes")
    public List<Merchants> getActiveMerchantList(String segment, long roleId) throws DataAccessLayerException {
        return getActiveMerchants(segment, roleId);
    }

    public List<Merchants> getActiveMerchantListPgWs() throws DataAccessLayerException {
        return getActiveMerchantPgWs();
    }

    @SuppressWarnings("unchecked")
    protected List<Merchants> getActiveMerchantPgWs() {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where ((U.userType = '"
                            + UserType.MERCHANT + "') or (U.userType = '" + UserType.RESELLER
                            + "') ) and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("unchecked")
    protected List<Merchants> getActiveMerchants(String segmentName, long roleId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = null;
            if (roleId == 1 || StringUtils.equalsIgnoreCase(segmentName, "default")) {
                merchantListRaw = session
                        .createQuery("Select emailId, payId, businessName from User U where ((U.userType = '"
                                + UserType.MERCHANT + "') or (U.userType = '" + UserType.RESELLER
                                + "') ) and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                        .getResultList();
            } else {
                merchantListRaw = session.createQuery(
                                "Select emailId, payId, businessName from User U where ((U.userType = '" + UserType.MERCHANT
                                        + "') or (U.userType = '" + UserType.RESELLER + "') ) and U.userStatus='"
                                        + UserStatusType.ACTIVE + "' and U.segment='" + segmentName + "' order by businessName")
                        .getResultList();
            }
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getSubUserList(String parentPayId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where U.userType = '"
                            + UserType.SUBUSER + "' and U.parentPayId = '" + parentPayId + "'")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getSubUsers(String parentPayId) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session.createQuery(
                            "Select payId, emailId, firstName, lastName, mobile, userStatus, encKey from User U where U.userType = '"
                                    + UserType.SUBUSER + "' and parentPayId='" + parentPayId + "'")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setPayId((String) objects[0]);
                merchant.setEmailId((String) objects[1]);
                merchant.setFirstName((String) objects[2]);
                merchant.setLastName((String) objects[3]);
                merchant.setMobile((String) objects[4]);
                String merchantKeyEnc = (String) objects[6];
                String merchantKey = decryptData(merchantKeyEnc, mainKey);
                logger.info("Mobile Number before Decryption " + merchant.getMobile());
                logger.info("Merchant Key " + merchantKey);
                merchant.setMobile(decryptData(merchant.getMobile(), merchantKey));
                logger.info("Mobile Number After Decryption " + merchant.getMobile());
                if (((UserStatusType) objects[5]).equals(UserStatusType.ACTIVE)) {
                    merchant.setIsActive(true);
                } else if (((UserStatusType) objects[5]).equals(UserStatusType.PENDING)) {
                    merchant.setIsActive(false);
                }
                merchantsList.add(merchant);
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    // get Agents
    @SuppressWarnings("unchecked")
    public List<SubAdmin> getUsers(String parentPayId) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<SubAdmin> agentsList = new ArrayList<SubAdmin>();
        try {
            List<Object[]> agentListRaw = session.createQuery(
                            "Select U.payId, U.emailId, U.firstName, U.lastName, U.mobile, U.userStatus, U.encKey, UG.group, U.segment, U.registrationDate from User U INNER JOIN UserGroup UG ON UG.id = U.userGroup.id where U.userType = '"
                                    + UserType.SUBADMIN + "' and U.parentPayId='" + parentPayId + "'")
                    .getResultList();

            for (Object[] objects : agentListRaw) {

                SubAdmin subAdmin = new SubAdmin();
                subAdmin.setPayId((String) objects[0]);
                subAdmin.setAgentEmailId((String) objects[1]);
                subAdmin.setAgentFirstName((String) objects[2]);
                subAdmin.setAgentLastName((String) objects[3]);
                subAdmin.setAgentMobile((String) objects[4]);
                String merchantKeyEnc = (String) objects[6];
                String merchantKey = decryptData(merchantKeyEnc, mainKey);
                subAdmin.setAgentMobile(decryptData(subAdmin.getAgentMobile(), merchantKey));
                if (((UserStatusType) objects[5]).equals(UserStatusType.ACTIVE)) {
                    subAdmin.setAgentIsActive(true);
                } else if (((UserStatusType) objects[5]).equals(UserStatusType.PENDING)) {
                    subAdmin.setAgentIsActive(false);
                }
                subAdmin.setGroupName((String) objects[7]);
                subAdmin.setSegment((String) objects[8]);
                subAdmin.setRegistrationDate(dateFormat.format((Date) objects[9]));
                agentsList.add(subAdmin);
            }
            tx.commit();


            logger.info("Sub-Admin list ={}" + new Gson().toJson(agentsList));
            return agentsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return agentsList;
    }

    // get Acquirers
    @SuppressWarnings("unchecked")
    public List<Acquirer> getAcquirers() {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Acquirer> acquirersList = new ArrayList<Acquirer>();
        try {
            List<Object[]> agentListRaw = session.createQuery(
                            "Select payId, emailId, firstName, lastName, businessName, accountNo, userStatus from User U where U.userType = '"
                                    + UserType.ACQUIRER + "' ")
                    .getResultList();

            for (Object[] objects : agentListRaw) {
                Acquirer acquirer = new Acquirer();
                acquirer.setPayId((String) objects[0]);
                acquirer.setAcquirerEmailId((String) objects[1]);
                acquirer.setAcquirerFirstName((String) objects[2]);
                acquirer.setAcquirerLastName((String) objects[3]);
                acquirer.setAcquirerBusinessName((String) objects[4]);
                acquirer.setAcquirerAccountNo((String) objects[5]);
                if (((UserStatusType) objects[6]).equals(UserStatusType.ACTIVE)) {
                    acquirer.setAcquirerIsActive(true);
                } else {
                    acquirer.setAcquirerIsActive(false);
                }
                acquirersList.add(acquirer);
            }
            tx.commit();
            return acquirersList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return acquirersList;
    }

    @SuppressWarnings("unchecked")
    public List<Agent> getAgent() {
        List<Agent> agentsList = new ArrayList<Agent>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> agentListRaw = session.createQuery(
                            "Select payId, emailId, firstName, lastName, mobile, userStatus, encKey from User U where U.userType = '"
                                    + UserType.AGENT + "' ")
                    .getResultList();

            for (Object[] objects : agentListRaw) {
                Agent agent = new Agent();
                agent.setPayId((String) objects[0]);
                agent.setAgentEmailId((String) objects[1]);
                agent.setAgentFirstName((String) objects[2]);
                agent.setAgentLastName((String) objects[3]);
                agent.setAgentMobile((String) objects[4]);
                String merchantKeyEnc = (String) objects[6];
                String merchantKey = decryptData(merchantKeyEnc, mainKey);
                agent.setAgentMobile(decryptData(agent.getAgentMobile(), merchantKey));

                if (((UserStatusType) objects[5]).equals(UserStatusType.ACTIVE)) {
                    agent.setAgentIsActive(true);
                } else if (((UserStatusType) objects[5]).equals(UserStatusType.PENDING)) {
                    agent.setAgentIsActive(false);
                }
                agentsList.add(agent);
            }
            tx.commit();
            return agentsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return agentsList;
    }

    public User findAcquirerByCode(String acquirerCode) {
        User user = getAcquirer(acquirerCode);
        return user;
    }

    protected User getAcquirer(String acqCode) {
        User responseUser = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            responseUser = (User) session
                    .createQuery("from User U where U.userType='ACQUIRER' and U.firstName = :acqCode")
                    .setParameter("acqCode", acqCode).setCacheable(true).getSingleResult();
            tx.commit();

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return responseUser;
    }

    public User findPayIdByEmail(String emailId) {
        User user = getPayId(emailId);
        return user;
    }

    protected User getPayId(String emailId) {
        User responseUser = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<User> users = session.createQuery("from User U where U.emailId = :emailId")
                    .setParameter("emailId", emailId).getResultList();

            for (User user : users) {
                responseUser = user;
                break;
            }

            tx.commit();
            return responseUser;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return responseUser;
    }

    @SuppressWarnings("rawtypes")
    public List getResellerList() throws DataAccessLayerException {
        return getResellers();
    }

    @SuppressWarnings("unchecked")
    private List<Merchants> getResellers() {
        List<Merchants> resellerList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session
                    .createQuery(
                            "Select emailId, payId, businessName from User U where U.userType = '" + UserType.RESELLER
                                    + "' and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                resellerList.add(merchant);
            }
            tx.commit();

            return resellerList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return resellerList;
    }

    public List<Merchants> getActiveResellerMerchantList(String reselleId) throws DataAccessLayerException {
        return getActiveResellerMerchants(reselleId);
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getActiveResellerMerchants(String resellerId) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where U.userType = '"
                            + UserType.MERCHANT + "'and U.userStatus='" + UserStatusType.ACTIVE + "' and resellerId = '"
                            + resellerId + "' order by businessName")
                    .getResultList();
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("rawtypes")
    public List getResellerMerchantList(String resellerId) throws DataAccessLayerException {

        return getResellerMerchant(resellerId);
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getResellerMerchant(String resellerId) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session.createQuery(
                            "Select emailId, payId, businessName from User U where U.userType = '" + UserType.MERCHANT
                                    + "'and U.userStatus='" + UserStatusType.ACTIVE + "' and resellerId = '" + resellerId + "'")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getAcquirerSubUsers(String parentPayId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = session.createQuery(
                            "Select payId, emailId, firstName, lastName, mobile, userStatus from User U where U.userType = '"
                                    + UserType.SUBACQUIRER + "' and parentPayId='" + parentPayId + "'")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setPayId((String) objects[0]);
                merchant.setEmailId((String) objects[1]);
                merchant.setFirstName((String) objects[2]);
                merchant.setLastName((String) objects[3]);
                merchant.setMobile((String) objects[4]);
                if (((UserStatusType) objects[5]).equals(UserStatusType.ACTIVE)) {
                    merchant.setIsActive(true);
                } else if (((UserStatusType) objects[5]).equals(UserStatusType.PENDING)) {
                    merchant.setIsActive(false);
                }
                merchantsList.add(merchant);
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    public List<User> getUserActiveList() throws DataAccessLayerException {
        return getUserActive();
    }

    @SuppressWarnings("unchecked")
    private List<User> getUserActive() {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> userList = new ArrayList<User>();
        try {
            userList = session.createQuery(" from User U where U.userStatus='" + UserStatusType.ACTIVE + "'")
                    .getResultList();
            tx.commit();

            return userList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userList;
    }

    @SuppressWarnings({"unchecked"})
    public List<String> getMerchantEmailIdListByBusinessType(String businessType) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<String> merchantEmailList = new ArrayList<String>();
        try {
            merchantEmailList = session
                    .createQuery(" Select U.emailId from User U where U.userStatus='" + UserStatusType.ACTIVE + "'"
                            + " and U.userType = 'MERCHANT' and U.industryCategory = :businessType")
                    .setParameter("businessType", businessType).getResultList();
            tx.commit();

            return merchantEmailList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantEmailList;
    }

    public String getMerchantNameByPayId(String payId) {
        String name = null;
        String firstName = null;
        String lastName = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            firstName = (String) session.createQuery("Select firstName from User U where U.payId = :payId")
                    .setParameter("payId", payId).getSingleResult();
            lastName = (String) session.createQuery("Select lastName from User U where U.payId = :payId")
                    .setParameter("payId", payId).getSingleResult();
            name = firstName + " " + lastName;
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return name;
    }

    public String getMerchantByPayId(String payId) {

        String businessName = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            businessName = (String) session.createQuery("Select businessName from User U where U.payId = :payId")
                    .setParameter("payId", payId).getSingleResult();

            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return businessName;
    }

    @SuppressWarnings("unchecked")
    public List<MerchantDetails> getAllAdminList() {
        List<MerchantDetails> merchants = new ArrayList<MerchantDetails>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<User> userList = new ArrayList<User>();
        try {
            userList = session.createQuery(" from User U where U.userType = 'ADMIN' ").getResultList();
            tx.commit();

            for (User user : userList) {

                MerchantDetails merchant = new MerchantDetails();
                merchant.setPayId(user.getPayId());
                merchant.setEmailId(user.getEmailId());
                merchant.setBusinessName(user.getBusinessName());
                merchant.setMobile(user.getMobile());
                if (user.getRegistrationDate() != null) {
                    Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String s = formatter.format(user.getRegistrationDate());
                    merchant.setRegistrationDate(s);
                } else {
                    merchant.setRegistrationDate("");
                }
                merchant.setUserType(user.getUserType().toString());
                merchant.setStatus(user.getUserStatus());

                if (user.getTenantId() != null) {
                    merchant.setTenantId(user.getTenantId().toString());
                } else {
                    merchant.setTenantId("NA");
                }

                if (user.getCompanyName() != null) {
                    merchant.setCompanyName(user.getCompanyName().toString());
                } else {
                    merchant.setCompanyName("NA");
                }

                if (user.getTenantNumber() != null) {
                    merchant.setTenantNumber(user.getTenantNumber().toString());
                } else {
                    merchant.setTenantNumber("NA");
                }

                merchants.add(merchant);

            }
            return merchants;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchants;
    }

    public List<Merchants> featchAllmerchant() throws SystemException {
        List<Merchants> merchants = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try (Connection connection = getConnection()) {
            try (PreparedStatement prepStmt = connection.prepareStatement(querymerchantList)) {
                try (ResultSet rs = prepStmt.executeQuery()) {
                    while (rs.next()) {
                        Merchants merchant = new Merchants();
                        merchant.setEmailId(rs.getString("emailId"));
                        merchants.add(merchant);
                    }
                }
            }
        } catch (SQLException exception) {
            logger.error("Database error", exception);
            throw new SystemException(ErrorType.DATABASE_ERROR, ErrorType.DATABASE_ERROR.getResponseMessage());
        } finally {
            autoClose(session);
        }
        return merchants;

    }

    public String getBusinessNameByEmailId(String emailId) {
        String businessName = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            businessName = (String) session.createQuery("Select businessName from User U where U.emailId = :emailId")
                    .setParameter("emailId", emailId).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return businessName;
    }

    public String getBusinessNameByPayId(String payId) {
        String businessName = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            businessName = (String) session.createQuery("Select businessName from User U where U.payId = :payId")
                    .setParameter("payId", payId).getSingleResult();
            tx.commit();
        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return businessName;
    }

    public String getEmailIdByBusinessName(String businessName) {
        String emailId = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            emailId = (String) session.createQuery("Select emailId from User U where U.businessName = :businessName")
                    .setParameter("businessName", businessName).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return emailId;
    }

    public String getPayIdByEmailId(String emailId) {
        String payId = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            payId = (String) session.createQuery("Select payId from User U where U.emailId = :emailId")
                    .setParameter("emailId", emailId).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return payId;
    }

    public String getEmailIdByPayId(String payId) {
        String emailId = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            emailId = (String) session.createQuery("Select emailId from User U where U.payId = :payId")
                    .setParameter("payId", payId).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return emailId;
    }

    public String getRefundValidationNCByEmailId(String emailId) {
        String refundValidationNamingConvention = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            refundValidationNamingConvention = (String) session
                    .createQuery("Select refundValidationNamingConvention from User U where U.emailId = :emailId")
                    .setParameter("emailId", emailId).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return refundValidationNamingConvention;
    }

    public String getSettlementNCByPayId(String payId) {
        String settlementNamingConvention = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            settlementNamingConvention = (String) session
                    .createQuery("Select settlementNamingConvention from User U where U.payId = :payId")
                    .setParameter("payId", payId).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return settlementNamingConvention;
    }

    public List<SubSuperAdmin> getSubSuperAdmin(String sessionPayId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<SubSuperAdmin> agentsList = new ArrayList<SubSuperAdmin>();
        try {
            List<Object[]> agentListRaw = session.createQuery(
                            "Select payId, emailId, firstName, lastName, mobile, userStatus from User U where U.userType = '"
                                    + UserType.SUBSUPERADMIN + "' and parentPayId='" + sessionPayId + "'")
                    .getResultList();

            for (Object[] objects : agentListRaw) {
                SubSuperAdmin subAdmin = new SubSuperAdmin();
                subAdmin.setPayId((String) objects[0]);
                subAdmin.setAgentEmailId((String) objects[1]);
                subAdmin.setAgentFirstName((String) objects[2]);
                subAdmin.setAgentLastName((String) objects[3]);
                subAdmin.setAgentMobile((String) objects[4]);
                if (((UserStatusType) objects[5]).equals(UserStatusType.ACTIVE)) {
                    subAdmin.setAgentIsActive(true);
                } else if (((UserStatusType) objects[5]).equals(UserStatusType.PENDING)) {
                    subAdmin.setAgentIsActive(false);
                }
                agentsList.add(subAdmin);
            }
            tx.commit();
            return agentsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return agentsList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getMerchantListByResellerID(String resellerId) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Object[]> merchantListRaw = session.createQuery(
                            "Select emailId, payId, businessName,resellerId,registrationDate,userStatus from User U where U.userType = '"
                                    + UserType.MERCHANT + "' and resellerId='" + resellerId + "'order by businessName")
                    .getResultList();
            for (Object[] objects : merchantListRaw) {
                if (objects[3] != null && objects[4] != null) {
                    if (StringUtils.isNotBlank((String) objects[3]) && !(boolean) objects[4]) {
                        continue;
                    }
                }
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchant.setResellerId((String) objects[3]);
                Date date = (Date) objects[4];
                merchant.setRegistrationDate(date.toString());
                UserStatusType ust = (UserStatusType) objects[5];
                merchant.setStatus(ust.getStatus());

                if (StringUtils.isNotBlank(merchant.getResellerId())) {
                    if (merchant.getResellerId().length() > 15) {
                        merchant.setResellerName(getBusinessNameByPayId(merchant.getResellerId()));
                        merchantsList.add(merchant);
                    }
                } else {
                    continue;
                }
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getAllActiveReseller() {
        List<Merchants> resellerList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, resellerId, businessName from User U where U.userType = '"
                            + UserType.RESELLER + "' and userStatus = '" + UserStatusType.ACTIVE
                            + "' order by businessName")
                    .getResultList();
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setResellerId((String) objects[2]);
                merchant.setBusinessName((String) objects[3]);
                resellerList.add(merchant);
            }
            tx.commit();
            return resellerList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return resellerList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getActiveResellerMerchantsList(String resellerId, int length, int start) {
        List<Merchants> merchantsList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where U.userType = '"
                            + UserType.MERCHANT + "'and U.userStatus='" + UserStatusType.ACTIVE + "' and resellerId = '"
                            + resellerId + "' order by businessName")
                    .setFirstResult(start).setMaxResults(length).getResultList();
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getResellerByResellerId(String resellerId) {
        List<Merchants> resellerList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> resellerListRaw = session.createQuery(
                            "Select emailId, payId, businessName, resellerId,registrationDate,userStatus from User U where U.resellerId = :resellerId and U.userType = '"
                                    + UserType.MERCHANT + "' and U.userStatus='" + UserStatusType.ACTIVE + "'")
                    .setParameter("resellerId", resellerId).setCacheable(true).getResultList();

            for (Object[] objects : resellerListRaw) {
                Merchants reseller = new Merchants();
                reseller.setEmailId((String) objects[0]);
                reseller.setPayId((String) objects[1]);
                reseller.setBusinessName((String) objects[2]);
                reseller.setResellerId((String) objects[3]);
                Date date = (Date) objects[4];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String regiDate = sdf.format(date);
                reseller.setRegistrationDate(regiDate);
                UserStatusType ust = (UserStatusType) objects[5];
                reseller.setStatus(ust.getStatus());

                if (StringUtils.isNotBlank(reseller.getResellerId())) {
                    if (reseller.getResellerId().length() > 15) {
                        reseller.setResellerName(getBusinessNameByPayId(reseller.getResellerId()));
                        resellerList.add(reseller);
                    }
                } else {
                    continue;
                }

            }

            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return resellerList;
    }

    public User findByEmailId(String emailId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        User responseUser = null;
        try {
            responseUser = (User) session.createQuery(getCompleteUserWithEmailIdQuery).setParameter("emailId", emailId)
                    .setCacheable(true).getSingleResult();
            tx.commit();
            return convertIntoPlainText(responseUser);
        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
            handleException(hibernateException, tx);
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return responseUser;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getResellerMerchants() {
        List<Merchants> merchantsList = new ArrayList<Merchants>();

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session.createQuery(
                            "Select emailId, payId, businessName,resellerId,registrationDate,userStatus from User U where U.userType = '"
                                    + UserType.MERCHANT + "' and U.resellerId is not null order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {

                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchant.setResellerId((String) objects[3]);
                Date date = (Date) objects[4];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String regiDate = sdf.format(date);
                merchant.setRegistrationDate(regiDate);
                UserStatusType ust = (UserStatusType) objects[5];
                merchant.setStatus(ust.getStatus());

                if (StringUtils.isNotBlank(merchant.getResellerId())) {
                    if (merchant.getResellerId().length() > 15) {
                        merchant.setResellerName(getBusinessNameByPayId(merchant.getResellerId()));
                        merchantsList.add(merchant);
                    }
                } else {
                    continue;
                }

            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    public User findUserForReseller(String emailId, String resellerId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        User responseUser = null;
        try {
            responseUser = (User) session
                    .createQuery("from User U where U.emailId = :emailId and U.resellerId = :resellerId")
                    .setParameter("emailId", emailId).setParameter("resellerId", resellerId).setCacheable(true)
                    .getSingleResult();

            tx.commit();

            // userMap.put(payId1, responseUser);
            return responseUser;
        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
            handleException(hibernateException, tx);
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return responseUser;
    }

    public List<String> getAcquirerTypeList(String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> list = new ArrayList<String>();
        try {
            Query query = session.createSQLQuery("SELECT distinct acquirerName FROM TdrSetting where  payId=:payId");
            query.setString("payId", payId);
            list = query.list();
            System.out.println("groupList " + list);
            tx.commit();
            logger.info("aquireer data in dao class" + list);
            return list;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return list;
    }

    public List<String> getCurrencyTypeList(String acquirer, String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> list = new ArrayList<String>();
        try {
            String queryString = "SELECT distinct currency FROM TdrSetting WHERE status='ACTIVE'";
            if (!acquirer.equalsIgnoreCase("ALL")) {
                queryString += " AND acquirerName=:acquirer";
            }
            if (!payId.equalsIgnoreCase("ALL")) {
                queryString += " AND payId=:payId";
            }
            Query query = session.createSQLQuery(queryString);
            if (!payId.equalsIgnoreCase("ALL")) {
                query.setString("payId", payId);
            }
            if (!acquirer.equalsIgnoreCase("ALL")) {
                query.setString("acquirer", acquirer);
            }
            list = query.list();
            logger.info("The query is getting run for this :- {}", queryString);
            tx.commit();
            logger.info("Currency data in dao class" + list);
            return list;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return list;
    }

    public List<String> getAcquirerTypeListFromTdrSetting(String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> list = new ArrayList<String>();
        try {
            Query query = session.createSQLQuery("SELECT distinct acquirerName FROM TdrSetting where  payId=:payId");
            query.setString("payId", payId);
            list = query.list();
            System.out.println("groupList " + list);

            tx.commit();
            logger.info("aquireer data in dao class" + list);
            return list;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return list;
    }

    public static void main(String[] args) {
        String key = getRandomDEKKey(16);
        // f9bxCSR27uhOXAgL
        String merchantKeyEnc = encryptData("hello Jay", key);
        System.out.println(merchantKeyEnc);

//		String merchantKeyEnc = "W/+LQ3qbZz6Qlko4C3ZoYcVK4qP6JZ8gYzNEkqLv5ig=";
        String merchantKey = decryptData(merchantKeyEnc, key);
        System.out.println(merchantKey);
        System.out.println("-------------- " + decryptData("53E1rfIDjslQGxgi6L06LA==", merchantKey));
    }

    public List<String> getMopTypelist(String payId, String Acquirer1, String paytype, String selectedcurrency) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> paylist = new ArrayList<String>();
        try {
            Query query = session.createSQLQuery(
                    "SELECT distinct mopType FROM TdrSetting where acquirerName=:acquirerName and payId=:payId and paymentType=:paymentType and currency=:currency and status='ACTIVE'");
            query.setString("payId", payId);
            query.setString("acquirerName", Acquirer1);
            query.setString("paymentType", paytype);
            query.setString("currency", selectedcurrency);

            // System.out.println("SELECT distinct mopType FROM TdrSetting where
            // acquirerName=:acquirerName and payId=:payId and paymentType like
            // '%"+paytype+"%' ");
            paylist = query.list();

            tx.commit();
            return paylist;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return paylist;
    }

    public List<String> getPaymentType(String payId, String acquirer1, String selectedCurrency) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> moplist = new ArrayList<String>();
        logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        try {

            Query query = session.createSQLQuery(
                    "SELECT distinct paymentType FROM TdrSetting where acquirerName= :acquirerName and payId=:payId and currency=:currency and status='ACTIVE'");
            query.setString("payId", payId);
            query.setString("acquirerName", acquirer1);
            query.setString("currency", selectedCurrency);
            moplist = query.list();
            logger.info("bbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaaaa" + moplist);

            tx.commit();
            return moplist;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return moplist;
    }

    @SuppressWarnings("unchecked")
    public List<Merchants> getAllReseller1() {
        List<Merchants> resellerList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> resellerListRaw = session.createQuery(
                            "Select emailId, payId, businessName, resellerId,registrationDate,userStatus,mobile,userType from User U where (U.userType = '"
                                    + UserType.RESELLER + "' or U.userType = '" + UserType.SMA + "' or U.userType = '"
                                    + UserType.MA + "' or U.userType='" + UserType.Agent + "') and U.userStatus!='DELETED'")
                    .setCacheable(true).getResultList();

            for (Object[] objects : resellerListRaw) {
                Merchants reseller = new Merchants();
                reseller.setEmailId((String) objects[0]);
                reseller.setPayId((String) objects[1]);
                reseller.setBusinessName((String) objects[2]);
                reseller.setResellerId((String) objects[3]);
                Date date = (Date) objects[4];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // logger.info("Registration date..."+objects[4]);
                String regiDate = sdf.format(date);
                reseller.setRegistrationDate(regiDate);
                UserStatusType ust = (UserStatusType) objects[5];
                reseller.setStatus(ust.getStatus());
                reseller.setMobile((String) objects[6]);
                UserType userType = (UserType) objects[7];
                reseller.setUserType(userType.name());
                resellerList.add(reseller);

            }

            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return resellerList;
    }

    public long getUserCountByRole(long roleId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            long count = (long) session.createQuery("Select count(*) from User U where U.role.id=:id")
                    .setParameter("id", roleId).getSingleResult();
            tx.commit();
            return count;
        } finally {
            autoClose(session);
        }
    }

    public String getMerchantReturnURL(String payId) throws SystemException {
        String returnURL = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            returnURL = (String) session.createQuery("Select returnURL from MerchantReturnURL where payId = :payId")
                    .setParameter("payId", payId).getSingleResult();

            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return returnURL;
    }

//	public String getMerchantReturnURL(String payId) throws SystemException {
//		String returnURL = null;
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//
//		try {
//			returnURL = (String) session.createQuery("Select returnURL from MerchantReturnURL where payId = :payId")
//					.setParameter("payId", payId).getSingleResult();
//
//			tx.commit();
//		} catch (ObjectNotFoundException objectNotFound) {
//			handleException(objectNotFound, tx);
//		} catch (HibernateException hibernateException) {
//			handleException(hibernateException, tx);
//		} finally {
//			autoClose(session);
//		}
//		return returnURL;
//	}

    public static List<MerchantDetails> filterByRoleType(String roleType, List<MerchantDetails> merchantsList) {
        if (StringUtils.isBlank(roleType)) {
            return merchantsList;
        }
//		if (StringUtils.equals(RoleType.CHECKER.name(), roleType)) {
//			return merchantsList.stream().filter(merchant -> merchant.getStatus() == UserStatusType.PENDING)
//					.collect(Collectors.toList());
//		} else if (StringUtils.equals(RoleType.OPERATION.name(), roleType)) {
//			return merchantsList.stream().filter(merchant -> merchant.getStatus() == UserStatusType.CHECKER_APPROVED)
//					.collect(Collectors.toList());
//		} else if (StringUtils.equals(RoleType.RISK.name(), roleType)) {
//			return merchantsList.stream().filter(merchant -> merchant.getStatus() == UserStatusType.OPERATION_APPROVED)
//					.collect(Collectors.toList());
//		}
        return merchantsList;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List getMerchantListForReseller() throws DataAccessLayerException {
        List<Merchants> merchantsList = new ArrayList<Merchants>();

        Session session = HibernateSessionProvider.getSession();
        try {
            List<Object[]> merchantListRaw = session.createQuery(
                            "Select emailId, payId, businessName from User U where U.resellerId is null and U.userType ='"
                                    + UserType.MERCHANT + "' and U.userStatus <> 'DELETED' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            return merchantsList;
        } finally {
            autoClose(session);
        }
    }

    // Added By Sweety
    @SuppressWarnings("unchecked")
    public List<Merchants> getActiveMerchant(String segment, long roleId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = null;
            if (roleId == 1 || StringUtils.equalsIgnoreCase(segment, "default")) {
                merchantListRaw = session.createQuery(
                                "Select emailId, payId, businessName from User U where ((U.userType = '" + UserType.MERCHANT
                                        + "')) and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                        .getResultList();
            } else {
                merchantListRaw = session
                        .createQuery("Select emailId, payId, businessName from User U where ((U.userType = '"
                                + UserType.MERCHANT + "')) and U.userStatus='" + UserStatusType.ACTIVE
                                + "' and U.segment='" + segment + "' order by businessName")
                        .getResultList();
            }
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;

    }

    public long isMerchantMapped(String payId, String resellerId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        logger.info("payId..." + payId);
        logger.info("resellerId..." + resellerId);
        try {
            long count = (long) session
                    .createQuery("Select count(*) from User U where U.payId=:payId and U.resellerId=:resellerId")
                    .setParameter("payId", payId).setParameter("resellerId", resellerId).getSingleResult();
            logger.info("payId...." + payId + ".............." + "resellerId....." + resellerId);
            logger.info("count to check that merchant already mapped with reseller...={}", count);
            tx.commit();
            logger.info("count to check that merchant already mapped with reseller...={}", count);
            return count;
        } finally {
            autoClose(session);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> getResellersbyid(String resellerId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery("FROM User RC WHERE resellerId=:resellerId and RC.userType ='"
                            + UserType.MERCHANT + "' order by businessName").setParameter("resellerId", resellerId)
                    .getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;

    }

    public void saveandUpdate(User user) throws DataAccessLayerException {
        logger.info("############################################" + user.toString());
        super.saveOrUpdate(user);
        logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + user.toString());
    }

    public List<Merchants> findByResellerId(String resellerId) {
        List<Merchants> resellerList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> resellerListRaw = session.createQuery(
                            "Select emailId, payId, businessName, resellerId from User U where U.resellerId = :resellerId and U.userType = '"
                                    + UserType.RESELLER + "' and U.userStatus='" + UserStatusType.ACTIVE + "'")
                    .setParameter("resellerId", resellerId).setCacheable(true).getResultList();

            for (Object[] objects : resellerListRaw) {
                Merchants reseller = new Merchants();
                reseller.setEmailId((String) objects[0]);
                reseller.setPayId((String) objects[1]);
                reseller.setBusinessName((String) objects[2]);
                reseller.setResellerId((String) objects[3]);

                if (StringUtils.isNotBlank(reseller.getResellerId())) {
                    if (reseller.getResellerId().length() > 15) {
                        reseller.setResellerName(getBusinessNameByPayId(reseller.getResellerId()));
                        resellerList.add(reseller);
                    }
                } else {
                    continue;
                }

            }

            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return resellerList;
    }

    @SuppressWarnings("unchecked")
    public List<String> getPayIdForSplitPaymentMerchant(String segmentName) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> merchantsList = new ArrayList<String>();
        try {
            merchantsList = session.createQuery(
                            "Select payId from User U where U.userType = 'MERCHANT' and U.segment = '" + segmentName + "'")
                    .getResultList();
            tx.commit();
            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    public RefundConfiguration getRefundMode(String payId, String acquirerName, String paymentType, String mopType) {
        logger.info("GetRefundMode payId = {}", payId);
        logger.info("GetRefundMode acquirerName = {}", acquirerName);
        logger.info("GetRefundMode paymentType = {}", paymentType);
        logger.info("GetRefundMode mopType = {}", mopType);

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        RefundConfiguration refundInfo = null;
        try {
            refundInfo = (RefundConfiguration) session.createQuery(
                            "from RefundConfiguration R where R.acquirerName = :acquirerName and R.paymentType = :paymentType")
                    .setParameter("acquirerName", acquirerName).setParameter("paymentType", paymentType)
                    .setCacheable(true).getSingleResult();

            tx.commit();

            // userMap.put(payId1, responseUser);
            return refundInfo;
        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
            handleException(hibernateException, tx);
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return refundInfo;
    }

    public String getPayIdByMerchantName(String merchantName) {
        String payId = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            payId = (String) session.createQuery("Select payId from User U where U.businessName = :businessName")
                    .setParameter("businessName", merchantName).setMaxResults(1).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return payId;
    }

    public String getPayIdByAcquirerNameForWallet(String acquirerName) {
        String payId = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            payId = (String) session.createQuery("Select payId from User U where U.businessName = :businessName and userType='ACQUIRER'")
                    .setParameter("businessName", acquirerName).getSingleResult();
            tx.commit();
            logger.info("PayId For Acquirer: {} {}", acquirerName, payId);
            return payId;
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } catch (Exception ignored) {
        } finally {
            autoClose(session);
        }
        return "NA";
    }

    @SuppressWarnings("deprecation")
    public List<String> getPaymentType(String payId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<String> moplist = new ArrayList<String>();
        try {

            Query query = session.createSQLQuery("SELECT distinct paymentType FROM TdrSetting where payId=:PayId");
            query.setString("PayId", payId);

            moplist = query.list();

            tx.commit();
            return moplist;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return moplist;
    }

    @SuppressWarnings("unchecked")
    public List<String> getMOPtypelist(String payId, String paytype) {
        Session session = HibernateSessionProvider.getSession();
        try {
            String queryStr = "SELECT distinct mopType FROM TdrSetting where paymentType=:payType";
            String queryStrMerchantSpecific = "SELECT distinct mopType FROM TdrSetting where payId=:payId and paymentType=:payType";
            queryStr = StringUtils.isBlank(payId) ? queryStr : queryStrMerchantSpecific;
            Query<String> query = session.createNativeQuery(queryStr);
            query.setParameter("payType", paytype);
            if (StringUtils.isNotBlank(payId)) {
                query.setParameter("payId", payId);
            }
            return query.getResultList();
        } finally {
            autoClose(session);
        }
    }

    public User findByUuid(String uuid) {
        logger.info("get UUID in findByUuid()....={}", uuid);
        Session session = HibernateSessionProvider.getSession();

        User responseUser = null;
        try {
            responseUser = (User) session.createQuery(getCompleteUserWithUuIdQuery).setParameter("uuid", uuid)
                    .getSingleResult();
            // logger.info("Data getting from DB : " + new Gson().toJson(responseUser));

            // userMap.put(payId1, responseUser);
            return responseUser;
        } catch (NoResultException noResultException) {
            return null;
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return responseUser;
    }

    // Added By Sweety
    @SuppressWarnings("unchecked")
    public List<Merchants> getWebStoreEnableMerchantList() {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = null;

            merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName,uuId from User U where ((U.userType = '"
                            + UserType.MERCHANT + "')) and U.userStatus='" + UserStatusType.ACTIVE
                            + "' and U.webStoreApiEnableFlag='" + '1' + "' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchant.setUuId((String) objects[3]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    public List<Merchants> getUserList(String segment, long roleId) {
        return getUserLists(segment, roleId);
    }

    protected List<Merchants> getUserLists(String segmentName, long roleId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<Merchants> merchantsList = new ArrayList<Merchants>();
        try {
            List<Object[]> merchantListRaw = null;
            if (roleId == 1 || StringUtils.equalsIgnoreCase(segmentName, "default")) {
                merchantListRaw = session
                        .createQuery("Select emailId, payId, businessName from User U where ((U.userType != '"
                                + UserType.MERCHANT + "') and (U.userType != '" + UserType.RESELLER
                                + "') ) and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                        .getResultList();
            } else {
                merchantListRaw = session.createQuery(
                                "Select emailId, payId, businessName from User U where ((U.userType = '" + UserType.MERCHANT
                                        + "') and (U.userType = '" + UserType.RESELLER + "') ) and U.userStatus='"
                                        + UserStatusType.ACTIVE + "' and U.segment='" + segmentName + "' order by businessName")
                        .getResultList();
            }
            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                merchantsList.add(merchant);
            }
            tx.commit();

            return merchantsList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return merchantsList;
    }

    public void createAccountCurrency(AccountCurrency accountCurrency) throws DataAccessLayerException {

        super.save(accountCurrency);
    }

    public AccountCurrency findAccountCurrencyDetails(String acqPayId, String merchantId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        AccountCurrency accountInfo = null;
        try {
            accountInfo = (AccountCurrency) session
                    .createQuery("from AccountCurrency R where R.acqPayId = :acqPayId and R.merchantId = :merchantId")
                    .setParameter("acqPayId", acqPayId).setParameter("merchantId", merchantId).setCacheable(true)
                    .getSingleResult();

            tx.commit();

            // userMap.put(payId1, responseUser);
            return accountInfo;
        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
            handleException(hibernateException, tx);
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return accountInfo;
    }

    public QuomoCurrencyConfiguration getQuomoCurrencyConfiguration(String acquirer, String paymentType, String mopType,
                                                                    String currencyCode) {
        logger.info("getQuomoCurrencyConfiguration Request ");

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        QuomoCurrencyConfiguration quomoCurrencyConfiguration = null;
        try {
            quomoCurrencyConfiguration = (QuomoCurrencyConfiguration) session.createQuery(
                            "from QuomoCurrencyConfiguration R where R.acquirer = :acquirer and R.paymentType = :paymentType "
                                    + "and R.mopType = :mopType and R.currencyCode = :currencyCode and R.status = :status ")
                    .setParameter("acquirer", acquirer).setParameter("paymentType", paymentType)
                    .setParameter("mopType", mopType).setParameter("currencyCode", currencyCode)
                    .setParameter("status", "ACTIVE").setCacheable(true).getSingleResult();

            tx.commit();

            // userMap.put(payId1, responseUser);
            return quomoCurrencyConfiguration;
        } catch (NoResultException noResultException) {
            return null;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
            handleException(hibernateException, tx);
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return quomoCurrencyConfiguration;
    }

    public List<QuomoCurrencyConfiguration> findByAcquirerFromQuomoCurrencyConfig(String acquirer) {
        try (Session session = HibernateSessionProvider.getSession()) {
            Query preQuery = session.createQuery("FROM QuomoCurrencyConfiguration WHERE  acquirer=:acquirer")
                    .setParameter("acquirer", acquirer);
            List<QuomoCurrencyConfiguration> dataList = preQuery.getResultList();
//			logger.info(new Gson().toJson("merchant mapping with currency result from Dao::::" + dataList));
            return dataList;
        }
    }

    // Added by Deep Sing for Payout
    @SuppressWarnings("unchecked")
    public List<User> fetchUsersBasedOnPOEnable() {
        Session session = HibernateSessionProvider.getSession();
        List<User> users = new ArrayList<>();

        try {
            users = session.createQuery("from User where POFlag=true").setCacheable(true).getResultList();
        } catch (NoResultException noResultException) {
            return null;
        } catch (HibernateException hibernateException) {
            logger.error("error" + hibernateException);
        } catch (Exception e) {
            logger.error("error " + e);
        } finally {
            autoClose(session);
        }
        return users;
    }

    public List<QuomoCurrencyConfiguration> findByCurrencyCodeAndMopName(String currencyCode, String bankName) {
        try (Session session = HibernateSessionProvider.getSession()) {
            Query preQuery = session
                    .createQuery(
                            "FROM QuomoCurrencyConfiguration WHERE currencyCode=:currencyCode AND bankName=:bankName")
                    .setParameter("currencyCode", currencyCode).setParameter("bankName", bankName);
            List<QuomoCurrencyConfiguration> mopList = preQuery.getResultList();
            logger.info(new Gson().toJson(
                    "merchant mapping with currency result from Dao for findByCurrencyCodeAndMopName ::::" + mopList));
            return mopList;
        }
    }

    // Getting currency dropdown for TDR acquirer wise
    public List<String> findByAcquireAndPayId(String acquirer, String emailId) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT DISTINCT(t.currency) FROM TdrSetting t INNER JOIN User u WHERE u.payId=t.payId AND t.acquirerName=:acquirer AND t.status='ACTIVE' AND u.emailId=:emailId")
                    .setParameter("acquirer", acquirer).setParameter("emailId", emailId);
            List<String> mopList = preQuery.getResultList();
            logger.info(new Gson().toJson("Get Currency from TDR SETTING by findByAcquireAndPayId ::::" + mopList));
            return mopList;
        }
    }

    // Getting only those currency which is mapped
    public List<String> findCurrencyByPayId(String payId) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT DISTINCT currency FROM TdrSetting  WHERE payId=:payId AND tdrStatus='ACTIVE' AND status='ACTIVE'")
                    .setParameter("payId", payId);
            List<String> currencyList = preQuery.getResultList();
            logger.info(new Gson().toJson("Get Currency from TDR SETTING by findCurrencyByPayId ::::" + currencyList));
            return currencyList;
        }
    }

    // Getting only those currency which is mapped from TDR Payout
    public List<String> findCurrencyByPayIdForPO(String payId) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT DISTINCT currency FROM TdrSettingPayout  WHERE payId=:payId AND tdrStatus='ACTIVE' AND status='ACTIVE'")
                    .setParameter("payId", payId);
            List<String> currencyList = preQuery.getResultList();
            logger.info(new Gson().toJson("Get Currency from TDR SETTING by findCurrencyByPayId ::::" + currencyList));
            return currencyList;
        }
    }

    // Getting Payment Type(FRM) from TDR while setting FRM
    public List<String> findPaymentTypeByPayIdAndCurrency(String payId, String currency) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT DISTINCT paymentType FROM TdrSetting  WHERE payId=:payId AND currency=:currency AND tdrStatus='ACTIVE' AND status='ACTIVE'")
                    .setParameter("payId", payId).setParameter("currency", currency);
            List<String> paymentTypeList = preQuery.getResultList();
            logger.info(
                    new Gson().toJson("Get Currency from TDR SETTING by findCurrencyByPayId ::::" + paymentTypeList));
            return paymentTypeList;
        }
    }

    // Getting Payment Type(FRM) from TDR while Activating Merchant
    public List<Object[]> findPaymentTypeForFrm(String payId) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT currency,paymentType FROM TdrSetting  WHERE payId=:payId AND tdrStatus='ACTIVE' AND status='ACTIVE' GROUP BY currency, paymentType")
                    .setParameter("payId", payId);
            logger.info("Query for FRM ::" + preQuery + "  payId:: " + payId);
            List<Object[]> paymentTypeList = preQuery.getResultList();
            logger.info(new Gson().toJson(
                    "Get Currency & PaymentType from TDR SETTING by findPaymentTypeForFrm ::::" + paymentTypeList));
            return paymentTypeList;
        }
    }

    // Getting Data from TDR to check while Adding New MOP in TDR
    public List<TdrSetting> findDataForAddingNewMOP(String payId) {
        try (Session session = HibernateSessionProvider.getSession()) {
            Query preQuery = session
                    .createQuery("FROM TdrSetting WHERE payId=:payId AND tdrStatus='ACTIVE' AND status='ACTIVE'")
                    .setParameter("payId", payId);
            logger.info("Query ::" + preQuery + "  payId:: " + payId);
            List<TdrSetting> dataList = preQuery.getResultList();
//			logger.info(new Gson().toJson("Get Data from TDR SETTING by findDataForAddingNewMOP ::::" + dataList));
            return dataList;
        }
    }

    // Getting Mapped MOP List for Profile
    public List<String> findMappedMopForProfile(String payId, String currency) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT DISTINCT mopType FROM TdrSetting WHERE payId=:payId AND status='ACTIVE' AND tdrStatus='ACTIVE' AND currency=:currency")
                    .setParameter("payId", payId).setParameter("currency", currency);
            logger.info("Query ::" + preQuery + "  payId:: " + payId + " Currency");
            List<String> dataList = preQuery.getResultList();
            logger.info("MOP LIST findMappedMopForProfile :::" + dataList);
            return dataList;
        }
    }

    // Getting MOP from TDR for Profile Service Tab
    public List<Object[]> findMopByPayIdAndCurrency(String payId, String currency) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT paymentType,mopType FROM TdrSetting WHERE payId=:payId AND currency=:currency AND status='ACTIVE' AND tdrStatus='ACTIVE' GROUP BY paymentType,mopType")
                    .setParameter("payId", payId).setParameter("currency", currency);
            logger.info("Query for FRM ::" + preQuery + "  payId:: " + payId + " currency ::" + currency);
            List<Object[]> paymentTypeList = preQuery.getResultList();
            logger.info(new Gson().toJson(
                    "Get PaymentType & MopType from TDR SETTING by findMopByPayIdAndCurrency ::::" + paymentTypeList));
            return paymentTypeList;
        }
    }

    public List<String> findPayTypeByPayIdAndCurrency(String payId, String currency) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT paymentType FROM TdrSetting WHERE payId=:payId AND currency=:currency AND status='ACTIVE' AND tdrStatus='ACTIVE' GROUP BY paymentType,mopType")
                    .setParameter("payId", payId).setParameter("currency", currency);
            logger.info("Query for FRM ::" + preQuery + "  payId:: " + payId + " currency ::" + currency);
            List<String> paymentTypeList = preQuery.getResultList();
            logger.info(new Gson().toJson(
                    "Get PaymentType & MopType from TDR SETTING by findMopByPayIdAndCurrency ::::" + paymentTypeList));
            return paymentTypeList;
        }
    }

    public List<String> findPayTypeByPayIdAndCurrencyType(String payId, String currency) {
        try (Session session = HibernateSessionProvider.getSession()) {
            NativeQuery preQuery = session.createNativeQuery(
                            "SELECT DISTINCT(paymentType) FROM TdrSetting WHERE payId=:payId AND currency=:currency AND status='ACTIVE' AND tdrStatus='ACTIVE' GROUP BY paymentType,mopType")
                    .setParameter("payId", payId).setParameter("currency", currency);
            logger.info("Query for FRM ::" + preQuery + "  payId:: " + payId + " currency ::" + currency);
            List<String> paymentTypeList = preQuery.getResultList();
            logger.info(new Gson().toJson(
                    "Get PaymentType & MopType from TDR SETTING by findMopByPayIdAndCurrency ::::" + paymentTypeList));
            return paymentTypeList;
        }
    }

    public List<Object[]> findMopDetailsByPayIdCurrencyAndPayType(String payId, String currency, String payType) {
        try (Session session = HibernateSessionProvider.getSession()) {

//			String query = "SELECT DISTINCT(t.mopType),t.acquirerName, t.merchantTdr, t.merchantMinTdrAmt, t.merchantMaxTdrAmt, "
//					+ "t.merchantPreference,t.enableSurcharge ,t.igst FROM TdrSetting as t INNER JOIN RouterConfiguration as r "
//					+ "ON t.payId=r.merchant WHERE t.paymentType=:paymentType and r.merchant=:payId and t.currency=:currency and r.currentlyActive=1 and r.status='ACTIVE' and  r.status='ACTIVE' and t.status='ACTIVE' and t.tdrStatus='ACTIVE'";

            String query = "SELECT mopType,acquirerName, merchantTdr, merchantMinTdrAmt, merchantMaxTdrAmt," +
                    "merchantPreference,fromDate,enableSurcharge,igst FROM TdrSetting WHERE paymentType=:paymentType and payId=:payId and currency=:currency and status='ACTIVE' AND tdrStatus='ACTIVE'";

            NativeQuery preQuery = session.createNativeQuery(query)
                    .setParameter("payId", payId)
                    .setParameter("currency", currency)
                    .setParameter("paymentType", payType);
            logger.info("Query for FRM ::" + preQuery + "  payId:: " + payId + " currency ::" + currency
                    + " PaymentType" + payType);

            List<Object[]> paymentTypeList = preQuery.getResultList();
            session.close();
            // logger.info(new Gson().toJson("Get PaymentType & MopType from TDR SETTING by
            // findMopByPayIdAndCurrency ::::" + new Gson().toJson(paymentTypeList)));
            return paymentTypeList;
        }
    }

    public void resetTwoFactorAuth(String emailId, String payId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            logger.info("Update Email Id: " + emailId);
            session.createQuery(
                            "update User set google2FASecretkey='', tfa_flag=1 where emailId=:emailId and payId=:payId")
                    .setParameter("emailId", emailId).setParameter("payId", payId).executeUpdate();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
    }

    public List<Merchants> getSMAList() {
        List<Merchants> smaList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where U.userType = '" + UserType.SMA
                            + "' and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                smaList.add(merchant);
            }
            tx.commit();

            return smaList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return smaList;
    }

    public List<Merchants> getMAList() {
        List<Merchants> maList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where U.userType = '" + UserType.MA
                            + "' and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                maList.add(merchant);
            }
            tx.commit();

            return maList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return maList;
    }

    public List<Merchants> getAgentList() {
        List<Merchants> agentList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            List<Object[]> merchantListRaw = session
                    .createQuery("Select emailId, payId, businessName from User U where U.userType = '" + UserType.Agent
                            + "' and U.userStatus='" + UserStatusType.ACTIVE + "' order by businessName")
                    .getResultList();

            for (Object[] objects : merchantListRaw) {
                Merchants merchant = new Merchants();
                merchant.setEmailId((String) objects[0]);
                merchant.setPayId((String) objects[1]);
                merchant.setBusinessName((String) objects[2]);
                agentList.add(merchant);
            }
            tx.commit();

            return agentList;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return agentList;
    }

    public List<Merchants> getuserMappedConfDetails(String smaVal, String maVal, String agentVal, String merchantVal,
                                                    String userType) {
        logger.info("userType...={}", userType);

        UserType usertype = UserType.valueOf(userType);

        logger.info("usertype...={}", usertype);
        List<Merchants> resellerList = new ArrayList<Merchants>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        String query = "select payId, businessName, userStatus,registrationDate from User where ";
        if (StringUtils.isNotBlank(smaVal)) {
            query = StringUtils.join(query, "smaId='", smaVal, "'");
        }

        if (StringUtils.isNotBlank(maVal)) {
            query = StringUtils.join(query, " and maId='", maVal, "'");
        }

        if (StringUtils.isNotBlank(agentVal)) {
            query = StringUtils.join(query, " and AgentId='", agentVal, "'");
        }

        if (StringUtils.isNotBlank(merchantVal)) {
            query = StringUtils.join(query, " and payId='", merchantVal, "'");
        }
        query = query = StringUtils.join(query, " and userType='", usertype, "'", " and userStatus='", UserStatusType.ACTIVE, "'");
        logger.info("query..={}", query);

        try {
            List<Object[]> resellerListRaw = session
                    .createQuery(query)
                    .getResultList();

//					"Select  payId, businessName, userStatus,registrationDate from User U where "
//					+ "U.smaId = :smaVal or U.maId= :maVal or U.AgentId= :agentVal or  U.payId= :merchantVal and U.userType =:userType"
//							+" and U.userStatus='" + UserStatusType.ACTIVE + "'")

            for (Object[] objects : resellerListRaw) {
                Merchants reseller = new Merchants();
                // reseller.setEmailId((String) objects[0]);
                reseller.setPayId((String) objects[0]);
                reseller.setBusinessName((String) objects[1]);
                UserStatusType ust = (UserStatusType) objects[2];

                reseller.setStatus(ust.toString());
                Date date = (Date) objects[3];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String regiDate = sdf.format(date);
                reseller.setRegistrationDate(regiDate);

                resellerList.add(reseller);

            }
            logger.info("list based on usertype..={}", resellerListRaw.size());
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return resellerList;
    }

    public List<User> getAgentsById(String agentId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery("FROM User RC WHERE agentId=:agentId and RC.userType ='"
                    + UserType.MERCHANT + "' order by businessName").setParameter("agentId", agentId).getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;
    }

    public List<User> getMaById(String smaId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery(
                            "FROM User RC WHERE smaId=:smaId and RC.userType ='" + UserType.MA + "' order by businessName")
                    .setParameter("smaId", smaId).getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;
    }

    public List<User> getAgentbyId(String maId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> agentList = new ArrayList<User>();
        try {
            agentList = session.createQuery(
                            "FROM User RC WHERE maId=:maId and RC.userType ='"
                                    + UserType.Agent + "' order by businessName", User.class)
                    .setParameter("maId", maId).list();

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return agentList;
    }

    public List<User> getMerchantBySMAId(String smaId) {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery(
                            "FROM User RC WHERE smaId=:smaId and RC.userType ='" + UserType.MERCHANT + "' order by businessName")
                    .setParameter("smaId", smaId).getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;

    }

    public List<User> getMerchantByMAId(String maId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery(
                            "FROM User RC WHERE maId=:maId and RC.userType ='" + UserType.MERCHANT + "' order by businessName")
                    .setParameter("maId", maId).getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;
    }

    public List<User> getMerchantByAgentId(String agentId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery(
                            "FROM User RC WHERE agentId=:agentId and RC.userType ='" + UserType.MERCHANT + "' order by businessName")
                    .setParameter("agentId", agentId).getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;
    }

    public List<User> getResellersbyUserType(String userType) {
        UserType usertype = UserType.valueOf(userType);
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            chargingdata = session.createQuery(
                            "FROM User RC WHERE RC.userType =:usertype and userStatus='ACTIVE' order by businessName")
                    .setParameter("usertype", usertype).getResultList();
            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;
    }

    @SuppressWarnings("unchecked")
    public List<User> getResellersbyId(String userType, String resellerId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            if (userType.equalsIgnoreCase("SMA")) {
                chargingdata = session.createQuery("FROM User RC WHERE smaId=:resellerId and RC.userType ='"
                                + UserType.MERCHANT + "' and userStatus='" + UserStatusType.ACTIVE + "' order by businessName").setParameter("resellerId", resellerId)
                        .getResultList();
            }

            if (userType.equalsIgnoreCase("MA")) {
                chargingdata = session.createQuery("FROM User RC WHERE maId=:resellerId and RC.userType ='"
                                + UserType.MERCHANT + "' and userStatus='" + UserStatusType.ACTIVE + "' order by businessName").setParameter("resellerId", resellerId)
                        .getResultList();
            }

            if (userType.equals("Agent")) {
                chargingdata = session.createQuery("FROM User RC WHERE agentId=:resellerId and RC.userType ='"
                                + UserType.MERCHANT + "' and userStatus='" + UserStatusType.ACTIVE + "' order by businessName").setParameter("resellerId", resellerId)
                        .getResultList();
            }

            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;

    }

    public List<User> findByUserId(UserType userType, String payId) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<User> chargingdata = new ArrayList<User>();
        try {
            if (userType.name().equalsIgnoreCase("SMA")) {
                chargingdata = session.createQuery("FROM User RC WHERE smaId=:payId and RC.userType ='"
                                + UserType.SMA + "' and userStatus='" + UserStatusType.ACTIVE + "' order by businessName").setParameter("payId", payId)
                        .getResultList();
            }

            if (userType.name().equalsIgnoreCase("MA")) {
                chargingdata = session.createQuery("FROM User RC WHERE maId=:payId and RC.userType ='"
                                + UserType.MA + "' and userStatus='" + UserStatusType.ACTIVE + "' order by businessName").setParameter("payId", payId)
                        .getResultList();
            }

            if (userType.name().equals("Agent")) {
                chargingdata = session.createQuery("FROM User RC WHERE agentId=:payId and RC.userType ='"
                                + UserType.Agent + "' and userStatus='" + UserStatusType.ACTIVE + "' order by businessName").setParameter("payId", payId)
                        .getResultList();
            }

            tx.commit();
            return chargingdata;

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return chargingdata;
    }

    public List<User> getUserForAuditTrail() {
        Session session = HibernateSessionProvider.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        Join<User, UserGroup> userGroupJoin = userRoot.join("userGroup");

        Predicate groupNamePredicate = criteriaBuilder.or(
                criteriaBuilder.like(userGroupJoin.get("group"), "%Sub%"),
                criteriaBuilder.like(userGroupJoin.get("group"), "%Ope%")
        );

        Predicate userTypePredicate = criteriaBuilder.or(
                criteriaBuilder.equal(userRoot.get("userType"), UserType.ADMIN),
                criteriaBuilder.equal(userRoot.get("userType"), UserType.SUBADMIN)
        );

        criteriaQuery.select(userRoot)
                .where(criteriaBuilder.and(groupNamePredicate, userTypePredicate));

        return session.createQuery(criteriaQuery).getResultList();
    }

    public boolean checkPayIdExistOrNot(String payId) {
        logger.info("checkPayIdExistOrNot " + payId);
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            TypedQuery<User> query = session.createQuery("Select U from User U where U.payId = :payId", User.class);
            query.setParameter("payId", payId);
            List<User> results = query.getResultList();
            tx.commit();
            if (results.isEmpty()) {
                logger.info("checkPayIdExistOrNot, true, payId:" + payId);
                return true;
            } else {
                logger.info("checkPayIdExistOrNot, false, payId:" + payId);
                return false;
            }
        } catch (Exception e) {
            logger.info("Exception in checkPayIdExistOrNot" + e);
        } finally {
            autoClose(session);
        }
        return false;
    }
}
