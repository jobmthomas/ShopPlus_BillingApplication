package com.shopplus.bill;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.hibernate.SalesReturnDetails;
import com.shopplus.hibernate.SalesReturnMaster;

public class SalesReturnBillGenerator {
	Logger logger = Logger.getLogger(SalesReturnBillGenerator.class);
	public float grandTotal = 0;
	public float rateTotal = 0;
	public float discountTotal = 0;
	public float finalToal = 0;
	public int billWidth = 0;
	DBUtils dbUtils = null;
	public String shopName = "";
	public int shopNameLineNo = 0;
	public int shopNamePosition = 0;
	public int shopNameFontSize = 12;
	public int normalFontSize = 12;

	public static int billNo = 0;

	public Map<Integer, char[][]> generateBill(int billNo, DBUtils dbUtils) {
		this.dbUtils = dbUtils;
		grandTotal = 0;
		SalesReturnMaster bm = (SalesReturnMaster) dbUtils.getSession().get(SalesReturnMaster.class, billNo);
		Set<SalesReturnDetails> billDetailsList = bm.getSalesReturnDetails();
		Map<Integer, char[][]> bill = new HashMap<Integer, char[][]>();

		try {

			int slNo = 1;

			int itemNoInThePage = 0;
			int pageNo = 1;
			int maxItemInAPage = 0;
			int itemLineNoInThePage = 0;
			int itemLineNoInThePageInitial = 0;

			float grandTotal = 0;

			File stocks = new File(Main.prop.getProperty("sales_bill_template"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stocks);

			NodeList nodesSalesReturnDimentionList = doc.getElementsByTagName("BILL_DIMENTION");
			Node nodesSalesReturnDimention = nodesSalesReturnDimentionList.item(0);
			Element billDimentionElement = (Element) nodesSalesReturnDimention;
			billWidth = Integer.parseInt(billDimentionElement.getAttribute("width"));
			normalFontSize = Integer.parseInt(billDimentionElement.getAttribute("normalFontSize"));

			NodeList nodesShopNameList = doc.getElementsByTagName("SHOP_NAME");
			Node nodeShopName = nodesShopNameList.item(0);
			Element shopNameElement = (Element) nodeShopName;
			shopNamePosition = Integer.parseInt(shopNameElement.getAttribute("position"));
			shopNameLineNo = Integer.parseInt(shopNameElement.getAttribute("lineNo"));
			int size = Integer.parseInt(shopNameElement.getAttribute("size"));
			shopNameFontSize = Integer.parseInt(shopNameElement.getAttribute("fontSize"));
			shopName = shopNameElement.getAttribute("value");

			if ((shopNamePosition + size) - 1 > billWidth) {
				logger.error("Check the element " + shopNameElement.getNodeName() + " Size(" + size + ")+position("
						+ shopNamePosition + ") is greater than the bill's width");
				System.exit(0);
			}

			NodeList nodesItemList = doc.getElementsByTagName("ITEMS");
			Node nodesItem = nodesItemList.item(0);
			Element itemsElement = (Element) nodesItem;
			maxItemInAPage = Integer.parseInt(itemsElement.getAttribute("maxItemPerPage"));
			itemLineNoInThePageInitial = Integer.parseInt(itemsElement.getAttribute("lineNo"));
			itemLineNoInThePage = Integer.parseInt(itemsElement.getAttribute("lineNo"));
			NodeList itemChildNodes = itemsElement.getChildNodes();
			for (SalesReturnDetails billDetail : billDetailsList) {
				String itemNameToExplode = "";
				int itemNameSize = 0;
				int itemNamePosition = 0;
				String itemNameAlign = "";
				for (int i = 0; i < itemChildNodes.getLength(); i++) {

					Node itemChildNode = itemChildNodes.item(i);
					if (!itemChildNode.getNodeName().equals("#text")) {

						Element itemElement = (Element) itemChildNode;
						size = Integer.parseInt(itemElement.getAttribute("size"));
						String align = itemElement.getAttribute("align");
						if (align == null) {
							align = "right";
						}
						int position = Integer.parseInt(itemElement.getAttribute("position"));

						char[][] billPage = bill.get(pageNo);
						if (billPage == null) {
							char[][] billPageNew = new char[1][billWidth];
							billPageNew = fillBIllWithSpace(billPageNew);
							billPageNew = printGeneralInfo(billPageNew, doc, bm, billWidth, 0, billNo, "HEADER");
							bill.put(pageNo, billPageNew);
							billPage = bill.get(pageNo);
						}
						if ((position + size) - 1 > billWidth) {
							logger.error("Check the element " + itemElement.getNodeName() + " Size(" + size
									+ ")+position(" + position + ") is greater than the bill's width");
							System.exit(0);
						}
						// char[] billLine = billPage[(itemLineNoInThePage -
						// 1)];

						switch (itemChildNode.getNodeName()) {

						case "PRINT_CHAR":
							String value = itemElement.getAttribute("value");
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position, value, size,
									align);

							break;

						case "SL_NO":

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position, slNo + "", size,
									align);

							break;
						case "QTY":

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									billDetail.getQuantity() + "", size, align);
							break;
						case "ITEM_NAME":

							int currentSizeUpperLimit = 0;
							if (billDetail.getItemName().length() > size) {
								currentSizeUpperLimit = size;
								itemNameToExplode = billDetail.getItemName().substring(size,
										billDetail.getItemName().length());
							} else {
								currentSizeUpperLimit = billDetail.getItemName().length();
							}
							itemNameSize = size;
							itemNamePosition = position;
							itemNameAlign = align;
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									billDetail.getItemName().substring(0, currentSizeUpperLimit) + "", size, align);
							break;
						case "RACK":

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									billDetail.getRack() + "", size, align);
							break;
						case "COMPANY_NAME":

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									billDetail.getCompany() + "", size, align);
							break;
						case "BATCH_NO":

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									billDetail.getBatchNo() + "", size, align);
							break;
						case "EXPD":

							String format = itemElement.getAttribute("format");
							Date expd = billDetail.getExpd();
							SimpleDateFormat sdf = new SimpleDateFormat(format);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									sdf.format(expd) + "", size, align);
							break;
						case "RATE":
							String decimalFormat = itemElement.getAttribute("decimalFormat");

							DecimalFormat df = new DecimalFormat(decimalFormat);

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(billDetail.getGrossAmount()) + "", size, align);
							break;
						case "AMOUNT":

							decimalFormat = itemElement.getAttribute("decimalFormat");

							df = new DecimalFormat(decimalFormat);

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format((billDetail.getGrossAmount() * billDetail.getQuantity())) + "", size,
									align);
							break;
						case "DISCOUNT_PER":
							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(billDetail.getDiscount()) + "", size, align);
							break;
						case "DISCOUNT":

							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							float gross = billDetail.getGrossAmount() * billDetail.getQuantity();
							float gst = ((gross * (billDetail.getsGst() + billDetail.getcGst()))
									/ (100 + (billDetail.getsGst() + billDetail.getcGst())));

							float sgst = (((gross - gst) / 100) * billDetail.getsGst());

							float cgst = (((gross - gst) / 100) * billDetail.getcGst());

							// float discount = (gross - (sgst + cgst)) / 100 *
							// billDetail.getDiscount();

							float discount = (gross) / 100 * billDetail.getDiscount();

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(discount) + "", size, align);
							break;

						case "CGST_PER":

							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(billDetail.getcGst()) + "", size, align);
							break;

						case "CGST":
							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							gross = billDetail.getGrossAmount() * billDetail.getQuantity();

							gst = ((gross * (billDetail.getsGst() + billDetail.getcGst()))
									/ (100 + (billDetail.getsGst() + billDetail.getcGst())));

							sgst = (((gross - gst) / 100) * billDetail.getsGst());

							cgst = (((gross - gst) / 100) * billDetail.getcGst());

							discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();

							float taxableAmount = gross - ((cgst + sgst) + discount);
							cgst = (taxableAmount / 100) * billDetail.getcGst();

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(cgst) + "", size, align);
							break;
						case "SGST_PER":

							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(billDetail.getsGst()) + "", size, align);
							break;
						case "SGST":

							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							gross = billDetail.getGrossAmount() * billDetail.getQuantity();

							gst = ((gross * (billDetail.getsGst() + billDetail.getcGst()))
									/ (100 + (billDetail.getsGst() + billDetail.getcGst())));

							sgst = (((gross - gst) / 100) * billDetail.getsGst());

							cgst = (((gross - gst) / 100) * billDetail.getcGst());

							discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();

							taxableAmount = gross - ((cgst + sgst) + discount);

							sgst = (taxableAmount / 100) * billDetail.getsGst();

							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(sgst) + "", size, align);
							break;
						case "GST_PER":

							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(billDetail.getsGst() + billDetail.getcGst()) + "", size, align);
							break;
						case "GST":

							decimalFormat = itemElement.getAttribute("decimalFormat");
							df = new DecimalFormat(decimalFormat);

							gross = billDetail.getGrossAmount() * billDetail.getQuantity();

							gst = ((gross * (billDetail.getsGst() + billDetail.getcGst()))
									/ (100 + (billDetail.getsGst() + billDetail.getcGst())));

							sgst = (((gross - gst) / 100) * billDetail.getsGst());

							cgst = (((gross - gst) / 100) * billDetail.getcGst());

							discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();

							taxableAmount = gross - ((cgst + sgst) + discount);
							cgst = (taxableAmount / 100) * billDetail.getcGst();

							sgst = (taxableAmount / 100) * billDetail.getsGst();
							gst = cgst + sgst;
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(gst) + "", size, align);
							break;

						case "NET_TOTAL":
							decimalFormat = itemElement.getAttribute("decimalFormat");

							df = new DecimalFormat(decimalFormat);

							gross = billDetail.getGrossAmount() * billDetail.getQuantity();

							gst = ((gross * (billDetail.getsGst() + billDetail.getcGst()))
									/ (100 + (billDetail.getsGst() + billDetail.getcGst())));

							sgst = (((gross - gst) / 100) * billDetail.getsGst());

							cgst = (((gross - gst) / 100) * billDetail.getcGst());

							discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();

							taxableAmount = gross - ((cgst + sgst) + discount);
							cgst = (taxableAmount / 100) * billDetail.getcGst();

							sgst = (taxableAmount / 100) * billDetail.getsGst();
							gst = cgst + sgst;

							float netTotal = taxableAmount + gst;

							grandTotal = grandTotal + netTotal;
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, position,
									df.format(netTotal) + "", size, align);
							break;
						}

						// billPage[(itemLineNoInThePage - 1)] = billLine;
						bill.put(pageNo, billPage);
					}
				}
				if (!itemNameToExplode.equals("")) {
					while (true) {
						itemLineNoInThePage++;
						int currentSizeUpperLimit = 0;
						if (itemNameToExplode.length() > itemNameSize) {
							currentSizeUpperLimit = itemNameSize;
							itemNameToExplode = billDetail.getItemName().substring(itemNameSize,
									itemNameToExplode.length());
							char[][] billPage = bill.get(pageNo);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, itemNamePosition,
									itemNameToExplode.substring(0, currentSizeUpperLimit) + "", itemNameSize,
									itemNameAlign);
							bill.put(pageNo, billPage);
						} else {
							currentSizeUpperLimit = itemNameToExplode.length();
							char[][] billPage = bill.get(pageNo);
							billPage = putValueInSalesReturn(billPage, itemLineNoInThePage, itemNamePosition,
									itemNameToExplode.substring(0, currentSizeUpperLimit) + "", itemNameSize,
									itemNameAlign);
							bill.put(pageNo, billPage);
							break;
						}

					}
				}
				slNo++;
				itemNoInThePage++;
				itemLineNoInThePage++;
				if (itemNoInThePage == maxItemInAPage) {

					char[][] billPage = bill.get(pageNo);
					billPage = printGeneralInfo(billPage, doc, bm, billWidth, itemLineNoInThePage, billNo, "FOOTER");
					bill.put(pageNo, billPage);
					itemNoInThePage = 0;
					itemLineNoInThePage = itemLineNoInThePageInitial;
					pageNo++;
				}

			}
			itemLineNoInThePage--;
			char[][] billLastPage = bill.get(pageNo);

			billLastPage = printGeneralInfo(billLastPage, doc, bm, billWidth, itemLineNoInThePage, billNo, "FOOTER");
			bill.put(pageNo, billLastPage);

			billLastPage = printGeneralInfo(billLastPage, doc, bm, billWidth, itemLineNoInThePage, billNo, "TOTALS");
			bill.put(pageNo, billLastPage);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bill;

	}

	private char[][] printGeneralInfo(char[][] billPage, Document doc, SalesReturnMaster billMaster, int billWidth,
			int itemEndLIneNo, int billNo, String elementName) {

		SalesReturnMaster bm = (SalesReturnMaster) dbUtils.getSession().get(SalesReturnMaster.class, billNo);
		Set<SalesReturnDetails> billDetailsList = bm.getSalesReturnDetails();
		grandTotal = 0;
		rateTotal = 0;
		discountTotal = 0;
		float totalSGst = 0;
		float totalCGst = 0;
		float totalGst = 0;
		float totalTaxable = 0;

		for (SalesReturnDetails billDetail : billDetailsList) {

			float gross = billDetail.getGrossAmount() * billDetail.getQuantity();
			rateTotal = rateTotal + gross;
			float gst = ((gross * (billDetail.getsGst() + billDetail.getcGst()))
					/ (100 + (billDetail.getsGst() + billDetail.getcGst())));

			float sgst = (((gross - gst) / 100) * billDetail.getsGst());

			float cgst = (((gross - gst) / 100) * billDetail.getcGst());

			float discount = (gross - (sgst + cgst)) / 100 * billDetail.getDiscount();
			discountTotal = discountTotal + discount;
			float taxableAmount = gross - ((cgst + sgst) + discount);
			cgst = (taxableAmount / 100) * billDetail.getcGst();

			sgst = (taxableAmount / 100) * billDetail.getsGst();
			gst = cgst + sgst;

			totalTaxable = totalTaxable + taxableAmount;

			totalCGst = totalCGst + cgst;
			totalSGst = totalSGst + sgst;

			totalGst = totalGst + (cgst + sgst);
			grandTotal = grandTotal + taxableAmount + (cgst + sgst);
		}

		String decimalFormat = null;
		DecimalFormat df;
		NodeList nodesHeader = doc.getElementsByTagName(elementName);
		if (nodesHeader.getLength() != 0) {
			Node nodeHeader = nodesHeader.item(0);
			Element headerElement = (Element) nodeHeader;
			String inReferenceToItems = "";
			if (elementName.equals("TOTALS"))
				inReferenceToItems = headerElement.getAttribute("inReferenceToItems");
			NodeList headerChildNodes = headerElement.getChildNodes();

			for (int i = 0; i < headerChildNodes.getLength(); i++) {

				Node headerChildNode = headerChildNodes.item(i);
				if (!headerChildNode.getNodeName().equals("#text")) {

					Element headerChildElement = (Element) headerChildNode;
					int size = Integer.parseInt(headerChildElement.getAttribute("size"));
					String align = headerChildElement.getAttribute("align");
					if (align == null) {
						align = "right";
					}
					int position = Integer.parseInt(headerChildElement.getAttribute("position"));
					int lineNo = Integer.parseInt(headerChildElement.getAttribute("lineNo"));

					lineNo = lineNo + itemEndLIneNo;

					if (elementName.equals("TOTALS"))
						if (!inReferenceToItems.equals("true"))
							lineNo = lineNo;
					if ((position + size) - 1 > billWidth) {
						logger.error("Check the element " + headerChildElement.getNodeName() + " Size(" + size
								+ ")+position(" + position + ") is greater than the bill's width(" + billWidth + ")");
						System.exit(0);
					}

					switch (headerChildElement.getNodeName()) {
					case "CUSTOMER_NAME":
						billPage = putValueInSalesReturn(billPage, lineNo, position, billMaster.getCustomerName() + "",
								size, align);
						break;
					case "DR_NAME":
						billPage = putValueInSalesReturn(billPage, lineNo, position, billMaster.getDoctorName() + "",
								size, align);
						break;
					case "LISCENCE_NO":
						break;
					case "GSTIN":
						break;
					case "BILL_NO":
						billPage = putValueInSalesReturn(billPage, lineNo, position,
								billMaster.getSalesReturnNumber() + "", size, align);
						this.billNo = billMaster.getSalesReturnNumber();
						break;
					case "DATE":

						String format = headerChildElement.getAttribute("format");
						Date createdDate = billMaster.getCreatedDate();
						SimpleDateFormat sdf = new SimpleDateFormat(format);

						billPage = putValueInSalesReturn(billPage, lineNo, position, sdf.format(createdDate) + "", size,
								align);
						break;
					case "PRINT_CHAR":

						Node nodePrintChar = headerChildNode;
						Element printCharElement = (Element) nodePrintChar;
						String value = printCharElement.getAttribute("value");

						billPage = putValueInSalesReturn(billPage, lineNo, position, value, size, align);
						break;

					case "GRAND_TOTAL":

						Node nodeGrandTotal = headerChildNode;
						Element grandTotalElement = (Element) nodeGrandTotal;

						decimalFormat = grandTotalElement.getAttribute("decimalFormat");

						df = new DecimalFormat(decimalFormat);

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(grandTotal) + "", size,
								align);

						break;

					case "RATE_TOTAL":

						Node nodeRateTotal = headerChildNode;
						Element rateTotalElement = (Element) nodeRateTotal;

						decimalFormat = rateTotalElement.getAttribute("decimalFormat");

						df = new DecimalFormat(decimalFormat);

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(rateTotal) + "", size,
								align);

						break;
					case "DISCOUNT_TOTAL":

						Node nodeDiscountTotal = headerChildNode;
						Element discountTotalElement = (Element) nodeDiscountTotal;

						decimalFormat = discountTotalElement.getAttribute("decimalFormat");

						df = new DecimalFormat(decimalFormat);

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(discountTotal) + "",
								size, align);

						break;
					case "EXTRA_DISCOUNT":

						Node nodeExtraDiscount = headerChildNode;
						Element extraDiscountElement = (Element) nodeExtraDiscount;

						decimalFormat = extraDiscountElement.getAttribute("decimalFormat");
						df = new DecimalFormat(decimalFormat);
						float extraDiscount = 0;

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(extraDiscount) + "",
								size, align);
						break;
					case "FINAL_TOTAL":

						Node nodeFinalTotal = headerChildNode;
						Element finalTotalElement = (Element) nodeFinalTotal;

						decimalFormat = finalTotalElement.getAttribute("decimalFormat");
						df = new DecimalFormat(decimalFormat);

						extraDiscount = 0;
						float finalTotal = grandTotal - extraDiscount;

						BigDecimal finalTotalRounded = new BigDecimal(finalTotal + "").setScale(0,
								BigDecimal.ROUND_HALF_UP);
						finalToal = Float.parseFloat(df.format(finalTotalRounded));
						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(finalTotalRounded) + "",
								size, align);
						break;
					case "GST_TOTAL":

						Node nodeGstTotal = headerChildNode;
						Element gstTotalElement = (Element) nodeGstTotal;

						decimalFormat = gstTotalElement.getAttribute("decimalFormat");

						df = new DecimalFormat(decimalFormat);

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(totalGst) + "", size,
								align);
						break;
					case "CGST_TOTAL":
						Node nodeCGstTotal = headerChildNode;
						Element cGstTotalElement = (Element) nodeCGstTotal;

						decimalFormat = cGstTotalElement.getAttribute("decimalFormat");

						df = new DecimalFormat(decimalFormat);

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(totalCGst) + "", size,
								align);
					case "SGST_TOTAL":

						Node nodeSGstTotal = headerChildNode;
						Element sGstTotalElement = (Element) nodeSGstTotal;

						decimalFormat = sGstTotalElement.getAttribute("decimalFormat");

						df = new DecimalFormat(decimalFormat);

						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(totalSGst) + "", size,
								align);
						break;

					case "TAXABLE_AMOUNT_TOTAL":

						Node nodeTaxableAmountTotal = headerChildNode;
						Element taxableAmountTotalElement = (Element) nodeTaxableAmountTotal;
						decimalFormat = taxableAmountTotalElement.getAttribute("decimalFormat");
						df = new DecimalFormat(decimalFormat);
						billPage = putValueInSalesReturn(billPage, lineNo, position, df.format(totalTaxable) + "", size,
								align);
						break;
					}

				}
			}

		}

		return billPage;
	}

	public char[][] putValueInSalesReturn(char[][] bill, int line, int position, String value, int size, String align) {
		String valueFixedLength = fixedLengthString(value + "", size, align);
		char[] valueFixedLengthArray = valueFixedLength.toCharArray();
		// billLine.
		char[][] billNew;
		if (line > bill.length) {
			billNew = new char[line][billWidth];
			billNew = fillBIllWithSpace(billNew);
			for (int i = 0; i < bill.length; i++) {

				for (int j = 0; j < bill[i].length; j++) {
					billNew[i][j] = bill[i][j];
				}
			}

		} else {
			billNew = bill;
		}
		char[] billLine = billNew[line - 1];
		for (char c : valueFixedLengthArray) {
			billLine[(position - 1)] = c;
			position++;
		}

		return billNew;
	}

	private List<String> printArray(Map<Integer, char[][]> bill) {

		List<String> billAsString = new ArrayList<String>();
		for (Map.Entry<Integer, char[][]> entry : bill.entrySet()) {

			String output = "";
			for (int row = 0; row < entry.getValue().length; row++) {
				for (int col = 0; col < entry.getValue()[row].length; col++) {
					output = output + entry.getValue()[row][col] + "";
				}
				output = output + "\n";
			}
			billAsString.add(output);
		}
		return billAsString;

	}

	public String fixedLengthString(String string, int length, String align) {

		if (align.equals("left"))
			return String.format("%1$-" + length + "s", string);
		else if (align.equals("center")) {
			return center(string, length);
		} else {
			return String.format("%1$" + length + "s", string);
		}

	}

	public char[][] fillBIllWithSpace(char[][] billPageNew) {

		for (int row = 0; row < billPageNew.length; row++) {
			for (int col = 0; col < billPageNew[row].length; col++) {
				billPageNew[row][col] = ' ';
			}
		}
		return billPageNew;
	}

	public static String center(String s, int size) {
		return center(s, size, ' ');
	}

	public static String center(String s, int size, char pad) {
		if (s == null || size <= s.length())
			return s;

		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < (size - s.length()) / 2; i++) {
			sb.append(pad);
		}
		sb.append(s);
		while (sb.length() < size) {
			sb.append(pad);
		}
		return sb.toString();
	}

}