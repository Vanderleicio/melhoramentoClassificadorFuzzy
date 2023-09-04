/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Arquivo.Utill;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import Fuzzy.Model.Instancia;
/**
 *
 * @author Allen Hichard
 */
public class KeelCC {
    public int qtdElemJanelas = 50;
    public List limitesInferiores;
    public List limitesSuperiores;
    public List nomesCaracteristicas;
    public List nomesDiscretos;
    public List nomesClasses;
    public List tipos;
    public List janelas;
    public ArrayList<Integer> contClasses;
    public List<Instancia> instancias;
    public String nomeArquivo;
    
    
    public KeelCC(){
        this.limitesInferiores = new ArrayList();
        this.limitesSuperiores = new ArrayList();
        this.nomesDiscretos = new ArrayList();
        this.nomesClasses = new ArrayList();
        this.nomesCaracteristicas = new ArrayList();
        this.tipos = new ArrayList();
        this.instancias = new ArrayList();
        this.janelas = new ArrayList();
    }
    
    public void extrair(String dataset) throws FileNotFoundException, IOException{
    	BufferedReader br = new BufferedReader(new FileReader(dataset));
        while(br.ready()){ 
            String linha = br.readLine();
            String normalizada = Normalizer.normalize(linha, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .replace(" ", ""); 
            if (normalizada.contains("@relation")){
                this.nomeArquivo = normalizada.split("@relation")[1].trim();
            } else if (normalizada.contains("@attribute") && normalizada.contains("[")){
                //System.out.println(linha.split(" ")[2]);
                this.tipos.add(linha.split(" ")[2]);
                
                //this.tipos.add("real");
                String dominio[] = normalizada.split("\\[")[1].split("\\]")[0].split(",");
                String addDiscreto[] = {"0"}; 
                this.limitesInferiores.add(dominio[0]);
                this.limitesSuperiores.add(dominio[1]);
                this.nomesDiscretos.add(addDiscreto);
            }else if (normalizada.contains("@attribute") && (normalizada.contains("{") && (normalizada.contains("class") || normalizada.contains("Class")))){
                String classes[] = normalizada.split("\\{")[1].split("\\}")[0].split(",");
                for (String classe : classes){
                    this.nomesClasses.add(classe);
                }
            }else if (normalizada.contains("@attribute") && normalizada.contains("{")){
                //System.out.println(linha.split(" ")[2]);
                this.tipos.add("discreto");
                
                //this.tipos.add("real");
                String classes[] = normalizada.split("\\{")[1].split("\\}")[0].split(",");
        
                this.limitesInferiores.add("D");
                this.limitesSuperiores.add("D");
                this.nomesDiscretos.add(classes);
            }else if (normalizada.contains("@inputs")){
                String caracteristicas[] = normalizada.split("@inputs")[1].split(",");
                for (String caracteristica : caracteristicas){
                    this.nomesCaracteristicas.add(caracteristica.replace("/", ""));
                }
            }else if (normalizada.contains("@input")){
                String caracteristicas[] = normalizada.split("@input")[1].split(",");
                for (String caracteristica : caracteristicas){
                    this.nomesCaracteristicas.add(caracteristica.replace("/", ""));
                }
             }else if (normalizada.contains("@data")){
                 criarInstancias(br);
             }
        }
        
        criarJanelas();
        
        /*System.out.println(this.nomeArquivo);
        System.out.println(this.limitesInferiores);
        System.out.println(this.limitesSuperiores);
        System.out.println(this.nomesCaracteristicas);
        System.out.println(this.nomesClasses);
        System.out.println(this.instancias.size());
        */ 
	br.close();
    }
    
    
    private void criarInstancias(BufferedReader br) throws IOException{
    	while(br.ready()){
            String linha = Normalizer.normalize(br.readLine(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                    .replace(" ", "");
            String[] instancia = linha.split(",");
            int separador = this.nomesCaracteristicas.size();
            List<Double> caracteristicas = new ArrayList();
            for (int i = 0; i < separador; i++){
                caracteristicas.add(Double.parseDouble(instancia[i]));
            }
          
            int indexClasse = this.nomesClasses.indexOf(instancia[separador]);
  
            this.instancias.add(new Instancia(caracteristicas, indexClasse));
        }
    }
    
    
    private void criarJanelas(){
        for (int i = 0; i < this.instancias.size(); i+=this.qtdElemJanelas){
            if (i+this.qtdElemJanelas < this.instancias.size())
                this.janelas.add(this.instancias.subList(i, i+this.qtdElemJanelas));
            else 
                this.janelas.add(this.instancias.subList(i, this.instancias.size()));
        }
        /*for (int i = 0; i< this.janelas.size(); i++){
            System.out.println(i);        }*/
    }
    
    public void setqtdElemJanelas(int qtdElemJanelas) {
    	this.qtdElemJanelas = qtdElemJanelas;
    }
}

