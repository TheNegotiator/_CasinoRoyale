import DDS.*;
import CR.*;

public class DealerSub{
	public DealerSub(){
		DDSEntityManager mgr = new DDSEntityManager();
		String partitionName = "Casino Royale";

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

                System.out.println ("=== [Subscriber] Ready ...");
		boolean terminate = false;
		int count = 0;
		while (!terminate && count < 1500) { // We dont want the example to run indefinitely
			bjPlayerReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
					ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
					ANY_INSTANCE_STATE.value);
			for (int i = 0; i < msgSeq.value.length; i++) {
					if(i == (msgSeq.value.length - 1)){
						System.out.println("=== [Subscriber] message received :");
						System.out.println("    Player ID  : "
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
                bjPlayerReader.return_loan(msgSeq, infoSeq);
		
		// clean up
		mgr.getSubscriber().delete_datareader(bjPlayerReader);
		mgr.deleteSubscriber();
		mgr.deleteTopic();
		mgr.deleteParticipant();
	}
}