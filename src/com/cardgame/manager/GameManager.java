package com.cardgame.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GameManager {

	private ThreadPoolExecutor gameExecutor;
	
	public GameManager(){
		gameExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
	}
	
	public ThreadPoolExecutor getExecutor(){
		return gameExecutor;
	}
	
	public void executeGamePlay(GameTable gameTable){
		gameExecutor.execute(gameTable);
	}
	
	public void endGame(){
		gameExecutor.shutdown();
	}
}
