package edu.ucla.mbi.imex.central.struts.action;

/* =============================================================================
 * $HeadURL::                                                                  $
 * $Id::                                                                       $
 * Version: $Rev::                                                             $
 *==============================================================================
 *
 * StatsAction action
 *                
 ============================================================================ */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import java.util.*;

import edu.ucla.mbi.util.data.*;
import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.util.struts.action.*;
import edu.ucla.mbi.util.struts.interceptor.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;

public class StatsAction extends PortalSupport {
    
    private static final String JSON = "json";    
    
    //--------------------------------------------------------------------------
    //  TracContext
    //--------------

    private TracContext tracContext;

    public void setTracContext( TracContext context ) {
        this.tracContext = context;
    }

    public TracContext getTracContext() {
        return this.tracContext;
    }
    
    //--------------------------------------------------------------------------

    String partnerStr = null;

    public String getPartner() {
	return partnerStr;
    }

    public void setPartner( String partner ) {
	this.partnerStr = partner;
    }
    
    String query = null;
    
    public String getQuery() {
	return query;
    }

    public void setQuery( String query ) {
	this.query = query;
    }
    
    //--------------------------------------------------------------------------
    // format
    //-------

    private String format = null;

    public void setFormat( String format ) {
        this.format = format;
    }

    public String getFormat(){
        return this.format;
    }

    
    //--------------------------------------------------------------------------
    // results
    //--------
    
    
    public Map counts = null;
    
    public Map getCounts() {
        return counts;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public String execute() throws Exception {

        IcStatsDao dao = getTracContext().getIcStatsDao();

        counts = new HashMap();

        if( query != null && query.length() > 0 ) {
            
            if(query.toLowerCase().equals("bypartner") ) {
                Map<Group,Map<DataState,Long>> 
                    res = dao.getCountByPartner();

                Map<Group,Map<DataState,Long>>
                    accRes = dao.getAccCountByPartner();

                List rows = new ArrayList();
                counts.put("rows",rows);

                Map<DataState,Long> totals = new HashMap<DataState,Long>();
                Map<DataState,Long> accTotals = new HashMap<DataState,Long>();


                for( Iterator<Map.Entry<Group,Map<DataState,Long>>>
                         i = res.entrySet().iterator();  i.hasNext(); ) {
                    
                    Map.Entry<Group,Map<DataState,Long>> me = i.next();
                    
                    Map rowMap = new HashMap();
                    
                    rowMap.put( "label", me.getKey().getLabel() );                   
                    rowMap.put( "name", me.getKey().getName() );                   
                    
                    Map<DataState,Long> accCnt = null; 
                    if( accRes.get(me.getKey()) != null ){
                        accCnt = accRes.get(me.getKey());
                    }

                    rowMap.put( "states", 
                                statesToRow( me.getValue(), accCnt, totals,accTotals ) );
                    rows.add( rowMap );
                }
                
                // unreserved
                //-----------

                Map rowMap = new HashMap();
                rowMap.put( "label", "Unassigned" );
                rowMap.put( "name", "Unreserved" );
                rowMap.put( "states",
                            statesToRow( dao.getCountNoPartner(), 
                                         dao.getAccCountNoPartner(), totals, accTotals ) );
                rows.add( rowMap );


                // totals
                //-------

                Map trowMap = new HashMap();
                trowMap.put( "label", "Total" );
                trowMap.put( "name", "total counts" );
                trowMap.put( "states",
                             statesToRow( totals, accTotals, null, null ) );
                rows.add( trowMap );

            }
            
            if( query.toLowerCase().equals("all") ) {

                Map rowMap = new HashMap();
                counts.put( "0",  rowMap);
                rowMap.put( "name", "ALL" );
                rowMap.put( "states",                            
                            statesToRow( dao.getCountAll(), 
                                         dao.getAccCountAll(), null, null ) );
            }
            
            if(query.toLowerCase().equals("nopartner") ) {
                
                Map rowMap = new HashMap();
                counts.put( "0", rowMap);
                rowMap.put( "name", "NONE" );
                rowMap.put( "states", 
                            statesToRow( dao.getCountNoPartner(), 
                                         dao.getAccCountNoPartner(), 
                                         null, null ) );                
            }            
        }
        
        if( format != null && format.toUpperCase().equals("JSON") ) {
            return JSON;
        }
        
	return SUCCESS;
    }
    
    //--------------------------------------------------------------------------

    private Map statesToRow( Map<DataState,Long> stc, 
                             Map<DataState,Long> acc,
                             Map<DataState,Long> totals,
                             Map<DataState,Long> accTotals ) {
        
        Map res = new HashMap();

        long total = 0;
        long accTotal = 0;

        for( Iterator<Map.Entry<DataState,Long>> 
                 i = stc.entrySet().iterator();  i.hasNext(); ) {
            
            Map.Entry<DataState,Long> me = i.next();
            
            Map col = new HashMap();
            col.put("name",me.getKey().getName() );
            col.put("cnt",me.getValue() );
            
            if( acc != null && acc.get(me.getKey()) != null ){
                col.put("acc", acc.get(me.getKey()) );
                accTotal += acc.get( me.getKey() );
            }

            total += me.getValue().longValue();
            
            res.put( me.getKey().getId(), col );


            if( totals != null ) {
                if( totals.get( me.getKey() ) == null ) {
                    totals.put( me.getKey(), 0L);
                }
                 
                totals.put( me.getKey(), 
                            totals.get( me.getKey() ).longValue() 
                            + me.getValue().longValue() );                
            }
            
            if( accTotals != null ) {
                if( accTotals.get( me.getKey() ) == null ) {
                    accTotals.put( me.getKey(), 0L);
                }
                
                if( acc != null && acc.get(me.getKey()) != null ){
                    accTotals.put( me.getKey(), 
                                   accTotals.get( me.getKey() ).longValue() 
                                   + acc.get(me.getKey()).longValue() );                
                }
            }            
        }

        Map tcol = new HashMap();
        tcol.put( "name", "total" );
        tcol.put( "cnt", total );
        tcol.put( "acc", accTotal );
        res.put( "total",tcol );
        return res;
    }

}