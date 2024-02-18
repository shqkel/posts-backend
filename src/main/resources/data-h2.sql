
-- h2 sample data insert

-- post
insert into post (id, title, writer, content, created_at)
values ((select nextval('POST_SEQ')), 'AI는 무엇인가요?', 'honggd', 'Artificial intelligence (AI) is the intelligence of machines or software, as opposed to the intelligence of human beings or animals.', CAST(current_timestamp AS TIMESTAMP(6)));
insert into post (id, title, writer, content, created_at)
values ((select nextval('POST_SEQ')), 'CHAT-GPT', 'honggd', 'ChatGPT is a large language model-based chatbot developed by OpenAI.', CAST(current_timestamp AS TIMESTAMP(6)));
insert into post (id, title, writer, content, created_at)
values ((select nextval('POST_SEQ')), 'React', 'sejong', '"React" is a popular JavaScript library for building member interfaces. React was developed by Facebook and has become widely used in web development.', CAST(current_timestamp AS TIMESTAMP(7)));

-- member
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'abcde','$2a$10$FW3ILWSTE0maZnLPhGlTDOXlE5NOMd7QJr80zirMDIi1Hljw7tY6y','아무개',PARSEDATETIME('1988-01-25','yyyy-MM-dd'),'abcde@naver.com',current_timestamp);
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'qwerty','$2a$10$ICxoR.RxiKw98pVXW45HCuH4SJWC1/mRFLBB8pH23T8JzxW7e.n/.','김말년',PARSEDATETIME('1978-02-25','yyyy-MM-dd'),'qwerty@naver.com',current_timestamp);
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'admin','$2a$10$yHzSOTYaC6wHUOrMm4ID/uWa27F/ng8.PfC1MHWVOE2Avr2CKmB.a','관리자',PARSEDATETIME('1990-12-25','yyyy-MM-dd'),'admin@naver.com',current_timestamp);
insert into member (id, member_id, password, name, birthday, email, created_at)
values ((select nextval('MEMBER_SEQ')),'honggd','$2a$10$S8K1zPoUD3593HsStS4rVuy.rKDfhlBDKWG.KBNQyGdyMPWePJDkW','홍길동',PARSEDATETIME('1999-09-09','yyyy-MM-dd'),'honggd@naver.com',current_timestamp);

-- authority
-- abcde(1), qwerty(51), admin(101), honggd(151)로 pk 등록된 경우
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),1,'ROLE_USER');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),51,'ROLE_USER');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),101,'ROLE_USER');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),101,'ROLE_ADMIN');
insert into authority (id, member_id, auth) values ((select nextval('AUTHORITY_SEQ')),151,'ROLE_USER');
