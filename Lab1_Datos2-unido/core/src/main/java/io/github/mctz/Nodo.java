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
public void setIzq(Nodo nodoIzq) {
        this.nodoIzq = nodoIzq;
    }

    public void setDer(Nodo nodoDer) {
        this.nodoDer = nodoDer;
    }
    
}
