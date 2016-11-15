/*
 *                         OpenSplice DDS
 *
 *   This software and documentation are Copyright 2006 to 2013 PrismTech
 *   Limited and its licensees. All rights reserved. See file:
 *
 *                     $OSPL_HOME/LICENSE 
 *
 *   for full copyright notice and license terms. 
 *
 */

/************************************************************************
 * LOGICAL_NAME:    HelloWorldSubscriber.java
 * FUNCTION:        Publisher's main for the HelloWorld OpenSplice programming example.
 * MODULE:          OpenSplice HelloWorld example for the java programming language.
 * DATE             September 2010.
 ************************************************************************/

import DDS.*;
import CR.*;
import java.util.*;

public class Player{






	public static void become_active_players(bjPlayer player){
		Random rand = new Random();
		player.uuid = rand.nextInt(2000);
		player.dealer_id  = -1;
		player.credits = 100;
		player.wager = 0;
		player.action = bjp_action.joining;
		int bets = 0;
		for(;;)
		{

			if(player.action == bjp_action.joining)
			{
				
				player = sub(player);
				pub(player);
				player.action = bjp_action.wagering;
			}
			else if(player.action == bjp_action.wagering)
			{		
				if(bets % 5 == 0)
					player.wager = 0;
				if(player.credits == 0)
				{
					return;
				}
				bets++;
				player.wager++;
				if(player.wager > 5)
					player.wager = 1;
				while(player.wager > player.credits)
					player.wager--;
				player.credits -= player.wager;
				System.out.println ("=== [Player "+player.uuid+"] Sending Wager ...");
				pub(player);
				player.action = bjp_action.requesting_a_card;
			}

			else if(player.action == bjp_action.requesting_a_card)
			{
		
				/*for(;;)
				{
					player = sub(player);
					if(player.action == bjp_action.requesting_a_card){
						pub(player);
						player.action = bjp_action.none;
						pub(player);


					}
							
					if(player.action == bjp_action.stay){
						pub(player);
						player.action = bjp_action.none;
						pub(player);
						break;
					}
				}*/
				return;
			}

			else break;
			printDealer(player);
		}


	}


	private static void printDealer(bjPlayer player)
	{
		System.out.println ("=== [Player " + player.uuid + "] ===");
		System.out.println ("\t>Dealer ID: " + player.dealer_id);
		System.out.println ("\t>Wager: " + player.wager);

	}
	public static bjPlayer sub(bjPlayer player){
		PlayerSub sub = new PlayerSub(player);
		return sub.get();
	}

	public static void pub(bjPlayer player){
		PlayerPub pub = new PlayerPub(player);
	}


	public static void main(String[] args) {
		bjPlayer player = new bjPlayer();
		become_active_players(player);

	}
}
