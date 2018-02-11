package controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.Utils;
import com.shopplus.bill.BillGenerator;
import com.shopplus.hibernate.BillDetails;
import com.shopplus.hibernate.BillMaster;
import com.shopplus.hibernate.BillNumberSequence;
import com.shopplus.hibernate.StockDetails;
import com.shopplus.hibernate.StockMaster;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SimpleBillingController {

	public static Map<String, SimpleBillingController> parallelBilling = new HashMap<String, SimpleBillingController>(
			1);
	Logger logger = Logger.getLogger(BillingController.class);
	DBUtils dbUtils;
	DBSearchUtils dbSearchUtils = null;
	private String instanceId;
	BillGenerator billGenerator;
	boolean addedItemToCart = false;
	int billNo = -1;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField totalSales;

	@FXML
	private TextField totalBills;

	@FXML
	private DatePicker billDate;

	@FXML
	TableView<SimpleBillTable> billTable;

	@FXML
	private TableColumn<SimpleBillTable, String> actionColumn;

	@FXML
	private TableColumn<SimpleBillTable, String> cGstBill;

	@FXML
	private TableColumn<SimpleBillTable, String> discountBill;

	@FXML
	private TableColumn<SimpleBillTable, String> itemNameBill;

	@FXML
	private ImageView plus;

	@FXML
	private TableColumn<SimpleBillTable, String> quantityBill;

	@FXML
	private TableColumn<SimpleBillTable, String> grossAmtBill;

	@FXML
	private TableColumn<SimpleBillTable, String> sGstBill;

	@FXML
	private TableColumn<SimpleBillTable, String> netTotalBill;
	@FXML
	private TextField custName;
	@FXML
	private TextField cgst;

	@FXML
	private TextField discount;

	@FXML
	AutoCompleteTextField itemNameAutofill;

	@FXML
	private TextField quantity;

	@FXML
	private TextField rate;

	@FXML
	private TextField sgst;

	@FXML
	private Text grandTotal;
	float grandTotalFloat = 0;

	@FXML
	void plusActionEnter(KeyEvent event) {

		if (event.getCode() == KeyCode.ENTER) {
			plusAction(null);
		}

	}

	@FXML
	void plusAction(MouseEvent event) {

		if (quantity.getText().equals("")) {
			quantity.requestFocus();
			return;
		}
		int quantityInteger;
		try {
			quantityInteger = Integer.parseInt(quantity.getText());
		} catch (Exception e) {
			quantity.requestFocus();
			return;
		}
		if (itemNameAutofill.getText().equals("")) {
			itemNameAutofill.requestFocus();
			return;
		}
		if (rate.getText().equals("")) {
			rate.requestFocus();
			return;
		}
		Float grossAmtFloat;
		try {
			grossAmtFloat = Float.parseFloat(rate.getText());
		} catch (Exception e) {
			rate.requestFocus();
			return;
		}
		if (discount.getText().equals("")) {
			discount.requestFocus();
			return;
		}
		Float discountFloat;
		try {
			discountFloat = Float.parseFloat(discount.getText());
		} catch (Exception e) {
			rate.requestFocus();
			return;
		}

		if (sgst.getText().equals("")) {
			sgst.requestFocus();
			return;
		}
		Float sGstFloat;
		try {
			sGstFloat = Float.parseFloat(sgst.getText());
		} catch (Exception e) {
			sgst.requestFocus();
			return;
		}
		if (cgst.getText().equals("")) {
			cgst.requestFocus();
			return;
		}
		Float cGstFloat;
		try {
			cGstFloat = Float.parseFloat(cgst.getText());
		} catch (Exception e) {
			cgst.requestFocus();
			return;
		}
		if (discountFloat > grossAmtFloat) {
			discount.requestFocus();
			return;
		}
		if (sGstFloat + cGstFloat > 100) {
			sgst.requestFocus();
			return;
		}

		ObservableList<SimpleBillTable> itemList = billTable.getItems();

		for (SimpleBillTable simpleBillTable : itemList) {
			if (simpleBillTable.itemNameBillProperty().getValue().equals(itemNameAutofill.getText())) {
				DecimalFormat df = new DecimalFormat("#0.00");
				float netTotal = quantityInteger * (grossAmtFloat - discountFloat);
				simpleBillTable.setQuantityBill(quantity.getText());
				simpleBillTable.setDiscountBill(discountFloat + "");
				simpleBillTable.setCGstBill(cGstFloat + "");
				simpleBillTable.setSGstBill(sGstFloat + "");
				simpleBillTable.setGrossAmtBill(grossAmtFloat + "");
				simpleBillTable.setNetTotalBill(df.format(netTotal));
				refreshGrandTotal();
				return;
			}
		}

		SimpleBillTable simpleBillTable = new SimpleBillTable(itemNameAutofill.getText(), grossAmtFloat, discountFloat,
				sGstFloat, cGstFloat, (grossAmtFloat - discountFloat) * quantityInteger, quantityInteger);
		itemList.add(simpleBillTable);
		refreshGrandTotal();

	}

	private void refreshGrandTotal() {
		grandTotalFloat = 0;
		ObservableList<SimpleBillTable> itemList = billTable.getItems();

		DecimalFormat df = new DecimalFormat("#0.00");
		for (SimpleBillTable simpleBillTable : itemList) {
			Float itemNetTotal = Float.parseFloat(simpleBillTable.getNetTotalBill());
			grandTotalFloat = grandTotalFloat + itemNetTotal;
			logger.info("INSIDE BILL TABLE ITEMS " + simpleBillTable.itemNameBillProperty().getValue() + "  :  "
					+ itemNetTotal);
		}
		grandTotal.setText(df.format(grandTotalFloat));

	}

	@FXML
	void prinitPreviewEnter(KeyEvent event) {

		if (event.getCode() == KeyCode.ENTER) {
			prinitPreview(null);
		}

	}

	@FXML
	void prinitPreview(MouseEvent event) {
		ObservableList<SimpleBillTable> billTableiTems = billTable.getItems();
		if (billTableiTems.size() > 0) {
			dbUtils.cancelTransaction();
			dbUtils.beginTransaction();
			refreshGrandTotal();

			LocalDate localDateBill = billDate.getValue();
			Instant instantBill = Instant.from(localDateBill.atStartOfDay(ZoneId.systemDefault()));
			java.util.Date billDate = Date.from(instantBill);

			Query q1 = dbUtils.getSession().createQuery("from BillNumberSequence");
			List<BillNumberSequence> result1 = (List<BillNumberSequence>) q1.list();
			int BillNumber = result1.get(0).getBillNumber();
			BillNumber = BillNumber + 1;
			billNo = BillNumber;
			logger.info("############################################" + grandTotalFloat);
			BillMaster bm = new BillMaster(BillNumber, billDate, grandTotalFloat, 0.0f, false, custName.getText(), "");
			billTableiTems = billTable.getItems();
			for (SimpleBillTable billDetails : billTableiTems) {

				float grossAmt = Float.parseFloat(billDetails.grossAmtBillProperty().getValue());
				float purchaseAmt = Float.parseFloat("0");

				float sGstPercentageFloat = Float.parseFloat(billDetails.sGstBillProperty().getValue());
				float cGstPercentageFloat = Float.parseFloat(billDetails.cGstBillProperty().getValue());

				float discountAMount = Float.parseFloat(billDetails.discountBillProperty().getValue());
				float a = grossAmt - discountAMount;
				float b = ((a * (sGstPercentageFloat + cGstPercentageFloat))
						/ (100 + (sGstPercentageFloat + cGstPercentageFloat)));
				float c = ((discountAMount * (sGstPercentageFloat + cGstPercentageFloat))
						/ (100 + (sGstPercentageFloat + cGstPercentageFloat)));
				float d = a - b;
				float e = discountAMount - c;

				float f = d + e;
				float g = (e / f) * 100;

				float discountPercentageFloat = g;

				int quantityInt = Integer.parseInt(billDetails.quantityBillProperty().getValue());

				UUID uuid = UUID.randomUUID();
				SimpleDateFormat sdfMfd = new SimpleDateFormat("yyyy-MM-dd");
				Date dateMfd = new Date();
				SimpleDateFormat sdfExpd = new SimpleDateFormat("yyyy-MM-dd");
				Date dateExpd = new Date();

				BillDetails sbd = new BillDetails(uuid + "", bm, billDetails.itemNameBillProperty().getValue().trim(),
						"DEFAULT", "DEFAULT", "DEFAULT", "DEFAULT", dateMfd, dateExpd, purchaseAmt, grossAmt,
						discountPercentageFloat, sGstPercentageFloat, cGstPercentageFloat, quantityInt);
				bm.getBillDetails().add(sbd);
			}

			dbUtils.getSession().save(bm);
			UUID uid = UUID.randomUUID();
			instanceId = uid + "";
			parallelBilling.put(instanceId, this);
			SimpleBillPrintPreview.instanceId = instanceId;

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SimpleBillPrintPreview.fxml"));
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

	public void updateSalesSummary() {

		LocalDate localDateFrom = billDate.getValue();
		Date dateFrom = Date.from(localDateFrom.atStartOfDay(ZoneId.systemDefault()).toInstant());

		LocalDate localDateTo = billDate.getValue();
		Date dateTo = Date.from(localDateTo.atStartOfDay(ZoneId.systemDefault()).toInstant());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		logger.info("Entered Dates are " + sdf.format(dateFrom) + " AND " + sdf.format(dateTo));

		Calendar c = Calendar.getInstance();
		c.setTime(dateTo);
		c.add(Calendar.DATE, 1);
		dateTo = c.getTime();

		String date1String = sdf.format(dateFrom);
		String date2String = sdf.format(dateTo);

		String sqlSalesBill = "SELECT parent.BILL_NUMBER AS BILL_NUMBER, parent.GRAND_TOTAL AS GRAND_TOTAL, parent.CREATED_DATE AS CREATED_DATE,parent.EXTRA_DISCOUNT AS EXTRA_DISCOUNT,parent.SUCCESS AS SUCCESS "
				+ " FROM BILL_MASTER parent WHERE DATE (parent.CREATED_DATE) >= DATE '" + date1String + "' "
				+ " AND DATE (parent.CREATED_DATE) < DATE '" + date2String + "' AND parent.SUCCESS=1 ";

		SQLQuery queryBill = dbSearchUtils.getSession().createSQLQuery(sqlSalesBill)
				.addScalar("BILL_NUMBER", Hibernate.INTEGER).addScalar("GRAND_TOTAL", Hibernate.FLOAT)
				.addScalar("CREATED_DATE", Hibernate.STRING).addScalar("EXTRA_DISCOUNT", Hibernate.FLOAT)
				.addScalar("SUCCESS", Hibernate.STRING);
		logger.info("QUERY 3 IS " + sqlSalesBill);
		ScrollableResults scResults = queryBill.scroll(ScrollMode.SCROLL_INSENSITIVE);
		float totalSalesFloat = 0;
		float totalSalesDiscountGivem = 0;
		int rowCount = 0;
		while (scResults.next()) {
			rowCount++;
			totalSalesFloat = totalSalesFloat + (float) scResults.get(1);
		}
		totalSales.setText(totalSalesFloat + "");
		totalBills.setText(rowCount + "");
		logger.info("Total Sales in " + date1String + " is " + totalSalesFloat);
		logger.info("Total bills generated in " + date1String + " is " + rowCount);

	}

	@FXML
	void initialize() {

		billDate.valueProperty().addListener((ov, oldValue, newValue) -> {
			updateSalesSummary();
		});

		dbUtils = new DBUtils();
		dbSearchUtils = new DBSearchUtils();
		billDate.setValue(LocalDate.now());
		billGenerator = new BillGenerator();
		Utils.addTextLimiter(itemNameAutofill, 30);
		Utils.addTextLimiter(rate, 9);
		Utils.addTextLimiter(discount, 9);
		Utils.addTextLimiter(sgst, 6);
		Utils.addTextLimiter(cgst, 6);
		Utils.addTextLimiter(quantity, 5);
		Clear();
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

						if (eachItem.toUpperCase().contains(itemNameAutofill.getText().trim().toUpperCase())) {
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
									DecimalFormat df = new DecimalFormat("#0.00");

									itemNameAutofill.setText(result);
									itemNameAutofill.entriesPopup.hide();

									StockMaster stockMaster = (StockMaster) dbSearchUtils.getSession()
											.get(StockMaster.class, itemNameAutofill.getText().trim());
									Set<StockDetails> stockDetails = stockMaster.getStockDetails();
									StockDetails defaultItem = null;
									for (StockDetails item : stockDetails) {
										defaultItem = item;
										break;
									}
									rate.setText(df.format(defaultItem.getGrossAmt()).toString());
									sgst.setText(defaultItem.getSgstPercentage() + "");
									cgst.setText(defaultItem.getCgstPercentage() + "");

									float gross = defaultItem.getGrossAmt();

									float discount = (gross) / 100 * defaultItem.getDiscount();

									SimpleBillingController.this.discount.setText(df.format(discount) + "");

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

		itemNameBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("itemNameBill"));
		itemNameBill.setEditable(true);

		quantityBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("quantityBill"));
		quantityBill.setEditable(true);

		grossAmtBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("grossAmtBill"));
		grossAmtBill.setEditable(true);

		discountBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("discountBill"));
		discountBill.setEditable(true);

		sGstBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("sGstBill"));
		sGstBill.setEditable(true);

		cGstBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("cGstBill"));
		cGstBill.setEditable(true);

		netTotalBill.setCellValueFactory(new PropertyValueFactory<SimpleBillTable, String>("netTotalBill"));
		netTotalBill.setEditable(true);

		actionColumn.setEditable(true);

		Callback<TableColumn<SimpleBillTable, String>, TableCell<SimpleBillTable, String>> cellFactory = new Callback<TableColumn<SimpleBillTable, String>, TableCell<SimpleBillTable, String>>() {
			@Override
			public TableCell call(final TableColumn<SimpleBillTable, String> param) {
				final TableCell<SimpleBillTable, String> cell = new TableCell<SimpleBillTable, String>() {

					final Button btn = new Button("REMOVE");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {

								billTable.getSelectionModel().select(getIndex());
								billTable.getItems().removeAll(billTable.getSelectionModel().getSelectedItems());
								refreshGrandTotal();
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		actionColumn.setCellFactory(cellFactory);
		billTable.setEditable(true);

		billTable.setRowFactory(tv -> {
			TableRow<SimpleBillTable> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					SimpleBillTable rowData = billTable.getSelectionModel().getSelectedItem();
					itemNameAutofill.setText(rowData.itemNameBillProperty().getValue());
					rate.setText(rowData.grossAmtBillProperty().getValue());
					discount.setText(rowData.discountBillProperty().getValue());
					cgst.setText(rowData.cGstBillProperty().getValue());
					sgst.setText(rowData.sGstBillProperty().getValue());
					quantity.setText(rowData.quantityBillProperty().getValue());
				}
			});
			return row;
		});
	}

	private void Clear() {

		itemNameAutofill.setText("");
		rate.setText("");
		discount.setText("0.0");
		quantity.setText("1");

		if (Main.prop.getProperty("gst_tax_method").equals("COMPOUNDING")) {
			cgst.setText("0");
			sgst.setText("0");
		} else {
			cgst.setText(Main.prop.getProperty("default_cgst_per"));
			sgst.setText(Main.prop.getProperty("default_sgst_per"));
		}
		billTable.getItems().clear();
		grandTotal.setText("0.00");

		updateSalesSummary();
	}

	@FXML
	void clearEnter(KeyEvent event) {

		if (event.getCode() == KeyCode.ENTER) {
			clear(null);
		}

	}

	@FXML
	void clear(MouseEvent event) {
		Clear();
	}

}
