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

import org.hibernate.annotations.Index;

@Entity
@Table(name = "SALES_RETURN_MASTER", uniqueConstraints = { @UniqueConstraint(columnNames = "SALES_RETURN_NUMBER") })
public class SalesReturnMaster implements java.io.Serializable {

	@Id
	@Column(name = "SALES_RETURN_NUMBER", nullable = false)
	private int salesReturnNumber;

	@Column(name = "CREATED_DATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "BILL_NO", nullable = false)
	@Index(name = "BILL_NO_IDX")
	private int billNumber;

	@Column(name = "GRAND_TOTAL", nullable = true)
	private Float grandTotal;

	@Column(name = "SUCCESS", nullable = true)
	private boolean success;

	@Column(name = "CUSTOMER_NAME", nullable = true, length = 30)
	private String customerName;

	@Column(name = "DOCTOR_NAME", nullable = true, length = 30)
	private String doctorName;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "salesReturnMaster", cascade = CascadeType.ALL)
	private Set<SalesReturnDetails> salesReturnDetails = new HashSet<SalesReturnDetails>(0);

	public SalesReturnMaster() {

	}

	public SalesReturnMaster(int salesReturnNumber, Date createdDate, Float grandTotal, int billNumber, boolean success,
			String customerName, String doctorName, Set<SalesReturnDetails> salesReturnDetails) {
		this.salesReturnNumber = salesReturnNumber;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.billNumber = billNumber;
		this.success = success;
		this.customerName = customerName;
		this.salesReturnDetails = salesReturnDetails;
		this.doctorName = doctorName;
	}

	public int getSalesReturnNumber() {
		return salesReturnNumber;
	}

	public void setSalesReturnNumber(int salesReturnNumber) {
		this.salesReturnNumber = salesReturnNumber;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(int billNumber) {
		this.billNumber = billNumber;
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

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Set<SalesReturnDetails> getSalesReturnDetails() {
		return salesReturnDetails;
	}

	public void setSalesReturnDetails(Set<SalesReturnDetails> salesReturnDetails) {
		this.salesReturnDetails = salesReturnDetails;
	}

}