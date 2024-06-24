package com.example.padel;

public class Item {
    private String Prenotazione;

    private String Giocatore1, Giocatore2, Giocatore3, Giocatore4;

    private String Allenatore;

    private String Data, Orario;
    private String Campo;

    public Item(String prenotazione, String player1, String player2, String player3, String player4, String date, String time, String court){
        this.Prenotazione = prenotazione;
        this.Giocatore1 = player1;
        this.Giocatore2 = player2;
        this.Giocatore3 = player3;
        this.Giocatore4 = player4;
        this.Data = date;
        this.Orario = time;
        this.Campo = court;
    }

    public Item(String prenotazione, String player1, String player2, String player3, String date, String time, String court){
        this.Prenotazione = prenotazione;
        this.Giocatore1 = player1;
        this.Giocatore2 = player2;
        this.Giocatore3 = player3;
        this.Data = date;
        this.Orario = time;
        this.Campo = court;
    }
    public Item(String prenotazione, String player1, String player2, String date, String time, String court){
        this.Prenotazione = prenotazione;
        this.Giocatore1 = player1;
        this.Giocatore2 = player2;
        this.Data = date;
        this.Orario = time;
        this.Campo = court;
    }

    public Item(String prenotazione, String player1, String date, String time, String court){
        this.Prenotazione = prenotazione;
        this.Giocatore1 = player1;
        this.Data = date;
        this.Orario = time;
        this.Campo = court;
    }

//    public Item(String prenotazione, String player1, String coach,  String date, String time, String court){
//        this.Prenotazione = prenotazione;
//        this.Giocatore1 = player1;
//        this.Allenatore = coach;
//        this.Data = date;
//        this.Orario = time;
//        this.Campo = court;
//    }

    public String getGiocatore1() {
        return Giocatore1;
    }

    public void setGiocatore1(String giocatore1) {
        Giocatore1 = giocatore1;
    }

    public String getGiocatore4() {
        return Giocatore4;
    }

    public void setGiocatore4(String giocatore4) {
        Giocatore4 = giocatore4;
    }

    public String getGiocatore2() {
        return Giocatore2;
    }

    public void setGiocatore2(String giocatore2) {
        Giocatore2 = giocatore2;
    }

    public String getGiocatore3() {
        return Giocatore3;
    }

    public void setGiocatore3(String giocatore3) {
        Giocatore3 = giocatore3;
    }

    public String getAllenatore() {
        return Allenatore;
    }

    public void setAllenatore(String allenatore) {
        Allenatore = allenatore;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getOrario() {
        return Orario;
    }

    public void setOrario(String orario) {
        Orario = orario;
    }

    public String getCampo() {
        return Campo;
    }

    public void setCampo(String campo) {
        Campo = campo;
    }

    public String getPrenotazione() {
        return Prenotazione;
    }

    public void setPrenotazione(String prenotazione) {
        Prenotazione = prenotazione;
    }
}
