package edu.ucla.mbi.imex.central.ws.server;

/* #============================================================================
 # $Id:: DipCachingImpl.java 317 2009-07-25 17:32:52Z lukasz                   $
 # Version: $Rev:: 317                                                         $
 #==============================================================================
 #
 # IcentralPortImpl - ImexCentral SOAP port implementation
 #
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.codec.binary.Base64;

import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.BindingProvider; 
import javax.xml.ws.Holder;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import javax.xml.ws.soap.Addressing;
import javax.xml.ws.handler.MessageContext; 

import java.util.*;
import javax.annotation.*;         

import edu.ucla.mbi.util.*;
import edu.ucla.mbi.util.dao.*;

import edu.ucla.mbi.util.data.*;
//import edu.ucla.mbi.util.data.dao.*;

import edu.ucla.mbi.imex.central.*;
import edu.ucla.mbi.imex.central.dao.*;
import edu.ucla.mbi.imex.central.ws.*;

@WebService(serviceName = "ImexCentralService", 
            portName = "ImexCentralPort", 
            endpointInterface = "edu.ucla.mbi.imex.central.ws.IcentralPort", 
            targetNamespace = "http://imex.mbi.ucla.edu/icentral/ws",
            wsdlLocation = "/WEB-INF/wsdl/icentral.wsdl") 

public class IcentralPortImpl implements IcentralPort {

    @Resource 
        WebServiceContext wsContext;
 
    //--------------------------------------------------------------------------
    // Entry Manager
    //--------------

    private EntryManager entryManager;

    public void setEntryManager( EntryManager manager ) {
        this.entryManager = manager;
    }

    public EntryManager getEntryManager() {
        return this.entryManager;
    }

    static DatatypeFactory dtf;
    static {
        try {
            dtf = DatatypeFactory.newInstance();
        } catch( DatatypeConfigurationException dce ) {
            // should not happen
        }
    }
    
    //--------------------------------------------------------------------------

    private long delay = 30L;

    public void setDelay( long delay ) {
        this.delay = delay;
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    static ObjectFactory of = new ObjectFactory();

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // IcentralPort interface
    //-----------------------

    public void 
        createPublication( Holder<edu.ucla.mbi.imex.central.ws.Publication> 
                           publication ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.Publication 
        createPublicationById( Identifier id ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        
        log.info( " login=" + c.getLogin() );
        log.info( " pass=" + c.getPass() );
        log.info( " identifier=" + id );
        
        log.info( " entry manager=" +  entryManager);
        log.info( " credentials test=" +  c.test() );
        
        if ( ! c.test() ) throw Fault.AUTH;
        
        String ns = id.getNs();
        String ac = id.getAc();
        
        if( ns == null || ac == null )  throw Fault.ID_MISSING;
        if( !ns.equals("pmid") ) throw Fault.ID_UNKNOWN;
        
        // test if already in
        //-------------------

        IcPub icPub = entryManager.getIcPubByPmid( ac );
        log.info( " icPub=" + icPub);

        if ( icPub == null ) {

            edu.ucla.mbi.util.data.Publication
                newPub = entryManager.getPubByPmid( ac );
            if( newPub != null ) {
                icPub = new IcPub( newPub );
            }
        }
        if ( icPub != null ) {
            log.info( " icPub=" + icPub + " ID=" + icPub.getId());
            if ( icPub.getId() == null ) {
                
                // ACL target control NOTE: implement as needed
                //---------------------------------------------
                /* if ( ownerMatch != null && ownerMatch.size() > 0 ) { } */
                
                User owner = entryManager.getUserContext()
                    .getUserDao().getUser( c.getLogin() );
                log.info( " owner set to: " + owner );
                
                DataState state =
                    entryManager.getWorkflowContext()
                    .getWorkflowDao().getDataState( "NEW" );
                log.info( " state set to: " + state );
                
                if ( state != null ) {
                    IcPub newPub = entryManager.addIcPub( icPub, owner, state );
                    if ( newPub != null ) {
                        icPub = newPub;
                    }
                }
            }
        } 
        
        if ( icPub != null ) {
            return buildPub( icPub );
        }
        throw Fault.NO_REC_CR;
    }
    
    //--------------------------------------------------------------------------
    
    public PublicationList getPublicationById( List<Identifier> idl )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getPublicationById" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if ( idl== null || idl.size() == 0 ) throw Fault.ID_MISSING;

        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();

        PublicationList pl = of.createPublicationList();
        for( Iterator<Identifier> idi = idl.iterator(); idi.hasNext(); ) {
            
            Identifier id = idi.next();
            
            String ns = id.getNs();
            String ac = id.getAc();
            
            log.info( " ns=" + ns + " ac=" + ac );

            boolean noid = true;
            IcPub icp = null;
            if( ns == null || ac == null ) throw Fault.ID_MISSING;

            if( ns.equals( "pmid" ) ) {
                icp = (IcPub) pubDao.getPublicationByPmid( ac );
                log.info( " icp=" + icp );
                noid = false;
            }
            
            if( ns.equals( "imex" ) ) {
                long lac = 0;
                
                try{
                    ac = ac.replaceFirst( "IM-", "" );
                    ac.replaceFirst( "-\\d$", "" );
                    lac = Long.parseLong( ac ); 
                } catch( Exception ex ) {
                    throw Fault.ID_UNKNOWN;
                }
                
                
                icp = (IcPub) pubDao.getPublicationByImexId( lac );
                log.info( " icp=" + icp );
                noid = false;
            }

            if( icp != null ) {
                pl.getPublication().add( buildPub( icp ) );
            }
                
            if( noid ) throw Fault.ID_UNKNOWN;

        }
        if( pl.getPublication().size() >  0 ) { 
            return pl;
        } else {
            throw Fault.NO_RECORD;
        }        
    }
    
    //--------------------------------------------------------------------------
    
    public PublicationList getPublicationByOwner( List<String> owner )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        

        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------
    
    public PublicationList getPublicationByStatus( List<String> status )
        throws IcentralFault { 

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        

        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.Publication 
        updatePublicationStatus( Identifier id, String status, String message )
        throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: updatePublicationStatus" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        if ( id == null ) throw Fault.ID_MISSING;
        
        String ns = id.getNs();
        String ac = id.getAc();
            
        if( ns == null || ac == null )  throw Fault.ID_MISSING;
        if( !ns.equals("pmid") ) throw Fault.ID_UNKNOWN;

        log.info( " ns=" + ns + " ac=" + ac );
       
        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();      
        IcPub icp = (IcPub) pubDao.getPublicationByPmid( ac );
        log.info( " icp=" + icp );
        
        if ( icp == null ) throw Fault.NO_RECORD;
        
        DataState state = entryManager.getWorkflowContext()
            .getWorkflowDao().getDataState( status );
        
        log.info( " state set to: " + state );
        if( state == null ) throw Fault.STAT_UNKNOWN;
        
        // update status
        //--------------
        
        IcPub icPub = entryManager.updateIcPubState( icp, state );
        return buildPub( icPub );
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.Publication 
        getPublicationImexAccession( Identifier id, 
                                     java.lang.Boolean create )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );
        
        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;

        if( id == null )  throw Fault.ID_MISSING;

        String ns = id.getNs();
        String ac = id.getAc();

        if( ns == null || ac == null )  throw Fault.ID_MISSING;
        if( !ns.equals("pmid") ) throw Fault.ID_UNKNOWN;
        
        log.info( " ns=" + ns + " ac=" + ac );

        IcPubDao pubDao = (IcPubDao) entryManager.getTracContext().getPubDao();
        IcPub icp = (IcPub) pubDao.getPublicationByPmid( ac );       
        if ( icp == null ) throw Fault.NO_RECORD;

        if( ( icp.getImexId() == null || icp.getImexId().equals("N/A") )
            && create ) {
            icp = entryManager.genIcPubImex( icp );
        } else {
            throw Fault.NO_IMEX;
        }
        return buildPub( icp );
    }

    //--------------------------------------------------------------------------

    public void getServerStatus( String depth, Holder<String> version, 
                                 Holder<String> status )
        throws IcentralFault {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl: getServerStatus" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) {
        
            try{
                Thread.sleep( delay*1000 );
            } catch ( InterruptedException ie) {
            }
            status.value = "";
        } else {
            status.value = "OK";
        }
        version.value = Icentral.WSVERSION;
    }
    
    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.Publication 
        updatePublicationAdminUser( Identifier identifier,
                                    String operation,
                                    String user) throws IcentralFault {

        edu.ucla.mbi.imex.central.ws.Publication retPub = null;


        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;


        if( operation != null && operation.toUpperCase().equals("ADD") ) {

            // get publication

            // get user

            // check authorisation

            // add user



            return retPub;
        }

        if( operation != null && operation.toUpperCase().equals("DROP") ) {

            // get publication

            // get user

            // check authorisation

            // add user

            return retPub;
        }
        
        throw Fault.UNSUP;
    }

    //--------------------------------------------------------------------------

    public edu.ucla.mbi.imex.central.ws.Publication 
        updatePublicationAdminGroup( Identifier identifier,
                                     String operation,
                                     String group ) throws IcentralFault {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "IcentralPortImpl:" );

        Credentials c = new Credentials( wsContext.getMessageContext() );
        if ( ! c.test() ) throw Fault.AUTH;
        
        throw Fault.UNSUP;
    }



    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // utilities
    //----------

    private edu.ucla.mbi.imex.central.ws.Publication buildPub( IcPub icp ) {
        
        edu.ucla.mbi.imex.central.ws.Publication 
            pub = of.createPublication();
        pub.setIdentifier( of.createIdentifier() );
        pub.getIdentifier().setNs( "pmid" );
        pub.getIdentifier().setAc( icp.getPmid() );
        
        pub.setAuthor( icp.getAuthor() );
        pub.setTitle( icp.getTitle() );
        pub.setPaperAbstract( icp.getAbstract() ) ;
        
        if( icp.getExpectedPubDate() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( icp.getExpectedPubDate() );
            } catch( Exception ex ) {
                //ignore
            } 
            if ( xmlDate != null ) {
                pub.setExpectedPublicationDate( xmlDate );
            }
        }
        
        if( icp.getPubDateStr() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( icp.getPubDate() );
            } catch( Exception ex ) {
                //ignore
            } 
            if ( xmlDate != null ) {
                pub.setPublicationDate( xmlDate );
            }
        }
        
        if( icp.getReleaseDateStr() != null ) {
            XMLGregorianCalendar xmlDate = null;
            try{
                xmlDate = dtf.
                    newXMLGregorianCalendar( icp.getReleaseDate() );
            } catch( Exception ex ) {
                //ignore
            }

            if ( xmlDate != null ) {
                pub.setReleaseDate( xmlDate );
            }
        }
        //pub.setCreationDate(icp);
        
        pub.setStatus( icp.getState().getName() );
        pub.setImexAccession( icp.getImexId() );
        pub.setOwner( icp.getOwner().getLogin() );

        return pub;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    class Credentials{
        
        String login = null;
        String pass = null;
        
        public String getLogin(){
            return login;
        }
        
        public String getPass(){
            return pass;
        }
        
        public Credentials( MessageContext context ) {
            
            try {
                Map requestHeaders =
                    (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS ) ;
                
                String b64str = (String) 
                    ((List) requestHeaders.get("Authorization")).get(0);            
                String lpString = 
                    new String( Base64.decodeBase64( b64str.substring(6) ) );
                
                login = lpString.substring( 0, lpString.indexOf( ":" ) );
                pass = lpString.substring( lpString.indexOf( ":" ) + 1 );
            } catch ( Exception e ) {
                // ignore: login/pass left at null
            }
        }

        public boolean test() {
            
            if ( entryManager == null ||  
                 entryManager.getUserContext() == null ) return false;
            
            UserDao dao = entryManager.getUserContext().getUserDao();
            if (dao == null ) return false;
            
            User user = dao.getUser( login );
            
            if ( user != null && pass != null ) {
                return user.testPassword( pass );
            } 
            return false;            
        }
    }
}
