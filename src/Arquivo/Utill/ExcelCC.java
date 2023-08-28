/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Arquivo.Utill;

import Fuzzy.Model.Resultado;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author User
 */
public class ExcelCC {

    private static String fileName;
    public Row row;
    public HSSFSheet folha;
    public HSSFWorkbook workbook;

    public ExcelCC(String nome, int qtdElemJanela, String sistema) {
        fileName = "Resultados//" + nome + " - " + qtdElemJanela + " - " + sistema + ".xls";

        try{ //edita
            FileInputStream file = new FileInputStream(new File(fileName));
            this.workbook = new HSSFWorkbook(file);
            this.folha = workbook.getSheetAt(0);
            this.row = folha.getRow(folha.getPhysicalNumberOfRows());
           
        } catch(IOException e){ // se não encontrar o arquivo, cria o arquivo
            this.workbook = new HSSFWorkbook();
            this.folha = workbook.createSheet("Resultados " + nome);
        }

    }
    
    public ExcelCC(String arquivoResultados, String nomeExperimento) {
    	fileName = "Resultados//Resumo-" + nomeExperimento + ".xls";

        try{ //edita
            FileInputStream file = new FileInputStream(new File(fileName));
            this.workbook = new HSSFWorkbook(file);
            this.folha = workbook.getSheetAt(0);
            this.row = folha.getRow(folha.getPhysicalNumberOfRows());
           
        } catch(IOException e){ // se não encontrar o arquivo, cria o arquivo
            this.workbook = new HSSFWorkbook();
            this.folha = workbook.createSheet("Resultados Gerais");
        }
        
        criarResultados("Resultados//" + arquivoResultados, nomeExperimento);
    }

    public void salvar(){
        try {
            FileOutputStream out = new FileOutputStream(new File(ExcelCC.fileName));
            this.workbook.write(out);
            out.close();
            //System.out.println("Arquivo Excel criado/editado com sucesso!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro na edição do arquivo!");
        }
    
    }
    public void criarCabecalho() {
        Row row = folha.createRow(0);
        Cell cabecalho0 = row.createCell(0);
        cabecalho0.setCellValue("GMean");
        Cell cabecalho1 = row.createCell(1);
        cabecalho1.setCellValue("Interpretabilidade");
        Cell cabecalho2 = row.createCell(2);
        cabecalho2.setCellValue("Regras");
        Cell cabecalho3 = row.createCell(3);
        cabecalho3.setCellValue("Desvio");
        Cell cabecalho4 = row.createCell(4);
        cabecalho4.setCellValue("Media Idade");
        Cell cabecalho5 = row.createCell(5);
        cabecalho5.setCellValue("Media Utilidade");
        Cell cabecalho6 = row.createCell(6);
        cabecalho6.setCellValue("Desvio padrão Idade");
        Cell cabecalho7 = row.createCell(7);
        cabecalho7.setCellValue("Desvio padrão Utilidade");
        Cell cabecalho9 = row.createCell(9);
        cabecalho9.setCellValue("Desvios Detectados");

        
        salvar();
    }

    

    public void criarCelulas(double gMean, double interp, String desvio, int regras, double mediaIdade, double mediaUtilidade, double desvioIdade, double desvioUtilidade) {
        int rownum = folha.getPhysicalNumberOfRows();
        row = folha.createRow(rownum++);
        
        Cell colunaA = row.createCell(0);
        colunaA.setCellValue(gMean);

        Cell colunaB = row.createCell(1);
        colunaB.setCellValue(interp);

        Cell colunaC = row.createCell(2);
        colunaC.setCellValue(regras);

        Cell colunaD = row.createCell(3);
        colunaD.setCellValue(desvio);
        
        Cell colunaE = row.createCell(4);
        colunaE.setCellValue(mediaIdade);
        
        Cell colunaF = row.createCell(5);
        colunaF.setCellValue(mediaUtilidade);
        
        Cell colunaG = row.createCell(6);
        colunaG.setCellValue(desvioIdade);
        
        Cell colunaH = row.createCell(7);
        colunaH.setCellValue(desvioUtilidade);
        
        salvar();
        
        
    }
    
    public void criarQntdDesvios(int qntDesviosDetectados, int qntDesviosEsperados) {
    	row = folha.createRow(1);
    	
    	Cell colunaI = row.createCell(9);
        colunaI.setCellValue(qntDesviosDetectados);
        
        salvar();
    	
    }
    
    public void criarResultados(String arquivoResultados, String nomeExperimento) {
    	String [] datasetsA = {"A100Agrawal", "A1000Agrawal", "A100LED", "A1000LED","A100STAGGER", "A1000STAGGER"};
    	String [] datasetsG = {"G50Agrawal", "G500Agrawal", "G50LED", "G500LED", "G50STAGGER", "G500STAGGER"};
    	String [] tiposDetect = {"Loose", "Med", "Strict"};
    	String [] metricas = {"Gmean", "AUC", "F1Score"};
    	
    	//Fuzzy Desvios Abruptos
    	int linhaInicial = 2;
    	int colInicial = 3;
    	int cont;
    	
    	row = folha.getRow(linhaInicial) == null? folha.createRow(linhaInicial): folha.getRow(linhaInicial);
    	
    	for (int i = colInicial; i < datasetsA.length + colInicial; i++) {
    		Cell celulaA = row.createCell(i);
    		celulaA.setCellValue(datasetsA[i - colInicial]);
    	}
    	
    	cont = 0;
		for (int j = (linhaInicial + 1); j < (tiposDetect.length * 3) + (linhaInicial + 1); j = j + 3) {
			row = folha.getRow(j) == null? folha.createRow(j): folha.getRow(j);
	    	Cell celulaB = row.createCell((colInicial - 2));
			celulaB.setCellValue(tiposDetect[cont++]);
			
			for (int k = j; k < metricas.length + j; k++) {
				row = folha.getRow(k) == null? folha.createRow(k): folha.getRow(k);
				Cell celulaC = row.createCell((colInicial - 1));
				celulaC.setCellValue(metricas[k - j]);
			}
		}
    	
		//Fuzzy Desvios Graduais
    	linhaInicial = 16;
    	colInicial = 3;
    	
    	row = folha.getRow(linhaInicial) == null? folha.createRow(linhaInicial): folha.getRow(linhaInicial);
    	for (int i = colInicial; i < datasetsG.length + colInicial; i++) {
    		Cell celulaC = row.createCell(i);
    		celulaC.setCellValue(datasetsG[i - colInicial]);
    	}
    	
    	cont = 0;
		for (int j = (linhaInicial + 1); j < (tiposDetect.length * 3) + (linhaInicial + 1); j = j + 3) {
			row = folha.getRow(j) == null? folha.createRow(j): folha.getRow(j);
	    	Cell celulaB = row.createCell((colInicial - 2));
			celulaB.setCellValue(tiposDetect[cont++]);
			
			for (int k = j; k < metricas.length + j; k++) {
				System.out.println("j:" + j + "k:" + k);
				row = folha.getRow(k) == null? folha.createRow(k): folha.getRow(k);
				Cell celulaC = row.createCell((colInicial - 1));
				celulaC.setCellValue(metricas[k - j]);
			}
		}
		
		//Fuzzy Gmeans Abruptos
    	linhaInicial = 2;
    	colInicial = 12;
    	
    	row = folha.getRow(linhaInicial) == null? folha.createRow(linhaInicial): folha.getRow(linhaInicial);
    	
    	for (int i = colInicial; i < datasetsA.length + colInicial; i++) {
    		Cell celulaA = row.createCell(i);
    		celulaA.setCellValue(datasetsA[i - colInicial]);
    	}
    	
    	cont = 0;
		for (int j = (linhaInicial + 1); j < (tiposDetect.length * 3) + (linhaInicial + 1); j = j + 3) {
			row = folha.getRow(j) == null? folha.createRow(j): folha.getRow(j);
	    	Cell celulaB = row.createCell((colInicial - 2));
			celulaB.setCellValue(tiposDetect[cont++]);
			
			for (int k = j; k < metricas.length + j; k++) {
				System.out.println("j:" + j + "k:" + k);
				row = folha.getRow(k) == null? folha.createRow(k): folha.getRow(k);
				Cell celulaC = row.createCell((colInicial - 1));
				celulaC.setCellValue(metricas[k - j]);
			}
		}
		//Fuzzy Gmeans Graduais
    	linhaInicial = 16;
    	colInicial = 12;
    	
    	row = folha.getRow(linhaInicial) == null? folha.createRow(linhaInicial): folha.getRow(linhaInicial);
    	for (int i = colInicial; i < datasetsG.length + colInicial; i++) {
    		Cell celulaC = row.createCell(i);
    		celulaC.setCellValue(datasetsG[i - colInicial]);
    	}
    	
    	cont = 0;
		for (int j = (linhaInicial + 1); j < (tiposDetect.length * 3) + (linhaInicial + 1); j = j + 3) {
			row = folha.getRow(j) == null? folha.createRow(j): folha.getRow(j);
	    	Cell celulaB = row.createCell((colInicial - 2));
			celulaB.setCellValue(tiposDetect[cont++]);
			
			for (int k = j; k < metricas.length + j; k++) {
				System.out.println("j:" + j + "k:" + k);
				row = folha.getRow(k) == null? folha.createRow(k): folha.getRow(k);
				Cell celulaC = row.createCell((colInicial - 1));
				celulaC.setCellValue(metricas[k - j]);
			}
		}

		
		try {
        	BufferedReader br = new BufferedReader(new FileReader(arquivoResultados));
        	String linha, classificador, dataset, metrica, gmean, detector, desvios;
			String[] splitada;
			int col, lin;
        	while (br.ready()) {
				linha = br.readLine();
				splitada = linha.split("-");
			    classificador = splitada[0];
			    dataset = splitada[1];
			    detector = splitada[3];
			    metrica = splitada[4].split(":")[0];
			    gmean = splitada[4].split(":")[1];
			    desvios = splitada[5];
				
			    lin = dataset.contains("G5")? 17: 3;
			    
			    for (int i=0; i < tiposDetect.length; i++) {
			    	if (detector.contains(tiposDetect[i])) {
			    		lin += i * 3;
			    	}
			    	if (metrica.contains(metricas[i])) {
			    		lin += i;
			    	}
			    }
			    //L: 6 C:D
			    col = 3;
			    for (int j=0; j < datasetsA.length; j++) {
			    	if (dataset.contains(datasetsA[j])) {
			    		col += j;
			    	} else if(dataset.contains(datasetsG[j])) {
			    		col += j;
			    	}
			    }
			    
			    System.out.println("l:" + lin + " c:" + col);
			    row = folha.getRow(lin) == null? folha.createRow(lin): folha.getRow(lin);
		    	Cell celula1 = row.createCell((col));
				celula1.setCellValue(gmean);
				
				Cell celula2 = row.createCell((col + 9));
				celula2.setCellValue(desvios);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	salvar();
    }

}
