package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcAdiDao:
 *
 *=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;

import edu.ucla.mbi.orm.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class IcObsMgrDao extends AbstractDAO implements ObsMgrDao {

    public List<User> getObserverList( DataItem subject ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcObsMgrDao->getObserverList id=" + subject  );
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<User> olst = null;

        try {
            Query query =
                session.createQuery( "select sor.observer "
                                     + " from SORel sor where "
                                     + " sor.subject = :subject " );

            query.setParameter( "subject", subject );
            
            olst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            log.info(e);
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        System.out.println("olist"+olst);

        return olst;
    }
    
    public List<DataItem> getSubjectList( User observer ){
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcObsMgrDao->getSubjectList id=" + observer  );

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        List<DataItem> slst = null;

        try {
            Query query =
                session.createQuery( "select sor.subject "
                                     + " from SORel sor where "
                                     + " sor.observer = :observer " );

            query.setParameter( "observer", observer );
            
            slst = (List<DataItem>) query.list();
            tx.commit();
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        System.out.println( "slist" + slst);
        
        return slst;
    }

    public void addSORel( DataItem subject, User observer ){
        Log log = LogFactory.getLog( this.getClass() );
        log.info('\n' + "got to addSORel with subject = " + subject + " and  observer = " + observer);
        SORel newRelationship = new SORel( subject, observer);
        log.info(newRelationship);
        super.saveOrUpdate(newRelationship);
    }

    public void dropSORel( DataItem subject, User observer ){
        Log log = LogFactory.getLog( this.getClass() );
        log.info('\n' + "got to dropSOrel with subject = " + subject + " and  observer = " + observer);
        SORel dropRelationship = new SORel( subject, observer);
        log.info(dropRelationship);
        super.delete(dropRelationship);

    }

}
