package controllers;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExpiringItems {
	private StringProperty itemName;
	private StringProperty batchId;
	private StringProperty balanceStock;
	private StringProperty dealerName;

	public ExpiringItems(String itemName, String batchId, String balanceStock, String dealerName) {
		setItemName(itemName);
		setBatchId(batchId);
		setBalanceStock(balanceStock);
		setDealerName(dealerName);

	}

	public StringProperty itemNameProperty() {
		if (itemName == null)
			itemName = new SimpleStringProperty(this, "itemName");
		return itemName;
	}

	public StringProperty batchIdProperty() {
		if (batchId == null)
			batchId = new SimpleStringProperty(this, "batchId");
		return batchId;
	}

	public StringProperty balanceStockProperty() {
		if (balanceStock == null)
			balanceStock = new SimpleStringProperty(this, "balanceStock");
		return balanceStock;
	}

	public StringProperty dealerNameProperty() {
		if (dealerName == null)
			dealerName = new SimpleStringProperty(this, "dealerName");
		return dealerName;
	}

	public final void setItemName(String value) {
		itemNameProperty().set(value);
	}

	public final void setDealerName(String dealerName) {
		dealerNameProperty().set(dealerName);
	}

	private void setBatchId(String batchId) {
		batchIdProperty().set(batchId);
	}

	private void setBalanceStock(String balanceStock) {
		balanceStockProperty().set(balanceStock);

	}
}
