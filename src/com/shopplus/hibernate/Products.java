package com.shopplus.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "PRODUCTS", uniqueConstraints = { @UniqueConstraint(columnNames = "ITEM_NAME") })
public class Products implements java.io.Serializable {

	@Id
	@Column(name = "ITEM_NAME", length = 30)
	String itemName;

	@Column(name = "CATEGORY", length = 30)
	@Index(name = "CATEGORY_IDX")
	String category;

	@Column(name = "COMPANY_NAME", length = 30)
	@Index(name = "COMPANY_NAME_IDX")
	String companyName;

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

	public Products(String itemName, String category, String companyName) {
		super();
		this.itemName = itemName;
		this.category = category;
		this.companyName = companyName;
	}

}