package controllers;

import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBUtils;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EditPopupController {
	static Logger logger = Logger.getLogger(EditPopupController.class);
	public static String itemNameInitial;
	public static String batchIdInitial;
	DBUtils dbUtils = null;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField barcode;

	@FXML
	private AutoCompleteTextField categoryAutoFill;

	@FXML
	private TextField expdWarningThresholdDays;

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
	private TextField grossAmt;

	@FXML
	private TextField purchaseAmt;

	@FXML
	private TextField batchId;

	@FXML
	private AutoCompleteTextField itemNameAutofill;

	@FXML
	private AutoCompleteTextField companyNameAutoFill;

	@FXML
	private CheckBox likeSearch;

	@FXML
	private DatePicker mfd;

	@FXML
	private TextField quantity;

	@FXML
	private TextField rack;

	@FXML
	private TextField sgstPercentage;

	@FXML
	private Hyperlink deleteCompleStock;

	@FXML
	void deleteCompleteStock(MouseEvent event) {

		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("REMOVE STOCK");
		String s = "The entire stock of " + (itemNameInitial + "").toUpperCase()
				+ " will be deleted \n Do you want to continue";
		confirmation.setContentText(s);

		Optional<ButtonType> result = confirmation.showAndWait();
		if (!result.isPresent()) {
			return;
		}

		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {

			dbUtils.beginTransaction();

			Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
			criteria.createAlias("stockDetails", "child");

			criteria.add(Restrictions.eq("itemName", itemNameInitial).ignoreCase());

			List<StockMaster> results = criteria.list();
			StockMaster stockMaster = results.get(0);

			dbUtils.getSession().delete(stockMaster);
			dbUtils.endTransaction();
			close(null);
		}

	}

	@FXML
	void close(MouseEvent event) {

		Stage stage = (Stage) itemNameAutofill.getScene().getWindow();
		stage.close();

	}

	@FXML
	void delete(MouseEvent event) throws ParseException {

		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("REMOVE STOCK");
		String s = "Do you really want to remove " + (itemNameInitial + "").toUpperCase() + " with batch no "
				+ (batchIdInitial + "").toUpperCase();
		confirmation.setContentText(s);

		Optional<ButtonType> result = confirmation.showAndWait();
		if (!result.isPresent()) {
			return;
		}

		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {

			dbUtils.beginTransaction();

			Query sqlToDelete = dbUtils.getSession().createSQLQuery("DELETE FROM  STOCK_DETAILS where ITEM_NAME=\""
					+ itemNameInitial + "\"  and BATCH_ID=\"" + batchIdInitial + "\"");
			sqlToDelete.executeUpdate();

			dbUtils.endTransaction();
			MainController.mainController.globalMessage
					.setText("Item " + itemNameInitial + " has been removed from the stock");
			AddStockController.addStockController.setNewStockAddedFlag();

			String tableUsedBy = SearchEditStockController.searchEditStockController.tableUsedBy;
			if (tableUsedBy.equals("SEARCH")) {
				SearchEditStockController.searchEditStockController.searchStock(null);
			} else if (tableUsedBy.equals("RESCENT_SEARCH")) {
				SearchEditStockController.searchEditStockController.showRescentItems(null);
			} else if (tableUsedBy.equals("EXPIRED_SEARCH")) {
				SearchEditStockController.searchEditStockController.showExpireItems(null);
			} else if (tableUsedBy.equals("SHOW_EMPTY_ITEMS")) {
				SearchEditStockController.searchEditStockController.showEmptyItems(null);
			}
			close(null);
		}

	}

	@FXML
	void saveChange(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		int expdWarningThresholdDaysInt = Integer.MAX_VALUE;

		try {
			expdWarningThresholdDaysInt = Integer.parseInt(expdWarningThresholdDays.getText() + "");
			if (expdWarningThresholdDaysInt < 0) {
				alert.setContentText("Expd warning threshold is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText("Expd warning threshold is an invalid value");
		}
		StockMaster stockMaster;
		try {
			int quantityInt = Integer.parseInt(quantity.getText() + "");
			if (quantityInt < 0) {
				alert.setContentText("Quantity is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText("Quantity is an invalid value");
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
			float purchaseAmtInt = Float.parseFloat(purchaseAmt.getText() + "");
			if (purchaseAmtInt < 0) {
				alert.setContentText(alert.getContentText() + "\n" + "Purchase Amt is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText(alert.getContentText() + "\n" + "Purchase Amt is an invalid value");
		}
		try {
			float discountFloat = Float.parseFloat(discount.getText() + "");
			if (discountFloat < 0) {
				alert.setContentText("Discount is an invalid value");
			}
		} catch (Exception e) {
			alert.setContentText("Discount is an invalid value");
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

		if (itemNameAutofill.getText().equals("") || dealerNameAutoFill.getText().equals("")
				|| companyNameAutoFill.getText().equals("") || sgstPercentage.getText().equals("")
				|| cgstPercentage.getText().equals("") || quantity.getText().equals("") || grossAmt.getText().equals("")
				|| expd.getValue() == null || batchId.getText() == null || mfd.getValue() == null) {
			alert.setContentText(
					"Item Name,BatchId,Dealer Name,Company Name,MFD,EXPD,Gross Amt,S-GST,C-GST,Quantity are mandatary fields");
		}
		if (!alert.getContentText().trim().equals("")) {
			alert.showAndWait();
			return;
		}

		try {

			dbUtils.beginTransaction();

			Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
			criteria.createAlias("stockDetails", "child");

			criteria.add(Restrictions.eq("itemName", itemNameInitial).ignoreCase());

			criteria.add(Restrictions.eq("child.batchId", batchIdInitial).ignoreCase());

			List<StockMaster> results = criteria.list();
			stockMaster = results.get(0);

			int quantityInt = Integer.parseInt(quantity.getText());
			float sgstPercentageFloat = Float.parseFloat(sgstPercentage.getText());
			float cgstPercentageFloat = Float.parseFloat(cgstPercentage.getText());
			float discountFloat = 0;
			if (!discount.getText().equals(""))
				discountFloat = Float.parseFloat(discount.getText());
			float grossAmtFloat = Float.parseFloat(grossAmt.getText());
			float purchaseAmtFloat = Float.parseFloat(purchaseAmt.getText());

			Date date = new Date(System.currentTimeMillis());

			if (!itemNameInitial.equals(itemNameAutofill.getText().trim())) {
				String hql = "update stock_master set item_name=? where item_name=?";
				Query query = dbUtils.getSession().createSQLQuery(hql);
				query.setString(0, itemNameAutofill.getText());
				query.setString(1, itemNameInitial);
				query.executeUpdate();
				dbUtils.endTransaction();
				dbUtils.beginTransaction();
				itemNameInitial = itemNameAutofill.getText();
				AddStockController.addStockController.setNewStockAddedFlag();
			}

			LocalDate localDateMfd = mfd.getValue();
			Instant instantMfd = Instant.from(localDateMfd.atStartOfDay(ZoneId.systemDefault()));
			java.util.Date mdfDate = Date.from(instantMfd);

			LocalDate localDateExpd = expd.getValue();
			Instant instantExpd = Instant.from(localDateExpd.atStartOfDay(ZoneId.systemDefault()));
			java.util.Date expdDate = Date.from(instantExpd);

			stockMaster.setCategory(categoryAutoFill.getText());
			stockMaster.setBarcode(barcode.getText());
			stockMaster.setUpdatedData(date);
			stockMaster.setCompanyName(companyNameAutoFill.getText());
			stockMaster.setExpdWarningThresholdDays(expdWarningThresholdDaysInt);
			Set<StockDetails> stockDetails = stockMaster.getStockDetails();

			Set<StockDetails> stockDetailsUpdatedList = new HashSet<>();
			for (StockDetails stockDetails2 : stockDetails) {

				if (stockDetails2.getBatchId().equals(batchId.getText())) {

					StockDetails stockDetailsUpdated = new StockDetails(stockDetails2.getUid() + "", stockMaster,
							stockDetails2.getBatchId(), rack.getText(), dealerNameAutoFill.getText(), mdfDate, expdDate,
							purchaseAmtFloat, grossAmtFloat, discountFloat, sgstPercentageFloat, cgstPercentageFloat,
							quantityInt, quantityInt);
					stockDetailsUpdatedList.add(stockDetailsUpdated);
				}
				stockDetailsUpdatedList.add(stockDetails2);
			}
			stockMaster.setStockDetails(stockDetailsUpdatedList);
			dbUtils.getSession().merge(stockMaster);

			DealerMaster dealerMaster = (DealerMaster) dbUtils.getSession().get(DealerMaster.class,
					dealerNameAutoFill.getText().trim());

			boolean newDealer = false;
			if (dealerMaster == null) {
				newDealer = true;
				dealerMaster = new DealerMaster(dealerNameAutoFill.getText(), "");
			}
			if (newDealer) {
				dbUtils.getSession().save(dealerMaster);
			}
			dbUtils.endTransaction();
			AddStockController.addStockController.setNewStockAddedFlag();

			MainController.mainController.globalMessage
					.setText(itemNameAutofill.getText() + " is updated  successfully");

			String tableUsedBy = SearchEditStockController.searchEditStockController.tableUsedBy;
			if (tableUsedBy.equals("SEARCH")) {
				SearchEditStockController.searchEditStockController.searchStock(null);
			} else if (tableUsedBy.equals("RESCENT_SEARCH")) {
				SearchEditStockController.searchEditStockController.showRescentItems(null);
			} else if (tableUsedBy.equals("EXPIRED_SEARCH")) {
				SearchEditStockController.searchEditStockController.showExpireItems(null);
			} else if (tableUsedBy.equals("SHOW_EMPTY_ITEMS")) {
				SearchEditStockController.searchEditStockController.showEmptyItems(null);
			}
			Stage stage = (Stage) itemNameAutofill.getScene().getWindow();
			stage.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@FXML
	void initialize() {

		deleteCompleStock.setTextFill(Color.web("#e08f0d"));
		dbUtils = new DBUtils();
		Utils.addTextLimiter(barcode, 100);
		Utils.addTextLimiter(categoryAutoFill, 30);
		Utils.addTextLimiter(dealerNameAutoFill, 30);
		Utils.addTextLimiter(companyNameAutoFill, 30);
		Utils.addTextLimiter(expdWarningThresholdDays, 3);
		deleteCompleStock.setText("Delete complete stock of item " + itemNameInitial);

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
							final String result = searchResult.get(i);
							Label entryLabel = new Label(result);
							CustomMenuItem item = new CustomMenuItem(entryLabel, true);
							item.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {

									itemNameAutofill.setText(result);
									itemNameAutofill.entriesPopup.hide();

									StockMaster stockMaster = (StockMaster) dbUtils.getSession().get(StockMaster.class,
											itemNameAutofill.getText().trim());

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
							final String result = searchResult.get(i);
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
							final String result = searchResult.get(i);
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
							final String result = searchResult.get(i);
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

		populateData();

	}

	void populateData() {

		Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
		criteria.createAlias("stockDetails", "child");

		criteria.add(Restrictions.eq("itemName", itemNameInitial).ignoreCase());

		criteria.add(Restrictions.eq("child.batchId", batchIdInitial).ignoreCase());

		List<StockMaster> results = (List<StockMaster>) criteria.list();
		Stock s = null;

		StockMaster sm = (StockMaster) results.get(0);
		Set<StockDetails> stockDetailsList = sm.getStockDetails();

		for (StockDetails stockDetails : stockDetailsList) {
			if (stockDetails.getBatchId().equals(batchIdInitial + ""))

				s = new Stock(sm.getItemName(), stockDetails.getBatchId() + "", sm.getBarcode() + "",
						sm.getCategory() + "", stockDetails.getRack() + "", stockDetails.getDealerName() + "",
						sm.getCompanyName() + "", stockDetails.getMfd() + "", stockDetails.getExpd() + "",
						stockDetails.getPurchaseAmt() + "", stockDetails.getGrossAmt() + "",
						stockDetails.getDiscount() + "", stockDetails.getSgstPercentage() + "",
						stockDetails.getCgstPercentage() + "", sm.getCreatedData() + "", sm.getUpdatedData() + "",
						stockDetails.getQuantityAvaialble() + "", sm.getExpdWarningThresholdDays() + "");

		}

		itemNameAutofill.setText(s.itemNameProperty().getValue());
		batchId.setText(s.batchIdProperty().getValue());
		barcode.setText(s.barCodeProperty().getValue());
		categoryAutoFill.setText(s.categoryProperty().getValue());
		rack.setText(s.rackProperty().getValue());
		dealerNameAutoFill.setText(s.dealerNameProperty().getValue());
		companyNameAutoFill.setText(s.companyNameProperty().getValue());
		expdWarningThresholdDays.setText(s.expdWarningThresholdDaysProperty().getValue());
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;

		String mfdString = s.mfdProperty().getValue();

		try {
			date = format.parse(mfdString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		LocalDate dateMfd = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		mfd.setValue(dateMfd);

		String expdString = s.expdProperty().getValue();

		try {
			date = format.parse(expdString);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

		LocalDate dateExpd = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		expd.setValue(dateExpd);

		grossAmt.setText(s.grossAmtProperty().getValue());
		purchaseAmt.setText(s.purchaseAmtProperty().getValue());

		discount.setText(s.discountProperty().getValue());
		sgstPercentage.setText(s.sGstProperty().getValue());
		cgstPercentage.setText(s.cGstProperty().getValue());
		quantity.setText(s.quantityProperty().getValue());

	}

	@FXML
	void clear(MouseEvent event) {

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
		discount.setText("");
		expdWarningThresholdDays.setText("");

	}
}
