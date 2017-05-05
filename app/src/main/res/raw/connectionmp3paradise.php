<?php

class BD {
    
    function __construct()
    { 
        $this->pdo = new PDO('mysql:host=localhost;dbname=Xjperez134_das_mp3_paradise;charset=utf8', 'Xjperez134', '*********');
    }

    function login($user,$pass){
        $query='select user from usuario where user=:user and pass=:pass';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':user', $user, PDO::PARAM_STR);
        $sth->bindParam(':pass', $pass, PDO::PARAM_STR);
        $sth->execute();
        $user = $sth->fetch(PDO::FETCH_ASSOC);
        if (isset($user["user"])) {
            return array('status'=>"ok",'user'=>$user["user"]);
        }
        else{
            return array('status'=> "error");
        }
    }

    function register($user,$pass){
        $query='insert into usuario(user,pass) values(:user,:pass)';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':user', $user, PDO::PARAM_STR);
        $sth->bindParam(':pass', $pass, PDO::PARAM_STR);
        if($sth->execute()){
            return array('status'=>"ok",'user'=>$user);
        }
        else{
            return array('status'=> "error");
        }
    }

    function getListaUsu($user){
        $query='select lista.nombre,lista.id,count(cancion.id) as num_canciones from lista left outer join cancion on lista.id=cancion.id_lista where lista.user=:user  group by  lista.nombre';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':user', $user, PDO::PARAM_STR);
        $sth->execute();
        $listas = $sth->fetchAll(PDO::FETCH_ASSOC);
        if(isset($listas[0])){
            return array('status'=>"ok",'listas'=>$listas);
        }
        else{
            return array('status'=> "error");
        }
    }
    
     function getCancionesLista($id_lista){
        $query='select * from cancion where id_lista=:id_lista';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':id_lista', $id_lista, PDO::PARAM_INT);
        $sth->execute();
        $canciones = $sth->fetchAll(PDO::FETCH_ASSOC);
        if(isset($canciones[0])){
            return array('status'=>"ok",'canciones'=>$canciones);
        }
        else{
            return array('status'=> "error");
        }
    }

    function getDownloadLista(){
        $query='select nombre,duracion,id from server_cancion';
        $sth = $this->pdo->prepare($query);
        $sth->execute();
        $canciones = $sth->fetchAll(PDO::FETCH_ASSOC);
        if(isset($canciones[0])){
            return array('status'=>"ok",'canciones'=>$canciones);
        }
        else{
            return array('status'=> "error");
        }
    }

    function getDownloadPath($id_cancion,$user){
        $query='insert into server_download(user,id_cancion) values(:user,:id_cancion)';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':id_cancion', $id_cancion, PDO::PARAM_INT);
        $sth->bindParam(':user', $user, PDO::PARAM_STR);
        if(!$sth->execute()){
            return array('status'=> "error");
        }
        $query='select path from server_cancion where id=:id_cancion';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':id_cancion', $id_cancion, PDO::PARAM_INT);
        $sth->execute();
        $cancion = $sth->fetch(PDO::FETCH_ASSOC);
        if(isset($cancion['path'])){
            return array('status'=>"ok",'path'=>$cancion['path']);
        }
        else{
            return array('status'=> "error");
        }
    }

    function insertLista($user,$nombre_lista){
        $query='insert into lista(user,nombre) values(:user,:nombre_lista)';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':user', $user, PDO::PARAM_STR);
        $sth->bindParam(':nombre_lista', $nombre_lista, PDO::PARAM_STR);
        $sth->execute();
        $query='select nombre,id from lista where user=:user and nombre=:nombre_lista';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':user', $user, PDO::PARAM_STR);
        $sth->bindParam(':nombre_lista', $nombre_lista, PDO::PARAM_STR);
        $sth->execute();
        $lista = $sth->fetch(PDO::FETCH_ASSOC);
        if(isset($lista["nombre"])){
            return array('status'=>"ok",'lista'=>$lista);
        }
        else{
            return array('status'=> "error");
        }
    }

    function insertCancion($id_lista,$nombre,$path,$duracion){
        $query='insert into cancion(id_lista,nombre,path,duracion) values(:id_lista,:nombre,:path,:duracion)';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':path', $path, PDO::PARAM_STR);
        $sth->bindParam(':nombre', $nombre, PDO::PARAM_STR);
        $sth->bindParam(':duracion', $duracion, PDO::PARAM_STR);
        $sth->bindParam(':id_lista', $id_lista, PDO::PARAM_INT);
        if($sth->execute()){
            return array('status'=>"ok");
        }
        else{
            return array('status'=> "error");
        }
    }

    function deleteCancionLista($id_cancion){
        $query='delete from cancion where id=:id_cancion';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':id_cancion', $id_cancion, PDO::PARAM_INT);
        if($sth->execute()){
            return array('status'=>"ok");
        }
        else{
            return array('status'=> "error");
        }
    }

    function deleteLista($id_lista){
        $query='delete from lista where id=:id_lista';
        $sth = $this->pdo->prepare($query);
        $sth->bindParam(':id_lista', $id_lista, PDO::PARAM_INT);
        if($sth->execute()){
            return array('status'=>"ok");
        }
        else{
            return array('status'=> "error");
        }
    }

}

