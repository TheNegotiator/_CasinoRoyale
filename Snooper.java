// The following code modified from from PrismTech HelloWorld example

/************************************************************************
 * LOGICAL_NAME:    Snooper.java
 * FUNCTION:        Reads both Dealer and Player topics for CR project.
 * DATE:            October 2016.
 * AUTHROS:         Group 8 - Clayton Burlison, Andrew Collyer, Shaun Sartin, Clint Wetzel
 ************************************************************************/

import DDS.*;
import CR.*;

public class Snooper {

	public static void main(String[] args) {
		DDSEntityManager mgr = new DDSEntityManager();
		DDSEntityManager mgr2 = new DDSEntityManager();
		String partitionName = "Casino Royale";

		// create Domain Participants
		mgr.createParticipant(partitionName);
		mgr2.createParticipant(partitionName);

		// create Types
		bjDealerTypeSupport bjDealerTS = new bjDealerTypeSupport();
		mgr.registerType(bjDealerTS);
		bjPlayerTypeSupport bjPlayerTS = new bjPlayerTypeSupport();
		mgr2.registerType(bjPlayerTS);

		// create Topics
		mgr.createTopic("Dealer");
		mgr2.createTopic("Player");

		// create Subscribers
		mgr.createSubscriber();
		mgr2.createSubscriber();

		// create DataReaders
		mgr.createReader();
		mgr2.createReader();

		// Read Events

		DataReader dreader = mgr.getReader();
		bjDealerDataReader CRReader = bjDealerDataReaderHelper.narrow(dreader);
		DataReader dreader2 = mgr2.getReader();
		bjPlayerDataReader CRReader2 = bjPlayerDataReaderHelper.narrow(dreader2);

		bjDealerSeqHolder bjDealerSeq = new bjDealerSeqHolder();
		SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();
		bjPlayerSeqHolder bjPlayerSeq = new bjPlayerSeqHolder();
		SampleInfoSeqHolder infoSeq2 = new SampleInfoSeqHolder();

		System.out.println ("=== [Snooper] Ready ...");
		boolean terminate = false;
		while (!terminate) {
			CRReader.read(bjDealerSeq, infoSeq, LENGTH_UNLIMITED.value,
					ANY_SAMPLE_STATE.value, 1,
					ANY_INSTANCE_STATE.value);
			CRReader2.read(bjPlayerSeq, infoSeq2, LENGTH_UNLIMITED.value,
					ANY_SAMPLE_STATE.value, 1,
					ANY_INSTANCE_STATE.value);
			for (int i = 0; i < bjDealerSeq.value.length; i++) {
				System.out.println("=== [Dealer] message received :");
				System.out.println("    uuid           : " + bjDealerSeq.value[i].uuid);
			}
			for (int i = 0; i < bjPlayerSeq.value.length; i++) {
				System.out.println("=== [Player] message received :");
				System.out.println("    uuid           : " + bjPlayerSeq.value[i].uuid);
			}
			// slight delay for the dds
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// Do nothing
			}
		}

	}
}
