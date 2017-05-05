package model;

public class Cancion {
    public int id;
    public String nombre;
    public String path;
    public String duracion;

    public Cancion(int idCan, String nombreCan, String pathCan, String duracionCan){
        id = idCan;
        nombre = nombreCan;
        path = pathCan;
        duracion = duracionCan;
    }
}
