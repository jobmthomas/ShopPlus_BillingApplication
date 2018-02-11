package com.shopplus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DBUtils {

	public static List<Session> allSesions = new ArrayList<>();

	final static Logger logger = Logger.getLogger(DBUtils.class);

	private Session session = null;

	public Session getSession() {
		allSesions.add(session);
		return session;
	}

	public Transaction t;

	public DBUtils() {
		session = DBSessionFactory.factory.openSession();
	}

	public void beginTransaction() {
		try {
			session.close();
			session = DBSessionFactory.factory.openSession();

			t = session.beginTransaction();

		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(e.getCause().getMessage());

		}
	}

	public void endTransaction() {
		try {

			t.commit();
			t = null;

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void cancelTransaction() {
		try {
			if (t != null) {

				t.rollback();
				t = null;
				session.close();
				session = DBSessionFactory.factory.openSession();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());

		}
	}

}
