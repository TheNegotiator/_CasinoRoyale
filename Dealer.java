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
 * LOGICAL_NAME:    Dealer.java
 * FUNCTION:        
 * MODULE:          
 * DATE             November 2016
 ************************************************************************/
//Changed

import DDS.*;
import CR.*;
import java.util.*;
public class Dealer{


	public ArrayList<ArrayList<Character>> 
				card_management(ArrayList<ArrayList<Character>> 
								shoe , int size_of_deck){

		//The values required to abstract to the IDL
		char[] suites = 	   {'C','D','H','S'};
		
		char[] base_values =   {'2','3','4','5',
								'6','7','8','9','T',
                                'J','Q','K','A'};

        //This will act like a temp list to append to the deck
        ArrayList<Character> card = new ArrayList<Character>();

        //Base case for recursion
        if(size_of_deck == 0){
        	return shoe;
        }

  		//Nested loop for constructing the actual card
        for(char s : suites){
        	for(char b : base_values){
        		card.add(s);
        		card.add(b);
        		//This is appending the temp list to the deck
        		shoe.add(new ArrayList<Character>(card));
        		card.clear();
        	} 
        }
        //Applying recursion to build the entire shoe
        return card_management(shoe, size_of_deck - 1);
	}



	public ArrayList<ArrayList<Character>> 
			shuffle_deck(ArrayList<ArrayList<Character>> shoe){
			long seed = System.nanoTime();
			Collections.shuffle(shoe, new Random(seed));
			return shoe;
		}



	public static bjDealer get_cards(bjDealer dealer,
							  ArrayList<ArrayList<Character>> shoe, 
							  int number_of_cards,
							  int index,
							  int player,
							  boolean true_for_dealer){

		if ((number_of_cards == 0) && (true_for_dealer == true))
				return dealer;

		if((number_of_cards == 0) && (true_for_dealer== false))
				return dealer;


		if(true_for_dealer == true){
			card cards = new card();
			dealer.cards[0].visible = true;
			dealer.cards[index].suite = shoe.get(0).get(0);
			dealer.cards[index].base_value = shoe.get(0).get(1);
			shoe.remove(0);	
		}

		if(true_for_dealer == false){
			card cards = new card();
			dealer.players[player].cards[0].visible = false;
			dealer.players[player].cards[index].suite = shoe.get(0).get(0);
			dealer.players[player].cards[index].base_value = shoe.get(0).get(1);
			shoe.remove(0);
		}
			
		return get_cards(dealer, shoe, number_of_cards -1, 
						index + 1,
						player,
						true_for_dealer);
	}


	private static void printDealer(bjDealer dealer)
	{
		System.out.println ("=== [Dealer " + dealer.uuid + "] ===");
		System.out.println ("\t>active players: " + dealer.active_players);
		System.out.println ("\t>target uuid: " + dealer.target_uuid);
		for(int i = 0; i < dealer.active_players; i++){
			System.out.println ("\t>player "+ (i + 1) + ": " + dealer.players[i].uuid);
			System.out.println ("\t->Wager: " + dealer.players[i].wager);
		}

	}
	

	
	public bjDealer active_players(ArrayList<ArrayList<Character>> shoe, bjDealer dealer){
		dealer.uuid = 1;
		dealer.seqno = 0;
		dealer.active_players = 0;
		dealer.action = bjd_action.shuffling;

		for(;;)
		{
			if(dealer.action == bjd_action.shuffling)
			{
				pub(dealer);
				dealer = sub(dealer);
				shoe = shuffle_deck(shoe);
				dealer.target_uuid = dealer.players[0].uuid;
				dealer.action = bjd_action.waiting;
			}

			else if(dealer.action == bjd_action.waiting)
			{
				System.out.println ("=== [Dealer "+ dealer.uuid + "] Looking for Wagers ...");
				dealer = sub(dealer);
				dealer.target_uuid = dealer.players[0].uuid;
				dealer.action = bjd_action.dealing;
			}


			else if(dealer.action == bjd_action.dealing)
			{
				System.out.println ("=== [Dealer "+ dealer.uuid + "] Dealing First Round ...");
				for(int i = 0; i < dealer.active_players; i++)
					dealer = get_cards(dealer, shoe, 1, 0, i, false);
				dealer = get_cards(dealer, shoe, 1, 0, -1, true);

				for(int i = 0; i < dealer.active_players; i++)
					dealer = get_cards(dealer, shoe, 1, 1, i, false);
				dealer = get_cards(dealer, shoe, 1, 1, -1, true);
				
				for(int i = 0; i < dealer.active_players; i++)
				{
		
					dealer.target_uuid = dealer.players[i].uuid;
					pub(dealer);	
				}


				/*for(int x = 0; x < dealer.active_players; x++){
					dealer.target_uuid = dealer.players[x].uuid;
					int index = 2;
					for(;;){
						pub(dealer);
						DealerSub dsub = new DealerSub(dealer);
						int decsion = 0;
						decsion = dsub.get_decsion();
						if(decsion == -1)
							break;

						if(decsion == 1){
							dealer = get_cards(dealer, shoe,1, index, x, false);
							index++;
							System.out.println("Index" + index);
							continue;
						}

					}
				}*/
				break;
			}
			else break;
			printDealer(dealer);
		}
		return dealer;
	}


	public static void pub(bjDealer dealer){
		DealerPub pub = new DealerPub(dealer);	
	}


	public static bjDealer sub(bjDealer dealer){
		DealerSub sub = new DealerSub(dealer);
		return sub.get();
	}

	public static void main(String[] args) {
		ArrayList<ArrayList<Character>> shoe 
						= new ArrayList<ArrayList<Character>>();
		Dealer deal 	= new Dealer();
		bjDealer dealer = new bjDealer();
		card cards 		= new card();
		shoe 			= deal.card_management(shoe, 6);
		shoe 			= deal.shuffle_deck(shoe);
        dealer 			= deal.get_cards(dealer, shoe, 2, 0, 0, true);
		dealer 			= deal.active_players(shoe, dealer);
		
		}
}
