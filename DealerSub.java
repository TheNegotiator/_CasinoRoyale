import DDS.*;
import CR.*;

public class DealerSub{


	bjDealer dealer;

	public bjDealer get()
	{
		return dealer;
	}

	int decsion = 0;
	public int get_decsion()
	{
		return decsion;
	}

	int wager = 0;
	public int get_wager()
	{
		return wager;
	}
	
	public DealerSub(bjDealer d){
		DDSEntityManager mgr = new DDSEntityManager();
		String partitionName = "Casino Royale";
		dealer = d;

		// create Domain Participant
		mgr.createParticipant(partitionName);

		// create Type
		bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
		mgr.registerType(msgTS);

		// create Topic
		mgr.createTopic("Player");

		// create Subscriber
		mgr.createSubscriber();

		// create DataReader
		mgr.createReader();

		// Read Events
		DataReader dreader = mgr.getReader();
		bjPlayerDataReader bjPlayerReader = bjPlayerDataReaderHelper.narrow(dreader);

		bjPlayerSeqHolder msgSeq = new bjPlayerSeqHolder();
		SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

		boolean terminate = false;
		int target = 0;

		System.out.println ("=== [Dealer "+ dealer.uuid + "] Ready ...");
		while (!terminate) { // We dont want the example to run indefinitely
			bjPlayerReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value, ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value, ANY_INSTANCE_STATE.value);
			for (int i = 0; i < msgSeq.value.length; i++) {
					if(dealer.action == bjd_action.shuffling &&  dealer.active_players < 6 &&
			 			msgSeq.value[i].dealer_id == dealer.uuid && 
				   		msgSeq.value[i].action == bjp_action.joining){//If we've found a player who can join  	
				   		dealer.players[dealer.active_players].uuid =  msgSeq.value[i].uuid;
				   		dealer.target_uuid = i;
				   		dealer.active_players++;
						System.out.println ("=== [Dealer "+dealer.uuid+"] Player "+ msgSeq.value[i].uuid +" Found ...");
						terminate = true;
					}

					else if(dealer.action == bjd_action.waiting && msgSeq.value[i].uuid == dealer.target_uuid && msgSeq.value[i].action == bjp_action.wagering){//If we've found a wager			   
						dealer.players[target].wager = msgSeq.value[i].wager;
						System.out.println ("=== [Dealer "+ dealer.uuid + "] Player " + msgSeq.value[i].uuid + " Wager Recieved ...");		
						if(++target < dealer.active_players)
							dealer.target_uuid = dealer.players[target].uuid;	
						terminate = true;
					}
					
					else if(dealer.action == bjd_action.dealing && msgSeq.value[i].action == bjp_action.requesting_a_card && msgSeq.value[i].uuid == dealer.target_uuid ){
						//decsion = 1;
						System.out.println("=== [Dealer "+ dealer.uuid + "] Player " + msgSeq.value[i].uuid + " Requesting Card ...");
						if(++target < dealer.active_players)
							dealer.target_uuid = dealer.players[target].uuid;	
						terminate = true;
					}

					else if(msgSeq.value[i].action == bjp_action.stay){
						//decsion = -1;
						System.out.println("Here2"+ decsion);
						break;
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
        bjPlayerReader.return_loan(msgSeq, infoSeq);
	
		// clean up
		mgr.getSubscriber().delete_datareader(bjPlayerReader);
		mgr.deleteSubscriber();
		mgr.deleteTopic();
		mgr.deleteParticipant();
	}
}
