Simple client:

java -Djavax.net.ssl.trustStore=<store> -Djavax.net.ssl.trustStorePassword=<store-pass> 
     -cp target/iclient.jar:../ic-api/target/ic-api.jar edu.ucla.mbi.imex.central.iclient <usr> <pass> <pmid>


