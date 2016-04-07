package com.datastax.demo.vehicle.webservice;

import com.datastax.demo.vehicle.SessionService;
import com.datastax.demo.vehicle.VehicleDao;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * This is a variant of SessionService which stores its Session objects in the webapp context.
 * It uses a SessionService when needed to get instances.
 * <p>
 * Normally this type of logic is contained in a separate layer from the app. Injection for this class is
 * managed by the Jersey container.
 */
public class ContextSessionService {
    private final static Logger logger = LoggerFactory.getLogger(ContextSessionService.class);
    private final static String CLUSTER = "cluster";
    private final static String SESSION = "session";
    private final static String VEHICLE_DAO = "vehicle-dao";
    private static final String CONTACT_POINTS = "contactPoints";
    private String contactPoints;

    private ServletContext context;
    private SessionService sessionService;

    public ContextSessionService(ServletContext context) {
        this.context = context;
    }

    private SessionService getSessionService() {
        if (sessionService == null) {
            sessionService = new SessionService(resolveContactPoints());
        }
        return sessionService;
    }

    private String resolveContactPoints() {
        contactPoints = context.getInitParameter(CONTACT_POINTS);
        if (contactPoints == null) {
            contactPoints = System.getProperty(CONTACT_POINTS, "127.0.0.1");
        }
        return contactPoints;
    }

    private Cluster getCluster() {
        Cluster cluster = (Cluster) context.getAttribute(CLUSTER);
        if (cluster == null) {
            synchronized (context) {
                cluster = (Cluster) context.getAttribute(CLUSTER);
                if (cluster == null) {
                    cluster = getSessionService().getCluster();
                    context.setAttribute(CLUSTER, cluster);
                }
            }
        }

        logger.info("Cluster dispatched on thread [" + Thread.currentThread().getName() + "]:" + cluster.toString());

        return cluster;
    }

    private Session getSession() {
        Session session = (Session) context.getAttribute(SESSION);
        if (session == null) {
            synchronized (context) {
                session = (Session) context.getAttribute(SESSION);
                if (session == null) {
                    session = getCluster().newSession();
                    context.setAttribute(SESSION, session);
                }
            }
        }

        logger.info("Session dispatched on thread [" + Thread.currentThread().getName() + "]:" + session.toString());

        return session;
    }

    public VehicleDao getDAO() {
        VehicleDao dao = (VehicleDao) context.getAttribute(VEHICLE_DAO);
        if (dao == null) {
            synchronized (context) {
                dao = (VehicleDao) context.getAttribute(VEHICLE_DAO);
                if (dao == null) {
                    dao = new VehicleDao(getSession());
                    context.setAttribute(VEHICLE_DAO, dao);
                }
            }
        }

        logger.info("DAO dispatched on thread [" + Thread.currentThread().getName() + "]:" + dao.toString());

        return dao;
    }
}
