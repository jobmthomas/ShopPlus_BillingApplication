package controllers;

import java.io.FileInputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBSearchUtils;
import com.shopplus.Main;
import com.shopplus.Utils;
import com.shopplus.hibernate.StockDetails;
import com.shopplus.hibernate.StockMaster;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SearchEditStockController implements Initializable {

	static Logger logger = Logger.getLogger(SearchEditStockController.class);
	DBSearchUtils dbUtils = null;
	public String tableUsedBy = "";
	public static SearchEditStockController searchEditStockController;

	@FXML
	public AutoCompleteTextField categoryAutoFill;

	@FXML
	public AutoCompleteTextField dealerNameAutoFill;

	@FXML
	public AutoCompleteTextField companyNameAutoFill;

	@FXML
	public AutoCompleteTextField itemNameAutofill;

	@FXML
	private DatePicker stockIntakeDate;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableColumn<Stock, String> barCodeColumn;

	@FXML
	private TableColumn<Stock, String> batchIdColumn;

	@FXML
	private TableColumn<Stock, String> cGstColumn;

	@FXML
	private TableColumn<Stock, String> categoryColumn;

	@FXML
	private TableColumn<Stock, String> dealerNameColumn;

	@FXML
	private TableColumn<Stock, String> companyNameColumn;

	@FXML
	private TableColumn<Stock, String> discountColumn;

	@FXML
	private TableView<Stock> editTable;

	@FXML
	private TableColumn<Stock, String> expdColumn;

	@FXML
	private TableColumn<Stock, String> grossAmtColumn;

	@FXML
	private TableColumn<Stock, String> itemNameColumn;

	@FXML
	private CheckBox likeSearch;

	@FXML
	private TableColumn<Stock, String> mfdColumn;

	@FXML
	private TableColumn<Stock, String> quantityColumn;

	@FXML
	private TableColumn<Stock, String> quantityRecentlyAddedColumn;

	@FXML
	private TableColumn<Stock, String> rackColumn;

	@FXML
	private TableColumn<Stock, String> sGstColumn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Utils.addTextLimiter(itemNameAutofill, 30);
		Utils.addTextLimiter(categoryAutoFill, 30);
		Utils.addTextLimiter(dealerNameAutoFill, 30);
		Utils.addTextLimiter(companyNameAutoFill, 30);

		searchEditStockController = this;
		dbUtils = new DBSearchUtils();
		itemNameAutofill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (itemNameAutofill.getText().length() == 0) {
					itemNameAutofill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (itemNameAutofill.newStockAdded == true) {
						String hql = "select distinct item_name from stock_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						itemNameAutofill.entries.clear();
						for (Object obj : results) {
							itemNameAutofill.entries.add(obj + "");
						}
						itemNameAutofill.newStockAdded = false;
					}

					for (String eachItem : itemNameAutofill.entries) {

						if (eachItem.toLowerCase().contains(itemNameAutofill.getText().toLowerCase())) {
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
						String hql = "select  distinct category from stock_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						categoryAutoFill.entries.clear();
						for (Object obj : results) {
							categoryAutoFill.entries.add(obj + "");
						}
						categoryAutoFill.newStockAdded = false;
					}

					for (String eachItem : categoryAutoFill.entries) {

						if (eachItem.toLowerCase().contains(categoryAutoFill.getText().toLowerCase())) {
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
									// populateTable(null);
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
						String hql = "select  distinct dealer_name from stock_details";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						dealerNameAutoFill.entries.clear();
						for (Object obj : results) {
							dealerNameAutoFill.entries.add(obj + "");
						}
						dealerNameAutoFill.newStockAdded = false;
					}

					for (String eachItem : dealerNameAutoFill.entries) {

						if (eachItem.toLowerCase().contains(dealerNameAutoFill.getText().toLowerCase())) {
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
									// populateTable(null);
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
						String hql = "select  distinct company_name from stock_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						companyNameAutoFill.entries.clear();
						for (Object obj : results) {
							companyNameAutoFill.entries.add(obj + "");
						}
						companyNameAutoFill.newStockAdded = false;
					}

					for (String eachItem : companyNameAutoFill.entries) {

						if (eachItem.toLowerCase().contains(companyNameAutoFill.getText().toLowerCase())) {
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
									// populateTable(null);
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
		editTable.setRowFactory(tv -> {
			TableRow<Stock> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Stock rowData = editTable.getSelectionModel().getSelectedItem();
					EditPopupController.itemNameInitial = rowData.itemNameProperty().getValue();
					EditPopupController.batchIdInitial = rowData.batchIdProperty().getValue();

					try {

						Parent root = FXMLLoader.load(getClass().getResource("EditStockPopup.fxml"));
						FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditStockPopup.fxml"));
						Parent root1 = (Parent) fxmlLoader.load();
						Stage stage = new Stage();
						stage.initModality(Modality.APPLICATION_MODAL);
						// stage.initStyle(StageStyle.UNDECORATED);
						stage.setResizable(false);
						stage.setTitle("Edit Stock");
						Scene scene = new Scene(root);
						scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));
						FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
						stage.getIcons().add(new Image(f));
						stage.setScene(scene);
						stage.showAndWait();

					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			});
			return row;
		});
	}

	@FXML
	void showRescentItems(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		tableUsedBy = "RESCENT_SEARCH";

		String sqlQueryEqual = "select this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD, child1_.PURCHASE_AMT as PURCHASE_AMT, child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_ on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

		String sqlQueryLike = "select  this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD, child1_.PURCHASE_AMT as PURCHASE_AMT, child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_   on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

		if (!itemNameAutofill.getText().equals("")) {

			sqlQueryEqual = sqlQueryEqual + " AND this_.ITEM_NAME =\"" + itemNameAutofill.getText() + "\"";
			sqlQueryLike = sqlQueryLike + " AND this_.ITEM_NAME  like \"%" + itemNameAutofill.getText() + "%\"";
		}
		if (!categoryAutoFill.getText().equals("")) {

			sqlQueryEqual = sqlQueryEqual + " AND this_.CATEGORY =\"" + categoryAutoFill.getText() + "\"";
			sqlQueryLike = sqlQueryLike + " AND this_.CATEGORY  like \"%" + categoryAutoFill.getText() + "%\"";
		}

		if (!companyNameAutoFill.getText().equals("")) {
			sqlQueryEqual = sqlQueryEqual + " AND this_.COMPANY_NAME =\"" + companyNameAutoFill.getText() + "\"";
			sqlQueryLike = sqlQueryLike + " AND this_.COMPANY_NAME  like \"%" + companyNameAutoFill.getText() + "%\"";
		}

		sqlQueryEqual = sqlQueryEqual + "  order by this_.UPDATED_DATE desc";
		sqlQueryLike = sqlQueryLike + "  order by this_.UPDATED_DATE desc";

		if (Main.prop.getProperty("apply_limit_to_data_fetch").equals("true")) {
			sqlQueryLike = sqlQueryLike + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
			sqlQueryEqual = sqlQueryEqual + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
		}
		Query query = null;
		if (likeSearch.isSelected()) {
			query = dbUtils.getSession().createSQLQuery(sqlQueryLike)

					.addScalar("ITEM_NAME", Hibernate.STRING).addScalar("CATEGORY", Hibernate.STRING)
					.addScalar("COMPANY_NAME", Hibernate.STRING).addScalar("BARCODE", Hibernate.STRING)
					.addScalar("CREATED_DATE", Hibernate.STRING).addScalar("UPDATED_DATE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING).addScalar("UID", Hibernate.STRING)
					.addScalar("BATCH_ID", Hibernate.STRING).addScalar("RACK", Hibernate.STRING)
					.addScalar("DEALER_NAME", Hibernate.STRING).addScalar("MFD", Hibernate.STRING)
					.addScalar("EXPD", Hibernate.STRING).addScalar("GROSS_AMT", Hibernate.STRING)
					.addScalar("PURCHASE_AMT", Hibernate.STRING).addScalar("DISCOUNT", Hibernate.STRING)
					.addScalar("S_GST_PERCENTAGE", Hibernate.STRING).addScalar("C_GST_PERCENTAGE", Hibernate.STRING)
					.addScalar("QUANTITY_AVAILABLE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING);
		} else {
			query = dbUtils.getSession().createSQLQuery(sqlQueryEqual).addScalar("ITEM_NAME", Hibernate.STRING)
					.addScalar("CATEGORY", Hibernate.STRING).addScalar("COMPANY_NAME", Hibernate.STRING)
					.addScalar("BARCODE", Hibernate.STRING).addScalar("CREATED_DATE", Hibernate.STRING)
					.addScalar("UPDATED_DATE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING).addScalar("UID", Hibernate.STRING)
					.addScalar("BATCH_ID", Hibernate.STRING).addScalar("RACK", Hibernate.STRING)
					.addScalar("DEALER_NAME", Hibernate.STRING).addScalar("MFD", Hibernate.STRING)
					.addScalar("EXPD", Hibernate.STRING).addScalar("PURCHASE_AMT", Hibernate.STRING)
					.addScalar("GROSS_AMT", Hibernate.STRING).addScalar("DISCOUNT", Hibernate.STRING)
					.addScalar("S_GST_PERCENTAGE", Hibernate.STRING).addScalar("C_GST_PERCENTAGE", Hibernate.STRING)
					.addScalar("QUANTITY_AVAILABLE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING);
		}

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		List<Stock> stockList = new ArrayList<Stock>();
		List obj = query.list();
		for (Object object : obj) {

			Map<Object, Object> sm = (Map<Object, Object>) object;

			Stock s = new Stock(sm.get("ITEM_NAME") + "", sm.get("BATCH_ID") + "", sm.get("BARCODE") + "",
					sm.get("CATEGORY") + "", sm.get("RACK") + "", sm.get("DEALER_NAME") + "",
					sm.get("COMPANY_NAME") + "", sm.get("MFD") + "", sm.get("EXPD") + "", sm.get("PURCHASE_AMT") + "",
					sm.get("GROSS_AMT") + "", sm.get("DISCOUNT") + "", sm.get("S_GST_PERCENTAGE") + "",
					sm.get("C_GST_PERCENTAGE") + "", sm.get("CREATED_DATE") + "", sm.get("UPDATED_DATE") + "",
					sm.get("QUANTITY_AVAILABLE") + "", sm.get("EXPD_WARNING_THRESHOLD_DAYS") + "");

			stockList.add(s);

		}

		itemNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("itemName"));
		itemNameColumn.setEditable(true);

		batchIdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("batchId"));
		batchIdColumn.setEditable(true);

		barCodeColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("barCode"));
		barCodeColumn.setEditable(true);

		categoryColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("category"));
		categoryColumn.setEditable(true);

		rackColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("rack"));
		rackColumn.setEditable(true);

		dealerNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("dealerName"));
		dealerNameColumn.setEditable(true);

		companyNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("companyName"));
		companyNameColumn.setEditable(true);

		mfdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("mfd"));
		mfdColumn.setEditable(true);

		expdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("expd"));
		expdColumn.setEditable(true);

		grossAmtColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("grossAmt"));
		grossAmtColumn.setEditable(true);

		discountColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("discount"));
		discountColumn.setEditable(true);

		sGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("sGst"));
		sGstColumn.setEditable(true);

		cGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("cGst"));
		cGstColumn.setEditable(true);

		quantityColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("quantity"));
		quantityColumn.setEditable(true);

		ObservableList<Stock> itemLisst = editTable.getItems();
		itemLisst.clear();
		itemLisst.setAll(stockList);
		editTable.setItems(itemLisst);
	}

	@FXML
	void showEmptyItems(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		tableUsedBy = "SHOW_EMPTY_ITEMS";

		String sqlQueryEqual = "select this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD, child1_.PURCHASE_AMT as PURCHASE_AMT, child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_ on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

		sqlQueryEqual = sqlQueryEqual
				+ " AND  child1_.QUANTITY_AVAILABLE =0  group by child1_.ITEM_NAME,child1_.batch_id order by this_.updated_date  asc";

		if (Main.prop.getProperty("apply_limit_to_data_fetch").equals("true")) {
			sqlQueryEqual = sqlQueryEqual + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
		}
		Query query = null;

		query = dbUtils.getSession().createSQLQuery(sqlQueryEqual).addScalar("ITEM_NAME", Hibernate.STRING)
				.addScalar("CATEGORY", Hibernate.STRING).addScalar("COMPANY_NAME", Hibernate.STRING)
				.addScalar("BARCODE", Hibernate.STRING).addScalar("CREATED_DATE", Hibernate.STRING)
				.addScalar("UPDATED_DATE", Hibernate.STRING).addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING)
				.addScalar("UID", Hibernate.STRING).addScalar("BATCH_ID", Hibernate.STRING)
				.addScalar("RACK", Hibernate.STRING).addScalar("DEALER_NAME", Hibernate.STRING)
				.addScalar("MFD", Hibernate.STRING).addScalar("EXPD", Hibernate.STRING)
				.addScalar("PURCHASE_AMT", Hibernate.STRING).addScalar("GROSS_AMT", Hibernate.STRING)
				.addScalar("DISCOUNT", Hibernate.STRING).addScalar("S_GST_PERCENTAGE", Hibernate.STRING)
				.addScalar("C_GST_PERCENTAGE", Hibernate.STRING).addScalar("QUANTITY_AVAILABLE", Hibernate.STRING)
				.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING);

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		List<Stock> stockList = new ArrayList<Stock>();
		List obj = query.list();
		for (Object object : obj) {

			Map<Object, Object> sm = (Map<Object, Object>) object;

			Stock s = new Stock(sm.get("ITEM_NAME") + "", sm.get("BATCH_ID") + "", sm.get("BARCODE") + "",
					sm.get("CATEGORY") + "", sm.get("RACK") + "", sm.get("DEALER_NAME") + "",
					sm.get("COMPANY_NAME") + "", sm.get("MFD") + "", sm.get("EXPD") + "", sm.get("PURCHASE_AMT") + "",
					sm.get("GROSS_AMT") + "", sm.get("DISCOUNT") + "", sm.get("S_GST_PERCENTAGE") + "",
					sm.get("C_GST_PERCENTAGE") + "", sm.get("CREATED_DATE") + "", sm.get("UPDATED_DATE") + "",
					sm.get("QUANTITY_AVAILABLE") + "", sm.get("EXPD_WARNING_THRESHOLD_DAYS") + "");

			stockList.add(s);

		}

		itemNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("itemName"));
		itemNameColumn.setEditable(false);

		batchIdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("batchId"));
		batchIdColumn.setEditable(false);

		barCodeColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("barCode"));
		barCodeColumn.setEditable(false);

		categoryColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("category"));
		categoryColumn.setEditable(false);

		rackColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("rack"));
		rackColumn.setEditable(false);

		dealerNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("dealerName"));
		dealerNameColumn.setEditable(false);

		companyNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("companyName"));
		companyNameColumn.setEditable(false);

		mfdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("mfd"));
		mfdColumn.setEditable(false);

		expdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("expd"));
		expdColumn.setEditable(false);

		grossAmtColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("grossAmt"));
		grossAmtColumn.setEditable(false);

		discountColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("discount"));
		discountColumn.setEditable(false);

		sGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("sGst"));
		sGstColumn.setEditable(false);

		cGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("cGst"));
		cGstColumn.setEditable(false);

		quantityColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("quantity"));
		quantityColumn.setEditable(false);

		ObservableList<Stock> itemLisst = editTable.getItems();
		itemLisst.clear();
		itemLisst.setAll(stockList);
		editTable.setItems(itemLisst);

	}

	private void populateTable(List<StockMaster> results) {
		if (!results.isEmpty()) {
			itemNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("itemName"));
			itemNameColumn.setEditable(false);

			batchIdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("batchId"));
			batchIdColumn.setEditable(false);

			barCodeColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("barCode"));
			barCodeColumn.setEditable(false);

			categoryColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("category"));
			categoryColumn.setEditable(false);

			rackColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("rack"));
			rackColumn.setEditable(false);

			dealerNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("dealerName"));
			dealerNameColumn.setEditable(false);

			companyNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("companyName"));
			companyNameColumn.setEditable(false);

			mfdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("mfd"));
			mfdColumn.setEditable(false);

			expdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("expd"));
			expdColumn.setEditable(false);

			grossAmtColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("grossAmt"));
			grossAmtColumn.setEditable(false);

			discountColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("discount"));
			discountColumn.setEditable(false);

			sGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("sGst"));
			sGstColumn.setEditable(false);

			cGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("cGst"));
			cGstColumn.setEditable(false);

			quantityColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("quantity"));
			quantityColumn.setEditable(false);

			List<Stock> stockList = new ArrayList<>();

			for (Object obj : results) {
				StockMaster sm = (StockMaster) obj;

				Set<StockDetails> stockDetailsList = sm.getStockDetails();

				for (StockDetails stockDetails : stockDetailsList) {

					boolean matchingBatchIdFound = false;
					for (Stock stock : stockList) {
						if (stock.itemNameProperty().getValue().equals(stockDetails.getStockMaster().getItemName())
								&& stock.batchIdProperty().getValue().equals(stockDetails.getBatchId())) {
							matchingBatchIdFound = true;

						}
					}

					if (!matchingBatchIdFound) {
						Stock s = new Stock(sm.getItemName(), stockDetails.getBatchId() + "", sm.getBarcode() + "",
								sm.getCategory() + "", stockDetails.getRack() + "", stockDetails.getDealerName() + "",
								sm.getCompanyName() + "", stockDetails.getMfd() + "", stockDetails.getExpd() + "",
								stockDetails.getPurchaseAmt() + "", stockDetails.getGrossAmt() + "",
								stockDetails.getDiscount() + "", stockDetails.getSgstPercentage() + "",
								stockDetails.getCgstPercentage() + "", sm.getCreatedData() + "",
								sm.getUpdatedData() + "", stockDetails.getQuantityAvaialble() + "",
								sm.getExpdWarningThresholdDays() + "");
						stockList.add(s);
					}

				}
			}
			editTable.getItems().setAll(stockList);

		} else {
			List<Stock> stockList = new ArrayList<>(0);
			editTable.getItems().setAll(stockList);
		}

	}

	@FXML
	void searchStock(MouseEvent event) throws ParseException {
		searchStock();
	}

	void searchStock() throws ParseException {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		tableUsedBy = "SEARCH";
		Session session = dbUtils.getSession();
		Criteria criteria = session.createCriteria(StockMaster.class);
		criteria.createAlias("stockDetails", "child");

		Criteria criteriaLike = session.createCriteria(StockMaster.class);
		criteriaLike.createAlias("stockDetails", "child");

		if (Main.prop.getProperty("apply_limit_to_data_fetch").equals("true")) {
			int limit = Integer.parseInt(Main.prop.getProperty("bulk_data_search_limit"));
			criteria.setMaxResults(limit);
			criteriaLike.setMaxResults(limit);
		}

		if (!itemNameAutofill.getText().equals("")) {
			criteria.add(Restrictions.eq("itemName", itemNameAutofill.getText()).ignoreCase());
			criteriaLike.add(Restrictions.like("itemName", "%" + itemNameAutofill.getText() + "%").ignoreCase());
		}
		if (!categoryAutoFill.getText().equals("")) {

			criteria.add(Restrictions.eq("category", categoryAutoFill.getText()).ignoreCase());
			criteriaLike.add(Restrictions.like("category", "%" + categoryAutoFill.getText() + "%").ignoreCase());
		}
		if (!dealerNameAutoFill.getText().equals("")) {
			criteria.add(Restrictions.eq("child.dealerName", dealerNameAutoFill.getText()).ignoreCase());
			criteriaLike
					.add(Restrictions.like("child.dealerName", "%" + dealerNameAutoFill.getText() + "%").ignoreCase());
		}

		if (!companyNameAutoFill.getText().equals("")) {
			criteria.add(Restrictions.eq("companyName", companyNameAutoFill.getText()).ignoreCase());
			criteriaLike.add(Restrictions.like("companyName", "%" + companyNameAutoFill.getText() + "%").ignoreCase());
		}

		if (stockIntakeDate.getValue() != null) {

			LocalDate localDate = stockIntakeDate.getValue();
			Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, 1);
			Date datePlusOne = c.getTime();

			c.setTime(date);
			c.add(Calendar.DATE, -1);
			Date dateMinusOne = c.getTime();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateStringPlusOne = sdf.format(datePlusOne);
			datePlusOne = sdf.parse(dateStringPlusOne);

			String dateStringMinusOne = sdf.format(datePlusOne);
			dateMinusOne = sdf.parse(dateStringMinusOne);
			// System.out.println(dateMinusOne);
			// System.out.println(date);
			// System.out.println(datePlusOne);
			criteria.add(Restrictions.lt("createdData", datePlusOne));
			criteria.add(Restrictions.ge("createdData", date));

			criteriaLike.add(Restrictions.lt("createdData", datePlusOne));
			criteriaLike.add(Restrictions.gt("createdData", date));

		}
		List<StockMaster> results;
		if (!likeSearch.isSelected()) {
			results = (List<StockMaster>) criteria.list();

		} else {
			results = (List<StockMaster>) criteriaLike.list();
		}

		populateTable(results);
	}

	@FXML
	void showExpireItems(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		tableUsedBy = "EXPIRED_SEARCH";

		String sqlQueryEqual = "select this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD,child1_.PURCHASE_AMT as PURCHASE_AMT, child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_ on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

		String sqlQueryLike = "select  this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD,child1_.PURCHASE_AMT as PURCHASE_AMT, child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_   on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

		if (!itemNameAutofill.getText().equals("")) {

			sqlQueryEqual = sqlQueryEqual + " AND this_.ITEM_NAME =\"" + itemNameAutofill.getText() + "\"";
			sqlQueryLike = sqlQueryLike + " AND this_.ITEM_NAME  like \"%" + itemNameAutofill.getText() + "%\"";
		}
		if (!categoryAutoFill.getText().equals("")) {

			sqlQueryEqual = sqlQueryEqual + " AND this_.CATEGORY =\"" + categoryAutoFill.getText() + "\"";
			sqlQueryLike = sqlQueryLike + " AND this_.CATEGORY  like \"%" + categoryAutoFill.getText() + "%\"";
		}

		if (!companyNameAutoFill.getText().equals("")) {
			sqlQueryEqual = sqlQueryEqual + " AND this_.COMPANY_NAME =\"" + companyNameAutoFill.getText() + "\"";
			sqlQueryLike = sqlQueryLike + " AND this_.COMPANY_NAME  like \"%" + companyNameAutoFill.getText() + "%\"";
		}

		sqlQueryEqual = sqlQueryEqual + "  order by child1_.QUANTITY_AVAILABLE asc";
		sqlQueryLike = sqlQueryLike + "   order by child1_.QUANTITY_AVAILABLE asc";

		if (Main.prop.getProperty("apply_limit_to_data_fetch").equals("true")) {
			sqlQueryLike = sqlQueryLike + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
			sqlQueryEqual = sqlQueryEqual + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
		}
		Query query = null;
		if (likeSearch.isSelected()) {
			query = dbUtils.getSession().createSQLQuery(sqlQueryLike)

					.addScalar("ITEM_NAME", Hibernate.STRING).addScalar("CATEGORY", Hibernate.STRING)
					.addScalar("COMPANY_NAME", Hibernate.STRING).addScalar("BARCODE", Hibernate.STRING)
					.addScalar("CREATED_DATE", Hibernate.STRING).addScalar("UPDATED_DATE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING).addScalar("UID", Hibernate.STRING)
					.addScalar("BATCH_ID", Hibernate.STRING).addScalar("RACK", Hibernate.STRING)
					.addScalar("DEALER_NAME", Hibernate.STRING).addScalar("MFD", Hibernate.STRING)
					.addScalar("EXPD", Hibernate.STRING).addScalar("GROSS_AMT", Hibernate.STRING)
					.addScalar("PURCHASE_AMT", Hibernate.STRING).addScalar("DISCOUNT", Hibernate.STRING)
					.addScalar("S_GST_PERCENTAGE", Hibernate.STRING).addScalar("C_GST_PERCENTAGE", Hibernate.STRING)
					.addScalar("QUANTITY_AVAILABLE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING);
		} else {
			query = dbUtils.getSession().createSQLQuery(sqlQueryEqual).addScalar("ITEM_NAME", Hibernate.STRING)
					.addScalar("CATEGORY", Hibernate.STRING).addScalar("COMPANY_NAME", Hibernate.STRING)
					.addScalar("BARCODE", Hibernate.STRING).addScalar("CREATED_DATE", Hibernate.STRING)
					.addScalar("UPDATED_DATE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING).addScalar("UID", Hibernate.STRING)
					.addScalar("BATCH_ID", Hibernate.STRING).addScalar("RACK", Hibernate.STRING)
					.addScalar("DEALER_NAME", Hibernate.STRING).addScalar("MFD", Hibernate.STRING)
					.addScalar("EXPD", Hibernate.STRING).addScalar("GROSS_AMT", Hibernate.STRING)
					.addScalar("PURCHASE_AMT", Hibernate.STRING).addScalar("DISCOUNT", Hibernate.STRING)
					.addScalar("S_GST_PERCENTAGE", Hibernate.STRING).addScalar("C_GST_PERCENTAGE", Hibernate.STRING)
					.addScalar("QUANTITY_AVAILABLE", Hibernate.STRING)
					.addScalar("EXPD_WARNING_THRESHOLD_DAYS", Hibernate.STRING);
		}

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		List<Stock> stockList = new ArrayList<Stock>();
		List obj = query.list();
		for (Object object : obj) {

			Map<Object, Object> sm = (Map<Object, Object>) object;

			Stock s = new Stock(sm.get("ITEM_NAME") + "", sm.get("BATCH_ID") + "", sm.get("BARCODE") + "",
					sm.get("CATEGORY") + "", sm.get("RACK") + "", sm.get("DEALER_NAME") + "",
					sm.get("COMPANY_NAME") + "", sm.get("MFD") + "", sm.get("EXPD") + "", sm.get("PURCHASE_AMT") + "",
					sm.get("GROSS_AMT") + "", sm.get("DISCOUNT") + "", sm.get("S_GST_PERCENTAGE") + "",
					sm.get("C_GST_PERCENTAGE") + "", sm.get("CREATED_DATE") + "", sm.get("UPDATED_DATE") + "",
					sm.get("QUANTITY_AVAILABLE") + "", sm.get("EXPD_WARNING_THRESHOLD_DAYS") + "");

			stockList.add(s);

		}

		itemNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("itemName"));
		itemNameColumn.setEditable(true);

		batchIdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("batchId"));
		batchIdColumn.setEditable(true);

		barCodeColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("barCode"));
		barCodeColumn.setEditable(true);

		categoryColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("category"));
		categoryColumn.setEditable(true);

		rackColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("rack"));
		rackColumn.setEditable(true);

		dealerNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("dealerName"));
		dealerNameColumn.setEditable(true);

		companyNameColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("companyName"));
		companyNameColumn.setEditable(true);

		mfdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("mfd"));
		mfdColumn.setEditable(true);

		expdColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("expd"));
		expdColumn.setEditable(true);

		grossAmtColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("grossAmt"));
		grossAmtColumn.setEditable(true);

		discountColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("discount"));
		discountColumn.setEditable(true);

		sGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("sGst"));
		sGstColumn.setEditable(true);

		cGstColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("cGst"));
		cGstColumn.setEditable(true);

		quantityColumn.setCellValueFactory(new PropertyValueFactory<Stock, String>("quantity"));
		quantityColumn.setEditable(true);

		ObservableList<Stock> itemLisst = editTable.getItems();
		itemLisst.clear();
		itemLisst.setAll(stockList);

		editTable.setItems(itemLisst);

	}

	public void setNewStockAddedFlag() {

		SearchEditStockController.searchEditStockController.itemNameAutofill.newStockAdded = true;
		SearchEditStockController.searchEditStockController.categoryAutoFill.newStockAdded = true;
		SearchEditStockController.searchEditStockController.dealerNameAutoFill.newStockAdded = true;
		SearchEditStockController.searchEditStockController.companyNameAutoFill.newStockAdded = true;

	}

}
// https://stackoverflow.com/questions/26555828/how-to-populate-tableview-dynamically-with-fxml-and-javafx
