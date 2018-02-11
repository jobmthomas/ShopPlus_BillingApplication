package controllers;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Utils;
import com.shopplus.hibernate.PurchaseInvoiceMaster;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class PurchaseInvoiceController {
	Logger logger = Logger.getLogger(PurchaseInvoiceController.class);
	DBUtils dbUtils;
	DBSearchUtils dbSearchUtils = null;

	@FXML
	private TableView<PurchaseInvoice> purchaseInvoiceTable;
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableColumn<PurchaseInvoice, String> actionColumn;

	@FXML
	private DatePicker invoiceDate;

	@FXML
	private TableColumn<PurchaseInvoice, String> invoiceDateColumn;

	@FXML
	private DatePicker invoiceDateSearch;
	@FXML
	private TextField invoiceNumberSearch;

	@FXML
	private TableColumn<PurchaseInvoice, String> invoiceNoColumn;

	@FXML
	private TextField cGst;

	@FXML
	private TableColumn<PurchaseInvoice, String> cGstColumn;

	@FXML
	private TextField companyName;

	@FXML
	private TextField invoiceNumber;

	@FXML
	private TableColumn<PurchaseInvoice, String> companyNameColumn;

	@FXML
	private TextField hsnSac;

	@FXML
	private TableColumn<PurchaseInvoice, String> hsnSacColumn;

	@FXML
	private TextField payed;

	@FXML
	private TableColumn<PurchaseInvoice, String> payedColumn;

	@FXML
	private TextField sGst;

	@FXML
	private TableColumn<PurchaseInvoice, String> sGstColumn;

	@FXML
	private TextField totalAmount;

	@FXML
	private TableColumn<PurchaseInvoice, String> totalAmountColumn;

	@FXML
	void clear(MouseEvent event) {

		invoiceDate.setValue(LocalDate.now());
		companyName.setText("");
		hsnSac.setText("");
		totalAmount.setText("");
		cGst.setText("");
		sGst.setText("");
		payed.setText("");
	}

	@FXML
	void notFullyPayed(MouseEvent event) {

		Session session = dbSearchUtils.getSession();
		Query query = session
				.createQuery("from PurchaseInvoiceMaster where payed != grandTotal order by created_date desc");
		List<PurchaseInvoiceMaster> results;
		results = (List<PurchaseInvoiceMaster>) query.list();
		populateTable(results);
	}

	@FXML
	void recentlyAdded(MouseEvent event) {

		Session session = dbSearchUtils.getSession();
		Query query = session.createQuery("from PurchaseInvoiceMaster order by created_date desc");
		List<PurchaseInvoiceMaster> results;
		results = (List<PurchaseInvoiceMaster>) query.list();
		populateTable(results);
	}

	@FXML
	void Search(MouseEvent event) {

		Session session = dbSearchUtils.getSession();
		Criteria criteria = session.createCriteria(PurchaseInvoiceMaster.class);
		if (!invoiceNumberSearch.getText().equals("")) {

			criteria.add(Restrictions.eq("invoiceNumber", invoiceNumberSearch.getText()));
		}
		if (invoiceDateSearch.getValue() != null) {

			LocalDate localDate = invoiceDateSearch.getValue();
			Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			criteria.add(Restrictions.eq("createdDate", date));
		} else {
			criteria.addOrder(Order.desc("createdDate"));
		}
		criteria.list();
		List<PurchaseInvoiceMaster> results;
		results = (List<PurchaseInvoiceMaster>) criteria.list();

		populateTable(results);
	}

	private void populateTable(List<PurchaseInvoiceMaster> results) {
		if (!results.isEmpty()) {

			purchaseInvoiceTable.setEditable(true);
			Callback<TableColumn<PurchaseInvoice, String>, TableCell<PurchaseInvoice, String>> cellFactory = new Callback<TableColumn<PurchaseInvoice, String>, TableCell<PurchaseInvoice, String>>() {
				public TableCell call(TableColumn p) {
					return new EditingCell();
				}
			};

			invoiceNoColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("invoiceNo"));
			invoiceNoColumn.setEditable(false);

			invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("invoiceDate"));
			invoiceDateColumn.setCellFactory(cellFactory);
			invoiceDateColumn.setEditable(true);
			invoiceDateColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.invoiceDateProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});

			companyNameColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("companyName"));
			companyNameColumn.setCellFactory(cellFactory);
			companyNameColumn.setEditable(true);
			companyNameColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.companyNameProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});

			hsnSacColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("hsnSac"));
			hsnSacColumn.setCellFactory(cellFactory);
			hsnSacColumn.setEditable(true);
			hsnSacColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.hsnSacProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});
			totalAmountColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("totalAmount"));
			totalAmountColumn.setCellFactory(cellFactory);
			totalAmountColumn.setEditable(true);
			totalAmountColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.totalAmountProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});

			cGstColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("cGst"));
			cGstColumn.setEditable(true);
			cGstColumn.setCellFactory(cellFactory);
			cGstColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.cGstProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});

			sGstColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("sGst"));
			sGstColumn.setEditable(true);
			sGstColumn.setCellFactory(cellFactory);
			sGstColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.sGstProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});
			payedColumn.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("payed"));
			payedColumn.setEditable(true);
			payedColumn.setCellFactory(cellFactory);
			payedColumn.setOnEditCommit(event -> {
				final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
				((PurchaseInvoice) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.payedProperty().setValue(value);
				purchaseInvoiceTable.refresh();
			});
			actionColumn.setEditable(true);

			Callback<TableColumn<PurchaseInvoice, String>, TableCell<PurchaseInvoice, String>> cellFactoryAction = new Callback<TableColumn<PurchaseInvoice, String>, TableCell<PurchaseInvoice, String>>() {
				@Override
				public TableCell call(final TableColumn<PurchaseInvoice, String> param) {
					final TableCell<PurchaseInvoice, String> cell = new TableCell<PurchaseInvoice, String>() {

						final Button btnSave = new Button("SAVE");
						final Button btnDelete = new Button("DELETE");

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							if (empty) {
								setGraphic(null);
								setText(null);
							} else {
								btnSave.setOnAction(event -> {

									PurchaseInvoice invoiceItem = getTableView().getItems().get(getIndex());
									PurchaseInvoiceMaster pbm = new PurchaseInvoiceMaster();
									pbm.setInvoiceNumber(invoiceItem.invoiceNoProperty().getValue());
									populateValueFromTable(pbm, invoiceItem);
									dbUtils.getSession().update(pbm);
									dbUtils.endTransaction();

								});

								btnDelete.setOnAction(event -> {

									PurchaseInvoice invoiceItem = getTableView().getItems().get(getIndex());
									PurchaseInvoiceMaster pbm = new PurchaseInvoiceMaster();
									pbm.setInvoiceNumber(invoiceItem.invoiceNoProperty().getValue());

									if (dbUtils == null) {
										dbUtils.beginTransaction();
									} else {
										dbUtils.cancelTransaction();
										dbUtils.beginTransaction();
									}
									dbUtils.getSession().delete(pbm);
									dbUtils.endTransaction();

								});

								HBox panel = new HBox(btnSave, btnDelete);
								setGraphic(panel);
								setText(null);
							}
						}

						private void populateValueFromTable(PurchaseInvoiceMaster pbm, PurchaseInvoice invoiceItem) {

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Information Dialog");
							alert.setHeaderText("Wrong entries");
							if (invoiceItem.totalAmountProperty().getValue().equals("")) {
								alert.setContentText("Please enter value for Total Amount");
								alert.showAndWait();
								return;

							} else if (invoiceItem.cGstProperty().getValue().equals("")) {
								alert.setContentText("Please enter value for CGST");
								alert.showAndWait();
								return;
							} else if (invoiceItem.sGstProperty().getValue().equals("")) {
								alert.setContentText("Please enter value for SGST");
								alert.showAndWait();
								return;
							} else if (invoiceItem.payedProperty().getValue().equals("")) {
								alert.setContentText("Please enter value for Payed");
								alert.showAndWait();
								return;
							} else if (invoiceItem.companyNameProperty().getValue().equals("")) {
								alert.setContentText("Please enter value for Company Name");
								alert.showAndWait();
								return;
							} else if (invoiceItem.companyNameProperty().getValue() == null) {
								alert.setContentText("Please enter value for Company Name");
								alert.showAndWait();
								return;
							}
							float totalAmountFloat;
							float cGstFloat;
							float sGstFloat;
							float payedFloat;
							try {

								totalAmountFloat = Float.parseFloat(invoiceItem.totalAmountProperty().getValue());

							} catch (Exception e) {
								alert.setContentText("Please enter proper value for Total amount field");
								alert.showAndWait();
								return;

							}
							try {

								payedFloat = Float.parseFloat(invoiceItem.payedProperty().getValue());

							} catch (Exception e) {
								alert.setContentText("Please enter proper value for Payed field");
								alert.showAndWait();
								return;

							}
							try {

								cGstFloat = Float.parseFloat(invoiceItem.cGstProperty().getValue() + "");

							} catch (Exception e) {
								alert.setContentText("Please enter proper value for Cgst field");
								alert.showAndWait();
								return;

							}
							try {

								sGstFloat = Float.parseFloat(invoiceItem.sGstProperty().getValue() + "");

							} catch (Exception e) {
								alert.setContentText("Please enter proper value for Sgst field");
								alert.showAndWait();
								return;

							}

							try {
								if (dbUtils.t == null) {
									dbUtils.beginTransaction();
								} else {
									dbUtils.cancelTransaction();
									dbUtils.beginTransaction();
								}

								System.out.println(invoiceItem.invoiceDateProperty().getValue());
								DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								Date date = null;
								try {
									date = formatter.parse(invoiceItem.invoiceDateProperty().getValue());
								} catch (ParseException e) {
									e.printStackTrace();
								}

								pbm.setCreatedDate(date);
								pbm.setCompanyName(invoiceItem.companyNameProperty().getValue());
								pbm.setHsnSac(invoiceItem.hsnSacProperty().getValue());
								pbm.setGrandTotal(totalAmountFloat);
								pbm.setCgst(cGstFloat);
								pbm.setSgst(sGstFloat);
								pbm.setPayed(payedFloat);

								Alert alertSuccess = new Alert(AlertType.INFORMATION);
								alertSuccess.setTitle("Information Dialog");
								alertSuccess.setHeaderText("Purchase invoice updated");
								alertSuccess.setContentText("Purchase invoice is updated");
								alertSuccess.showAndWait();
							} catch (NumberFormatException e) {
								dbUtils.cancelTransaction();

								alert.setContentText("Error in saving changes , please check the values and try again");
								alert.showAndWait();
								return;
							}

						}
					};
					return cell;
				}
			};

			actionColumn.setCellFactory(cellFactoryAction);

			List<PurchaseInvoice> purchaseInvoiceLIst = new ArrayList<>();

			for (Object obj : results) {
				PurchaseInvoiceMaster pbm = (PurchaseInvoiceMaster) obj;

				PurchaseInvoice s = new PurchaseInvoice(pbm.getInvoiceNumber() + "", pbm.getCreatedDate() + "",
						pbm.getCompanyName(), pbm.getHsnSac(), pbm.getGrandTotal() + "", pbm.getCgst() + "",
						pbm.getSgst() + "", pbm.getPayed() + "");
				purchaseInvoiceLIst.add(s);
			}
			purchaseInvoiceTable.getItems().setAll(purchaseInvoiceLIst);

		} else {
			List<PurchaseInvoice> purchaseInvoiceLIst = new ArrayList<>(0);
			purchaseInvoiceTable.getItems().setAll(purchaseInvoiceLIst);
		}
	}

	@FXML
	void save(MouseEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");
		if (totalAmount.getText().equals("")) {
			alert.setContentText("Please enter value for Total Amount");
			totalAmount.requestFocus();
			alert.showAndWait();
			return;

		} else if (cGst.getText().equals("")) {
			alert.setContentText("Please enter value for CGST");
			cGst.requestFocus();
			alert.showAndWait();
			return;
		} else if (sGst.getText().equals("")) {
			alert.setContentText("Please enter value for SGST");
			sGst.requestFocus();
			alert.showAndWait();
			return;
		} else if (payed.getText().equals("")) {
			alert.setContentText("Please enter value for Payed");
			payed.requestFocus();
			alert.showAndWait();
			return;
		} else if (companyName.getText().equals("")) {
			alert.setContentText("Please enter value for Company Name");
			companyName.requestFocus();
			alert.showAndWait();
			return;
		} else if (invoiceNumber.getText().equals("")) {
			alert.setContentText("Please enter value for Invoice Number");
			invoiceNumber.requestFocus();
			alert.showAndWait();
			return;
		} else if (invoiceDate.getValue() == null) {
			alert.setContentText("Please enter value for Company Name");
			companyName.requestFocus();
			alert.showAndWait();
			return;
		}
		try {
			totalAmount.requestFocus();
			float totalAmountFloat = Float.parseFloat(totalAmount.getText());
			cGst.requestFocus();
			float cGstFloat = Float.parseFloat(cGst.getText() + "");
			sGst.requestFocus();
			float sGstFloat = Float.parseFloat(sGst.getText() + "");
			payed.requestFocus();
			float payedFloat = Float.parseFloat(payed.getText());

			try {
				if (dbUtils.t == null) {
					dbUtils.beginTransaction();
				} else {
					dbUtils.cancelTransaction();
					dbUtils.beginTransaction();
				}

				PurchaseInvoiceMaster pbm = new PurchaseInvoiceMaster();
				pbm.setInvoiceNumber(invoiceNumber.getText());

				LocalDate localDateInvoice = invoiceDate.getValue();
				Instant instantInvoice = Instant.from(localDateInvoice.atStartOfDay(ZoneId.systemDefault()));
				java.util.Date invoiceDateDate = Date.from(instantInvoice);
				pbm.setCreatedDate(invoiceDateDate);
				pbm.setCompanyName(companyName.getText());
				pbm.setHsnSac(hsnSac.getText());
				pbm.setGrandTotal(totalAmountFloat);
				pbm.setCgst(cGstFloat);
				pbm.setSgst(sGstFloat);
				pbm.setPayed(payedFloat);
				dbUtils.getSession().save(pbm);

				dbUtils.endTransaction();

				Alert alertSuccess = new Alert(AlertType.INFORMATION);
				alertSuccess.setTitle("Information Dialog");
				alertSuccess.setHeaderText("Purchase invoice saved");
				alertSuccess.setContentText(
						"The new purchase invoice is saved using invoice number " + invoiceNumber.getText());
				alertSuccess.showAndWait();
			} catch (NumberFormatException e) {
				alert.setContentText("Please enter proper value");
			}
		} catch (Exception e) {
			dbUtils.cancelTransaction();
		}

	}

	@FXML
	void initialize() {
		dbUtils = new DBUtils();
		dbSearchUtils = new DBSearchUtils();
		invoiceDate.setValue(LocalDate.now());
		companyName.setText("");
		hsnSac.setText("");
		totalAmount.setText("");
		cGst.setText("");
		sGst.setText("");
		payed.setText("0.0");

		purchaseInvoiceTable.setEditable(true);
		Utils.addTextLimiter(invoiceNumber, 30);
		Utils.addTextLimiter(companyName, 30);
		Utils.addTextLimiter(hsnSac, 30);
		Utils.addTextLimiter(totalAmount, 30);
		Utils.addTextLimiter(cGst, 30);
		Utils.addTextLimiter(sGst, 30);
		Utils.addTextLimiter(payed, 30);

	}

}

class EditingCell extends TableCell<PurchaseInvoice, String> {

	private TextField textField;

	public EditingCell() {
	}

	@Override
	public void startEdit() {
		super.startEdit();

		if (textField == null) {
			createTextField();
		}

		setGraphic(textField);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		textField.selectAll();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(String.valueOf(getItem()));
		setContentDisplay(ContentDisplay.TEXT_ONLY);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setGraphic(textField);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			} else {
				setText(getString());
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
		}
	}

	private void createTextField() {
		textField = new TextField(getString());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}
