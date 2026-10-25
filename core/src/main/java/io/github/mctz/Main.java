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

    private int turnosRestantes = 6;
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
        texturaTelefono = cargarSiExiste("telefono.png");
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

        TextButton anterior = boton("◀", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                rolActual = (rolActual - 1 + roles.length) % roles.length;
                actualizarRol(contenido);
            }
        });
        TextButton siguiente = boton("▶", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                rolActual = (rolActual + 1) % roles.length;
                actualizarRol(contenido);
            }
        });

        contenido.add(anterior).width(75).height(75);
        actualizarRol(contenido);
        contenido.add(siguiente).width(75).height(75);

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

        TextButton anterior = boton("◀", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                rolActual = (rolActual - 1 + roles.length) % roles.length;
                mostrarSeleccionRol();
            }
        });
        TextButton siguiente = boton("▶", new ClickListener() {
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
                "VERIFICAR: comprueba la publicación.\n" +
                "COMPARTIR: difunde la información.\n" +
                "IGNORAR: evita interactuar con ella.\n\n" +
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
        turnosRestantes = 6;
        conteoActividad = 0;
        conteoErrores = 0;
        conteoAciertos = 0;
        juegoTerminado = false;
        caos = 1f;

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

        btnVerificar = new TextButton("Verificar", estiloBotonJuego);
        btnCompartir = new TextButton("Compartir", estiloBotonJuego);
        btnIgnorar = new TextButton("Ignorar", estiloBotonJuego);

        Table raizUI = new Table();
        raizUI.setFillParent(true);
        Stack pilaCelular = new Stack();

        Table ui = new Table();
        ui.add(lblTendencias).width(250).padBottom(10).colspan(2).row();
        ui.add(titular).padBottom(28).colspan(2).row();
        ui.add(btnVerificar).width(140).height(45).padBottom(13).colspan(2).row();
        ui.add(btnCompartir).width(130).height(45).padRight(8);
        ui.add(btnIgnorar).width(130).height(45);

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

        btnVerificar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (juegoTerminado) {
                    mostrarMenu();
                    return;
                }
                if (noticiaActual != null && turnosRestantes > 0) {
                    if (noticiaActual.esFalsa) {
                        titular.setText("¡ALERTA!\nEs una noticia FALSA.");
                        titular.setColor(Color.valueOf("#DF0E0B"));
                    } else {
                        titular.setText("Todo en orden.\nLa noticia es real.");
                        titular.setColor(Color.valueOf("#3baaa7"));
                    }
                }
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

        if (malaDecision) {
            conteoErrores++;
        } else {
            conteoAciertos++;
        }

        conteoActividad++;
        turnosRestantes--;

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
            noticiaActual = bancoNoticias[MathUtils.random(0, bancoNoticias.length - 1)];
            titular.setText(noticiaActual.titular);
            titular.setColor(Color.valueOf("#3baaa7"));
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
        }

        // Si la decisión fue mala, aparece el personaje advirtiendo
        if (malaDecision) {
            caos = Math.max(0f, caos - PERDIDA_POR_ERROR);
            actualizarBarraCaos();
            mostrarAdvertencia();
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

    private void mostrarAdvertencia() {
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
            }
        });
        overlay.add(entendido).width(160).height(45).padTop(15);

        escenario.addActor(overlay);
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
        bancoNoticias[0] = new News("ULTIMA HORA:\nEl alcalde venderá el parque\na un casino internacional.", true, "General");
        bancoNoticias[1] = new News("SALUD:\nInicia jornada de vacunacion\nen la plaza central.", false, "Salud");
        bancoNoticias[2] = new News("POLÍTICA:\nCandidato opositor fue grabado\nrecibiendo sobornos.", true, "Política");
        bancoNoticias[3] = new News("SEGURIDAD:\nNuevo sistema de 50 cámaras\ninstalado en el centro.", false, "Seguridad");
        bancoNoticias[4] = new News("RUMOR:\nEl agua potable de la zona sur\nestá contaminada con plomo.", true, "General");
        bancoNoticias[5] = new News("ECONOMÍA:\nLos impuestos prediales subirán\nun 50% el próximo mes.", true, "Economía");
        bancoNoticias[6] = new News("CULTURA:\nEl festival de verano\nconfirma fechas oficiales.", false, "Cultura");
        bancoNoticias[7] = new News("TRÁNSITO:\nEl puente principal colapsará\nsi no se cierra hoy mismo.", true, "Tránsito");
        bancoNoticias[8] = new News("MEDIO AMBIENTE:\nSe sembrarán 500 árboles\nen la periferia de la ciudad.", false, "Medio Ambiente");
        bancoNoticias[9] = new News("ESCÁNDALO:\nEl alcalde usó fondos públicos\npara remodelar su mansión.", true, "Política");
        bancoNoticias[10] = new News("TECNOLOGÍA:\nHabrá WiFi gratuito en todas\nlas estaciones de bus.", false, "Tecnología");
        bancoNoticias[11] = new News("URGENTE:\nToque de queda instaurado\npor supuestas protestas armadas.", true, "General");
        bancoNoticias[12] = new News("DEPORTES:\nEl equipo local clasifica\na la final del torneo regional.", false, "Deportes");
        bancoNoticias[13] = new News("FRAUDE:\nLas máquinas de votación\nya están hackeadas.", true, "General");
        bancoNoticias[14] = new News("SALUD:\nHospitales al borde del colapso\npor virus desconocido.", true, "Salud");
        bancoNoticias[15] = new News("SOCIAL:\nAbren nuevo comedor\ncomunitario en la comuna 4.", false, "Social");
        bancoNoticias[16] = new News("ALERTA:\nSe avecina una tormenta eléctrica\nsevera hacia la ciudad.", false, "General");
        bancoNoticias[17] = new News("POLICÍA:\nCapturan a la banda de ladrones\ndel centro comercial.", false, "Política");
        bancoNoticias[18] = new News("RUMOR:\nEl candidato de la oposición es\nun actor contratado.", true, "General");
        bancoNoticias[19] = new News("EDUCACIÓN:\nInauguran biblioteca pública\ncon 10,000 libros nuevos.", false, "Educación");
        bancoNoticias[20] = new News("ECONOMÍA:\nLos supermercados se quedarán\nsin comida mañana por la huelga.", true, "Economía");
        bancoNoticias[21] = new News("EMPLEO:\nFeria laboral ofrece\nmás de 1000 vacantes hoy.", false, "Empleo");
        bancoNoticias[22] = new News("POLÍTICA:\nSe suspenden las elecciones\nhasta el próximo año.", true, "Política");
        bancoNoticias[23] = new News("INFRAESTRUCTURA:\nReparación de vías iniciará\nel lunes a las 6 AM.", false, "Infraestructura");
        bancoNoticias[24] = new News("ESCÁNDALO:\nDocumentos prueban que\nel parque ya fue vendido.", true, "General");
        bancoNoticias[25] = new News("SEGURIDAD:\nExtraterrestres fueron vistos\nsobrevolando la alcaldía.", true, "Seguridad");
        bancoNoticias[26] = new News("CULTURA:\nCine al aire libre gratuito\neste viernes en la plaza.", false, "Cultura");
        bancoNoticias[27] = new News("URGENTE:\nEl transporte público\nserá gratuito para siempre.", true, "General");
        bancoNoticias[28] = new News("COMUNIDAD:\nVecinos organizan jornada\nde limpieza en el río.", false, "Social");
        bancoNoticias[29] = new News("ELECCIONES:\nÚltimo día para inscribir\ntu cédula de votación.", false, "Política");
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
    }
}