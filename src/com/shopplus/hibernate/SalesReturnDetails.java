package com.shopplus.hibernate;

// https://www.mkyong.com/hibernate/hibernate-one-to-many-relationship-example-annotation/
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SALES_RETURN_DETAILS")
public class SalesReturnDetails implements java.io.Serializable {

	@Id
	@Column(name = "UID", length = 100)
	String uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALES_RETURN_NUMBER", nullable = false)
	private SalesReturnMaster salesReturnMaster;

	@Column(name = "ITEM_NAME", length = 30)
	String itemName;

	@Column(name = "BATCH_NO", length = 30)
	String batchNo;

	@Column(name = "RACK", length = 30)
	String rack;

	@Column(name = "COMPANY", length = 30)
	String company;

	@Column(name = "DEALER", length = 30)
	String dealer;

	@Column(name = "MFD")
	@Temporal(TemporalType.DATE)
	Date mfd;

	@Column(name = "EXPD")
	@Temporal(TemporalType.DATE)
	Date expd;

	@Column(name = "GROSS_AMOUNT")
	float grossAmount;

	@Column(name = "PURCHASE_AMOUNT")
	float purchaseAmount;

	@Column(name = "DISCOUNT")
	float discount;

	@Column(name = "S_GST")
	float sGst;

	@Column(name = "C_GST")
	float cGst;

	@Column(name = "QUANTITY")
	int quantity;

	public SalesReturnDetails() {
	}

	public SalesReturnDetails(String uuid, SalesReturnMaster salesReturnMaster, String itemName, String batchNo,
			String rack, String company, String dealer, Date mfd, Date expd, float purchaseAmount, float grossAmount,
			float discount, float sGst, float cGst, int quantity) {

		super();
		this.uid = uuid;
		this.salesReturnMaster = salesReturnMaster;
		this.itemName = itemName;
		this.batchNo = batchNo;
		this.rack = rack;
		this.company = company;
		this.dealer = dealer;
		this.mfd = mfd;
		this.expd = expd;
		this.grossAmount = grossAmount;
		this.purchaseAmount = purchaseAmount;
		this.discount = discount;

		this.sGst = sGst;
		this.cGst = cGst;
		this.quantity = quantity;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public SalesReturnMaster getSalesReturnMaster() {
		return salesReturnMaster;
	}

	public void setSalesReturnMaster(SalesReturnMaster salesReturnMaster) {
		this.salesReturnMaster = salesReturnMaster;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public Date getMfd() {
		return mfd;
	}

	public void setMfd(Date mfd) {
		this.mfd = mfd;
	}

	public Date getExpd() {
		return expd;
	}

	public void setExpd(Date expd) {
		this.expd = expd;
	}

	public float getGrossAmount() {
		return grossAmount;
	}

	public float getPurchaseAmount() {
		return purchaseAmount;
	}

	public void setPurchaseAmount(float purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	public void setGrossAmount(float grossAmount) {
		this.grossAmount = grossAmount;
	}

	public float getsGst() {
		return sGst;
	}

	public void setsGst(float sGst) {
		this.sGst = sGst;
	}

	public float getcGst() {
		return cGst;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getRack() {
		return rack;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public void setcGst(float cGst) {
		this.cGst = cGst;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}