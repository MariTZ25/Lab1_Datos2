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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
    
    private Stage escenario;
    private Skin skin;
    private News[] bancoNoticias;
    private Nodo escenaRoot;
    private Nodo root;
    private Nodo nodoActual;

    @Override
    public void create () {
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
        bancoNoticias[29] = new News("ELECCIONES:\nÚltimo día para inscribir\ntu cédula de votación.", false, "Política" );

        root = generarArbolDinamico(6);
        nodoActual = root; 

        //COMPONENTES VISUALES UIX------------------------------------------------

        skin = new Skin();
        BitmapFont fuenteBotones = new BitmapFont(Gdx.files.internal("MilanyFont.fnt"));
        fuenteBotones.getData().setScale(0.3f); 
        skin.add("fuente", fuenteBotones);
        
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

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.up = skin.newDrawable("fondo_boton");
        estiloBoton.font = skin.getFont("fuente");
        estiloBoton.fontColor = Color.valueOf("#E6EEEE");

        Image fondoCelular = new Image(skin.getRegion("fondo_telefono"));
        
        final Label titular = new Label(nodoActual.actualNews.titular, estiloTexto);
        titular.setAlignment(Align.center);

        TextButton btnVerificar = new TextButton("Verificar", estiloBoton);
        TextButton btnCompartir = new TextButton("Compartir", estiloBoton);
        TextButton btnIgnorar = new TextButton("Ignorar", estiloBoton);

        Table raizUI = new Table();
        raizUI.setFillParent(true); 
        Stack pilaCelular = new Stack(); 
        
        Table ui = new Table(); 
        ui.add(titular).padBottom(60).colspan(2).row(); 
        ui.add(btnVerificar).width(140).height(45).padBottom(20).colspan(2).row();
        ui.add(btnCompartir).width(130).height(45).padRight(10);
        ui.add(btnIgnorar).width(130).height(45);

        pilaCelular.add(fondoCelular);
        pilaCelular.add(ui);
        raizUI.add(pilaCelular).width(340).height(620);
        escenario.addActor(raizUI);

        // ----------------------------------------------------------------------------------------------------------------------------

        //Para cuando se detecte el click en el botón--------------------------------------------------------------------------------

        btnVerificar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nodoActual != null) {
                    if (nodoActual.actualNews.esFalsa) {
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
                if (nodoActual != null) {
                    nodoActual = viajarPorElArbol(titular, true, nodoActual);
                }
            }
        });

        btnIgnorar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nodoActual != null) {
                    nodoActual = viajarPorElArbol(titular, false, nodoActual);
                }
            }
        });

        //Aquí llamo a la fun q me va a permitir elegir la escena buena o mala y random  
        if(rumboJuego(factorEquilibrio(nodoActual)) < 6){
            
        }
        
        pixTelefono.dispose();
        btnPix.dispose();
    }
    
    // -------------------------------------------Aplicación del árbol-----------------------------------------------------
    public Nodo generarArbolDinamico(int noticiasRestantes) {
        if (noticiasRestantes == 0) {
            return null;
        }
        News noticiaRandom = bancoNoticias[MathUtils.random(0, 29)]; //Aquí uso el elemento random 
        Nodo nuevoNodo = new Nodo(noticiaRandom);
        nuevoNodo.setConsecuencias(generarArbolDinamico(noticiasRestantes - 1), generarArbolDinamico(noticiasRestantes - 1));
        return nuevoNodo;
    }


//esto está para saber el rumbo de la historia
    public int altura(Nodo nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(altura(nodo.nodoIzq), altura(nodo.nodoDer));
    }


    public int factorEquilibrio(Nodo nodo) {
        if (nodo == null) {
            return 0;
        }
        return altura(nodo.nodoIzq) - altura(nodo.nodoDer);
    }


    public void inOrden(Nodo nodito){
        if(nodito == null){
            return;
        } else {
            inOrden(nodito.nodoIzq);
            System.out.println(nodito.actualNews.titular+", ");
            inOrden(nodito.nodoDer);
        }
    }

    public Nodo viajarPorElArbol(Label titular, boolean compartido, Nodo nodito) {
        if(nodito == null){
            return null;
        }
        
        nodito.nodoIzq = viajarPorElArbol(titular, compartido, nodito.nodoIzq);
        
        if ((compartido && !nodito.actualNews.esFalsa )||(!compartido && nodito.actualNews.esFalsa)) {
            nodito.nodoDer = viajarPorElArbol(titular, compartido, nodito.nodoDer);
        } else if((compartido && nodito.actualNews.esFalsa)||(!compartido && !nodito.actualNews.esFalsa)) {
            if (nodito.nodoIzq == null) {
                nodito = nodito.nodoDer;
            } else if (nodito.nodoDer == null) {
                nodito = nodito.nodoIzq;
            } else {
                Nodo temporal = nodito.nodoDer;
                while (temporal.nodoIzq != null) {
                    temporal = temporal.nodoIzq;
                }
                temporal.nodoIzq = nodito.nodoIzq;
                nodito = nodito.nodoDer;
            }
        }
        
        nodoActual = nodito;
        
        if (nodoActual != null) {
            titular.setText(nodoActual.actualNews.titular);
            titular.setColor(Color.valueOf("#3baaa7")); 
        } else {
            titular.setText("FIN DEL DÍA\nHas procesado todas\nlas noticias de hoy.");
            titular.setColor(Color.valueOf("#591D67")); 
        }
        return nodito;
    }

    public int rumboJuego(int fe){

        if (fe > 0) {
            int num=MathUtils.random(0, 5);
            return num; 
        } else if (fe < 0) {
            int num=MathUtils.random(6, 10);
            return num; 
        } else {
            return 11; 
        }
    }

    @Override
    public void render () {
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
    public void dispose () {
        escenario.dispose();
        skin.dispose();
    }
}