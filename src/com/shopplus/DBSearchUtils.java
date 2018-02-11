package com.shopplus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DBSearchUtils {
	public static List<Session> allSesions = new ArrayList<>();

	final static Logger logger = Logger.getLogger(DBSearchUtils.class);

	private Session session = null;
	private Transaction t;

	public Session getSession() {

		if (session != null) {
			session.flush();
			t.rollback();
			session.close();
		}
		session = DBSessionFactory.factory.openSession();
		t = session.beginTransaction();
		allSesions.add(session);
		return session;
	}

	public DBSearchUtils() {

	}

}
