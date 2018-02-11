package com.shopplus.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BILL_MASTER", uniqueConstraints = { @UniqueConstraint(columnNames = "BILL_NUMBER") })
public class BillMaster implements java.io.Serializable {

	@Id
	@Column(name = "BILL_NUMBER", nullable = false)
	private int billNumber;

	@Column(name = "CREATED_DATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "EXTRA_DISCOUNT", nullable = true)
	private Float extraDiscount;

	@Column(name = "GRAND_TOTAL", nullable = true)
	private Float grandTotal;

	@Column(name = "SUCCESS", nullable = true)
	private boolean success = false;

	@Column(name = "CUSTOMER_NAME", nullable = true, length = 30)
	private String customerName;

	@Column(name = "DOCTOR_NAME", nullable = true, length = 30)
	private String doctorName;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "billMaster", cascade = CascadeType.ALL)
	private Set<BillDetails> billDetails = new HashSet<BillDetails>(0);

	public BillMaster() {

	}

	public BillMaster(int billNumber, Date createdDate, Float grandTotal, Float extraDiscount, boolean success,
			String customerName, String doctorName) {
		this.billNumber = billNumber;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.extraDiscount = extraDiscount;
		this.success = success;
		this.customerName = customerName;
		this.doctorName = doctorName;
	}

	public BillMaster(int billNumber, Date createdDate, Float grandTotal, Float extraDiscount, boolean success,
			String customerName, Set<BillDetails> billDetails) {
		this.billNumber = billNumber;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.extraDiscount = extraDiscount;
		this.success = success;
		this.customerName = customerName;
		this.billDetails = billDetails;
	}

	public Float getExtraDiscount() {
		return extraDiscount;
	}

	public void setExtraDiscount(Float extraDiscount) {
		this.extraDiscount = extraDiscount;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public int getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(int billNumber) {
		this.billNumber = billNumber;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Float getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Float grandTotal) {
		this.grandTotal = grandTotal;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Set<BillDetails> getBillDetails() {
		return billDetails;
	}

	public void setBillDetails(Set<BillDetails> billDetails) {
		this.billDetails = billDetails;
	}

}