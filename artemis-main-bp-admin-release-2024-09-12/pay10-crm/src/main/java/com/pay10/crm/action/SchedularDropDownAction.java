package com.pay10.crm.action;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pay10.commons.util.MopType.getGetMopsFromSystemProp;
import static com.pay10.commons.util.PaymentType.getGetPaymentsFromSystemProp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class SchedularDropDownAction extends AbstractSecureAction {

    private static Logger logger = LoggerFactory.getLogger(SchedularChangeSettingAction.class.getName());

    private InputStream paymentMethodStream;
    private InputStream mopTypeStream;
    private String acquirerName;
    private String paymentType;



    private String mopType;
    private String dropDown;
    private User sessionUser = new User();

    @Override
    public String execute() {

        sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        try {
            if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)) {

                if(dropDown.equals("PaymentTypeDropDown")) {
                    try {
                        paymentMethodStream = new ByteArrayInputStream(
                                getPaymentMethods(acquirerName)
                                        .getBytes("UTF-8"));
                    } catch (Exception e) {
                        paymentMethodStream = new ByteArrayInputStream("None"
                                .getBytes("UTF-8"));
                        e.printStackTrace();
                    }
                }
                else if(dropDown.equals("MopTypeDropDown")){
                    try {
                        mopTypeStream = new ByteArrayInputStream(
                                getMopTypes(acquirerName,paymentType)
                                        .getBytes("UTF-8"));
                    } catch (Exception e) {
                        mopTypeStream = new ByteArrayInputStream("None"
                                .getBytes("UTF-8"));
                        e.printStackTrace();
                    }
                }

            }
            return SUCCESS;
        }catch (Exception exception) {
            logger.error("Exception", exception);
            JSONObject object = new JSONObject();
//            return object.toString();
            return ERROR;
        }
    }

    public String getAcquirerName() {
        return acquirerName;
    }

    public void setAcquirerName(String acquirerName) {
        this.acquirerName = acquirerName;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getMopType() {
        return mopType;
    }

    public void setMopType(String mopType) {
        this.mopType = mopType;
    }

    public InputStream getPaymentMethodStream() {
        return paymentMethodStream;
    }

    public InputStream getMopTypeStream() {
        return mopTypeStream;
    }

    public void setMopTypeStream(InputStream mopTypeStream) {
        this.mopTypeStream = mopTypeStream;
    }

    public String getDropDown() {
        return dropDown;
    }

    public void setDropDown(String dropDown) {
        this.dropDown = dropDown;
    }


    public String getPaymentMethods(String acquirerName){
        StringBuilder sb = new StringBuilder();
        List<PaymentType> paymentTypesList = getGetPaymentsFromSystemProp(acquirerName);

        for(PaymentType paymentType: paymentTypesList){
            sb.append(paymentType.getCode()).append(",").append(paymentType.getName()).append(",");
        }
        return sb.toString();
    }

    public String getMopTypes(String acquirerName, String paymentType) {
        String finalStr = acquirerName + paymentType + "MOP";
        List<MopType> mopTypeList = getGetMopsFromSystemProp(finalStr);
        StringBuilder sb = new StringBuilder();
        for (MopType mopType : mopTypeList) {
            sb.append(mopType.getCode()).append(",").append(mopType.getName()).append(",");
        }
        return sb.toString();

    }

}
