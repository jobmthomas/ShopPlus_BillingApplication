package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

	private StringProperty userName;

	private StringProperty role;
	private StringProperty createdOn;

	public User(String userName, String role, String createdOn) {

		setUserName(userName);

		setRole(role);
		setCreatedOn(createdOn);
	}

	private void setCreatedOn(String createdOn) {
		createdOnProperty().set(createdOn);
	}

	private void setUserName(String userName) {
		userNameProperty().set(userName);
	}

	private void setRole(String role) {
		roleProperty().set(role);
	}

	public StringProperty createdOnProperty() {
		if (createdOn == null)
			createdOn = new SimpleStringProperty(this, "createdOn");
		return createdOn;
	}

	public StringProperty userNameProperty() {
		if (userName == null)
			userName = new SimpleStringProperty(this, "userName");
		return userName;
	}

	public StringProperty roleProperty() {
		if (role == null)
			role = new SimpleStringProperty(this, "role");
		return role;
	}

}
