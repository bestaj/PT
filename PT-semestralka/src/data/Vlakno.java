package data;

import java.util.Random;

import gui.controller.MainController;

/**
 * Tøída {@code Vlakno} slouží pro bìh dalšího vlákna aplikace, 
 * které slouží pro generování objednávek s exponenciálne pravdìpodobnostním rozdìlením.
 * 
 * @author Jiøí Bešta, Olesya Dutchuk
 */
public class Vlakno implements Runnable {
	//x = log(1-u)/(-<lambda>)
	private long randomX;
	private MainController mc;
	private Random rng = new Random();

	// Nastaví mainController
	public void setMainController(MainController mainController) {
		this.mc = mainController;
	}
 
    @Override
    public void run() {
    	// Støední hodnota je 300s skuteèného èasu
    	randomX = (long)(Math.log(1 - rng.nextDouble()) / (-1.0/2.5)) * 1000;
    	 while (true) {
	        if (Thread.interrupted()) {
	          break;
	        }
	       
        	try {
        		Thread.sleep(randomX);
        		mc.generujObjednavky(1);
        		randomX = (long)(Math.log(1 - rng.nextDouble()) / (-1.0/2.5)) * 1000;
    	   
        	} catch (InterruptedException ex) {
        		return;
	        	}      
    	 	}
	    }
}
