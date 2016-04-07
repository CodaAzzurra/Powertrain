package com.datastax.demo.vehicle.containermanaged;

import com.datastax.demo.vehicle.SessionService;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.util.Optional;

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
    private static final String CONTACT_POINTS = "contactPoints";
    private String contactPoints;

    @Context
    private ServletContext context;
    private SessionService sessionService;

    /**
     * Use this constructor when you want to control the construction of the inner session service.
     */
    public ContextSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Use this constructor when relying solely on container injection.
     */
    public ContextSessionService() {
        this.sessionService = new SessionService(contactPoints);
    }

    private String resolveContactPoints() {
        contactPoints = context.getInitParameter(CONTACT_POINTS);
        if (contactPoints==null) {
            contactPoints = System.getProperty(CONTACT_POINTS,"127.0.0.1");
        }
        return contactPoints;
    }

    private Cluster getCluster() {
        Cluster cluster = (Cluster) context.getAttribute(CLUSTER);
        if (cluster == null) {
            synchronized (context) {
                cluster = (Cluster) context.getAttribute(CLUSTER);
                if (cluster == null) {
                    cluster = sessionService.getCluster();
                    context.setAttribute(CLUSTER, cluster);
                }
            }
        }

        logger.info("Cluster dispatched on thread [" + Thread.currentThread().getName() + "]:" + cluster.toString());

        return cluster;
    }

    public Session getSession() {
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

}
