package com.shopplus.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "DOCTOR_MASTER", uniqueConstraints = { @UniqueConstraint(columnNames = "DOCTOR_NAME") })
public class DoctorMaster {

	@Id
	@Column(name = "DOCTOR_NAME", length = 45)
	String doctorName;

	@Column(name = "ADDRESS", length = 200)
	String address;

	public DoctorMaster(String doctorName, String address) {
		super();
		this.doctorName = doctorName;
		this.address = address;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public DoctorMaster() {

	}
}
