/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.Model;

/**
 *
 * @author Allen Hichard
 */
public class ConjuntoAtivo {
    public int indexConjuntoAtivado;
    public double pertinencia;
    
    public ConjuntoAtivo(int indexConjuntoAtivado,double pertinencia){
        this.indexConjuntoAtivado = indexConjuntoAtivado;
        this.pertinencia = pertinencia;      
    }
}
