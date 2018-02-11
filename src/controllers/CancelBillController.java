package controllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.Utils;
import com.shopplus.bill.SalesReturnBillGenerator;
import com.shopplus.hibernate.BillDetails;
import com.shopplus.hibernate.BillMaster;
import com.shopplus.hibernate.SalesReturnDetails;
import com.shopplus.hibernate.SalesReturnMaster;
import com.shopplus.hibernate.StockMaster;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class CancelBillController {

	public static Map<String, CancelBillController> parallelSalesReturn = new HashMap<String, CancelBillController>(1);

	private boolean billCancelled;
	SalesReturnBillGenerator billGenerator;
	public int salesReturnNumber;
	public int billNo;
	String billNoInitial;
	CancelBillController salesReturnController;
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

		billCancelled = false;
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
	void cancelBill(MouseEvent event) {

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

		BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);
		bm.setSuccess(false);
		dbUtils.getSession().update(bm);
		billCancelled = true;
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

		BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);

		if (bm != null) {

			if (!bm.isSuccess()) {
				ObservableList<BillTable> itemLisst = billTable.getItems();
				itemLisst.clear();
				itemLisst.setAll(itemLisst);
				billTable.setItems(itemLisst);
				alert.setContentText("The bill " + billNo + " is already cancelled");
				alert.showAndWait();
				return;
			}
		}
		populateBillTable();
		billCancelled = false;
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

		BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class, billNo);

		if (bm != null) {
			if (bm.isSuccess()) {
				LocalDate dateCreated = bm.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				billDate.setValue(dateCreated);

				customerName.setText(bm.getCustomerName());

				discount.setText(bm.getExtraDiscount() + "");

				Set<BillDetails> billDetailsList = bm.getBillDetails();

				List<BillTable> billItemList = new ArrayList<BillTable>();

				Query q = dbUtils.getSession()
						.createSQLQuery(
								"select ITEM_NAME,BATCH_NO,QUANTITY FROM (SELECT SRD.ITEM_NAME,SRD.BATCH_NO,SRD.QUANTITY FROM shopplus.SALES_RETURN_MASTER SRM LEFT "
										+ "JOIN shopplus.SALES_RETURN_DETAILS SRD ON (SRM.SALES_RETURN_NUMBER=SRD.SALES_RETURN_NUMBER)"
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
							billDetail.getDiscount(), billDetail.getsGst(), billDetail.getcGst(), 0f, quantityNew,
							netTotal, billDetail.getRack(), billDetail.getDealer());

					billItemList.add(bt);
				}

				billTable.getItems().clear();
				billTable.getItems().setAll(billItemList);
				grandTotal.setText(df.format(grandTotalFloat) + "");
			}
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
	public void saveChnages(MouseEvent e) {
		if (billCancelled) {
			Alert confirmation = new Alert(AlertType.CONFIRMATION);
			confirmation.setTitle("CANCEL BILL");
			String s = "Do you want to save the changes";
			confirmation.setContentText(s);

			Optional<ButtonType> result = confirmation.showAndWait();

			if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
				dbUtils.endTransaction();
				populateBillTable();
				billCancelled = false;
			}
		} else {
			Alert confirmation = new Alert(AlertType.INFORMATION);
			confirmation.setTitle("CANCEL BILL");
			String s = "Cancel the bill before save";
			confirmation.setContentText(s);
			confirmation.showAndWait();
		}
	}

}
