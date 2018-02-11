package com.shopplus;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class DBSessionFactory {

	final static Logger logger = Logger.getLogger(DBSessionFactory.class);
	public static SessionFactory factory;

	public static void initializeDB() {
		try {

			Configuration cfg = new AnnotationConfiguration().configure();

			cfg.setProperty("hibernate.hbm2ddl.auto", Main.prop.getProperty("hbm2ddl.auto"));
			cfg.setProperty("hibernate.dialect", Main.prop.getProperty("dialect"));
			cfg.setProperty("hibernate.connection.url", Main.prop.getProperty("connection.url"));
			cfg.setProperty("hibernate.connection.username", Main.prop.getProperty("connection.username"));
			cfg.setProperty("hibernate.connection.password", Main.prop.getProperty("connection.password"));
			cfg.setProperty("hibernate.connection.driver_class", Main.prop.getProperty("connection.driver_class"));
			cfg.setProperty("hibernate.show_sql", Main.prop.getProperty("show_sql"));
			cfg.setProperty("hibernate.show_sql", Main.prop.getProperty("show_sql"));
			factory = cfg.buildSessionFactory();

		} catch (Exception e) {

			logger.error("Unable to initialize databse connection, please check the config file (hibernamt.cfg.xml) "
					+ e.getMessage() + "," + e.getCause());

			System.exit(1);

		}
	}

	public static void loadDriverClass() {

		File driverJar = new File(Main.prop.getProperty("database_driver_jar"));
		if (driverJar.exists() && driverJar.getName().endsWith(".jar")) {
			try {

				URL myJarFile = new URL("jar", "", "file:" + driverJar.getAbsolutePath() + "!/");
				URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

				Class sysClass = URLClassLoader.class;
				Method sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
				sysMethod.setAccessible(true);
				sysMethod.invoke(sysLoader, new Object[] { myJarFile });

				Class.forName(Main.prop.getProperty("connection.driver_class"));

			} catch (ClassNotFoundException | MalformedURLException | NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("Unable to load driver jar " + e.getMessage());
				System.exit(1);
			}
		} else {
			logger.error("Invalid location of driver jar");
			System.exit(1);
		}
	}
}
