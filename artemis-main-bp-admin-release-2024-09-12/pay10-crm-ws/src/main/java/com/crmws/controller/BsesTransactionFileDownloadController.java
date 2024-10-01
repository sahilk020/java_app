package com.crmws.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.entity.ResponseMessage;
import com.crmws.service.impl.BsesTransactionFileDownloadServiceImpl;
import com.pay10.commons.entity.TdrWaveOffSettingEntity;

@RestController
@RequestMapping("/Bses")
public class BsesTransactionFileDownloadController {
	static int flag=0;
	@Autowired
	private BsesTransactionFileDownloadServiceImpl bsesTransactionFileDownloadServiceImpl;
	
	@GetMapping("/downloadTransactionWiseReport")
	public ResponseEntity<ResponseMessage> getDownloadTransactionWiseReport(){
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			String ip=localhost.getHostAddress().trim();
			
			List<String>ipMatcher =new ArrayList<>();
			ipMatcher.add("10.0.1.169");
			ipMatcher.add("127.0.0.1");
			ipMatcher.add("192.168.1.3");
			ipMatcher.add("10.0.1.170");
			ipMatcher.add("192.168.1.6");
			ipMatcher.add("136.232.148.174");
			ipMatcher.add("146.196.32.137");
			System.out.println(ip);
			ipMatcher.stream().forEach(t->{
				if (t.equalsIgnoreCase(ip)) {
					flag=1;
				}
			});
			
			if (flag==1) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(bsesTransactionFileDownloadServiceImpl.getDownloadTransactionWiseReport(), HttpStatus.OK)));
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("IP Not Match Please Contact to Admin", HttpStatus.OK)));
		
	}
	@GetMapping("/downloadMisReport")
	public ResponseEntity<ResponseMessage> downloadMisReport(){
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			String ip=localhost.getHostAddress().trim();
			System.out.println(ip);
			List<String>ipMatcher =new ArrayList<>();
			ipMatcher.add("10.0.1.169");
			ipMatcher.add("127.0.0.1");
			ipMatcher.add("192.168.1.3");
			ipMatcher.add("10.0.1.170");
			ipMatcher.add("192.168.1.6");
			ipMatcher.add("136.232.148.174");
			ipMatcher.add("146.196.32.137");
			
			ipMatcher.stream().forEach(t->{
				if (t.equalsIgnoreCase(ip)) {
					flag=1;
				}
			});
			
			if (flag==1) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(bsesTransactionFileDownloadServiceImpl.downloadMisReport(), HttpStatus.OK)));
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("IP Not Match Please Contact to Admin", HttpStatus.OK)));
		
	}
	@GetMapping("/downloadTxtFile")
	public ResponseEntity<ResponseMessage> downloadTxtFile(){
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			String ip=localhost.getHostAddress().trim();
			
			List<String>ipMatcher =new ArrayList<>();
			ipMatcher.add("10.0.1.169");
			ipMatcher.add("127.0.0.1");
			ipMatcher.add("192.168.1.3");
			ipMatcher.add("10.0.1.170");
			ipMatcher.add("136.232.148.174");
			ipMatcher.add("146.196.32.137");
			System.out.println(ip);
			
			ipMatcher.stream().forEach(t->{
				if (t.equalsIgnoreCase(ip)) {
					flag=1;
				}
			});
			
			if (flag==1) {
				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(bsesTransactionFileDownloadServiceImpl.downloadTxtFile(), HttpStatus.OK)));
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("IP Not Match Please Contact to Admin", HttpStatus.OK)));
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseMessage> save(@RequestBody TdrWaveOffSettingEntity entity){
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(bsesTransactionFileDownloadServiceImpl.save(entity), HttpStatus.OK)));
	}
	@GetMapping("/getPayment/{Payid}")
	public List<String> getpayment(@PathVariable String Payid){
		return bsesTransactionFileDownloadServiceImpl.getPaymentype(Payid);
	}
	
	@GetMapping("/sendMailBses")
	public ResponseEntity<ResponseMessage> sendMailBses(){
		
		
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			String ip=localhost.getHostAddress().trim();
			
			List<String>ipMatcher =new ArrayList<>();
			ipMatcher.add("10.0.1.169");
			ipMatcher.add("127.0.0.1");
			ipMatcher.add("192.168.1.3");
			ipMatcher.add("10.0.1.170");
			ipMatcher.add("136.232.148.174");
			ipMatcher.add("146.196.32.137");
			ipMatcher.add("172.16.2.245");
			ipMatcher.add("192.168.1.7");
			ipMatcher.add("192.168.127.127");
			ipMatcher.add("172.16.2.179");
			ipMatcher.add("172.16.0.126");
			
			System.out.println(ip);
			
			ipMatcher.stream().forEach(t->{
				if (t.equalsIgnoreCase(ip)) {
					flag=1;
				}
			});
			
//			if (flag==1) {
//				return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(bsesTransactionFileDownloadServiceImpl.sendMailBses(), HttpStatus.OK)));
//			}
			return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage(bsesTransactionFileDownloadServiceImpl.sendMailBses(), HttpStatus.OK)));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		return ResponseEntity.status(HttpStatus.OK).body((new ResponseMessage("IP Not Match Please Contact to Admin", HttpStatus.OK)));
		
	}
}
