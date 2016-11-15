import DDS.*;
import CR.*;

public class PlayerSub{
public PlayerSub(){
		DDSEntityManager mgr = new DDSEntityManager();
		String partitionName = "Casino Royale";

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

                System.out.println ("=== [Subscriber] Ready ...");
		boolean terminate = false;
		int count = 0;
		while (!terminate && count < 1500) { // We dont want the example to run indefinitely
			bjDealerReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
					ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
					ANY_INSTANCE_STATE.value);
			for (int i = 0; i < msgSeq.value.length; i++) {
						if(i == (msgSeq.value.length - 1)){
							System.out.println("=== [Subscriber] message received :");
							System.out.println("    Dealer ID  : "
								+ msgSeq.value[i].uuid);
							terminate = true;
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
			++count;
			
		}
                bjDealerReader.return_loan(msgSeq, infoSeq);
		
		// clean up
		mgr.getSubscriber().delete_datareader(bjDealerReader);
		mgr.deleteSubscriber();
		mgr.deleteTopic();
		mgr.deleteParticipant();
	}
}