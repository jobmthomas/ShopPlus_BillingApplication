package com.shopplus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Query;

import com.shopplus.hibernate.BillNumberSequence;
import com.shopplus.hibernate.Login;
import com.shopplus.hibernate.SalesReturnNumberSequence;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	public static String currentUserRole;
	static Logger logger = Logger.getLogger(Main.class);
	Stage stage = new Stage();
	public static Properties prop = new Properties();

	public static void main(String[] args) {

		PropertyConfigurator.configure(Constants.DEFAULT_LOG4J_PROPERTIES_LOCATION);
		logger.info("Initializing system");

		CheckLisceneKey();

		logger.info("Reading Properties");
		readPropertiesFile();
		logger.info("Properties file read successfully");

		logger.info("Loadding driver class");
		DBSessionFactory.loadDriverClass();
		logger.info("Driver class loaded successfully");

		logger.info("Initializing database connection");
		DBSessionFactory.initializeDB();
		logger.info("Database initialized successfully");

		setUpSequence();

		launch(args);
	}

	private static void setUpSequence() {

		DBUtils dbUtils = new DBUtils();
		dbUtils.beginTransaction();

		Query q1 = dbUtils.getSession().createQuery("from BillNumberSequence");
		List<BillNumberSequence> result1 = (List<BillNumberSequence>) q1.list();
		if (result1.size() == 0) {
			int billNumner = Integer.parseInt(prop.getProperty("BillNumberInitialValue"));
			BillNumberSequence bns = new BillNumberSequence(billNumner);
			dbUtils.getSession().save(bns);
		}

		Query q2 = dbUtils.getSession().createQuery("from SalesReturnNumberSequence");
		List<BillNumberSequence> result2 = (List<BillNumberSequence>) q2.list();
		if (result2.size() == 0) {
			int billNumner = Integer.parseInt(prop.getProperty("SalesReturnNumberInitialValue"));
			SalesReturnNumberSequence sns = new SalesReturnNumberSequence(billNumner);
			dbUtils.getSession().save(sns);
		}

		Query q3 = dbUtils.getSession().createQuery("from Login");
		List<Login> result3 = (List<Login>) q3.list();
		if (result3.size() == 0) {

			String uSERNAME = "admin";
			String pASSWORD = "admin";
			String rOLE = "ADMIN";

			String key = "Bar12345Bar12345"; // 128 bit key
			String initVector = "RandomInitVector"; // 16 bytes IV
			pASSWORD = encrypt(key, initVector, pASSWORD);
			Login login = new Login(uSERNAME, pASSWORD, rOLE, new Date());
			dbUtils.getSession().save(login);
		}

		dbUtils.endTransaction();
	}

	private static void CheckLisceneKey() {

		try {

			String computerName = System.getenv("COMPUTERNAME");

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(computerName.getBytes());
			byte byteData[] = md.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			RandomAccessFile inFile = new RandomAccessFile("./conf/Liscence.txt", "rw");
			String lineData = inFile.readLine();
			if (lineData == null) {
				inFile.close();
				logger.info("This application is not liscenced to be use: " + "Please contact to buy/reniew liscence");
				logger.info(computerName);
				logger.exists("Exiting the application");
				System.exit(0);

			}
			if (!lineData.equals(hexString.toString())) {
				logger.info("This application is not liscenced to be use: "
						+ "Please contact the suplier to buy/reniew liscence");
				logger.info(computerName);

				logger.exists("Exiting the application");
				System.exit(0);
			}

		} catch (NoSuchAlgorithmException e) {

		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}

	}

	private static void readPropertiesFile() {
		try {
			InputStream input = new FileInputStream("./conf/properties");
			prop.load(input);
		} catch (IOException e) {

			logger.error("Unable to read propoerties file " + e.getMessage());
			logger.info("properties file should be prescent in  conf");
			System.exit(1);
		}
	}

	@Override
	public void start(Stage arg0) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
		Scene scene = new Scene(root);
		stage.setTitle("ShopPlus");
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setResizable(false);
		scene.getStylesheets().add(prop.getProperty("css_file_path"));

		FileInputStream f = new FileInputStream(prop.getProperty("app_icon_image_path"));
		stage.getIcons().add(new Image(f));
		stage.show();

	}

	public static String encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
