package edu.ucla.mbi.imex.central.dao;

/*==============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * IcStatsDao: entry statistics 
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
       
public class IcStatsDao extends AbstractDAO {

    //public IcStatsDao(){ super();}

    //public IcStatsDao( SessionFactory sessionFactory ){
    //    super( sessionFactory );
    //}

    public static final Integer PARTNERID = new Integer( 15 ); // set as bean param


    public List<Object> getJournalYVCounts( Journal jrnl, 
					    String year, String volume,
					    int firstRecord,
					    int blockSize ){

	List<Object> cntall = getJournalYVCounts( jrnl, year, volume );

	// note: paging based on unique issue id count
	//--------------------------------------------
	
	// make list of issue IDs

	// 
	
	return null;
    }
    
    public List<Object> getJournalYVCounts( Journal jrnl, 
					    String year, String volume ){

        String qStr = "select distinct p.issue, p.stage.id, p.state.id, " +
	    " count( distinct p ) " + 
            " from IcPub p where p.source.id = :jid" +
	    " and  p.year = :yr and p.volume = :vo " +
	    " group by p.issue, p.stage.id, p.state.id"
	    + " order by p.issue";
                
	Log log = LogFactory.getLog( this.getClass() );
	log.info( "getJournalYVCounts: :" + year + ": :" + volume + ":");
	log.info(jrnl.getId());

        List<Object> clist = new ArrayList<Object>();
	
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {            
            Query query = session.createQuery( qStr );

	    query.setParameter( "jid", jrnl.getId() );
	    query.setParameter( "yr", year );
	    query.setParameter( "vo", volume );
	    
            List  res = query.list();
            log.info("res: " + res.size());
            if( res.size()>0 ) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {

                    Object[] ir = (Object[]) i.next();
                    
		    String issue = (String) ir[0];
                    Integer stageId = (Integer) ir[1];
                    Integer stateId = (Integer) ir[2];
                    Long count = (Long) ir[3]; 
                    
                    IcDataStage stage = (IcDataStage) 
                        super.find( IcDataStage.class, stageId );
                    
                    IcDataState state = (IcDataState) 
                        super.find( IcDataState.class, stateId );
                    
		    //log.info( "I: "+ issue +" StgID: " + stageId + " SteID: " + stateId 
		    //          + " cnt: " + count + " issue: " + issue);

		    List<Object> cnt = new ArrayList<Object>();
                    
		    String vissue = issue.replaceAll( "\\D", "" );
		    int iissue = 0;
		    if( vissue.length() >0 ){
			iissue = Integer.parseInt( vissue );
		    }
		    
		    cnt.add( issue);
		    cnt.add( stage.getName());
                    cnt.add( state.getName());
                    cnt.add( count);
                    cnt.add( iissue );
		    
		    if( clist.size() == 0 ){
			clist.add( cnt );
		    } else{
			if( iissue >= (int) ((List<Object>)clist.get( clist.size()-1 )).get(4) ){
			    clist.add( cnt ); // as last
			} else{
			    if( iissue <= (int) ((List<Object>) clist.get(0)).get(4) ){
				clist.add( 0, cnt ); // as first
			    } else {
				int min = 0;
				int max = clist.size()-1;
				//log.info(" min:" + min + " max:" + max );

				int turns = 20; 
				while( min < max  && turns >0){
				    turns--;
				    int mid = ( min+max ) / 2;
				    int miss = (int) ((List<Object>) clist.get(mid)).get(4);
				    //log.info("   mid:" + mid + " mis:" + miss);
				    if( iissue <= miss ){
					max = mid;
				    } else {
					min = mid;
				    }

				    if( max-min == 1){
					int imin =  (int) ((List<Object>) clist.get(min)).get(4);
					int imax =  (int) ((List<Object>) clist.get(max)).get(4);
					if( iissue >= imin && iissue<= imax){
					    min=max;
					}
				        //log.info("    min:" + min + " imin:" + imin + " max:" + max + " imax:" + imax + " IIS: " + iissue);
				    }
				    
				    //log.info("   min:" + min + " max:" + max + " mid:" + mid + " mis:" + miss);
				}
				//log.info("   adding at:" + min + " total(pre): "+ clist.size());
				clist.add( min, cnt ); // at min
				//log.info("   added at:" + min + " total(post): "+ clist.size());
				for( Iterator c = clist.iterator(); c.hasNext(); ) {
				    List<Object> cc = (List<Object>)c.next();
				    //log.info("       issue="+ cc.get(4));
				}
			    }
			}
		    }		   
                }

            } else {               
                log.info( "IcStatsDao(getCountAll): no counts"  );
            }
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }

	return clist;

    }

    public Map<DataState,Long> getCountAll() { 
        
        String qStr = "select p.state.id, count( distinct p ) " + 
            " from IcPub p group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();
            
            if( res.size()>0 ) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer stateId = (Integer) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put( state, cnt );
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountAll): no counts"  );
            }
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }

    //--------------------------------------------------------------------------

    public Map<DataState,Long> getAccCountAll() { 
        
        String qStr = "select p.state.id, count( distinct p ) " + 
            " from IcPub p where not(p.icKey is null) group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();

        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();
            
            if( res.size()>0 ) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Long stateId = (Long) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put( state, cnt );
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountAll): no counts"  );
            }
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }

    //--------------------------------------------------------------------------
    
    public Map<Group,Map<DataState,Long>> 
        getCountByPartner( String pipeline ){
        
        return null;
    }

    public Map<Group,Map<DataState,Long>> 
        getAccCountByPartner( String pipeline ){

        
        return null;
    }
    
    //--------------------------------------------------------------------------

    public Map<Group,Map<DataState,Long>> getAccCountByPartner() { 
        
        String qStr = "select grp.id, p.state.id, count( distinct p ) " + 
            " from IcPub p join p.adminGroups grp join grp.roles role " +
            "  where not(p.icKey is null) and role.id= :pid " +
            "  group by grp.id, p.state.id";
        
        Log log = LogFactory.getLog( this.getClass() );
        
        Map<Group,Map<DataState,Long>> 
            resmap = new HashMap<Group,Map<DataState,Long>>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );

            log.info( "pid" + IcStatsDao.PARTNERID);
            
            query.setParameter( "pid", IcStatsDao.PARTNERID );
            List  res = query.list();
            log.info( "res=" + res);
            if( res.size()>0 ){
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer partnerId = (Integer) ir[0]; 
                    Integer stateId = (Integer) ir[1];
                    Long cnt = (Long) ir[2]; 
                    
                    Group partner = (IcGroup) 
                        super.find( IcGroup.class, partnerId );
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    if( partner != null ) {

                        if( resmap.get(partner) == null ) {
                            Map<DataState,Long> 
                                pst = new HashMap<DataState,Long>();
                            resmap.put( partner, pst );
                        }
                        resmap.get(partner).put(state,cnt);
                    }
                }
            } else {
                
                log.info( "IcStatsDao(getCountByPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }

        return resmap;
    }
    
   //--------------------------------------------------------------------------
    
    public Map<Group,Map<DataState,Long>> getCountByPartner() { 
        
        String qStr = "select grp.id, p.state.id, count( distinct p ) " + 
            " from IcPub p join p.adminGroups grp join grp.roles role " +
            "  where role.id= :pid group by grp.id, p.state.id";
        
        Map<Group,Map<DataState,Long>> 
            resmap = new HashMap<Group,Map<DataState,Long>>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
      
            Query query = session.createQuery( qStr );
            query.setParameter( "pid", IcStatsDao.PARTNERID );

            //XX 
            List  res = query.list();
            
            if(res.size()>0) {
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer partnerId = (Integer) ir[0]; 
                    Integer stateId = (Integer) ir[1];
                    Long cnt = (Long) ir[2]; 
                    
                    Group partner = (IcGroup) 
                        super.find( IcGroup.class, partnerId );
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    if( partner != null ) {

                        if( resmap.get(partner) == null ) {
                            Map<DataState,Long> 
                                pst = new HashMap<DataState,Long>();
                            resmap.put( partner, pst );
                        }
                        resmap.get(partner).put(state,cnt);
                    }
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountByPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }

        return resmap;
    }
    
    //--------------------------------------------------------------------------
    
    public Map<DataState,Long> getCountNoPartner() { 

        String qStr = "select p.state.id, count(distinct p) " +
            " from IcPub p where p.adminGroups.size = 0 " +
            " group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();

            if(res.size()>0) {
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer stateId = (Integer) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put(state,cnt);
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountNoPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }
    
    //--------------------------------------------------------------------------
    
    public Map<DataState,Long> getAccCountNoPartner() { 

        String qStr = "select p.state.id, count(distinct p) from IcPub p " +
            " where not(p.icKey is null) and p.adminGroups.size = 0 " +
            " group by p.state.id";
        
        Map<DataState,Long> resmap = new HashMap<DataState,Long>();
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            
            Query query = session.createQuery( qStr );
            List  res = query.list();

            if(res.size()>0) {
                
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    
                    Integer stateId = (Integer) ir[0];
                    Long cnt = (Long) ir[1]; 
                    
                    DataState state = (DataState) 
                        super.find( IcDataState.class, stateId );
                    
                    resmap.put(state,cnt);
                }
            } else {
                Log log = LogFactory.getLog( this.getClass() );
                log.info( "IcStatsDao(getCountNoPartner): no counts" );
            }
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //System.out.println("Session closed (exeption)");
            //HibernateUtil.closeSession();
            session.close();
        }
        return resmap;
    }
    
    //--------------------------------------------------------------------------
    
    public void testQuery( String testQuery ) { 
        
        //Session session =
        //    HibernateUtil.getSessionFactory().openSession();
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();

        try {
            //startOperation();
            Query query =
                session.createQuery( testQuery );
            
            List  res = query.list();

            if(res.size()>0) {
                for( Iterator i = res.iterator(); i.hasNext(); ) {
                    Object[] ir = (Object[])i.next();
                    for(int j = 0; j < ir.length; j++ ) {
                        System.out.print(" " + ir[j] );
                    }
                    System.out.println("");
                }
                System.out.println("Size: " + res.size() + "res(0)=" + res.get(0) );
            } else {
                System.out.println("M/T");
            }
            tx.commit();
            System.out.println("Session closed normally");
            
        } catch ( HibernateException e ) {
            handleException( e );
            // log error ?
        } finally {
            //HibernateUtil.closeSession();
            session.close();
        }
    }

    //--------------------------------------------------------------------------
    /*
    public Publication getPublication( String title ) { 
        
        Publication pub = null;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
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
    */
}
