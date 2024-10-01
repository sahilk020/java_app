package com.pay10.notification.txnpush.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.notification.txnpush.pushtxnemailbuilder.NotificationDetail;
import com.pay10.notification.txnpush.pushtxnemailbuilder.PushTxnEmailBuilder;

@RestController
public class NotificationTxnPushEmailController {
	
	@Autowired
	private PushTxnEmailBuilder pushTxnEmailBuilder; 


	
	@RequestMapping(method = RequestMethod.POST, value = "/updateRequestNotificationEmail")
	public void updateRequestNotificationEmail(@RequestBody NotificationDetail notificationDetail ){
		pushTxnEmailBuilder.updateRequestNotification(notificationDetail);
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/updateApproveRejectNotificationServiceTaxEmail")
	public void  updateApproveRejectNotificationForServiceTaxEmail(@RequestBody NotificationDetail notificationDetail){
		pushTxnEmailBuilder.updateApproveRejectNotificationForServiceTax(notificationDetail);
	
	}

}