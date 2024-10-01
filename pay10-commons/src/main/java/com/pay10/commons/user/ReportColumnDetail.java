package com.pay10.commons.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "report_column_detail")
public class ReportColumnDetail {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "tag_name")
	private String tagName;
	@Column(name = "map_value",length = 2000)
	private String mapValue;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getMapValue() {
		return mapValue;
	}

	public void setMapValue(String mapValue) {
		this.mapValue = mapValue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
