/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Arquivo.Utill;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Allen Hichard
 */
public class DiretorioCC {
    
    public String diretorioRaiz;
    
    public DiretorioCC(String tipoDataset){
        this.diretorioRaiz = System.getProperty("user.dir")+"\\Repositorio\\ComConcept" + tipoDataset;
    }
    
    public List<String> problemas(){
        List listDatasets = new ArrayList();
        File pastas = new File(this.diretorioRaiz);
        String problemas[] = pastas.list();
        for (String problema : problemas){
            listDatasets.add(this.diretorioRaiz + "\\" + problema);
        }
        return listDatasets;
        
    }
    
    public static void main(String[] args){
        DiretorioCC dir = new DiretorioCC("\\DatasetsArtificiais\\Fuzzy\\Abruptos");
        System.out.println(dir.problemas());
    }
                  
}

    



/*



        for dataset in os.listdir(caminhoDatasets+"/"+pastaDataset):
            if dataset.__contains__(treino_teste+".dat"):
                datasetsList.append(caminhoDatasets+"/"+pastaDataset+"/"+dataset)
    return datasetsList

"""print(datasets("tra"))
print(datasets("tst"))"""
*/