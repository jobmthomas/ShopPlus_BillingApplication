package controllers;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import com.shopplus.Main;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class PeriodicReportBillDetailsController {

	public static PeriodicReportBillDetailsController periodicReportBillDetailsController;
	static int salesTableCurrentPage = 1;
	static int salesReturnTableCurrentPage = 0;
	static int salesDetailTableCurrentPage = 0;
	static int salesReturnDetailTableCurrentPage = 0;
	static int purchaseTableCurrentPage = 0;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableView<ObservableList<StringProperty>> salesTable;

	@FXML
	private TableView<ObservableList<StringProperty>> salesReturnTable;

	@FXML
	private TableView<ObservableList<StringProperty>> salesReturnDetailedTable;

	@FXML
	private TableView<ObservableList<StringProperty>> salesDetailedTable;

	@FXML
	private TableView<ObservableList<StringProperty>> PurchaseTable;

	@FXML
	void initialize() {
		periodicReportBillDetailsController = this;
		PurchaseTable.setEditable(true);
	}

	public void initSalesTable() throws Exception {
		salesTableCurrentPage = 1;
		populateTable(salesTable, "file:./reports/Report_Sales.csv", true, "SALES");

	}

	public void initPuchaseTable() throws Exception {
		purchaseTableCurrentPage = 1;
		populateTable(PurchaseTable, "file:./reports/Report_Purchase.csv", true, "PURCHASE");

	}

	@FXML
	public void nextPurchaseTable(MouseEvent event) throws Exception {
		purchaseTableCurrentPage = purchaseTableCurrentPage + 1;
		populateTable(PurchaseTable, "file:./reports/Report_Purchase.csv", false, "PURCHASE");
	}

	@FXML
	public void previousPurchaseTable(MouseEvent event) throws Exception {
		purchaseTableCurrentPage = purchaseTableCurrentPage - 1;
		populateTable(PurchaseTable, "file:./reports/Report_Purchase.csv", false, "PURCHASE");

	}

	@FXML
	public void nextSalesTable(MouseEvent event) throws Exception {
		salesTableCurrentPage = salesTableCurrentPage + 1;
		populateTable(salesTable, "file:./reports/Report_Sales.csv", false, "SALES");
	}

	@FXML
	public void previousSalesTable(MouseEvent event) throws Exception {
		salesTableCurrentPage = salesTableCurrentPage - 1;
		populateTable(salesTable, "file:./reports/Report_Sales.csv", false, "SALES");

	}

	public void initSalesReturnTable() throws Exception {
		salesReturnTableCurrentPage = 1;
		populateTable(salesReturnTable, "file:./reports/Report_Return.csv", true, "RETURN");

	}

	@FXML
	public void nextSalesReturnTable(MouseEvent event) throws Exception {
		salesReturnTableCurrentPage = salesReturnTableCurrentPage + 1;
		populateTable(salesReturnTable, "file:./reports/Report_Return.csv", false, "RETURN");
	}

	@FXML
	public void previousSalesReturnTable(MouseEvent event) throws Exception {
		salesReturnTableCurrentPage = salesReturnTableCurrentPage - 1;
		populateTable(salesReturnTable, "file:./reports/Report_Return.csv", false, "RETURN");

	}

	public void initSalesDetailedable() throws Exception {
		salesDetailTableCurrentPage = 1;
		populateTable(salesDetailedTable, "file:./reports/Report_Sales_Detailed.csv", true, "SALES_DETAILS");

	}

	@FXML
	public void nextSalesDetailedTable(MouseEvent event) throws Exception {
		salesDetailTableCurrentPage = salesDetailTableCurrentPage + 1;
		populateTable(salesDetailedTable, "file:./reports/Report_Sales_Detailed.csv", false, "SALES_DETAILS");
	}

	@FXML
	public void previousSalesDetailedTable(MouseEvent event) throws Exception {
		salesDetailTableCurrentPage = salesDetailTableCurrentPage - 1;
		populateTable(salesDetailedTable, "file:./reports/Report_Sales_Detailed.csv", false, "SALES_DETAILS");

	}

	public void initSalesReturnDetailedable() throws Exception {
		salesReturnDetailTableCurrentPage = 1;
		populateTable(salesReturnDetailedTable, "file:./reports/Report_Return_Detailed.csv", true, "RETURN_DETAILS");

	}

	@FXML
	public void nextSalesReturnDetailedTable(MouseEvent event) throws Exception {
		salesReturnDetailTableCurrentPage = salesReturnDetailTableCurrentPage + 1;
		populateTable(salesReturnDetailedTable, "file:./reports/Report_Return_Detailed.csv", false, "RETURN_DETAILS");
	}

	@FXML
	public void previousSalesReturnDetailedTable(MouseEvent event) throws Exception {
		salesReturnDetailTableCurrentPage = salesReturnDetailTableCurrentPage - 1;
		populateTable(salesReturnDetailedTable, "file:./reports/Report_Return_Detailed.csv", false, "RETURN_DETAILS");

	}

	@FXML
	public void openReportFilePurchase(MouseEvent event) throws Exception {
		Desktop.getDesktop().open(new File("./reports/Report_Purchase.csv"));

	}

	@FXML
	public void openReportFileSales(MouseEvent event) throws Exception {
		Desktop.getDesktop().open(new File("./reports/Report_Sales.csv"));

	}

	@FXML
	public void openReportFileReturn(MouseEvent event) throws Exception {
		Desktop.getDesktop().open(new File("./reports/Report_Return.csv"));

	}

	@FXML
	public void openReportFileSalesDetails(MouseEvent event) throws Exception {
		Desktop.getDesktop().open(new File("./reports/Report_Sales_Detailed.csv"));

	}

	@FXML
	public void openReportFileReturnDetails(MouseEvent event) throws Exception {
		Desktop.getDesktop().open(new File("./reports/Report_Return_Detailed.csv"));

	}

	private static void populateTable(final TableView<ObservableList<StringProperty>> table, final String urlSpec,
			final boolean hasHeader, String tableName) throws Exception {

		table.getItems().clear();
		table.getColumns().clear();
		table.setPlaceholder(new Label("Loading..."));
		int limit = Integer.parseInt(Main.prop.getProperty("page_scroll_batch_size"));
		int currentPage = 0;
		if (tableName.equals("SALES")) {
			currentPage = salesTableCurrentPage;
		} else if (tableName.equals("SALES_DETAILS")) {
			currentPage = salesDetailTableCurrentPage;
		} else if (tableName.equals("RETURN")) {
			currentPage = salesReturnTableCurrentPage;
		} else if (tableName.equals("RETURN_DETAILS")) {
			currentPage = salesReturnDetailTableCurrentPage;
		} else if (tableName.equals("PURCHASE")) {
			currentPage = purchaseTableCurrentPage;
		}
		int fistLine = (currentPage * limit) - limit + 2;

		BufferedReader in = getReaderFromUrl(urlSpec);
		// Header line
		int count = 0;

		final String headerLine = in.readLine();
		final String[] headerValues = headerLine.split(",");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int column = 0; column < headerValues.length; column++) {
					table.getColumns().add(createColumn(column, headerValues[column]));
				}
			}
		});

		count++;
		// Data:

		String dataLine;

		while ((dataLine = in.readLine()) != null) {
			count++;
			if (count == fistLine) {
				count = 0;
				final String[] dataValues = dataLine.split(",");

				ObservableList<StringProperty> data = FXCollections.observableArrayList();
				for (String value : dataValues) {
					data.add(new SimpleStringProperty(value));
				}
				table.getItems().add(data);
				count++;
				break;
			}
		}

		while ((dataLine = in.readLine()) != null) {

			final String[] dataValues = dataLine.split(",");

			ObservableList<StringProperty> data = FXCollections.observableArrayList();
			for (String value : dataValues) {
				data.add(new SimpleStringProperty(value));
			}
			table.getItems().add(data);
			count++;
			if (count == limit) {
				break;
			}

		}

		if (count < limit) {

			table.setPlaceholder(new Label("All data loaded"));
		}

		in.close();

	}

	private static TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex,
			String columnTitle) {
		TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
		String title;
		if (columnTitle == null || columnTitle.trim().length() == 0) {
			title = "Column " + (columnIndex + 1);
		} else {
			title = columnTitle;
		}
		column.setText(title);
		column.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
						ObservableList<StringProperty> values = cellDataFeatures.getValue();
						if (columnIndex >= values.size()) {
							return new SimpleStringProperty("");
						} else {
							return cellDataFeatures.getValue().get(columnIndex);
						}
					}
				});
		return column;
	}

	private static BufferedReader getReaderFromUrl(String urlSpec) throws Exception {
		URL url = new URL(urlSpec);
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		return new BufferedReader(new InputStreamReader(in));
	}
}
