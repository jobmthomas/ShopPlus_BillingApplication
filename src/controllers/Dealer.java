package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Dealer {

	private StringProperty dealerName;
	private StringProperty dealerAddress;
	String dealerAddressComplete;

	public Dealer(String dealerName, String dealerAddress) {

		setDealerName(dealerName);
		setDealerAddress(dealerAddress.substring(0, Math.min(dealerAddress.length(), 20)));
		dealerAddressComplete = dealerAddress;
	}

	private void setDealerName(String dealerName) {
		dealerNameProperty().set(dealerName);
	}

	public StringProperty dealerNameProperty() {
		if (dealerName == null)
			dealerName = new SimpleStringProperty(this, "dealerName");
		return dealerName;
	}

	private void setDealerAddress(String dealerAddress) {
		dealerAddressProperty().set(dealerAddress);

	}

	public StringProperty dealerAddressProperty() {
		if (dealerAddress == null)
			dealerAddress = new SimpleStringProperty(this, "dealerAddress");
		return dealerAddress;
	}

}
