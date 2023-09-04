/**
 * 
 */
package Treinos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.RecognitionException;

import java.util.Random;
import Arquivo.Utill.DiretorioCC;
import Arquivo.Utill.ExcelCC;
import Arquivo.Utill.KeelCC;
import Arquivo.Utill.AdaptarARFF;
import Concept.Controller.DriftController;
import Fuzzy.ArquivoFLC.FormateCC;
import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.ControllerNaoEstacionario.ControllerConceptFuzzy;
import Fuzzy.ControllerNaoEstacionario.Metricas;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import Fuzzy.Raciocínio.Classico;
import Treinos.Balanceamento;

/**
 * @author Vanderleicio
 *
 */
public class TreinosDetectores {

	/**
	 * @param args

	 */
	public static void main(String[] args) throws SecurityException, ClassNotFoundException, IOException, RecognitionException, Exception {
		/*
		String caminhoTxt = System.getProperty("user.dir") + "\\Resultados\\gMeanMedio-Cenario1.txt";
		ExcelCC teste = new ExcelCC("gMeanMedio-Cenario1.txt", "ExperimentoCenario1");
		ExcelCC teste1 = new ExcelCC("gMeanMedio-CenarioIncremental-Idade.txt", "ExperimentoIncrementalIdade"); 
		ExcelCC teste2 = new ExcelCC("gMeanMedio-CenarioIncrementa-Utilidade.txt", "ExperimentoIncrementalUtilidade"); 
		*/

		determinarCenario(2);
		
		
	}
	
	public static void determinarCenario(int cenario) throws SecurityException, ClassNotFoundException, RecognitionException, Exception {
		String caminhoTxt, caminho, caminho2, nomeCenario;
		FileWriter arq, arq1;
		
		caminhoTxt = System.getProperty("user.dir") + "\\Resultados";
		switch(cenario) {
		case 0:
			//Gerar resumo na planilha
			ExcelCC teste = new ExcelCC("gMeanMedio-NovasInstancias.txt", "ExperimentoNovasInstancias");
			System.out.println("Foi");
			break;
		
		case 1:
			//Testar todos os datasets artificiais, com todos os detectores no Fuzzy e no NB
			nomeCenario = "BaseReiniciada";
			
			arq = new FileWriter(caminhoTxt + "\\metricasMedias-" + nomeCenario + ".txt");
			for (int j=0; j <2; j++) {
				if (j==0) {
					caminho = "\\DatasetsArtificiais\\Fuzzy\\";
				}else {
					caminho = "\\DatasetsReais";
				}
				for (Metricas metrica: Metricas.values()) {
					for (int i=0; i < 6; i++) {
						if (i <= 2) {
							caminho2 = caminho + (j == 1?"":"Graduais");
							ControllerConceptFuzzy.treinar(i, caminho2, arq, 0, metrica, nomeCenario);
						} else {
							caminho2 = caminho + (j == 1?"":"Abruptos");
							ControllerConceptFuzzy.treinar(i, caminho2, arq, 0, metrica, nomeCenario);
							
						}
					}				
					
				}
			}
			arq.close();
			break;
		
		case 2:
			//Testar todos os datasets naturais com todos os detectores no Fuzzy;
			String [] cenarios = {"considerandoNovasRegras",  "NAOconsiderando"};
			for (int k=0; k < 2; k++) {
				nomeCenario = cenarios[k];
				arq1 = new FileWriter(caminhoTxt + "\\metricasMedias-" + nomeCenario + ".txt");
				for (int j=0; j <2; j++) {
					
					if (j==0) {
						caminho = "\\DatasetsArtificiais\\Fuzzy\\";
					}else {
						caminho = "\\DatasetsReais";
					}
					
					for (Metricas metrica: Metricas.values()) {
						for (int i=0; i < 6; i++) {
							if (i <= 2) {
								caminho2 = caminho + (j == 1?"":"Graduais");
								ControllerConceptFuzzy.treinarIncremental(i, caminho2, arq1, k, metrica, nomeCenario);
							} else {
								caminho2 = caminho + (j == 1?"":"Abruptos");
								ControllerConceptFuzzy.treinarIncremental(i, caminho2, arq1, k, metrica, nomeCenario);
								
							}
						}				
						
					}
				}
				
			}

			break;
			
		case 3:
			
			FileWriter arq3;
			String [] cenarios1 = {"tNormaSempretNormaDesvios",  "tNormaSempretNormaTdsJanelas", "tNormaSempreFSArtigoDesvios", "tNormaSempreFSArtigoTdsJanelas"};
			Metricas metrica = Metricas.F1SCORE;
			for (int i = 0; i < 2; i++) {				
				nomeCenario = cenarios1[i];
				String nomeArq = cenarios1[i] + "resultado";
				arq3 = new FileWriter(caminhoTxt + "\\TreinosParametroUtilidade\\" + nomeArq + ".txt");
				caminho = "\\DatasetsArtificiais\\Fuzzy\\";
				
				ControllerConceptFuzzy.treinarIncremental(1, caminho + "Graduais", arq3, i, metrica, nomeCenario);
				ControllerConceptFuzzy.treinarIncremental(3, caminho + "Abruptos", arq3, i, metrica, nomeCenario);
				
				arq3.close();		
			}

			break;
			
	}
	}
	/*
	public static void treinarCenario(int numJanelas, ArrayList<Float> lambdas, ArrayList<Float> confidences, boolean tdsDetec, int detector) throws SecurityException, ClassNotFoundException, IOException, RecognitionException, JMException, Exception {
		ControllerConceptFuzzy.elemJanelas = numJanelas;
		ControllerConceptFuzzy.lambdas = lambdas;
		ControllerConceptFuzzy.confidences = confidences;
		
		if (tdsDetec) {
			for (int i = 0; i < 6; i++) {
				ControllerConceptFuzzy.treinar(i);
			}
		} else {
			ControllerConceptFuzzy.treinar(detector);
		}
	}
	public static void contarClasses() throws FileNotFoundException, IOException {
		float nExemplos, nExClasse;
		float porcentagem;
		DiretorioCC dir = new DiretorioCC();
		Iterator exemplos = dir.problemas().iterator();
		
		String caminhoTxt = System.getProperty("user.dir") + "\\Repositorio";
		FileWriter arq = new FileWriter(caminhoTxt + "\\ClassesPorDataset.txt");
		PrintWriter gravarArq = new PrintWriter(arq);
		
		while (exemplos.hasNext()) {
			String caminho = (String) exemplos.next();
			
			KeelCC dataset = new KeelCC();
			dataset.extrair(caminho);
			nExemplos = dataset.instancias.size();
			
			gravarArq.printf(dataset.nomeArquivo + "%n");
			System.out.println(dataset.nomeArquivo + "\n");
			
			for (int i = 0; i < dataset.contClasses.size(); i++) {
				
				nExClasse = dataset.contClasses.get(i);
				porcentagem = nExClasse/nExemplos * 100;
				gravarArq.printf(((String) dataset.nomesClasses.get(i)) + ": " + dataset.contClasses.get(i) + " = "+ porcentagem  + "%n");
				System.out.println(dataset.nomesClasses.get(i) + ": " + nExClasse + " = " + porcentagem + "%");
				
			}
			gravarArq.printf("==========================================================%n");
			System.out.println("==========================================================");
			
		}
		arq.close();
	}
	
	public static void criarFLCsemTXT() throws IOException {
		DiretorioCC dir = new DiretorioCC();
		Iterator exemplos = dir.problemas().iterator();
		
		String caminho = (String) exemplos.next();
		System.out.println(caminho);
		
		FileWriter arq = new FileWriter(caminho.replace("ComConcept\\poker-lsn.arff", "elecNormNew-FLC.txt"));
		PrintWriter gravarArq = new PrintWriter(arq);
		
		
		KeelCC dataset = new KeelCC();
		FormateCC formate = new FormateCC();
		dataset.extrair(caminho);
		formate.gerarFLC(dataset);
		
		gravarArq.printf(formate.jLogicFuzzy);
		arq.close();
		System.out.println(formate.jLogicFuzzy);
	}
	
	public static void treinarModeloSemDetector() throws RecognitionException, FileNotFoundException, IOException {
		boolean desbalanceado = false;
		DiretorioCC dir = new DiretorioCC();
		Iterator exemplos = dir.problemas().iterator();
		double acuracia;
		double InterpSAG;

		Classico teste;

		double acuracia_pos_treino;
		double interp_pos_treino;
		boolean teveDesvio;
		int qtdDesvio = 0;

		while (exemplos.hasNext()) {

			List<Regra> melhoresRegras;

			// Inicialização do Concept

			System.out.println("using NO detector ");


			String caminho = (String) exemplos.next();
			KeelCC dataset = new KeelCC();
			dataset.setqtdElemJanelas(25);
			
			FormateCC formate = new FormateCC();
			dataset.extrair(caminho);
			formate.gerarFLC(dataset);
			ExcelCC excel = new ExcelCC(dataset.nomeArquivo, dataset.qtdElemJanelas, "SemDetector");
			excel.criarCabecalho();


			// Criação do FLC

			//System.out.println(formate.jLogicFuzzy);

			//Primeira Classificação
			System.out.println("Construindo Primeiro Sistema Fuzzy");
			WangMendel wm = new WangMendel(formate.jLogicFuzzy, (List<Instancia>) dataset.janelas.get(0), dataset.nomesCaracteristicas);
			wm.criarRegras();
			melhoresRegras = wm.regras;

			//indiceJanela < dataset.janelas.size()
			for (int indiceJanela = 1; indiceJanela < dataset.janelas.size(); indiceJanela++) {
				teste = new Classico(wm.fis, melhoresRegras, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
				acuracia = teste.classificar(desbalanceado);
				InterpSAG = teste.interpretabilidade();

				System.out.println("Bloco: " + indiceJanela + ", Acuracia: " + acuracia);

				excel.criarCelulas(acuracia, 0, InterpSAG, 0, 0, "----");

			}
		}
	
	}*/

}
