package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class StandartSapmaHesapla {

    private short[] sayilar;
    private short threadSayisi;

    public StandartSapmaHesapla(short[] sayilar, short threadSayisi) {
        this.sayilar = sayilar;
        if(threadSayisi >=1 && threadSayisi <=20 && (sayilar.length/2) >=threadSayisi){
            this.threadSayisi = threadSayisi;
        }
        else {
            System.out.println("thread sayisi beklentileri karsilamiyor.");
            Runtime.getRuntime().exit(0);
        }
    }

    public double standartSapmaHesapla() {
        int elemanSayisi = sayilar.length;
        int elemanPerThread = elemanSayisi / threadSayisi;
        int kalanEleman = elemanSayisi % threadSayisi;

        ExecutorService executorService = Executors.newFixedThreadPool(threadSayisi);

        try {
            List<Callable<Double>> toplamHesaplamaGorevleri = new ArrayList<>();
            List<Callable<Double>> kareHesaplamaGorevleri = new ArrayList<>();

            for (int i = 0; i < threadSayisi; i++) {
                int start = i * elemanPerThread + Math.min(i, kalanEleman);
                int end = start + elemanPerThread + (i < kalanEleman ? 1 : 0);
                toplamHesaplamaGorevleri.add(new ToplamHesaplamaGorevi(start, end));
                kareHesaplamaGorevleri.add(new KareHesaplamaGorevi(start, end));
            }

            List<Future<Double>> toplamFutures = executorService.invokeAll(toplamHesaplamaGorevleri);
            List<Future<Double>> kareFutures = executorService.invokeAll(kareHesaplamaGorevleri);

            double toplam = 0.0;
            double kareToplam = 0.0;

            for (Future<Double> toplamFuture : toplamFutures) {
                try {
                    toplam += toplamFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Toplam Alma Hatası: " + e.getMessage());
                }
            }

            double ortalama = toplam / elemanSayisi;

            for (Future<Double> kareFuture : kareFutures) {
                try {
                    kareToplam += kareFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Kare Toplam Alma Hatası: " + e.getMessage());
                }
            }

            double standartSapma = Math.sqrt(kareToplam / (elemanSayisi - 1));

            System.out.println("Toplam: " + toplam);
            System.out.println("Ortalama: " + ortalama);
            System.out.println("Kare Toplam: " + kareToplam);
            System.out.println("Standart Sapma: " + standartSapma);

            return standartSapma;

        } catch (InterruptedException e) {
            System.out.println("Hesaplama Hatası: " + e.getMessage());
            return Double.NaN;
        } finally {
            executorService.shutdown();
        }
    }

    private class ToplamHesaplamaGorevi implements Callable<Double> {
        private int start;
        private int end;

        public ToplamHesaplamaGorevi(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Double call() {
            double toplam = 0.0;

            for (int i = start; i < end; i++) {
                toplam += sayilar[i];
            }

            System.out.println(Thread.currentThread().getName() + " - Toplam Hesaplandı: " + toplam);

            return toplam;
        }
    }

    private class KareHesaplamaGorevi implements Callable<Double> {
        private int start;
        private int end;

        public KareHesaplamaGorevi(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Double call() {
            double kareToplam = 0.0;
            double ortalama = 0.0;

            for (int i = start; i < end; i++) {
                ortalama += sayilar[i];
            }

            ortalama /= (end - start);

            for (int i = start; i < end; i++) {
                double sapma = sayilar[i] - ortalama;
                kareToplam += sapma * sapma;
            }

            System.out.println(Thread.currentThread().getName() + " - Kare Toplam Hesaplandı: " + kareToplam);

            return kareToplam;
        }
    }
}
