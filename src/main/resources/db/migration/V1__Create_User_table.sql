create table USER (
    id int primary key auto_increment,
    username varchar(20) unique,
    password varchar(100),
    avatar varchar(64),
    created_at DATE,
    updated_at DATE
)