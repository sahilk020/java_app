package com.pay10.commons.repository;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;


	@Entity
	@Proxy(lazy = false)
	@Table(name = "fileentitiy")
	public class Fileentitiy implements Serializable {

		private static final long serialVersionUID = -2316060336671262154L;

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;

		private String filename;
		private String createdate;
		private String filesize;
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
		public String getCreatedate() {
			return createdate;
		}
		public void setCreatedate(String createdate) {
			this.createdate = createdate;
		}
		public String getFilesize() {
			return filesize;
		}
		public void setFilesize(String filesize) {
			this.filesize = filesize;
		}
		@Override
		public String toString() {
			return "Fileentitiy [id=" + id + ", filename=" + filename + ", createdate=" + createdate + ", filesize="
					+ filesize + "]";
		}
		
	
}
