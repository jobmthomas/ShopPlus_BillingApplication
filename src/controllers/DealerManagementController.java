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
import com.shopplus.hibernate.DealerMaster;

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

public class DealerManagementController implements Initializable {
	public static DealerManagementController dealerManagementController;
	static Logger logger = Logger.getLogger(DealerManagementController.class);

	public DBUtils dbUtils = null;

	@FXML
	public AutoCompleteTextField dealerNameAutofill;

	@FXML
	public TextField dealerName;

	@FXML
	public TextArea dealerAddress;

	@FXML
	private CheckBox likeSearch;

	@FXML
	private TableView<Dealer> dealerTable;

	@FXML
	public TableColumn<Dealer, String> dealerNameColumn;

	@FXML
	public TableColumn<Dealer, String> dealerAddressColumn;

	@FXML
	private Label dealerCountDown;

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

	public void addTextLimiter(final TextArea tf, final int maxLength, Label dealerCountDown) {
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
				if (dealerCountDown != null)
					dealerCountDown.setText(maxLength - tf.getText().length() + "");
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addTextLimiter(dealerAddress, 200, dealerCountDown);
		addTextLimiter(dealerName, 30);
		dbUtils = new DBUtils();
		dealerManagementController = this;
		dealerNameAutofill.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (dealerNameAutofill.getText().length() == 0) {
					dealerNameAutofill.entriesPopup.hide();
				} else {
					LinkedList<String> searchResult = new LinkedList<>();

					if (dealerNameAutofill.newStockAdded == true) {
						String hql = "select  dealer_name from dealer_master";
						Query query = dbUtils.getSession().createSQLQuery(hql);
						List results = query.list();
						dealerNameAutofill.entries.clear();
						for (Object object : results) {
							dealerNameAutofill.entries.add(object.toString());
						}
						dealerNameAutofill.newStockAdded = false;
					}

					for (String eachItem : dealerNameAutofill.entries) {

						if (eachItem.toLowerCase().contains(dealerNameAutofill.getText().toLowerCase())) {
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
									dealerNameAutofill.setText(result);
									dealerNameAutofill.entriesPopup.hide();

								}
							});
							menuItems.add(item);

							dealerNameAutofill.entriesPopup.getItems().clear();
							dealerNameAutofill.entriesPopup.getItems().addAll(menuItems);

						}
						if (!dealerNameAutofill.entriesPopup.isShowing()) {
							dealerNameAutofill.entriesPopup.show(dealerNameAutofill, Side.BOTTOM, 0, 0);
						}
					} else {
						dealerNameAutofill.entriesPopup.hide();
					}
				}
			}
		});

		dealerNameAutofill.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				dealerNameAutofill.entriesPopup.hide();

			}
		});

		dealerTable.setRowFactory(tv -> {
			TableRow<Dealer> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Dealer rowData = dealerTable.getSelectionModel().getSelectedItem();
					dealerName.setText(rowData.dealerNameProperty().getValue());
					dealerAddress.setText(rowData.dealerAddressComplete);
				}
			});
			return row;
		});
	}

	@FXML
	void searchDealer(MouseEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");

		if (!Main.currentUserRole.equals("ADMIN")) {
			alert.setHeaderText("Permission Denied");
			alert.showAndWait();
			return;
		}

		Criteria criteria = dbUtils.getSession().createCriteria(DealerMaster.class);

		Criteria criteriaLike = dbUtils.getSession().createCriteria(DealerMaster.class);

		if (!dealerNameAutofill.getText().equals("")) {
			criteria.add(Restrictions.eq("dealerName", dealerNameAutofill.getText()).ignoreCase());
			criteriaLike.add(Restrictions.like("dealerName", "%" + dealerNameAutofill.getText() + "%").ignoreCase());
		}

		List<DealerMaster> results;
		if (!likeSearch.isSelected()) {
			results = (List<DealerMaster>) criteria.list();

		} else {
			results = (List<DealerMaster>) criteriaLike.list();
		}
		populateTable(results);
	}

	private void populateTable(List<DealerMaster> results) {
		if (!results.isEmpty()) {
			dealerNameColumn.setCellValueFactory(new PropertyValueFactory<Dealer, String>("dealerName"));
			dealerNameColumn.setEditable(false);

			dealerAddressColumn.setCellValueFactory(new PropertyValueFactory<Dealer, String>("dealerAddress"));
			dealerAddressColumn.setEditable(false);

			List<Dealer> dealerList = new ArrayList<>();

			for (Object obj : results) {
				DealerMaster dealerMaster = (DealerMaster) obj;
				Dealer dealer = new Dealer(dealerMaster.getDealerName(), dealerMaster.getAddress() + "");
				dealerList.add(dealer);

			}
			dealerTable.getItems().setAll(dealerList);

		} else {
			List<Dealer> dealerList = new ArrayList<>(0);
			dealerTable.getItems().setAll(dealerList);
		}

	}

	@FXML
	void save(MouseEvent event) {

		if (!dealerName.getText().equals("")) {
			dbUtils.beginTransaction();

			DealerMaster dealerMaster = (DealerMaster) dbUtils.getSession().get(DealerMaster.class,
					dealerName.getText().trim());

			boolean newDealer = false;
			if (dealerMaster == null) {
				newDealer = true;
				dealerMaster = new DealerMaster(dealerName.getText(), dealerAddress.getText());
			} else {
				dealerMaster.setAddress(dealerAddress.getText());
			}
			if (newDealer) {
				dbUtils.getSession().save(dealerMaster);
				MainController.mainController.globalMessage.setText(" Dealer " + dealerName.getText() + " created");
			} else {
				dbUtils.getSession().merge(dealerMaster);
				MainController.mainController.globalMessage.setText(" Dealer " + dealerName.getText() + " updated");
			}
			searchDealer(null);
			dealerNameAutofill.newStockAdded = true;
			dbUtils.endTransaction();
		}
	}

	@FXML
	void clear(MouseEvent event) {
		dealerName.setText("");
		dealerAddress.setText("");
	}

	@FXML
	void delete(MouseEvent event) {

		Alert confirmation = new Alert(AlertType.CONFIRMATION);
		confirmation.setTitle("REMOVE STOCK");
		String s = "Do you really want to remove " + dealerName.getText() + "!";
		confirmation.setContentText(s);

		Optional<ButtonType> result = confirmation.showAndWait();

		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {

			DealerMaster dealerMaster = (DealerMaster) dbUtils.getSession().get(DealerMaster.class,
					dealerName.getText().trim());

			if (dealerMaster != null) {
				dbUtils.beginTransaction();
				dbUtils.getSession().delete(dealerMaster);

				dbUtils.endTransaction();
				MainController.mainController.globalMessage.setText(" Dealer " + dealerName.getText() + " removed");

			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Wrong entries");
				alert.setContentText("Dealer with name " + dealerName.getText() + " not exist");
				alert.showAndWait();
				return;
			}
		}

		dealerNameAutofill.newStockAdded = true;
		searchDealer(null);
		clear(null);

	}

}
