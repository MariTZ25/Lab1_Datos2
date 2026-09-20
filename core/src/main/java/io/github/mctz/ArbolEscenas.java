package io.github.mctz;

public class ArbolEscenas {

    public Nodo generarArbol() {
        
        News txtRaiz = new News("INICIO DEL DÍA\nLa ciudad está tranquila, por ahora.", false, "Escena");
        Nodo raizEscenas = new Nodo(txtRaiz);

        News txtMalo1 = new News("ESCENA MALA 1", false, "Escena");
        Nodo malo1 = new Nodo(txtMalo1);
        
        News txtBueno1 = new News("ESCENA BUENA 1", false, "Escena");
        Nodo bueno1 = new Nodo(txtBueno1);
        
        raizEscenas.nodoIzq = malo1;  
        raizEscenas.nodoDer = bueno1; 

        News txtMalo2 = new News("ESCENA MALA 2", false, "Escena");
        Nodo malo2 = new Nodo(txtMalo2); 
        
        News txtRecuperacion = new News("ESCENA BUENA DE LA PRIMERA MALA", false, "Escena");
        Nodo recuperacion = new Nodo(txtRecuperacion); 
        //Aquí se crean los nodos del lado izq del arbol
        malo1.nodoIzq = malo2;
        malo1.nodoDer = recuperacion;

        
        News txtDuda = new News("ESCENA MALA DE LA PRIMERA BUENA", false, "Escena");
        Nodo duda = new Nodo(txtDuda);
        
        News txtBueno2 = new News("ESCENA BUENA 2", false, "Escena");
        Nodo bueno2 = new Nodo(txtBueno2); 
        
        bueno1.nodoIzq = duda;
        bueno1.nodoDer = bueno2;
        //nivel 1 del arbol lleno hasta acá-------------------------------
        
        
        
        News txtColapso = new News("ESCENA MALA DE LA SEGUNDA MALA", false, "Final");
        Nodo colapso = new Nodo(txtColapso);
        
        News txtDisturbios = new News("ESCENA BUENA DE LA SEGUNDA MALA", false, "Final");
        Nodo disturbios = new Nodo(txtDisturbios);
        
        malo2.nodoIzq = colapso;    
        malo2.nodoDer = disturbios; 

        News txtNeutro = new News("ESCENA NEUTRA", false, "Final");
        Nodo neutro = new Nodo(txtNeutro);
        
        recuperacion.nodoIzq = disturbios; 
        recuperacion.nodoDer = neutro;
        
        duda.nodoIzq = neutro;
        duda.nodoDer = bueno2; 
       
        News txtProgreso = new News("ESCENA MALA DE LA SEGUNDA BUENA", false, "Final");
        Nodo progreso = new Nodo(txtProgreso);
        
        News txtPerfecto = new News("ESCENA TODO BUENO", false, "Final");
        Nodo perfecto = new Nodo(txtPerfecto);
        
        bueno2.nodoIzq = progreso;
        bueno2.nodoDer = perfecto;   
        
        return raizEscenas;
    }
}