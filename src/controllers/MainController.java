package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.shopplus.DBSearchUtils;
import com.shopplus.DBSessionFactory;
import com.shopplus.DBUtils;
import com.shopplus.Main;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController {

	static Logger logger = Logger.getLogger(MainController.class);
	public static MainController mainController = null;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	Label globalMessage;

	@FXML
	private AnchorPane manageStock;

	@FXML
	private TabPane tabedPaneMain;
	@FXML
	private ImageView img;

	@FXML
	private ImageView closeAppImg;

	@FXML
	private ImageView simpleBilling;

	@FXML
	void closeApp(MouseEvent event) {
		logger.info("DB SEARCH UTILS ");
		logger.info("TOTAL SESSIONS CREATED " + DBSearchUtils.allSesions.size());
		int count = 0;
		for (Session session : DBSearchUtils.allSesions) {
			if (session.isOpen()) {
				count++;
				session.close();
			}
		}
		logger.info("TOTAL SESSIONS ACTIVE " + count);
		logger.info("DB  UTILS ");
		logger.info("TOTAL SESSIONS CREATED " + DBUtils.allSesions.size());
		count = 0;
		for (Session session : DBUtils.allSesions) {
			if (session.isOpen()) {
				count++;
				session.close();
			}
		}
		logger.info("TOTAL SESSIONS ACTIVE " + count);
		DBSessionFactory.factory.close();
		Stage stage = (Stage) closeAppImg.getScene().getWindow();
		stage.close();
	}

	@FXML
	void initialize() {

		File imafeFile = new File(Main.prop.getProperty("manage_user_image_path"));
		Image image = new Image(imafeFile.toURI().toString());
		img.setImage(image);

		File imafeFile2 = new File(Main.prop.getProperty("simple_billing_image_path"));
		Image image2 = new Image(imafeFile2.toURI().toString());
		simpleBilling.setImage(image2);

		File imafeFile3 = new File(Main.prop.getProperty("quit_image_path"));
		Image image3 = new Image(imafeFile3.toURI().toString());
		closeAppImg.setImage(image3);

		mainController = this;

		globalMessage.setTextFill(Color.web("#e08f0d"));
		globalMessage.setText("Welcome!");

		VBox content = new VBox(); // create a VBox to act like a graphic
									// content for the Tab

		///////////////////////////////////////////
		Label label = new Label("Text");
		label.setAlignment(Pos.BOTTOM_CENTER);

		ObservableList<Tab> tabs = tabedPaneMain.getTabs();

		for (Tab tab : tabs) {

			if (tab.getText().equals(" Manage Stock")) {
				tab.setGraphic(content);
			}
		}

	}

	@FXML
	void manageUser(MouseEvent event) throws IOException {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Wrong entries");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		Parent root = FXMLLoader.load(getClass().getResource("ManageUser.fxml"));
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle("Manage Users");
		stage.setScene(scene);
		stage.initStyle(StageStyle.DECORATED);
		stage.setResizable(false);
		scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));

		FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
		stage.getIcons().add(new Image(f));
		stage.showAndWait();

	}

	@FXML
	void simpleBilling(MouseEvent event) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("SimpleBilling.fxml"));
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle("Simple Billing");
		stage.setScene(scene);
		stage.initStyle(StageStyle.DECORATED);
		stage.setResizable(false);
		scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));

		FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
		stage.getIcons().add(new Image(f));
		stage.showAndWait();

	}

}
