package com.pay10.crm.action;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MerchantSignupNotifier;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.BusinessType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.CreateNewUser;
import com.pay10.crm.util.PasswordDecryptor;

public class SingupMerchantAction extends AbstractSecureAction {
    private final static Logger logger = LoggerFactory.getLogger(SingupMerchantAction.class);

    @Autowired
    private EmailControllerServiceProvider emailControllerServiceProvider;

    @Autowired
    private CrmValidator validator;

    @Autowired
    private CreateNewUser createUser;

    @Autowired
    private PropertiesManager propertiesManager;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private AuditTrailService auditTrailService;

    @Autowired
    private PasswordDecryptor passwordDecryptor;
    private static final long serialVersionUID = 5995449017764989418L;
    private String aliasName;//deepak
    private String emailId;
    private String password;
    private String businessName;
    private String mobile;
    private String confirmPassword;
    private String userRoleType;
    private String industryCategory;
    private String industrySubCategory;
    private ResponseObject responseObject = new ResponseObject();
    private Map<String, String> industryCategoryList = new TreeMap<String, String>();
    private Map<String, String> industrySubCategoryList = new TreeMap<String, String>();
    private long roleId;
    private List<Role> roles;
    private long userGroupId;
    private List<UserGroup> userGroups;
    private String segmentName;

    @SuppressWarnings("unchecked")
    @Override
    public String execute() {

        logger.info("@@@ Executing SingupMerchantAction");

        logger.info("@@@ Executing SingupMerchantAction Parameters  Alias Name: {}, Email ID: {}, Password: {}, Confirm Password: {}, Business Name: {}, Mobile: {}, User Role Type: {}, Industry Category: {}, Industry Sub Category: {}, Role ID: {}, Segment Name: {}, User Group ID: {}, Roles: {}, User Groups: {}",
                aliasName, emailId, password, confirmPassword, businessName, mobile, userRoleType, industryCategory, industrySubCategory, roleId, segmentName, userGroupId,
                roles != null ? roles.stream().map(Role::getRoleName).collect(Collectors.joining(", ")) : "None",
                userGroups != null ? userGroups.stream().map(UserGroup::getGroup).collect(Collectors.joining(", ")) : "None");

        if (StringUtils.isNotBlank(getEmailId())) {

            try {
                setEmailId(getEmailId().toLowerCase());

                User user = getUserInstance();
                if (userRoleType.equals(CrmFieldConstants.USER_RESELLER_TYPE.getValue())) {
                    responseObject = createUser.createUser(user, UserType.RESELLER, "");
                } else {
                    responseObject = createUser.createUser(user, UserType.MERCHANT, "");
                }

                Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
                        .get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
                String action = userRoleType.equals(CrmFieldConstants.USER_RESELLER_TYPE.getValue()) ? "signupReseller" : "signupMerchant";

                AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), null, actionMessagesByAction.get(action));
                auditTrailService.saveAudit(request, auditTrail);

                if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
                    addActionMessage(responseObject.getResponseMessage());
                }
                if (ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
                    responseObject.setResponseMessage("User successfully registered");
                    addActionMessage(responseObject.getResponseMessage());

                    // Sending Email for Email Validation

                    //vijay...............................merchant registration done verify email.................................................
                   // emailControllerServiceProvider.emailValidator(responseObject);

                    // Sending notification email to Admin on merchant signup
                    if (PropertiesManager.propertiesMap.get("sendEmailOnSignup") != null
                            && PropertiesManager.propertiesMap.get("sendEmailOnSignupList") != null
                            && PropertiesManager.propertiesMap.get("sendEmailOnSignup").equalsIgnoreCase("Y")) {

                        String emailList = PropertiesManager.propertiesMap.get("sendEmailOnSignupList");
                        User merchant = getUserInstance();

                        if (emailList.length() > 0 && merchant != null) {
                            String emailListArray[] = emailList.split(",");

                            for (String email : emailListArray) {

                                MerchantSignupNotifier merchantSignupNotifier = new MerchantSignupNotifier();
                                merchantSignupNotifier.setMerchantEmail(merchant.getEmailId());
                                merchantSignupNotifier.setMerchantName(merchant.getBusinessName());
                                merchantSignupNotifier.setMerchantPhone(merchant.getMobile());
                                merchantSignupNotifier.setEmail(email.trim());
                                merchantSignupNotifier.setReceiverName(email.trim());
                                emailControllerServiceProvider.merchantSignupNotifier(merchantSignupNotifier);

                            }

                        }

                    }
                }
                return SUCCESS;
            } catch (Exception exception) {
                logger.error("Exception", exception);
                return ERROR;
            }
        } else {
            return INPUT;
        }
    }

    private User getUserInstance() {
        User user = new User();
        user.setAliasName(getAliasName());//deepak
        user.setEmailId(getEmailId());
        user.setPassword(getPassword());
        user.setMobile(getMobile());
        user.setBusinessName(getBusinessName());
        user.setPasswordExpired(false); // Set password expiry to false by default.
        if (userRoleType.equals(CrmFieldConstants.USER_RESELLER_TYPE.getValue())) {
        } else {
            user.setIndustryCategory(industryCategory);
            user.setIndustrySubCategory(industrySubCategory);
        }
        user.setRole(roleDao.getRole(getRoleId()));
        user.setSegment(segmentName);
        user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
        return user;
    }

    @Override
    public void validate() {
        String userType = userRoleType;
        logger.info("@@@ Executing SingupMerchantAction validate() userType : {} ", userType);

        // Changes By Pritam Ray
        if (validator.validateBlankField(getBusinessName())) {
            addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
        } else if(!validator.validateBusinessName(getBusinessName())) {
            addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
        }

        if (validator.validateBlankField(getEmailId())) {
            addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
        } else if (!(validator.isValidEmailId(getEmailId()))) {
            addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
        }
        if (!(userType.equalsIgnoreCase(UserType.RESELLER.toString()))) {
            if (validator.validateBlankField(getIndustryCategory())) {
                addFieldError(CrmFieldType.INDUSTRY_CATEGORY.getName(),
                        validator.getResonseObject().getResponseMessage());
            } else if (!(validator.validateField(CrmFieldType.INDUSTRY_CATEGORY, getIndustryCategory()))) {
                addFieldError(CrmFieldType.INDUSTRY_CATEGORY.getName(),
                        ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
            }
            if (validator.validateBlankField(getIndustrySubCategory())) {
                addFieldError(CrmFieldType.INDUSTRY_SUB_CATEGORY.getName(),
                        validator.getResonseObject().getResponseMessage());
            } else if (!(validator.validateField(CrmFieldType.INDUSTRY_SUB_CATEGORY, getIndustrySubCategory()))) {
                addFieldError(CrmFieldType.INDUSTRY_SUB_CATEGORY.getName(),
                        ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
            }
        }
        Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();
        industryCategoryList.putAll(industryCategoryLinkedMap);
        Map<String, String> industryCategoryLinkedMap1 = BusinessType.getIndustryCategoryList();
        industrySubCategoryList.putAll(industryCategoryLinkedMap1);
        setRoles(roleDao.getActiveRoles());
        List<UserGroup> groups = userGroupDao.getUserGroups();
        groups = groups.stream().filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller")).collect(Collectors.toList());
        setUserGroups(groups);
    }

    public Map<String, String> getIndustrySubCategoryList() {
        return industrySubCategoryList;
    }

    public void setIndustrySubCategoryList(Map<String, String> industrySubCategoryList) {
        this.industrySubCategoryList = industrySubCategoryList;
    }

    public Map<String, String> getIndustryCategoryList() {
        return industryCategoryList;
    }

    public void setIndustryCategoryList(Map<String, String> industryCategoryList) {
        this.industryCategoryList = industryCategoryList;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getIndustryCategory() {
        return industryCategory;
    }

    public void setIndustryCategory(String industryCategory) {
        this.industryCategory = industryCategory;
    }

    public String getIndustrySubCategory() {
        return industrySubCategory;
    }

    public void setIndustrySubCategory(String industrySubCategory) {
        this.industrySubCategory = industrySubCategory;
    }

    public String getUserRoleType() {
        return userRoleType;
    }

    public void setUserRoleType(String userRoleType) {
        this.userRoleType = userRoleType;
    }

    public ResponseObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(ResponseObject responseObject) {
        this.responseObject = responseObject;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(long userGroupId) {
        this.userGroupId = userGroupId;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
