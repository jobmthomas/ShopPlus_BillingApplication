package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import com.shopplus.DBSearchUtils;
import com.shopplus.Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PeriodicReportController {

	Logger logger = Logger.getLogger(PeriodicReportController.class);
	private DBSearchUtils dbSearchUtils = null;
	@FXML
	private ResourceBundle resources;

	@FXML
	private ComboBox<String> modeOfView;

	@FXML
	private ComboBox<String> dataOnGrapg;

	@FXML
	private URL location;

	@FXML
	private TableColumn<BillTableReport, String> billNoBill;

	@FXML
	private TableColumn<SalesReturnTableReport, String> billNoReturn;

	@FXML
	private TableColumn<BillTableReport, String> dateBill;

	@FXML
	private TableColumn<SalesReturnTableReport, String> dateReturn;

	@FXML
	private DatePicker fromDate;

	@FXML
	private TableColumn<BillTableReport, String> grandTotalBill;

	@FXML
	private TableColumn<BillTableReport, String> sgstBill;
	@FXML
	private TableColumn<BillTableReport, String> cgstBill;
	@FXML
	private TableColumn<BillTableReport, String> cgst;
	@FXML
	private TableColumn<BillTableReport, String> cgstReturn;
	@FXML
	private TableColumn<BillTableReport, String> sgst;
	@FXML
	private TableColumn<BillTableReport, String> sgstReturn;
	@FXML
	private TableColumn<SalesReturnTableReport, String> grandTotalReturn;

	@FXML
	private TextField gstCollected;
	@FXML
	private TextField cgstCollected;
	@FXML
	private TextField sgstCollected;

	@FXML
	private TextField gstCollected_return;
	@FXML
	private TextField cgstCollected_return;
	@FXML
	private TextField sgstCollected_return;

	@FXML
	private TextField gstPayesDuringPurchase;
	@FXML
	private TextField cgstPayesDuringPurchase;
	@FXML
	private TextField sgstPayesDuringPurchase;

	@FXML
	private TextField gstPayesDuringPurchase_return;
	@FXML
	private TextField cgstPayesDuringPurchase_return;
	@FXML
	private TextField sgstPayesDuringPurchase_return;

	@FXML
	private PieChart mostProfitablePie;

	@FXML
	private PieChart mostSoldPie;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private TextField remainingGstPayable;
	@FXML
	private TextField remainingCgstPayable;

	@FXML
	private TextField remainingSgstPayable;

	@FXML
	private TableColumn<SalesReturnTableReport, String> salesReturnBillNoReturn;

	@FXML
	private LineChart<String, Float> lineChart;

	@FXML
	private DatePicker toDate;

	@FXML
	private TextField totalDiscountGiven;
	@FXML
	private TextField totalSales;
	@FXML
	private TextField totalReturn;
	@FXML
	private TextField totalPurchase;
	@FXML
	private TextField totalPurchase_return;

	Query queryBill;
	Query querySalesReturn;

	String date1String;
	String date2String;

	Map<Integer, gst> gstSalesGroupByBillNo = new LinkedHashMap<Integer, gst>();
	Map<Integer, gst> gstReturnGroupByBillNo = new LinkedHashMap<Integer, gst>();

	@FXML
	void generateReports(MouseEvent event) throws Exception {
		try {

			logger.info("**************************** REPORT GENERATION - STARTS HERE *******************************");
			File reportPurchase = new File("./reports/Report_Purchase.csv");
			if (reportPurchase.exists()) {
				reportPurchase.delete();
				reportPurchase.createNewFile();
			} else
				reportPurchase.createNewFile();
			FileOutputStream fosPurchase = new FileOutputStream(reportPurchase);
			fosPurchase.write("BILL_NUMBER,BILL_DATE,COMPANY_NAME,HSN_SAC,TOTAL_AMOUNT,CGST,SGST,PAYED\n".getBytes());

			logger.info("**************************** REPORT GENERATION - STARTS HERE *******************************");
			File reportDetailsSales = new File("./reports/Report_Sales_Detailed.csv");
			if (reportDetailsSales.exists()) {
				reportDetailsSales.delete();
				reportDetailsSales.createNewFile();
			} else
				reportDetailsSales.createNewFile();
			FileOutputStream fosDetailsSales = new FileOutputStream(reportDetailsSales);
			fosDetailsSales
					.write("BILL_NUMBER,BILL_DATE,STATUS,EXTRA_DISCOUNT,BILL_TYPE,ITEM_NAME,BATCH_NUMBER,PURCHASE_AMOUNT,SELLING_PRICE,DISCOUNT_PER,SGST_PER,CGST_PER,QUANTITY,TOTAL_PURCHASE,SGST_PURCHASE,CGST_PURCHASE,TOTAL_SALES,SGST_SALES,CGST_SALES,DISCOUNT_ALLOWED\n"
							.getBytes());
			File reportDetailsReturn = new File("./reports/Report_Return_Detailed.csv");
			if (reportDetailsReturn.exists()) {
				reportDetailsReturn.delete();
				reportDetailsReturn.createNewFile();
			} else
				reportDetailsReturn.createNewFile();
			FileOutputStream fosDetailsReturn = new FileOutputStream(reportDetailsReturn);
			fosDetailsReturn
					.write("BILL_NUMBER,SALES_RETURN_BILL_NUMBER,BILL_DATE,EXTRA_DISCOUNT,BILL_TYPE,ITEM_NAME,BATCH_NUMBER,PURCHASE_AMOUNT,SELLING_PRICE,DISCOUNT_PER,SGST_PER,CGST_PER,QUANTITY,TOTAL_PURCHASE,SGST_PURCHASE,CGST_PURCHASE,TOTAL_SALES,SGST_SALES,CGST_SALES,DISCOUNT_ALLOWED\n"
							.getBytes());
			File reportSales = new File("./reports/Report_Sales.csv");
			if (reportSales.exists()) {
				reportSales.delete();
				reportSales.createNewFile();
			} else
				reportSales.createNewFile();
			FileOutputStream fosSales = new FileOutputStream(reportSales);
			fosSales.write("BILL_NUMBER,BILL_DATE,STATUS,TOTAL,SGST,CGST\n".getBytes());
			File reportReturn = new File("./reports/Report_Return.csv");
			if (reportReturn.exists()) {
				reportReturn.delete();
				reportReturn.createNewFile();
			}
			FileOutputStream fosReturn = new FileOutputStream(reportReturn);
			fosReturn.write("SALES_RETURN_NUMBER,BILL_NUMBER,BILL_DATE,TOTAL,SGST,CGST\n".getBytes());
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Wrong entries");

			if (!Main.currentUserRole.equals("ADMIN")) {
				alert.setHeaderText("Permission Denied");
				alert.showAndWait();
				return;
			}

			if (fromDate.getValue() == null || toDate.getValue() == null) {
				alert.setContentText("Please enter From and To dates");
			}

			LocalDate localDateMfd = fromDate.getValue();
			Instant instantMfd = Instant.from(localDateMfd.atStartOfDay(ZoneId.systemDefault()));
			java.util.Date mdfDate = Date.from(instantMfd);

			LocalDate localDateExpd = toDate.getValue();
			Instant instantExpd = Instant.from(localDateExpd.atStartOfDay(ZoneId.systemDefault()));
			java.util.Date expdDate = Date.from(instantExpd);
			if (expdDate.before(mdfDate)) {
				alert.setContentText("From date should be greater than or equal to To date");
			}

			if (!alert.getContentText().trim().equals("")) {
				alert.showAndWait();
				return;
			}

			LocalDate localDateFrom = fromDate.getValue();
			Date dateFrom = Date.from(localDateFrom.atStartOfDay(ZoneId.systemDefault()).toInstant());

			LocalDate localDateTo = toDate.getValue();
			Date dateTo = Date.from(localDateTo.atStartOfDay(ZoneId.systemDefault()).toInstant());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			logger.info("Entered Dates are " + sdf.format(dateFrom) + " AND " + sdf.format(dateTo));

			Calendar c = Calendar.getInstance();
			c.setTime(dateTo);
			c.add(Calendar.DATE, 1);
			dateTo = c.getTime();

			date1String = sdf.format(dateFrom);
			date2String = sdf.format(dateTo);

			logger.info("Dates for query are " + sdf.format(dateFrom) + " AND " + sdf.format(dateTo));

			String sqlSales = "SELECT   parent.BILL_NUMBER AS BILL_NUMBER" + ",null AS SALES_RETURN_NUMBER"
					+ ",parent.CREATED_DATE AS CREATED_DATE" + ",0 AS EXTRA_DISCOUNT" + ",child.ITEM_NAME AS ITEM_NAME"
					+ ",child.BATCH_NO AS BATCH_NO" + ",child.GROSS_AMOUNT AS GROSS_AMOUNT"
					+ ",child.PURCHASE_AMOUNT AS PURCHASE_AMOUNT" + ",child.DISCOUNT AS DISCOUNT"
					+ ",child.S_GST AS S_GST" + ",child.C_GST AS C_GST"
					+ ",child.QUANTITY AS QUANTITY,parent.SUCCESS AS SUCCESS" + " FROM BILL_MASTER parent "
					+ "INNER JOIN BILL_DETAILS child ON (parent.BILL_NUMBER = child.BILL_NUMBER) "
					+ "WHERE DATE (parent.CREATED_DATE) >= DATE  '" + date1String
					+ "'  AND DATE (parent.CREATED_DATE) < DATE  '" + date2String + "' ORDER BY CREATED_DATE ASC ";
			logger.info("QUERY 1 IS " + sqlSales);
			Query querySales = dbSearchUtils.getSession().createSQLQuery(sqlSales)
					.addScalar("BILL_NUMBER", Hibernate.INTEGER).addScalar("SALES_RETURN_NUMBER", Hibernate.INTEGER)
					.addScalar("CREATED_DATE", Hibernate.STRING).addScalar("EXTRA_DISCOUNT", Hibernate.FLOAT)
					.addScalar("ITEM_NAME", Hibernate.STRING).addScalar("BATCH_NO", Hibernate.STRING)
					.addScalar("GROSS_AMOUNT", Hibernate.FLOAT).addScalar("PURCHASE_AMOUNT", Hibernate.FLOAT)
					.addScalar("DISCOUNT", Hibernate.FLOAT).addScalar("S_GST", Hibernate.FLOAT)
					.addScalar("C_GST", Hibernate.FLOAT).addScalar("QUANTITY", Hibernate.INTEGER)
					.addScalar("SUCCESS", Hibernate.STRING);
			ScrollableResults scResults = querySales.scroll(ScrollMode.SCROLL_INSENSITIVE);

			float totalGrossSales = 0;
			float totalGrossPurchase = 0;

			float totalSGstSales = 0;
			float totalCGstSales = 0;
			float totalGstSales = 0;

			float totalSGstPurchase = 0;
			float totalCGstPurchase = 0;
			float totalGstPurchase = 0;

			// float totalTaxable = 0;
			float totalDiscounts = 0;
			// float grandTotal = 0;
			int billPrevNum = Integer.MIN_VALUE;

			ObservableList<XYChart.Series<String, Float>> answer = FXCollections.observableArrayList();

			Series<String, Float> totalSalesValues = new Series<String, Float>();
			Series<String, Float> totalPurchaseValues = new Series<String, Float>();
			Series<String, Float> totalProfitValues = new Series<String, Float>();

			Map<String, Float> totalSalesValuesHashMap = new LinkedHashMap<String, Float>();
			Map<String, Float> totalPurchaseValuesHashMap = new LinkedHashMap<String, Float>();
			Map<String, Float> totalProfitValuesHashMap = new LinkedHashMap<String, Float>();

			totalSalesValues.setName("Sales");
			totalPurchaseValues.setName("Purchase value of sold items");
			totalProfitValues.setName("Profit");

			gstSalesGroupByBillNo.clear();
			gstReturnGroupByBillNo.clear();
			int rowCount = 0;
			while (scResults.next()) {
				rowCount++;

				int billNo = scResults.getInteger(0);

				String dateString = scResults.getString(2);
				float extraDiscount = scResults.getFloat(3);
				String itemName = scResults.getString(4);
				String batchNo = scResults.getString(5);
				float grossAmount = scResults.getFloat(6);
				float purchaseAmount = scResults.getFloat(7);
				float discountPer = scResults.getFloat(8);
				float sGstPer = scResults.getFloat(9);
				float cGstPer = scResults.getFloat(10);
				int quantity = scResults.getInteger(11);
				String sucess = scResults.getString(12);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = dateFormat.parse(dateString);

				int EMPTY_BILL_IND = scResults.getInteger(11);

				if (EMPTY_BILL_IND == -1) {
					quantity = Math.abs(quantity);
				}

				String status = "";
				if (sucess.equals("0")) {
					status = "CANCELLED";
				} else {
					status = "SUCCESS";
				}

				fosDetailsSales.write((billNo + "," + dateFormat.format(date) + "," + status + "," + extraDiscount
						+ ",SALES," + itemName + "," + batchNo + "," + purchaseAmount + "," + grossAmount + "," + " "
						+ discountPer + "," + sGstPer + "," + cGstPer + "," + quantity).getBytes());
				logger.info((billNo + "," + dateFormat.format(date) + "," + status + "," + extraDiscount + ",SALES,"
						+ itemName + "," + batchNo + "," + purchaseAmount + "," + grossAmount + "," + " " + discountPer
						+ "," + sGstPer + "," + cGstPer + "," + quantity));
				float grossPurchase = 0;

				grossPurchase = purchaseAmount * quantity;
				fosDetailsSales.write(("," + grossPurchase).getBytes());
				float gstPurchase = ((grossPurchase * (sGstPer + cGstPer)) / (100 + (sGstPer + cGstPer)));

				float sgstPurchase = (((grossPurchase - gstPurchase) / 100) * sGstPer);

				float cgstPurchase = (((grossPurchase - gstPurchase) / 100) * cGstPer);
				fosDetailsSales.write(("," + sgstPurchase).getBytes());
				fosDetailsSales.write(("," + cgstPurchase).getBytes());
				if (status.equals("SUCCESS")) {
					totalGrossPurchase = totalGrossPurchase + grossPurchase;
					totalCGstPurchase = totalCGstPurchase + cgstPurchase;
					totalSGstPurchase = totalSGstPurchase + sgstPurchase;
					totalGstPurchase = totalGstPurchase + (cgstPurchase + sgstPurchase);
				}
				float grossSales = 0;

				grossSales = grossAmount * quantity;

				float gstSales = ((grossSales * (sGstPer + cGstPer)) / (100 + (sGstPer + cGstPer)));

				float sgstSales = (((grossSales - gstSales) / 100) * sGstPer);

				float cgstSales = (((grossSales - gstSales) / 100) * cGstPer);

				float discount = (grossSales - (sgstSales + cgstSales)) / 100 * discountPer;

				if (billPrevNum != billNo) {
					totalDiscounts = totalDiscounts + extraDiscount;
				}
				billPrevNum = billNo;

				float taxableAmount = grossSales - ((cgstSales + sgstSales) + discount);
				cgstSales = (taxableAmount / 100) * cGstPer;

				sgstSales = (taxableAmount / 100) * sGstPer;
				gstSales = cgstSales + sgstSales;

				fosDetailsSales.write(("," + (taxableAmount + (cgstSales + sgstSales))).getBytes());
				fosDetailsSales.write(("," + sgstSales).getBytes());
				fosDetailsSales.write(("," + cgstSales).getBytes());
				fosDetailsSales.write(("," + (discount + (discount / 100) * (sGstPer + cGstPer)) + "\n").getBytes());
				gst gst = gstSalesGroupByBillNo.get(billNo);

				if (gst == null)
					gst = new gst();
				gst.setCgst(gst.getCgst() + cgstSales);
				gst.setSgst(gst.getSgst() + sgstSales);
				gstSalesGroupByBillNo.put(billNo, gst);
				if (status.equals("SUCCESS")) {

					totalGrossSales = totalGrossSales + (taxableAmount + gstSales);
					totalCGstSales = totalCGstSales + cgstSales;
					totalSGstSales = totalSGstSales + sgstSales;
					totalGstSales = totalGstSales + (cgstSales + sgstSales);
				}

				if (status.equals("SUCCESS")) {
					String category = "";
					if (modeOfView.getValue().equals("DAILY")) {
						SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
						category = f1.format(date);
					} else if (modeOfView.getValue().equals("MONTHLY")) {
						SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM");
						category = f1.format(date);
					} else if (modeOfView.getValue().equals("YEARLY")) {
						SimpleDateFormat f1 = new SimpleDateFormat("yyyy");
						category = f1.format(date);
					}
					float totalSalesCumulative = 0;
					if (totalSalesValuesHashMap.containsKey(category)) {
						totalSalesCumulative = totalSalesValuesHashMap.get(category);
						totalSalesCumulative = totalSalesCumulative + (taxableAmount + gstSales);
					} else {
						totalSalesCumulative = totalSalesCumulative + (taxableAmount + gstSales);
					}
					totalSalesValuesHashMap.put(category, totalSalesCumulative);

					float totalPurchaseCumulative = 0;
					if (totalPurchaseValuesHashMap.containsKey(category)) {
						totalPurchaseCumulative = totalPurchaseValuesHashMap.get(category);

						totalPurchaseCumulative = totalPurchaseCumulative + grossPurchase;

					} else {

						totalPurchaseCumulative = totalPurchaseCumulative + grossPurchase;

					}
					totalPurchaseValuesHashMap.put(category, totalPurchaseCumulative);
					float totalProfitCumulative = 0;
					if (totalProfitValuesHashMap.containsKey(category)) {
						totalProfitCumulative = totalProfitValuesHashMap.get(category);

						totalProfitCumulative = totalProfitCumulative
								+ ((taxableAmount + gstSales) - purchaseAmount - (gstSales - gstPurchase));

					} else {
						totalProfitCumulative = totalProfitCumulative
								+ ((taxableAmount + gstSales) - purchaseAmount - (gstSales - gstPurchase));

					}
					totalProfitValuesHashMap.put(category, totalProfitCumulative);
				}
			}
			if (rowCount == 0) {
				logger.info("No data returned by the query ");
			}
			String sqlSalesReturn = "SELECT   parent.BILL_NO AS BILL_NUMBER"
					+ ",parent.SALES_RETURN_NUMBER SALES_RETURN_NUMBER" + ",parent.CREATED_DATE AS CREATED_DATE"
					+ ",0 AS EXTRA_DISCOUNT" + ",child.ITEM_NAME AS ITEM_NAME" + ",child.BATCH_NO AS BATCH_NO"
					+ ",child.GROSS_AMOUNT AS GROSS_AMOUNT" + ",child.PURCHASE_AMOUNT AS PURCHASE_AMOUNT"
					+ ",child.DISCOUNT AS DISCOUNT" + ",child.S_GST AS S_GST" + ",child.C_GST AS C_GST"
					+ ",child.QUANTITY AS QUANTITY" + " FROM SALES_RETURN_MASTER parent "
					+ "INNER JOIN SALES_RETURN_DETAILS child ON (parent.SALES_RETURN_NUMBER = child.SALES_RETURN_NUMBER) "
					+ "WHERE DATE (parent.CREATED_DATE) >= DATE  '" + date1String
					+ "'  AND DATE (parent.CREATED_DATE) < DATE  '" + date2String + "' ORDER BY CREATED_DATE ASC";
			logger.info("QUERY 2 IS " + sqlSalesReturn);
			Query querySalesReturn = dbSearchUtils.getSession().createSQLQuery(sqlSalesReturn)
					.addScalar("BILL_NUMBER", Hibernate.INTEGER).addScalar("SALES_RETURN_NUMBER", Hibernate.INTEGER)
					.addScalar("CREATED_DATE", Hibernate.STRING).addScalar("EXTRA_DISCOUNT", Hibernate.FLOAT)
					.addScalar("ITEM_NAME", Hibernate.STRING).addScalar("BATCH_NO", Hibernate.STRING)
					.addScalar("GROSS_AMOUNT", Hibernate.FLOAT).addScalar("PURCHASE_AMOUNT", Hibernate.FLOAT)
					.addScalar("DISCOUNT", Hibernate.FLOAT).addScalar("S_GST", Hibernate.FLOAT)
					.addScalar("C_GST", Hibernate.FLOAT).addScalar("QUANTITY", Hibernate.INTEGER);

			scResults = querySalesReturn.scroll(ScrollMode.SCROLL_INSENSITIVE);
			float totalGrossSales_return = 0;
			float totalGrossPurchase_return = 0;

			float totalSGstSales_return = 0;
			float totalCGstSales_return = 0;
			float totalGstSales_return = 0;

			float totalSGstPurchase_return = 0;
			float totalCGstPurchase_return = 0;
			float totalGstPurchase_return = 0;
			rowCount = 0;
			while (scResults.next())

			{
				rowCount++;
				int billNo = scResults.getInteger(0);
				Integer salesReturnNumber = scResults.getInteger(1);
				String dateString = scResults.getString(2);
				float extraDiscount = scResults.getFloat(3);
				String itemName = scResults.getString(4);
				String batchNo = scResults.getString(5);
				float grossAmount = scResults.getFloat(6);
				float purchaseAmount = scResults.getFloat(7);
				float discountPer = scResults.getFloat(8);
				float sGstPer = scResults.getFloat(9);
				float cGstPer = scResults.getFloat(10);
				int quantity = scResults.getInteger(11);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = dateFormat.parse(dateString);

				fosDetailsReturn.write((billNo + "," + salesReturnNumber + "," + date + "," + extraDiscount
						+ ",SALES_RETURN," + itemName + "," + batchNo + "," + purchaseAmount + "," + grossAmount + ","
						+ " " + discountPer + "," + sGstPer + "," + cGstPer + "," + quantity).getBytes());
				logger.info((billNo + "," + salesReturnNumber + "," + date + "," + extraDiscount + ",SALES_RETURN,"
						+ itemName + "," + batchNo + "," + purchaseAmount + "," + grossAmount + "," + " " + discountPer
						+ "," + sGstPer + "," + cGstPer + "," + quantity));
				float grossPurchase = 0;

				grossPurchase = purchaseAmount * quantity;
				fosDetailsReturn.write(("," + grossPurchase).getBytes());
				float gstPurchase = ((grossPurchase * (sGstPer + cGstPer)) / (100 + (sGstPer + cGstPer)));

				float sgstPurchase = (((grossPurchase - gstPurchase) / 100) * sGstPer);

				float cgstPurchase = (((grossPurchase - gstPurchase) / 100) * cGstPer);

				fosDetailsReturn.write(("," + sgstPurchase).getBytes());
				fosDetailsReturn.write(("," + cgstPurchase).getBytes());
				totalGrossPurchase_return = totalGrossPurchase_return + grossPurchase;
				totalCGstPurchase_return = totalCGstPurchase_return + cgstPurchase;
				totalSGstPurchase_return = totalSGstPurchase_return + sgstPurchase;
				totalGstPurchase_return = totalGstPurchase_return + (cgstPurchase + sgstPurchase);

				float grossSales = 0;

				grossSales = grossAmount * quantity;

				float gstSales = ((grossSales * (sGstPer + cGstPer)) / (100 + (sGstPer + cGstPer)));

				float sgstSales = (((grossSales - gstSales) / 100) * sGstPer);

				float cgstSales = (((grossSales - gstSales) / 100) * cGstPer);

				float discount = (grossSales - (sgstSales + cgstSales)) / 100 * discountPer;

				if (billPrevNum != billNo) {
					totalDiscounts = totalDiscounts + extraDiscount;
				}
				billPrevNum = billNo;

				float taxableAmount = grossSales - ((cgstSales + sgstSales) + discount);
				cgstSales = (taxableAmount / 100) * cGstPer;

				sgstSales = (taxableAmount / 100) * sGstPer;
				gstSales = cgstSales + sgstSales;
				fosDetailsReturn.write(("," + (taxableAmount + (cgstSales + sgstSales))).getBytes());
				fosDetailsReturn.write(("," + sgstSales).getBytes());
				fosDetailsReturn.write(("," + cgstSales).getBytes());
				fosDetailsReturn.write(("," + (discount + (discount / 100) * (sGstPer + cGstPer)) + "\n").getBytes());
				gst gst = gstReturnGroupByBillNo.get(salesReturnNumber);
				if (gst == null)
					gst = new gst();
				gst.setCgst(gst.getCgst() + cgstSales);
				gst.setSgst(gst.getSgst() + sgstSales);
				gstReturnGroupByBillNo.put(salesReturnNumber, gst);
				totalGrossSales_return = totalGrossSales_return + (taxableAmount + gstSales);
				totalCGstSales_return = totalCGstSales_return + cgstSales;
				totalSGstSales_return = totalSGstSales_return + sgstSales;
				totalGstSales_return = totalGstSales_return + (cgstSales + sgstSales);

				String category = "";
				if (modeOfView.getValue().equals("DAILY")) {
					SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
					category = f1.format(date);
				} else if (modeOfView.getValue().equals("MONTHLY")) {
					SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM");
					category = f1.format(date);
				} else if (modeOfView.getValue().equals("YEARLY")) {
					SimpleDateFormat f1 = new SimpleDateFormat("yyyy");
					category = f1.format(date);
				}
				float totalSalesCumulative = 0;
				if (totalSalesValuesHashMap.containsKey(category)) {
					totalSalesCumulative = totalSalesValuesHashMap.get(category);

					totalSalesCumulative = totalSalesCumulative - (taxableAmount + gstSales);
				} else {

					totalSalesCumulative = totalSalesCumulative - (taxableAmount + gstSales);
				}
				totalSalesValuesHashMap.put(category, totalSalesCumulative);

				float totalPurchaseCumulative = 0;
				if (totalPurchaseValuesHashMap.containsKey(category)) {
					totalPurchaseCumulative = totalPurchaseValuesHashMap.get(category);

					totalPurchaseCumulative = totalPurchaseCumulative - grossPurchase;
				} else {

					totalPurchaseCumulative = totalPurchaseCumulative - grossPurchase;
				}
				totalPurchaseValuesHashMap.put(category, totalPurchaseCumulative);
				float totalProfitCumulative = 0;
				if (totalProfitValuesHashMap.containsKey(category)) {
					totalProfitCumulative = totalProfitValuesHashMap.get(category);

					totalProfitCumulative = totalProfitCumulative
							- ((taxableAmount + gstSales) - purchaseAmount - (gstSales - gstPurchase));

				} else {
					totalProfitCumulative = totalProfitCumulative
							- ((taxableAmount + gstSales) - purchaseAmount - (gstSales - gstPurchase));

				}
				totalProfitValuesHashMap.put(category, totalProfitCumulative);

			}

			if (rowCount == 0) {
				logger.info("No data returned by the query ");
			}
			Float valueBefore = null;
			int i = 0;
			for (Entry<String, Float> entry : totalSalesValuesHashMap.entrySet()) {

				Data data = new XYChart.Data(entry.getKey(), entry.getValue());
				data.setNode(new HoveredThresholdNode((i == 0) ? 0 : valueBefore, entry.getValue()));
				valueBefore = entry.getValue();
				totalSalesValues.getData().add(data);
				i++;

			}
			i = 0;
			for (Entry<String, Float> entry : totalPurchaseValuesHashMap.entrySet()) {

				Data data = new XYChart.Data(entry.getKey(), entry.getValue());
				data.setNode(new HoveredThresholdNode((i == 0) ? 0 : valueBefore, entry.getValue()));
				valueBefore = entry.getValue();
				totalPurchaseValues.getData().add(data);
				i++;
			}
			i = 0;
			for (Entry<String, Float> entry : totalProfitValuesHashMap.entrySet()) {

				Data data = new XYChart.Data(entry.getKey(), entry.getValue());

				data.setNode(new HoveredThresholdNode((i == 0) ? 0 : valueBefore, entry.getValue()));
				valueBefore = entry.getValue();
				totalProfitValues.getData().add(data);
				i++;
			}

			// *********************************

			Float[] result = prepareSalesBillReportFiles(fosSales);
			float totalSalesFloat = result[0];
			float totalSalesDiscountGivem = result[1];
			float totalSalesReturnFloat = prepareSalesReturnBillReportFiles(fosReturn);
			preparePurchaseBillReportFiles(fosPurchase);

			if (Main.prop.getProperty("gst_tax_method").equals("COMPOUNDING")) {

				logger.info("TAX MODE IS COMPOUNTING");
				float compoundSGst = Float.parseFloat(Main.prop.getProperty("compount_sgst_per"));

				float compoundCGst = Float.parseFloat(Main.prop.getProperty("compount_cgst_per"));

				Main.prop.getProperty("gst_tax_method");
				logger.info("TOTAL SALES IS " + totalSalesFloat);
				totalSales.setText(totalSalesFloat + "");
				totalPurchase.setText(totalGrossPurchase + "");

				gstCollected.setText("NA");
				gstPayesDuringPurchase.setText("NA");

				sgstCollected.setText("NA");
				sgstPayesDuringPurchase.setText("NA");

				cgstCollected.setText("NA");
				cgstPayesDuringPurchase.setText("NA");
				logger.info("TOTAL SALES RETURN IS " + totalSalesReturnFloat);
				totalReturn.setText(totalSalesReturnFloat + "");
				totalPurchase_return.setText(totalGrossPurchase_return + "");

				gstCollected_return.setText("NA");
				gstPayesDuringPurchase_return.setText("NA");

				sgstCollected_return.setText("NA");
				sgstPayesDuringPurchase_return.setText("NA");

				cgstCollected_return.setText("NA");
				cgstPayesDuringPurchase_return.setText("NA");

				float remainingCgstpayable = ((totalSalesFloat / 100) * (compoundCGst))
						- ((totalSalesReturnFloat / 100) * (compoundCGst));

				float remainingSgstpayable = ((totalSalesFloat / 100) * (compoundSGst))
						- ((totalSalesReturnFloat / 100) * (compoundSGst));

				float remainingGgstpayable = ((totalSalesFloat / 100) * (compoundSGst + compoundCGst))
						- ((totalSalesReturnFloat / 100) * (compoundSGst + compoundCGst));

				remainingCgstPayable.setText(remainingCgstpayable + "");
				remainingSgstPayable.setText(remainingSgstpayable + "");
				remainingGstPayable.setText(remainingGgstpayable + "");

				totalDiscountGiven.setText(totalSalesDiscountGivem + "");
				answer.addAll(totalSalesValues);
				lineChart.setData(answer);
				lineChart.setTitle("Sales Summary");
			} else {
				logger.info("TAX MODE IS NON COMPOUNTING");
				logger.info("TOTAL SALES IS " + totalGrossSales);
				totalSales.setText(totalGrossSales + "");
				totalPurchase.setText(totalGrossPurchase + "");

				gstCollected.setText(totalGstSales + "");
				gstPayesDuringPurchase.setText(totalGstPurchase + "");

				sgstCollected.setText(totalSGstSales + "");
				sgstPayesDuringPurchase.setText(totalSGstPurchase + "");

				cgstCollected.setText(totalCGstSales + "");
				cgstPayesDuringPurchase.setText(totalCGstPurchase + "");

				totalReturn.setText(totalGrossSales_return + "");
				totalPurchase_return.setText(totalGrossPurchase_return + "");

				gstCollected_return.setText(totalGstSales_return + "");
				gstPayesDuringPurchase_return.setText(totalGstPurchase_return + "");

				sgstCollected_return.setText(totalSGstSales_return + "");
				sgstPayesDuringPurchase_return.setText(totalSGstPurchase_return + "");

				cgstCollected_return.setText(totalCGstSales_return + "");
				cgstPayesDuringPurchase_return.setText(totalCGstPurchase_return + "");

				remainingCgstPayable.setText(
						(totalCGstSales - totalCGstPurchase) - (totalCGstSales_return - totalCGstPurchase_return) + "");
				remainingSgstPayable.setText(
						(totalSGstSales - totalSGstPurchase) - (totalSGstSales_return - totalSGstPurchase_return) + "");
				remainingGstPayable.setText(
						(totalGstSales - totalGstPurchase) - (totalGstSales_return - totalGstPurchase_return) + "");

				if (Main.prop.getProperty("gst_tax_method").equals("COMPOUNDING")) {

					remainingGstPayable.setText((totalGrossSales / 100) * 1 + "");

				}
				totalDiscountGiven.setText(totalSalesDiscountGivem + "");

				answer.addAll(totalSalesValues, totalPurchaseValues, totalProfitValues);
				lineChart.setData(answer);
				lineChart.setTitle("Sales Summary");

			}
			PeriodicReportBillDetailsController.periodicReportBillDetailsController.initSalesTable();
			PeriodicReportBillDetailsController.periodicReportBillDetailsController.initSalesReturnTable();
			PeriodicReportBillDetailsController.periodicReportBillDetailsController.initSalesDetailedable();
			PeriodicReportBillDetailsController.periodicReportBillDetailsController.initSalesReturnDetailedable();
			PeriodicReportBillDetailsController.periodicReportBillDetailsController.initPuchaseTable();

			fosDetailsSales.close();

			fosDetailsReturn.close();
			fosSales.close();
			fosReturn.close();
			fosPurchase.close();
			logger.info("**************************** REPORT GENERATION - ENDS HERE*******************************");
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(e);
			logger.error(e.toString());
		}

	}

	private Float[] prepareSalesBillReportFiles(FileOutputStream fosSales)
			throws HibernateException, IOException, ParseException {
		String sqlSalesBill = "SELECT parent.BILL_NUMBER AS BILL_NUMBER, parent.GRAND_TOTAL AS GRAND_TOTAL, parent.CREATED_DATE AS CREATED_DATE,parent.EXTRA_DISCOUNT AS EXTRA_DISCOUNT,parent.SUCCESS AS SUCCESS "
				+ " FROM BILL_MASTER parent WHERE DATE (parent.CREATED_DATE) >= DATE '" + date1String + "' "
				+ " AND DATE (parent.CREATED_DATE) < DATE '" + date2String + "' ";

		queryBill = dbSearchUtils.getSession().createSQLQuery(sqlSalesBill).addScalar("BILL_NUMBER", Hibernate.INTEGER)
				.addScalar("GRAND_TOTAL", Hibernate.FLOAT).addScalar("CREATED_DATE", Hibernate.STRING)
				.addScalar("EXTRA_DISCOUNT", Hibernate.FLOAT).addScalar("SUCCESS", Hibernate.STRING);
		logger.info("QUERY 3 IS " + sqlSalesBill);
		ScrollableResults scResults = queryBill.scroll(ScrollMode.SCROLL_INSENSITIVE);

		Float[] result = new Float[2];
		float totalSalesFloat = 0;
		float totalSalesDiscountGivem = 0;
		int rowCount = 0;
		DecimalFormat df = new DecimalFormat("#0.00");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		while (scResults.next()) {

			Date date = dateFormat.parse(scResults.get(2) + "");

			rowCount++;
			totalSalesDiscountGivem = totalSalesDiscountGivem + (float) scResults.get(3);
			int billNO = (int) scResults.get(0);

			gst gst = gstSalesGroupByBillNo.get(billNO);

			String status = "";
			if (scResults.get(4).equals("0")) {
				status = "CANCELLED";
			} else {
				totalSalesFloat = totalSalesFloat + (float) scResults.get(1);
				status = "SUCCESS";
			}
			fosSales.write((scResults.get(0) + "," + dateFormat.format(date) + "," + status + ","
					+ df.format(scResults.get(1)) + "," + gst.sgst + "," + gst.cgst + "\n").getBytes());
			logger.info((scResults.get(0) + "," + dateFormat.format(date) + "," + status + "," + scResults.get(1) + ","
					+ gst.sgst + "," + gst.cgst + "\n"));

		}

		if (rowCount == 0) {
			logger.info("No data returned by the query ");
		}
		result[0] = totalSalesFloat;
		result[1] = totalSalesDiscountGivem;
		return result;
	}

	private float prepareSalesReturnBillReportFiles(FileOutputStream fosReturn)
			throws HibernateException, IOException, ParseException {

		String sqlSalesReturnBill = "SELECT parent.SALES_RETURN_NUMBER AS SALES_RETURN_NUMBER, parent.BILL_NO AS BILL_NUMBER, parent.GRAND_TOTAL AS GRAND_TOTAL, parent.CREATED_DATE AS CREATED_DATE "
				+ " FROM SALES_RETURN_MASTER parent  WHERE DATE (parent.CREATED_DATE) >= DATE '" + date1String + "' "
				+ " AND DATE (parent.CREATED_DATE) < DATE '" + date2String + "' ";

		querySalesReturn = dbSearchUtils.getSession().createSQLQuery(sqlSalesReturnBill)
				.addScalar("SALES_RETURN_NUMBER", Hibernate.INTEGER).addScalar("BILL_NUMBER", Hibernate.INTEGER)
				.addScalar("GRAND_TOTAL", Hibernate.FLOAT).addScalar("CREATED_DATE", Hibernate.STRING);
		logger.info("QUERY 4 IS " + sqlSalesReturnBill);
		ScrollableResults scResults = querySalesReturn.scroll(ScrollMode.SCROLL_INSENSITIVE);
		float totalSalesReturnFloat = 0;
		int rowCount = 0;
		DecimalFormat df = new DecimalFormat("#0.00");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		while (scResults.next()) {

			Date date = dateFormat.parse(scResults.get(3) + "");

			rowCount++;
			totalSalesReturnFloat = totalSalesReturnFloat + (float) scResults.get(2);
			int salesReturnBillNO = (int) scResults.get(0);
			gst gst = gstReturnGroupByBillNo.get(salesReturnBillNO);

			fosReturn.write((scResults.get(0) + "," + scResults.get(1) + "," + dateFormat.format(date) + ","
					+ df.format(scResults.get(2)) + "," + gst.sgst + "," + gst.cgst + "\n").getBytes());
			logger.info((scResults.get(0) + "," + scResults.get(1) + "," + dateFormat.format(date) + ","
					+ scResults.get(2) + "," + gst.sgst + "," + gst.cgst + "\n"));
		}
		if (rowCount == 0) {
			logger.info("No data returned by the query ");
		}
		return totalSalesReturnFloat;

	}

	private void preparePurchaseBillReportFiles(FileOutputStream fosPurchase)
			throws HibernateException, IOException, ParseException {
		String sqlPurchaseBill = "SELECT parent.INVOICE_NUMBER AS INVOICE_NUMBER, parent.CREATED_DATE AS CREATED_DATE, "
				+ "parent.COMPANY_NAME AS COMPANY_NAME,parent.HSN_SAC AS HSN_SAC,parent.GRAND_TOTAL AS GRAND_TOTAL"
				+ ",parent.CGST AS CGST,parent.SGST AS SGST,parent.PAYED AS PAYED "
				+ "FROM purchase_invoice_master parent WHERE DATE (parent.CREATED_DATE) >= DATE '" + date1String + "' "
				+ " AND DATE (parent.CREATED_DATE) < DATE '" + date2String + "'  ORDER BY parent.CREATED_DATE ASC";

		queryBill = dbSearchUtils.getSession().createSQLQuery(sqlPurchaseBill)
				.addScalar("INVOICE_NUMBER", Hibernate.STRING).addScalar("CREATED_DATE", Hibernate.STRING)
				.addScalar("COMPANY_NAME", Hibernate.STRING).addScalar("HSN_SAC", Hibernate.STRING)
				.addScalar("GRAND_TOTAL", Hibernate.FLOAT).addScalar("CGST", Hibernate.FLOAT)
				.addScalar("SGST", Hibernate.FLOAT).addScalar("PAYED", Hibernate.FLOAT);
		logger.info("QUERY 5 IS " + sqlPurchaseBill);
		ScrollableResults scResults = queryBill.scroll(ScrollMode.SCROLL_INSENSITIVE);
		int rowCount = 0;
		DecimalFormat df = new DecimalFormat("#0.00");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		while (scResults.next()) {

			Date date = dateFormat.parse(scResults.get(1) + "");

			rowCount++;

			fosPurchase.write((scResults.get(0) + "," + dateFormat.format(date) + "," + scResults.get(2) + ","
					+ scResults.get(3) + "," + df.format(scResults.get(4)) + "," + df.format(scResults.get(5)) + ","
					+ df.format(scResults.get(6)) + "," + df.format(scResults.get(7)) + "\n").getBytes());

			logger.info((scResults.get(0) + "," + dateFormat.format(date) + "," + scResults.get(2) + ","
					+ scResults.get(3) + "," + df.format(scResults.get(4)) + "," + df.format(scResults.get(5)) + ","
					+ df.format(scResults.get(6)) + "," + df.format(scResults.get(7)) + "\n"));
		}
		if (rowCount == 0) {
			logger.info("No data returned by the query ");
		}

	}

	@FXML
	void initialize() {
		dbSearchUtils = new DBSearchUtils();

	}

	class gst {

		gst() {
			cgst = 0;
			sgst = 0;
		}

		float cgst;
		float sgst;

		public float getCgst() {
			return cgst;
		}

		public void setCgst(float cgst) {
			this.cgst = cgst;
		}

		public float getSgst() {
			return sgst;
		}

		public void setSgst(float sgst) {
			this.sgst = sgst;
		}
	}

	class HoveredThresholdNode extends StackPane {
		HoveredThresholdNode(float priorValue, float value) {
			setPrefSize(15, 15);

			final Label label = createDataThresholdLabel(priorValue, value);

			setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					getChildren().setAll(label);
					setCursor(Cursor.NONE);
					toFront();
				}
			});
			setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					getChildren().clear();
					setCursor(Cursor.CROSSHAIR);
				}
			});
		}
	}

	private Label createDataThresholdLabel(float priorValue, float value) {
		final Label label = new Label(value + "");
		label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
		label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

		if (priorValue == 0) {
			label.setTextFill(Color.DARKGRAY);
		} else if (value > priorValue) {
			label.setTextFill(Color.FORESTGREEN);
		} else {
			label.setTextFill(Color.FIREBRICK);
		}

		label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
		return label;
	}
}
