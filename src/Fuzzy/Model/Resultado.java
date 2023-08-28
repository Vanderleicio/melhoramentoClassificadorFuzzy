/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.Model;

/**
 *
 * @author Allen Hichard
 */
public class Resultado {
    
    public String nome;
    public double acuraciaTrainSAG;
    public double acuraciaTrainCAG;
    public double acuraciaTestSAG;
    public double acuraciaTestCAG;
    public double InterpSAG;
    public double InterpCAG;
    public int qtd_geracoes;

    @Override
    public String toString() {
        return "Resultado{" + "nome=" + nome + ", acuraciaTrainSAG=" + acuraciaTrainSAG + ", acuraciaTrainCAG=" + acuraciaTrainCAG + ", acuraciaTestSAG=" + acuraciaTestSAG + ", acuraciaTestCAG=" + acuraciaTestCAG + ", InterpSAG=" + InterpSAG + ", InterpCAG=" + InterpCAG + ", qtd_geracoes=" + qtd_geracoes + '}';
    }

}
