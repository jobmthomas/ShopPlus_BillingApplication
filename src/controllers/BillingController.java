package controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.Utils;
import com.shopplus.bill.BillGenerator;
import com.shopplus.hibernate.BillDetails;
import com.shopplus.hibernate.BillMaster;
import com.shopplus.hibernate.BillNumberSequence;
import com.shopplus.hibernate.StockMaster;

import javafx.application.Platform;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class BillingController implements Initializable {

	public static Map<String, BillingController> parallelBilling = new HashMap<String, BillingController>(1);
	Logger logger = Logger.getLogger(BillingController.class);
	DBUtils dbUtils = null;
	private DBSearchUtils dbSearchUtils = null;
	BillGenerator billGenerator;

	boolean addedItemToCart = false;

	int billNo = -1;
	private String instanceId;
	float grandTotalFloat;

	@FXML
	public TextField customerName;

	@FXML
	public AutoCompleteTextField categoryAutoFill;

	@FXML
	public AutoCompleteTextField companyNameAutoFill;

	@FXML
	public AutoCompleteTextField itemNameAutofill;

	@FXML
	public TextField quantity;

	@FXML
	public Text grandTotal;
	@FXML
	public TextField extraDiscount;

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
	public TableView<Stock> editTable;

	@FXML
	public TableView<BillTable> billTable;

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

	@FXML
	private TableColumn<BillTable, String> cGstBill;

	@FXML
	private TableColumn<BillTable, String> companyNameBill;

	@FXML
	private TableColumn<BillTable, String> discountBill;

	@FXML
	private TableColumn<BillTable, String> exprdBill;

	@FXML
	private TableColumn<BillTable, String> grossAmtBill;
	@FXML
	private TableColumn<BillTable, String> itemNameBill;

	@FXML
	private TableColumn<BillTable, String> batchNoNill;

	@FXML
	private TableColumn<BillTable, String> mfdBill;
	@FXML
	private TableColumn<BillTable, String> netTotalBill;
	@FXML
	private TableColumn<BillTable, String> actionBill;
	@FXML
	private TableColumn<BillTable, String> quantityBill;
	@FXML
	private TableColumn<BillTable, String> sGstBill;

	@FXML
	private TableColumn<BillTable, String> totalBill;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		dbUtils = new DBUtils();
		dbSearchUtils = new DBSearchUtils();
		billGenerator = new BillGenerator();
		Utils.addTextLimiter(customerName, 30);

		Utils.addTextLimiter(quantity, 4);
		Utils.addTextLimiter(extraDiscount, 8);

		Utils.addTextLimiter(itemNameAutofill, 8);
		Utils.addTextLimiter(categoryAutoFill, 8);
		Utils.addTextLimiter(companyNameAutoFill, 8);

		UUID uid = UUID.randomUUID();
		instanceId = uid + "";
		itemNameAutofill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (itemNameAutofill.getText().length() == 0) {
					itemNameAutofill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (itemNameAutofill.newStockAdded == true) {

						Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
						criteria.createAlias("stockDetails", "child");
						Date currentDate = new Date();
						criteria.add(Restrictions.ge("child.expd", currentDate));
						criteria.add(Restrictions.ge("child.quantityAvaialble", 0));
						criteria.setProjection(Projections.distinct(Projections.property("itemName")));

						List results = criteria.list();
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
					if (Main.prop.getProperty("bill_live_stock_searching").equals("true"))
						searchStock(null, null, null, false, true, true);
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

									searchStock(null, null, null, false, true, false);
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

						Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
						criteria.createAlias("stockDetails", "child");
						Date currentDate = new Date();
						criteria.add(Restrictions.ge("child.expd", currentDate));
						criteria.add(Restrictions.ge("child.quantityAvaialble", 0));
						criteria.setProjection(Projections.distinct(Projections.property("category")));

						List results = criteria.list();
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
					if (Main.prop.getProperty("bill_live_stock_searching").equals("true"))
						searchStock(null, null, null, false, true, true);
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
									searchStock(null, null, null, false, true, false);
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

		companyNameAutoFill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (companyNameAutoFill.getText().length() == 0) {
					companyNameAutoFill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (companyNameAutoFill.newStockAdded == true) {

						Criteria criteria = dbUtils.getSession().createCriteria(StockMaster.class);
						criteria.createAlias("stockDetails", "child");
						Date currentDate = new Date();
						criteria.add(Restrictions.ge("child.expd", currentDate));
						criteria.add(Restrictions.ge("child.quantityAvaialble", 0));
						criteria.setProjection(Projections.distinct(Projections.property("companyName")));

						List results = criteria.list();
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
					if (Main.prop.getProperty("bill_live_stock_searching").equals("true"))
						searchStock(null, null, null, false, true, true);
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
									searchStock(null, null, null, false, true, false);
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
					Stock rowData = row.getItem();
					// editTable.getSelectionModel().select(rowData);
				}
			});
			return row;
		});
	}

	@FXML
	void searchItem(MouseEvent event) {
		searchStock(null, null, null, false, true, false);
	}

	@FXML
	void beginAnotherBilling(MouseEvent event) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				Platform.runLater(() -> {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Billing.fxml"));
					Parent root1;
					try {
						root1 = (Parent) fxmlLoader.load();
						Stage stage = new Stage();
						stage.initModality(Modality.WINDOW_MODAL);
						stage.initStyle(StageStyle.DECORATED);
						stage.setResizable(false);
						stage.setTitle("NEW BILLING SESSION");
						Scene scene = new Scene(root1);
						scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));

						FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
						stage.getIcons().add(new Image(f));
						stage.setScene(scene);
						stage.show();
					} catch (IOException e) {
						e.printStackTrace();
					}

				});

			}
		});
		t.start();
	}

	@FXML
	void addToCartEnter(KeyEvent event) {

		if (event.getCode() == KeyCode.ENTER) {
			addToCart(null);
		}

	}

	@FXML
	void addToCart(MouseEvent event) {

		int quantityInt = 0;
		Stock s = editTable.getSelectionModel().getSelectedItem();
		if (s != null) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Wrong entries");
			try {
				quantityInt = Integer.parseInt(quantity.getText() + "");
			} catch (Exception e) {
				alert.setContentText("Quantity is an invalid value");
			}
			int stockAvalableQuantiy = Integer.parseInt(s.quantityProperty().getValue());

			if ((stockAvalableQuantiy - quantityInt) < 0) {
				alert.setContentText("Unable to purchase " + quantityInt + " items . since stock is limitted ");
			}

			if (!alert.getContentText().trim().equals("")) {
				alert.showAndWait();
				return;
			}

			if (!addedItemToCart) {

				dbUtils.beginTransaction();
				Query q1 = dbUtils.getSession().createQuery("from BillNumberSequence");
				List<BillNumberSequence> result1 = (List<BillNumberSequence>) q1.list();
				int BillNumber = result1.get(0).getBillNumber();
				BillNumber = BillNumber + 1;
				BillMaster bm = new BillMaster(BillNumber, new Date(), null, null, false, customerName.getText(), "");
				dbUtils.getSession().save(bm);
				billNo = BillNumber;
				parallelBilling.put(instanceId, this);
				addedItemToCart = true;

			}

			BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);
			Set<BillDetails> billDetailsList = bm.getBillDetails();

			Stock selectedItem = editTable.getSelectionModel().getSelectedItem();
			BillDetails matchedBill = null;
			for (BillDetails billDetails : billDetailsList) {

				if (billDetails.getItemName().equals(selectedItem.itemNameProperty().getValue())
						&& billDetails.getBatchNo().equals(selectedItem.batchIdProperty().getValue())) {
					billDetails.setQuantity(quantityInt);
					matchedBill = billDetails;
				}
			}
			if (matchedBill == null) {

				float grossAmt = Float.parseFloat(selectedItem.grossAmtProperty().getValue());
				float purchaseAmt = Float.parseFloat(selectedItem.purchaseAmtProperty().getValue());
				float discountPercentageFloat = Float.parseFloat(selectedItem.discountProperty().getValue());

				float sGstPercentageFloat = Float.parseFloat(selectedItem.sGstProperty().getValue());
				float cGstPercentageFloat = Float.parseFloat(selectedItem.cGstProperty().getValue());

				UUID uuid = UUID.randomUUID();
				SimpleDateFormat sdfMfd = new SimpleDateFormat("yyyy-MM-dd");
				Date dateMfd = null;
				try {
					dateMfd = sdfMfd.parse(selectedItem.mfdProperty().getValue());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				SimpleDateFormat sdfExpd = new SimpleDateFormat("yyyy-MM-dd");
				Date dateExpd = null;
				try {
					dateExpd = sdfExpd.parse(selectedItem.expdProperty().getValue());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				BillDetails bd = new BillDetails(uuid + "", bm, selectedItem.itemNameProperty().getValue(),
						selectedItem.batchIdProperty().getValue(), selectedItem.rackProperty().getValue(),
						selectedItem.companyNameProperty().getValue(), selectedItem.dealerNameProperty().getValue(),
						dateMfd, dateExpd, purchaseAmt, grossAmt, discountPercentageFloat, sGstPercentageFloat,
						cGstPercentageFloat, quantityInt);

				billDetailsList.add(bd);
				bm.setBillDetails(billDetailsList);
				dbUtils.getSession().merge(bm);

			} else {
				bm.setBillDetails(billDetailsList);
				dbUtils.getSession().merge(bm);
			}
			populateBillTable();
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Wrong selection");
			alert.setContentText("Please select an item from item table");
			if (!alert.getContentText().trim().equals("")) {
				alert.showAndWait();
				return;
			}
		}
	}

	public void populateBillTable() {

		grandTotalFloat = 0;
		BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);
		if (bm != null) {
			Set<BillDetails> billDetailsList = bm.getBillDetails();

			itemNameBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("itemNameBill"));
			itemNameBill.setEditable(false);

			batchNoNill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("batchNoBill"));
			batchNoNill.setEditable(false);

			companyNameBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("companyNameBill"));
			companyNameBill.setEditable(false);

			exprdBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("expdBill"));
			exprdBill.setEditable(false);

			quantityBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("quantityBill"));
			quantityBill.setEditable(false);

			grossAmtBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("grossAmtBill"));
			grossAmtBill.setEditable(false);

			totalBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("totalBill"));
			totalBill.setEditable(false);

			discountBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("discountBill"));
			discountBill.setEditable(false);

			sGstBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("sGstBill"));
			sGstBill.setEditable(false);

			cGstBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("cGstBill"));
			cGstBill.setEditable(false);

			netTotalBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("netTotalBill"));
			netTotalBill.setEditable(false);

			// actionBill.setCellValueFactory(new
			// PropertyValueFactory<BillTable,
			// String>("DUMMY"));
			actionBill.setEditable(false);

			Callback<TableColumn<BillTable, String>, TableCell<BillTable, String>> cellFactory = new Callback<TableColumn<BillTable, String>, TableCell<BillTable, String>>() {
				@Override
				public TableCell call(final TableColumn<BillTable, String> param) {
					final TableCell<BillTable, String> cell = new TableCell<BillTable, String>() {

						final Button btn = new Button("REMOVE");

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							if (empty) {
								setGraphic(null);
								setText(null);
							} else {
								btn.setOnAction(event -> {

									BillTable billItem = getTableView().getItems().get(getIndex());
									String itemName = billItem.itemNameBillProperty().getValue();
									String batchNo = billItem.batchNoBillProperty().getValue();
									BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);

									Set<BillDetails> billDeetails = bm.getBillDetails();
									Set<BillDetails> removedBill = new HashSet<BillDetails>();

									for (BillDetails billDetails : billDeetails) {
										if (!(billDetails.getItemName().equals(itemName)
												&& billDetails.getBatchNo().equals(batchNo))) {
											removedBill.add(billDetails);
										}
									}
									dbUtils.cancelTransaction();
									dbUtils.beginTransaction();

									Query q1 = dbSearchUtils.getSession().createQuery("from BillNumberSequence");
									List<BillNumberSequence> result1 = (List<BillNumberSequence>) q1.list();
									BillNumberSequence bns = result1.get(0);
									int billNumberInitial = bns.getBillNumber();
									int billNumberNew = billNumberInitial + 1;

									billNo = billNumberNew;
									bm.setBillNumber(billNumberNew);
									bm.setBillDetails(removedBill);
									dbUtils.getSession().update(bm);
									populateBillTable();

								});
								setGraphic(btn);
								setText(null);
							}
						}
					};
					return cell;
				}
			};

			actionBill.setCellFactory(cellFactory);

			List<BillTable> billItemList = new ArrayList<BillTable>();
			grandTotalFloat = 0;
			DecimalFormat df = new DecimalFormat("#0.00");
			for (BillDetails billDetail : billDetailsList) {

				float gross = billDetail.getGrossAmount() * billDetail.getQuantity();

				float gst = ((billDetail.getGrossAmount() * (billDetail.getsGst() + billDetail.getcGst()))
						/ (100 + (billDetail.getsGst() + billDetail.getcGst()))) * billDetail.getQuantity();

				float sgst = (((gross - gst) / 100) * billDetail.getsGst());

				float cgst = (((gross - gst) / 100) * billDetail.getcGst());

				float discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();
				float netTotal = 0;
				if (discount != 0) {
					float taxableAmount = gross - ((cgst + sgst) + discount);
					cgst = (taxableAmount / 100) * billDetail.getcGst();

					sgst = (taxableAmount / 100) * billDetail.getsGst();
					gst = cgst + sgst;

					netTotal = taxableAmount + gst;
				} else {
					netTotal = gross;
				}
				grandTotalFloat = grandTotalFloat + netTotal;

				BillTable bt = new BillTable(billDetail.getUid(), billDetail.getItemName() + "",
						billDetail.getBatchNo() + "", billDetail.getCompany() + "", billDetail.getMfd() + "",
						billDetail.getExpd() + "", billDetail.getPurchaseRate(), billDetail.getGrossAmount(),
						billDetail.getDiscount(), billDetail.getsGst(), billDetail.getcGst(),
						(billDetail.getQuantity() * billDetail.getGrossAmount()), billDetail.getQuantity(), netTotal,
						billDetail.getRack(), billDetail.getDealer());

				billItemList.add(bt);
			}

			billTable.getItems().setAll(billItemList);
			grandTotal.setText(df.format(grandTotalFloat) + "/-");
		} else {
			ObservableList<BillTable> itemLisst = billTable.getItems();
			itemLisst.clear();
			itemLisst.setAll(itemLisst);
			billTable.setItems(itemLisst);
		}
	}

	void searchStock(String itemName, String companyName, String dealerName, boolean showRescentItem,
			boolean showBasedOnExpdDate, boolean likeSearchInternal) {

		String sqlQueryEqual = "select this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD, child1_.PURCHASE_AMT as PURCHASE_AMT,child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_ on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

		String sqlQueryLike = "select  this_.ITEM_NAME as ITEM_NAME, this_.CATEGORY as CATEGORY, this_.COMPANY_NAME as COMPANY_NAME, this_.BARCODE as BARCODE, this_.CREATED_DATE as CREATED_DATE, this_.UPDATED_DATE as UPDATED_DATE, this_.EXPD_WARNING_THRESHOLD_DAYS as EXPD_WARNING_THRESHOLD_DAYS, child1_.UID as UID, child1_.BATCH_ID as BATCH_ID, child1_.RACK as RACK, child1_.DEALER_NAME as DEALER_NAME, child1_.MFD as MFD, child1_.EXPD as EXPD, child1_.PURCHASE_AMT as PURCHASE_AMT,child1_.GROSS_AMT as GROSS_AMT, child1_.DISCOUNT as DISCOUNT, child1_.S_GST_PERCENTAGE as S_GST_PERCENTAGE, child1_.C_GST_PERCENTAGE as C_GST_PERCENTAGE, child1_.QUANTITY_AVAILABLE as QUANTITY_AVAILABLE from STOCK_MASTER this_ inner join STOCK_DETAILS child1_   on this_.ITEM_NAME=child1_.ITEM_NAME where 1=1 ";

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

		sqlQueryEqual = sqlQueryEqual
				+ " AND expd > sysdate() AND child1_.QUANTITY_AVAILABLE > 0  group by child1_.ITEM_NAME,child1_.batch_id order by child1_.expd  asc";
		sqlQueryLike = sqlQueryLike
				+ " AND expd > sysdate() AND child1_.QUANTITY_AVAILABLE > 0 group by child1_.ITEM_NAME,child1_.batch_id order by child1_.expd  asc";

		if (Main.prop.getProperty("apply_limit_to_data_fetch").equals("true")) {
			sqlQueryLike = sqlQueryLike + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
			sqlQueryEqual = sqlQueryEqual + " LIMIT " + Main.prop.getProperty("bulk_data_search_limit");
		}

		Query query = null;
		if (likeSearch.isSelected()) {
			query = dbSearchUtils.getSession().createSQLQuery(sqlQueryLike)

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
		} else if (likeSearchInternal) {
			query = dbSearchUtils.getSession().createSQLQuery(sqlQueryLike).addScalar("ITEM_NAME", Hibernate.STRING)
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

		} else {
			query = dbSearchUtils.getSession().createSQLQuery(sqlQueryEqual).addScalar("ITEM_NAME", Hibernate.STRING)
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

	@FXML
	void previewEnter(KeyEvent event) {

		if (event.getCode() == KeyCode.ENTER) {
			previewBill(null);
		}

	}

	@FXML
	void previewBill(MouseEvent event) {

		if (billNo != -1) {
			BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);
			if (!extraDiscount.getText().equals("")) {
				try {
					float extraDiscountFloat = Float.parseFloat(extraDiscount.getText());

					bm.setExtraDiscount(extraDiscountFloat);

				} catch (Exception e) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Information Dialog");
					alert.setHeaderText("Wrong entries");

					alert.setContentText(" Extra discount value is invalid");
					alert.showAndWait();
					return;
				}
			} else {
				bm.setExtraDiscount(0f);
			}
			bm.setCustomerName(customerName.getText());
			bm.setDoctorName("");
			dbUtils.getSession().merge(bm);
			PrintPreview.instanceId = instanceId;

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrintPreview.fxml"));
			Parent root1;
			try {
				root1 = (Parent) fxmlLoader.load();
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				// stage.initStyle(StageStyle.UNDECORATED);
				stage.setResizable(false);
				stage.setTitle("Print Preview");
				Scene scene = new Scene(root1);
				scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));
				stage.setScene(scene);

				FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
				stage.getIcons().add(new Image(f));
				stage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
