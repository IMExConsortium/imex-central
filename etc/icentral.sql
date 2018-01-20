CREATE TABLE role (
    id serial primary key, 
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default ''
);
CREATE INDEX role_1 on role (name);

CREATE TABLE grp (
    id serial NOT NULL,
    label character varying(32) UNIQUE NOT NULL,
    name character varying(64)  NOT NULL default '',
    comments character varying(256) not null default '',
    contact_uid int,
    admin_uid int
);
CREATE INDEX grp_idx1 ON grp (label);
CREATE INDEX grp_idx2 ON grp (name);

CREATE TABLE usr (
    id serial NOT NULL,
    login  character varying(32),
    pass character varying(32),  
    fname character varying(64),
    lname character varying(64),
    affiliation character varying(128),
    email character varying(128),
    act_key character varying(64) not null default '',
    act_flag boolean not null default false,
    enable_flag boolean not null default true,
    prefs text not null default '',
    time_cr timestamp not null default now()
);
CREATE INDEX usr_idx1 ON usr (login);
CREATE INDEX usr_idx2 ON usr (fname);
CREATE INDEX usr_idx3 ON usr (lname);
CREATE INDEX usr_idx4 ON usr (affiliation);
CREATE INDEX usr_idx5 ON usr (email);

CREATE TABLE usr_role (
    usr_id integer not null default 0, 
    role_id integer not null default 0,
    primary key (usr_id,role_id) 
);
CREATE INDEX usr_role_1 on usr_role (role_id);
CREATE INDEX usr_role_2 on usr_role (role_id, usr_id);
CREATE INDEX usr_role_3 on usr_role (usr_id,role_id);

CREATE TABLE usr_grp (
    usr_id integer not null default 0, 
    grp_id integer not null default 0,
    primary key (usr_id, grp_id)
);
CREATE INDEX usr_grp_1 on usr_grp (grp_id);
CREATE INDEX usr_grp_2 on usr_grp (grp_id, usr_id);
CREATE INDEX usr_grp_3 on usr_grp (usr_id,grp_id);

CREATE TABLE grp_role ( 
    grp_id integer not null default 0, 
    role_id integer not null default 0,
    primary key (grp_id, role_id)
);
CREATE INDEX grp_role_1 on grp_role (role_id);
CREATE INDEX grp_role_2 on grp_role (role_id, grp_id);
CREATE INDEX grp_role_3 on grp_role (grp_id,role_id);


CREATE TABLE datastate (
    id serial primary key, 
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default ''
);
CREATE INDEX datastate_1 on datastate (name);

CREATE TABLE datastage (
    id serial primary key, 
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default ''
);
CREATE INDEX datastage_1 on datastage (name);


CREATE TABLE cflag (
    id serial primary key, 
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default ''
);
CREATE INDEX cflag_1 on  (name);

CREATE TABLE trans (
    id serial primary key, 
    name character varying(32) not null default '',
    comments character varying(256) not null default '',
    from_sid integer not null default 0,
    to_sid integer not null default 0
);
CREATE INDEX trans_1 on trans (name);
CREATE INDEX trans_2 on trans (from_sid,to_sid);
CREATE INDEX trans_3 on trans (to_sid);

CREATE TABLE journal (
    id serial primary key,
    owner_uid integer not null default 0,
    crt timestamp not null default now(),

    title character varying(256) not null default '',
    issn character varying(32) not null default '',
    nlmid character varying(32) not null default '',
    url character varying(256) not null default '',
    comments character varying(256) not null default ''
);
CREATE INDEX j_1 on journal (owner_uid);
CREATE INDEX j_2 on journal (crt);
CREATE INDEX j_3 on journal (title);
CREATE INDEX j_4 on journal (nlmid);

CREATE TABLE entry (
    id serial primary key,
    owner_uid integer not null default 0,
    crt timestamp not null default now(),

    contact_email character varying(128) default '',

    source_id integer not null default 0,
    state_id integer not null default 0,
    stage_id integer not null default 0,

--    imexId character varying(32) default '',
    imex_id integer, 

    doi character varying(256) default '',
    pmid character varying(32) default '',
    jspec character varying(256) default '',
    title character varying(1024) default '',
    author text default '',
    abstract text default '',

    year character varying(32) default '',
    volume character varying(32) default '',
    issue character varying(32) default '',
    pages character varying(32) default '',

    pub_date timestamp ,
    epub_date timestamp,
    rel_date timestamp,
        
    act_uid integer not null default 0,
    act_timestamp timestamp not null default now(),
    
    mod_uid integer not null default 0,
    mod_timestamp timestamp not null default now()

);

CREATE INDEX e_1 on entry (owner_uid);
CREATE INDEX e_2 on entry (crt);

CREATE INDEX e_3 on entry (pmid);
CREATE INDEX e_4 on entry (doi);
CREATE INDEX e_5 on entry (jspec);
CREATE INDEX e_6 on entry (title);
CREATE INDEX e_7 on entry (pub_date);
CREATE INDEX e_8 on entry (epub_date);
CREATE INDEX e_9 on entry (rel_date);

CREATE INDEX e_10 on entry (state_id);
CREATE INDEX e_11 on entry (stage_id);


CREATE TABLE entry_ausr (
    entry_id integer not null default 0, 
    usr_id integer not null default 0,
    primary key (entry_id,usr_id) 
);
CREATE INDEX entry_ausr_1 on entry_ausr (entry_id);
CREATE INDEX entry_ausr_2 on entry_ausr (entry_id, usr_id);
CREATE INDEX entry_ausr_3 on entry_ausr (usr_id,entry_id);

CREATE TABLE entry_agrp (
    entry_id integer not null default 0, 
    grp_id integer not null default 0,
    primary key (entry_id,grp_id) 
);
CREATE INDEX entry_agrp_1 on entry_agrp (entry_id);
CREATE INDEX entry_agrp_2 on entry_agrp (entry_id, grp_id);
CREATE INDEX entry_agrp_3 on entry_agrp (grp_id,entry_id);

CREATE TABLE journal_ausr (
    journal_id integer not null default 0, 
    usr_id integer not null default 0,
    primary key (journal_id,usr_id) 
);
CREATE INDEX journal_ausr_1 on journal_ausr (journal_id);
CREATE INDEX journal_ausr_2 on journal_ausr (journal_id, usr_id);
CREATE INDEX journal_ausr_3 on journal_ausr (usr_id,journal_id);

CREATE TABLE journal_agrp (
    journal_id integer not null default 0, 
    grp_id integer not null default 0,
    primary key (journal_id,grp_id) 
);
CREATE INDEX journal_agrp_1 on journal_agrp (journal_id);
CREATE INDEX journal_agrp_2 on journal_agrp (journal_id, grp_id);
CREATE INDEX journal_agrp_3 on journal_agrp (grp_id,journal_id);

create table keyspace (
    id serial NOT NULL primary key, 
    name character varying(32) NOT NULL default '', 
    prefix character varying(8) NOT NULL default '',
    postfix character varying(8) NOT NULL default '', 
    active boolean NOT NULL default true, 
    comments text NOT NULL default '');

CREATE INDEX keyspace_1 on keyspace (name);

create table key (
    id serial NOT NULL primary key, 
    keyspace_id integer not null default 0,
    value integer not null default 0,
    created timestamp not null default now()
);

CREATE INDEX key_1 on key (value);
CREATE INDEX key_2 on key (keyspace_id,value);

CREATE TABLE adi (
    id serial NOT NULL primary key,
    owner_uid integer not null default 0,
    crt timestamp not null default now(),

    root_id integer not null default 0,
    parent_id integer

);

CREATE INDEX adi_1 on adi (owner_uid);
CREATE INDEX adi_2 on adi (root_id);
CREATE INDEX adi_3 on adi (parent_id);
CREATE INDEX adi_4 on adi (crt);

CREATE TABLE comment (
     id serial NOT NULL primary key,
     adi_id integer,
     cflag_id integer,
     format integer not null default 0,
     subject text not null default '',
     body text not null default ''

);

CREATE INDEX com_1 on comment (adi_id);
CREATE INDEX com_2 on comment (subject);
CREATE INDEX com_3 on comment (body);
CREATE INDEX com_4 on comment (cflag_id);

CREATE TABLE cflag (
    id serial primary key, 
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default ''
);

CREATE INDEX cflag_1 on cflag (name);


CREATE TABLE log (
     id serial NOT NULL primary key,
     adi_id integer,
     subject text not null default '',
     body text not null default ''

);

CREATE INDEX log_1 on log (adi_id);
CREATE INDEX log_2 on log (subject);
CREATE INDEX log_3 on log (body);


CREATE TABLE attachment (
     id serial NOT NULL primary key,
     adi_id integer,
     cflag_id integer,
     format integer not null default 0,
     subject text not null default '',
     body text not null default '',
     
);

CREATE INDEX att_1 on attachment (adi_id);
CREATE INDEX att_2 on attachment (subject);
CREATE INDEX att_3 on attachment (body);

       
CREATE TABLE sorel (
    id serial NOT NULL,
    subject_id integer not null default 0,
    user_id integer not null default 0
);

CREATE INDEX sorel_1 on sorel (subject_id);
CREATE INDEX sorel_2 on sorel (user_id);


CREATE TABLE eorel (
    id serial NOT NULL,
    event character varying(32) not null default '',
    user_id integer not null default 0
);

CREATE INDEX eorel_1 on eorel (event);
CREATE INDEX eorel_2 on eorel (user_id);



CREATE TABLE  score (
     id serial NOT NULL primary key,
     adi_id integer,
     name text not null default '',
     value real not null default 0

);

CREATE INDEX score_1 on score (adi_id);
CREATE INDEX score_2 on score (name);
CREATE INDEX score_3 on score (value);
CREATE INDEX score_4 on score (name,value);


