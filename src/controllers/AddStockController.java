package controllers;

import java.net.URL;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.Utils;
import com.shopplus.hibernate.DealerMaster;
import com.shopplus.hibernate.StockDetails;
import com.shopplus.hibernate.StockMaster;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AddStockController {

	DBUtils dbUtils = null;
	DBSearchUtils dbSearchUtils = null;
	static Logger logger = Logger.getLogger(AddStockController.class);
	public static AddStockController addStockController;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField barcode;

	@FXML
	private AutoCompleteTextField categoryAutoFill;

	@FXML
	private TextField cgstPercentage;

	@FXML
	private AutoCompleteTextField dealerNameAutoFill;

	@FXML
	private TextArea dealteAddress;

	@FXML
	private TextField discount;

	@FXML
	private TableView<Stock> editTable;

	@FXML
	private DatePicker expd;

	@FXML
	private TextField expdWarningThresholdDays;

	@FXML
	private TextField grossAmt;

	@FXML
	private TextField purchaseRate;

	@FXML
	private CheckBox foraStrip;

	@FXML
	private TextField batchId;

	@FXML
	private AutoCompleteTextField itemNameAutofill;

	@FXML
	private AutoCompleteTextField companyNameAutoFill;

	@FXML
	private DatePicker mfd;

	@FXML
	private TextField quantity;

	@FXML
	private TextField inaStrip;

	@FXML
	private TextField rack;

	@FXML
	private TextField sgstPercentage;

	@FXML
	void addStock(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");
		if (!inaStrip.getText().equals("")) {
			if (!foraStrip.isSelected()) {
				foraStrip.requestFocus();
				alert.setHeaderText("Please check 'For a strip' option ");
				alert.showAndWait();
				return;
			}
		}
		if (foraStrip.isSelected()) {
			if (inaStrip.getText().equals("")) {
				inaStrip.requestFocus();
				alert.setHeaderText("Please provide quantity in a strip");
				alert.showAndWait();
				return;
			}
		}
		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		int expdWarningThresholdDaysInt = Integer.MAX_VALUE;
		try {
			int quantityInt = Integer.parseInt(quantity.getText() + "");
			if (quantityInt < 0) {
				alert.setContentText("Quantity is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText("Quantity is an invalid value");
		}

		try {
			expdWarningThresholdDaysInt = Integer.parseInt(expdWarningThresholdDays.getText() + "");
			if (expdWarningThresholdDaysInt < 0) {
				alert.setContentText("Expd warning threshold is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText("Expd warning threshold is an invalid value");
		}

		try {
			float grossAmtInt = Float.parseFloat(grossAmt.getText() + "");
			if (grossAmtInt < 0) {
				alert.setContentText(alert.getContentText() + "\n" + "Gross Amt is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText(alert.getContentText() + "\n" + "Gross Amt is an invalid value");
		}

		try {
			float purchaseRateInt = Float.parseFloat(purchaseRate.getText() + "");
			if (purchaseRateInt < 0) {
				alert.setContentText(alert.getContentText() + "\n" + "Purchase Amt is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText(alert.getContentText() + "\n" + "Purchase Amt is an invalid value");
		}

		if (!discount.getText().equals("")) {
			try {
				float discountFloat = Float.parseFloat(discount.getText() + "");
				if (discountFloat < 0) {
					alert.setContentText("Discount is an invalid value");
				}
			} catch (Exception e) {
				alert.setContentText("Discount is an invalid value");
			}
		}

		try {
			float sGstInt = Float.parseFloat(sgstPercentage.getText() + "");
			if (sGstInt < 0) {
				alert.setContentText(alert.getContentText() + "\n" + "S-GST is an invalid value");
			}

		} catch (Exception e) {
			alert.setContentText(alert.getContentText() + "\n" + "S-GST is an invalid value");
		}

		try {
			float cGstInt = Float.parseFloat(cgstPercentage.getText() + "");
			if (cGstInt < 0) {
				alert.setContentText(alert.getContentText() + "\n" + "C-GST is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText(alert.getContentText() + "\n" + "C-GST is an invalid value");
		}

		if (foraStrip.isSelected()) {
			if (inaStrip.getText().equals("")) {
				alert.setContentText(alert.getContentText() + "\n"
						+ "Must enter no of items in a strip(Fill 'in a strip' near Quantity)");
			} else {
				try {
					int insStripInt = Integer.parseInt(inaStrip.getText());
				} catch (Exception e) {
					alert.setContentText(alert.getContentText() + "\n"
							+ "Must enter no of items in a strip(Fill 'in a strip' near Quantity)");
				}
			}
		}

		if (itemNameAutofill.getText().equals("") || sgstPercentage.getText().equals("")
				|| cgstPercentage.getText().equals("") || quantity.getText().equals("") || grossAmt.getText().equals("")
				|| purchaseRate.getText().equals("") || expd.getValue() == null || batchId.getText() == null
				|| mfd.getValue() == null) {
			alert.setContentText(
					"Item Name,BatchId,MFD,EXPD,Purchase Rate,Selling Rate,S-GST,C-GST,Quantity are mandatary fields");
			alert.showAndWait();
			return;
		}
		LocalDate localDateMfd = mfd.getValue();
		Instant instantMfd = Instant.from(localDateMfd.atStartOfDay(ZoneId.systemDefault()));
		java.util.Date mdfDate = Date.from(instantMfd);

		LocalDate localDateExpd = expd.getValue();
		Instant instantExpd = Instant.from(localDateExpd.atStartOfDay(ZoneId.systemDefault()));
		java.util.Date expdDate = Date.from(instantExpd);
		if (expdDate.before(mdfDate)) {
			alert.setContentText("Expiry date should be greater than or equal to manufacturing date");
		}

		if (!alert.getContentText().trim().equals("")) {
			alert.showAndWait();
			return;
		}

		Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
		criteria.createAlias("stockDetails", "child");

		criteria.add(Restrictions.eq("itemName", itemNameAutofill.getText()).ignoreCase());

		criteria.add(Restrictions.eq("child.batchId", batchId.getText()).ignoreCase());

		List<StockMaster> results = (List<StockMaster>) criteria.list();

		if (results.size() > 0) {

			alert.setContentText("Same Item is exist with bath no " + batchId.getText().toUpperCase());
			alert.showAndWait();
			return;
		}

		dbUtils.beginTransaction();
		try {

			int quantityInt = Integer.parseInt(quantity.getText());
			float sgstPercentageFloat = Float.parseFloat(sgstPercentage.getText());
			float cgstPercentageFloat = Float.parseFloat(cgstPercentage.getText());
			float discountFloat = 0;
			if (!discount.getText().equals(""))
				discountFloat = Float.parseFloat(discount.getText());
			float grossAmtFloat = Float.parseFloat(grossAmt.getText());
			float purchaseAmtFloat = Float.parseFloat(purchaseRate.getText());

			if (foraStrip.isSelected()) {

				int insStripInt = Integer.parseInt(inaStrip.getText());
				grossAmtFloat = grossAmtFloat / insStripInt;
				String decimalFormat = "#0.00";
				DecimalFormat df = new DecimalFormat(decimalFormat);
				grossAmtFloat = Float.parseFloat(df.format(grossAmtFloat));

				purchaseAmtFloat = purchaseAmtFloat / insStripInt;

				purchaseAmtFloat = Float.parseFloat(df.format(purchaseAmtFloat));

			}
			Date date = new Date(System.currentTimeMillis());

			UUID uid = UUID.randomUUID();

			StockMaster stockMaster = (StockMaster) dbUtils.getSession().get(StockMaster.class,
					itemNameAutofill.getText().trim());
			DealerMaster dealerMaster = (DealerMaster) dbUtils.getSession().get(DealerMaster.class,
					dealerNameAutoFill.getText().trim());
			boolean newStock = false;
			if (stockMaster == null) {
				newStock = true;
				stockMaster = new StockMaster(itemNameAutofill.getText().trim().toUpperCase(),
						categoryAutoFill.getText().trim(), companyNameAutoFill.getText().trim(),
						barcode.getText().trim(), date, date, expdWarningThresholdDaysInt, null);
			} else {
				stockMaster.setCategory(categoryAutoFill.getText());
				stockMaster.setCompanyName(companyNameAutoFill.getText().trim());
				stockMaster.setBarcode(barcode.getText().trim());
				stockMaster.setExpdWarningThresholdDays(expdWarningThresholdDaysInt);
				stockMaster.setUpdatedData(date);
			}
			boolean newDealer = false;
			if (dealerMaster == null) {
				newDealer = true;
				dealerMaster = new DealerMaster(dealerNameAutoFill.getText(), "");
			}

			StockDetails stockDetails = new StockDetails(uid + "", stockMaster, batchId.getText().trim(),
					rack.getText().trim(), dealerNameAutoFill.getText().trim(), mdfDate, expdDate, purchaseAmtFloat,
					grossAmtFloat, discountFloat, sgstPercentageFloat, cgstPercentageFloat, quantityInt, quantityInt);

			Set<StockDetails> stockDetailsList = new HashSet<StockDetails>(0);
			stockDetailsList.add(stockDetails);
			stockMaster.setStockDetails(stockDetailsList);
			if (newStock) {
				dbUtils.getSession().save(stockMaster);
			} else {
				dbUtils.getSession().merge(stockMaster);
			}
			if (newDealer) {
				dbUtils.getSession().save(dealerMaster);
			}
			dbUtils.endTransaction();
			setNewStockAddedFlag();

			MainController.mainController.globalMessage
					.setText(quantityInt + " " + itemNameAutofill.getText() + " is added to the stock successfully");
			clear(null);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public void setNewStockAddedFlag() {

		itemNameAutofill.newStockAdded = true;
		categoryAutoFill.newStockAdded = true;
		dealerNameAutoFill.newStockAdded = true;
		companyNameAutoFill.newStockAdded = true;
		if (SearchEditStockController.searchEditStockController != null) {
			SearchEditStockController.searchEditStockController.itemNameAutofill.newStockAdded = true;
			SearchEditStockController.searchEditStockController.categoryAutoFill.newStockAdded = true;
			SearchEditStockController.searchEditStockController.dealerNameAutoFill.newStockAdded = true;
			SearchEditStockController.searchEditStockController.companyNameAutoFill.newStockAdded = true;
		}

		for (Entry<String, BillingController> entry : BillingController.parallelBilling.entrySet()) {
			entry.getValue().itemNameAutofill.newStockAdded = true;
			entry.getValue().categoryAutoFill.newStockAdded = true;
			entry.getValue().companyNameAutoFill.newStockAdded = true;
		}
		for (Entry<String, SimpleBillingController> entry : SimpleBillingController.parallelBilling.entrySet()) {
			entry.getValue().itemNameAutofill.newStockAdded = true;
		}
		if (DealerManagementController.dealerManagementController != null) {
			DealerManagementController.dealerManagementController.dealerNameAutofill.newStockAdded = true;
		}
	}

	@FXML
	void clear(MouseEvent event) {

		itemNameAutofill.setText("");
		barcode.setText("");
		categoryAutoFill.setText("");
		rack.setText("");
		dealerNameAutoFill.setText("");
		mfd.setValue(null);
		expd.setValue(null);
		companyNameAutoFill.setText("");
		grossAmt.setText("");
		sgstPercentage.setText("");
		cgstPercentage.setText("");
		quantity.setText("");
		discount.setText("0");
		batchId.setText("");
		expdWarningThresholdDays.setText("0");
		inaStrip.setText("");
		purchaseRate.setText("");
	}

	public void initialize() {

		dbUtils = new DBUtils();
		dbSearchUtils = new DBSearchUtils();
		Utils.addTextLimiter(itemNameAutofill, 30);
		Utils.addTextLimiter(batchId, 30);
		Utils.addTextLimiter(barcode, 100);
		Utils.addTextLimiter(categoryAutoFill, 30);
		Utils.addTextLimiter(dealerNameAutoFill, 30);
		Utils.addTextLimiter(companyNameAutoFill, 30);
		Utils.addTextLimiter(expdWarningThresholdDays, 3);
		Utils.addTextLimiter(rack, 30);

		addStockController = this;

		itemNameAutofill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (itemNameAutofill.getText().length() == 0) {
					itemNameAutofill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (itemNameAutofill.newStockAdded == true) {
						String hql = "select  item_name from stock_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						itemNameAutofill.entries.clear();
						for (Object obj : results) {
							itemNameAutofill.entries.add((obj + "").toUpperCase());
						}
						itemNameAutofill.newStockAdded = false;
					}

					for (String eachItem : itemNameAutofill.entries) {

						if (eachItem.toUpperCase().contains(itemNameAutofill.getText().toUpperCase())) {
							searchResult.add(eachItem);
						}
					}
					if (searchResult.size() > 0) {

						List<CustomMenuItem> menuItems = new LinkedList<>();
						// If you'd like more entries, modify this line.
						int maxEntries = 10;
						int count = Math.min(searchResult.size(), maxEntries);
						for (int i = 0; i < count; i++) {
							final String result = String.format("%1$-" + 30 + "s", searchResult.get(i));
							Label entryLabel = new Label(result);
							CustomMenuItem item = new CustomMenuItem(entryLabel, true);
							item.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {

									itemNameAutofill.setText(result);
									itemNameAutofill.entriesPopup.hide();

									StockMaster stockMaster = (StockMaster) dbSearchUtils.getSession()
											.get(StockMaster.class, itemNameAutofill.getText().trim());

									categoryAutoFill.setText(stockMaster.getCategory());
									companyNameAutoFill.setText(stockMaster.getCompanyName());
									barcode.setText(stockMaster.getBarcode());
									expdWarningThresholdDays.setText(stockMaster.getExpdWarningThresholdDays() + "");

								}
							});
							menuItems.add(item);

							itemNameAutofill.entriesPopup.getItems().clear();
							itemNameAutofill.entriesPopup.getItems().addAll(menuItems);

						}
						if (!itemNameAutofill.entriesPopup.isShowing()) {
							itemNameAutofill.entriesPopup.show(itemNameAutofill, Side.BOTTOM, 0, 0);
						}
					} else {
						itemNameAutofill.entriesPopup.hide();
					}
				}
			}
		});

		itemNameAutofill.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				itemNameAutofill.entriesPopup.hide();

			}
		});

		categoryAutoFill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (categoryAutoFill.getText().length() == 0) {
					categoryAutoFill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (categoryAutoFill.newStockAdded == true) {
						String hql = "select distinct category from stock_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						categoryAutoFill.entries.clear();
						for (Object object : results) {
							categoryAutoFill.entries.add((object.toString()).toUpperCase());
						}
						categoryAutoFill.newStockAdded = false;
					}

					for (String eachItem : categoryAutoFill.entries) {

						if (eachItem.toUpperCase().contains(categoryAutoFill.getText().toUpperCase())) {
							searchResult.add(eachItem);
						}
					}
					if (searchResult.size() > 0) {

						List<CustomMenuItem> menuItems = new LinkedList<>();
						// If you'd like more entries, modify this line.
						int maxEntries = 10;
						int count = Math.min(searchResult.size(), maxEntries);
						for (int i = 0; i < count; i++) {
							final String result = String.format("%1$-" + 30 + "s", searchResult.get(i));
							Label entryLabel = new Label(result);
							CustomMenuItem item = new CustomMenuItem(entryLabel, true);
							item.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {
									categoryAutoFill.setText(result);
									categoryAutoFill.entriesPopup.hide();

								}
							});
							menuItems.add(item);

							categoryAutoFill.entriesPopup.getItems().clear();
							categoryAutoFill.entriesPopup.getItems().addAll(menuItems);

						}
						if (!categoryAutoFill.entriesPopup.isShowing()) {
							categoryAutoFill.entriesPopup.show(categoryAutoFill, Side.BOTTOM, 0, 0);
						}
					} else {
						categoryAutoFill.entriesPopup.hide();
					}
				}
			}
		});

		categoryAutoFill.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				categoryAutoFill.entriesPopup.hide();

			}
		});

		dealerNameAutoFill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (dealerNameAutoFill.getText().length() == 0) {
					dealerNameAutoFill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (dealerNameAutoFill.newStockAdded == true) {
						String hql = "select distinct dealer_name from dealer_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						dealerNameAutoFill.entries.clear();
						for (Object object : results) {
							dealerNameAutoFill.entries.add((object.toString()).toUpperCase());
						}
						dealerNameAutoFill.newStockAdded = false;
					}

					for (String eachItem : dealerNameAutoFill.entries) {

						if (eachItem.toUpperCase().contains(dealerNameAutoFill.getText().toUpperCase())) {
							searchResult.add(eachItem);
						}
					}
					if (searchResult.size() > 0) {

						List<CustomMenuItem> menuItems = new LinkedList<>();
						// If you'd like more entries, modify this line.
						int maxEntries = 10;
						int count = Math.min(searchResult.size(), maxEntries);
						for (int i = 0; i < count; i++) {
							final String result = String.format("%1$-" + 30 + "s", searchResult.get(i));
							Label entryLabel = new Label(result);
							CustomMenuItem item = new CustomMenuItem(entryLabel, true);
							item.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {
									dealerNameAutoFill.setText(result);
									dealerNameAutoFill.entriesPopup.hide();

								}
							});
							menuItems.add(item);

							dealerNameAutoFill.entriesPopup.getItems().clear();
							dealerNameAutoFill.entriesPopup.getItems().addAll(menuItems);

						}
						if (!dealerNameAutoFill.entriesPopup.isShowing()) {
							dealerNameAutoFill.entriesPopup.show(dealerNameAutoFill, Side.BOTTOM, 0, 0);
						}
					} else {
						dealerNameAutoFill.entriesPopup.hide();
					}
				}
			}
		});

		dealerNameAutoFill.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				dealerNameAutoFill.entriesPopup.hide();

			}
		});
		companyNameAutoFill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (companyNameAutoFill.getText().length() == 0) {
					companyNameAutoFill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (companyNameAutoFill.newStockAdded == true) {
						String hql = "select distinct company_name  from stock_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						companyNameAutoFill.entries.clear();
						for (Object object : results) {
							companyNameAutoFill.entries.add((object.toString()).toUpperCase());
						}
						companyNameAutoFill.newStockAdded = false;
					}

					for (String eachItem : companyNameAutoFill.entries) {

						if (eachItem.toUpperCase().contains(companyNameAutoFill.getText().toUpperCase())) {
							searchResult.add(eachItem);
						}
					}
					if (searchResult.size() > 0) {

						List<CustomMenuItem> menuItems = new LinkedList<>();
						// If you'd like more entries, modify this line.
						int maxEntries = 10;
						int count = Math.min(searchResult.size(), maxEntries);
						for (int i = 0; i < count; i++) {
							final String result = String.format("%1$-" + 30 + "s", searchResult.get(i));
							Label entryLabel = new Label(result);
							CustomMenuItem item = new CustomMenuItem(entryLabel, true);
							item.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {
									companyNameAutoFill.setText(result);
									companyNameAutoFill.entriesPopup.hide();

								}
							});
							menuItems.add(item);

							companyNameAutoFill.entriesPopup.getItems().clear();
							companyNameAutoFill.entriesPopup.getItems().addAll(menuItems);

						}
						if (!companyNameAutoFill.entriesPopup.isShowing()) {
							companyNameAutoFill.entriesPopup.show(companyNameAutoFill, Side.BOTTOM, 0, 0);
						}
					} else {
						companyNameAutoFill.entriesPopup.hide();
					}
				}
			}
		});

		companyNameAutoFill.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				companyNameAutoFill.entriesPopup.hide();

			}
		});
		clear(null);
	}
}
