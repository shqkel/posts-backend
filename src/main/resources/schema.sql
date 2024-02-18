-- DDL
drop table post;
drop sequence post_seq;

create table post (
  id number,
  title varchar2(500),
  writer varchar2(50),
  content varchar2(4000),
  created_at date default sysdate,
  constraint pk_post_id primary key (id)
);

create sequence post_seq increment by 50;

-- create table authority (
--     id number(19,0) not null,
--     member_id number(19,0) not null,
--     auth varchar2(255 char) not null check (auth in ('ROLE_USER','ROLE_ADMIN')),
--     primary key (id),
--     constraint uq_authority unique (member_id, auth)
-- );
-- create sequence authority_seq start with 1 increment by 50;
--
-- create table member (
--     id number(19,0) not null,
--     member_id varchar2(255 char) not null unique,
--     password varchar2(255 char) not null,
--     name varchar2(255 char),
--     birthday date,
--     email varchar2(255 char),
--     created_at timestamp(6),
--     primary key (id)
-- );
-- create sequence member_seq start with 1 increment by 50;

