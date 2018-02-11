package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBUtils;
import com.shopplus.bill.PageableText_Main;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

public class PreviewSalesReturn {

	public DBUtils dbUtils = null;
	DBSearchUtils dbSearchUtils = null;
	static Logger logger = Logger.getLogger(PreviewSalesReturn.class);

	public static String instanceId;
	@FXML
	private Button printButton;
	@FXML
	private ResourceBundle resources;

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

		try {
			writeSalesReturnBillToFile(SalesReturnController.parallelSalesReturn.get(instanceId).billNo, billAsMap);
			PageableText_Main ptm = new PageableText_Main();
			ptm.main("./tmp/SalesReturn/" + SalesReturnController.parallelSalesReturn.get(instanceId).billNo + ".txt",
					SalesReturnController.parallelSalesReturn.get(instanceId).billGenerator.billWidth, billHeight);
		} catch (Exception PrintException) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Print Failed");
			alert.setContentText(PrintException.getMessage());
			logger.error(PrintException.getMessage());
			alert.showAndWait();
			return;
		}
		if (dbUtils.t != null)
			dbUtils.endTransaction();
		try {

			MainController.mainController.globalMessage.setText("Printing .... ");
		} catch (Exception e) {

		}

		SalesReturnController.parallelSalesReturn.get(instanceId).billNo = -1;

		ObservableList<BillTable> itemLisstEdit = SalesReturnController.parallelSalesReturn.get(instanceId).billTable
				.getItems();
		itemLisstEdit.clear();
		SalesReturnController.parallelSalesReturn.get(instanceId).billTable.setItems(itemLisstEdit);

		SalesReturnController.parallelSalesReturn.get(instanceId).discount.setText("");
		SalesReturnController.parallelSalesReturn.get(instanceId).customerName.setText("");

		SalesReturnController.parallelSalesReturn.get(instanceId).billDate.setValue(null);
		SalesReturnController.parallelSalesReturn.get(instanceId).grandTotal.setText("");

		try {
			MainController.mainController.globalMessage.setText("Print competed");
		} catch (Exception e) {

		}
		// SalesReturnController.parallelSalesReturn.remove(instanceId);
		close(null);

	}

	private void writeSalesReturnBillToFile(int billNo, Map<Integer, char[][]> billAsMap) {
		List<String> billAsString = new ArrayList<String>();

		String output = "";
		for (Map.Entry<Integer, char[][]> entry : billAsMap.entrySet()) {

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

		try {
			BufferedWriter bw = null;
			FileWriter fw = null;
			File file = new File("./tmp/SalesReturn/" + billNo + ".txt");
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

	@FXML
	void close(MouseEvent event) {
		Stage stage = (Stage) billPreview.getScene().getWindow();
		stage.close();
	}

	@FXML
	void initialize() {
		printButton.requestFocus();
		dbUtils = SalesReturnController.parallelSalesReturn.get(instanceId).dbUtils;
		dbSearchUtils = new DBSearchUtils();

		assert billPreview != null : "fx:id=\"billPreview\" was not injected: check your FXML file 'Untitled'.";
		billAsMap = SalesReturnController.parallelSalesReturn.get(instanceId).billGenerator.generateBill(
				SalesReturnController.parallelSalesReturn.get(instanceId).billNo,
				SalesReturnController.parallelSalesReturn.get(instanceId).dbUtils);

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
