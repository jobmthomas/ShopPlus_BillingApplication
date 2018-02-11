package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.bill.BillGenerator;
import com.shopplus.bill.PageableText_Main;
import com.shopplus.hibernate.BillDetails;
import com.shopplus.hibernate.BillMaster;
import com.shopplus.hibernate.BillNumberSequence;
import com.shopplus.hibernate.StockDetails;
import com.shopplus.hibernate.StockMaster;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

public class PrintPreview {

	DBUtils dbUtils = null;
	DBSearchUtils dbSearchUtils = null;
	static Logger logger = Logger.getLogger(PrintPreview.class);

	public static String instanceId;
	@FXML
	private ResourceBundle resources;
	@FXML
	private Button printButton;
	@FXML
	private URL location;

	@FXML
	private TextArea billPreview;

	private Map<Integer, char[][]> billAsMap;
	private int billHeight = 0;

	@FXML
	void printEnter(KeyEvent event) {

		if (event.getCode() == KeyCode.ENTER) {
			print(null);
		}

	}

	@FXML
	void print(MouseEvent event) {

		BillMaster bm = (BillMaster) dbUtils.getSession().get(BillMaster.class,
				BillingController.parallelBilling.get(instanceId).billNo);

		bm.setSuccess(true);
		DecimalFormat df = new DecimalFormat("#0.00");
		bm.setGrandTotal(
				Float.parseFloat(df.format(BillingController.parallelBilling.get(instanceId).grandTotalFloat)));

		Set<BillDetails> purchasedItems = bm.getBillDetails();
		List<String> stockUpdateQuery = new ArrayList<>();
		StockMaster sm = null;
		for (BillDetails billDetails : purchasedItems) {

			Criteria criteria = dbSearchUtils.getSession().createCriteria(StockMaster.class);
			criteria.createAlias("stockDetails", "child");
			criteria.add(Restrictions.eq("itemName", billDetails.getItemName()).ignoreCase());
			criteria.add(Restrictions.eq("child.batchId", billDetails.getBatchNo()).ignoreCase());
			List<StockMaster> results = (List<StockMaster>) criteria.list();
			if (results.size() == 0) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Infojjrmation Dialog");
				alert.setHeaderText("Limitted Stock");

				if (!Main.currentUserRole.equals("ADMIN")) {
					alert.setHeaderText("Stock of Item : " + billDetails.getItemName() + " with Batch no : "
							+ billDetails.getBatchNo() + " is not exist in the database");
					alert.showAndWait();
					return;
				}
			}
			sm = results.get(0);
			Set<StockDetails> stockDetalils = sm.getStockDetails();

			Set<StockDetails> stockDetailsUpdatedList = new HashSet<>();

			for (StockDetails stock : stockDetalils) {
				if ((stock.getBatchId().equals(billDetails.getBatchNo()))) {
					int quantity = stock.getQuantityAvaialble();
					if (quantity < billDetails.getQuantity()) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Information Dialog");
						alert.setHeaderText("Limitted Stock");
						alert.setHeaderText("Stock of Item : " + billDetails.getItemName() + " is reduces to "
								+ quantity + " during this sales");
						alert.showAndWait();
						return;
					}

					stockUpdateQuery.add("UPDATE STOCK_DETAILS SET QUANTITY_AVAILABLE=QUANTITY_AVAILABLE-"
							+ billDetails.getQuantity() + " WHERE ITEM_NAME=\"" + billDetails.getItemName()
							+ "\" AND BATCH_ID=\"" + billDetails.getBatchNo() + "\"");
					quantity = quantity - billDetails.getQuantity();
					stock.setQuantityAvaialble(quantity);
					stockDetailsUpdatedList.add(stock);
				} else {
					stockDetailsUpdatedList.add(stock);
				}
			}
			sm.setStockDetails(stockDetailsUpdatedList);

		}

		if (dbUtils.t != null) {

			while (true) {
				try {
					Query q1 = dbSearchUtils.getSession().createQuery("from BillNumberSequence");
					List<BillNumberSequence> result1 = (List<BillNumberSequence>) q1.list();
					BillNumberSequence bns = result1.get(0);
					int billNumberInitial = bns.getBillNumber();
					int billNumberNew = billNumberInitial + 1;
					BillingController.parallelBilling.get(instanceId).billNo = billNumberNew;
					Query queryUpdate = dbUtils.getSession()
							.createSQLQuery("UPDATE BILL_NUMBER_SEQUENCE SET BILL_NUMBER_SEQUENCE=" + billNumberNew
									+ " WHERE BILL_NUMBER_SEQUENCE=" + billNumberInitial);
					queryUpdate.executeUpdate();

					if (billNumberNew == bm.getBillNumber()) {
						for (String query : stockUpdateQuery) {

							Query q = dbUtils.getSession().createSQLQuery(query);
							q.executeUpdate();

						}

						dbUtils.getSession().merge(bm);

						dbUtils.endTransaction();
						break;
					} else {
						dbUtils.cancelTransaction();
						dbUtils.beginTransaction();

						queryUpdate = dbUtils.getSession()
								.createSQLQuery("UPDATE BILL_NUMBER_SEQUENCE SET BILL_NUMBER_SEQUENCE=" + billNumberNew
										+ " WHERE BILL_NUMBER_SEQUENCE=" + billNumberInitial);
						queryUpdate.executeUpdate();

						for (String query : stockUpdateQuery) {

							Query q = dbUtils.getSession().createSQLQuery(query);
							q.executeUpdate();
						}
						bm.setBillNumber(billNumberNew);
						dbUtils.getSession().save(bm);
						dbUtils.endTransaction();
						break;
					}
				} catch (ConstraintViolationException e) {
					dbUtils.cancelTransaction();
					dbUtils.beginTransaction();
				}
			}

			BillingController.parallelBilling.get(instanceId).addedItemToCart = false;
			try {
				MainController.mainController.globalMessage.setText("Printing .... ");
			} catch (Exception e) {

			}

			billAsMap = BillingController.parallelBilling.get(instanceId).billGenerator
					.generateBill(BillingController.parallelBilling.get(instanceId).billNo, dbUtils);

			try {

				writeBillToFile(BillGenerator.billNo, billAsMap);
				PageableText_Main ptm = new PageableText_Main();
				while (true) {
					boolean status = ptm.main("./tmp/Sales/" + BillGenerator.billNo + ".txt",
							BillingController.parallelBilling.get(instanceId).billGenerator.billWidth, billHeight);
					if (!status) {

						Alert confirmation = new Alert(AlertType.CONFIRMATION);
						confirmation.setTitle("Print Failed");
						String s = "Print Failed, but you can access the bill from tmp/Sales/" + BillGenerator.billNo
								+ "\nDo you want to try printing again?";
						confirmation.setContentText(s);

						Optional<ButtonType> result = confirmation.showAndWait();
						if (!result.isPresent()) {
							continue;
						}
						if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
							continue;
						} else {
							break;
						}
					} else {
						break;
					}
				}
			} catch (Exception PrintException) {

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Print Failed");
				alert.setContentText(PrintException.getMessage());
				logger.error(PrintException.getMessage());
				alert.showAndWait();
				return;
			}

			ObservableList<Stock> itemLisstEdit = BillingController.parallelBilling.get(instanceId).editTable
					.getItems();
			itemLisstEdit.clear();
			BillingController.parallelBilling.get(instanceId).editTable.setItems(itemLisstEdit);

			ObservableList<BillTable> itemLisstBilling = BillingController.parallelBilling.get(instanceId).billTable
					.getItems();
			itemLisstBilling.clear();
			BillingController.parallelBilling.get(instanceId).billTable.setItems(itemLisstBilling);

			BillingController.parallelBilling.get(instanceId).grandTotal.setText("");
			BillingController.parallelBilling.get(instanceId).extraDiscount.setText("");
			BillingController.parallelBilling.get(instanceId).itemNameAutofill.setText("");
			BillingController.parallelBilling.get(instanceId).companyNameAutoFill.setText("");
			BillingController.parallelBilling.get(instanceId).categoryAutoFill.setText("");
			BillingController.parallelBilling.get(instanceId).customerName.setText("");
			BillingController.parallelBilling.get(instanceId).quantity.setText("");
			try {
				MainController.mainController.globalMessage.setText("Print competed");
			} catch (Exception e) {

			}
			BillingController.parallelBilling.get(instanceId).billNo = -1;
			BillingController.parallelBilling.remove(instanceId);

			close(null);
		}
	}

	private void writeBillToFile(int billNo, Map<Integer, char[][]> billAsMap) {
		List<String> billAsString = new ArrayList<String>();
		for (Map.Entry<Integer, char[][]> entry : billAsMap.entrySet()) {
			String output = "";
			for (int row = 0; row < entry.getValue().length; row++) {
				for (int col = 0; col < entry.getValue()[row].length; col++) {
					output = output + entry.getValue()[row][col] + "";
				}
				output = output + "\n";
				billHeight++;
			}
			billAsString.add(output);
			billHeight++;

			try {

				BufferedWriter bw = null;
				FileWriter fw = null;
				File file = new File("./tmp/Sales/" + billNo + ".txt");
				if (file.exists())
					file.delete();
				file.createNewFile();
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				bw.write(output);
				bw.close();
				fw.close();
			} catch (IOException e) {
				logger.error("Error while creating fill file " + e.getMessage());
			}
		}

	}

	@FXML
	void close(MouseEvent event) {
		Stage stage = (Stage) billPreview.getScene().getWindow();
		stage.close();
	}

	@FXML
	void initialize() {
		printButton.requestFocus();
		dbUtils = BillingController.parallelBilling.get(instanceId).dbUtils;
		dbSearchUtils = new DBSearchUtils();

		assert billPreview != null : "fx:id=\"billPreview\" was not injected: check your FXML file 'Untitled'.";

		billAsMap = BillingController.parallelBilling.get(instanceId).billGenerator
				.generateBill(BillingController.parallelBilling.get(instanceId).billNo, dbUtils);

		List<String> billAsString = printArray(billAsMap);

		String textFieldValue = "";
		int pageNo = 1;
		for (String bill : billAsString) {
			textFieldValue = textFieldValue + "Page " + pageNo + "\n";
			textFieldValue = textFieldValue + bill + "\n\n";
			pageNo++;
		}
		billPreview.setFont(Font.font("Consolas", FontPosture.REGULAR, 10));
		billPreview.setText(textFieldValue);

	}

	private List<String> printArray(Map<Integer, char[][]> bill) {
		billHeight = 0;
		List<String> billAsString = new ArrayList<String>();
		for (Map.Entry<Integer, char[][]> entry : bill.entrySet()) {

			String output = "";
			for (int row = 0; row < entry.getValue().length; row++) {
				for (int col = 0; col < entry.getValue()[row].length; col++) {
					output = output + entry.getValue()[row][col] + "";
				}
				output = output + "\n";
				billHeight++;
			}
			billAsString.add(output);
			billHeight++;
		}
		return billAsString;

	}

}
