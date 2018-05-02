create table if not exists connection.restaurants (
    id_restaurant bigint not null auto_increment comment 'incremental id for restaurants',
    `name` varchar(100) not null comment 'restaurant name',
    primary key pk_restaurants (id_restaurant)
) comment = 'Restaurants table';

create table if not exists connection.status (
    id_status binary(16) not null comment 'incremental id for status',
    id_restaurant bigint not null comment 'related restaurant id',
    dt_inits datetime not null comment 'datetime the status starts',
    dt_ends datetime not null comment 'datetime the status ends',
    type int comment 'the type can be either online, offline and unavailable',
    primary key pk_schedules (id_status),
    foreign key fk_status_restaurants (id_restaurant)
        references connection.restaurants(id_restaurant)
) comment = 'Status table';

 insert into restaurants (`name`) values ('rest01');
 insert into restaurants (`name`) values ('rest02');
 insert into restaurants (`name`) values ('rest03');
 insert into restaurants (`name`) values ('rest04');
 insert into restaurants (`name`) values ('rest05');
