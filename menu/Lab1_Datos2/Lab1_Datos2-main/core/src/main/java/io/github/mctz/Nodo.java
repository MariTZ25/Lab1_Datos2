package io.github.mctz;


public class Nodo {
    News actualNews; 
    Nodo nodoIzq; 
    Nodo nodoDer;   

    public Nodo(News evento) {
        this.actualNews = evento;
        this.nodoIzq = null;
        this.nodoDer = null;
    }

    public void setConsecuencias(Nodo izquierda, Nodo derecha) {
        this.nodoIzq = izquierda;
        this.nodoDer = derecha;
    }
}
