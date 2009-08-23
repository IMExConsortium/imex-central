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
    name character varying(32) UNIQUE not null default '',
    comments character varying(256) not null default '',
    from_sid integer not null default 0,
    to_sid integer not null default 0
);
CREATE INDEX trans_1 on trans (name);
CREATE INDEX trans_2 on trans (from_sid,to_sid);
CREATE INDEX trans_3 on trans (to_sid);

