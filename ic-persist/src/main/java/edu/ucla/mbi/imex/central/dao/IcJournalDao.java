package edu.ucla.mbi.imex.central.dao;

/*===========================================================================
 * $HeadURL::                                                               $
 * $Id::                                                                    $
 * Version: $Rev::                                                          $
 *===========================================================================
 *
 * IcJournalDAO:
 *
 *========================================================================= */

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

public class IcJournalDao extends AbstractDAO implements JournalVIPDao {
    

    //public IcJournalDao(){ super(); }

    //public IcJournalDao( SessionFactory sessionFactory ){
    //    super( sessionFactory );
    //}

    public Journal getJournal( int id ) { 
        
        Journal journal = (IcJournal) super.find( IcJournal.class, id );
        return journal; 
    }
    

    //---------------------------------------------------------------------

    public Journal getJournalByNlmid( String nlmid ) { 
        
        Journal journal = null;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();

        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();


        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.nlmid = :nlmid ");
            query.setParameter( "nlmid", nlmid );
            query.setFirstResult( 0 );
            journal = (IcJournal) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return journal; 
    }

    //---------------------------------------------------------------------

    public Journal getJournal( String title ) { 
        
        Journal journal = null;

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcJournal j where " +
                                     " j.title = :title ");
            query.setParameter("title", title );
            query.setFirstResult( 0 );
            journal = (IcJournal) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return journal; 
    }

    //--------------------------------------------------------------------------

    public List<Journal> getJournalList() {
        
        List<Journal> jlst = null;

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query = session
                .createQuery( "from IcJournal j where id > 0 order by id" );
            
            jlst = (List<Journal>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return jlst;
    }

    //--------------------------------------------------------------------------

    public long getJournalCount() {
        
        long count = 0;
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query = session
                .createQuery( "select count(j) from IcJournal j where id > 0" );
            count  = (Long) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return count;
    }


    //--------------------------------------------------------------------------
    
    public long getJournalCount(  Map<String,String> flt ) {

        long count = 0;

        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Criteria crit = session.createCriteria( IcJournal.class );
            
            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }
            
            List foo = crit.setProjection( Projections.rowCount()).list();
            count  = ((Long) foo.get(0) ).longValue() ;

            Log log = LogFactory.getLog( this.getClass() );
            log.debug( "count=" + count );
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        return count;
    }
    
    //--------------------------------------------------------------------------

    
    public Map<Journal,List> getJournalListStats( List<Journal> jlist ){
        
        Map<Journal,List> result = new HashMap<Journal,List>(); 
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List rlst = null;
        
        try {
            Query query = session
                .createQuery( "select e.stage.id, e.state.id, e.source.id, count( e )"+
                              " from IcPub as e where e.source in (:jl)" +
                              " group by e.stage.id, e.state.id, e.source.id");
            
            query.setParameterList( "jl", jlist );
            
            rlst =query.list();            

        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

        if( rlst != null ){
            for( Iterator i = rlst.iterator(); i.hasNext(); ){
                Object[] r = (Object[]) i.next();
                Map<String,Object> rm = new HashMap<String,Object>();

                Session sessionA = getCurrentSession();
                Transaction txA = sessionA.beginTransaction();
                
                try {
                    Query queryA = sessionA
                        .createQuery( "select s from IcDataStage as s where s.id = :id");
                    queryA.setParameter( "id", (Integer) r[0] );
                    
                    rm.put( "stage", queryA.uniqueResult());
                } catch ( HibernateException e ) {
                    handleException( e );
                    // log error ?
                } finally {
                    //HibernateUtil.closeSession();
                    sessionA.close();
                }

                Session sessionB = getCurrentSession();
                Transaction txB = sessionB.beginTransaction();
                
                try {
                    Query queryB = sessionB
                        .createQuery( "select s from IcDataState as s where s.id = :id");
                    queryB.setParameter( "id", (Integer) r[1] );
                    
                    rm.put( "state", queryB.uniqueResult());
                } catch ( HibernateException e ) {
                    handleException( e );
                    // log error ?
                } finally {
                    //HibernateUtil.closeSession();
                    sessionB.close();
                }
                
                rm.put( "count", r[3]);

                Session sessionC = getCurrentSession();
                Transaction txC = sessionC.beginTransaction();

                Journal j = null;

                try {
                    Query queryC = sessionC
                        .createQuery( "select j from IcJournal as j where j.id = :id");
                    queryC.setParameter( "id", (Integer) r[2] );
                    
                    j = (Journal) queryC.uniqueResult();
                    
                } catch ( HibernateException e ) {
                    handleException( e );
                    // log error ?
                } finally {
                    //HibernateUtil.closeSession();
                    sessionC.close();
                }
                
                if( j != null && result.get( j ) == null ){
                    result.put( j, new ArrayList() );
                }
                result.get( j ).add( rm );
            }        
        }

        return result;
    }

    //--------------------------------------------------------------------------

    public List<Journal> getJournalList( int firstRecord,
                                         int blockSize ) {
        List<Journal> jlst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcJournalDao:getJournalList(block)"  );

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query = session
                .createQuery( "from IcJournal p where id > 0 order by id ");
            query.setFirstResult( firstRecord );
            query.setMaxResults( blockSize );

            jlst = (List<Journal>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        System.out.println("jlist" + jlst);
        return jlst;
    }

    //--------------------------------------------------------------------------

    public List<Journal> getJournalList( int firstRecord,
                                         int blockSize,
                                         String skey,
                                         boolean asc ) {
       
        List<Journal> jlst = new ArrayList<Journal>();
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcJournaaDao:getJournalList(block) sort=:" + skey );

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            Criteria crit = session.createCriteria( IcJournal.class );
            
            if( firstRecord >= 0 && blockSize > 0 ){
                crit.setFirstResult( firstRecord );
                crit.setMaxResults( blockSize );
            }

            if( skey != null && skey.length() > 0 ){
                if( asc ){
                    crit.addOrder( Order.asc( skey ) );
                }else{
                    crit.addOrder( Order.desc( skey ) );
                }
            }
            
            jlst = crit.list();

            tx.commit();

        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return jlst;
    }

    
    public List<Journal> getJournalList( int firstRecord,
                                         int blockSize,
                                         String skey, boolean asc,
                                         Map<String,String> flt ){
        
        List<Journal> jlst = new ArrayList<Journal>();

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "IcJournalDao:getJournalList(block) sort=:" + skey );
        log.debug( "IcJournalDao:getJournalList(block) filt(partner)=:" +
                   flt.get("partner") );

        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {

            Criteria crit = session.createCriteria( IcJournal.class );
            
            if (skey != null && skey.length() > 0 ) {
                if ( asc ) {
                    crit.addOrder( Order.asc( skey ) );
                } else {
                    crit.addOrder( Order.desc( skey ) );
                }
            }

            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }

            crit.setFirstResult( firstRecord );
            crit.setMaxResults( blockSize );
            
            jlst = crit.list();

            log.debug( "IcPubDao: plst=" + jlst);
            log.debug( "IcPubDao: size=" + jlst.size());
            
            tx.commit();

        } catch ( HibernateException e ) {
            e.printStackTrace();
            handleException( e );
            
            // log exception ?
            e.printStackTrace();

        } catch( Exception ex ) {
            // log exception ?
            ex.printStackTrace();
        } finally {
            session.close();
        }
        
        return jlst;
        
    }



    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    public void saveJournal( Journal journal ) { 
                  
        if ( journal instanceof IcJournal ) {
            super.saveOrUpdate( journal );
        } else {
            super.saveOrUpdate( new IcJournal( journal ) );
        }
    }
    

    //-------------------------------------------------------------------------
    
    public void updateJournal( Journal journal ) { 
        super.saveOrUpdate( journal );
    }
    
    
    //-------------------------------------------------------------------------

    public void deleteJournal( Journal journal ) {    
        super.delete( journal );
    }

    //-------------------------------------------------------------------------
    
    public String getJournalYear( Journal journal, boolean first ){
       
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        String year = "";
        
        String queryStr = first ?
            "select min(p.year) from IcPub as p where p.source = :jo"
            :
            "select max(p.year) from IcPub as p where p.source = :jo";
        

        try {
            Query query = session.createQuery( queryStr );
            query.setParameter( "jo", journal );                
            year  = (String) query.uniqueResult();
            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return year;
    }

    public List<String> getJournalYearList( Journal journal, boolean first ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<String>  result =  new ArrayList<String>();
        
        String queryStr = first ?
            "select distinct p.year, length(p.year) from IcPub as p where p.source = :jo " +
            " order by length(p.year),p.year asc"
            :
            "select distinct p.year, length(p.year) from IcPub as p where p.source = :jo " +
            " order by length(p.year),p.year desc";

        try {
            Query query = session.createQuery( queryStr );
            query.setParameter( "jo", journal );
            
            List resList = query.list();
            for(Iterator rl = resList.iterator(); rl.hasNext();){
                Object[] rr =(Object[]) rl.next();
                if( ((String) rr[0]).length() > 0){
                    result.add((String) rr[0]);
                }
            }
            tx.commit();
            
        } catch ( HibernateException e ) {
            
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return result;
    }

    //-------------------------------------------------------------------------

    public String getJournalVolume( Journal journal, boolean first,
                                    String year ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        String volume= "";
        String queryStr = first ? 
            "select min(p.volume) from IcPub as p where p.source = :jo" 
            + " and year= :yr" 
            :
            "select max(p.volume) from IcPub as p where p.source = :jo"
            + " and year= :yr";
        
        try {
            Query query = session.createQuery( queryStr);
            query.setParameter( "jo", journal );
            query.setParameter( "yr", year );                
            volume  = (String) query.uniqueResult();
            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return volume;
    }
    
    public List<String> getJournalVolumeList( Journal journal, boolean first,
                                              String year ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<String> result = new ArrayList<String>();
        
        String queryStr = first ?
            "select distinct p.volume, length(p.volume) from IcPub as p where p.source = :jo"
            + " and year= :yr order by length(p.volume),p.volume asc"
            :        
            "select distinct p.volume, length(p.volume) from IcPub as p where p.source = :jo"
            + " and year= :yr  order by length(p.volume),p.volume desc";

        try {
            Query query = session.createQuery( queryStr);
            query.setParameter( "jo", journal );
            query.setParameter( "yr", year );
            
            List resList = query.list();
            
            for(Iterator rl = resList.iterator(); rl.hasNext();){
                Object[] rr =(Object[]) rl.next();
                if( ((String) rr[0]).length() > 0){
                    result.add((String) rr[0]);
                }
            }
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return result;
    }

    //-------------------------------------------------------------------------

    public String getJournalIssue( Journal journal, boolean first,
                                   String year, String volume ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        String issue= "";

        String queryStr = first ? 
            "select min(p.issue) from IcPub as p where p.source = :jo" 
            + " and year= :yr and volume = :vo" 
            :
            "select max(p.issue) from IcPub as p where p.source = :jo"
            + " and year= :yr and volume = :vo";
        
        try {
            Query query = session.createQuery( queryStr );
            query.setParameter( "jo", journal );
            query.setParameter( "yr", year );
            query.setParameter( "vo", volume );
                
            issue = (String) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return issue;
    }
    
    public List<String> getJournalIssueList( Journal journal, boolean first,
                                             String year, String volume  ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        List<String> result = new ArrayList<String>();

        String queryStr = first ? 
            "select distinct p.issue,length(p.issue) from IcPub as p where p.source = :jo" 
            + " and year= :yr and volume = :vo order by length(p.issue),p.issue asc" 
            :
            "select distinct p.issue,length(p.issue) from IcPub as p where p.source = :jo"
            + " and year= :yr and volume = :vo order by length(p.issue),p.issue desc";
        
        try {
            Query query = session.createQuery( queryStr );
            query.setParameter( "jo", journal );
            query.setParameter( "yr", year );
            query.setParameter( "vo", volume );
            
            List resList = query.list();
            for(Iterator rl = resList.iterator(); rl.hasNext();){
                Object[] rr =(Object[]) rl.next();
                if( ((String) rr[0]).length() > 0){
                    result.add((String) rr[0]);
                }
            }
            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return result;
    }

    
    //--------------------------------------------------------------------------

    public List<Group>  getAdminGroups( String qstr ){

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<Group> glst = null;

        if( qstr!= null && qstr.toUpperCase().equals( "ROLE:PARTNER" ) ){
            try {
                
                Query query = session
                    .createQuery( "select distinct gr from IcGroup as gr"
                                  + " join gr.roles as rl"
                                  + " where upper( rl.name ) like :q and "
                                  + "  gr in (select distinct gl"
                                  + "           from IcJournal as j"
                                  + "             join j.adminGroups as gl)"
                                  );

                query.setParameter("q", "%PARTNER%" );
                glst = (List<Group>) query.list();
                tx.commit();
            } catch ( HibernateException e ) {
                handleException( e );
                e.printStackTrace();
            } catch( Exception ex ) {
                ex.printStackTrace();
            } finally {
                session.close();
            }
        } else {
            try {
                Query query = session
                    .createQuery( "select distinct gr from IcGroup as gr"
                                  + " order by gr.name" );
                //query.setParameter("q", qstr );
                glst = (List<Group>) query.list();
                tx.commit();
            } catch ( HibernateException e ) {
                handleException( e );
                e.printStackTrace();
            } catch( Exception ex ) {
                ex.printStackTrace();
            } finally {
                session.close();
            }
        }
        return glst;
    }

    //--------------------------------------------------------------------------
    //---------------------------------------------------------------------
    
    private  Criteria addFilter( Criteria crit, Map<String,String> flt ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        
        if( flt.get("partner") != null ){ 
            if( flt.get("partner").equalsIgnoreCase("Unassigned") 
                || flt.get("partner").equalsIgnoreCase("IMEX")  ){
                
                if( flt.get("partner").equalsIgnoreCase("Unassigned") ){
                    crit.add( Restrictions.isEmpty( "adminGroups" ) );
                } 
            
                if( flt.get("partner").equalsIgnoreCase("IMEX") ){
                    crit.createAlias( "adminGroups","ag")
                        .createCriteria("ag.roles")
                        .add( Restrictions.eq( "name","IMEx partner") );  
                } 
            } else {
                crit.createAlias( "adminGroups", "ag" )
                    .add( Restrictions.eq( "ag.label",
                                           flt.get( "partner" )
                                           ) );
            }
        }
        return crit;
    }
}

