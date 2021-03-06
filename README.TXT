IMEXCENTRAL: README.TXT

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


Migration to v1.02 tables
-------------------------

 alter table entry add column act_uid integer not null default 0;
 alter table entry add column act_timestamp timestamp not null default now();

 alter table entry add column mod_uid integer not null default 0;
 alter table entry add column mod_timestamp timestamp not null default now();
 
 update entry set act_uid=owner_uid where act_uid=0;
 update entry set mod_uid=owner_uid where act_uid=0;

 CREATE INDEX e_10 on entry (act_uid);
 CREATE INDEX e_11 on entry (act_timestamp);
 CREATE INDEX e_12 on entry (mod_uid);
 CREATE INDEX e_13 on entry (mod_timestamp);


Migration to v2.00 tables (icentral_v21 database)
-------------------------

- new states

insert into datastate (name) values ('REQUEST');
insert into datastate (name) values ('QUEUE');
insert into datastate (name) values ('RETRACTED');

- new stage table/stages

CREATE TABLE datastage (
    id serial primary key,
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default ''
);

CREATE INDEX datastage_1 on datastage (name);
insert into datastage (name,comments) values ('PREQUEUE','');
insert into datastage (name,comments) values ('QUEUE','');
insert into datastage (name,comments) values ('CURATION','');

- new entry columns

alter table entry add column volume  character varying(32) default '';
alter table entry add column issue  character varying(32) default '';
alter table entry add column pages  character varying(32) default '';
alter table entry add column year character varying(32) default '';
alter table entry add column stage_id integer not null default 1;

CREATE INDEX e_14 on entry (stage_id);
CREATE INDEX e_15 on entry (volume);
CREATE INDEX e_16 on entry (issue);
CREATE INDEX e_17 on entry (pages);
CREATE INDEX e_18 on entry (year);
CREATE INDEX e_19 on entry (year,volume);


deployed on:
     icentral: 10.1.1.113
     requires java 1.8.x
              tomcat 8.x


2018-11-24

 - columns added
  -  unique flag added to roles
  
     alter table role add column grp_unique boolean not null default false;

    NOTE: only one group with the same, grp_unique-flagged role can be assigned
    to a given record

  - sha1pass column added to usr
    alter table usr add column sha1pass character varying(32) not null default ''::character varying;

    sha1 pass is passed as a string preceded by SHA1: prefix


Stage/Status update strategy
----------------------------

stage           state

prequeue        new, request, discarded

     setting state -> 'queue' changes stage -> 'queue', 
                                      state->'new' (if different from 'new' or 'request') 

queue           new, request, discarded
curation        reserved, inprogress, incomplete, processed, discarded,retracted, 


                if( state.getName().equals("QUEUE") ){
                    state = wflowContext.getWorkflowDao().getDataState( "NEW" );
                    stage = wflowContext.getWorkflowDao().getDataStage( "QUEUE" );
                }

                if( ! ( state.getName().equals("NEW") ||
                        state.getName().equals("QUEUE") ||
                        state.getName().equals("REQUEST") ||
                        state.getName().equals("DISCARDED") ) ){
                    stage = wflowContext.getWorkflowDao().getDataStage( "CURATION" );
                }


2018-11-24  curation/review state added
