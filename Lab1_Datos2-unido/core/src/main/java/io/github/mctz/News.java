package io.github.mctz;

public class News {
    String titular;
    boolean esFalsa;
    String categoria;
    // Índice del lugar (ver Lugar) donde se puede verificar; -1 si no aplica
    int lugar;

    public News(String titular, boolean esFalsa, String categoria) {
        this(titular, esFalsa, categoria, -1);
    }

    public News(String titular, boolean esFalsa, String categoria, int lugar) {
        this.titular = titular;
        this.esFalsa = esFalsa;
        this.categoria = categoria;
        this.lugar = lugar;
    }
}
