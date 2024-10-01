package com.pay10.commons.user;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

@Component
public class LoginHistoryDao extends HibernateAbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(LoginHistoryDao.class);

    private LoginHistory loginHistory = new LoginHistory();

    private Boolean isValid = true;
    private String validOperatingSystem;
    private String validBrowser;
    private String validIp;
    private CrmValidator validator = new CrmValidator();

    @Autowired
    private UserDao userDao;

    public LoginHistoryDao() {
        super();
    }

    public void create(LoginHistory loginhistory) throws DataAccessLayerException {
        super.save(loginhistory);
    }

    public void delete(LoginHistory loginhistory) throws DataAccessLayerException {
        super.delete(loginhistory);
    }

    public LoginHistory find(Long id) throws DataAccessLayerException {
        return (LoginHistory) super.find(LoginHistory.class, id);
    }

    public LoginHistory find(String name) throws DataAccessLayerException {
        return (LoginHistory) super.find(LoginHistory.class, name);
    }

    public List<LoginHistory> findAll(int draw, int length, int startFrom) throws DataAccessLayerException {
        List<LoginHistory> loginhistoryList = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {

            String sqlQuery = "select * from Login_History lh left join User su on lh.emailId = su.emailId where su.userType <> 'POSMERCHANT' order by Id desc  Limit "
                    + startFrom + "," + length + "";
            loginhistoryList = session.createNativeQuery(sqlQuery, LoginHistory.class).getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return loginhistoryList;
    }

    public List<LoginHistory> findAllUsers(String merchantPayId) throws DataAccessLayerException {
        List<LoginHistory> loginhistoryList = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where su.parentPayid = :parentPayid and su.userType <> '"
                    + UserType.POSMERCHANT + "' ORDER BY ID DESC";
            loginhistoryList = session.createNativeQuery(sqlQuery, LoginHistory.class)
                    .setParameter("parentPayid", merchantPayId).getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }

        return loginhistoryList;
    }

    public List<LoginHistory> findByParentPayId(String payId) {
        List<LoginHistory> loginhistoryList = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "SELECT * FROM Login_History where emailid in (select emailId from User where  parentPayId=:payId ) order by id desc";
            loginhistoryList = session.createNativeQuery(sqlQuery, LoginHistory.class)
                    .setParameter("payId", payId).getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }

        return loginhistoryList;

    }

    public List<LoginHistory> findLoginHisAllSubUser(String emailId1, String parentPayId) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId and su.parentPayid = :parentPayId and su.userType = '"
                    + UserType.SUBUSER + "' ORDER BY ID DESC";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .setParameter("parentPayId", parentPayId).getResultList();
            tx.commit();
            return userLoginHistory;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }

    public List<LoginHistory> findLoginHisUser(String emailId1, UserType userType, int draw, int length, int startFrom,
                                               String firstName, String lastName) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId and su.userType = '"
                    + userType + "' ORDER BY ID DESC LIMIT " + startFrom + " ," + length + "";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .getResultList();
            tx.commit();
            for (LoginHistory lh : userLoginHistory) {
                lh.setBusinessName(firstName + " " + lastName);
            }
            return userLoginHistory;
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }

        return userLoginHistory;
    }

    public void update(LoginHistory loginhistory) throws DataAccessLayerException {
        super.saveOrUpdate(loginhistory);
    }

    public LoginHistory findLastLoginByUser(String emailId) throws DataAccessLayerException { // second last because
        // last is the current
        // attempt which is
        // logged in
        LoginHistory responseloginHistory = new LoginHistory();

        List<LoginHistory> userLoginHistory = findLoginHisUser(emailId);
        // To get second result
        int counter = 0;
        for (LoginHistory loginHistory : userLoginHistory) {
            if (counter == 1) {
                responseloginHistory = loginHistory;
                break;
            }
            counter++;
        }
        return responseloginHistory;
    }


    protected List<LoginHistory> findLoginHisUser(String emailId1) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId and su.userType = '"
                    + UserType.MERCHANT + "' ORDER BY ID DESC";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }

    public List<LoginHistory> findLoginHisByUser(String emailId1, int draw, int length, int startFrom)
            throws DataAccessLayerException {
        return findLoginHisUserBpGate(emailId1, draw, length, startFrom);
    }

    public List<LoginHistory> findByEmailId(String emailId) {
        return findByEmailId(emailId);
    }

    protected List<LoginHistory> findLoginHisUser(String emailId1, int draw, int length, int startFrom) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId  and su.userType = '"
                    + UserType.MERCHANT + "'  ORDER BY ID DESC LIMIT " + startFrom + " ," + length + "";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }


    //Added by Deep Singh code start here
    public BigInteger countTotalFindLoginHisByUserBpGate(String email) throws DataAccessLayerException {
        BigInteger total = null;
        try {
//				String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId='"
//						+ email + "' and su.userType = '" + UserType.MERCHANT + "'  ORDER BY ID DESC ";
            User userDB = userDao.findByEmailId(email);
            System.out.println("Merchant PayID : " + userDB.getPayId());
            String sqlQuery = "select count(*) from Login_History where emailid in (select emailId from User where (usertype='" + UserType.SUBUSER + "' or userType='" + UserType.MERCHANT + "') and (parentPayId='" + userDB.getPayId() + "' or payId='" + userDB.getPayId() + "')) order by id desc";

            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;
    }

    protected List<LoginHistory> findLoginHisUserBpGate(String emailId1, int draw, int length, int startFrom) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
//				String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId  and su.userType = '"
//						+ UserType.MERCHANT + "'  ORDER BY ID DESC LIMIT " + startFrom + " ," + length + "";
            User userDB = userDao.findByEmailId(emailId1);
            System.out.println("Merchant PayID : " + userDB.getPayId());
            String sqlQuery = "select * from Login_History where emailid in (select emailId from User where (usertype='" + UserType.SUBUSER + "' or userType='" + UserType.MERCHANT + "') and (parentPayId='" + userDB.getPayId() + "' or payId='" + userDB.getPayId() + "')) order by id desc limit " + startFrom + "," + length;
            System.out.println("Final Query : " + sqlQuery);
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }

    //Added by Deep Singh code end here

    public List<LoginHistory> findLoginUserOrSubuser(String emailId1, String merchantPayID, int draw, int length,
                                                     int startFrom) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where (lh.emailId=:emailId or su.parentPayId=:merchantPayID)  and (su.userType = '"
                    + UserType.MERCHANT + "' or su.userType = '" + UserType.SUBUSER + "') ORDER BY ID DESC LIMIT "
                    + startFrom + " ," + length + "";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .setParameter("merchantPayID", merchantPayID).getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }

    public List<LoginHistory> findLoginHisByAgent(String emailId1, int draw, int length, int startFrom)
            throws DataAccessLayerException {
        return findLoginHisAgent(emailId1, draw, length, startFrom);
    }

    protected List<LoginHistory> findLoginHisAgent(String emailId1, int draw, int length, int startFrom) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId  and su.userType = '"
                    + UserType.AGENT + "'  ORDER BY ID DESC LIMIT " + startFrom + " ," + length + "";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }

    public List<LoginHistory> findLoginSubAdmin(String emailId1, int draw, int length, int startFrom, String firstName,
                                                String lastName) throws DataAccessLayerException {
        return findLoginHistorySubAdmin(emailId1, draw, length, startFrom, firstName, lastName);
    }

    protected List<LoginHistory> findLoginHistorySubAdmin(String emailId1, int draw, int length, int startFrom,
                                                          String firstName, String lastName) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId  and su.userType = '"
                    + UserType.SUBADMIN + "'  ORDER BY ID DESC LIMIT " + startFrom + " ," + length + "";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId1)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }

        for (LoginHistory lh : userLoginHistory) {
            lh.setBusinessName(firstName + " " + lastName);
        }
        return userLoginHistory;
    }

    public List<LoginHistory> getLoginHistoryByEmailId(String emailId) {
        List<LoginHistory> loginHistories = new ArrayList<>();
        try (Session session = HibernateSessionProvider.getSession()) {
            String query = "SELECT * FROM  Login_History WHERE emailId=" + emailId + " ORDER BY timeStamp DESC";
            loginHistories = session.createNativeQuery(query, LoginHistory.class).getResultList();
        }
        return loginHistories;
    }

    public void saveLoginDetails(String request, Boolean status, User user, String ip, String message) {

        logger.info("Executing saveLoginDetails with request: {}, status: {}, user: {}, ip: {}, message: {}",
                request, status, user.getEmailId(), ip, message);

        Date date = new Date();
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        ReadableUserAgent agent = parser.parse(request);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);

        String browserName = getBrowserName(request);

        logger.info("Executing Save LoginDetails BrowserName : {} ", browserName);

        validateIp(ip);
        loginHistory.setIp(getValidIp());

        validateBrowser(agent.getName());
        loginHistory.setBrowser(browserName);

        validateOS(agent.getOperatingSystem().getName());
        loginHistory.setOs(getValidOperatingSystem());

        loginHistory.setBusinessName(user.getBusinessName());
        loginHistory.setEmailId(user.getEmailId());
        loginHistory.setTimeStamp(formattedDate);
        loginHistory.setStatus(status);
        loginHistory.setFailureReason(message);
        create(loginHistory);

    }

    public static String getBrowserName(String userAgent) {
        logger.info("Executing getBrowserName with userAgent: {}", userAgent);

        if (userAgent == null) {
            logger.warn("User agent is null, returning Unknown");
            return "Unknown";
        }

        String browserName;
        if (userAgent.contains("Edg")) {
            browserName = "Edge";
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            browserName = "Chrome";
        } else if (userAgent.contains("Firefox")) {
            browserName = "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            browserName = "Safari";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            browserName = "Internet Explorer";
        } else {
            browserName = "Unknown";
        }

        logger.debug("Detected browser name: {}", browserName);
        return browserName;
    }

    public BigInteger countTotalAdmin() throws DataAccessLayerException {
        BigInteger total = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = "select count(*)  from Login_History lh left join User su on lh.emailId = su.emailId where su.userType <> 'POSMERCHANT'";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return total;

    }

    public BigInteger countTotalAdminByUser() throws DataAccessLayerException {
        BigInteger total = null;

        try {
            String sqlQuery = "select count(*)  from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId and su.userType = '"
                    + UserType.MERCHANT + "' ORDER BY ID DESC";
            countTotal(sqlQuery);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public BigInteger countTotalFindLoginHisByUser(String email) throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId='"
                    + email + "' and su.userType = '" + UserType.MERCHANT + "'  ORDER BY ID DESC ";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    // Count total Merchant and SubUser from LoginHistory
    public BigInteger countTotalFindLoginByUserOrSubuser(String email, String merchantPayID)
            throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where (lh.emailId='"
                    + email + "' or su.parentPayId='" + merchantPayID + "') and (su.userType = '" + UserType.MERCHANT
                    + "'  or su.userType = '" + UserType.SUBUSER + "') ORDER BY ID DESC ";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public BigInteger countTotalFindLoginHisByAgent(String email) throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId='"
                    + email + "' and su.userType = '" + UserType.AGENT + "'  ORDER BY ID DESC ";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public BigInteger countTotalfindLoginHisUser(UserType userType, String emailId) throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select Count(*) from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId='"
                    + emailId + "'and su.userType = '" + userType + "' ORDER BY ID DESC";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public BigInteger countTotalfindResellerAll(String resellerId) throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where su.userType <> 'POSMERCHANT' and su.resellerId = '"
                    + resellerId + "' ORDER BY ID DESC";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public BigInteger countTotalfindLoginHisByUser(String email) throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId='"
                    + email + "' and su.userType = '" + UserType.MERCHANT + "'  ORDER BY ID DESC ";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public BigInteger countTotal(String getQuery) throws DataAccessLayerException {
        BigInteger total = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {

            String sqlQuery = getQuery;
            total = (BigInteger) session.createNativeQuery(sqlQuery).getSingleResult();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return total;

    }

    public void validateIp(String value) {

        if (validator.validateField(CrmFieldType.IP, value)) {
            setValidIp(value);
        } else {
            setValidIp("unknown");
        }
    }

    public void validateBrowser(String value) {

        if (validator.validateField(CrmFieldType.BROWSER, value)) {
            setValidBrowser(value);
        } else {
            setValidBrowser("unknown");
        }

    }

    public void validateOS(String value) {

        if (validator.validateField(CrmFieldType.OPERATINGSYSTEM, value)) {
            setValidOperatingSystem(value);
        } else {
            setValidOperatingSystem("unknown");
        }

    }

    public List<LoginHistory> findResellerAll(String resellerId, int draw, int length, int startFrom)
            throws DataAccessLayerException {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<LoginHistory> loginhistoryList = new ArrayList<LoginHistory>();
        try {
            String sqlQuery = "select * from Login_History lh left join User su on lh.emailId = su.emailId where su.userType <> 'POSMERCHANT' and su.resellerId = '"
                    + resellerId + "' LIMIT " + startFrom + "," + length + "";
            loginhistoryList = session.createNativeQuery(sqlQuery, LoginHistory.class).getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }

        return loginhistoryList;
    }

    public int findLastPasswordCreateDate(String email) throws DataAccessLayerException {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<UserRecords> UserRecords = new ArrayList<UserRecords>();
        int response = 0;
        try {

            String sqlQuery = "SELECT * FROM User_Records WHERE emailId='" + email
                    + "'ORDER BY createDate DESC LIMIT 1";
            UserRecords = session.createNativeQuery(sqlQuery, UserRecords.class).getResultList();
            tx.commit();

            // Check added by shaiwal for the cases where no record is found in database
            if (UserRecords.size() < 1) {
                return response;
            }
            Timestamp CreateDateTimeStamp = (Timestamp) UserRecords.get(0).getCreateDate();
            java.sql.Timestamp timestamp1 = java.sql.Timestamp.valueOf(CreateDateTimeStamp.toString());
            long longtimestamp = timestamp1.getTime();
            java.util.Date sqlToDte = new java.sql.Date(longtimestamp);
            java.util.Date sqlFromDte = new java.sql.Date(System.currentTimeMillis());

            String dateFrom = sqlFromDte.toString();
            String dateTo = sqlToDte.toString();

            String[] splitdateFroms = dateFrom.split("-");
            String strDateFrom = splitdateFroms[2] + "-" + splitdateFroms[1] + "-" + splitdateFroms[0];
            String[] splitdateTos = dateTo.split("-");
            String strDatesTo = splitdateTos[2] + "-" + splitdateTos[1] + "-" + splitdateTos[0];

            Long Datediff = DateCreater.diffDate(strDatesTo, strDateFrom);
            response = Datediff.intValue();

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return response;
    }

    public LoginHistory findLastLoginHistory(String email) throws DataAccessLayerException {
        LoginHistory loginHistory = null;
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        try {
            String sqlQuery = "SELECT * FROM Login_History WHERE emailId='" + email
                    + "' ORDER BY timeStamp DESC LIMIT 1";
            loginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).getSingleResult();
            tx.commit();


        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return loginHistory;

    }

    public String findLastLoginRecord(String email) throws DataAccessLayerException {
        List<LoginHistory> loginhistoryList = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        String failReason = "no fail reason";

        try {
            String sqlQuery = "SELECT * FROM Login_History WHERE emailId='" + email
                    + "' ORDER BY timeStamp DESC LIMIT 3";
            loginhistoryList = session.createNativeQuery(sqlQuery, LoginHistory.class).getResultList();
            tx.commit();

            if (loginhistoryList.size() < 3) {
                return "no fail reason";
            }
            failReason = loginhistoryList.get(0).getFailureReason();
            if (failReason != null) {
                return failReason;
            }
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return failReason;

    }

    public String findLastLoginHisByEmail(String email, ResponseObject responseObject) throws DataAccessLayerException {
        List<LoginHistory> loginhistoryList = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        String status = "";
        String response = "fail";

        String msg = responseObject.getResponseMessage();
        String rspMsg = ErrorType.USER_PASSWORD_INCORRECT.getResponseMessage();
        String pwdWrongMsg = ErrorType.ATTEMPT_MESSAGE.getResponseMessage();

        try {
            String sqlQuery = "SELECT * FROM Login_History WHERE emailId='" + email
                    + "' ORDER BY timeStamp DESC LIMIT 3";
            loginhistoryList = session.createNativeQuery(sqlQuery, LoginHistory.class).getResultList();
            tx.commit();

            if (loginhistoryList.size() < 3) {
                return response;
            }

            Timestamp timestamp1 = Timestamp.valueOf(loginhistoryList.get(0).getTimeStamp());
            java.util.Date date = new java.util.Date();
            Timestamp timestamp2 = new Timestamp(date.getTime());
            int diffMinutesInTimes = DateCreater.diffMinutes(timestamp1, timestamp2);

            String failReason = loginhistoryList.get(0).getFailureReason();
            if (failReason != null) {
                if (failReason.equals("Account Lock")) {
                    if (diffMinutesInTimes <= 30) {
                        int timePending = 30;
                        int leftTime = timePending - diffMinutesInTimes;

                        response = "Your account has been temporarily locked, please try again after " + Integer.toString(leftTime) + " minute";

                        return response;
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                if (loginhistoryList.get(i).isStatus() == true) {
                    status = "limit";
                    break;
                } else {
                    status = "limitOver";

                }
            }

            if (status.equals("limitOver") && msg != null) {

                if (diffMinutesInTimes <= 30) {
                    //int timePending = 30;
                    //int leftTime = timePending - diffMinutesInTimes;
//					response = "Your account has been Temporarily locked for " + Integer.toString(leftTime)
//							+ " minutes";
                    response = "Your account has been temporarily locked, please try again after 30 minute";


                    return response;
                } else {
                    response = pwdWrongMsg;
                    return response;
                }

            }

            if (response.equals("fail") && msg == rspMsg) {
                if (diffMinutesInTimes > 30) {
                    response = pwdWrongMsg;
                    return response;
                }
                String dbLastFailureReason = loginhistoryList.get(0).getFailureReason();
                if (dbLastFailureReason != null) {
                    try {
                        String[] dbLastFailureReasons = dbLastFailureReason.split("!");
                        String[] arrOfStrs = dbLastFailureReasons[1].split(" ");
                        int count = Integer.parseInt(arrOfStrs[3]);
                        response = "Incorrect Username or Password! You have " + Integer.toString(--count)
                                + " chances left";
                    } catch (Exception e) {
                        response = pwdWrongMsg;
                    }

                } else {
                    response = pwdWrongMsg;
                }
            }

        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return response;

    }

    //Added by Sweety
    public BigInteger countTotalfindLoginHisByReseller(String email) throws DataAccessLayerException {
        BigInteger total = null;
        try {
            String sqlQuery = "select count(*) from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId='"
                    + email + "' and su.userType = '" + UserType.RESELLER + "'  ORDER BY ID DESC ";
            total = countTotal(sqlQuery);
        } catch (ObjectNotFoundException objectNotFound) {
            // handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            // handleException(hibernateException,tx);
        } finally {
            // autoClose(session);
        }
        return total;

    }

    public List<LoginHistory> findLoginHisByReseller(String emailId, int draw, int length, int start) {
        List<LoginHistory> userLoginHistory = new ArrayList<LoginHistory>();
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            String sqlQuery = "select lh.* from Login_History lh left join User su on lh.emailId = su.emailId where lh.emailId=:emailId  and su.userType = '"
                    + UserType.RESELLER + "'  ORDER BY ID DESC LIMIT " + start + " ," + length + "";
            userLoginHistory = session.createNativeQuery(sqlQuery, LoginHistory.class).setParameter("emailId", emailId)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound, tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException, tx);
        } finally {
            autoClose(session);
        }
        return userLoginHistory;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public String getValidOperatingSystem() {
        return validOperatingSystem;
    }

    public void setValidOperatingSystem(String validOperatingSystem) {
        this.validOperatingSystem = validOperatingSystem;
    }

    public String getValidBrowser() {
        return validBrowser;
    }

    public void setValidBrowser(String validBrowser) {
        this.validBrowser = validBrowser;
    }

    public String getValidIp() {
        return validIp;
    }

    public void setValidIp(String validIp) {
        this.validIp = validIp;
    }

}
