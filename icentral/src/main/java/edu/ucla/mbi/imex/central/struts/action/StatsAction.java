package edu.ucla.mbi.imex.central.struts.action;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * StatsAction action
 *                
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import java.util.*;


import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.data.*;

import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.struts2.action.*;
import edu.ucla.mbi.util.struts2.interceptor.*;

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
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    
    public String execute() throws Exception {

        IcStatsDao dao = getTracContext().getIcStatsDao();

        counts = new HashMap();

        if( query != null && query.length() > 0 ) {
            
            if(query.toLowerCase().equals("bypartner") ) {
                Map<Group,Map<DataState,Long>> 
                    res = dao.getCountByPartner();

                List rows = new ArrayList();
                counts.put("rows",rows);

                Map<DataState,Long> totals = new HashMap<DataState,Long>();


                for( Iterator<Map.Entry<Group,Map<DataState,Long>>>
                         i = res.entrySet().iterator();  i.hasNext(); ) {
                    
                    Map.Entry<Group,Map<DataState,Long>> me = i.next();
                    
                    Map rowMap = new HashMap();
                    
                    rowMap.put( "label", me.getKey().getLabel() );                   
                    rowMap.put( "name", me.getKey().getName() );                   
                    rowMap.put( "states", statesToRow( me.getValue(), totals ) );
                    rows.add( rowMap );
                }

                // unreserved
                //-----------

                Map rowMap = new HashMap();
                rowMap.put( "label", "Unassigned" );
                rowMap.put( "name", "Unreserved" );
                rowMap.put( "states",
                            statesToRow( dao.getCountNoPartner(), totals ) );
                rows.add( rowMap );


                // totals
                //-------

                Map trowMap = new HashMap();
                trowMap.put( "label", "Total" );
                trowMap.put( "name", "total counts" );
                trowMap.put( "states",
                            statesToRow( totals, null ) );
                rows.add( trowMap );

            }
            
            if( query.toLowerCase().equals("all") ) {

                Map rowMap = new HashMap();
                counts.put( "0",  rowMap);
                rowMap.put( "name", "ALL" );
                rowMap.put( "states",                            
                            statesToRow( dao.getCountAll(), null ) );
            }
            
            if(query.toLowerCase().equals("nopartner") ) {
                
                Map rowMap = new HashMap();
                counts.put( "0", rowMap);
                rowMap.put( "name", "NONE" );
                rowMap.put( "states", 
                            statesToRow( dao.getCountNoPartner(), null ) );                
            }            
        }
        
        if( format != null && format.toUpperCase().equals("JSON") ) {
            return JSON;
        }
        
	return SUCCESS;
    }
    

    //---------------------------------------------------------------------


    private Map statesToRow( Map<DataState,Long> stc, 
                             Map<DataState,Long> totals ) {

        Map res = new HashMap();

        long total = 0;

        for( Iterator<Map.Entry<DataState,Long>> 
                 i = stc.entrySet().iterator();  i.hasNext(); ) {
            
            Map.Entry<DataState,Long> me = i.next();
            
            Map col = new HashMap();
            col.put("name",me.getKey().getName() );
            col.put("cnt",me.getValue() );
            
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
            
        }

        Map tcol = new HashMap();
        tcol.put( "name", "total" );
        tcol.put( "cnt", total );
        res.put( "total",tcol );
        return res;
    }



    //---------------------------------------------------------------------

    /*
    public void validate() {

	Log log = LogFactory.getLog( this.getClass() );
		    
	if( getSubmit() != null && getSubmit().length() > 0 ) {


	    String comment = getComment();
	    if ( comment != null ) {
		try {
		    comment = comment.replaceAll("^\\s+","");
		    comment = comment.replaceAll("\\s+$","");
		} catch ( Exception ex ) {
		    // cannot be here 
		}
		setComment( comment );
	    }

	    if ( comment == null || comment.length() == 0 ) {
		addFieldError( "comment",
			       "Comment field cannot be left empty" );
	    }
	    
	    if ( getSession().get( "DIP_USER_ID" ) != null &&
		 (Integer) getSession().get( "DIP_USER_ID" )  > 0 ) {
		
		
		
	    } else {
		
		// test recaptcha
		//---------------

		if( recaptcha != null ) {
		    
		    ReCaptchaResponse reCaptchaResponse = 
			recaptcha.checkAnswer( ServletActionContext.
					   getRequest().getRemoteHost(),  
					       rcf, rrf );  
		    
		    if ( !reCaptchaResponse.isValid() ) {  
			addActionError("Not a good CAPTCHA");
		    } else {
			
			log.info( "  recaptcha response=" + 
				  reCaptchaResponse.getErrorMessage() );
		    }
		}
	    }
	}
    }

    */

}