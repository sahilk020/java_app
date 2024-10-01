package com.pay10.crm.action;

import com.pay10.commons.dao.MerchantGridViewService;
import com.pay10.commons.dao.SuccessRateThresholdSetting;
import com.pay10.commons.user.SuccessRateThresholdSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SchedularSettingAction extends AbstractSecureAction {

    @Autowired
    private MerchantGridViewService merchantGridViewService;

    @Autowired
    private SuccessRateThresholdSettingDao successRateThresholdSettingDao;

    @Autowired
    private DataEncoder encoder;
    @Autowired
    private CrmValidator validator;

    private static Logger logger = LoggerFactory.getLogger(MerchantGridViewAction.class.getName());
    private static final long serialVersionUID = 3293888841176590776L;
    private List<SuccessRateThresholdSetting> aaData;
    private User sessionUser = new User();
    private String businessType;
    private String acquirer;


    public String getAcquirer() {
        return this.acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    @Override
    public String execute() {
        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        try {
//            if(sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)){

                  if (acquirer.equals("ALL")) {
                      aaData = encoder.encodeSuccessRateThresholdSettingObj(successRateThresholdSettingDao.getAllSuccessThresholdSettings());
                  } else {
                      aaData = encoder.encodeSuccessRateThresholdSettingObj(successRateThresholdSettingDao.getAllSuccessThresholdSettingsOfAcquirer(getAcquirer()));
                  }
//            }
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }
    }


    //Show all seetingList in Dashbord
    public String listOfSettings(){
        try {
            aaData = aaData;
            return SUCCESS;
        } catch (Exception exception) {
            logger.error("Exception", exception);
            return ERROR;
        }
    }

    public List<SuccessRateThresholdSetting> getaaData() {
        return aaData;
    }

    public void setaaData(List<SuccessRateThresholdSetting> setaaData) {
        this.aaData = setaaData;
    }

}
