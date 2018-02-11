package controllers;

import java.util.SortedSet;
import java.util.TreeSet;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.
 * 
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField {
	/** The existing autocomplete entries. */
	public SortedSet<String> entries;
	/** The popup used to select an entry. */
	public ContextMenu entriesPopup;

	public boolean newStockAdded = true;

	/** Construct a new AutoCompleteTextField. */
	public AutoCompleteTextField() {
		super();
		entries = new TreeSet<>();

		entriesPopup = new ContextMenu();

	}

	/**
	 * Populate the entry set with the given search results. Display is limited
	 * to 10 entries, for performance.
	 * 
	 * @param searchResult
	 *            The set of matching strings.
	 */

}