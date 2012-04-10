package org.hupo.psi.mi.psq.client;
        
/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # psqclient - command line PSICQUIC client
 #
 #=========================================================================== */

import java.util.*;

import org.hupo.psi.mi.psq.*;

import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;

import javax.xml.namespace.QName;
import java.net.URL;

import org.apache.commons.cli.*;

//import edu.ucla.mbi.imex.central.ws.*;

public class psqclient {
    
    static PsqService service;
    static PsqPort port;
    static String endpoint = "http://10.1.1.206:8080/ic-psq-server/ws";

    String url="http://127.0.0.1:8080/ic-psq-server/ws";
    String op ="query";
    String query ="*:*";
    String ns ="";
    String ac ="";
    
    public static void main( String[] args ) {
        
        String url ="";
        String propName ="";
        String op ="query";
        String query ="*:*";
        String ns ="";
        String ac ="";
        
        String sFirst= "0";
        String sSize= "10";
        int first = 0;
        int size = 0;

        System.out.println( "psqclient" );
        
        Options options = new Options();
        
        Option hlpOption = OptionBuilder.withLongOpt( "help" )
            .withDescription( "help " )
            .create( "help" );

        options.addOption( hlpOption );

        Option urlOption = OptionBuilder.withLongOpt( "url" )
            .withArgName( "url" ).hasArg()
            .withDescription( "PSICQUIC server URL" )
            .create( "url" );

        options.addOption( urlOption );

        Option opOption = OptionBuilder.withLongOpt( "operation" )
            .withArgName( "operation" ).hasArg()
            .withDescription( "operation" )
            .create( "op" );

        options.addOption( opOption );

        Option qOption = OptionBuilder.withLongOpt( "query" )
            .withArgName( "query" ).hasArg()
            .withDescription( "MIQL query" )
            .create( "q" );

        options.addOption( qOption );

        Option nsOption = OptionBuilder.withLongOpt( "namespace" )
            .withArgName( "namespace" ).hasArg()
            .withDescription( "namespace" )
            .create( "ns" );

        options.addOption( nsOption );

        Option acOption = OptionBuilder.withLongOpt( "accession" )
            .withArgName( "accession" ).hasArg()
            .withDescription( "accession" )
            .create( "ac" );

        options.addOption( acOption );
        
        Option propOption = OptionBuilder.withLongOpt( "property" )
            .withArgName( "name" ).hasArg()
            .withDescription( "property name" )
            .create( "prp" );

        options.addOption( propOption );

        Option firstOption = OptionBuilder.withLongOpt( "first" )
            .withArgName( "int" ).hasArg()
            .withDescription( "first record" )
            .create( "fr" );

        options.addOption( firstOption );

        Option sizeOption = OptionBuilder.withLongOpt( "block-size" )
            .withArgName( "int" ).hasArg()
            .withDescription( "block size" )
            .create( "bs" );

        options.addOption( sizeOption );

        
        try{
            
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse( options, args);
            
            if( cmd.hasOption( "help" ) ){

                HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth( 127 );
                formatter.printHelp( "psqclient", options );
                System.exit(0);
            }
            
            if( cmd.hasOption( "url" ) ){
                url = cmd.getOptionValue("url");
            }

            if( cmd.hasOption( "op" ) ){
                op = cmd.getOptionValue( "op" );
            }

            if( cmd.hasOption( "q" ) ){
                query = cmd.getOptionValue( "q" );
            }

            if( cmd.hasOption( "ns" ) ){
                ns = cmd.getOptionValue( "ns" );
            }

            if( cmd.hasOption( "ac" ) ){
                ac = cmd.getOptionValue( "ac" );
            }
            if( cmd.hasOption( "prp" ) ){
                propName = cmd.getOptionValue( "prp" );
            }

            if( cmd.hasOption( "fr" ) ){
                sFirst = cmd.getOptionValue( "fr" );
            }
            if( cmd.hasOption( "bs" ) ){
                sSize = cmd.getOptionValue( "bs" );
            }

            first = Integer.parseInt( sFirst );
            size = Integer.parseInt( sSize );
            
            
        } catch( Exception exp ) {
            System.out.println( "psqclient: Options parsing failed. " +
                                "Reason: " + exp.getMessage() );
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(127);
            formatter.printHelp( "psqclient", options );
            System.exit(1);
        }
        
        ObjectFactory psqOF = new ObjectFactory();
        RequestInfo ri = psqOF.createRequestInfo();
        
        System.out.println( "URL: " + url );
        PsqClient psc = new PsqClient();
        psc.connect( url );

        if( op.equalsIgnoreCase( "getproperty" ) ){
            String propValue = psc.getProperty( propName );
            System.out.println( "property: " + propName + " value: " + propValue );
            System.exit(0);
        }
       
        if( op.equalsIgnoreCase( "getproperties" ) ){
            List<Map<String,String>> propList = psc.getProperties();
            if( propList != null && propList.size() > 0 ){
                System.out.println( "Properties:" );
                for( Iterator<Map<String,String>> pi = propList.iterator(); pi.hasNext(); ){
                    Map<String,String> cp = pi.next();
                    System.out.println( "  property: " + cp.get( "key" ) 
                                        + " value: " + cp.get( "value" ));
                }
            }
            System.exit(0);
        }
        
        if( op.equalsIgnoreCase( "getbyquery" ) ){
            QueryResponse qr = psc.getByQuery( query, first, size );
            
            if( qr != null ) {
                if( qr.getResultInfo() != null ){
                    System.out.println( " ResultInfo: First Record="
                                        + qr.getResultInfo().getFirstResult());
                }

                if( qr.getResultSet() != null ){
                    System.out.println( qr.getResultSet().getMitab() );
                }
            }            
            System.exit(0);
        }
        
        if( op.equalsIgnoreCase( "getversion" ) ){
            String ver =  psc.getVersion();
            System.out.println( "Version:" + ver );
            System.exit(0);
        }

        if( op.equalsIgnoreCase( "getsupportedreturntype" ) ){
            List<String> stl = psc.getSupportedReturnTypes();
            if( stl != null && stl.size() > 0 ){
                System.out.println( "SupportedReturnTypes:" );
                for( Iterator<String> pi = stl.iterator(); pi.hasNext(); ){
                    System.out.println( "  " + pi.next() );
                }
            }
            System.exit(0);
        }
        
        if( op.equalsIgnoreCase( "getsupporteddbacs" ) ){
            List<String> stl = psc.getSupportedDbAcs();
            if( stl != null && stl.size() > 0 ){
                System.out.println( "SupportedReturnTypes:" );
                for( Iterator<String> pi = stl.iterator(); pi.hasNext(); ){
                    System.out.println( "  " + pi.next() );
                }
            }            
            System.exit(0);
        }
        
        if( op.equalsIgnoreCase( "getbyinteraction" ) ){

            DbRef dbref = psqOF.createDbRef();

            dbref.setDbAc( ns );
            dbref.setId( ac );
            
            QueryResponse qr = 
                psc.getByInteraction( dbref , first, size );
            System.exit(0);
        }

        if( op.equalsIgnoreCase( "getByInteractor" ) ){

            DbRef dbref = psqOF.createDbRef();

            dbref.setDbAc( ns );
            dbref.setId( ac );
            
            QueryResponse qr = 
                psc.getByInteractor( dbref , first, size );
            System.exit(0);
        }
        
        if( op.equalsIgnoreCase( "getByInteractorList" ) ){

            DbRef dbref = psqOF.createDbRef();

            dbref.setDbAc( ns );
            dbref.setId( ac );

            DbRefList dbrl= psqOF.createDbRefList();
            dbrl.getDbRef().add(dbref);

            QueryResponse qr = 
                psc.getByInteractorList( dbrl.getDbRef(), 
                                         first, size, "" );
            System.exit(0);
        }

        if( op.equalsIgnoreCase( "getByInteractionList" ) ){
            
            DbRef dbref = psqOF.createDbRef();

            dbref.setDbAc( ns );
            dbref.setId( ac );

            DbRefList dbrl= psqOF.createDbRefList();
            dbrl.getDbRef().add(dbref);
            
            QueryResponse qr = 
                psc.getByInteractionList( dbrl.getDbRef(), 
                                          first, size );
            System.exit(0);
        }
    }
}
