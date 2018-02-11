package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBUtils;
import com.shopplus.hibernate.DealerMaster;
import com.shopplus.hibernate.Login;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UserManagementController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableColumn<User, String> actionColumn;

	@FXML
	private PasswordField password;

	@FXML
	private TableColumn<User, String> createdOn;

	@FXML
	private PasswordField passwordReenter;

	@FXML
	private ComboBox<String> role;

	@FXML
	private TableColumn<User, String> roleColumn;

	@FXML
	private TableColumn<User, String> userNameColumn;

	@FXML
	private TableView<User> userTable;

	@FXML
	private TextField username;

	private DBUtils dbUtils;
	@FXML
	private Label message;

	@FXML
	void close(MouseEvent event) {

		Stage stage = (Stage) username.getScene().getWindow();
		stage.close();
	}

	@FXML
	void create(MouseEvent event) {

		Login login = (Login) dbUtils.getSession().get(Login.class, username.getText());
		if (login != null) {
			message.setText("Username not available");
		} else {
			String uSERNAME = username.getText();
			String pASSWORD = password.getText();
			if (password.getLength() < 4) {
				message.setText("Enter password with altlest 4 letters");
				return;
			}
			if (!pASSWORD.equals(passwordReenter.getText())) {
				message.setText("Password and Re-enter passworld should be same");
				return;
			}
			dbUtils.beginTransaction();
			String rOLE = role.getValue();

			String key = "Bar12345Bar12345"; // 128 bit key
			String initVector = "RandomInitVector"; // 16 bytes IV
			pASSWORD = encrypt(key, initVector, pASSWORD);
			login = new Login(uSERNAME, pASSWORD, rOLE, new Date());
			dbUtils.getSession().save(login);

			message.setText("User " + uSERNAME + " created successfully");
			dbUtils.endTransaction();
			populateTable();
		}

	}

	@FXML
	void initialize() {
		assert actionColumn != null : "fx:id=\"actionColumn\" was not injected: check your FXML file 'ManageUser.fxml'.";
		assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'ManageUser.fxml'.";

		assert passwordReenter != null : "fx:id=\"passwordReenter\" was not injected: check your FXML file 'ManageUser.fxml'.";
		assert role != null : "fx:id=\"role\" was not injected: check your FXML file 'ManageUser.fxml'.";
		assert roleColumn != null : "fx:id=\"roleColumn\" was not injected: check your FXML file 'ManageUser.fxml'.";
		assert userNameColumn != null : "fx:id=\"userNameColumn\" was not injected: check your FXML file 'ManageUser.fxml'.";
		assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'ManageUser.fxml'.";
		dbUtils = new DBUtils();

		userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
		userNameColumn.setEditable(false);

		roleColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));
		roleColumn.setEditable(false);

		createdOn.setCellValueFactory(new PropertyValueFactory<User, String>("createdOn"));
		createdOn.setEditable(false);

		actionColumn.setEditable(false);

		Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory = new Callback<TableColumn<User, String>, TableCell<User, String>>() {
			@Override
			public TableCell call(final TableColumn<User, String> param) {
				final TableCell<User, String> cell = new TableCell<User, String>() {

					final Button btn = new Button("REMOVE");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								User user = getTableView().getItems().get(getIndex());
								if (user.roleProperty().getValue().equals("ADMIN")) {
									Criteria criteria = dbUtils.getSession().createCriteria(Login.class);
									criteria.add(Restrictions.eq("ROLE", "ADMIN"));
									List<Login> result = criteria.list();
									if (result.size() == 1) {
										Alert confirmation = new Alert(AlertType.CONFIRMATION);
										confirmation.setTitle("REMOVE STOCK");
										String s = "This is the only ADMIN user remains , "
												+ "you have to create another ADMIN user before deleting this  ";
										confirmation.setContentText(s);
										confirmation.showAndWait();
										return;
									}
								}

								Alert confirmation = new Alert(AlertType.CONFIRMATION);
								confirmation.setTitle("REMOVE STOCK");
								String s = "Do you want to delete this User?";
								confirmation.setContentText(s);

								Optional<ButtonType> result = confirmation.showAndWait();
								if (!result.isPresent()) {
									return;
								}

								if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
									dbUtils.beginTransaction();
//									System.out.println(user.userNameProperty().getValue());
									Login login = (Login) dbUtils.getSession().get(Login.class,
											user.userNameProperty().getValue());
									dbUtils.getSession().delete(login);
									dbUtils.endTransaction();
									populateTable();
								}

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		actionColumn.setCellFactory(cellFactory);

		populateTable();

	}

	public void populateTable() {
		Query query = dbUtils.getSession().createQuery("from Login");

		List<Login> results = (List<Login>) query.list();

		List<User> userList = new ArrayList<User>();
		for (Login login : results) {
			User user = new User(login.getUSERNAME(), login.getROLE(), login.getCreatedOn() + "");
			userList.add(user);
		}
		ObservableList<User> items = userTable.getItems();
		items.clear();
		items.addAll(userList);
		userTable.setItems(items);
	}

	public static String encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			// System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted));

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
