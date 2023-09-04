/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.ControllerNaoEstacionario;

import Fuzzy.BaseConhecimento.WangMendel;
import Fuzzy.ArquivoFLC.FormateCC;
import Fuzzy.Raciocínio.Classico;
import java.io.FileWriter;
import java.io.PrintWriter;

import Arquivo.Utill.DiretorioCC;
import Arquivo.Utill.ExcelCC;
import Arquivo.Utill.KeelCC;
import Concept.Controller.DriftController;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jFuzzyLogic.FIS;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class ControllerConceptFuzzy {
	
	public static int elemJanelas =  50;
	public static ArrayList<Float> lambdas;
	public static ArrayList<Float> confidences;
	
	
	// classificador = null
	public static void treinar(int detector, String tipoDoDataset, FileWriter arquivo, int classificador, Metricas metricaUsada, String nomeCenario) throws IOException, RecognitionException, SecurityException, ClassNotFoundException, Exception {
		boolean desbalanceado = true;
		DiretorioCC dir = new DiretorioCC(tipoDoDataset);
		Iterator exemplos = dir.problemas().iterator();
		double metrica, metricaAcumulada, metricaMedia, InterpSAG;
		Classico teste;
		boolean teveDesvio;
		
		double[] parametros = {0, 0, 0, 0};
		//double [] paramsIdade = {0, 0, 0, 0}; //Maior média, menor média, maior desvio, menor desvio.
		//double [] paramsUtilidade = {0, 0, 0, 0};
		
		while (exemplos.hasNext()) {
			int qtdDesvio = 0;
			List<Regra> melhoresRegras;
			
			// Inicialização do detector de Concept Drift
			DriftController.lambdas = lambdas;
			DriftController.confidences = confidences;
			DriftController drift = new DriftController();
			DriftController.createDetectors();
			DriftController.detector_atual = detector;
			drift.detectors[DriftController.detector_atual].prepareForUse();
			System.out.println("using detector " + drift.detectorNames[DriftController.detector_atual]);

			
			String caminho = (String) exemplos.next();
			
			KeelCC dataset = new KeelCC();
			dataset.setqtdElemJanelas(elemJanelas);
			
			ExcelCC excel;
			String nomeClassif;
		
			// Testando com todos os detectores
			// for (int i = 0; i < n_detectores; i++) {

			// Criação do FLC

			//System.out.println(formate.jLogicFuzzy);
			metricaAcumulada = 0;
			
			//Para o modelo Fuzzy:
			int nRegras = 0;
			nomeClassif = "Fuzzy";
			FormateCC formate = new FormateCC();
			System.out.println(caminho);
			dataset.extrair(caminho);
			formate.gerarFLC(dataset);
			FIS fis = FIS.createFromString(formate.jLogicFuzzy, true);
			
			List<Instancia> instanciasAtuais;
			
			String pastaDeResultados = "FUZZY-" + dataset.nomeArquivo;
			excel = new ExcelCC(pastaDeResultados, dataset.qtdElemJanelas, "SAG" + drift.detectorNames[DriftController.detector_atual] + "-" + metricaUsada.getNome() + "-" + nomeCenario);
			excel.criarCabecalho();
			//Primeira Classificação
			System.out.println(dataset.nomeArquivo);
			System.out.println("Construindo Primeiro Sistema Fuzzy");
			
			boolean multiclasses = dataset.nomesClasses.size() > 2; //Avalia se o dataset tem multiplas classes para o processo de avaliação da performance
			
			instanciasAtuais = new ArrayList<Instancia>((List<Instancia>) dataset.janelas.get(0));
			WangMendel wm = new WangMendel(formate.jLogicFuzzy, instanciasAtuais, dataset.nomesCaracteristicas, dataset.nomesDiscretos);
			wm.criarRegras(0);
			melhoresRegras = wm.regras;
			nRegras = melhoresRegras.size();
			for (int indiceJanela = 1; indiceJanela < dataset.janelas.size(); indiceJanela++) {
				
				teste = new Classico(wm.fis, melhoresRegras, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
				metrica = teste.classificar(desbalanceado, multiclasses, dataset.nomesDiscretos, metricaUsada);
				metricaAcumulada += metrica;
				InterpSAG = teste.interpretabilidade();

				System.out.println("Bloco: " + indiceJanela + ", " + metricaUsada.getNome() + ": " + metrica + ", Regras: " + nRegras);
				teveDesvio = drift.detectar(metrica);

				if(teveDesvio) {
					parametros = wm.calcularEstatisticas(indiceJanela);
				} 
				
				excel.criarCelulas(metrica, InterpSAG, teveDesvio? "Desviou": "----", nRegras, 0, parametros[1], parametros[3]);
				//excel.criarCelulas(metrica, InterpSAG, teveDesvio? "Desviou": "----", nRegras, 0, 0, 0, 0);
				instanciasAtuais = new ArrayList<Instancia>((List<Instancia>) dataset.janelas.get(indiceJanela));
				if(teveDesvio) {
					qtdDesvio++;
					System.out.println("Teve desvio");
					wm = new WangMendel(formate.jLogicFuzzy, instanciasAtuais, dataset.nomesCaracteristicas, dataset.nomesDiscretos);
                    wm.criarRegras(indiceJanela);
					melhoresRegras = wm.regras;
					nRegras = melhoresRegras.size();
				}
				
			}
			drift.resetarDeteccao();

			
			
			excel.criarQntdDesvios(qtdDesvio, 0);
			
			metricaMedia = metricaAcumulada/dataset.janelas.size();
			PrintWriter gravarArq = new PrintWriter(arquivo);
			//gravarArq.printf(nomeClassif + "-" + dataset.nomeArquivo + " - " + dataset.qtdElemJanelas + " - " + drift.detectorNames[DriftController.detector_atual] + "-" + metricaUsada.getNome() + ": " + metricaMedia +"%n");
			gravarArq.printf(nomeClassif + "-" + dataset.nomeArquivo + " - " + dataset.qtdElemJanelas + " - " + drift.detectorNames[DriftController.detector_atual] + "-" + metricaUsada.getNome() + ": " + metricaMedia + "-" + qtdDesvio +"%n");
			// }
		}
		
	}
		
	public static void treinarIncremental(int detector, String tipoDoDataset, FileWriter arquivo, int cenarioFS, Metricas metricaUsada, String nomeCenario) throws IOException, RecognitionException, SecurityException, ClassNotFoundException, Exception {
		boolean desbalanceado = true;
		boolean teveDesvio;
		boolean mediaTodaJanela = false;
		double metrica, metricaAcumulada, metricaMedia, InterpSAG, utilidadeMedia, idadeMedia;
		Classico teste;
		double[] parametros = {0, 0, 0, 0};
		double[] resetParam = {0, 0, 0, 0};
		
		DiretorioCC dir = new DiretorioCC(tipoDoDataset);
		Iterator exemplos = dir.problemas().iterator();

		while (exemplos.hasNext()) {
			int qtdDesvio = 0;
			List<Regra> melhoresRegras;
			
			// Inicialização do Concept
			DriftController.lambdas = lambdas;
			DriftController.confidences = confidences;
			DriftController drift = new DriftController();
			DriftController.createDetectors();
			DriftController.detector_atual = detector;
			drift.detectors[DriftController.detector_atual].prepareForUse();
			System.out.println("using detector " + drift.detectorNames[DriftController.detector_atual]);

			
			String caminho = (String) exemplos.next();
			
			KeelCC dataset = new KeelCC();
			dataset.setqtdElemJanelas(elemJanelas);
			
			ExcelCC excel;
			String nomeClassif;

			metricaAcumulada = 0;
			int nRegras = 0;
			nomeClassif = "Fuzzy";
			FormateCC formate = new FormateCC();
			dataset.extrair(caminho);
			formate.gerarFLC(dataset);
			FIS fis = FIS.createFromString(formate.jLogicFuzzy, true);
			
			List<Instancia> instanciasAtuais;
			
			String pastaDeResultados = "FUZZY-" + dataset.nomeArquivo;
			excel = new ExcelCC(pastaDeResultados, dataset.qtdElemJanelas, "SAG" + drift.detectorNames[DriftController.detector_atual] + "-" + metricaUsada.getNome() + "-" + nomeCenario);
			excel.criarCabecalho();
			//Primeira Classificação
			System.out.println(dataset.nomeArquivo);
			System.out.println("Construindo Primeiro Sistema Fuzzy");
			
			boolean multiclasses = dataset.nomesClasses.size() > 2; //Avalia se o dataset tem multiplas classes para o processo de avaliação da performance
			
			instanciasAtuais = new ArrayList<Instancia>((List<Instancia>) dataset.janelas.get(0));
			WangMendel wm = new WangMendel(formate.jLogicFuzzy, instanciasAtuais, dataset.nomesCaracteristicas, dataset.nomesDiscretos);
			wm.criarRegras(0);
			melhoresRegras = wm.regras;
			nRegras = melhoresRegras.size();
			
			for (int indiceJanela = 1; indiceJanela < dataset.janelas.size(); indiceJanela++) {
				int regrasDescartadas = 0;
				wm.atualizarUtilidade(indiceJanela);
				
				if (mediaTodaJanela){
					parametros = wm.calcularEstatisticas(indiceJanela);
				}
				
				
				//Fim dos testes
				teste = new Classico(wm.fis, melhoresRegras, (List<Instancia>) dataset.janelas.get(indiceJanela), dataset.nomesCaracteristicas);
				metrica = teste.classificar(desbalanceado, multiclasses, dataset.nomesDiscretos, metricaUsada);
				metricaAcumulada += metrica;
				InterpSAG = teste.interpretabilidade();

				System.out.println("Bloco: " + indiceJanela + ", G-mean: " + metrica + ", Regras: " + nRegras);
				teveDesvio = drift.detectar(metrica);
				
				if (teveDesvio) {
					System.out.println("Teve Desvio");
					
					if (cenarioFS == 0) {
						regrasDescartadas = wm.atualizarBase();
					}
					parametros = wm.calcularEstatisticas(indiceJanela);
					qtdDesvio++;
					wm.setNovasInstancias(instanciasAtuais, indiceJanela);
					wm.criarRegras(indiceJanela);
					
					if (cenarioFS == 1) {
						regrasDescartadas = wm.atualizarBase();
					}
					melhoresRegras = wm.regras;
					nRegras = melhoresRegras.size();
				}
				
				excel.criarCelulas(metrica, InterpSAG, teveDesvio? "Desviou": "----", nRegras, regrasDescartadas, parametros[1], parametros[3]);
				instanciasAtuais = new ArrayList<Instancia>((List<Instancia>) dataset.janelas.get(indiceJanela));
				parametros = resetParam;
				
				
			}
			drift.resetarDeteccao();
			
			excel.criarQntdDesvios(qtdDesvio, 0);
			
			metricaMedia = metricaAcumulada/dataset.janelas.size();
			PrintWriter gravarArq = new PrintWriter(arquivo);
			//gravarArq.printf(nomeClassif + "-" + dataset.nomeArquivo + " - " + dataset.qtdElemJanelas + " - " + drift.detectorNames[DriftController.detector_atual] + "-" + metricaUsada.getNome() + ": " + "Range media Idade: " + "[" + paramsIdade[1] + ";" + paramsIdade[0] + "]" + "Range desvio Idade: " + "[" + paramsIdade[3] + ";" + paramsIdade[2] + "]" + "Range media Utilidade: "+ "[" + paramsUtilidade[1] + ";" + paramsUtilidade[0] + "]" + "Range desvio Utilidade: "+ "[" + paramsUtilidade[3] + ";" + paramsUtilidade[2] + "]" +"%n");
			gravarArq.printf(nomeClassif + "-" + dataset.nomeArquivo + " - " + dataset.qtdElemJanelas + " - " + drift.detectorNames[DriftController.detector_atual] + "-" + metricaUsada.getNome() + ": " + metricaMedia + "-" + qtdDesvio +"%n");
		}
		
	}

}


