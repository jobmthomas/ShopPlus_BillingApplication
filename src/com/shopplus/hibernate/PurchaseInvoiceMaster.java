package com.shopplus.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PURCHASE_INVOICE_MASTER", uniqueConstraints = { @UniqueConstraint(columnNames = "INVOICE_NUMBER") })
public class PurchaseInvoiceMaster implements java.io.Serializable {

	@Id
	@Column(name = "INVOICE_NUMBER", nullable = false, length = 30)
	private String invoiceNumber;

	@Column(name = "CREATED_DATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "COMPANY_NAME", nullable = true, length = 30)
	private String companyName;

	@Column(name = "HSN_SAC", nullable = true, length = 30)
	private String hsnSac;

	@Column(name = "GRAND_TOTAL", nullable = true)
	private Float grandTotal;

	@Column(name = "CGST", nullable = true)
	private Float cgst;

	@Column(name = "SGST", nullable = true)
	private Float sgst;

	@Column(name = "PAYED", nullable = true)
	private Float payed;

	public PurchaseInvoiceMaster() {

	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getHsnSac() {
		return hsnSac;
	}

	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	public Float getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Float grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Float getCgst() {
		return cgst;
	}

	public void setCgst(Float cgst) {
		this.cgst = cgst;
	}

	public Float getSgst() {
		return sgst;
	}

	public void setSgst(Float sgst) {
		this.sgst = sgst;
	}

	public Float getPayed() {
		return payed;
	}

	public void setPayed(Float payed) {
		this.payed = payed;
	}

}