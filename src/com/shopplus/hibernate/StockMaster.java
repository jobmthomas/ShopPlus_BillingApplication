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
@Table(name = "STOCK_MASTER", uniqueConstraints = { @UniqueConstraint(columnNames = "ITEM_NAME") })
public class StockMaster {

	@Id
	@Column(name = "ITEM_NAME", length = 30)
	String itemName;

	@Column(name = "CATEGORY", length = 30)
	@Index(name = "CATEGORY_IDX")
	String category;

	@Column(name = "COMPANY_NAME", length = 30)
	@Index(name = "COMPANY_NAME_IDX")
	String companyName;

	@Column(name = "BARCODE", length = 100)
	@Index(name = "BARCODE_IDX")
	String barcode;

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = "CREATED_DATE_IDX")
	Date createdData;

	@Column(name = "UPDATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = "UPDATED_DATE_IDX")
	Date updatedData;

	@Column(name = "EXPD_WARNING_THRESHOLD_DAYS", length = 4)
	@Index(name = "EXPD_WARNING_THRESHOLD_DAYS")
	int expdWarningThresholdDays;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "stockMaster", cascade = CascadeType.ALL)

	private Set<StockDetails> stockDetails = new HashSet<StockDetails>(0);

	public StockMaster() {

	}

	public StockMaster(String itemName, String category, String companyName, String barcode, Date createdData,
			Date updatedData, int expdWarningThresholdDays, Set<StockDetails> stockDetails) {
		super();
		this.itemName = itemName;
		this.category = category;
		this.companyName = companyName;
		this.barcode = barcode;
		this.createdData = createdData;
		this.updatedData = updatedData;
		if (stockDetails != null)
			this.stockDetails = stockDetails;
		this.expdWarningThresholdDays = expdWarningThresholdDays;
	}

	public int getExpdWarningThresholdDays() {
		return expdWarningThresholdDays;
	}

	public void setExpdWarningThresholdDays(int expdWarningThresholdDays) {
		this.expdWarningThresholdDays = expdWarningThresholdDays;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Date getCreatedData() {
		return createdData;
	}

	public void setCreatedData(Date createdData) {
		this.createdData = createdData;
	}

	public Date getUpdatedData() {
		return updatedData;
	}

	public void setUpdatedData(Date updatedData) {
		this.updatedData = updatedData;
	}

	public Set<StockDetails> getStockDetails() {
		return stockDetails;
	}

	public void setStockDetails(Set<StockDetails> stockDetails) {
		this.stockDetails = stockDetails;
	}

}
