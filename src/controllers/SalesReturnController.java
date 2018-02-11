package controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.Utils;
import com.shopplus.bill.SalesReturnBillGenerator;
import com.shopplus.hibernate.BillDetails;
import com.shopplus.hibernate.BillMaster;
import com.shopplus.hibernate.SalesReturnDetails;
import com.shopplus.hibernate.SalesReturnMaster;
import com.shopplus.hibernate.SalesReturnNumberSequence;
import com.shopplus.hibernate.StockDetails;
import com.shopplus.hibernate.StockMaster;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SalesReturnController {

	public static Map<String, SalesReturnController> parallelSalesReturn = new HashMap<String, SalesReturnController>(
			1);

	private boolean itemReturned;
	SalesReturnBillGenerator billGenerator;
	public int salesReturnNumber;
	public int billNo;
	String billNoInitial;
	SalesReturnController salesReturnController;
	public DBUtils dbUtils = null;
	public DBSearchUtils dbSearchUtils = null;

	List<String> stockUpdateQuery = new ArrayList<>();
	List<StockMaster> newlycreatedStocks = new ArrayList<>();
	SalesReturnMaster srm;

	private String instanceId;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	public DatePicker billDate;

	@FXML
	public TextField customerName;

	@FXML
	public TextField discount;

	@FXML
	public TextField grandTotal;

	@FXML
	private TextField billNoTextField;

	@FXML
	void printPreview(MouseEvent event) {
	}

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
	public TableView<BillTable> billTable;

	@FXML
	void cancel(MouseEvent event) {

		itemReturned = false;
		if (dbUtils.t != null)
			dbUtils.cancelTransaction();

		billNoTextField.setText("");
		customerName.setText("");
		billDate.setValue(null);
		discount.setText("");
		grandTotal.setText("");

		ObservableList<BillTable> itemLisst = billTable.getItems();
		itemLisst.clear();
		itemLisst.setAll(itemLisst);
		billTable.setItems(itemLisst);
	}

	@FXML
	void searchBill(MouseEvent event) {
		if (dbUtils.t != null)
			dbUtils.cancelTransaction();
		dbUtils.beginTransaction();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		if (billNoTextField.getText().equals("")) {
			alert.setContentText("Enter valid Bill Number ");
			alert.showAndWait();
			return;
		}
		try {
			int billNoInt = Integer.parseInt(billNoTextField.getText() + "");
		} catch (Exception e) {
			alert.setContentText("Enter valid Bill Number ");
			alert.showAndWait();
			return;
		}
		populateBillTable();
	}

	public void populateBillTable() {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		billNo = Integer.parseInt(billNoTextField.getText());

		float grandTotalFloat = 0;

		billNoInitial = billNoTextField.getText();

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

		discountBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("discountBill"));
		discountBill.setEditable(false);

		sGstBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("sGstBill"));
		sGstBill.setEditable(false);

		cGstBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("cGstBill"));
		cGstBill.setEditable(false);

		netTotalBill.setCellValueFactory(new PropertyValueFactory<BillTable, String>("netTotalBill"));
		netTotalBill.setEditable(false);

		actionBill.setEditable(false);

		Callback<TableColumn<BillTable, String>, TableCell<BillTable, String>> cellFactory = new Callback<TableColumn<BillTable, String>, TableCell<BillTable, String>>() {
			@Override
			public TableCell call(final TableColumn<BillTable, String> param) {
				final TableCell<BillTable, String> cell = new TableCell<BillTable, String>() {

					final Button btn = new Button("RETURN");

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * javafx.scene.control.Cell#updateItem(java.lang.Object,
					 * boolean)
					 */
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
								int quantityInt = Integer.parseInt(billItem.quantityBillProperty().getValue() + "");
								int quantityEnteredsInt = 0;

								Dialog<String> dialog = new Dialog<>();
								dialog.setTitle("Sales Return");
								dialog.setHeaderText("Enter sales return quantity");

								ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
								dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

								GridPane grid = new GridPane();
								grid.setHgap(10);
								grid.setVgap(10);

								TextField username = new TextField();
								username.setPromptText("Sales return quantity");

								grid.add(new Label("Return quantity:"), 0, 0);
								grid.add(username, 1, 0);

								Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
								loginButton.setDisable(true);

								username.textProperty().addListener((observable, oldValue, newValue) -> {
									loginButton.setDisable(newValue.trim().isEmpty());
								});

								dialog.getDialogPane().setContent(grid);

								Platform.runLater(() -> username.requestFocus());

								dialog.setResultConverter(dialogButton -> {
									if (dialogButton == loginButtonType) {
										return username.getText();
									}
									return null;
								});

								Optional<String> result = dialog.showAndWait();

								if (result.isPresent()) {

									try {
										quantityEnteredsInt = Integer.parseInt(result.get());
									} catch (Exception e) {
										alert.setContentText("Enter valid quantity");
										alert.showAndWait();
										return;
									}
									if (quantityEnteredsInt > quantityInt) {
										alert.setContentText("Enter valid quantity");
										alert.showAndWait();
										return;
									}
									if (quantityEnteredsInt == 0) {
										alert.setContentText("Enter valid quantity");
										alert.showAndWait();
										return;
									}
								} else {
									return;
								}

								if (billNo != -1) {
									int effectiveQuantity = 0;
									if (batchNo.equals("DEFAULT")) {
										effectiveQuantity = 0;
									} else {
										effectiveQuantity = quantityEnteredsInt;
									}

									Criteria criteria = dbSearchUtils.getSession().createCriteria(StockMaster.class);
									criteria.createAlias("stockDetails", "child");

									criteria.add(Restrictions.eq("itemName", itemName).ignoreCase());

									criteria.add(Restrictions.eq("child.batchId", batchNo).ignoreCase());

									List<StockMaster> results = (List<StockMaster>) criteria.list();

									if (results.size() == 0) {

										Alert confirmation = new Alert(AlertType.CONFIRMATION);
										confirmation.setTitle("REMOVE STOCK");
										String s = "Stock of item  " + (itemName + "").toUpperCase() + " with batch no "
												+ (batchNo + " Does not exist \n Do you want to create it now ");
										confirmation.setContentText(s);

										Optional<ButtonType> resultConfirmation = confirmation.showAndWait();
										if (!resultConfirmation.isPresent()) {
											return;
										}

										if ((resultConfirmation.isPresent())
												&& (resultConfirmation.get() == ButtonType.OK)) {

											Date date = new Date(System.currentTimeMillis());
											StockMaster stockMaster = new StockMaster(itemName.trim().toUpperCase(), "",
													billItem.companyNameBillProperty().getValue(), "", date, date, 0,
													null);

											float sgstPercentageFloat = Float
													.parseFloat(billItem.sGstBillProperty().getValue());
											float cgstPercentageFloat = Float
													.parseFloat(billItem.cGstBillProperty().getValue());
											float discountFloat = 0;
											if (!discount.getText().equals(""))
												discountFloat = Float
														.parseFloat(billItem.discountBillProperty().getValue());
											float grossAmtFloat = Float
													.parseFloat(billItem.grossAmtBillProperty().getValue() + "");
											float purchaseAmtFloat = Float
													.parseFloat(billItem.purchaseAmtBillProperty().getValue() + "");

											UUID uid = UUID.randomUUID();
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

											try {
												java.util.Date mfd = sdf.parse(billItem.mfdBillProperty().getValue());
												java.util.Date expd = sdf.parse(billItem.expdBillProperty().getValue());

												StockDetails stockDetails = new StockDetails(uid + "", stockMaster,
														billItem.batchNoBillProperty().getValue(),
														billItem.rackProperty().getValue(),
														billItem.dealerNameProperty().getValue(), mfd, expd,
														purchaseAmtFloat, grossAmtFloat, discountFloat,
														sgstPercentageFloat, cgstPercentageFloat, effectiveQuantity,
														effectiveQuantity);
												Set<StockDetails> stockDetailsList = new HashSet<StockDetails>(0);
												stockDetailsList.add(stockDetails);
												stockMaster.setStockDetails(stockDetailsList);

												criteria = dbSearchUtils.getSession().createCriteria(StockMaster.class);

												criteria.add(Restrictions.eq("itemName", itemName).ignoreCase());

												results = (List<StockMaster>) criteria.list();

												if (results.size() == 0) {
													dbUtils.getSession().save(stockMaster);
												} else {
													dbUtils.getSession().merge(stockMaster);
												}
												newlycreatedStocks.add(stockMaster);
											} catch (Exception e) {

												e.printStackTrace();
											}

										} else {
											return;
										}
									} else {
										stockUpdateQuery
												.add("UPDATE STOCK_DETAILS SET QUANTITY_AVAILABLE=QUANTITY_AVAILABLE+"
														+ effectiveQuantity + " WHERE ITEM_NAME=\"" + itemName
														+ "\" AND BATCH_ID=\"" + batchNo + "\"");
									}

									if (!itemReturned) {

										Query q1 = dbUtils.getSession().createQuery("from SalesReturnNumberSequence");
										List<SalesReturnNumberSequence> result1 = (List<SalesReturnNumberSequence>) q1
												.list();
										int SalesReturnNo = result1.get(0).getSalesReturnNumber();
										SalesReturnNo = SalesReturnNo + 1;
										salesReturnNumber = SalesReturnNo;
										float grossAmtFloat = Float
												.parseFloat(billItem.grossAmtBillProperty().getValue() + "");

										float sGstPer = Float.parseFloat(billItem.sGstBillProperty().getValue());
										float cGstPer = Float.parseFloat(billItem.cGstBillProperty().getValue());
										float disountPer = Float.parseFloat(billItem.discountBillProperty().getValue());
										float gross = grossAmtFloat * quantityEnteredsInt;
										float gst = ((grossAmtFloat * (sGstPer + cGstPer))
												/ (100 + (sGstPer + cGstPer))) * quantityEnteredsInt;

										float sgst = (((gross - gst) / 100) * sGstPer);

										float cgst = (((gross - gst) / 100) * cGstPer);

										float discount = (gross - (sgst + cgst)) / 100 * disountPer;

										float taxableAmount = gross - ((cgst + sgst) + discount);
										cgst = (taxableAmount / 100) * sGstPer;

										sgst = (taxableAmount / 100) * cGstPer;
										gst = cgst + sgst;

										Set<SalesReturnDetails> salesReturnDetails = new HashSet<>();
										srm = new SalesReturnMaster(SalesReturnNo, new Date(), taxableAmount + gst,
												billNo, false, customerName.getText(), "", salesReturnDetails);
										dbUtils.getSession().save(srm);
									} else {

										float grossAmtFloat = Float
												.parseFloat(billItem.grossAmtBillProperty().getValue() + "");
										grossAmtFloat = Float
												.parseFloat(billItem.grossAmtBillProperty().getValue() + "");

										float sGstPer = Float.parseFloat(billItem.sGstBillProperty().getValue());
										float cGstPer = Float.parseFloat(billItem.cGstBillProperty().getValue());
										float disountPer = Float.parseFloat(billItem.discountBillProperty().getValue());
										float gross = grossAmtFloat * quantityEnteredsInt;
										float gst = ((grossAmtFloat * (sGstPer + cGstPer))
												/ (100 + (sGstPer + cGstPer))) * quantityEnteredsInt;

										float sgst = (((gross - gst) / 100) * sGstPer);

										float cgst = (((gross - gst) / 100) * cGstPer);

										float discount = (gross - (sgst + cgst)) / 100 * disountPer;

										float taxableAmount = gross - ((cgst + sgst) + discount);
										cgst = (taxableAmount / 100) * sGstPer;

										sgst = (taxableAmount / 100) * cGstPer;
										gst = cgst + sgst;

										srm.setGrandTotal(srm.getGrandTotal() + (taxableAmount + gst));

									}

									Set<SalesReturnDetails> salesReturnDetails = srm.getSalesReturnDetails();

									boolean foundMatch = false;
									for (SalesReturnDetails salesReturnDetail : salesReturnDetails) {

										if (salesReturnDetail.getItemName().equals(itemName)
												&& salesReturnDetail.getBatchNo().equals(batchNo)) {
											foundMatch = true;
											salesReturnDetail
													.setQuantity(salesReturnDetail.getQuantity() + quantityEnteredsInt);
											srm.setSalesReturnDetails(salesReturnDetails);

										}
									}
									if (!foundMatch) {

										UUID uuid = UUID.randomUUID();
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

										java.util.Date mfd;
										try {
											mfd = sdf.parse(billItem.mfdBillProperty().getValue());

											java.util.Date expd = sdf.parse(billItem.expdBillProperty().getValue());

											float sgstPercentageFloat = Float
													.parseFloat(billItem.sGstBillProperty().getValue());
											float cgstPercentageFloat = Float
													.parseFloat(billItem.cGstBillProperty().getValue());
											float discountFloat = 0;
											if (!discount.getText().equals(""))
												discountFloat = Float
														.parseFloat(billItem.discountBillProperty().getValue());
											float grossAmtFloat = Float
													.parseFloat(billItem.grossAmtBillProperty().getValue() + "");
											float purchaseAmtFloat = Float
													.parseFloat(billItem.purchaseAmtBillProperty().getValue() + "");
											SalesReturnDetails srd = new SalesReturnDetails(uuid + "", srm, itemName,
													batchNo, billItem.rackProperty().getValue(),
													billItem.companyNameBillProperty().getValue(),
													billItem.dealerNameProperty().getValue(), mfd, expd,
													purchaseAmtFloat, grossAmtFloat, discountFloat, sgstPercentageFloat,
													cgstPercentageFloat, quantityEnteredsInt);

											salesReturnDetails.add(srd);
										} catch (Exception e) {

											e.printStackTrace();
										}

										srm.setSalesReturnDetails(salesReturnDetails);

									}
									dbUtils.getSession().merge(srm);
									itemReturned = true;
									populateBillTable();
								}

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

		BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);

		if (bm != null) {

			LocalDate dateCreated = bm.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			billDate.setValue(dateCreated);

			customerName.setText(bm.getCustomerName());

			discount.setText(bm.getExtraDiscount() + "");

			Set<BillDetails> billDetailsList = bm.getBillDetails();

			List<BillTable> billItemList = new ArrayList<BillTable>();

			Query q = dbUtils.getSession()
					.createSQLQuery(
							"select ITEM_NAME,BATCH_NO,QUANTITY FROM (SELECT SRD.ITEM_NAME,SRD.BATCH_NO,SRD.QUANTITY FROM shopplus.SALES_RETURN_MASTER SRM LEFT JOIN shopplus.SALES_RETURN_DETAILS SRD ON (SRM.SALES_RETURN_NUMBER=SRD.SALES_RETURN_NUMBER)"
									+ "WHERE SRM.BILL_NO=" + billNo + " AND SRD.ITEM_NAME IS NOT NULL)TMP")
					.addScalar("ITEM_NAME", Hibernate.STRING).addScalar("BATCH_NO", Hibernate.STRING)
					.addScalar("QUANTITY", Hibernate.INTEGER);

			q.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

			List<Stock> stockList = new ArrayList<Stock>();
			List obj = q.list();
			DecimalFormat df = new DecimalFormat("#0.00");
			for (BillDetails billDetail : billDetailsList) {
				int quantityNew = billDetail.getQuantity();

				for (Object object : obj) {

					Map<Object, Object> srd = (Map<Object, Object>) object;

					if (srd.get("BATCH_NO").equals(billDetail.getBatchNo())
							&& srd.get("ITEM_NAME").equals(billDetail.getItemName())) {
						int quantity = (int) srd.get("QUANTITY");
						quantityNew = quantityNew - quantity;
					}
				}
				if (srm != null) {
					for (SalesReturnDetails srd : srm.getSalesReturnDetails()) {
						if (srd.getBatchNo().equals(billDetail.getBatchNo())
								&& srd.getItemName().equals(billDetail.getItemName())) {
							int quantity = (int) srd.getQuantity();
							quantityNew = quantityNew - quantity;
						}
					}
				}

				float gross = billDetail.getGrossAmount() * quantityNew;

				float gst = ((billDetail.getGrossAmount() * (billDetail.getsGst() + billDetail.getcGst()))
						/ (100 + (billDetail.getsGst() + billDetail.getcGst()))) * quantityNew;

				float sgst = (((gross - gst) / 100) * billDetail.getsGst());

				float cgst = (((gross - gst) / 100) * billDetail.getcGst());

				float discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();

				float taxableAmount = gross - ((cgst + sgst) + discount);
				cgst = (taxableAmount / 100) * billDetail.getcGst();

				sgst = (taxableAmount / 100) * billDetail.getsGst();
				gst = cgst + sgst;

				float netTotal = taxableAmount + gst;
				grandTotalFloat = grandTotalFloat + netTotal;

				BillTable bt = new BillTable(billDetail.getUid(), billDetail.getItemName() + "",
						billDetail.getBatchNo() + "", billDetail.getCompany() + "", billDetail.getMfd() + "",
						billDetail.getExpd() + "", billDetail.getPurchaseRate(), billDetail.getGrossAmount(),
						billDetail.getDiscount(), billDetail.getsGst(), billDetail.getcGst(), 0f, quantityNew, netTotal,
						billDetail.getRack(), billDetail.getDealer());

				billItemList.add(bt);
			}

			billTable.getItems().clear();
			billTable.getItems().setAll(billItemList);
			grandTotal.setText(df.format(grandTotalFloat) + "");

		} else

		{
			ObservableList<BillTable> itemLisst = billTable.getItems();
			itemLisst.clear();
			itemLisst.setAll(itemLisst);
			billTable.setItems(itemLisst);
			alert.setContentText("There is no bill exist with Bill Number " + billNo);
			alert.showAndWait();
			return;
		}
	}

	@FXML
	void initialize() {

		Utils.addTextLimiter(billNoTextField, 8);
		Utils.addTextLimiter(customerName, 30);

		discount.setEditable(false);
		grandTotal.setEditable(false);

		dbUtils = new DBUtils();
		dbSearchUtils = new DBSearchUtils();
		billGenerator = new SalesReturnBillGenerator();
		UUID uid = UUID.randomUUID();
		instanceId = uid + "";
		salesReturnController = this;
		parallelSalesReturn.put(instanceId, salesReturnController);

	}

	@FXML
	void previewBill(MouseEvent event) {

	}

	@FXML
	public void saveChnages(MouseEvent e) {
		if (itemReturned) {
			Alert confirmation = new Alert(AlertType.CONFIRMATION);
			confirmation.setTitle("SALES RETURN");
			String s = "Do you want to save the changes";
			confirmation.setContentText(s);

			Optional<ButtonType> result = confirmation.showAndWait();

			if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
				while (true) {
					try {
						dbUtils.cancelTransaction();
						dbUtils.beginTransaction();
						Query q1 = dbSearchUtils.getSession()
								.createSQLQuery("SELECT SALES_RETURN_NUMBER_SEQUENCE from SALES_RETURN_NUMBER_SEQUENCE")
								.addScalar("SALES_RETURN_NUMBER_SEQUENCE", Hibernate.INTEGER);
						List obj = q1.list();
						int SalesReturnNumberInitial = 0;
						for (Object object : obj) {
							SalesReturnNumberInitial = (Integer) object;
						}

						int SalesReturnNumber = SalesReturnNumberInitial + 1;
						if (srm.getSalesReturnNumber() == SalesReturnNumber) {

							Query queryUpdate = dbUtils.getSession().createSQLQuery(
									"UPDATE SALES_RETURN_NUMBER_SEQUENCE SET SALES_RETURN_NUMBER_SEQUENCE="
											+ salesReturnNumber + " WHERE SALES_RETURN_NUMBER_SEQUENCE="
											+ SalesReturnNumberInitial);
							queryUpdate.executeUpdate();
							dbUtils.getSession().save(srm);
							for (StockMaster stock : newlycreatedStocks) {
								dbUtils.getSession().merge(stock);
							}
							for (String stockUpdate : stockUpdateQuery) {

								Query q = dbUtils.getSession().createSQLQuery(stockUpdate);
								q.executeUpdate();

							}

							dbUtils.endTransaction();
							srm = null;
							itemReturned = false;
							newlycreatedStocks.clear();
							stockUpdateQuery.clear();
							confirmation.setContentText(
									"Sales return successully saved.\nClick Print bill to take the print");
							confirmation.showAndWait();
							break;
						} else {

							dbUtils.cancelTransaction();
							dbUtils.beginTransaction();

							Query queryUpdate = dbUtils.getSession().createSQLQuery(
									"UPDATE SALES_RETURN_NUMBER_SEQUENCE SET SALES_RETURN_NUMBER_SEQUENCE="
											+ SalesReturnNumber + " WHERE SALES_RETURN_NUMBER_SEQUENCE="
											+ SalesReturnNumberInitial);
							queryUpdate.executeUpdate();
							srm.setSalesReturnNumber(SalesReturnNumber);
							dbUtils.getSession().save(srm);
							for (StockMaster stock : newlycreatedStocks) {
								dbUtils.getSession().merge(stock);
							}
							for (String stockUpdate : stockUpdateQuery) {
								Query q = dbUtils.getSession().createSQLQuery(stockUpdate);
								q.executeUpdate();
							}
							dbUtils.endTransaction();
							srm = null;
							itemReturned = false;
							newlycreatedStocks.clear();
							stockUpdateQuery.clear();
							confirmation.setContentText(
									"Sales return successully saved.\nClick Print bill to take the print");
							confirmation.showAndWait();
							break;
						}

					} catch (ConstraintViolationException e1) {
						dbUtils.cancelTransaction();
						dbUtils.beginTransaction();
					}
				}
				populateBillTable();
			}
		}
	}

	@FXML
	public void previewSalesReturn(MouseEvent e) {
		if (dbUtils.t == null) {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PreviewSalesRetun.fxml"));
			Parent root1;

			try {

				PreviewSalesReturn.instanceId = instanceId;
				root1 = (Parent) fxmlLoader.load();
				PreviewSalesReturn.instanceId = instanceId;
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
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Save the changes before print");
			alert.showAndWait();
			return;
		}
	}

}
