package io.github.mctz;

import com.badlogic.gdx.graphics.Color;

// Un lugar del pueblo al que el jugador puede ir a verificar una noticia
public class Lugar {

    // Índices de los lugares (se usan en el banco de noticias)
    public static final int PLAZA = 0;
    public static final int PARQUE = 1;
    public static final int ALCALDIA = 2;
    public static final int HOSPITAL = 3;
    public static final int PUENTE = 4;
    public static final int TERMINAL = 5;
    public static final int REGISTRADURIA = 6;
    public static final int SEDE_CAMPANA = 7;
    public static final int CENTRO_COMERCIAL = 8;
    public static final int BARRIO_SUR = 9;
    public static final int BIBLIOTECA = 10;
    public static final int ESTADIO = 11;

    String nombre;
    // Imagen opcional en assets (si no existe se usa el color de fondo)
    String archivoImagen;
    Color color;
    // Lo que el jugador ve cuando llega y la noticia no es de ese lugar
    String descripcion;

    public Lugar(String nombre, String archivoImagen, Color color, String descripcion) {
        this.nombre = nombre;
        this.archivoImagen = archivoImagen;
        this.color = color;
        this.descripcion = descripcion;
    }

    public static Lugar[] crearLugares() {
        Lugar[] l = new Lugar[12];
        l[PLAZA] = new Lugar("PLAZA CENTRAL", "lugar_plaza.png", Color.valueOf("#C9853F"),
                "Palomas, vendedores de raspao y gente conversando en las bancas.");
        l[PARQUE] = new Lugar("PARQUE", "lugar_parque.png", Color.valueOf("#3F8F4E"),
                "Niños jugando y señores trotando entre los árboles.");
        l[ALCALDIA] = new Lugar("ALCALDÍA", "lugar_alcaldia.png", Color.valueOf("#5A6FA8"),
                "Funcionarios entrando y saliendo con carpetas.");
        l[HOSPITAL] = new Lugar("HOSPITAL", "lugar_hospital.png", Color.valueOf("#5FA8A8"),
                "La sala de espera está tranquila, como un día normal.");
        l[PUENTE] = new Lugar("PUENTE PRINCIPAL", "lugar_puente.png", Color.valueOf("#7A7A7A"),
                "Los carros pasan sin problema sobre el puente.");
        l[TERMINAL] = new Lugar("TERMINAL DE BUSES", "lugar_terminal.png", Color.valueOf("#C9B03F"),
                "Buses saliendo y llegando, la gente hace fila con su pasaje.");
        l[REGISTRADURIA] = new Lugar("REGISTRADURÍA", "lugar_registraduria.png", Color.valueOf("#8A5AA8"),
                "Una funcionaria atiende a la fila de ciudadanos.");
        l[SEDE_CAMPANA] = new Lugar("SEDE DE CAMPAÑA", "lugar_sede.png", Color.valueOf("#A85A5A"),
                "Voluntarios pegando afiches y repartiendo volantes.");
        l[CENTRO_COMERCIAL] = new Lugar("CENTRO COMERCIAL", "lugar_centro_comercial.png", Color.valueOf("#A8708F"),
                "Las tiendas están abiertas y hay música en los pasillos.");
        l[BARRIO_SUR] = new Lugar("BARRIO SUR Y RÍO", "lugar_barrio_sur.png", Color.valueOf("#4F7FA0"),
                "Vecinos en las terrazas y el río corriendo tranquilo.");
        l[BIBLIOTECA] = new Lugar("BIBLIOTECA", "lugar_biblioteca.png", Color.valueOf("#8F6B4A"),
                "Estudiantes leyendo en silencio.");
        l[ESTADIO] = new Lugar("ESTADIO", "lugar_estadio.png", Color.valueOf("#4A8F7A"),
                "Las canchas están vacías, solo el señor que corta el césped.");
        return l;
    }
}
