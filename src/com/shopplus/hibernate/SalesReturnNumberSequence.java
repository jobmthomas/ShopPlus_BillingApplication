package com.shopplus.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SALES_RETURN_NUMBER_SEQUENCE", uniqueConstraints = {
		@UniqueConstraint(columnNames = "SALES_RETURN_NUMBER_SEQUENCE") })
public class SalesReturnNumberSequence implements java.io.Serializable {

	@Id
	@Column(name = "SALES_RETURN_NUMBER_SEQUENCE", nullable = false)
	private int salesReturnNumber;

	public int getSalesReturnNumber() {
		return salesReturnNumber;
	}

	public void setSalesReturnNumber(int salesReturnNumber) {
		this.salesReturnNumber = salesReturnNumber;
	}

	public SalesReturnNumberSequence(int salesReturnNumber) {
		super();
		this.salesReturnNumber = salesReturnNumber;
	}

	SalesReturnNumberSequence() {

	}

}