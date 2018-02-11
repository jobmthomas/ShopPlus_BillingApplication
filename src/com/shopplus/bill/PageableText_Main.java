package com.shopplus.bill;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.log4j.Logger;

import com.shopplus.Main;

public class PageableText_Main {
	static Logger logger = Logger.getLogger(PageableText_Main.class);
	public int width = 0;
	public int height = 0;

	public boolean main(String fileName, int width, int height) {

		this.width = width;
		this.height = height;
		// Get the PrinterJob object that coordinates everything about printing:
		PrinterJob job = PrinterJob.getPrinterJob();

		// Get the page format:
		PageFormat format = definePageFormat(job);

		// Default attributes to printer:
		PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
		// attributes.add(OrientationRequested.PORTRAIT);
		attributes.add(MediaSizeName.NA_LETTER);

		// Create our PageableText object, and tell the PrinterJob about it
		try {
			job.setPageable(new PageableText(new File(fileName), format));
		} catch (IOException ex) {
			logger.error("Cannot load file to print" + ex.getMessage());
		}

		// Ask the user to select a printer, etc., and if not canceled, print
		// pages:
		try {
			if (job.printDialog(attributes)) {
				job.print();
				return true;
			} else {
				return false;
			}
		} catch (PrinterException e) {
			logger.error("Print failes " + e.getMessage());
			return false;
		}
	}

	private PageFormat definePageFormat(PrinterJob printJob) {
		PageFormat pageFormat = printJob.defaultPage();
		Paper paper = pageFormat.getPaper();
		// pageFormat.setOrientation(PageFormat.LANDSCAPE);

		int bill_upper_left_x = Integer.parseInt(Main.prop.getProperty("bill_upper_left_x"));
		int bill_upper_left_y = Integer.parseInt(Main.prop.getProperty("bill_upper_left_y"));

		if (Main.prop.getProperty("printer_page_size_calculation").equals("manual")) {

			int printer_page_width = Integer.parseInt(Main.prop.getProperty("printer_page_width"));
			int printer_page_height = Integer.parseInt(Main.prop.getProperty("printer_page_height"));

			paper.setSize((printer_page_width) - (bill_upper_left_x * 2),
					(printer_page_height) - (bill_upper_left_y * 2));

			paper.setImageableArea(bill_upper_left_x, bill_upper_left_y, printer_page_width, printer_page_height);

		} else {
			paper.setSize((8.5 * width) + (bill_upper_left_x * 2), (11 * height) + (bill_upper_left_y * 2));
			paper.setImageableArea(bill_upper_left_x, bill_upper_left_y, 6.75 * width, 9.75 * height);
		}

		pageFormat.setPaper(paper);
		return pageFormat;
	}
}