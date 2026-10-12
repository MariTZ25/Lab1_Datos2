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
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling; // NUEVO: Para que la imagen no se deforme
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Main extends ApplicationAdapter {

    private Stage escenario;
    private Skin skin;
    private News[] bancoNoticias;
    private Nodo escenaActual;
    private News noticiaActual;

    private int turnosRestantes = 6;
    private int conteoActividad = 0;
    private int conteoErrores = 0;
    private int conteoAciertos = 0;

    private Label titular;
    private Label lblTendencias;
    private Texture texturaTelefono;
    private StringBuilder tendenciasBuilder;
    private BitmapFont fuenteTendencias; // NUEVO: Fuente más pequeña para los #

    private int confiabilidad = 100;
    private boolean noticiaVerificada = false;
    private Label lblConfiabilidad;

    private Table panelConciencia;
    private Label lblConciencia;
    private Texture texturaConciencia;

    private Table panelFuturo;
    private Label lblFuturo;
    private Texture texturaFuturo;

    @Override
    public void create() {
        escenario = new Stage(new FitViewport(800, 600));
        Gdx.input.setInputProcessor(escenario);

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

        ArbolEscenas creadorEscenas = new ArbolEscenas();
        escenaActual = creadorEscenas.generarArbol();

        noticiaActual = bancoNoticias[MathUtils.random(0, 29)];

        skin = new Skin();
        BitmapFont fuenteBotones = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteBotones.getData().setScale(0.3f);
        skin.add("fuente", fuenteBotones);

        fuenteTendencias = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteTendencias.getData().setScale(0.14f);

        Pixmap pixTelefono = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixTelefono.setColor(Color.valueOf("#E6EEEE"));
        pixTelefono.fill();
        skin.add("fondo_telefono", new Texture(pixTelefono));

        Pixmap btnPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnPix.setColor(Color.valueOf("#bc8fff"));
        btnPix.fill();
        skin.add("fondo_boton", new Texture(btnPix));

        Label.LabelStyle estiloTexto = new Label.LabelStyle();
        estiloTexto.font = skin.getFont("fuente");
        estiloTexto.fontColor = Color.valueOf("#3baaa7");

        Label.LabelStyle estiloTendencias = new Label.LabelStyle();
        estiloTendencias.font = fuenteTendencias;
        estiloTendencias.fontColor = Color.valueOf("#8b9bb4");

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.up = skin.newDrawable("fondo_boton");
        estiloBoton.font = skin.getFont("fuente");
        estiloBoton.fontColor = Color.valueOf("#E6EEEE");

        texturaTelefono = new Texture(Gdx.files.internal("telefono.png"));
        Image fondoCelular = new Image(texturaTelefono);
        fondoCelular.setScaling(Scaling.fit);

        titular = new Label(noticiaActual.titular, estiloTexto);
        titular.setAlignment(Align.center);

        lblTendencias = new Label("", estiloTendencias);
        lblTendencias.setAlignment(Align.center);
        lblTendencias.setWrap(true);

        lblConfiabilidad = new Label("Confiabilidad: 100%", estiloTendencias);
        lblConfiabilidad.setColor(Color.valueOf("#3baaa7"));

        tendenciasBuilder = new StringBuilder();
        actualizarTendenciasRedes();

        TextButton btnVerificar = new TextButton("Verificar", estiloBoton);
        TextButton btnCompartir = new TextButton("Compartir", estiloBoton);
        TextButton btnIgnorar = new TextButton("Ignorar", estiloBoton);

        Table raizUI = new Table();
        raizUI.setFillParent(true);
        Stack pilaCelular = new Stack();

        Table ui = new Table();
        ui.add(lblConfiabilidad).colspan(2).padBottom(6).row();
        ui.add(lblTendencias).width(250).padBottom(10).colspan(2).row();
        ui.add(titular).padBottom(28).colspan(2).row();
        ui.add(btnVerificar).width(140).height(45).padBottom(13).colspan(2).row();
        ui.add(btnCompartir).width(130).height(45).padRight(8);
        ui.add(btnIgnorar).width(130).height(45);

        pilaCelular.add(fondoCelular);
        pilaCelular.add(ui);
        raizUI.add(pilaCelular).width(1122).height(2046);
        escenario.addActor(raizUI);

        texturaConciencia = new Texture(Gdx.files.internal("chica1.png"));
        Image imgConciencia = new Image(texturaConciencia);
        imgConciencia.setScaling(Scaling.fit);

        lblConciencia = new Label("", estiloTexto);
        lblConciencia.setWrap(true);
        lblConciencia.setAlignment(Align.center);

        TextButton btnEntendido = new TextButton("Entendido", estiloBoton);

        Table tarjeta = new Table();
        tarjeta.setBackground(skin.newDrawable("fondo_telefono"));
        tarjeta.pad(20);
        tarjeta.add(imgConciencia).size(150, 150).padBottom(10).row();
        tarjeta.add(lblConciencia).width(480).padBottom(15).row();
        tarjeta.add(btnEntendido).width(160).height(45);

        panelConciencia = new Table();
        panelConciencia.setFillParent(true);
        panelConciencia.setBackground(skin.newDrawable("fondo_boton", new Color(0, 0, 0, 0.7f)));
        panelConciencia.setTouchable(Touchable.enabled); // bloquea los botones de atrás
        panelConciencia.add(tarjeta);
        panelConciencia.setVisible(false);
        escenario.addActor(panelConciencia);

        btnEntendido.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                panelConciencia.setVisible(false);
                if (turnosRestantes <= 0) {
                    mostrarYoFuturo();
                }
            }
        });

        texturaFuturo = new Texture(Gdx.files.internal("chica2.png"));
        Image imgFuturo = new Image(texturaFuturo);
        imgFuturo.setScaling(Scaling.fit);

        lblFuturo = new Label("", estiloTexto);
        lblFuturo.setWrap(true);
        lblFuturo.setAlignment(Align.center);

        TextButton btnSalir = new TextButton("Salir", estiloBoton);

        Table tarjetaFuturo = new Table();
        tarjetaFuturo.setBackground(skin.newDrawable("fondo_telefono"));
        tarjetaFuturo.pad(20);
        tarjetaFuturo.add(imgFuturo).size(130, 130).padBottom(10).row();
        tarjetaFuturo.add(lblFuturo).width(500).padBottom(15).row();
        tarjetaFuturo.add(btnSalir).width(160).height(45);

        panelFuturo = new Table();
        panelFuturo.setFillParent(true);
        panelFuturo.setBackground(skin.newDrawable("fondo_boton", new Color(0, 0, 0, 0.8f)));
        panelFuturo.setTouchable(Touchable.enabled);
        panelFuturo.add(tarjetaFuturo);
        panelFuturo.setVisible(false);
        escenario.addActor(panelFuturo);

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        btnVerificar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (noticiaActual != null && turnosRestantes > 0) {
                    noticiaVerificada = true;
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

        pixTelefono.dispose();
        btnPix.dispose();
    }

    public void procesarTurno(boolean compartido) {
        if (turnosRestantes <= 0) {
            return;
        }

        boolean esFalsa = noticiaActual.esFalsa;
        boolean malaDecision = (compartido && esFalsa) || (!compartido && !esFalsa);

        if (malaDecision) {
            conteoErrores++;
        } else {
            conteoAciertos++;
        }

        actualizarConfiabilidad(calcularCambioConfiabilidad(compartido, esFalsa, noticiaVerificada));

        if (compartido && esFalsa) {
            mostrarConciencia(noticiaVerificada);
        }

        conteoActividad++;
        turnosRestantes--;

        if (conteoActividad == 2) {

            if (conteoErrores > conteoAciertos) {
                if (escenaActual.nodoIzq != null) {
                    escenaActual = escenaActual.nodoIzq;
                }
            } else if (conteoAciertos > conteoErrores) {
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
            noticiaActual = bancoNoticias[MathUtils.random(0, 29)];
            titular.setText(noticiaActual.titular);
            titular.setColor(Color.valueOf("#3baaa7"));
        } else {
            if (escenaActual != null) {
                titular.setText("FIN DEL DÍA\n\n" + escenaActual.actualNews.titular);
                titular.setColor(Color.valueOf("#591D67"));
                lblTendencias.setText("Tendencias finales consolidadas.");
            }
        }
    }

    public void actualizarTendenciasRedes() {
        if (escenaActual == null) {
            return;
        }

        tendenciasBuilder.setLength(0);
        tendenciasBuilder.append("Trending:\n");

        recolectarTendenciasInOrden(escenaActual);

        lblTendencias.setText(tendenciasBuilder.toString());
    }

    //--recorrido-------inorden-------
    private void recolectarTendenciasInOrden(Nodo nodo) {
        if (nodo == null) {
            return;
        }

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

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        ScreenUtils.clear(Color.valueOf("#223E9A"));
        escenario.act(Gdx.graphics.getDeltaTime());
        escenario.draw();
    }

    @Override
    public void resize(int width, int height) {
        escenario.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        escenario.dispose();
        skin.dispose();
        if (texturaTelefono != null) {
            texturaTelefono.dispose();
        }
        if (fuenteTendencias != null) {
            fuenteTendencias.dispose();
        }
        if (texturaConciencia != null) {
            texturaConciencia.dispose();
        }
        if (texturaFuturo != null) {
            texturaFuturo.dispose();
        }
    }

    private int calcularCambioConfiabilidad(boolean compartido, boolean esFalsa, boolean verificada) {
        if (compartido && esFalsa) {
            return verificada ? -30 : -20; // verificada y aun así la compartió: peor
        }
        if (compartido && !esFalsa) {
            return verificada ? +5 : -5;   // acertó, pero sin comprobar
        }
        if (!compartido && esFalsa) {
            return verificada ? +10 : +5;
        }
        return -5;                                                  // ignoró una noticia real
    }

    private void actualizarConfiabilidad(int cambio) {
        confiabilidad = MathUtils.clamp(confiabilidad + cambio, 0, 100);
        lblConfiabilidad.setText("Confiabilidad: " + confiabilidad + "%");
        if (confiabilidad >= 70) {
            lblConfiabilidad.setColor(Color.valueOf("#3baaa7"));
        } else if (confiabilidad >= 40) {
            lblConfiabilidad.setColor(Color.valueOf("#E0A100"));
        } else {
            lblConfiabilidad.setColor(Color.valueOf("#DF0E0B"));
        }
    }

    private void mostrarConciencia(boolean laHabiaVerificado) {
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
                    + "3. Pulsa Verificar\n"
                    + "4. Si dudas, no la compartas";
        }
        lblConciencia.setText(mensaje);
        panelConciencia.toFront();
        panelConciencia.setVisible(true);
    }

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

        lblFuturo.setText(historia + "\n\nConfiabilidad final: " + confiabilidad + "%");
        panelFuturo.toFront();
        panelFuturo.setVisible(true);
    }
}
