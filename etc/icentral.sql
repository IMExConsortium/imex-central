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

    source_id integer not null default 0,
    state_id integer not null default 0,

    imexId character varying(32) default '',

    doi character varying(256) default '',
    pmid character varying(32) default '',
    jspec character varying(256) default '',
    title character varying(1024) default '',
    author text default '',
    abstract text default '',
    pub_date timestamp ,
    epub_date timestamp,
    rel_date timestamp
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
