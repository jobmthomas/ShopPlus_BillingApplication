package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SalesReturnTableReport {

	private StringProperty slNo;
	private StringProperty salesReturnBillNo;
	private StringProperty billNo;
	private StringProperty date;
	private StringProperty grandTotal;
	private StringProperty cgstReturn;
	private StringProperty sgstReturn;

	public SalesReturnTableReport(String slNo, String salesReturnBillNo, String billNo, String date, String grandTotal,
			String cgstReturn, String sgstReturn) {

		super();
		setSlNo(slNo);
		setSalesReturnBillNo(salesReturnBillNo);
		setBillNo(billNo);
		setDate(date);
		setGrandTotal(grandTotal);
		setSgst(sgstReturn);
		setCgst(cgstReturn);
	}

	private void setSgst(String sgstBill) {
		sgstReturnProperty().set(sgstBill);

	}

	private void setCgst(String cgstBill) {
		cgstReturnProperty().set(cgstBill);

	}

	public StringProperty sgstReturnProperty() {
		if (sgstReturn == null)
			sgstReturn = new SimpleStringProperty(this, "sgstReturn");
		return sgstReturn;
	}

	public StringProperty cgstReturnProperty() {
		if (cgstReturn == null)
			cgstReturn = new SimpleStringProperty(this, "cgstReturn");
		return cgstReturn;
	}

	private void setSlNo(String slNo) {
		slNoProperty().set(slNo);

	}

	private void setSalesReturnBillNo(String salesReturnBillNo) {
		salesReturnBillNoProperty().set(salesReturnBillNo);

	}

	private void setBillNo(String billNo) {
		billNoProperty().set(billNo);

	}

	private void setDate(String date) {
		dateProperty().set(date);

	}

	private void setGrandTotal(String grandTotal) {
		grandTotalProperty().set(grandTotal);

	}

	public StringProperty slNoProperty() {
		if (slNo == null)
			slNo = new SimpleStringProperty(this, "slNo");
		return slNo;
	}

	public StringProperty billNoProperty() {
		if (billNo == null)
			billNo = new SimpleStringProperty(this, "billNo");
		return billNo;
	}

	public StringProperty salesReturnBillNoProperty() {
		if (salesReturnBillNo == null)
			salesReturnBillNo = new SimpleStringProperty(this, "salesReturnBillNo");
		return salesReturnBillNo;
	}

	public StringProperty dateProperty() {
		if (date == null)
			date = new SimpleStringProperty(this, "date");
		return date;
	}

	public StringProperty grandTotalProperty() {
		if (grandTotal == null)
			grandTotal = new SimpleStringProperty(this, "grandTotal");
		return grandTotal;
	}

}
