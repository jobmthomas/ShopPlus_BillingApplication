package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Doctor {

	private StringProperty doctorName;
	private StringProperty doctorAddress;
	String doctorAddressComplete;

	public Doctor(String doctorName, String doctorAddress, String doctorAddressComplete) {

		setDoctorName(doctorName);

		setDoctorAddress(doctorAddress.substring(0, Math.min(doctorAddress.length(), 20)));
		this.doctorAddressComplete = doctorAddressComplete;
	}

	private void setDoctorName(String doctorName) {
		doctorNameProperty().set(doctorName);
	}

	public StringProperty doctorNameProperty() {
		if (doctorName == null)
			doctorName = new SimpleStringProperty(this, "doctorName");
		return doctorName;
	}

	private void setDoctorAddress(String doctorAddress) {
		doctorAddressProperty().set(doctorAddress);

	}

	public StringProperty doctorAddressProperty() {
		if (doctorAddress == null)
			doctorAddress = new SimpleStringProperty(this, "doctorAddress");
		return doctorAddress;
	}

}
