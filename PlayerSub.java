import DDS.*;
import CR.*;
import java.util.*;
public class PlayerSub{

bjPlayer player;

void printHand(card[] hand)
{
	for(int i=0; i<21; i++)
	{
		//if(!(hand[i].suite 
	}
}


public bjPlayer get()
{
	return player;
}

public PlayerSub(bjPlayer p){
		DDSEntityManager mgr = new DDSEntityManager();
		String partitionName = "Casino Royale";
		player = p;
		// create Domain Participant
		mgr.createParticipant(partitionName);

		// create Type
		bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
		mgr.registerType(msgTS);

		// create Topic
		mgr.createTopic("Dealer");

		// create Subscriber
		mgr.createSubscriber();

		// create DataReader
		mgr.createReader();

		// Read Events
		DataReader dreader = mgr.getReader();
		bjDealerDataReader bjDealerReader = bjDealerDataReaderHelper.narrow(dreader);

		bjDealerSeqHolder msgSeq = new bjDealerSeqHolder();
		SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println ("=== [Player "+ player.uuid+ "] Ready ...");
		boolean terminate = false;
		int count = 0;
		card cards = new card();
		while (!terminate) { // We dont want the example to run indefinitely
			bjDealerReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
					ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
					ANY_INSTANCE_STATE.value);
			for (int i = 0; i < msgSeq.value.length; i++) {
				if(msgSeq.value[i].action == bjd_action.shuffling && player.dealer_id  == -1){	
					player.dealer_id = msgSeq.value[i].uuid;
					terminate = true;
					System.out.println ("=== [Player "+player.uuid+"] Game Joined ...");
				}
				else if(msgSeq.value[i].action == bjd_action.dealing && player.action == bjp_action.requesting_a_card && msgSeq.value[i].uuid == player.dealer_id && msgSeq.value[i].target_uuid == player.uuid){	
					terminate = true;
					System.out.println ("=== [Player "+player.uuid+"] Getting my Cards ...");
					/*print_hand(msgSeq.value[i].cards);
					int me = 0, score;
					for(int j = 0; j < msgSeq.value[i].active_players; j++)
						if(msgSeq.value[i].players[j].uuid ==  player.uuid)
							me = j;
					print_hand(msgSeq.value[i].players[me].cards);
					score = score_hand(msgSeq.value[i].players[me].cards);
					System.out.println ("=== [Player "+player.uuid+"] current score " + score + " ...");
					if(score > 21)
						 player.action = bjp_action.none;*/
				}
					
			}
			try
			{
				Thread.sleep(200);
			}
			catch(InterruptedException ie)
			{
				// nothing to do
			}
		}			
        bjDealerReader.return_loan(msgSeq, infoSeq);
		
		// clean up
		mgr.getSubscriber().delete_datareader(bjDealerReader);
		mgr.deleteSubscriber();
		mgr.deleteTopic();
		mgr.deleteParticipant();
	}
}
