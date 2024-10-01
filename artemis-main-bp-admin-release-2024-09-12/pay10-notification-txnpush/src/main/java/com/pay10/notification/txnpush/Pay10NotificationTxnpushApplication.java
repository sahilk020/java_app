package com.pay10.notification.txnpush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.pay10.commons","com.pay10.notification.txnpush"})
public class Pay10NotificationTxnpushApplication {
	public static void main(String[] args) {
		SpringApplication.run(Pay10NotificationTxnpushApplication.class, args);
	}
}
