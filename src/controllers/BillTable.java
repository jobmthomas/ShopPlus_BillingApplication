package controllers;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BillTable {

	private StringProperty uuidBil;
	private StringProperty itemNameBill;
	private StringProperty batchNoBill;
	private StringProperty companyNameBill;
	private StringProperty dealerNameBill;
	private StringProperty rackBill;
	private StringProperty mfdBill;
	private StringProperty expdBill;
	private StringProperty grossAmtBill;
	private StringProperty purchaseAmtBill;
	private StringProperty discountBill;
	private StringProperty sGstBill;
	private StringProperty cGstBill;
	private StringProperty totalBill;
	private StringProperty quantityBill;
	private StringProperty netTotalBill;

	DecimalFormat df = new DecimalFormat("#0.00");

	public BillTable() {
	}

	public BillTable(String uuid, String itemName, String batchNo, String companyName, String mfd, String expd,
			Float purchaseAmt, Float grossAmt, Float discount, Float sGst, Float cGst, Float total, int quantity,
			Float netTotal, String rack, String dealerName) {

		setUuid(uuid);
		setItemNameBill(itemName);
		setBatchNoBill(batchNo);

		setCompanyNameBill(companyName);
		setMfdBill(mfd);
		setExpdBill(expd);
		setPurchaseAmtBill(df.format(purchaseAmt));
		setGrossAmtBill(df.format(grossAmt));
		setDiscountBill(discount + "");
		setSGstBill(sGst + "");
		setCGstBill(cGst + "");
		setTotalBill(df.format(total));
		setQuantityBill(quantity + "");
		setNetTotalBill(df.format(netTotal));
		setRack(rack);
		setDealerName(dealerName);
	}

	private void setUuid(String uuid) {
		uuidProperty().set(uuid);

	}

	public StringProperty uuidProperty() {
		if (uuidBil == null)
			uuidBil = new SimpleStringProperty(this, "uuidBil");
		return uuidBil;
	}

	private void setDealerName(String dealerName) {
		dealerNameProperty().set(dealerName);

	}

	public StringProperty dealerNameProperty() {
		if (dealerNameBill == null)
			dealerNameBill = new SimpleStringProperty(this, "dealerNameBill");
		return dealerNameBill;
	}

	private void setRack(String rack) {
		rackProperty().set(rack);

	}

	public StringProperty rackProperty() {
		if (rackBill == null)
			rackBill = new SimpleStringProperty(this, "rackBill");
		return rackBill;
	}

	private void setTotalBill(String total) {
		totalBillProperty().set(total + "");

	}

	private void setNetTotalBill(String total) {
		netTotalBillProperty().set(total + "");

	}

	private void setExpdBill(String expd) {
		expdBillProperty().set(expd);

	}

	private void setBatchNoBill(String batchNo) {
		batchNoBillProperty().set(batchNo);
	}

	public StringProperty batchNoBillProperty() {
		if (batchNoBill == null)
			batchNoBill = new SimpleStringProperty(this, "batchNoBill");
		return batchNoBill;
	}

	public StringProperty totalBillProperty() {
		if (totalBill == null)
			totalBill = new SimpleStringProperty(this, "totalBill");
		return totalBill;
	}

	public StringProperty netTotalBillProperty() {
		if (netTotalBill == null)
			netTotalBill = new SimpleStringProperty(this, "netTotalBill");
		return netTotalBill;
	}

	public StringProperty expdBillProperty() {
		if (expdBill == null)
			expdBill = new SimpleStringProperty(this, "expdBill");
		return expdBill;
	}

	private void setMfdBill(String mfd) {
		mfdBillProperty().set(mfd);

	}

	public StringProperty mfdBillProperty() {
		if (mfdBill == null)
			mfdBill = new SimpleStringProperty(this, "mfdBill");
		return mfdBill;
	}

	private void setCompanyNameBill(String companyName) {
		companyNameBillProperty().set(companyName);
	}

	public StringProperty companyNameBillProperty() {
		if (companyNameBill == null)
			companyNameBill = new SimpleStringProperty(this, "companyNameBill");
		return companyNameBill;
	}

	public final void setItemNameBill(String value) {
		itemNameBillProperty().set(value);
	}

	public final void setGrossAmtBill(String value) {
		grossAmtBillProperty().set(value + "");
	}

	public final void setPurchaseAmtBill(String value) {
		purchaseAmtBillProperty().set(value + "");
	}

	public final void setSGstBill(String value) {
		sGstBillProperty().set(value + "");

	}

	public final void setCGstBill(String value) {
		cGstBillProperty().set(value + "");
	}

	public final void setQuantityBill(String value) {
		quantityBillProperty().set(value + "");
	}

	public StringProperty itemNameBillProperty() {
		if (itemNameBill == null)
			itemNameBill = new SimpleStringProperty(this, "itemNameBill");
		return itemNameBill;
	}

	private void setDiscountBill(String discount) {

		discountBillProperty().set(discount + "");
	}

	public StringProperty discountBillProperty() {
		if (discountBill == null)
			discountBill = new SimpleStringProperty(this, "discountBill");
		return discountBill;
	}

	public StringProperty sGstBillProperty() {
		if (sGstBill == null)
			sGstBill = new SimpleStringProperty(this, "sGstBill");
		return sGstBill;
	}

	public StringProperty cGstBillProperty() {
		if (cGstBill == null)
			cGstBill = new SimpleStringProperty(this, "cGstBill");
		return cGstBill;
	}

	public StringProperty quantityBillProperty() {
		if (quantityBill == null)
			quantityBill = new SimpleStringProperty(this, "quantityBill");
		return quantityBill;
	}

	public StringProperty grossAmtBillProperty() {
		if (grossAmtBill == null)
			grossAmtBill = new SimpleStringProperty(this, "grossAmtBill");
		return grossAmtBill;
	}

	public StringProperty purchaseAmtBillProperty() {
		if (purchaseAmtBill == null)
			purchaseAmtBill = new SimpleStringProperty(this, "purchaseAmtBill");
		return purchaseAmtBill;
	}

}
