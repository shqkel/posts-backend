-- DDL
-- h2 db용

create table post (
  id number,
  title varchar2(500),
  writer varchar2(50),
  content varchar2(4000),
  created_at date default sysdate,
  constraint pk_post_id primary key (id)
);


create table member (
    id number,
    username varchar2(50),
    password varchar2(300) not null,
    name varchar2(256) not null,
    birthday date,
    email varchar2(300),
    created_at timestamp default current_timestamp,
    constraint pk_member_id primary key(id)
);


create table authority (
   id number,
   member_id number,
   name varchar2(50),
   constraint pk_authority_id primary key (id),
   constraint uq_authority unique (member_id, name),
   constraint fk_authority_member_id foreign key(member_id)
       references member(id)
           on delete cascade
);