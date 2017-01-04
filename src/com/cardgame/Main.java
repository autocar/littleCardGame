package com.cardgame;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.cardgame.manager.GameManager;
import com.cardgame.manager.GameTable;
import com.cardgame.util.GameConstants;


public class Main {

	static Map<Integer,Integer> resultsMap = new ConcurrentHashMap<Integer,Integer>();
	
	public static void incrementResultsMap(Integer key){
		Integer currVal = resultsMap.get(key);
		resultsMap.put(key, ++currVal);
	}
	
	public static Map<Integer,Integer> getResultsMap(){
		return resultsMap;
	}
	
	
	public static void main(String[] args) throws InterruptedException {

		// 6 parameters 
		
		int concurrentGameCount 	= Integer.parseInt(args[0]);
		int totalGameCount			= Integer.parseInt(args[1]);
		
		String[] botClassNames 		= new String[GameConstants.PLAYER_COUNT];
		
		for (int i = 0 ; i < GameConstants.PLAYER_COUNT ; i++){
			botClassNames[i] = args[i+2];
			// initialize result map
			resultsMap.put(i, 0);
		}
		
		int playCount = totalGameCount / concurrentGameCount;
		
		GameManager gameManager = new GameManager();
		
		long startTime = System.currentTimeMillis();
		
		for (int i = 0 ; i < concurrentGameCount ; i++){
			try {
				GameTable gameTable = new GameTable(botClassNames, playCount);
				gameManager.executeGamePlay(gameTable);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | NoSuchMethodException
					| SecurityException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		gameManager.endGame();
		
		try {
			gameManager.getExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			System.out.println("Total Games Played: " + totalGameCount);
			for (Integer i : Main.getResultsMap().keySet()){
				System.out.println(botClassNames[i]+" : " + Main.getResultsMap().get(i) +" wins");
			}
			System.out.println("Simulation Duration: " + (System.currentTimeMillis() - startTime) + " ms.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
