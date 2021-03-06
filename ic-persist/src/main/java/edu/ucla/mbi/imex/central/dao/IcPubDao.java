package edu.ucla.mbi.imex.central.dao;

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


       
public class IcPubDao extends AbstractDAO implements PublicationDao {

    //public IcPubDao(){ super(); }

    //public IcPubDao( SessionFactory sessionFactory ){
    //    super( sessionFactory );
    //}

    
    public Publication getPublication( int id ) { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: id(int)=" + id  );
        
        try{

            Publication pub = (IcPub) super.find( IcPub.class, new Integer( id ) );
            log.info( "IcPubDao->getPublication: id=" + id + " ::DONE"  );
            return pub;
        } catch( Exception ex ){
            return null;
        } 
    }
    
    //--------------------------------------------------------------------------

    public Publication getPublication( String title ) { 
        
        Publication pub = null;
        
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
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
    
    //--------------------------------------------------------------------------

    public Publication getPublicationByPmid( String pmid ) { 
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: pmid=" + pmid  );

        Publication pub = null;
        if ( pmid == null || pmid.equals("") ) return pub;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.pmid = :pmid ");
            query.setParameter("pmid", pmid );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }

    //--------------------------------------------------------------------------

    public Publication getPublicationByDoi( String doi ) { 
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: doi=" + doi  );

        Publication pub = null;
        if ( doi == null || doi.equals("") ) return pub;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.doi = :doi ");
            query.setParameter("doi", doi );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }

    //--------------------------------------------------------------------------

    public Publication getPublicationByJint( String jint ) { 
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao->getPublication: jint=" + jint  );

        Publication pub = null;
        if ( jint == null || jint.equals("") ) return pub;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        

        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.journalSpecific = :jint ");
            query.setParameter("jint", jint );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
    
    //--------------------------------------------------------------------------

    public Publication getPublicationByKey( String key ) { 
        
        Publication pub = null;
        if ( key == null || key.equals("") ) return pub;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            key = key.replaceAll( "\\D+", "" );

            long lkey = Long.parseLong(key);
            
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.icKey.value = :key ");
            query.setParameter("key", lkey );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
            
        } catch ( NumberFormatException nfe ) {
            // log error ?
        } catch ( HibernateException e ) {
            handleException( e );
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
    
    //--------------------------------------------------------------------------

    public List<Publication> getPublicationList() {
        
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList"  );
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p order by id ");
            
            plst = (List<Publication>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        
        //System.out.println("plist"+plst);

        return plst;
    }

    //--------------------------------------------------------------------------

    public List<Publication> getPublicationList( List<Integer> idl ){
        
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "IcPubDao:getPublicationList( idlist )" );
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {

            Query query = session
                .createQuery("select p.id, p from IcPub p where p.id in (:l)")
                .setParameter( "l", idl );
            
            Map<Integer,IcPub> rmap=new HashMap<Integer,IcPub>();

            for( Iterator i = query.iterate(); i.hasNext(); ){
                Object[] ir = (Object[]) i.next();
                rmap.put((Integer) ir[0], (IcPub) ir[1]);                
            }

            tx.commit();
            plst = new ArrayList<Publication>();
                
            for( Iterator<Integer> i = idl.iterator(); i.hasNext(); ){
                
                Publication ip = rmap.get( i.next() );
                if(  ip != null ){
                    plst.add( ip );                    
                }
            }

        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        log.debug( "plist: " + plst );

        return plst;
    }

    //--------------------------------------------------------------------------

    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize ) {
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList(block)"  );

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p order by id ");
            query.setFirstResult( firstRecord );
            query.setMaxResults( blockSize );
            
            plst = (List<Publication>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        } 
        
        //System.out.println("plist" + plst);
        return plst;
    }

    //--------------------------------------------------------------------------

    public List<Publication> getPublicationList( int firstRecord, 
                                                 int blockSize,
                                                 String skey,
                                                 boolean asc ) {
        List<Publication> plst = null;
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcPubDao:getPublicationList(block) sort=:" + skey );
               
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Criteria crit = session.createCriteria( IcPub.class );
            
            if( skey != null && skey.length() > 0 ){
                if( asc ){
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.asc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.asc( skey ) );
                    }
                }else{
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.desc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.desc( skey ) );
                    }
                }
            }

            if( firstRecord >= 0 && blockSize > 0 ){
                crit.setFirstResult( firstRecord );
                crit.setMaxResults( blockSize );
            }
            
            //Query query =
            //    session.createQuery( "from IcPub p order by id ");
            //query.setFirstResult( firstRecord );
            //query.setMaxResults( blockSize );
            
            //plst = (List<Publication>) query.list();

            plst = crit.list();
            
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log exception ?
            e.printStackTrace();
        } finally {
            //HibernateUtil.closeSession();
            session.close();                   
        }        
        return plst;
    }

    //---------------------- ******    -----------------------------------------

    public List<Publication> getPublicationList( int firstRecord,
                                                 int blockSize,
                                                 String skey, boolean asc,
                                                 Map<String,String> flt ){
        
        List<Publication> plst = new ArrayList<Publication>();
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "IcPubDao:getPublicationList(block) sort=:" + skey );
        log.debug( "IcPubDao:getPublicationList(block) filt(partner)=:" + 
                  flt.get("partner") );

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            
            Criteria crit = session.createCriteria( IcPub.class );

            if ( flt != null ) {
                crit = this.addFilter( crit, flt );
            }

            
            if (skey != null && skey.length() > 0 ) {
                if ( asc ) {
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.asc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.asc( skey ) );
                    }
                } else {
                    if( skey.equals( "imex" ) ){
                        crit.createAlias( "icKey", "imex" )
                            .addOrder( Order.desc( "imex.value" ) );
                    }else{
                        crit.addOrder( Order.desc( skey ) );
                    }
                }
            }

            
            crit.setFirstResult( firstRecord );
            crit.setMaxResults( blockSize );
            
            plst = crit.list();
            
            log.debug( "IcPubDao: plst=" + plst); 
            log.debug( "IcPubDao: size=" + plst.size()); 
            
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
            //HibernateUtil.closeSession();
            session.close();
        }
        
        return plst;
    }

    //--------------------------------------------------------------------------

    public long getPublicationCount() {

        long count = 0;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query = session.createQuery( "select count(p) from IcPub p" );
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

    public List<User> getOwners( String qstr ) {

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        List<User> ulst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct p.owner from IcPub p "
                              + " where upper( p.owner.login ) like :q "
                              + " order by p.owner.login" );
            query.setParameter("q", qstr );
            ulst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return ulst;
    }

    //--------------------------------------------------------------------------

    public List<User>  getAdminUsers( String qstr ){

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<User> ulst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct au from IcPub as p"
                              + " join p.adminUsers as au"
                              + " where upper( au.login ) like :q "
                              + " order by au.login" );
            query.setParameter("q", qstr );
            ulst = (List<User>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return ulst;
    }

    //--------------------------------------------------------------------------

    public List<Group>  getAdminGroups( String qstr ){

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<Group> glst = null;

        if( qstr!= null && qstr.toUpperCase().equals("ROLE:PARTNER" ) ){
            try {
                Query query = session
                    .createQuery( "select distinct gr from IcGroup as gr"
                                  + " join gr.roles as rl"
                                  + " where upper( rl.name ) like :q " 
                                  + " order by gr.name" );
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
        Group unassigned = new Group();
        unassigned.setLabel("Unassigned");
        glst.add( unassigned );
        return glst;
    }
    
    //--------------------------------------------------------------------------

    public List<DataState> getStages( String qstr ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<DataState> slst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct ds from IcDataStage as ds"
                              + " order by ds.name" );
            //query.setParameter( "q", qstr );
            slst = (List<DataState>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return slst;
    }
    //--------------------------------------------------------------------------

    public List<DataState> getStates( String qstr ){
        
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        List<DataState> slst = null;

        if( qstr!= null ){
            qstr = qstr.toUpperCase();
        }

        try {
            Query query = session
                .createQuery( "select distinct ds from IcDataState as ds"
                              + " order by ds.name" );
             //query.setParameter( "q", qstr );
            slst = (List<DataState>) query.list();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            e.printStackTrace();
        } catch( Exception ex ) {
            ex.printStackTrace();
        } finally {

            session.close();
        }

        return slst;
    }

    //--------------------------------------------------------------------------
    
    public long getPublicationCount(  Map<String,String> flt ) {

        long count = 0;

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Criteria crit = session.createCriteria( IcPub.class );
            
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

    public Publication savePublication( Publication publication ) { 

        Log log = LogFactory.getLog( this.getClass() );
        
        if ( publication  instanceof IcPub ) {
            log.info( "IcPub");
            log.info( ((IcPub) publication).getStage() );
            log.info( ((IcPub) publication).getState() );
           
            super.saveOrUpdate( (IcPub) publication );

            log.info( "IcPubDao->savePublication: id(pub)=" + publication.getId() );

            return publication;
        } else {
            log.info( "Publication");

            IcPub icp = new IcPub( publication);
            super.saveOrUpdate( icp );

            log.info( "IcPubDao->savePublication: id(icpub)=" + icp.getId() );

            return icp;
        }
    }
    
    //---------------------------------------------------------------------

    public Publication updatePublication( Publication publication ) { 
        
        super.saveOrUpdate( publication );

        
        return publication;
    }

    public Publication updatePublication( Publication publication, 
                                          User luser ) { 
        
        if ( publication  instanceof IcPub ) {

            IcPub pub = (IcPub) publication;
            pub.setActUser( luser ) ;
            pub.setActDate();
            pub.setModUser( luser ) ;
            pub.setModDate();

            super.saveOrUpdate( pub );
            
            return pub;
        }
        return null;
    }
    
    //---------------------------------------------------------------------

    public void deletePublication( Publication publication ) { 
        
        super.delete( publication );
    }
    
    
    //---------------------------------------------------------------------
    
    private  Criteria addFilter( Criteria crit, Map<String,String> flt ) {
        
        Log log = LogFactory.getLog( this.getClass() );
        if( flt.get("stage") != null && 
            !flt.get("stage").equals("") ){
            
            crit.createAlias( "stage", "sg" )
                .add( Restrictions.eq( "sg.name",
                                       flt.get( "stage" ) 
                                       ) );
        }

        if( flt.get("status") != null && 
            !flt.get("status").equals("") ){
            
            crit.createAlias( "state", "st" )
                .add( Restrictions.eq( "st.name",
                                       flt.get( "status" ) 
                                       ) );
        }
        
        if( flt.get("partner") != null && 
            !flt.get("partner").equals("") ){
            
            if( flt.get("partner").equals("Unassigned"))
            {
                crit.add( Restrictions.isEmpty( "adminGroups" ) );
            } 
            else
            {
                crit.createAlias( "adminGroups", "ag" )
                    .add( Restrictions.eq( "ag.label",
                                           flt.get( "partner" )
                                           ) );
            }
        }
        
        if( flt.get("editor") != null && 
            !flt.get("editor").equals("") ){
            crit.createAlias( "adminUsers", "au" )
                .add( Restrictions.eq( "au.login",
                                       flt.get( "editor" )
                                       ) );
        }


        if( flt.get( "owner" ) != null && 
            !flt.get("owner").equals("") ){
            crit.createAlias( "owner", "ow" )
                .add( Restrictions.eq( "ow.login",
                                       flt.get( "owner" )
                                       ) );
        }

        if( flt.get( "cflag" ) != null && 
            !flt.get("cflag").equals("") ){
                      
            DetachedCriteria critt = DetachedCriteria.forClass( IcComment.class );
                        
            critt.createAlias("icFlag","flg")
                .add(  Restrictions.eq( "flg.name", flt.get( "cflag" ) ) )
                .setProjection( Projections.property("root.id") );
            
            crit.add( Property.forName("id").in(critt) );
            
            log.info("Flag crt: " + flt.get( "cflag") );
        }


        if( flt.get( "nlmid" ) != null && 
            !flt.get("nlmid").equals("") ){
                      
            DetachedCriteria critt = DetachedCriteria.forClass( IcComment.class );
                    
            //xxxxxxxxxxxxxxxxx
            critt.createAlias("icFlag","flg")
                .add(  Restrictions.eq( "flg.name", flt.get( "cflag" ) ) )
                .setProjection( Projections.property("root.id") );
            
            crit.add( Property.forName("id").in(critt) );
            
            log.info("Flag crt: " + flt.get( "cflag") );
        }






        int jid = 1;
        try{
            jid = Integer.parseInt( flt.get( "jid" ) );
        }catch( Exception ex ){
            // shouldnt happen
        }
                 

        if( flt.get( "jid" ) != null &&
            !flt.get("jid").equals("") ){
            crit.createAlias( "source", "sr" )
                //.add( Restrictions.eq( "sr.id", flt.get( "jid" ) ) );
                .add( Restrictions.eq( "sr.id", jid ) );
        }

        if( flt.get( "year" ) != null &&
            !flt.get("year").equals("") ){
            crit.add( Restrictions.eq( "year", flt.get( "year" ) ) );
        }

        if( flt.get( "volume" ) != null &&
            !flt.get("volume").equals("") ){
            crit.add( Restrictions.eq( "volume", flt.get( "volume" ) ) );
        }

        if( flt.get( "issue" ) != null &&
            !flt.get("issue").equals("") ){
            crit.add( Restrictions.eq( "issue", flt.get( "issue" ) ) );
        }

        return crit;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public IcPub getPublicationByImexId( long imex ) { 
        
        IcPub pub = null;
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            Query query =
                session.createQuery( "from IcPub p where " +
                                     " p.icKey.value = :imex ");
            query.setParameter( "imex", imex );
            query.setFirstResult( 0 );
            pub = (IcPub) query.uniqueResult();
            tx.commit();
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return pub; 
    }
}
