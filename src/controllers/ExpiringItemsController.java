package controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.shopplus.Main;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ExpiringItemsController {

	public static List<ExpiringItems> items;

	public static String itemsDetailsForClipboard;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableColumn<ExpiringItems, String> batchNo;

	@FXML
	private TableColumn<ExpiringItems, String> daysAhead;

	@FXML
	private TableColumn<ExpiringItems, String> balanceStock;

	@FXML
	private TableColumn<ExpiringItems, String> dealerName;
	@FXML
	private TableColumn<ExpiringItems, String> itemName;

	@FXML
	private Label message;

	@FXML
	private TableView<ExpiringItems> userTable;

	@FXML
	void close(MouseEvent event) {
		Stage stageLogin = (Stage) userTable.getScene().getWindow();
		stageLogin.close();
	}

	@FXML
	void copyToClipBoard(MouseEvent event) {
		StringSelection stringSelection = new StringSelection(itemsDetailsForClipboard);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	@FXML
	void ok(MouseEvent event) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle("ShopPlus");
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		stage.setResizable(false);
		scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));

		FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
		stage.getIcons().add(new Image(f));
		stage.show();

		Stage stageLogin = (Stage) userTable.getScene().getWindow();
		stageLogin.close();
	}

	@FXML
	void okEnter(KeyEvent event) throws IOException {

		if (event.getCode() == KeyCode.ENTER) {
			ok(null);
		}
	}

	@FXML
	void initialize() {

		itemName.setCellValueFactory(new PropertyValueFactory<ExpiringItems, String>("itemName"));
		itemName.setEditable(false);
		dealerName.setCellValueFactory(new PropertyValueFactory<ExpiringItems, String>("dealerName"));
		dealerName.setEditable(false);

		batchNo.setCellValueFactory(new PropertyValueFactory<ExpiringItems, String>("batchId"));
		batchNo.setEditable(false);

		balanceStock.setCellValueFactory(new PropertyValueFactory<ExpiringItems, String>("balanceStock"));
		balanceStock.setEditable(false);

		ObservableList<ExpiringItems> items = userTable.getItems();
		items.clear();
		items.addAll(ExpiringItemsController.items);

		userTable.setItems(items);
		userTable.getSortOrder().add(balanceStock);
	}

}
