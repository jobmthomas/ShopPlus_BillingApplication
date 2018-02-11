package controllers;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PurchaseInvoice {

	private StringProperty invoiceNo;
	private StringProperty invoiceDate;
	private StringProperty companyName;
	private StringProperty hsnSac;
	private StringProperty totalAmount;
	private StringProperty cGst;
	private StringProperty sGst;
	private StringProperty payed;

	public PurchaseInvoice(String invoiceNo, String invoiceDate, String companyName, String hsnSac, String totalAmount,
			String cGst, String sGst, String payed) {
		setInvoiceNo(invoiceNo);
		setInvoiceDate(invoiceDate);
		setCompanyName(companyName);
		setHsnSac(hsnSac);
		setTotalAmount(totalAmount);
		setCgst(cGst);
		setSgst(sGst);
		setPayed(payed);
	}

	private void setPayed(String payed) {
		Float payedFloat = new Float(payed);
		DecimalFormat df = new DecimalFormat("#0.00");
		payedProperty().set(df.format(payedFloat));
	}

	public StringProperty payedProperty() {
		if (payed == null)
			payed = new SimpleStringProperty(this, "payed");
		return payed;
	}

	private void setSgst(String sGst) {
		Float sGstFloat = new Float(sGst);
		DecimalFormat df = new DecimalFormat("#0.00");
		sGstProperty().set(df.format(sGstFloat));

	}

	public StringProperty sGstProperty() {
		if (sGst == null)
			sGst = new SimpleStringProperty(this, "sGst");
		return sGst;
	}

	private void setCgst(String cGst) {
		Float cGstFloat = new Float(cGst);
		DecimalFormat df = new DecimalFormat("#0.00");
		cGstProperty().set(df.format(cGstFloat));

	}

	public StringProperty cGstProperty() {
		if (cGst == null)
			cGst = new SimpleStringProperty(this, "cGst");
		return cGst;
	}

	private void setTotalAmount(String totalAmount) {
		Float totalAmountFloat = new Float(totalAmount);
		DecimalFormat df = new DecimalFormat("#0.00");
		totalAmountProperty().set(df.format(totalAmountFloat));

	}

	public StringProperty totalAmountProperty() {
		if (totalAmount == null)
			totalAmount = new SimpleStringProperty(this, "totalAmount");
		return totalAmount;
	}

	private void setHsnSac(String hsnSac) {
		hsnSacProperty().set(hsnSac);

	}

	public StringProperty hsnSacProperty() {
		if (hsnSac == null)
			hsnSac = new SimpleStringProperty(this, "hsnSac");
		return hsnSac;
	}

	private void setInvoiceDate(String invoiceDate) {
		invoiceDateProperty().set(invoiceDate);

	}

	public StringProperty invoiceDateProperty() {
		if (invoiceDate == null)
			invoiceDate = new SimpleStringProperty(this, "invoiceDate");
		return invoiceDate;
	}

	private void setCompanyName(String companyName) {
		companyNameProperty().set(companyName);

	}

	public StringProperty companyNameProperty() {
		if (companyName == null)
			companyName = new SimpleStringProperty(this, "companyName");
		return companyName;
	}

	private void setInvoiceNo(String invoiceNo) {
		invoiceNoProperty().set(invoiceNo);
	}

	public StringProperty invoiceNoProperty() {
		if (invoiceNo == null)
			invoiceNo = new SimpleStringProperty(this, "invoiceNo");
		return invoiceNo;
	}

}
