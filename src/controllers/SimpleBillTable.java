package controllers;

import java.text.DecimalFormat;

import com.shopplus.Main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SimpleBillTable {

	private StringProperty itemNameBill;
	private StringProperty grossAmtBill;
	private StringProperty discountBill;
	private StringProperty sGstBill;
	private StringProperty cGstBill;
	private StringProperty quantityBill;
	private StringProperty netTotalBill;

	DecimalFormat df = new DecimalFormat("#0.00");

	public SimpleBillTable() {

		setItemNameBill("");
		setGrossAmtBill("");
		setDiscountBill("");
		setSGstBill(Main.prop.getProperty("default_sgst_per") + "");
		setCGstBill(Main.prop.getProperty("default_cgst_per") + "");
		setQuantityBill("1");
		setNetTotalBill("");
	}

	public SimpleBillTable(String itemName, Float grossAmt, Float discount, Float sGst, Float cGst, Float netTotal,
			int quantity) {

		setItemNameBill(itemName);
		setGrossAmtBill(df.format(grossAmt));
		setDiscountBill(df.format(discount));
		setSGstBill(sGst + "");
		setCGstBill(cGst + "");
		setQuantityBill(quantity + "");
		setNetTotalBill(df.format(netTotal));
	}

	public void setNetTotalBill(String total) {
		netTotalBillProperty().set(total + "");

	}

	public String getNetTotalBill() {
		return netTotalBillProperty().getValue();

	}

	public StringProperty netTotalBillProperty() {
		if (netTotalBill == null)
			netTotalBill = new SimpleStringProperty(this, "netTotalBill");
		return netTotalBill;
	}

	public final void setItemNameBill(String value) {
		itemNameBillProperty().set(value);
	}

	public final void setGrossAmtBill(String value) {
		grossAmtBillProperty().set(value + "");
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

	public void setDiscountBill(String discount) {

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

}
