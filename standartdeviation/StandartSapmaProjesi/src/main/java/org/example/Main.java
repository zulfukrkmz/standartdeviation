package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Sayıları girin (virgülle ayırın): ");
        String input = scanner.nextLine();

        String[] sayiStrDizi = input.split(",");
        short[] sayilar = new short[sayiStrDizi.length];

        for (int i = 0; i < sayiStrDizi.length; i++) {
            sayilar[i] = Short.parseShort(sayiStrDizi[i].trim());
        }

        System.out.print("Thread sayısını girin: ");
        short threadSayisi = scanner.nextShort();

        StandartSapmaHesapla standartSapmaHesaplayici = new StandartSapmaHesapla(sayilar, threadSayisi);
        double standartSapma = standartSapmaHesaplayici.standartSapmaHesapla();

        System.out.println("Sonuç: " + standartSapma);
    }
}
