package com.pay10.commons.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "auth_token")
public class SbiToken implements Serializable {

	private static final long serialVersionUID = -8794117484489299407L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String acquirerName;

	@Column
	private String clientId;

	@Column
	private String clientSecret;

	@Column
	private String accessToken;

	@Column
	private int accessTokentExpire;

	@Column
	private String refreshToken;

	@Column
	private int refreshTokenExpire;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getAccessTokentExpire() {
		return accessTokentExpire;
	}

	public void setAccessTokentExpire(int accessTokentExpire) {
		this.accessTokentExpire = accessTokentExpire;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public int getRefreshTokenExpire() {
		return refreshTokenExpire;
	}

	public void setRefreshTokenExpire(int refreshTokenExpire) {
		this.refreshTokenExpire = refreshTokenExpire;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
