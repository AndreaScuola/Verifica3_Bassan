package org.example.modelli;

public class Animale {
    private Integer id;
    private String nome;
    private String specie;
    private String habitat;
    private String dieta;

    public Integer getId() {return id;}
    public String getNome() {return nome;}
    public String getSpecie() {return specie;}
    public String getHabitat() {return habitat;}
    public String getDieta() {return dieta;}

    @Override
    public String toString(){
        return "ID: " + id + ", nome: " + nome + ", specie: " + specie + ", habitat: " + habitat + ", dieta: " + dieta;
    }
}
