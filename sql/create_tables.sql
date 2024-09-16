create table if not exists credentials
(
    id serial
        constraint credentials_pkey
            primary key,
    login varchar(255),
    password varchar(255)
);

alter table credentials owner to mongol;

create table if not exists persons
(
    role varchar(31) not null,
    id serial
        constraint persons_pkey
            primary key,
    date_of_birth date,
    first_name varchar(255),
    last_name varchar(255),
    patronymic varchar(255),
    credential_id integer
        constraint fkmo4ifcdjrtukdqxb5ps6dqo35
            references credentials
            on update cascade on delete cascade
);

alter table persons owner to mongol;

create table if not exists groups
(
    id serial
        constraint groups_pkey
            primary key,
    name varchar(255),
    teacher_id integer
        constraint fk2usjbs9tagqo9b71mm8ra790p
            references persons
            on update cascade on delete set null
);

alter table groups owner to mongol;

create table if not exists group_student
(
    group_id integer not null
        constraint fkent8q7mj5tq6lup5v1e7alrqx
            references groups
            on update cascade on delete cascade,
    student_id integer not null
        constraint fkju4ijt5jr29o4f7b254eou74w
            references persons
            on update cascade on delete cascade,
    constraint group_student_pkey
        primary key (group_id, student_id)
);

alter table group_student owner to mongol;

create table if not exists salaries
(
    id serial
        constraint salaries_pkey
            primary key,
    date_of_salary date,
    count integer,
    teacher_id integer
        constraint fkeuvfci1ldskaogco874xqkund
            references persons
            on update cascade on delete cascade
);

alter table salaries owner to mongol;

create table if not exists subjects
(
    id serial
        constraint subjects_pkey
            primary key,
    name varchar(255)
);

alter table subjects owner to mongol;

create table if not exists group_subject
(
    group_id integer not null
        constraint fkma8bk9fi6g9bwl1ybe4em41tl
            references groups
            on update cascade on delete cascade,
    subject_id integer not null
        constraint fk2qu1afk2lnpuu3y670eofwqxp
            references subjects
            on update cascade on delete cascade,
    constraint group_subject_pkey
        primary key (group_id, subject_id)
);

alter table group_subject owner to mongol;

create table if not exists marks
(
    id serial
        constraint marks_pkey
            primary key,
    date_of_mark date,
    point integer,
    subject_id integer not null
        constraint uk_4pnuumjkh6yadhn9gjn7uj6x0
            unique
        constraint fkbobdu0dce8k5vpm5mbd5ggpb1
            references subjects
            on update cascade on delete cascade,
    student_id integer
        constraint fkl2jo6eh0ldfna7h6ru74rfm33
            references persons
            on update cascade on delete cascade
);

alter table marks owner to mongol;

