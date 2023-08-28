package Arquivo.Utill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class AdaptarARFF {
	public String caminhoNovoArquivo;
	
	public AdaptarARFF(String caminho) throws IOException {
		this.caminhoNovoArquivo = caminho;
	}
	
	public static void main(String[] args) throws IOException {
		/*
		String teste = "123 {4,5,6}";
		String[] teste1 = teste.split("\\{")[1].split("}")[0].split(",");
		ArrayList<String[]> teste2 = new ArrayList<String[]>();
		teste2.add(teste1);
		for (String[] teste3 : teste2) {
			System.out.println(teste3[0]);
		}*/
		
		String rota = System.getProperty("user.dir") + "\\Repositorio\\ComConcept\\DatasetsArtificiais\\Novos\\";
		AdaptarARFF adapt = new AdaptarARFF(rota);
		adapt.converterArquivoNumeric(rota + "Agrawal-100-Abrupto.ARFF", "Teste.ARFF");
		System.out.println("Convertido");
	}
	
	public void corrigirARFF(String caminho) throws IOException {
		String arquivoEntrada = caminho;
        String arquivoSaida = caminho.replace(".csv", ".arff");

        BufferedReader br = new BufferedReader(new FileReader(arquivoEntrada));
        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoSaida));
            
        while (br.ready()) {
        	String linha = br.readLine();
            linha += ","; // Adiciona uma vírgula no final de cada linha
            bw.write(linha);
            bw.newLine(); // Adiciona uma quebra de linha após cada linha
        }
        br.close();
        bw.close();
	}
	
	public void converterArquivoNumeric(String caminho, String nomeArquivo) throws IOException {
		String arquivoEntrada = caminho;
        String arquivoSaida = this.caminhoNovoArquivo + nomeArquivo;
        ArrayList<String> nomesCaracts = new ArrayList<String>();
        
        ArrayList<Integer> eNumeric = new ArrayList<Integer>();
        
        ArrayList<String> nomesNumeric = new ArrayList<String>();
        ArrayList<String> nomesDiscretas = new ArrayList<String>();
        
        ArrayList<Integer> numNumeric = new ArrayList<Integer>();
        
        ArrayList<String> nomeClasses = new ArrayList<String>();
        ArrayList<Integer> numClasses =new ArrayList<Integer>();
        ArrayList<Double> limiteSup = new ArrayList<Double>();
        ArrayList<Double> limiteInf = new ArrayList<Double>();
        
        BufferedReader br1 = new BufferedReader(new FileReader(arquivoEntrada));
        BufferedReader br = new BufferedReader(new FileReader(arquivoEntrada));
        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoSaida));
        ArrayList<String[]> nomeCaract = new ArrayList<String[]>();
        
        while (br1.ready()) {
        	String linha1 = br1.readLine();
        	
        	if(linha1.contains("@attribute")) {
        		String nome = linha1.split(" ")[1];
        		String tipo = linha1.split(" ")[2];
        		nomesCaracts.add(nome);
        		if (tipo.equals("numeric")) {
        			eNumeric.add(1);
        		} else {
        			eNumeric.add(0);
        		}
        	}else if(linha1.contains("@data")) {
        		if(limiteSup.size() == 0) {
        			for (int i = 0; i < nomesCaracts.size(); i++) {
        				limiteSup.add(0.0);
        				limiteInf.add(Double.MAX_VALUE);
        			}
        		}
        	}else if(linha1.contains("@relation")) {
        		
        	}else if(linha1.length() >= 1) {
        		String[] dados = linha1.split(",");
        		for (int i = 0; i < dados.length; i++) {
        			if (eNumeric.get(i) == 1) {
        				
        				if(limiteSup.get(i) < (Double.parseDouble(dados[i])) ) {
        					limiteSup.set(i, Double.parseDouble(dados[i]));
        				}
        				if(limiteInf.get(i) > (Double.parseDouble(dados[i])) ) {
        					limiteInf.set(i, Double.parseDouble(dados[i]));
        				}
        			}
        			
        		}
        	}
        }

        
        while (br.ready()) {
        	String linha = br.readLine();
        	
        	//Título do arquivo
        	if(linha.contains("@relation")) {
        		bw.write(linha);
        		bw.newLine();
        		bw.newLine();
        	//Classes a ser classificadas
        	}else if (linha.contains("{") && linha.contains("class")) {
        		String[] classes = linha.split("\\{")[1].split("}")[0].split(",");
        		
        		bw.write("@attribute class {");
        		for (int i = 0; i< classes.length; i++) {
        			if (i != classes.length - 1) {
        				nomeClasses.add(classes[i]);
        				bw.write(i + ",");
        			} else {
        				nomeClasses.add(classes[i]);
        				bw.write(i + "");
        			}
        		}
        		bw.write("}");
        		bw.newLine();
        		bw.newLine();
        	//Características discretas
        	} else if(linha.contains("{")) {
        		String[] caracts = linha.split("\\{")[1].split("}")[0].split(",");
        		nomeCaract.add(caracts);
        		bw.write("@attribute " + linha.split(" ")[1] + " real {");
        		for (int i = 0; i< caracts.length; i++) {
        			if (i != caracts.length - 1) {
        				bw.write(i + ",");
        			} else {
        				bw.write(i + "");
        			}
        		}
        		bw.write("}");
        		bw.newLine();
        	} else if(linha.contains("numeric")) {
        		bw.write("@attribute " + linha.split(" ")[1] + " real [");
        		int index = nomesCaracts.indexOf(linha.split(" ")[1]);
        		bw.write(Double.toString(limiteInf.get(index)) + ",");
        		bw.write(Double.toString(limiteSup.get(index)) + "]");
        		bw.newLine();
        	//@data
        	}else if(linha.contains("@data")) {
        		bw.write("@data");
        		bw.newLine();
        		bw.newLine();
        	//Dados 
        	} else if (linha.length() >= 1){
        		String[] dados = linha.split(",");
        		int cont = 0;
        		for (int i = 0; i < eNumeric.size(); i++) {
        			if (i != dados.length - 1) {
	        			if (eNumeric.get(i) == 1) {
	        				bw.write(dados[i]+",");
	        			} else {
	        				String[] dadoParcial = nomeCaract.get(cont++);
	        				for (int j = 0; j < dadoParcial.length; j++) {
	            				if (dadoParcial[j].equals(dados[i])) {
	            					bw.write(j+",");
	            				}
	            			}
	        			}
        			} else if(i == dados.length - 1){
        				bw.write(nomeClasses.indexOf(dados[i])+",");
        				bw.newLine();
        			}
        		}
        	}
        }
        br1.close();
        bw.close();
	}
	
	public void converterArquivo(String caminho, String nomeArquivo) throws IOException {
		String arquivoEntrada = caminho;
        String arquivoSaida = this.caminhoNovoArquivo + nomeArquivo;
        ArrayList<String[]> nomeCaract = new ArrayList<String[]>();
        ArrayList<Integer> numCaract =new ArrayList<Integer>();
        ArrayList<String> nomeClasses = new ArrayList<String>();
        ArrayList<Integer> numClasses =new ArrayList<Integer>();
        
        
        BufferedReader br = new BufferedReader(new FileReader(arquivoEntrada));
        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoSaida));
            
        while (br.ready()) {
        	String linha = br.readLine();
        	//Título do arquivo
        	
        	if(linha.contains("@relation")) {
        		bw.write(linha);
        		bw.newLine();
        		bw.newLine();
        	//Classes a ser classificadas
        		
        	}else if (linha.contains("{") && linha.contains("class")) {
        		String[] classes = linha.split("\\{")[1].split("}")[0].split(",");
        		
        		bw.write("@attribute class {");
        		for (int i = 0; i< classes.length; i++) {
        			if (i != classes.length - 1) {
        				nomeClasses.add(classes[i]);
        				bw.write(i + ",");
        			} else {
        				nomeClasses.add(classes[i]);
        				bw.write(i + "");
        			}
        		}
        		bw.write("}");
        		bw.newLine();
        	//Características discretas
        	} else if(linha.contains("{")) {
        		String[] caracts = linha.split("\\{")[1].split("}")[0].split(",");
        		nomeCaract.add(caracts);
        		bw.write("@attribute " + linha.split(" ")[1] + " real {");
        		for (int i = 0; i< caracts.length; i++) {
        			if (i != caracts.length - 1) {
        				bw.write(i + ",");
        			} else {
        				bw.write(i + "");
        			}
        		}
        		bw.write("}");
        		bw.newLine();
        	//@data
        	} else if(linha.contains("@data")) {
        		bw.write("@data");
        		bw.newLine();
        	//Dados 
        	} else if (linha.length() >= 1){
        		String[] dados = linha.split(",");
        		for (int i = 0; i < dados.length; i++) {
        			if (i != dados.length - 1) {
        				String[] dadoParcial = nomeCaract.get(i);
            			for (int j = 0; j < dadoParcial.length; j++) {
            				if (dadoParcial[j].equals(dados[i])) {
            					bw.write(j+",");
            				}
            			}
        			} else if(i == dados.length - 1){
        				bw.write(nomeClasses.indexOf(dados[i])+",");
        				bw.newLine();
        			}
        			
        		}
        	}
        }
        br.close();
        bw.close();
	}
}
