IMEXCENTRAL: README.TXT
-----------------------

$HeadURL$
$Id$

Project compilation
--------------------------------------------------------------------------------

  All packages/modules within imex central project are built with Apache Maven 
(http://maven.apache.org/) project management tool. This requires, at least 
during the initial build, access to the network, as most of the libraries the
project depends upon have to be downloaded from their distribution repositories. 

Initial build
-------------

  It is recommended to initially build all modules within imex central by 
executing:

    mvn install

From then on it is necessary to recompile only the modules that have been 
modified or updated from the subversion repository.


Web Applications
----------------

  Some of the  modules (in particular icentral) implement Web application that 
can be deployed, after generation of a war file, within appropriate servlet 
container (such as tomcat, glassfish or jboss).  However, during development 
they can be simply deployed by executing, 
within their respective directories:

   mvn jetty:run-war

This will start the applicatiton under control of the jetty application server 
(http://jetty.codehaus.org/jetty/) started locally. To this end, the war file
prepared during the installtion stage is extracted into ./target/work/webapp
directory which is then used as the root directory of the running web appliation.
Contents of this directory are recreated every time the jetty server is 
restarted. Note that it, potentially, results in the loss of all the changes 
made to the application (such as modification of the menus, pages and other 
configuration files made through the web GUI).

Maven Profiles
--------------

  icentral-test  - access test database, build icentraltest.war 
         
  icentral-beta  - acccess production database, build icentralbeta.war

  icental-prod   - access production database, build icentral.war

  Note: in order to connect to the database and generate recaptchas define 
   corresponding private (~/.m2/settings.xml) or system-wide 
   ($M2_HOME/config/settings.xml) profiles:

    <profile>
      <id>icentral-XXXX</id>
      <activation>
       <activeByDefault>false</activeByDefault>
      </activation>
      <properties>
       <icentral.jdbc.user>****</icentral.jdbc.user>
       <icentral.jdbc.pass>****</icentral.jdbc.pass>

       <icentral.recaptcha.publicKey>****</icentral.recaptcha.publicKey>
       <icentral.recaptcha.privateKey>***</icentral.recaptcha.privateKey>
      </properties>
    </profile>

  and default properties:
     
   <icentral.jdbc.user>****</icentral.jdbc.user>
   <icentral.jdbc.pass>****</icentral.jdbc.pass>

   <icentral.recaptcha.publicKey>****</icentral.recaptcha.publicKey>
   <icentral.recaptcha.privateKey>***</icentral.recaptcha.privateKey>  

  to appropriately set database/recaptcha login information



