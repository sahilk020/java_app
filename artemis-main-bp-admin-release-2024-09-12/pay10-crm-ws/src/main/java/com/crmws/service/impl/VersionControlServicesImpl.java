package com.crmws.service.impl;

import java.util.Date;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.crmws.entity.ResponseMessage;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.VersionControlEntity;
import com.pay10.commons.repository.VersionControlRepo;
import com.pay10.commons.util.TDRStatus;


@Service
public class VersionControlServicesImpl {

	@Autowired
	private VersionControlRepo repo;
	
	public VersionControlEntity updateAndInsertVersion(String type) {
		try {
			VersionControlEntity controlEntity=repo.getVersion();
			
			if(controlEntity==null) {
				VersionControlEntity versionControlEntity=new VersionControlEntity();
				versionControlEntity.setCreateDate(new Date());
				versionControlEntity.setStatus(TDRStatus.ACTIVE.getName());
				versionControlEntity.setVersion("1.0.0");
				repo.save(versionControlEntity);
			}else {
				VersionControlEntity versionControlEntity=new VersionControlEntity();
				
				controlEntity.setStatus(TDRStatus.INACTIVE.getName());
				repo.update(controlEntity);
				if(type.equalsIgnoreCase("increment")) {
					  String newVersion = incrementVersion(controlEntity.getVersion());
					  versionControlEntity.setVersion(newVersion);
				}else {
					  String newVersion = decrementVersion(controlEntity.getVersion());
					  versionControlEntity.setVersion(newVersion);
				}
	
				versionControlEntity.setCreateDate(new Date());
				versionControlEntity.setStatus(TDRStatus.ACTIVE.getName());
				
				repo.save(versionControlEntity);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return repo.getVersion();
	}

	public ResponseEntity<ResponseMessage> getVersion() {
		ResponseMessage message=new ResponseMessage();
		try {
			VersionControlEntity controlEntity=repo.getVersion();
			
			message.setRespmessage(String.valueOf(controlEntity.getVersion()==null?"":controlEntity.getVersion()));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
	
	
	 public static String incrementVersion(String version) {
	        String[] parts = version.split("\\."); // Split by dot
	        int major = Integer.parseInt(parts[0]);
	        int minor = Integer.parseInt(parts[1]);
	        int patch = Integer.parseInt(parts[2]);
	 
	        if (patch < 9) {
	            patch++; // Increase the patch version
	        } else {
	            patch = 0; // Reset patch version to 0
	            minor++; // Increase the minor version
	        }
	 
	        return String.format("%d.%d.%d", major, minor, patch);
	    }
	 
	 public static String decrementVersion(String version) {
	        String[] parts = version.split("\\."); // Split by dot
	        int major = Integer.parseInt(parts[0]);
	        int minor = Integer.parseInt(parts[1]);
	        int patch = Integer.parseInt(parts[2]);
	 
	        if (minor > 0 && patch == 0) {
	            minor--; // Decrease the minor version
	            patch = 9; // Set patch version to 9
	        } else if (patch > 0) {
	            patch--; // Decrease the patch version
	        }
	 
	        return String.format("%d.%d.%d", major, minor, patch);
	    }
}
