package com.shopplus.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BILL_NUMBER_SEQUENCE", uniqueConstraints = { @UniqueConstraint(columnNames = "BILL_NUMBER_SEQUENCE") })
public class BillNumberSequence implements java.io.Serializable {

	@Id
	@Column(name = "BILL_NUMBER_SEQUENCE", nullable = false)
	private int billNumber;

	public int getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(int billNumber) {
		this.billNumber = billNumber;
	}

	public BillNumberSequence(int billNumber) {
		super();
		this.billNumber = billNumber;
	}

	BillNumberSequence() {

	}

}