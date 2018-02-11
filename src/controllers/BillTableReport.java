package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BillTableReport {

	private StringProperty slNo;
	private StringProperty billNo;
	private StringProperty date;
	private StringProperty grandTotal;
	private StringProperty cgstBill;
	private StringProperty sgstBill;

	public BillTableReport(String slNo, String billNo, String date, String grandTotal, String cgstBill,
			String sgstBill) {

		super();
		setSlNo(slNo);
		setBillNo(billNo);
		setDate(date);
		setGrandTotal(grandTotal);
		setSgst(sgstBill);
		setCgst(cgstBill);
	}

	private void setSgst(String sgstBill) {
		sgstBillProperty().set(sgstBill);

	}

	private void setCgst(String cgstBill) {
		cgstBillProperty().set(cgstBill);

	}

	public StringProperty sgstBillProperty() {
		if (sgstBill == null)
			sgstBill = new SimpleStringProperty(this, "sgstBill");
		return sgstBill;
	}

	public StringProperty cgstBillProperty() {
		if (cgstBill == null)
			cgstBill = new SimpleStringProperty(this, "cgstBill");
		return cgstBill;
	}

	private void setSlNo(String slNo) {
		slNoProperty().set(slNo);

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
