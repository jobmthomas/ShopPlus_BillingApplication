package com.shopplus.hibernate;

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

import org.hibernate.annotations.Index;

@Entity
@Table(name = "STOCK_DETAILS")

public class StockDetails implements java.io.Serializable {

	@Id
	@Column(name = "UID", length = 100)
	String uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_NAME", nullable = false)
	private StockMaster stockMaster;

	@Column(name = "BATCH_ID", length = 30)
	@Index(name = "BATCH_ID_IDX")
	String batchId;

	@Column(name = "RACK", length = 30)
	String rack;

	@Column(name = "DEALER_NAME", length = 30)
	@Index(name = "DEALER_NAME_IDX")
	String dealerName;

	@Column(name = "MFD")
	@Temporal(TemporalType.DATE)
	Date mfd;

	@Column(name = "EXPD")
	@Index(name = "EXPD_IDX")
	@Temporal(TemporalType.DATE)
	Date expd;

	@Column(name = "GROSS_AMT")
	float grossAmt;

	@Column(name = "PURCHASE_AMT")
	float purchaseAmt;

	@Column(name = "DISCOUNT")
	float discount;

	@Column(name = "S_GST_PERCENTAGE")
	float sgstPercentage;

	@Column(name = "C_GST_PERCENTAGE")
	float cgstPercentage;

	@Column(name = "QUANTITY_AVAILABLE")
	int quantityAvaialble;

	@Column(name = "RESCENT_STOCK_INTAKE")
	int rescentStockIntake;

	public StockDetails() {

	}

	public StockDetails(String uid, StockMaster stockMaster, String batchId, String rack, String dealerName, Date mfd,
			Date expd, float purchaseAmt, float grossAmt, float discount, float sgstPercentage, float cgstPercentage,
			int quantityAvaialble, int rescentStockIntake) {
		super();
		this.uid = uid;
		this.stockMaster = stockMaster;
		this.batchId = batchId;
		this.rack = rack;
		this.dealerName = dealerName;
		this.mfd = mfd;
		this.expd = expd;
		this.purchaseAmt = purchaseAmt;
		this.grossAmt = grossAmt;
		this.discount = discount;
		this.sgstPercentage = sgstPercentage;
		this.cgstPercentage = cgstPercentage;
		this.quantityAvaialble = quantityAvaialble;
		this.rescentStockIntake = rescentStockIntake;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public StockMaster getStockMaster() {
		return stockMaster;
	}

	public void setStockMaster(StockMaster stockMaster) {
		this.stockMaster = stockMaster;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getRack() {
		return rack;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public String getDealerName() {
		return dealerName;
	}

	public float getPurchaseAmt() {
		return purchaseAmt;
	}

	public void setPurchaseAmt(float purchaseAmt) {
		this.purchaseAmt = purchaseAmt;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
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

	public float getGrossAmt() {
		return grossAmt;
	}

	public void setGrossAmt(float grossAmt) {
		this.grossAmt = grossAmt;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getSgstPercentage() {
		return sgstPercentage;
	}

	public void setSgstPercentage(float sgstPercentage) {
		this.sgstPercentage = sgstPercentage;
	}

	public float getCgstPercentage() {
		return cgstPercentage;
	}

	public void setCgstPercentage(float cgstPercentage) {
		this.cgstPercentage = cgstPercentage;
	}

	public int getQuantityAvaialble() {
		return quantityAvaialble;
	}

	public void setQuantityAvaialble(int quantityAvaialble) {
		this.quantityAvaialble = quantityAvaialble;
	}

	public int getRescentStockIntake() {
		return rescentStockIntake;
	}

	public void setRescentStockIntake(int rescentStockIntake) {
		this.rescentStockIntake = rescentStockIntake;
	}

}
