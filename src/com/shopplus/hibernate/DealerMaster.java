package com.shopplus.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "DEALER_MASTER", uniqueConstraints = { @UniqueConstraint(columnNames = "DEALER_NAME") })
public class DealerMaster {

	@Id
	@Column(name = "DEALER_NAME", length = 45)
	String dealerName;

	@Column(name = "ADDRESS", length = 200)
	String address;

	public DealerMaster(String dealerName, String address) {
		super();
		this.dealerName = dealerName;
		this.address = address;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public DealerMaster() {

	}
}
