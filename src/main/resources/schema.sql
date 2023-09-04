-- @spring
create table post (
                      id number,
                      title varchar2(500),
                      writer varchar2(50),
                      content varchar2(4000),
                      created_at date default sysdate,
                      constraint pk_post_id primary key (id)
);

create sequence seq_post_id;


-- POST /posts
insert into post (id, title, writer, content, created_at) values (seq_post_id.nextval, 'AI는 무엇인가요?', 'sinsa', 'Artificial intelligence (AI) is the intelligence of machines or software, as opposed to the intelligence of human beings or animals. AI applications include advanced web search engines (e.g., Google Search), recommendation systems (used by YouTube, Amazon, and Netflix), understanding human speech (such as Siri and Alexa), self-driving cars (e.g., Waymo), generative or creative tools (ChatGPT and AI art), and competing at the highest level in strategic games (such as chess and Go).[1]', default);
insert into post (id, title, writer, content, created_at) values (seq_post_id.nextval, 'CHAT-GPT', 'honggd', 'ChatGPT is a large language model-based chatbot developed by OpenAI.', default);
insert into post (id, title, writer, content, created_at) values (seq_post_id.nextval, 'React', 'sejong', '"React" is a popular JavaScript library for building member interfaces. React was developed by Facebook and has become widely used in web development.', default);

-- GET /posts
select * from post order by id desc;

-- GET /posts/1
select * from post where id = 1;

-- PATCH /posts/1
update post set title = '짱 쉬운 AI', content = 'Artificial intelligence, That\'s all~.' escape '\' where id = 1;

-- DELETE /posts/1
delete from posts where id = 1;


-- insert all into post (id, title, writer, content)
-- values(seq_post_id.nextval, title, writer, content)
-- select * from post order by id;



-- h2 sample data insert
-- created_at timestamp(6)
insert into post (id, title, writer, content, created_at)
values ((select nextval('SEQ_POST_ID')), 'AI는 무엇인가요?', 'honggd', 'Artificial intelligence (AI) is the intelligence of machines or software, as opposed to the intelligence of human beings or animals.', CAST(current_timestamp AS TIMESTAMP(6)));
insert into post (id, title, writer, content, created_at)
values ((select nextval('SEQ_POST_ID')), 'CHAT-GPT', 'honggd', 'ChatGPT is a large language model-based chatbot developed by OpenAI.', CAST(current_timestamp AS TIMESTAMP(6)));
insert into post (id, title, writer, content, created_at)
values ((select nextval('SEQ_POST_ID')), 'React', 'sejong', '"React" is a popular JavaScript library for building member interfaces. React was developed by Facebook and has become widely used in web development.', CAST(current_timestamp AS TIMESTAMP(7)));





-- h2 db용
create table member (
    id number,
    member_id varchar2(50),
    password varchar2(300) not null,
    name varchar2(256) not null,
    birthday date,
    email varchar2(300),
    created_at timestamp default current_timestamp,
    constraint pk_member_id primary key(id)
);

insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'abcde','$2a$10$FW3ILWSTE0maZnLPhGlTDOXlE5NOMd7QJr80zirMDIi1Hljw7tY6y','아무개',PARSEDATETIME('1988-01-25','yyyy-MM-dd'),'abcde@naver.com',current_timestamp);
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'qwerty','$2a$10$ICxoR.RxiKw98pVXW45HCuH4SJWC1/mRFLBB8pH23T8JzxW7e.n/.','김말년',PARSEDATETIME('1978-02-25','yyyy-MM-dd'),'qwerty@naver.com',current_timestamp);
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'admin','$2a$10$yHzSOTYaC6wHUOrMm4ID/uWa27F/ng8.PfC1MHWVOE2Avr2CKmB.a','관리자',PARSEDATETIME('1990-12-25','yyyy-MM-dd'),'admin@naver.com',current_timestamp);
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'honggd','$2a$10$S8K1zPoUD3593HsStS4rVuy.rKDfhlBDKWG.KBNQyGdyMPWePJDkW','홍길동',PARSEDATETIME('1999-09-09','yyyy-MM-dd'),'honggd@naver.com',current_timestamp);

create table authority (
   id number,
   member_id number,
   auth varchar2(50),
   constraint pk_authority_id primary key (id),
   constraint uq_authority unique (member_id, auth),
   constraint fk_authority_member_id foreign key(member_id)
       references member(id)
           on delete cascade
);
-- abcde(1), qwerty(51), admin(101), honggd(151)로 pk 등록된 경우
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),1,'ROLE_USER');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),51,'ROLE_USER');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),101,'ROLE_USER');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),101,'ROLE_ADMIN');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),151,'ROLE_USER');


select * from member;
select * from authority;
