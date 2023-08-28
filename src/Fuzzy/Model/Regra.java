/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Allen Hichard
 */
public class Regra{
    
    public List<Integer> antecedentes;
    public int consequente;
    public double tnorma;
    public double tnormaAtual;
    public int indexAcumulado;
    public int indexCriacao;
    public int nDados = 1;
    public double utilidade = 1.0;
    public double ativacaoAcm;
    
    
    public int tamanhoInstancia(){
        return this.antecedentes.size() + 1;
    }
    public Regra(){
        this.antecedentes = new ArrayList<>();
    }
    
    public Regra(int indexAtual){
        this.antecedentes = new ArrayList<>();
        this.indexCriacao = indexAtual;
        this.indexAcumulado = indexAtual;
    }
    
    public void addAntecedente(int antecedente){
        this.antecedentes.add(antecedente);
    }
    
    public void addConsequente(int classe){
        this.consequente = classe;
    }
    
    public void setTnorma(double valorTnorma){
        this.tnorma = valorTnorma;
        this.tnormaAtual = valorTnorma;
    }
    
    public void setTnormaAtual(double valorTnorma){
        this.tnormaAtual = valorTnorma;
    }
    
    public void acumularAtivacao(double forcaAtivacao) {
    	this.ativacaoAcm += forcaAtivacao;
    }
    
    public void addDado(int index) {
    	this.indexAcumulado += index;
    	this.nDados ++;
    }
    
    public double getCurrentAge(int instanteAtual) {
    	double age = instanteAtual - (this.indexAcumulado/this.nDados);
    	return age;
    }
    @Override
    public String toString() {
        return "Regra{" + "antecedentes=" + antecedentes + ", consequente=" + consequente + ", tnorma=" + tnorma + '}';
    }
    
    public boolean regraCancelada(){
        boolean regraDontCare = true;
        for (int valor : this.antecedentes){
            if(valor != -1){
                regraDontCare = false;
            }
        }
        return regraDontCare;
    }
    
    
    
    
    
}
