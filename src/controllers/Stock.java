package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Stock {

	private StringProperty itemName;
	private StringProperty batchId;

	private StringProperty barCode;
	private StringProperty category;
	private StringProperty expdWarningThresholdDays;
	private StringProperty rack;
	private StringProperty dealerName;
	private StringProperty companyName;

	private StringProperty mfd;
	private StringProperty expd;
	private StringProperty grossAmt;
	private StringProperty purchaseAmt;
	private StringProperty discount;
	private StringProperty sGst;
	private StringProperty cGst;

	private StringProperty createdDate;
	private StringProperty updatedDate;
	private StringProperty quantity;

	public Stock(String itemName, String batchId, String barCode, String category, String rack, String dealerName,
			String companyName, String mfd, String expd, String purchaseAmt, String grossAmt, String discount,
			String sGst, String cGst, String createdDate, String updatedDate, String quantity,
			String expdWarningThresholdDays) {

		setItemName(itemName);
		setBatchId(batchId);
		setBarCode(barCode);
		setCategory(category);
		setRack(rack);
		setDealerName(dealerName);
		setCompanyName(companyName);
		setMfd(mfd);
		setExpd(expd);
		setGrossAmt(grossAmt);
		setPurchaseAmt(purchaseAmt);
		setDiscount(discount);
		setSGst(sGst);
		setCGst(cGst);
		setCreatedDate(createdDate);
		setUpdatedDate(updatedDate);
		setQuantity(quantity);
		setExpdWarningThresholdDays(expdWarningThresholdDays);
	}

	private void setExpdWarningThresholdDays(String expdWarningThresholdDays) {
		expdWarningThresholdDaysProperty().set(expdWarningThresholdDays);
	}

	public StringProperty expdWarningThresholdDaysProperty() {
		if (expdWarningThresholdDays == null)
			expdWarningThresholdDays = new SimpleStringProperty(this, "expdWarningThresholdDays");
		return expdWarningThresholdDays;
	}

	private void setUpdatedDate(String updatedDate) {
		updatedDateProperty().set(updatedDate);

	}

	private void setCreatedDate(String createdDate) {
		createdDateProperty().set(createdDate);
	}

	private void setExpd(String expd) {
		expdProperty().set(expd);

	}

	public StringProperty expdProperty() {
		if (expd == null)
			expd = new SimpleStringProperty(this, "expd");
		return expd;
	}

	private void setMfd(String mfd) {
		mfdProperty().set(mfd);

	}

	public StringProperty mfdProperty() {
		if (mfd == null)
			mfd = new SimpleStringProperty(this, "mfd");
		return mfd;
	}

	private void setDealerName(String dealerName) {
		dealerNameProperty().set(dealerName);
	}

	private void setCompanyName(String companyName) {
		companyNameProperty().set(companyName);
	}

	public StringProperty companyNameProperty() {
		if (companyName == null)
			companyName = new SimpleStringProperty(this, "companyName");
		return companyName;
	}

	public StringProperty dealerNameProperty() {
		if (dealerName == null)
			dealerName = new SimpleStringProperty(this, "dealerName");
		return dealerName;
	}

	private void setBatchId(String batchId) {
		batchIdProperty().set(batchId);
	}

	public StringProperty batchIdProperty() {
		if (batchId == null)
			batchId = new SimpleStringProperty(this, "batchId");
		return batchId;
	}

	public final void setItemName(String value) {
		itemNameProperty().set(value);
	}

	public final void setBarCode(String value) {
		barCodeProperty().set(value);
	}

	public final void setCategory(String value) {
		categoryProperty().set(value);
	}

	public final void setRack(String value) {
		rackProperty().set(value);
	}

	public final void setGrossAmt(String value) {
		grossAmtProperty().set(value);
	}

	public final void setPurchaseAmt(String value) {
		purchaseAmtProperty().set(value);
	}

	public StringProperty purchaseAmtProperty() {
		if (purchaseAmt == null)
			purchaseAmt = new SimpleStringProperty(this, "purchaseAmt");
		return purchaseAmt;
	}

	public final void setSGst(String value) {
		sGstProperty().set(value);
	}

	public final void setCGst(String value) {
		cGstProperty().set(value);
	}

	public final void setQuantity(String value) {
		quantityProperty().set(value);
	}

	private StringProperty createdDateProperty() {
		if (createdDate == null)
			createdDate = new SimpleStringProperty(this, "createdDate");
		return createdDate;
	}

	private StringProperty updatedDateProperty() {
		if (updatedDate == null)
			updatedDate = new SimpleStringProperty(this, "updatedDate");
		return updatedDate;
	}

	public StringProperty itemNameProperty() {
		if (itemName == null)
			itemName = new SimpleStringProperty(this, "itemName");
		return itemName;
	}

	public StringProperty barCodeProperty() {
		if (barCode == null)
			barCode = new SimpleStringProperty(this, "barCode");
		return barCode;
	}

	public StringProperty categoryProperty() {
		if (category == null)
			category = new SimpleStringProperty(this, "category");
		return category;
	}

	public StringProperty rackProperty() {
		if (rack == null)
			rack = new SimpleStringProperty(this, "rack");
		return rack;
	}

	public StringProperty grossAmtProperty() {
		if (grossAmt == null)
			grossAmt = new SimpleStringProperty(this, "grossAmt");
		return grossAmt;
	}

	private void setDiscount(String discount) {

		discountProperty().set(discount);
	}

	public StringProperty discountProperty() {
		if (discount == null)
			discount = new SimpleStringProperty(this, "discount");
		return discount;
	}

	public StringProperty sGstProperty() {
		if (sGst == null)
			sGst = new SimpleStringProperty(this, "sGst");
		return sGst;
	}

	public StringProperty cGstProperty() {
		if (cGst == null)
			cGst = new SimpleStringProperty(this, "cGst");
		return cGst;
	}

	public StringProperty quantityProperty() {
		if (quantity == null)
			quantity = new SimpleStringProperty(this, "quantity");
		return quantity;
	}

}
