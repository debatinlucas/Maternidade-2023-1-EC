package br.com.dlweb.maternidade.bebe;

public class Bebe {
    private int altura;
    private Float peso;
    private String nome, data_nascimento, medicoId, maeId;

    public Bebe() {}

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(String data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }

    public String getMaeId() {
        return maeId;
    }

    public void setMaeId(String maeId) {
        this.maeId = maeId;
    }
}
