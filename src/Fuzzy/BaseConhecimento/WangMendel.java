/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.BaseConhecimento;

import Fuzzy.Model.ConjuntoAtivo;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class WangMendel {
    
    public FIS fis;
    public List<Instancia> instancias;
    public List<String> variaveis;
    public List<Regra> regras;
    public List<String> nomesDiscretos;
    
    public boolean variavelTeste = false;
    public static boolean tnormaComoFS = false;
    
    public WangMendel(String jLogicFuzzy, List<Instancia> instancias, List<String> variaveis, List<String> nomesDiscretos) throws RecognitionException {
        //FIS fis = FIS.createFromString(jLogicFuzzy, true);
        this.fis = FIS.createFromString(jLogicFuzzy, true);
        this.instancias = instancias;
        this.variaveis = variaveis;
        this.nomesDiscretos = nomesDiscretos;
        this.regras = new ArrayList<>();
        //JFuzzyChart.get().chart(fis.getFunctionBlock(null));
    }
    
    public void criarRegras(int indexAtual){
        for (Instancia instancia: this.instancias){
            Regra regra = new Regra(indexAtual);
            Iterator var = this.variaveis.iterator();
            Iterator caracteristicas = instancia.caracteristicas.iterator();
            Iterator discretas = this.nomesDiscretos.iterator();
            double tnorma = 1;
            while (var.hasNext()){
                String variavel = (String) var.next();
                double valor = (double) caracteristicas.next();
                String eDiscreta[] = (String[]) discretas.next();
                ConjuntoAtivo ca = conjuntoAtivado(variavel, valor, eDiscreta);
                regra.addAntecedente(ca.indexConjuntoAtivado);
                tnorma *= ca.pertinencia;
            }
            regra.setTnorma(tnorma);
            regra.addConsequente(instancia.classe);
            tratarInconsistencias(regra, indexAtual);
        }
        
        //for (Regra regra : this.regras){
          //  System.out.println(regra.toString());
        //}
        //return new ArrayList();
    }
        
    public void setNovasInstancias(List<Instancia> novasInstancias, int indexAtual) {
    	this.instancias = novasInstancias;
    	this.criarRegras(indexAtual);
    }
    
    public void atualizarUtilidade(int indexAtual) {
    	//Atualização da utilidade de cada regra
    	double tNormaAcm = 0;
    	
    	int nRegras = this.regras.size();
    	
    	for(Regra r: this.regras) {
    		tNormaAcm += r.tnormaAtual;
    	}
    	
    	for (int i = 0; i < nRegras; i++) {
    		Regra r = this.regras.get(i);
    		double forcaAtivacao = (tNormaAcm == 0)? 0: r.tnormaAtual/tNormaAcm;
    		
    		r.acumularAtivacao(forcaAtivacao);
    		
    		r.utilidade = r.ativacaoAcm/(indexAtual - r.indexCriacao);	
    	}
    }
    
    public double[] calcularEstatisticas(int indexAtual) {
    	double [] parametros = {0, 0, 0, 0};
    	
    	
    	double mediaUtilidade = 0; 
    	double desvioUtilidade = 0;
    	
    	int nRegras = this.regras.size();
    	for (Regra r: this.regras){
    		mediaUtilidade += r.utilidade;
    	}
    	
    	
    	mediaUtilidade = mediaUtilidade / nRegras;
 
    	
    	for (Regra r: this.regras) {
    		desvioUtilidade += Math.pow((r.utilidade - mediaUtilidade), 2);
    			
    	}
    	
    	desvioUtilidade = Math.sqrt(desvioUtilidade/nRegras);
    		
    	parametros[0] = 0;
    	parametros[1] = mediaUtilidade;
    	parametros[2] = 0;
    	parametros[3] = desvioUtilidade;
    		
    	return parametros;
    }
    
    public int atualizarBase() {
    	int contDesc = 0;
    	double utilidadeMedia = 0;
    	
    	for (Regra r: this.regras) {
    		utilidadeMedia += r.utilidade;
    	}
    	
    	utilidadeMedia = utilidadeMedia/this.regras.size();
		//Atualização com base na Utilidade da Regra
    	for (int i = 0; i < this.regras.size(); i++) {
    		Regra r = this.regras.get(i);
    		if (r.utilidade < utilidadeMedia) {
    			this.regras.remove(i);
    			contDesc++;
    		}
    	}
    	
    	return contDesc;
    	
    }
    
    private ConjuntoAtivo conjuntoAtivado(String variavel, double valor, String[] eDiscretas){
    	ArrayList<String> terms = new ArrayList<String>();
    	String[] alfabeto = {
    		    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
    		    "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    		};
    	if (eDiscretas[0] == "0") {
    		terms.add("BAIXA");
    		terms.add("MEDIA");
    		terms.add("ALTA");
    	} else {
    		for (int i=0; i< eDiscretas.length; i++) {
    			terms.add(alfabeto[i]);
    		}
    	}
        int term = -1;
        double pertinencia = 0;
        for (int i = 0; i < terms.size(); i++) {
            fis.setVariable(variavel, valor);
            double p = fis.getVariable(variavel).getMembership(terms.get(i));
            if (p > pertinencia){
                pertinencia = p;
                term = i;
            }
        }
        return new ConjuntoAtivo(term, pertinencia);
    }
    
    private void tratarInconsistencias(Regra regra, int indexAtual){
        if (regra.tnorma > 0){
            for (Regra r: this.regras){
                if (r.antecedentes.equals(regra.antecedentes)){
                	if(r.consequente == regra.consequente) {
                		r.addDado(indexAtual);
                	}
                	
                    if (r.tnorma < regra.tnorma){
                        r.consequente = regra.consequente;
                        r.tnorma = regra.tnorma;
                    } 
                    
                    return;
                }
            }
            this.regras.add(regra);
        }
       
    }
}
