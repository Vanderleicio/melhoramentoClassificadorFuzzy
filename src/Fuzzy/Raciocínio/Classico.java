/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.Raciocínio;

import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import Fuzzy.ControllerNaoEstacionario.Metricas;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.html.HTMLDocument;
import net.sourceforge.jFuzzyLogic.FIS;

/**
 *
 * @author Allen Hichard
 */
public class Classico {
    
    public FIS fis;
    public List<Instancia> instancias;
    public List<Regra> regras;
    public List<String> variaveis;
    public List<Integer> gabarito;
    public List<Integer> resultadoClassificacao;
    public List<Double> scores;
    public static boolean variavelTeste = false;
    public static boolean tnormaSempre;
    
    public Classico(FIS fis, List<Regra> regras, List<Instancia> instancias, List<String> variaveis){
        this.fis = fis;
        this.instancias = instancias;
        this.variaveis = variaveis;
        this.regras = regras;
        this.gabarito = new ArrayList<>();
        this.resultadoClassificacao = new ArrayList<>();
        this.scores = new ArrayList<>();
    }
    
    public double classificar(boolean desbalanceado, boolean multiclasses, List<String>  discretas, Metricas metrica){
    	String[] alfabeto = {
    		    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
    		    "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    		};
        for (Instancia instancia : this.instancias){
            List<Double> atributos = instancia.caracteristicas;
            this.gabarito.add(instancia.classe);
            int classificacao = -1;
            double maiorTnorma = -1;
            int cont = 0;
            int posRegraUsada = 0;
            for (Regra regra : this.regras){
                double tnorma = 1;
                Iterator var = this.variaveis.iterator();
                Iterator caracteristicas = instancia.caracteristicas.iterator();
                Iterator antecedentes = regra.antecedentes.iterator();
                Iterator discreta = discretas.iterator();
                while (var.hasNext()){
                	ArrayList<String> terms = new ArrayList<String>();
                    String variavel = (String) var.next(); //atributos 
                    double valor = (double) caracteristicas.next(); //valores das instancias
                    int indiceTermo = (int) antecedentes.next(); //id do conjunto
                    fis.setVariable(variavel, valor);
                    String eDiscretas[] = (String[]) discreta.next();
                    
                    if (eDiscretas[0] == "0") {
                		terms.add("BAIXA");
                		terms.add("MEDIA");
                		terms.add("ALTA");
                	} else {
                		for (int i=0; i< eDiscretas.length; i++) {
                			terms.add(alfabeto[i]);
                		}
                	}
                    
                    if (indiceTermo != -1) {
                        tnorma *= fis.getVariable(variavel).getMembership(terms.get(indiceTermo));
                    	}  
                }
                if (tnormaSempre) {
                	regra.setTnormaAtual(tnorma);
                }
                
                if (tnorma > maiorTnorma){
                    maiorTnorma = tnorma;
                    classificacao = regra.consequente;
                    posRegraUsada = cont;
                }
                cont++;
               
            }
            // Só atualiza a tNorma da regra que foi usada para determinar a resposta
            if (!tnormaSempre) {
            	this.regras.get(posRegraUsada).setTnormaAtual(maiorTnorma);            	
            }
            
            this.scores.add(maiorTnorma);
            this.resultadoClassificacao.add(classificacao);
        }
        if (desbalanceado) return this.performance(metrica, multiclasses);
        return this.acc();
    }
    
    public double acc(){
        Iterator gabarito = this.gabarito.iterator();
        Iterator resultado = this.resultadoClassificacao.iterator();
        
        double total = this.resultadoClassificacao.size();
        int count = 0;
        
        while (gabarito.hasNext()) {
            int gab = (int) gabarito.next();
            int res = (int) resultado.next(); 
            if (gab == res) count++;
        }
        
        return count/total;
    }
    
    public double interpretabilidade (){
        double numerador = 0;
        for (Regra regra: this.regras){
            Iterator antecedentes = regra.antecedentes.iterator();
            while(antecedentes.hasNext()){
                int antecedente = (int) antecedentes.next();
                if (antecedente != -1) numerador++;        
            }
            numerador++;
        }
        if (this.regras.size() < 2){
            return 0;
        }
        //System.out.println(numerador + " - " + this.instancias.size()+" - "+this.regras.get(0).tamanhoInstancia());
        return 1 - (numerador/(this.instancias.size()* this.regras.get(0).tamanhoInstancia()));
    }
    
    public double performance(Metricas metrica, boolean multiclasse){
        Iterator gabarito = this.gabarito.iterator();
        Iterator resultado = this.resultadoClassificacao.iterator();
        List<Double> true_positives = new ArrayList<Double>();

        //System.out.println(this.gabarito.size());
        //System.out.println(this.resultadoClassificacao.size());
        double total = this.resultadoClassificacao.size();
        int count = 0;
        double VP = 0;
        double VN = 0;
        double FP = 0;
        double FN = 0;
        //System.out.println("total = " + total);
        while (gabarito.hasNext()) {
            int gab = (int) gabarito.next();
            int res = (int) resultado.next();
            //System.out.println(gab + " -------------- " + res);
            if (gab == 0 && res == 0) {
            	VP++;
            	true_positives.add(1.0);
            }
            else if (gab == 1 && res == 1) {
            	VN++;
            	true_positives.add(1.0);
            }
            else if (gab == 1 && res == 0) {
            	FP++;
            	true_positives.add(0.0);
            }
            else if (gab == 0 && res == 1) {
            	FN++;
            	true_positives.add(0.0);
            }
        }
        
        /*
        double[] tList = new double[true_positives.size()];
        double[] escores = new double[this.scores.size()];
        
        for (int i = 0; i < true_positives.size(); i++) {
        	tList[i] = true_positives.get(i);
        }
        
        for (int i = 0; i < this.scores.size(); i++) {
        	escores[i] = this.scores.get(i);
        }
        */
        
        //Roc roc = new Roc(escores, tList);
        //System.out.println(VP+"--"+VN+"--"+FP+"--"+FN);
        
        double taxa_VP, taxa_VN, taxa_FP, taxa_FN, precision;
        
        if((VP + FN)!=0) taxa_VP = VP / (VP + FN);
        else taxa_VP = 0;
        
        if((VN + FP)!=0) taxa_VN = VN / (VN + FP);
        else taxa_VN = 0;
        
        if((FP + VN)!=0) taxa_FP = FP / (FP + VN);
        else taxa_FP = 0;
        
        if((FN + VP)!=0) taxa_FN = FN / (FN + VP);
        else taxa_FN = 0;
        
        if((VP + FP)!=0) precision = VP / (VP + FP);
        else precision = 0;
        
        //Double AUC = (1 + taxa_VP - taxa_FP) / 2;
        //Double AUC = roc.computeAUC();
        //System.out.println("Taxa_VP: " + taxa_VP + " precision: " + precision + " taxa_VN: " + taxa_VN);
        double Gmean, AUC, F1Score;
        
        if (multiclasse) {
        	ArrayList<Integer> classesAnalisadas = new ArrayList<Integer>();
        	ArrayList<Integer> aparicoesClasses = new ArrayList<Integer>();
        	
        	double acmF1 = 0;
        	double acmGmean = 0;
        	double acmAUC = 0;
        	for (Integer classeFoco : this.gabarito) {
        		if (!classesAnalisadas.contains(classeFoco)) {
        			aparicoesClasses.add(1);
        			double mVP = 0;
        			double mVN = 0;
        			double mFP = 0;
        			double mFN = 0;
        			
        			for (int i = 0; i < this.gabarito.size(); i++) {
        				if((this.gabarito.get(i) == classeFoco) && (this.resultadoClassificacao.get(i) == classeFoco)){
        					mVP++;
        				} else if((this.gabarito.get(i) == classeFoco) && (this.resultadoClassificacao.get(i) != classeFoco)) {
        					mFN++;
        				} else if((this.gabarito.get(i) != classeFoco) && (this.resultadoClassificacao.get(i) != classeFoco)) {
        					mVN++;
        				} else if((this.gabarito.get(i) != classeFoco) && (this.resultadoClassificacao.get(i) == classeFoco)) {
        					mFP++;
        				}
        			}
        			
        			double aparicoes = (double) this.gabarito.stream().filter(e -> e.equals(classeFoco)).count();
        			double frequenciaRelativa = aparicoes / this.gabarito.size();
        			double mPrecision = (mVP + mFP) == 0? 0: (mVP/(mVP + mFP));
        			double mRecall = (mVP + mFN) == 0? 0: (mVP/(mVP + mFN));
        			double mTaxaVN = (mFN + mVP) == 0? 0: (mFN / (mFN + mVP));
        			acmF1 += (mPrecision + mRecall) == 0? 0: ((2*mPrecision*mRecall)/(mPrecision + mRecall));
        			acmAUC += frequenciaRelativa * ((mRecall + mTaxaVN)/2);
        			acmGmean += frequenciaRelativa * (Math.sqrt(mRecall * mTaxaVN));
        			classesAnalisadas.add(classeFoco);
        		} 
        		
        	}
        	Gmean = acmGmean;
        	AUC = acmAUC;
        	F1Score = acmF1 / classesAnalisadas.size();
        } else {
        	Gmean = Math.sqrt(taxa_VP * taxa_VN);
        	AUC = (taxa_VP + taxa_VN) / 2;
        	F1Score = (precision + taxa_VP) == 0? 0: ((2*precision*taxa_VP)/(precision + taxa_VP));        	
        }
        //System.out.println(roc.computeAUC());
        //List<CurveCoordinates> roc_coordinates = roc.computeRocPointsAndGenerateCurve(AUC + "Roc_curve.png");
        
        switch(metrica) {
        	case GMEAN:
        		return Gmean;
        	case AUC:
        		return AUC;
        	case F1SCORE:
        		return F1Score;
        	default:
        		return 0;
        }
        
        
    }
    
}




    
    
        
       
       
       



