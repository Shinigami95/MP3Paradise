<?php
if (isset($_POST['func'])) {
	require_once "connectionmp3paradise.php";
	$bd=new BD();
	switch ($_POST['func']) {
		case 'login':
			$json = $bd->login($_POST['user'],$_POST['pass']);
			break;
		case 'register':
			$json = $bd->register($_POST['user'],$_POST['pass']);
			break;
		case "get_listas_usu":
			$json = $bd->getListaUsu($_POST['user']);
			break;
		case "get_canciones_lista":
			$json = $bd->getCancionesLista($_POST['id_lista']);
			break;
		case "get_download_lista":
			$json = $bd->getDownloadLista();
			break;
		case "get_download_path":
			$json = $bd->getDownloadPath($_POST['id_cancion'],$_POST['user']);
			break;
		case "add_lista":
			$json = $bd->insertLista($_POST['user'],$_POST['nombre_lista']);
			break;
		case "add_cancion_lista":
			$json = $bd->insertCancion($_POST['id_lista'],$_POST['nombre'],$_POST['path'],$_POST['duracion']);
			break;
		case "delete_cancion_lista":
			$json = $bd->deleteCancionLista($_POST["id_cancion"]);
			break;
		case "delete_lista":
			$json = $bd->deleteLista($_POST["id_lista"]);
			break;
		default:
			$json="";
			break;
	}
	echo json_encode($json);
}
?>