package com.shopplus.bill;

import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.util.Vector;

import com.shopplus.Main;

public class PageableText implements Pageable, Printable {
	// Constants for font name, size, style and line spacing

	PageFormat format; // The page size, margins, and orientation
	Vector lines; // The text to be printed, broken into lines
	Font font;
	Font fontSpecial;// The font to print with
	int linespacing; // How much space between lines
	int linesPerPage; // How many lines fit on a page
	int numPages; // How many pages required to print all lines
	int baseline = -1; // The baseline position of the font.
	String[] specialLines;

	/** Create a PageableText object for a string of text */
	public PageableText(String text, PageFormat format) throws IOException {
		this(new StringReader(text), format);
	}

	/** Create a PageableText object for a file of text */
	public PageableText(File file, PageFormat format) throws IOException {
		this(new FileReader(file), format);
	}

	/** Create a PageableText object for a stream of text */
	public PageableText(Reader stream, PageFormat format) throws IOException {
		this.format = format;

		// First, read all the text, breaking it into lines.
		// This code ignores tabs, and does not wrap long lines.
		BufferedReader in = new BufferedReader(stream);
		lines = new Vector();
		String line;
		while ((line = in.readLine()) != null)
			lines.addElement(line);

		String FONTFAMILY = Main.prop.getProperty("FONTFAMILY");
		int FONTSIZE = Integer.parseInt(Main.prop.getProperty("FONTSIZE"));
		int FONTSTYLE = Integer.parseInt(Main.prop.getProperty("FONTSTYLE"));
		float LINESPACEFACTOR = Float.parseFloat(Main.prop.getProperty("LINESPACEFACTOR"));

		String FONTFAMILY_SPECIAL = Main.prop.getProperty("FONTFAMILY_SPECIAL");
		int FONTSIZE_SPECIAL = Integer.parseInt(Main.prop.getProperty("FONTSIZE_SPECIAL"));
		int FONTSTYLE_SPECIAL = Integer.parseInt(Main.prop.getProperty("FONTSTYLE_SPECIAL"));

		// Create the font we will use, and compute spacing between lines
		font = new Font(FONTFAMILY, FONTSTYLE, FONTSIZE);
		fontSpecial = new Font(FONTFAMILY_SPECIAL, FONTSTYLE_SPECIAL, FONTSIZE_SPECIAL);
		linespacing = (int) (FONTSIZE * LINESPACEFACTOR);

		specialLines = Main.prop.getProperty("SPECIAL_LINES").split(",");

		for (int j = 0; j < specialLines.length; j++) {

			specialLines[j] = Integer.parseInt(specialLines[j]) - 1 + "";
		}
		// Figure out how many lines per page, and how many pages
		linesPerPage = (int) Math.floor(format.getImageableHeight() / linespacing);
		numPages = (lines.size() - 1) / linesPerPage + 1;
	}

	// These are the methods of the Pageable interface.
	// Note that the getPrintable() method returns this object, which means
	// that this class must also implement the Printable interface.
	public int getNumberOfPages() {
		return numPages;
	}

	public PageFormat getPageFormat(int pagenum) {
		return format;
	}

	public Printable getPrintable(int pagenum) {
		return this;
	}

	/**
	 * This is the print() method of the Printable interface. It does most of
	 * the printing work.
	 */
	public int print(Graphics g, PageFormat format, int pagenum) {

		// Tell the PrinterJob if the page number is not a legal one.
		if ((pagenum < 0) | (pagenum >= numPages))
			return NO_SUCH_PAGE;

		// First time we're called, figure out the baseline for our font.
		// We couldn't do this earlier because we needed a Graphics object
		if (baseline == -1) {
			FontMetrics fm = g.getFontMetrics(font);
			baseline = fm.getAscent();
		}

		// Clear the background to white. This shouldn't be necessary, but is
		// required on some systems to workaround an implementation bug
		g.setColor(Color.white);
		g.fillRect((int) format.getImageableX(), (int) format.getImageableY(), (int) format.getImageableWidth(),
				(int) format.getImageableHeight());

		// Set the font and the color we will be drawing with.
		// Note that you cannot assume that black is the default color!

		g.drawString("AMMUS GREETING GALLERY", 0, 0);

		g.setFont(font);
		g.setColor(Color.black);

		// Figure out which lines of text we will print on this page
		int startLine = pagenum * linesPerPage;
		int endLine = startLine + linesPerPage - 1;
		if (endLine >= lines.size())
			endLine = lines.size() - 1;

		// Compute the position on the page of the first line.
		int x0 = (int) format.getImageableX();
		int y0 = (int) format.getImageableY() + baseline;

		// Loop through the lines, drawing them all to the page.
		for (int i = startLine; i <= endLine; i++) {
			// Get the line
			String line = (String) lines.elementAt(i);
			System.out.println(line + "," + i);
			boolean isSpeacial = false;
			for (int j = 0; j < specialLines.length; j++) {

				if (specialLines[j].equals(i + "")) {
					isSpeacial = true;
				}
			}
			if (isSpeacial) {
				g.setFont(fontSpecial);
				g.setColor(Color.black);
				if (line.length() > 0)
					g.drawString(line, x0, y0);

			} else {
				g.setFont(font);
				g.setColor(Color.black);
				if (line.length() > 0)
					g.drawString(line, x0, y0);

			}
			// Draw the line.
			// We use the integer version of drawString(), not the Java 2D
			// version that uses floating-point coordinates. A bug in early
			// Java2 implementations prevents the Java 2D version from working.
			if (line.length() > 0)
				g.drawString(line, x0, y0);

			// Move down the page for the next line.
			y0 += linespacing;
		}

		// Tell the PrinterJob that we successfully printed the page.
		return PAGE_EXISTS;
	}
}