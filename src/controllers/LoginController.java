package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import com.shopplus.DBSearchUtils;
import com.shopplus.Main;
import com.shopplus.hibernate.Login;
import com.shopplus.hibernate.StockDetails;
import com.shopplus.hibernate.StockMaster;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController {

	@FXML
	private ResourceBundle resources;
	@FXML
	private ImageView img;
	@FXML
	private URL location;

	@FXML
	private PasswordField password;

	@FXML
	private TextField username;

	private DBSearchUtils dbUtils;
	@FXML
	private Label loginMessage;

	@FXML
	void close(MouseEvent event) throws IOException {
		Stage stage = (Stage) username.getScene().getWindow();
		stage.close();
	}

	@FXML
	void loginEnter(KeyEvent event) throws IOException {

		if (event.getCode() == KeyCode.ENTER) {
			login(null);
		}
	}

	@FXML
	void login(MouseEvent event) throws IOException {

		boolean loginSuccessfull = false;
		Login login = (Login) dbUtils.getSession().get(Login.class, username.getText());
		if (login != null) {

			String key = "Bar12345Bar12345"; // 128 bit key
			String initVector = "RandomInitVector"; // 16 bytes IV
			String username = login.getUSERNAME();
			String password = login.getPASSWORD();
			if (username.equals(this.username.getText())
					&& this.password.getText().equals(decrypt(key, initVector, password))) {
				loginSuccessfull = true;
			} else {
				loginMessage.setText("Invalid Username/password");
			}
		} else {
			loginMessage.setText("Invalid Username/password");
		}

		if (loginSuccessfull) {
			List<ExpiringItems> items = new ArrayList<>();
			String itemsDetailsForClipboard = "";
			if (Main.prop.getProperty("enable_expirycheck_in_the_begining").equals("true")) {

				Query query = dbUtils.getSession().createQuery("from StockMaster");

				query.setMaxResults(Integer.MAX_VALUE);
				query.setFirstResult(0);
				ScrollableResults scResults = query.scroll(ScrollMode.SCROLL_INSENSITIVE);

				while (scResults.next()) {

					StockMaster sm = (StockMaster) scResults.get(0);
					Set<StockDetails> stockDetails = sm.getStockDetails();
					int threshold = sm.getExpdWarningThresholdDays();
					for (StockDetails stockDetail : stockDetails) {
						if (stockDetail.getQuantityAvaialble() < threshold) {
							ExpiringItems ExpiringItem = new ExpiringItems(sm.getItemName(), stockDetail.getBatchId(),
									stockDetail.getQuantityAvaialble() + "", stockDetail.getDealerName());

							itemsDetailsForClipboard = itemsDetailsForClipboard + "\nITEM NAME: " + sm.getItemName()
									+ "\n\t BATCH NO: " + stockDetail.getBatchId() + "\n\t STOCK AVAILABLE: "
									+ stockDetail.getQuantityAvaialble() + "\n\t DEALER NAME :"
									+ stockDetail.getDealerName();
							items.add(ExpiringItem);
						}
					}

				}
				ExpiringItemsController.items = items;
				ExpiringItemsController.itemsDetailsForClipboard = itemsDetailsForClipboard;
				if (items.size() > 0) {

					Main.currentUserRole = login.getROLE();
					Parent root = FXMLLoader.load(getClass().getResource("ExpiringItems.fxml"));
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

					Stage stageLogin = (Stage) username.getScene().getWindow();
					stageLogin.close();
				}

			}

			if (items.size() == 0) {

				Main.currentUserRole = login.getROLE();

				Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setTitle("ShopPlus");
				stage.setScene(scene);
				stage.setResizable(false);
				scene.getStylesheets().add(Main.prop.getProperty("css_file_path"));

				FileInputStream f = new FileInputStream(Main.prop.getProperty("app_icon_image_path"));
				stage.getIcons().add(new Image(f));
				stage.show();

				Stage stageLogin = (Stage) username.getScene().getWindow();
				stageLogin.close();
			}

		}

	}

	@FXML
	void initialize() {
		assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'Untitled'.";
		assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'Untitled'.";

		dbUtils = new DBSearchUtils();
		File imafeFile = new File(Main.prop.getProperty("app_icon_image_path"));
		Image image = new Image(imafeFile.toURI().toString());
		img.setImage(image);
	}

	public static String decrypt(String key, String initVector, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
