package com.shopplus.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import javafx.scene.control.Label;

@Entity
@Table(name = "LOGIN", uniqueConstraints = { @UniqueConstraint(columnNames = "USERNAME") })
public class Login implements java.io.Serializable {

	@Id
	@Column(name = "USERNAME", nullable = false, length = 10)
	private String USERNAME;

	@Column(name = "PASSWORD", nullable = false, length = 250)
	private String PASSWORD;

	@Column(name = "ROLE", nullable = false, length = 12)
	private String ROLE;

	@Column(name = "CREATED_ON", nullable = false, length = 12)
	private Date createdOn;

	public Login() {

	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Login(String uSERNAME, String pASSWORD, String rOLE, Date createdOn) {
		super();
		USERNAME = uSERNAME;
		PASSWORD = pASSWORD;
		ROLE = rOLE;
		this.createdOn = createdOn;
	}

	public String getUSERNAME() {
		return USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getROLE() {
		return ROLE;
	}

	public void setROLE(String rOLE) {
		ROLE = rOLE;
	}

}