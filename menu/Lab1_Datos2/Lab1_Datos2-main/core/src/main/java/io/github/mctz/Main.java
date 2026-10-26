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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Alcalde Digital - primera interfaz gráfica.
 *
 * La navegación está inspirada en el flujo de Carta Abierta:
 * menú -> selección de personaje/rol -> selección de modo -> partida.
 * La lógica del árbol de noticias del laboratorio se conserva y se conecta
 * con la pantalla de partida.
 */
public class Main extends ApplicationAdapter {

    private static final float ANCHO = 1100f;
    private static final float ALTO = 700f;

    private Stage escenario;
    private Skin skin;
    private BitmapFont fuente;
    private BitmapFont fuenteGrande;
    private Texture fondo;
    private Texture candidato;
    private Texture chica1;
    private Texture chica2;

    private News[] bancoNoticias;
    private Nodo root;
    private Nodo nodoActual;

    private int rolActual = 0;
    private final String[] roles = {"CIUDADANO", "PERIODISTA", "INFLUENCER", "CANDIDATO A ALCALDE"};
    private boolean musicaActiva = true;

    private final Color AZUL_FONDO = Color.valueOf("#A9CCE3");
    private final Color AZUL_PANEL = Color.valueOf("#6A9FC3");
    private final Color DORADO = Color.valueOf("#D6B45A");
    private final Color CREMA = Color.valueOf("#F3E8C8");
    private final Color AZUL_CLARO = Color.valueOf("#80C7D9");
    private final Color ROJO = Color.valueOf("#D95C5C");

    @Override
    public void create() {
        escenario = new Stage(new FitViewport(ANCHO, ALTO));
        Gdx.input.setInputProcessor(escenario);

        cargarRecursos();
        cargarNoticias();
        root = generarArbolDinamico(6);
        nodoActual = root;

        mostrarMenu();
    }

    private void cargarRecursos() {
        skin = new Skin();
        fuente = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuente.getData().setScale(0.34f);
        fuenteGrande = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteGrande.getData().setScale(0.75f);
        skin.add("fuente", fuente);

        fondo = texturaColor(AZUL_FONDO);
        skin.add("fondo", fondo);
        skin.add("panel", texturaColor(AZUL_PANEL));
        skin.add("dorado", texturaColor(DORADO));
        skin.add("boton", texturaColor(Color.valueOf("#7FB3D5")));
        skin.add("botonHover", texturaColor(Color.valueOf("#7FB3D5")));
        skin.add("rojo", texturaColor(ROJO));

        candidato = cargarSiExiste("candidato.png");
        chica1 = cargarSiExiste("chica1.png");
        chica2 = cargarSiExiste("chica2.png");
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

    private void basePantalla() {
        escenario.clear();
        Image img = new Image(skin.get("fondo", Texture.class));
        img.setFillParent(true);
        escenario.addActor(img);
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
        // Se elimina la información anterior salvo las flechas. La tabla tiene
        // tres columnas y se reconstruye para mantener la navegación sencilla.
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
    // PARTIDA: se conecta con el árbol que ya existía
    // ================================================================
    private void iniciarPartida() {
        root = generarArbolDinamico(6);
        nodoActual = root;
        mostrarPartida();
    }

    private void mostrarPartida() {
        basePantalla();
        Table pantalla = new Table();
        pantalla.setFillParent(true);
        pantalla.pad(25);

        Table cabecera = new Table();
        Label ciudad = new Label("CIUDAD NOVA", estiloTexto(DORADO, 0.45f));
        Label rol = new Label("ROL: " + roles[rolActual], estiloTexto(CREMA, 0.28f));
        cabecera.add(ciudad).left().expandX();
        cabecera.add(rol).right();
        pantalla.add(cabecera).fillX().row();

        Table centro = new Table();
        centro.defaults().pad(10);

        Label tituloNoticia = new Label(nodoActual.actualNews.titular, estiloTexto(CREMA, 0.37f));
        tituloNoticia.setWrap(true);
        tituloNoticia.setAlignment(Align.center);

        Table tarjeta = new Table();
        tarjeta.background(new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(skin.get("panel", Texture.class))));
        tarjeta.pad(30);
        tarjeta.add(new Label("PUBLICACIÓN", estiloTexto(DORADO, 0.32f))).row();
        tarjeta.add(tituloNoticia).width(580).height(180).padTop(15).row();

        centro.add(tarjeta).width(680).height(280).row();

        TextButton verificar = boton("VERIFICAR", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (nodoActual != null) {
                    String mensaje = nodoActual.actualNews.esFalsa
                            ? "¡ALERTA! La publicación es FALSA."
                            : "Todo en orden. La publicación es VERDADERA.";
                    mostrarResultadoTemporal(mensaje, nodoActual.actualNews.esFalsa);
                }
            }
        });
        TextButton compartir = boton("COMPARTIR", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (nodoActual != null) {
                    nodoActual = viajarPorElArbol(null, true, nodoActual);
                    if (nodoActual != null) mostrarPartida(); else mostrarFin();
                }
            }
        });
        TextButton ignorar = boton("IGNORAR", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (nodoActual != null) {
                    nodoActual = viajarPorElArbol(null, false, nodoActual);
                    if (nodoActual != null) mostrarPartida(); else mostrarFin();
                }
            }
        });

        Table acciones = new Table();
        acciones.add(verificar).width(190).height(58).pad(5);
        acciones.add(compartir).width(190).height(58).pad(5);
        acciones.add(ignorar).width(190).height(58).pad(5);
        centro.add(acciones).row();

        pantalla.add(centro).expandY().center().row();

        Table indicadores = new Table();
        indicadores.add(indicador("INFORMACIÓN VERIFICADA", "82")).pad(5);
        indicadores.add(indicador("CONFIANZA", "74")).pad(5);
        indicadores.add(indicador("CONVIVENCIA", "88")).pad(5);
        indicadores.add(indicador("DESINFORMACIÓN", "21")).pad(5);
        pantalla.add(indicadores).fillX().row();

        escenario.addActor(pantalla);
    }

    private Table indicador(String nombre, String valor) {
        Table t = new Table();
        t.background(new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(skin.get("boton", Texture.class))));
        t.pad(7);
        t.add(new Label(nombre + ": " + valor, estiloTexto(CREMA, 0.19f)));
        return t;
    }

    private void mostrarResultadoTemporal(String mensaje, boolean falsa) {
        // Para esta primera entrega se muestra una retroalimentación gráfica.
        // El siguiente paso será convertirla en una ventana animada.
        basePantalla();
        Table t = new Table();
        t.setFillParent(true);
        Label titulo = new Label(falsa ? "INFORMACIÓN NO CONFIABLE" : "INFORMACIÓN VERIFICADA",
                estiloTexto(falsa ? ROJO : AZUL_CLARO, 0.55f));
        titulo.setAlignment(Align.center);
        t.add(titulo).row();
        Label m = new Label(mensaje + "\n\nEsta decisión tendrá consecuencias en la ciudad.", estiloTexto(CREMA, 0.32f));
        m.setAlignment(Align.center);
        t.add(m).padTop(20).row();
        t.add(boton("CONTINUAR", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarPartida(); }
        })).width(300).height(60).padTop(30);
        escenario.addActor(t);
    }

    private void mostrarFin() {
        basePantalla();
        Table t = new Table();
        t.setFillParent(true);
        t.add(new Label("FIN DEL DÍA", estiloTexto(DORADO, 0.7f))).row();
        t.add(new Label("Has procesado todas las noticias de hoy.\nEl resultado de Ciudad Nova se calculará con las decisiones tomadas.",
                estiloTexto(CREMA, 0.32f))).padTop(20).row();
        t.add(boton("VOLVER AL MENÚ", new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { mostrarMenu(); }
        })).width(330).height(60).padTop(30);
        escenario.addActor(t);
    }

    // ================================================================
    // ÁRBOL DE NOTICIAS - lógica existente del laboratorio
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

    public Nodo generarArbolDinamico(int noticiasRestantes) {
        if (noticiasRestantes == 0) return null;
        News noticiaRandom = bancoNoticias[MathUtils.random(0, bancoNoticias.length - 1)];
        Nodo nuevoNodo = new Nodo(noticiaRandom);
        nuevoNodo.setConsecuencias(
                generarArbolDinamico(noticiasRestantes - 1),
                generarArbolDinamico(noticiasRestantes - 1));
        return nuevoNodo;
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

    /**
     * Avanza por el árbol según la decisión del jugador.
     * Se mantiene la idea original: compartir/ignorar conduce por ramas
     * diferentes según si la noticia es verdadera o falsa.
     */
    public Nodo viajarPorElArbol(Label titular, boolean compartido, Nodo nodito) {
        if (nodito == null) return null;

        nodito.nodoIzq = viajarPorElArbol(null, compartido, nodito.nodoIzq);

        if ((compartido && !nodito.actualNews.esFalsa) || (!compartido && nodito.actualNews.esFalsa)) {
            nodito.nodoDer = viajarPorElArbol(null, compartido, nodito.nodoDer);
        } else if ((compartido && nodito.actualNews.esFalsa) || (!compartido && !nodito.actualNews.esFalsa)) {
            if (nodito.nodoIzq == null) {
                nodito = nodito.nodoDer;
            } else if (nodito.nodoDer == null) {
                nodito = nodito.nodoIzq;
            } else {
                Nodo temporal = nodito.nodoDer;
                while (temporal.nodoIzq != null) temporal = temporal.nodoIzq;
                temporal.nodoIzq = nodito.nodoIzq;
                nodito = nodito.nodoDer;
            }
        }

        nodoActual = nodito;
        return nodito;
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
        ScreenUtils.clear(AZUL_FONDO);
        escenario.act(Gdx.graphics.getDeltaTime());
        escenario.draw();
    }

    @Override
    public void resize(int width, int height) {
        escenario.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (escenario != null) escenario.dispose();
        if (skin != null) skin.dispose();
        if (fondo != null) fondo.dispose();
        if (candidato != null) candidato.dispose();
        if (chica1 != null) chica1.dispose();
        if (chica2 != null) chica2.dispose();
    }
}
