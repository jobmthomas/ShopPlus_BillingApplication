package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.shopplus.DBUtils;
import com.shopplus.Main;
import com.shopplus.hibernate.DoctorMaster;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class DoctorManagementController implements Initializable {
	public static DoctorManagementController doctorManagementController;
	static Logger logger = Logger.getLogger(DoctorManagementController.class);
	public DBUtils dbUtils = null;

	@FXML
	public AutoCompleteTextField doctorNameAutofill;

	@FXML
	public TextField doctorName;

	@FXML
	public TextArea doctorAddress;

	@FXML
	private CheckBox likeSearch;

	@FXML
	private TableView<Doctor> doctorTable;

	@FXML
	public TableColumn<Doctor, String> doctorNameColumn;

	@FXML
	public TableColumn<Doctor, String> doctorAddressColumn;

	@FXML
	private Label doctorCountDown;

	public void addTextLimiter(final TextField tf, final int maxLength) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue,
					final String newValue) {
				if (tf.getText().length() > maxLength) {
					String s = tf.getText().substring(0, maxLength);
					tf.setText(s);
				} else {
					tf.setText(tf.getText().toUpperCase());
				}
			}
		});
	}

	public void addTextLimiter(final TextArea tf, final int maxLength, Label doctorCountDown) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue,
					final String newValue) {
				if (tf.getText().length() > maxLength) {
					String s = tf.getText().substring(0, maxLength);
					tf.setText(s);
				} else {
					tf.setText(tf.getText().toUpperCase());
				}

				if (doctorCountDown != null)
					doctorCountDown.setText(maxLength - tf.getText().length() + "");
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addTextLimiter(doctorAddress, 200, doctorCountDown);
		addTextLimiter(doctorName, 30);
		dbUtils = new DBUtils();

		doctorManagementController = this;
		doctorNameAutofill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (doctorNameAutofill.getText().length() == 0) {
					doctorNameAutofill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (doctorNameAutofill.newStockAdded == true) {
						String hql = "select  doctor_name from doctor_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						doctorNameAutofill.entries.clear();
						for (Object object : results) {
							doctorNameAutofill.entries.add(object.toString());
						}
						doctorNameAutofill.newStockAdded = false;
					}

					for (String eachItem : doctorNameAutofill.entries) {

						if (eachItem.toLowerCase().contains(doctorNameAutofill.getText().toLowerCase())) {
							searchResult.add(eachItem);
						}
					}
					if (searchResult.size() > 0) {

						List<CustomMenuItem> menuItems = new LinkedList<>();
						// If you'd like more entries, modify this line.
						int maxEntries = 10;
						int count = Math.min(searchResult.size(), maxEntries);
						for (int i = 0; i < count; i++) {
							final String result = String.format("%1$-" + 30 + "s", searchResult.get(i));
							Label entryLabel = new Label(result);
							CustomMenuItem item = new CustomMenuItem(entryLabel, true);
							item.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {
									doctorNameAutofill.setText(result);
									doctorNameAutofill.entriesPopup.hide();

								}
							});
							menuItems.add(item);

							doctorNameAutofill.entriesPopup.getItems().clear();
							doctorNameAutofill.entriesPopup.getItems().addAll(menuItems);

						}
						if (!doctorNameAutofill.entriesPopup.isShowing()) {
							doctorNameAutofill.entriesPopup.show(doctorNameAutofill, Side.BOTTOM, 0, 0);
						}
					} else {
						doctorNameAutofill.entriesPopup.hide();
					}
				}
			}
		});

		doctorNameAutofill.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				doctorNameAutofill.entriesPopup.hide();

			}
		});

		doctorTable.setRowFactory(tv -> {
			TableRow<Doctor> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Doctor rowData = doctorTable.getSelectionModel().getSelectedItem();
					doctorName.setText(rowData.doctorNameProperty().getValue());
					doctorAddress.setText(rowData.doctorAddressComplete);
				}
			});
			return row;
		});
	}

	@FXML
	void searchDoctor(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		Criteria criteria = dbUtils.getSession().createCriteria(DoctorMaster.class);

		Criteria criteriaLike = dbUtils.getSession().createCriteria(DoctorMaster.class);

		if (!doctorNameAutofill.getText().equals("")) {
			criteria.add(Restrictions.eq("doctorName", doctorNameAutofill.getText()).ignoreCase());
			criteriaLike.add(Restrictions.like("doctorName", "%" + doctorNameAutofill.getText() + "%").ignoreCase());
		}

		List<DoctorMaster> results;
		if (!likeSearch.isSelected()) {
			results = (List<DoctorMaster>) criteria.list();

		} else {
			results = (List<DoctorMaster>) criteriaLike.list();
		}
		populateTable(results);
	}

	private void populateTable(List<DoctorMaster> results) {
		if (!results.isEmpty()) {
			doctorNameColumn.setCellValueFactory(new PropertyValueFactory<Doctor, String>("doctorName"));
			doctorNameColumn.setEditable(false);

			doctorAddressColumn.setCellValueFactory(new PropertyValueFactory<Doctor, String>("doctorAddress"));
			doctorAddressColumn.setEditable(false);

			List<Doctor> doctorList = new ArrayList<>();

			for (Object obj : results) {
				DoctorMaster doctorMaster = (DoctorMaster) obj;
				Doctor doctor = new Doctor(doctorMaster.getDoctorName(), doctorMaster.getAddress() + "",
						doctorMaster.getAddress() + "");
				doctorList.add(doctor);

			}
			doctorTable.getItems().setAll(doctorList);

		} else {
			List<Doctor> doctorList = new ArrayList<>(0);
			doctorTable.getItems().setAll(doctorList);
		}

	}

	@FXML
	void save(MouseEvent event) {

		if (!doctorName.getText().equals("")) {
			dbUtils.beginTransaction();

			DoctorMaster doctorMaster = (DoctorMaster) dbUtils.getSession().get(DoctorMaster.class,
					doctorName.getText().trim());

			boolean newDoctor = false;
			if (doctorMaster == null) {
				newDoctor = true;
				doctorMaster = new DoctorMaster(doctorName.getText(), doctorAddress.getText());
			} else {
				doctorMaster.setAddress(doctorAddress.getText());
			}
			if (newDoctor) {
				dbUtils.getSession().save(doctorMaster);
				MainController.mainController.globalMessage.setText(" Doctor " + doctorName.getText() + " created");
			} else {
				dbUtils.getSession().merge(doctorMaster);
				MainController.mainController.globalMessage.setText(" Doctor " + doctorName.getText() + " updated");
			}
			searchDoctor(null);
			doctorNameAutofill.newStockAdded = true;
			dbUtils.endTransaction();
		}
	}

	@FXML
	void clear(MouseEvent event) {
		doctorName.setText("");
		doctorAddress.setText("");
	}

	@FXML
	void delete(MouseEvent event) {

		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("REMOVE STOCK");
		String s = "Do you really want to remove " + doctorName.getText() + "!";
		confirmation.setContentText(s);

		Optional<ButtonType> result = confirmation.showAndWait();

		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {

			DoctorMaster doctorMaster = (DoctorMaster) dbUtils.getSession().get(DoctorMaster.class,
					doctorName.getText().trim());

			if (doctorMaster != null) {
				dbUtils.beginTransaction();
				dbUtils.getSession().delete(doctorMaster);

				dbUtils.endTransaction();
				MainController.mainController.globalMessage.setText(" Doctor " + doctorName.getText() + " removed");

			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Wrong entries");
				alert.setContentText("Doctor with name " + doctorName.getText() + " not exist");
				alert.showAndWait();
				return;
			}
		}

		doctorNameAutofill.newStockAdded = true;
		searchDoctor(null);
		clear(null);

	}

}
