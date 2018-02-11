package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
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

public class SimpleBillPrintPreview {

	DBUtils dbUtils = null;
	DBSearchUtils dbSearchUtils = null;
	static Logger logger = Logger.getLogger(SimpleBillPrintPreview.class);

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
				SimpleBillingController.parallelBilling.get(instanceId).billNo);

		bm.setSuccess(true);
		List<StockMaster> listOfStockToUpdate = new ArrayList<>();
		List<StockMaster> listOfStockToAdd = new ArrayList<>();
		for (BillDetails billItems : bm.getBillDetails()) {
			String itemName = billItems.getItemName();
			Criteria criteria = dbSearchUtils.getSession().createCriteria(StockMaster.class);
			criteria.createAlias("stockDetails", "child");
			criteria.add(Restrictions.eq("itemName", itemName.trim()).ignoreCase());
			List<StockMaster> results = (List<StockMaster>) criteria.list();
			if (results.size() == 0) {
				StockMaster sm = new StockMaster(itemName, "DEFAULT", "DEFAULT", "DEFAULT", new Date(), new Date(), 0,
						null);

				UUID uid = UUID.randomUUID();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				StockDetails stockDetails = new StockDetails(uid + "", sm, "DEFAULT", "DEFAULT", "DEFAULT", new Date(),
						new Date(), 0, billItems.getGrossAmount(), billItems.getDiscount(), billItems.getsGst(),
						billItems.getcGst(), 0, 0);

				sm.getStockDetails().add(stockDetails);
				listOfStockToAdd.add(sm);
				AddStockController.addStockController.setNewStockAddedFlag();
			} else {
				StockMaster sm = results.get(0);

				Set<StockDetails> stockDetalils = sm.getStockDetails();
				boolean foundDefaultStock = false;
				for (StockDetails stock : stockDetalils) {
					if (stock.getBatchId().equals("DEFAULT")) {
						foundDefaultStock = true;

						boolean valueChange = false;
						if (billItems.getGrossAmount() != stock.getGrossAmt()) {
							stock.setGrossAmt(billItems.getGrossAmount());
							valueChange = true;
						}
						if (billItems.getsGst() != stock.getSgstPercentage()) {
							stock.setSgstPercentage(billItems.getsGst());
							valueChange = true;
						}
						if (billItems.getcGst() != stock.getCgstPercentage()) {
							stock.setCgstPercentage(billItems.getcGst());
							valueChange = true;
						}
						if (billItems.getDiscount() != stock.getDiscount()) {
							stock.setDiscount(billItems.getDiscount());
							valueChange = true;
						}
						if (valueChange) {
							AddStockController.addStockController.setNewStockAddedFlag();
							listOfStockToUpdate.add(sm);
							break;
						}
					}
				}

				if (!foundDefaultStock) {
					UUID uid = UUID.randomUUID();
					StockDetails stockDetails = new StockDetails(uid + "", sm, "DEFAULT", "DEFAULT", "DEFAULT",
							new Date(), new Date(), 0, billItems.getGrossAmount(), billItems.getDiscount(),
							billItems.getsGst(), billItems.getcGst(), 0, 0);
					sm.getStockDetails().add(stockDetails);
					listOfStockToUpdate.add(sm);
				}
			}
		}
		if (dbUtils.t != null) {

			while (true) {
				try {
					Query q1 = dbSearchUtils.getSession().createQuery("from BillNumberSequence");
					List<BillNumberSequence> result1 = (List<BillNumberSequence>) q1.list();
					BillNumberSequence bns = result1.get(0);
					int billNumberInitial = bns.getBillNumber();
					int billNumberNew = billNumberInitial + 1;
					SimpleBillingController.parallelBilling.get(instanceId).billNo = billNumberNew;
					Query queryUpdate = dbUtils.getSession()
							.createSQLQuery("UPDATE BILL_NUMBER_SEQUENCE SET BILL_NUMBER_SEQUENCE=" + billNumberNew
									+ " WHERE BILL_NUMBER_SEQUENCE=" + billNumberInitial);
					queryUpdate.executeUpdate();

					if (billNumberNew == bm.getBillNumber()) {

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
			try {
				dbUtils.beginTransaction();

				for (StockMaster stockMaster : listOfStockToAdd) {
					dbUtils.getSession().save(stockMaster);
				}
				for (StockMaster stockMaster : listOfStockToUpdate) {
					dbUtils.getSession().merge(stockMaster);
				}
				dbUtils.endTransaction();
			} catch (Exception e) {
				logger.error("Error while creating/updating stock " + e.getMessage());
				dbUtils.cancelTransaction();
			}
			SimpleBillingController.parallelBilling.get(instanceId).addedItemToCart = false;
			try {
				MainController.mainController.globalMessage.setText("Printing .... ");
			} catch (Exception e) {

			}

			billAsMap = SimpleBillingController.parallelBilling.get(instanceId).billGenerator
					.generateBill(SimpleBillingController.parallelBilling.get(instanceId).billNo, dbUtils);

			try {

				writeBillToFile(SimpleBillingController.parallelBilling.get(instanceId).billNo, billAsMap);
				PageableText_Main ptm = new PageableText_Main();
				while (true) {
					boolean status = ptm.main(
							"./tmp/SimpleSales/" + SimpleBillingController.parallelBilling.get(instanceId).billNo
									+ ".txt",
							SimpleBillingController.parallelBilling.get(instanceId).billGenerator.billWidth,
							billHeight);
					if (!status) {

						Alert confirmation = new Alert(AlertType.CONFIRMATION);
						confirmation.setTitle("Print Failed");
						String s = "Print Failed, but you can access the bill from tmp/SimpleSales/"
								+ SimpleBillingController.parallelBilling.get(instanceId).billNo
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

			ObservableList<SimpleBillTable> itemLisstEdit = SimpleBillingController.parallelBilling
					.get(instanceId).billTable.getItems();
			itemLisstEdit.clear();
			SimpleBillingController.parallelBilling.get(instanceId).billTable.setItems(itemLisstEdit);

			ObservableList<SimpleBillTable> itemLisstBilling = SimpleBillingController.parallelBilling
					.get(instanceId).billTable.getItems();
			itemLisstBilling.clear();
			SimpleBillingController.parallelBilling.get(instanceId).billTable.setItems(itemLisstBilling);

			SimpleBillingController.parallelBilling.get(instanceId).clear(null);
			try {
				MainController.mainController.globalMessage.setText("Print competed");
			} catch (Exception e) {
				logger.error("" + e.getMessage());
			}
			SimpleBillingController.parallelBilling.get(instanceId).billNo = -1;
			SimpleBillingController.parallelBilling.remove(instanceId);
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
				File file = new File("./tmp/SimpleSales/" + billNo + ".txt");
				if (file.exists())
					file.delete();
				file.createNewFile();
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				bw.write(output);
				bw.close();
				fw.close();
			} catch (IOException e) {
				logger.error("Error while creating bill file " + e.getMessage());
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
		dbUtils = SimpleBillingController.parallelBilling.get(instanceId).dbUtils;
		dbSearchUtils = new DBSearchUtils();

		assert billPreview != null : "fx:id=\"billPreview\" was not injected: check your FXML file 'Untitled'.";
		billAsMap = SimpleBillingController.parallelBilling.get(instanceId).billGenerator
				.generateBill(SimpleBillingController.parallelBilling.get(instanceId).billNo, dbUtils);

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
