/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept.Controller;

/* Code used for Lift-per-drift: an evaluation metric for classification frameworks with concept drift detection
 * 
 * This code is made available for research reproducability. For any other purposes, please contact the author first at rand079 at aucklanduni dot ac dot nz
 */


import moa.classifiers.core.driftdetection.AbstractChangeDetector;
import moa.classifiers.core.driftdetection.HDDM_W_Test;
import moa.classifiers.core.driftdetection.HDDM_A_Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class DriftController {
	
	
	public static AbstractChangeDetector[] detectors = new AbstractChangeDetector[14];
	public static String[] detectorNames = new String[14];
	public static int detector_atual = -1;
	public static ArrayList<Float> lambdas;
	public static ArrayList<Float> confidences;
	
      

    	public static void main(String[] args) throws Exception {
    		
            //.getChange()
                    createDetectors();
                    int detect = 0;
                    Random gerador = new Random();
                    
                    ArrayList<Float> acuracias = new ArrayList<Float>();
                    
                    for (int k = 0; k < 20; k++) {
                    	acuracias.add(100*gerador.nextFloat());
                    }
                    
                    for (int j = 0; j < 6; j++) {
                    	detect = j;
                        detectors[detect].prepareForUse();
                        System.out.println("\nusing detector " + detectorNames[detect]);     
                        for (int i = 20; i > 0; i--){
                        	
                            float acuracia = acuracias.get(i - 1);
                            comunicacao(detectors, detect, acuracia);
                        }
                    }

                    /*
                    comunicacao(detectors, detect, 97);
                    comunicacao(detectors, detect, 1);
                    comunicacao(detectors, detect, 97);
                    comunicacao(detectors, detect, 1);
                    comunicacao(detectors, detect, 97);
                    comunicacao(detectors, detect, 1);
                    comunicacao(detectors, detect, 97);
                    */
            }
	public static void createDetectors() throws Exception{
		//detectors
		if (lambdas == null) {
			lambdas = new ArrayList<Float>();
			
			for (int i = 0; i < 6; i++) {				
				lambdas.add((float) 0.05);
			}
			
		}
		
		if (confidences == null) {
			confidences = new ArrayList<Float>();
			/*
			confidences.add((float) 0.00000125);
			confidences.add((float) 0.000025);
			confidences.add((float) 0.00005);
			confidences.add((float) 0.00000125);
			confidences.add((float) 0.000025);
			confidences.add((float) 0.00005);
			*/
			
			
			confidences.add((float) 0.0005);
			confidences.add((float) 0.01);
			confidences.add((float) 0.2);
			confidences.add((float) 0.0005);
			confidences.add((float) 0.01);
			confidences.add((float) 0.2);
			
			
		}
		detectors = new  AbstractChangeDetector[6];
		detectorNames = new String[6];
		
		HDDM_W_Test ddHDDMwStrict = new HDDM_W_Test();
		ddHDDMwStrict.lambdaOption.setValue(lambdas.get(0));
		ddHDDMwStrict.driftConfidenceOption.setValue(confidences.get(0));
		
		
		HDDM_W_Test ddHDDMwMed = new HDDM_W_Test();
		ddHDDMwMed.lambdaOption.setValue(lambdas.get(1));
		ddHDDMwMed.driftConfidenceOption.setValue(confidences.get(1));
		
		
		HDDM_W_Test ddHDDMwLoose = new HDDM_W_Test();
		ddHDDMwLoose.lambdaOption.setValue(lambdas.get(2));
		ddHDDMwLoose.driftConfidenceOption.setValue(confidences.get(2));
		
                
                ///////////////////////////////////////////////////////
                
                HDDM_A_Test ddHDDMaStrict = new HDDM_A_Test();
		//ddHDDMaStrict.lambdaOption.setValue(0.05);
		ddHDDMaStrict.driftConfidenceOption.setValue(confidences.get(3));
			
		HDDM_A_Test ddHDDMaMed = new HDDM_A_Test();
		//ddHDDMaMed.lambdaOption.setValue(0.05);
		ddHDDMaMed.driftConfidenceOption.setValue(confidences.get(4));
		
		HDDM_A_Test ddHDDMaLoose = new HDDM_A_Test();
		//ddHDDMaLoose.lambdaOption.setValue(0.05);
		ddHDDMaLoose.driftConfidenceOption.setValue(confidences.get(5));
        
		//Warnings:
		/*
		ddHDDMwStrict.warningConfidenceOption.setValue(0.0025);
		ddHDDMwMed.warningConfidenceOption.setValue(0.05);
		ddHDDMwLoose.warningConfidenceOption.setValue(1.0);
		ddHDDMaStrict.warningConfidenceOption.setValue(0.0025);
		ddHDDMaMed.warningConfidenceOption.setValue(0.05);
		ddHDDMaLoose.warningConfidenceOption.setValue(1.0);*/
		
		//Array Detectores
		detectors[0] = ddHDDMwStrict;
		detectors[1] = ddHDDMwMed;
		detectors[2] = ddHDDMwLoose;
		detectors[3] = ddHDDMaStrict;
		detectors[4] = ddHDDMaMed;
		detectors[5] = ddHDDMaLoose;
		//Nomes dos detectores:
		
		//Com progressão de confiança:
		detectorNames[0] = "ddHDDMwStrict";
		detectorNames[1] = "ddHDDMwMed";
		detectorNames[2] = "ddHDDMwLoose";
		detectorNames[3] = "ddHDDMaStrict";
		detectorNames[4] = "ddHDDMaMed";
		detectorNames[5] = "ddHDDMaLoose";
		
		//Confiança menor do que a do Strict:
		/*
		detectorNames[0] = "ddHDDMwStricter125";
		detectorNames[1] = "ddHDDMwStricter25";
		detectorNames[2] = "ddHDDMwStricter5";
		detectorNames[3] = "ddHDDMaStricter125";
		detectorNames[4] = "ddHDDMaStricter25";
		detectorNames[5] = "ddHDDMaStricter5";*/
		
	}
        
        public boolean detectar(double acuracia){
            double erro = 100 - 100*acuracia;
            detectors[detector_atual].input(erro);
            return detectors[detector_atual].getChange();
        }
        
        public boolean getWarningZone() {
            return detectors[detector_atual].getWarningZone();
        }
        
        public void resetarDeteccao(){
             detectors[detector_atual].resetLearning();
        }
        
        
        public static void comunicacao(AbstractChangeDetector[] detectors, int c, float acuracia){
            double erro = 100 - acuracia;
            detectors[c].input(erro);
            if (detectors[c].getChange()){
                System.out.println("Acurácia: " + acuracia + " Erro: "+erro+" - " + detectors[c].getChange());
                detectors[c].resetLearning();
            } else{
                System.out.println("Acurácia: " + acuracia + " Erro: "+erro);
            }
        }
        

                                   

        

                          
	
}