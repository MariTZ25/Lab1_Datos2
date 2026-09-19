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
    private Nodo escenaActual; 
    private News noticiaActual;
    
    private int turnosRestantes = 6; 
    private int conteoActividad = 0;
    private int conteoErrores = 0; 
    private int conteoAciertos = 0; 
    private Label titular; 

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

        ArbolEscenas creadorEscenas = new ArbolEscenas();
        escenaActual = creadorEscenas.generarArbol(); 

        noticiaActual = bancoNoticias[MathUtils.random(0, 29)];

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
        
        titular = new Label(noticiaActual.titular, estiloTexto);
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

        // ------------------------------------------------------botones----------------------------------------------------------------------

        btnVerificar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        pixTelefono.dispose();
        btnPix.dispose();
    }
    
    // ------------------------------------------- Procesamiento de Turno -----------------------------------------------------
    
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
        }
        
        if (turnosRestantes > 0) {
            noticiaActual = bancoNoticias[MathUtils.random(0, 29)];
            titular.setText(noticiaActual.titular);
            titular.setColor(Color.valueOf("#3baaa7")); 
        } else {
            if (escenaActual != null) {
                titular.setText("FIN DEL DÍA\n\n" + escenaActual.actualNews.titular);
                titular.setColor(Color.valueOf("#591D67")); 
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------

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