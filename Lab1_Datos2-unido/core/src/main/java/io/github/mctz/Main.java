package io.github.mctz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Main extends ApplicationAdapter {

    private static final float ANCHO = 1100f;
    private static final float ALTO = 700f;

    // Tamaño base del juego
    private static final float ANCHO_JUEGO = 800f;
    private static final float ALTO_JUEGO = 600f;

    // Están ordenados para mayor comodidad los archivos
    private static final String ARCHIVO_FONDO = "FondoApp.png";
    private static final String ARCHIVO_INFLUENCER = "influencer.png";
    private static final String ARCHIVO_ADVERTENCIA = "advertencia.png";
    private static final String ARCHIVO_BARRILETE = "barriletefondo.png";

    // Celular, tamaño base y escala 
    private static final float ANCHO_CELULAR = 1122f;
    private static final float ALTO_CELULAR = 2046f;
    private static final float ESCALA_CELULAR = 1.25f;

    // Barra de caos
    // nota de sustentación: cada error baja 20%
    private static final float PERDIDA_POR_ERROR = 0.2f;
    private static final float ANCHO_BARRA = 300f;
    private static final float ALTO_BARRA = 20f;
    private static final float BARRA_X = 5f;
    private static final float BARRA_ARRIBA = 55f; 

    private Stage escenario;
    private FitViewport vistaMenu;
    private ExtendViewport vistaJuego;
    private Skin skin;
    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private Texture fondo;
    private Texture candidato;
    private Texture chica1;
    private Texture chica2;
    private Texture influencer;
    private Texture fondoApp;
    private Texture advertencia;
    private Texture barrilete;
    private SpriteBatch fondoBatch;

    //La barra está llena
    private float caos = 1f;
    private Image barraRelleno;
    private Group grupoCaos;
    private Texture texturaTelefono;

    private int rolActual = 0;
    private final String[] roles = {"CIUDADANO", "PERIODISTA", "INFLUENCER", "CANDIDATO A ALCALDE"};
    private boolean musicaActiva = true;

    private final Color AZUL_FONDO = Color.valueOf("#A9CCE3");
    private final Color AZUL_PANEL = Color.valueOf("#6A9FC3");
    private final Color DORADO = Color.valueOf("#D6B45A");
    private final Color CREMA = Color.valueOf("#F3E8C8");
    private final Color AZUL_CLARO = Color.valueOf("#80C7D9");
    private final Color ROJO = Color.valueOf("#D95C5C");
    private final Color FONDO_JUEGO = Color.valueOf("#223E9A");

    private Color colorFondo = AZUL_FONDO;

    // ---------------- Estado del juego ----------------
    private News[] bancoNoticias;
    private Nodo escenaActual;
    private News noticiaActual;

    private int turnosRestantes = TURNOS_DIA;
    private int conteoActividad = 0;
    private int conteoErrores = 0;
    private int conteoAciertos = 0;
    private boolean juegoTerminado = false;

    private Label titular;
    private Label lblTendencias;
    private StringBuilder tendenciasBuilder;
    private BitmapFont fuenteJuego;
    private BitmapFont fuenteTendencias;
    private TextButton btnVerificar;
    private TextButton btnCompartir;
    private TextButton btnIgnorar;

    // ---------------- Confiabilidad ----------------
    // Empieza en 100% y sube o baja según cómo decide el jugador
    private int confiabilidad = 100;
    private boolean noticiaVerificada = false;
    private Label lblConfiabilidad;

    // ---------------- Reloj del día ----------------
    // El día va de 8 a. m. a 8 p. m. Cada decisión gasta 1 hora y cada viaje
    // para verificar gasta 2, así que no alcanza para verificarlo todo.
    private static final int TURNOS_DIA = 6;
    private static final int HORA_INICIO = 8;
    private static final int HORA_FIN = 20;
    private static final int COSTO_DECISION = 1;
    private static final int COSTO_VIAJE = 2;       // ir, tomar la foto y volver
    private static final int COSTO_REINTENTO = 1;   // repetir la foto si salió movida
    private int hora = HORA_INICIO;
    private Label lblHora;
    private Label lblAviso;

    // ---------------- Lugares del pueblo ----------------
    private Lugar[] lugares;
    private Texture[] texturasLugares;
    private Table panelLugar;

    // ---------------- Mapa ilustrado (assets/mapa.png) ----------------
    // Medidas de la imagen original; las zonas están en píxeles de la imagen
    // medidos desde la esquina SUPERIOR izquierda: {x, y, ancho, alto}
    private static final float MAPA_ANCHO = 1586f;
    private static final float MAPA_ALTO = 992f;
    private static final int[][] ZONAS_MAPA = {
            {30, 238, 345, 202},    // PLAZA
            {400, 220, 380, 200},   // PARQUE
            {820, 180, 330, 245},   // ALCALDIA
            {1170, 185, 385, 240},  // HOSPITAL
            {60, 480, 345, 190},    // PUENTE
            {440, 440, 320, 200},   // TERMINAL
            {820, 435, 345, 210},   // REGISTRADURIA
            {1200, 435, 335, 215},  // SEDE_CAMPANA
            {70, 670, 310, 200},    // CENTRO_COMERCIAL
            {430, 660, 345, 160},   // BARRIO_SUR
            {850, 660, 290, 160},   // BIBLIOTECA
            {1195, 660, 340, 210}   // ESTADIO
    };
    private static final int[] ZONA_CASA = {655, 822, 265, 153};
    // Recuadro que tapa la hora y la noticia que vienen dibujadas en la imagen
    private static final int[] ZONA_INFO = {14, 14, 372, 220};
    // Ponlo en true para ver las zonas clicables en rojo y ajustarlas
    private static final boolean MOSTRAR_ZONAS = false;
    private Texture mapa;
    // Fondo de la casa (detrás del celular)
    private Texture fondoCasa;
    // Colores de los recuadros sobre las imágenes de los lugares
    private final Color MORADO = Color.valueOf("#5B2A86");
    private final Color PANEL_CLARO = new Color(1f, 0.96f, 0.9f, 0.9f);

    // ---------------- Minijuego de flechas (tomar la foto) ----------------
    private static final float ANCHO_BARRA_TIEMPO = 400f;
    private Texture[] texturasFlechas;   // 0 arriba, 1 abajo, 2 izquierda, 3 derecha
    private int[] secuencia;
    private Image[] imagenesSecuencia;
    private int posSecuencia;
    private boolean minijuegoActivo = false;
    private float tiempoMinijuego;
    private float tiempoMaxMinijuego;
    private Image barraTiempo;

    @Override
    public void create() {
        vistaMenu = new FitViewport(ANCHO, ALTO);
        vistaJuego = new ExtendViewport(ANCHO_JUEGO, ALTO_JUEGO);
        escenario = new Stage(vistaMenu);
        Gdx.input.setInputProcessor(escenario);
        fondoBatch = new SpriteBatch();

        tendenciasBuilder = new StringBuilder();

        cargarRecursos();
        cargarNoticias();

        mostrarMenu();
    }

    private void cargarRecursos() {
        skin = new Skin();
        fuente = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuente.getData().setScale(0.34f);
        fuenteGrande = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteGrande.getData().setScale(0.75f);
        skin.add("fuente", fuente);

        // Fuentes del juego (celular)
        fuenteJuego = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteJuego.getData().setScale(0.3f);
        skin.add("fuenteJuego", fuenteJuego);

        fuenteTendencias = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteTendencias.getData().setScale(0.14f);
        skin.add("fuenteTendencias", fuenteTendencias);

        fondo = texturaColor(AZUL_FONDO);
        skin.add("fondo", fondo);
        skin.add("panel", texturaColor(AZUL_PANEL));
        skin.add("dorado", texturaColor(DORADO));
        skin.add("boton", texturaColor(Color.valueOf("#7FB3D5")));
        skin.add("botonHover", texturaColor(Color.valueOf("#7FB3D5")));
        skin.add("rojo", texturaColor(ROJO));
        skin.add("botonJuego", texturaColor(Color.valueOf("#bc8fff")));

        candidato = cargarSiExiste("candidato.png");
        chica1 = cargarSiExiste("chica1.png");
        chica2 = cargarSiExiste("chica2.png");
        influencer = cargarSiExiste(ARCHIVO_INFLUENCER);
        fondoApp = cargarSiExiste(ARCHIVO_FONDO);
        advertencia = cargarSiExiste(ARCHIVO_ADVERTENCIA);
        barrilete = cargarSiExiste(ARCHIVO_BARRILETE);
        // Cuadro semitransparente ROSADO de la alerta (R, G, B, transparencia)
        skin.add("rosado", texturaColor(new Color(1f, 0.41f, 0.71f, 0.65f)));
        skin.add("blanco", texturaColor(Color.WHITE));
        skin.add("pista", texturaColor(Color.valueOf("#be00ab")));
        // Fondo claro de las tarjetas de la conciencia y del yo del futuro
        skin.add("fondoTarjeta", texturaColor(Color.valueOf("#E6EEEE")));
        texturaTelefono = cargarSiExiste("telefono.png");
        mapa = cargarSiExiste("mapa.png");
        fondoCasa = cargarSiExiste("casa.png");

        // Flechas del minijuego dibujadas con Pixmap (la fuente no trae flechas)
        texturasFlechas = new Texture[4];
        for (int i = 0; i < 4; i++) texturasFlechas[i] = crearFlecha(i);

        // Lugares: si existe la imagen en assets se usa, si no, un color de fondo
        lugares = Lugar.crearLugares();
        texturasLugares = new Texture[lugares.length];
        for (int i = 0; i < lugares.length; i++) {
            texturasLugares[i] = cargarSiExiste(lugares[i].archivoImagen);
        }
    }

    // Dibuja una flecha blanca de 64x64 (se colorea después con setColor)
    private Texture crearFlecha(int direccion) {
        Pixmap p = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        // Ojo: en Pixmap el eje Y va hacia abajo
        switch (direccion) {
            case 0: // arriba
                p.fillTriangle(32, 4, 4, 36, 60, 36);
                p.fillRectangle(22, 36, 20, 24);
                break;
            case 1: // abajo
                p.fillTriangle(32, 60, 4, 28, 60, 28);
                p.fillRectangle(22, 4, 20, 24);
                break;
            case 2: // izquierda
                p.fillTriangle(4, 32, 36, 4, 36, 60);
                p.fillRectangle(36, 22, 24, 20);
                break;
            default: // derecha
                p.fillTriangle(60, 32, 28, 4, 28, 60);
                p.fillRectangle(4, 22, 24, 20);
        }
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private Texture texturaColor(Color color) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(color);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private Texture cargarSiExiste(String nombre) {
        if (Gdx.files.internal(nombre).exists()) return new Texture(Gdx.files.internal(nombre));
        return null;
    }

    private Label.LabelStyle estiloTexto(Color color, float escala) {
        BitmapFont f = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        f.getData().setScale(escala);
        Label.LabelStyle s = new Label.LabelStyle(f, color);
        return s;
    }

    private TextButton.TextButtonStyle estiloBoton() {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.up = new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(skin.get("boton", Texture.class)));
        s.over = new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(skin.get("botonHover", Texture.class)));
        s.down = new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(skin.get("dorado", Texture.class)));
        s.font = fuente;
        s.fontColor = CREMA;
        s.overFontColor = Color.WHITE;
        s.downFontColor = AZUL_FONDO;
        return s;
    }

    private TextButton boton(String texto, ClickListener accion) {
        TextButton b = new TextButton(texto, estiloBoton());
        b.addListener(accion);
        return b;
    }

    // Botón con una flecha dibujada (la fuente no trae los caracteres de flecha)
    private TextButton botonFlecha(int direccion, ClickListener accion) {
        TextButton b = new TextButton("", estiloBoton());
        b.clearChildren();
        Image flecha = new Image(texturasFlechas[direccion]);
        flecha.setColor(CREMA);
        b.add(flecha).size(34, 34);
        b.addListener(accion);
        return b;
    }

    //Ajusta la ventana al cambiar entre menú y juego
    private void usarViewport(Viewport v) {
        if (escenario.getViewport() != v) {
            escenario.setViewport(v);
        }
        v.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void basePantalla() {
        basePantalla(vistaMenu);
    }

    //Limpia el escenario
    private void basePantalla(Viewport v) {
        usarViewport(v);
        colorFondo = (v == vistaJuego) ? FONDO_JUEGO : AZUL_FONDO;
        minijuegoActivo = false; // si se cambia de pantalla, el minijuego se detiene
        escenario.clear();
    }

    private TextButton.TextButtonStyle estiloBotonJuego() {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.up = skin.newDrawable("botonJuego");
        s.font = fuenteJuego;
        s.fontColor = Color.valueOf("#E6EEEE");
        return s;
    }

    // ================================================================
    // MENÚ PRINCIPAL
    // ================================================================
    private void mostrarMenu() {
        basePantalla();

        Table principal = new Table();
        principal.setFillParent(true);

        Label titulo = new Label("ALCALDE\nDIGITAL", estiloTexto(DORADO, 1.05f));
        titulo.setAlignment(Align.center);

        Label subtitulo = new Label("CIUDAD NOVA", estiloTexto(CREMA, 0.45f));
        subtitulo.setAlignment(Align.center);

        Table botones = new Table();
        botones.defaults().width(310).height(62).pad(7);
        botones.add(boton("JUGAR", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarSeleccionRol(); }
        })).row();
        botones.add(boton("OPCIONES", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarOpciones(); }
        })).row();
        botones.add(boton("AYUDA", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarAyuda(); }
        })).row();
        botones.add(boton("SALIR", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { Gdx.app.exit(); }
        })).row();

        principal.add(titulo).padTop(55).row();
        principal.add(subtitulo).padTop(4).row();
        principal.add(botones).padTop(35).row();
        principal.add(new Label("Una decisión responsable puede cambiar Ciudad Nova", estiloTexto(AZUL_CLARO, 0.28f))).padTop(20);

        escenario.addActor(principal);
    }

    // ================================================================
    // SELECCIÓN DE ROL
    // ================================================================
    private void mostrarSeleccionRol() {
        basePantalla();

        Table rootUI = new Table();
        rootUI.setFillParent(true);

        Label titulo = new Label("SELECCIONA TU ROL", estiloTexto(DORADO, 0.65f));
        titulo.setAlignment(Align.center);
        rootUI.add(titulo).colspan(3).padTop(30).row();

        Table tarjeta = new Table();
        tarjeta.background(new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(skin.get("panel", Texture.class))));
        tarjeta.pad(25);

        Table contenido = new Table();
        contenido.defaults().pad(8);

        // actualizarRol pone las dos flechas y el personaje del rol actual
        actualizarRol(contenido);

        tarjeta.add(contenido).row();

        TextButton confirmar = boton("CONFIRMAR ROL", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarSeleccionModo(); }
        });
        TextButton volver = boton("VOLVER", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarMenu(); }
        });

        tarjeta.add(confirmar).width(300).height(58).padTop(15).row();
        tarjeta.add(volver).width(300).height(48).padTop(5);

        rootUI.add(tarjeta).colspan(3).width(650).height(470).padTop(30);
        escenario.addActor(rootUI);
    }

    private void actualizarRol(Table contenido) {
        contenido.clearChildren();

        TextButton anterior = botonFlecha(2, new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                rolActual = (rolActual - 1 + roles.length) % roles.length;
                mostrarSeleccionRol();
            }
        });
        TextButton siguiente = botonFlecha(3, new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                rolActual = (rolActual + 1) % roles.length;
                mostrarSeleccionRol();
            }
        });

        Table centro = new Table();
        centro.defaults().pad(5);

        if (rolActual == 3 && candidato != null) {
            Image imagen = new Image(candidato);
            imagen.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            centro.add(imagen).width(300).height(210).row();
        } else if (rolActual == 0 && chica1 != null) {
            Image imagen = new Image(chica1);
            imagen.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            centro.add(imagen).width(220).height(210).row();
        } else if (rolActual == 1 && chica2 != null) {
            Image imagen = new Image(chica2);
            imagen.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            centro.add(imagen).width(220).height(210).row();
        } else if (rolActual == 2 && influencer != null) {
            Image imagen = new Image(influencer);
            imagen.setScaling(com.badlogic.gdx.utils.Scaling.fit);
            centro.add(imagen).width(220).height(210).row();
        } else {
            centro.add(new Label("◆", estiloTexto(DORADO, 1.0f))).height(210).row();
        }

        Label rol = new Label(roles[rolActual], estiloTexto(CREMA, 0.48f));
        rol.setAlignment(Align.center);
        centro.add(rol).row();

        String descripcion;
        switch (rolActual) {
            case 0: descripcion = "Interactúa responsablemente con la información."; break;
            case 1: descripcion = "Investiga y ayuda a identificar información confiable."; break;
            case 2: descripcion = "Tus publicaciones pueden llegar rápidamente a otros ciudadanos."; break;
            default: descripcion = "Construye confianza y enfrenta rumores o información falsa.";
        }
        Label desc = new Label(descripcion, estiloTexto(AZUL_CLARO, 0.25f));
        desc.setWrap(true);
        desc.setAlignment(Align.center);
        centro.add(desc).width(330).row();

        contenido.add(anterior).width(75).height(75);
        contenido.add(centro).width(430).height(330);
        contenido.add(siguiente).width(75).height(75);
    }

    // ================================================================
    // SELECCIÓN DE MODO
    // ================================================================
    private void mostrarSeleccionModo() {
        basePantalla();
        Table t = new Table();
        t.setFillParent(true);

        Label titulo = new Label("SELECCIONA EL MODO", estiloTexto(DORADO, 0.65f));
        titulo.setAlignment(Align.center);
        t.add(titulo).row();

        Label rol = new Label("ROL: " + roles[rolActual], estiloTexto(CREMA, 0.35f));
        rol.setAlignment(Align.center);
        t.add(rol).padTop(10).row();

        t.add(boton("PARTIDA LOCAL", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { iniciarPartida(); }
        })).width(350).height(70).padTop(35).row();

        t.add(new Label("La arquitectura cliente-servidor y el modo multijugador\nse integrarán en las siguientes etapas.",
                estiloTexto(AZUL_CLARO, 0.25f))).padTop(18).row();

        t.add(boton("VOLVER", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarSeleccionRol(); }
        })).width(300).height(55).padTop(25);

        escenario.addActor(t);
    }

    // ================================================================
    // AYUDA / OPCIONES
    // ================================================================
    private void mostrarAyuda() {
        basePantalla();
        Table t = new Table();
        t.setFillParent(true);

        Label titulo = new Label("AYUDA", estiloTexto(DORADO, 0.7f));
        titulo.setAlignment(Align.center);
        t.add(titulo).row();

        String texto = "OBJETIVO\n" +
                "Ayuda a Ciudad Nova a terminar el proceso electoral\n" +
                "con mayor información verificada, convivencia, confianza\n" +
                "y bienestar, evitando la desinformación y los conflictos.\n\n" +
                "ACCIONES\n" +
                "IR A VERIFICAR: ve al lugar de la noticia y toma una foto\n" +
                "pulsando las flechas en orden. Ir y volver toma 2 horas.\n" +
                "COMPARTIR: difunde la información (1 hora).\n" +
                "IGNORAR: evita interactuar con ella (1 hora).\n\n" +
                "El día va de 8 a. m. a 8 p. m.: no alcanza para verificarlo todo.\n" +
                "El árbol de noticias determina las consecuencias de las decisiones.";

        Label contenido = new Label(texto, estiloTexto(CREMA, 0.29f));
        contenido.setAlignment(Align.center);
        contenido.setWrap(true);
        t.add(contenido).width(760).padTop(25).row();
        t.add(boton("VOLVER", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarMenu(); }
        })).width(280).height(55).padTop(25);

        escenario.addActor(t);
    }

    private void mostrarOpciones() {
        basePantalla();
        Table t = new Table();
        t.setFillParent(true);

        Label titulo = new Label("OPCIONES", estiloTexto(DORADO, 0.7f));
        titulo.setAlignment(Align.center);
        t.add(titulo).row();

        TextButton musica = boton(musicaActiva ? "MÚSICA: ACTIVADA" : "MÚSICA: DESACTIVADA", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                musicaActiva = !musicaActiva;
                mostrarOpciones();
            }
        });
        t.add(musica).width(350).height(60).padTop(35).row();

        t.add(new Label("Modo de alto contraste: preparado para integrarse como componente inclusivo.",
                estiloTexto(AZUL_CLARO, 0.25f))).padTop(20).row();

        t.add(boton("VOLVER", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarMenu(); }
        })).width(280).height(55).padTop(30);

        escenario.addActor(t);
    }

    // ================================================================
    // PARTIDA (celular)
    // ================================================================
    private void iniciarPartida() {
        // Se reinicia todo el estado para que cada partida empiece limpia
        turnosRestantes = TURNOS_DIA;
        conteoActividad = 0;
        conteoErrores = 0;
        conteoAciertos = 0;
        juegoTerminado = false;
        caos = 1f;
        confiabilidad = 100;
        noticiaVerificada = false;
        hora = HORA_INICIO;
        minijuegoActivo = false;

        ArbolEscenas creadorEscenas = new ArbolEscenas();
        escenaActual = creadorEscenas.generarArbol();

        noticiaActual = bancoNoticias[MathUtils.random(0, bancoNoticias.length - 1)];

        mostrarPartida();
    }

    private void mostrarPartida() {
        basePantalla(vistaJuego);

        Label.LabelStyle estiloTextoJuego = new Label.LabelStyle();
        estiloTextoJuego.font = fuenteJuego;
        estiloTextoJuego.fontColor = Color.valueOf("#3baaa7");

        Label.LabelStyle estiloTendencias = new Label.LabelStyle();
        estiloTendencias.font = fuenteTendencias;
        estiloTendencias.fontColor = Color.valueOf("#8b9bb4");

        TextButton.TextButtonStyle estiloBotonJuego = estiloBotonJuego();

        titular = new Label(noticiaActual.titular, estiloTextoJuego);
        titular.setAlignment(Align.center);

        lblTendencias = new Label("", estiloTendencias);
        lblTendencias.setAlignment(Align.center);
        lblTendencias.setWrap(true);
        actualizarTendenciasRedes();

        lblConfiabilidad = new Label("", estiloTendencias);
        actualizarConfiabilidad(0);

        lblHora = new Label("", estiloTendencias);
        lblHora.setColor(Color.valueOf("#591D67"));

        lblAviso = new Label("", estiloTendencias);
        lblAviso.setColor(Color.valueOf("#DF0E0B"));
        lblAviso.setAlignment(Align.center);

        btnVerificar = new TextButton("Ir a verificar", estiloBotonJuego);
        btnCompartir = new TextButton("Compartir", estiloBotonJuego);
        btnIgnorar = new TextButton("Ignorar", estiloBotonJuego);

        Table raizUI = new Table();
        raizUI.setFillParent(true);
        Stack pilaCelular = new Stack();

        Table ui = new Table();
        ui.add(lblHora).colspan(2).padBottom(4).row();
        ui.add(lblConfiabilidad).colspan(2).padBottom(6).row();
        ui.add(lblTendencias).width(250).padBottom(10).colspan(2).row();
        ui.add(titular).padBottom(28).colspan(2).row();
        ui.add(btnVerificar).width(200).height(45).padBottom(13).colspan(2).row();
        ui.add(btnCompartir).width(130).height(45).padRight(8);
        ui.add(btnIgnorar).width(130).height(45).row();
        ui.add(lblAviso).colspan(2).width(280).padTop(8);

        if (texturaTelefono != null) {
            Image fondoCelular = new Image(texturaTelefono);
            fondoCelular.setScaling(Scaling.fit);
            pilaCelular.add(fondoCelular);
        }
        pilaCelular.add(ui);
        
        raizUI.add(pilaCelular)
                .width(ANCHO_CELULAR * ESCALA_CELULAR)
                .height(ALTO_CELULAR * ESCALA_CELULAR);
        escenario.addActor(raizUI);
        crearBarraCaos();
        refrescarCelular();

        btnVerificar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (juegoTerminado) {
                    mostrarMenu();
                    return;
                }
                if (noticiaActual == null || turnosRestantes <= 0) return;
                if (noticiaVerificada) {
                    lblAviso.setText("Ya verificaste esta noticia. Decide: compartir o ignorar.");
                    return;
                }
                if (!alcanzaTiempo(COSTO_VIAJE)) {
                    lblAviso.setText("No te alcanza el tiempo para ir y volver\nantes de que se acabe el día.");
                    return;
                }
                mostrarMapa();
            }
        });

        btnCompartir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                procesarTurno(true);
            }
        });

        btnIgnorar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                procesarTurno(false);
            }
        });
    }

    public void procesarTurno(boolean compartido) {
        if (turnosRestantes <= 0) return;

        boolean esFalsa = noticiaActual.esFalsa;
        boolean malaDecision = (compartido && esFalsa) || (!compartido && !esFalsa);
        // Se guarda antes de reiniciarlo para la siguiente noticia
        boolean laHabiaVerificado = noticiaVerificada;

        if (malaDecision) {
            conteoErrores++;
        } else {
            conteoAciertos++;
        }

        actualizarConfiabilidad(calcularCambioConfiabilidad(compartido, esFalsa, laHabiaVerificado));

        conteoActividad++;
        turnosRestantes--;
        hora += COSTO_DECISION;

        if (conteoActividad == 2) {

            if (conteoErrores > conteoAciertos) {
                if (escenaActual.nodoIzq != null) {
                    escenaActual = escenaActual.nodoIzq;
                }
            }
            else if (conteoAciertos > conteoErrores) {
                if (escenaActual.nodoDer != null) {
                    escenaActual = escenaActual.nodoDer;
                }
            }

            conteoActividad = 0;
            conteoErrores = 0;
            conteoAciertos = 0;

            actualizarTendenciasRedes();
        }

        if (turnosRestantes > 0) {
            noticiaVerificada = false;
            noticiaActual = bancoNoticias[MathUtils.random(0, bancoNoticias.length - 1)];
            refrescarCelular();
        } else {
            juegoTerminado = true;
            if (escenaActual != null) {
                titular.setText("FIN DEL DÍA\n\n" + escenaActual.actualNews.titular);
                titular.setColor(Color.valueOf("#591D67"));
                lblTendencias.setText("Tendencias finales consolidadas.");
            }
            // Al terminar, el botón de arriba pasa a ser "volver al menú"
            btnVerificar.setText("Menú");
            btnCompartir.setVisible(false);
            btnIgnorar.setVisible(false);
            lblHora.setText("Hora: " + formatoHora(hora) + "   |   Fin del día");
            lblAviso.setText("");
        }

        if (malaDecision) {
            caos = Math.max(0f, caos - PERDIDA_POR_ERROR);
            actualizarBarraCaos();
        }

        // Si era el último turno, al cerrar el aviso aparece el yo del futuro
        final boolean esUltimoTurno = juegoTerminado;
        Runnable despuesDelAviso = new Runnable() {
            @Override public void run() {
                if (esUltimoTurno) mostrarYoFuturo();
            }
        };

        if (compartido && esFalsa) {
            // Compartir algo falso es lo más grave: habla la conciencia
            mostrarConciencia(laHabiaVerificado, despuesDelAviso);
        } else if (malaDecision) {
            // Ignorar una noticia real: advertencia normal
            mostrarAdvertencia(despuesDelAviso);
        } else if (esUltimoTurno) {
            mostrarYoFuturo();
        }
    }

    // ================================================================
    // RELOJ DEL DÍA
    // ================================================================
    private String formatoHora(int h) {
        int h12 = (h > 12) ? h - 12 : h;
        return h12 + ":00 " + (h < 12 ? "a. m." : "p. m.");
    }

    // Revisa si una acción que cuesta "costo" horas cabe en el día, dejando
    // 1 hora reservada para decidir cada noticia que falta
    private boolean alcanzaTiempo(int costo) {
        return hora + costo <= HORA_FIN - turnosRestantes * COSTO_DECISION;
    }

    // Actualiza la pantalla del celular según el estado actual
    private void refrescarCelular() {
        int numero = TURNOS_DIA - turnosRestantes + 1;
        lblHora.setText("Hora: " + formatoHora(hora) + "   |   Noticia " + numero + " de " + TURNOS_DIA);
        lblAviso.setText("");
        if (noticiaVerificada) {
            if (noticiaActual.esFalsa) {
                titular.setText("VERIFICADA: ES FALSA\n\n" + noticiaActual.titular);
                titular.setColor(Color.valueOf("#DF0E0B"));
            } else {
                titular.setText("VERIFICADA: ES REAL\n\n" + noticiaActual.titular);
                titular.setColor(Color.valueOf("#2E8B57"));
            }
            btnVerificar.setText("Ya verificada");
        } else {
            titular.setText("NUEVA NOTIFICACIÓN\n\n" + noticiaActual.titular);
            titular.setColor(Color.valueOf("#3baaa7"));
            btnVerificar.setText("Ir a verificar");
        }
    }

    // ================================================================
    // MAPA DEL PUEBLO
    // ================================================================
    private void mostrarMapa() {
        if (mapa != null) {
            mostrarMapaIlustrado();
        } else {
            mostrarMapaBotones(); // respaldo si no está mapa.png
        }
    }

    private void mostrarMapaIlustrado() {
        basePantalla();

        // Todo el mapa va en un grupo del tamaño de la imagen original, escalado
        // para caber en la pantalla. Así las zonas se escriben en píxeles de la imagen.
        float escala = Math.min(ANCHO / MAPA_ANCHO, ALTO / MAPA_ALTO);
        Group grupoMapa = new Group();
        grupoMapa.setSize(MAPA_ANCHO, MAPA_ALTO);
        grupoMapa.setScale(escala);
        grupoMapa.setPosition((ANCHO - MAPA_ANCHO * escala) / 2f, (ALTO - MAPA_ALTO * escala) / 2f);

        Image imagenMapa = new Image(mapa);
        imagenMapa.setSize(MAPA_ANCHO, MAPA_ALTO);
        grupoMapa.addActor(imagenMapa);

        for (int i = 0; i < lugares.length; i++) {
            final int indice = i;
            grupoMapa.addActor(crearZona(ZONAS_MAPA[i], new Runnable() {
                @Override public void run() { viajarA(indice); }
            }));
        }
        grupoMapa.addActor(crearZona(ZONA_CASA, new Runnable() {
            @Override public void run() { mostrarPartida(); }
        }));

        // Recuadro con la hora y la noticia reales (tapa el texto fijo de la imagen)
        int[] z = ZONA_INFO;
        Image borde = new Image(skin.newDrawable("blanco", Color.valueOf("#E85FA8")));
        borde.setBounds(z[0], MAPA_ALTO - z[1] - z[3], z[2], z[3]);
        borde.setTouchable(Touchable.disabled);
        grupoMapa.addActor(borde);

        Table info = new Table();
        info.setBackground(skin.newDrawable("blanco", Color.valueOf("#FFF4E6")));
        info.setBounds(z[0] + 6, MAPA_ALTO - z[1] - z[3] + 6, z[2] - 12, z[3] - 12);
        info.pad(10).top().left();
        info.setTouchable(Touchable.disabled);
        Color morado = Color.valueOf("#5B2A86");
        info.add(new Label("Hora: " + formatoHora(hora), estiloTexto(morado, 0.42f))).left().row();
        info.add(new Label("Ir, tomar la foto y volver toma " + COSTO_VIAJE + " horas",
                estiloTexto(morado, 0.3f))).left().padBottom(8).row();
        info.add(new Label("La noticia:", estiloTexto(Color.valueOf("#C2185B"), 0.32f))).left().row();
        Label noticia = new Label(noticiaActual.titular.replace("\n", " "), estiloTexto(morado, 0.3f));
        noticia.setWrap(true);
        info.add(noticia).width(z[2] - 32).left();
        grupoMapa.addActor(info);

        escenario.addActor(grupoMapa);
    }

    // Zona invisible y clicable sobre un edificio; se ilumina al pasar el mouse
    private Image crearZona(int[] z, final Runnable accion) {
        final Color normal = MOSTRAR_ZONAS ? new Color(1, 0, 0, 0.3f) : new Color(1, 1, 1, 0f);
        final Color encima = new Color(1, 1, 1, 0.25f);
        final Image zona = new Image(skin.newDrawable("blanco"));
        zona.setColor(normal);
        // La imagen mide Y desde arriba; libGDX la mide desde abajo
        zona.setBounds(z[0], MAPA_ALTO - z[1] - z[3], z[2], z[3]);
        zona.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { accion.run(); }
            @Override public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                zona.setColor(encima);
            }
            @Override public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                zona.setColor(normal);
            }
        });
        return zona;
    }

    private void mostrarMapaBotones() {
        basePantalla();
        Table t = new Table();
        t.setFillParent(true);

        Label titulo = new Label("¿A DÓNDE VAS?", estiloTexto(DORADO, 0.6f));
        titulo.setAlignment(Align.center);
        t.add(titulo).row();

        Label info = new Label("Hora: " + formatoHora(hora) + "   |   Ir, tomar la foto y volver toma "
                + COSTO_VIAJE + " horas", estiloTexto(CREMA, 0.27f));
        info.setAlignment(Align.center);
        t.add(info).padTop(6).row();

        Label recordatorio = new Label("La noticia: " + noticiaActual.titular.replace("\n", " "),
                estiloTexto(AZUL_CLARO, 0.25f));
        recordatorio.setWrap(true);
        recordatorio.setAlignment(Align.center);
        t.add(recordatorio).width(820).padTop(10).row();

        Table rejilla = new Table();
        rejilla.defaults().width(240).height(58).pad(6);
        for (int i = 0; i < lugares.length; i++) {
            final int indice = i;
            rejilla.add(boton(lugares[i].nombre, new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { viajarA(indice); }
            }));
            if (i % 4 == 3) rejilla.row();
        }
        t.add(rejilla).padTop(18).row();

        t.add(boton("VOLVER A CASA", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarPartida(); }
        })).width(300).height(52).padTop(18);

        escenario.addActor(t);
    }

    private void viajarA(int indiceLugar) {
        hora += COSTO_VIAJE;
        mostrarLugar(indiceLugar);
    }

    // ================================================================
    // ESCENA DEL LUGAR
    // ================================================================
    private void mostrarLugar(int indiceLugar) {
        basePantalla();
        minijuegoActivo = false;
        Lugar lugar = lugares[indiceLugar];

        Stack pila = new Stack();
        pila.setFillParent(true);

        Image fondoLugar;
        if (texturasLugares[indiceLugar] != null) {
            fondoLugar = new Image(texturasLugares[indiceLugar]);
            fondoLugar.setScaling(Scaling.fill);
        } else {
            fondoLugar = new Image(skin.newDrawable("blanco", lugar.color));
            fondoLugar.setScaling(Scaling.stretch);
        }
        pila.add(fondoLugar);

        Table contenido = new Table();

        // Arriba: nombre del lugar y hora, en una franja clara
        Table franja = new Table();
        franja.setBackground(skin.newDrawable("blanco", PANEL_CLARO));
        franja.pad(8, 30, 8, 30);
        Label titulo = new Label(lugar.nombre, estiloTexto(MORADO, 0.55f));
        franja.add(titulo).row();
        franja.add(new Label("Hora: " + formatoHora(hora), estiloTexto(MORADO, 0.26f)));
        contenido.add(franja).expandY().top().padTop(14).row();

        // Abajo: el recuadro con el texto, el minijuego y los botones,
        // para que el edificio de la imagen quede despejado en el centro
        panelLugar = new Table();
        panelLugar.setBackground(skin.newDrawable("blanco", PANEL_CLARO));
        panelLugar.pad(18, 25, 18, 25);
        contenido.add(panelLugar).width(820).bottom().padBottom(18);
        pila.add(contenido);
        escenario.addActor(pila);

        // Las teclas del minijuego llegan al escenario (se borra con escenario.clear())
        escenario.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (minijuegoActivo) teclaMinijuego(keycode);
                return false;
            }
        });

        if (noticiaActual.lugar == indiceLugar) {
            mostrarInicioFoto();
        } else {
            mostrarLugarEquivocado(lugar);
        }
    }

    private Label textoPanel(String texto) {
        Label l = new Label(texto, estiloTexto(MORADO, 0.3f));
        l.setWrap(true);
        l.setAlignment(Align.center);
        return l;
    }

    private TextButton botonVolverCasa() {
        return boton("VOLVER A CASA", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarPartida(); }
        });
    }

    private void mostrarLugarEquivocado(Lugar lugar) {
        panelLugar.clear();
        panelLugar.add(textoPanel(lugar.descripcion
                + "\n\nAquí no está pasando nada de lo que dice la noticia."
                + "\nParece que no era el lugar correcto.")).width(760).row();
        panelLugar.add(botonVolverCasa()).width(300).height(55).padTop(20);
    }

    private void mostrarInicioFoto() {
        panelLugar.clear();
        panelLugar.add(textoPanel("Aquí es donde está pasando todo. Toma una foto como prueba:"
                + "\npulsa las flechas del teclado (o WASD) en el orden en que aparecen"
                + "\nantes de que se acabe el tiempo.")).width(760).row();
        panelLugar.add(boton("TOMAR FOTO", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { iniciarMinijuego(); }
        })).width(300).height(60).padTop(20);
    }

    // ================================================================
    // MINIJUEGO DE FLECHAS
    // ================================================================
    private void iniciarMinijuego() {
        panelLugar.clear();

        // El periodista sabe investigar: secuencia más corta y más tiempo
        boolean esPeriodista = (rolActual == 1);
        int largo = esPeriodista ? 4 : 5;
        tiempoMaxMinijuego = esPeriodista ? 6f : 5f;

        secuencia = new int[largo];
        imagenesSecuencia = new Image[largo];
        Table fila = new Table();
        for (int i = 0; i < largo; i++) {
            secuencia[i] = MathUtils.random(0, 3);
            Image img = new Image(texturasFlechas[secuencia[i]]);
            img.setColor(MORADO);
            imagenesSecuencia[i] = img;
            fila.add(img).size(72, 72).pad(8);
        }

        panelLugar.add(textoPanel("¡Toma la foto! Pulsa las flechas en orden")).width(760).padBottom(12).row();
        panelLugar.add(fila).padBottom(16).row();

        Group grupoTiempo = new Group();
        grupoTiempo.setSize(ANCHO_BARRA_TIEMPO, 16);
        Image pistaTiempo = new Image(skin.get("pista", Texture.class));
        pistaTiempo.setSize(ANCHO_BARRA_TIEMPO, 16);
        grupoTiempo.addActor(pistaTiempo);
        barraTiempo = new Image(skin.newDrawable("blanco", Color.valueOf("#4CAF50")));
        barraTiempo.setSize(ANCHO_BARRA_TIEMPO, 16);
        grupoTiempo.addActor(barraTiempo);
        panelLugar.add(grupoTiempo).size(ANCHO_BARRA_TIEMPO, 16);

        posSecuencia = 0;
        tiempoMinijuego = tiempoMaxMinijuego;
        escenario.setKeyboardFocus(null);
        minijuegoActivo = true;
    }

    private void teclaMinijuego(int keycode) {
        int direccion;
        switch (keycode) {
            case Input.Keys.UP: case Input.Keys.W: direccion = 0; break;
            case Input.Keys.DOWN: case Input.Keys.S: direccion = 1; break;
            case Input.Keys.LEFT: case Input.Keys.A: direccion = 2; break;
            case Input.Keys.RIGHT: case Input.Keys.D: direccion = 3; break;
            default: return; // otras teclas no cuentan
        }

        if (direccion == secuencia[posSecuencia]) {
            imagenesSecuencia[posSecuencia].setColor(Color.valueOf("#4CAF50"));
            posSecuencia++;
            if (posSecuencia == secuencia.length) terminarMinijuego(true);
        } else {
            imagenesSecuencia[posSecuencia].setColor(ROJO);
            terminarMinijuego(false);
        }
    }

    // Se llama en cada frame desde render()
    private void actualizarMinijuego(float delta) {
        tiempoMinijuego -= delta;
        float fraccion = Math.max(0f, tiempoMinijuego / tiempoMaxMinijuego);
        barraTiempo.setWidth(ANCHO_BARRA_TIEMPO * fraccion);
        if (fraccion < 0.3f) barraTiempo.setColor(ROJO);
        if (tiempoMinijuego <= 0) terminarMinijuego(false);
    }

    private void terminarMinijuego(boolean exito) {
        minijuegoActivo = false;
        panelLugar.clear();

        if (exito) {
            noticiaVerificada = true;
            String resultado = noticiaActual.esFalsa
                    ? "¡FOTO TOMADA!\n\nLo que ves aquí no coincide con la noticia.\nLa noticia es FALSA."
                    : "¡FOTO TOMADA!\n\nLo que dice la noticia está pasando de verdad.\nLa noticia es REAL.";
            panelLugar.add(textoPanel(resultado)).width(760).row();
            panelLugar.add(botonVolverCasa()).width(300).height(55).padTop(20);
            return;
        }

        panelLugar.add(textoPanel("La foto salió movida y no sirve como prueba.")).width(760).row();
        Table botones = new Table();
        botones.defaults().width(320).height(55).pad(8);
        if (alcanzaTiempo(COSTO_REINTENTO)) {
            botones.add(boton("REPETIR FOTO (+" + COSTO_REINTENTO + " HORA)", new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    hora += COSTO_REINTENTO;
                    iniciarMinijuego();
                }
            }));
        }
        botones.add(botonVolverCasa());
        panelLugar.add(botones).padTop(16);
    }

    // ================================================================
    // CONFIABILIDAD
    // ================================================================
    private int calcularCambioConfiabilidad(boolean compartido, boolean esFalsa, boolean verificada) {
        if (compartido && esFalsa) {
            return verificada ? -30 : -20;  // verificada y aun así la compartió: peor
        }
        if (compartido && !esFalsa) {
            return verificada ? +5 : -5;    // acertó, pero sin comprobar
        }
        if (!compartido && esFalsa) {
            return verificada ? +10 : +5;
        }
        return -5;                          // ignoró una noticia real
    }

    private void actualizarConfiabilidad(int cambio) {
        confiabilidad = MathUtils.clamp(confiabilidad + cambio, 0, 100);
        if (lblConfiabilidad == null) return;
        lblConfiabilidad.setText("Confiabilidad: " + confiabilidad + "%");
        if (confiabilidad >= 70) {
            lblConfiabilidad.setColor(Color.valueOf("#3baaa7"));
        } else if (confiabilidad >= 40) {
            lblConfiabilidad.setColor(Color.valueOf("#E0A100"));
        } else {
            lblConfiabilidad.setColor(Color.valueOf("#DF0E0B"));
        }
    }

    //Barra de caos en la esquina superior izquierda 
    private void crearBarraCaos() {
        grupoCaos = new Group();

        Image pista = new Image(skin.get("pista", Texture.class));
        pista.setSize(ANCHO_BARRA, ALTO_BARRA);
        grupoCaos.addActor(pista);

        Texture texRelleno = (barrilete != null) ? barrilete : skin.get("blanco", Texture.class);
        barraRelleno = new Image(texRelleno);
        barraRelleno.setSize(ANCHO_BARRA * caos, ALTO_BARRA);
        if (barrilete == null) barraRelleno.setColor(colorCaos());
        grupoCaos.addActor(barraRelleno);

        Label.LabelStyle estilo = new Label.LabelStyle(fuenteJuego, Color.WHITE);
        Label etiqueta = new Label("CAOS", estilo);
        etiqueta.setPosition(0, ALTO_BARRA + 4);
        grupoCaos.addActor(etiqueta);

        escenario.addActor(grupoCaos);
        posicionarBarra();
    }

    private void posicionarBarra() {
        if (grupoCaos != null) {
            grupoCaos.setPosition(BARRA_X, escenario.getHeight() - BARRA_ARRIBA);
        }
    }

    private void actualizarBarraCaos() {
        if (barraRelleno == null) return;
        barraRelleno.clearActions();
        if (barrilete != null) {
            barraRelleno.addAction(Actions.sizeTo(ANCHO_BARRA * caos, ALTO_BARRA, 0.6f, Interpolation.smooth));
        } else {
            barraRelleno.addAction(Actions.parallel(
                    Actions.sizeTo(ANCHO_BARRA * caos, ALTO_BARRA, 0.6f, Interpolation.smooth),
                    Actions.color(colorCaos(), 0.6f)));
        }
    }

    private Color colorCaos() {
        if (caos > 0.6f) return Color.valueOf("#4CAF50");
        if (caos > 0.3f) return Color.valueOf("#F2C94C");
        return Color.valueOf("#DF0E0B");
    }

    private void dibujarFondo() {
        // En la pantalla del celular se ve la casa de fondo; en el resto, FondoApp
        Texture fondoApp = (escenario.getViewport() == vistaJuego && fondoCasa != null)
                ? fondoCasa : this.fondoApp;
        if (fondoApp == null) return;
        int w = Gdx.graphics.getBackBufferWidth();
        int h = Gdx.graphics.getBackBufferHeight();
        Gdx.gl.glViewport(0, 0, w, h);

        float escala = Math.max((float) w / fondoApp.getWidth(), (float) h / fondoApp.getHeight());
        float dw = fondoApp.getWidth() * escala;
        float dh = fondoApp.getHeight() * escala;

        fondoBatch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
        fondoBatch.begin();
        fondoBatch.draw(fondoApp, (w - dw) / 2f, (h - dh) / 2f, dw, dh);
        fondoBatch.end();

        escenario.getViewport().apply(); 
    }

    private void mostrarAdvertencia(final Runnable alCerrar) {
        final Table overlay = new Table();
        overlay.setFillParent(true); 
        overlay.setTouchable(Touchable.enabled); 
        overlay.background(skin.newDrawable("rosado"));

        if (advertencia != null) {
            Image img = new Image(advertencia);
            img.setScaling(Scaling.fit);
            overlay.add(img).width(260).height(260).row();
        }

        Label.LabelStyle estilo = new Label.LabelStyle(fuenteJuego, Color.WHITE);
        Label mensaje = new Label("¡CUIDADO!\nEsa decisión no fue la mejor.\nPiensa bien antes de elegir.", estilo);
        mensaje.setAlignment(Align.center);
        overlay.add(mensaje).padTop(10).row();

        TextButton entendido = new TextButton("Entendido", estiloBotonJuego());
        entendido.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
                if (alCerrar != null) alCerrar.run();
            }
        });
        overlay.add(entendido).width(160).height(45).padTop(15);

        escenario.addActor(overlay);
    }

    // Tarjeta con personaje + texto + botones, sobre un fondo oscuro que bloquea el juego
    private Table crearTarjetaPersonaje(Texture personaje, float tamImagen, String texto,
                                        float anchoTexto, float opacidad, TextButton... botones) {
        Label.LabelStyle estilo = new Label.LabelStyle(fuenteJuego, Color.valueOf("#3baaa7"));

        Table tarjeta = new Table();
        tarjeta.setBackground(skin.newDrawable("fondoTarjeta"));
        tarjeta.pad(20);

        if (personaje != null) {
            Image img = new Image(personaje);
            img.setScaling(Scaling.fit);
            tarjeta.add(img).size(tamImagen, tamImagen).padBottom(10).row();
        }

        Label lbl = new Label(texto, estilo);
        lbl.setWrap(true);
        lbl.setAlignment(Align.center);
        tarjeta.add(lbl).width(anchoTexto).padBottom(15).row();

        Table filaBotones = new Table();
        for (TextButton b : botones) {
            filaBotones.add(b).width(160).height(45).pad(0, 5, 0, 5);
        }
        tarjeta.add(filaBotones);

        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(skin.newDrawable("blanco", new Color(0, 0, 0, opacidad)));
        overlay.setTouchable(Touchable.enabled); // bloquea los botones de atrás
        overlay.add(tarjeta);
        return overlay;
    }

    // ================================================================
    // PERSONAJE DE CONCIENCIA
    // ================================================================
    private void mostrarConciencia(boolean laHabiaVerificado, final Runnable alCerrar) {
        String mensaje;
        if (laHabiaVerificado) {
            mensaje = "TU CONCIENCIA:\n"
                    + "¡La verificaste y sabías que era falsa,\n"
                    + "y aun así la compartiste!\n\n"
                    + "Eso ya no es un descuido: es\n"
                    + "desinformar a propósito.";
        } else {
            mensaje = "TU CONCIENCIA:\n"
                    + "¡Acabas de compartir información FALSA!\n\n"
                    + "Eso no se debe hacer: las mentiras viajan\n"
                    + "más rápido que la verdad y dañan a otros.\n\n"
                    + "Antes de compartir:\n"
                    + "1. Revisa quién la publicó\n"
                    + "2. Búscala en otro medio confiable\n"
                    + "3. Ve al lugar y verifícala\n"
                    + "4. Si dudas, no la compartas";
        }

        TextButton entendido = new TextButton("Entendido", estiloBotonJuego());
        final Table panel = crearTarjetaPersonaje(chica1, 150, mensaje, 480, 0.7f, entendido);
        entendido.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                panel.remove();
                if (alCerrar != null) alCerrar.run();
            }
        });
        escenario.addActor(panel);
    }

    // ================================================================
    // YO DEL FUTURO (final según la confiabilidad)
    // ================================================================
    private void mostrarYoFuturo() {
        String historia;

        if (confiabilidad >= 80) {
            historia = "TU YO DEL FUTURO (diez años después)\n\n"
                    + "Hoy, cuando hablo, la ciudad entera guarda silencio. "
                    + "Mi abuela siempre decía que la verdad tarda, pero llega, "
                    + "y tenía razón.\n\n"
                    + "Hasta mi peor enemigo, el candidato de la oposición, "
                    + "vino a pedirme perdón llorando bajo la lluvia. "
                    + "Yo solo le respondí, mirando el atardecer: "
                    + "\"Verifiqué antes de compartir\".";
        } else if (confiabilidad >= 50) {
            historia = "TU YO DEL FUTURO (diez años después)\n\n"
                    + "Tengo amigos fieles, pero algunos me miran con una duda "
                    + "que quema. Una vez compartí un rumor sin comprobarlo y "
                    + "alguien inocente pagó las consecuencias.\n\n"
                    + "Desde esa noche no he vuelto a dormir en paz. "
                    + "Cada madrugada me repito frente al espejo: "
                    + "\"La próxima vez, verificar primero\".";
        } else if (confiabilidad >= 20) {
            historia = "TU YO DEL FUTURO (diez años después)\n\n"
                    + "Mi nombre ya no vale nada. Mi mejor amigo descubrió que "
                    + "fui yo quien difundió aquel falso escándalo y me gritó "
                    + "en medio de una tormenta: \"¡Me engañaste!\"\n\n"
                    + "Ahora publico la verdad y nadie me cree. "
                    + "Así de caro cuesta una mentira compartida.";
        } else {
            historia = "TU YO DEL FUTURO (diez años después)\n\n"
                    + "Anoche me desenmascararon en televisión nacional. "
                    + "\"Ella difundió el falso toque de queda\", dijo el "
                    + "presentador, y la ciudad entera lo escuchó.\n\n"
                    + "Mi propia madre apagó el televisor sin mirarme. "
                    + "Lo perdí todo por un simple clic. "
                    + "Si pudiera volver atrás... ¿verificarías tú?";
        }

        historia += "\n\nRol: " + roles[rolActual] + "   |   Confiabilidad final: " + confiabilidad + "%";

        TextButton menu = new TextButton("Menú", estiloBotonJuego());
        TextButton salir = new TextButton("Salir", estiloBotonJuego());
        final Table panel = crearTarjetaPersonaje(chica2, 130, historia, 500, 0.8f, menu, salir);

        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { mostrarMenu(); }
        });
        salir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { Gdx.app.exit(); }
        });
        escenario.addActor(panel);
    }

    public void actualizarTendenciasRedes() {
        if (escenaActual == null) return;

        tendenciasBuilder.setLength(0);
        tendenciasBuilder.append("Trending:\n");

        recolectarTendenciasInOrden(escenaActual);

        lblTendencias.setText(tendenciasBuilder.toString());
    }

    //--recorrido inorden--------------------------------------------------------------------------------------
    private void recolectarTendenciasInOrden(Nodo nodo) {
        if (nodo == null) return;

        recolectarTendenciasInOrden(nodo.nodoIzq);

        String[] lineas = nodo.actualNews.titular.split("\n");
        if (lineas.length > 0) {
            String hashtag = lineas[0].replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ]", "");
            if (!hashtag.isEmpty()) {
                tendenciasBuilder.append("#").append(hashtag).append("  ");
            }
        }

        recolectarTendenciasInOrden(nodo.nodoDer);
    }

    // ================================================================
    // BANCO DE NOTICIAS Y UTILIDADES DE ÁRBOL
    // ================================================================
    private void cargarNoticias() {
        bancoNoticias = new News[30];
        bancoNoticias[0] = new News("ULTIMA HORA:\nEl alcalde venderá el parque\na un casino internacional.", true, "General", Lugar.PARQUE);
        bancoNoticias[1] = new News("SALUD:\nInicia jornada de vacunación\nen la plaza central.", false, "Salud", Lugar.PLAZA);
        bancoNoticias[2] = new News("POLÍTICA:\nCandidato opositor fue grabado recibiendo\nsobornos en su sede de campaña.", true, "Política", Lugar.SEDE_CAMPANA);
        bancoNoticias[3] = new News("SEGURIDAD:\nNuevo sistema de 50 cámaras\ninstalado en el centro comercial.", false, "Seguridad", Lugar.CENTRO_COMERCIAL);
        bancoNoticias[4] = new News("RUMOR:\nEl agua potable del barrio sur\nestá contaminada con plomo.", true, "General", Lugar.BARRIO_SUR);
        bancoNoticias[5] = new News("ECONOMÍA:\nLa alcaldía subirá los impuestos\nprediales un 50% el próximo mes.", true, "Economía", Lugar.ALCALDIA);
        bancoNoticias[6] = new News("CULTURA:\nEl festival de verano en la plaza\nconfirma fechas oficiales.", false, "Cultura", Lugar.PLAZA);
        bancoNoticias[7] = new News("TRÁNSITO:\nEl puente principal colapsará\nsi no se cierra hoy mismo.", true, "Tránsito", Lugar.PUENTE);
        bancoNoticias[8] = new News("MEDIO AMBIENTE:\nSe sembrarán 500 árboles\na orillas del río.", false, "Medio Ambiente", Lugar.BARRIO_SUR);
        bancoNoticias[9] = new News("ESCÁNDALO:\nEl alcalde usó fondos públicos\npara remodelar su mansión.", true, "Política", Lugar.ALCALDIA);
        bancoNoticias[10] = new News("TECNOLOGÍA:\nHabrá WiFi gratuito en\nla terminal de buses.", false, "Tecnología", Lugar.TERMINAL);
        bancoNoticias[11] = new News("URGENTE:\nLa alcaldía decreta toque de queda\npor supuestas protestas armadas.", true, "General", Lugar.ALCALDIA);
        bancoNoticias[12] = new News("DEPORTES:\nEl equipo local clasifica a la final;\nhoy celebran en el estadio.", false, "Deportes", Lugar.ESTADIO);
        bancoNoticias[13] = new News("FRAUDE:\nLas máquinas de votación de la\nregistraduría ya están hackeadas.", true, "General", Lugar.REGISTRADURIA);
        bancoNoticias[14] = new News("SALUD:\nEl hospital está al borde del colapso\npor un virus desconocido.", true, "Salud", Lugar.HOSPITAL);
        bancoNoticias[15] = new News("SOCIAL:\nAbren nuevo comedor\ncomunitario en el barrio sur.", false, "Social", Lugar.BARRIO_SUR);
        bancoNoticias[16] = new News("ALERTA:\nLa alcaldía avisa que se avecina\nuna tormenta eléctrica severa.", false, "General", Lugar.ALCALDIA);
        bancoNoticias[17] = new News("POLICÍA:\nCapturan a la banda de ladrones\ndel centro comercial.", false, "Política", Lugar.CENTRO_COMERCIAL);
        bancoNoticias[18] = new News("RUMOR:\nEl candidato de la oposición es un\nactor contratado, dicen en su sede.", true, "General", Lugar.SEDE_CAMPANA);
        bancoNoticias[19] = new News("EDUCACIÓN:\nInauguran biblioteca pública\ncon 10,000 libros nuevos.", false, "Educación", Lugar.BIBLIOTECA);
        bancoNoticias[20] = new News("ECONOMÍA:\nEl supermercado del centro comercial\nse quedará sin comida por la huelga.", true, "Economía", Lugar.CENTRO_COMERCIAL);
        bancoNoticias[21] = new News("EMPLEO:\nFeria laboral en la plaza central\nofrece más de 1000 vacantes hoy.", false, "Empleo", Lugar.PLAZA);
        bancoNoticias[22] = new News("POLÍTICA:\nLa registraduría suspende las\nelecciones hasta el próximo año.", true, "Política", Lugar.REGISTRADURIA);
        bancoNoticias[23] = new News("INFRAESTRUCTURA:\nReparación del puente principal\niniciará el lunes a las 6 AM.", false, "Infraestructura", Lugar.PUENTE);
        bancoNoticias[24] = new News("ESCÁNDALO:\nDocumentos prueban que\nel parque ya fue vendido.", true, "General", Lugar.PARQUE);
        bancoNoticias[25] = new News("SEGURIDAD:\nExtraterrestres fueron vistos\nsobrevolando la alcaldía.", true, "Seguridad", Lugar.ALCALDIA);
        bancoNoticias[26] = new News("CULTURA:\nCine al aire libre gratuito\neste viernes en la plaza.", false, "Cultura", Lugar.PLAZA);
        bancoNoticias[27] = new News("URGENTE:\nDesde hoy los buses de la terminal\nserán gratuitos para siempre.", true, "General", Lugar.TERMINAL);
        bancoNoticias[28] = new News("COMUNIDAD:\nVecinos organizan jornada\nde limpieza en el río.", false, "Social", Lugar.BARRIO_SUR);
        bancoNoticias[29] = new News("ELECCIONES:\nÚltimo día en la registraduría\npara inscribir tu cédula.", false, "Política", Lugar.REGISTRADURIA);
    }

    public int altura(Nodo nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(altura(nodo.nodoIzq), altura(nodo.nodoDer));
    }

    public int factorEquilibrio(Nodo nodo) {
        if (nodo == null) return 0;
        return altura(nodo.nodoIzq) - altura(nodo.nodoDer);
    }

    public void inOrden(Nodo nodito) {
        if (nodito == null) return;
        inOrden(nodito.nodoIzq);
        System.out.println(nodito.actualNews.titular + ", ");
        inOrden(nodito.nodoDer);
    }

    public int rumboJuego(int fe) {
        if (fe > 0) return MathUtils.random(0, 5);
        if (fe < 0) return MathUtils.random(6, 10);
        return 11;
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            mostrarMenu();
        }
        if (minijuegoActivo) actualizarMinijuego(Gdx.graphics.getDeltaTime());
        ScreenUtils.clear(colorFondo);
        dibujarFondo();
        escenario.act(Gdx.graphics.getDeltaTime());
        escenario.draw();
    }

    @Override
    public void resize(int width, int height) {
        escenario.getViewport().update(width, height, true);
        posicionarBarra(); 
    }

    @Override
    public void dispose() {
        if (escenario != null) escenario.dispose();
        if (skin != null) skin.dispose();
        if (fondo != null) fondo.dispose();
        if (candidato != null) candidato.dispose();
        if (chica1 != null) chica1.dispose();
        if (chica2 != null) chica2.dispose();
        if (texturaTelefono != null) texturaTelefono.dispose();
        if (influencer != null) influencer.dispose();
        if (fondoApp != null) fondoApp.dispose();
        if (advertencia != null) advertencia.dispose();
        if (fondoBatch != null) fondoBatch.dispose();
        if (barrilete != null) barrilete.dispose();
        if (mapa != null) mapa.dispose();
        if (fondoCasa != null) fondoCasa.dispose();
        if (texturasFlechas != null) {
            for (Texture t : texturasFlechas) if (t != null) t.dispose();
        }
        if (texturasLugares != null) {
            for (Texture t : texturasLugares) if (t != null) t.dispose();
        }
    }
}
