package data;

import java.util.Random;

import gui.controller.MainController;

/**
 * T��da {@code Vlakno} slou�� pro b�h dal��ho vl�kna aplikace, 
 * kter� slou�� pro generov�n� objedn�vek s exponenci�lne pravd�podobnostn�m rozd�len�m.
 * 
 * @author Ji�� Be�ta, Olesya Dutchuk
 */
public class Vlakno implements Runnable {
	//x = log(1-u)/(-<lambda>)
	private long randomX;
	private MainController mc;
	private Random rng = new Random();

	// Nastav� mainController
	public void setMainController(MainController mainController) {
		this.mc = mainController;
	}
 
    @Override
    public void run() {
    	// St�edn� hodnota je 300s skute�n�ho �asu
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
