CREATE TABLE if not exists users (
                                    id     SERIAL PRIMARY KEY,
                                    username    varchar(20),
                                    firstname varchar(30),
                                    lastname varchar(30)
);

insert into users (username, firstname, lastname)
values ('john', 'John', 'Homeowner'), ('jane', 'Jane', 'Dot'),
       ('ivan', 'Ivan', 'Ivanov');

