create table usuario(
	user varchar(50) not null,
	pass varchar(50) not null,
	primary key(user)
);

create table lista(
	id integer auto_increment not null,
	nombre varchar(50),
	user varchar(50),
	primary key(id),
	foreign key(user) references usuario(user) on update cascade on delete cascade,
	CONSTRAINT nlista UNIQUE (user,nombre)
);

create table cancion(
	id integer auto_increment not null,
	id_lista integer,
	nombre text,
	path text,
	duracion text,
	primary key(id),
	foreign key(id_lista) references lista(id) on update cascade on delete cascade
);

create table server_cancion(
	id integer auto_increment not null,
	nombre text,
	path text,
	duracion text,
	primary key(id)
);

create table server_download(
	id integer auto_increment not null,
	user varchar(50),
	id_cancion integer,
	download_date timestamp default current_timestamp,
	primary key(id),
	foreign key(user) references usuario(user) on update cascade on delete cascade,
	foreign key(id_cancion) references server_cancion(id) on update cascade on delete cascade
);