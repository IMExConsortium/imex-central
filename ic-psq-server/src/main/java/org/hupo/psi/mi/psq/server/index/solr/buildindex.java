package org.hupo.psi.mi.psq.server.index.solr;

/* =============================================================================
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #
 # BuildSolrIndex: Build Solr-based index
 #
 #=========================================================================== */

import java.util.*;
import java.net.*;
import java.io.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.util.JAXBResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.cli.*;

import org.apache.solr.common.*;
import org.apache.solr.core.*;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.*;
import org.apache.solr.client.solrj.response.*;

import java.util.zip.CRC32;
import java.lang.Thread.State;

import edu.ucla.mbi.util.JsonContext;

public class buildindex{
    
    public static final String 
        CONTEXT = "../etc/ic-psq/server/psq-context.json";
    
    public static final String 
        RFRMT = "mif254";   

    public static String rfrmt;

    public static String dir =  "/home/lukasz/imex_test";
    public static boolean zip = false;
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public static void main( String [ ] args ){
        
        Options options = new Options();
 
        Option hlpOption = OptionBuilder.withLongOpt( "help" )
            .withDescription( "help " )
            .create( "help" );

        options.addOption( hlpOption );
        
        Option urlOption = OptionBuilder.withLongOpt( "url" )
            .withArgName( "url" ).hasArg()
            .withDescription( "server url" )
            .create( "url" );


        Option dirOption = OptionBuilder.withLongOpt( "dir" )
            .withArgName( "dir" ).hasArg()
            .withDescription( "file location" )
            .create( "d" );

        options.addOption( dirOption );

        Option zipOption = OptionBuilder.withLongOpt( "zip" )
            .withDescription( "zipped files" )
            .create( "z" );

        options.addOption( zipOption );
        
        Option ctxOption = OptionBuilder.withLongOpt( "context" )
            .withArgName( "file.json" ).hasArg()
            .withDescription( "configuration file" )
            .create( "ctx" );

        options.addOption( ctxOption );
        
        String context = BuildSolrIndex.CONTEXT;
 
        Option iftOption = OptionBuilder.withLongOpt( "iformat" )
            .withArgName( "format" ).hasArg()
            .withDescription( "input record format" )
            .create( "ift" );
        
        options.addOption( iftOption );
        
        String ifrmt = BuildSolrIndex.RFRMT;
        
        try{
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse( options, args);

            if( cmd.hasOption("help") ){

                HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth( 127 );
                formatter.printHelp( "BuildSolrIndex", options );
                System.exit(0);
            }

            if( cmd.hasOption("ctx") ){
                context = cmd.getOptionValue("ctx");
            }
        
            if( cmd.hasOption("ift") ){
                ifrmt = cmd.getOptionValue( "ift" );
            }

            if( cmd.hasOption("d") ){
                dir = cmd.getOptionValue( "d" );
            }
            
            if( cmd.hasOption("z") ){
                zip = true;
            }

        } catch( Exception exp ) {
            System.out.println( "BuildSolrIndex: Options parsing failed. " +
                                "Reason: " + exp.getMessage() );
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(127);
            formatter.printHelp( "BuildSolrIndex", options );
            System.exit(1);
        }

        System.out.println( "Context: " + context );
        
        BuildSolrIndex psi = new BuildSolrIndex( context, ifrmt,
                                                 dir, zip );
        psi.start();
        
    }
}
