import DDS.*;
import CR.*;


public class DealerPub{
		public DealerPub(bjDealer dealer){
		DDSEntityManager mgr = new DDSEntityManager();
		String partitionName = "Casino Royale";

		// create Domain Participant
		mgr.createParticipant(partitionName);

		// create Type
		bjDealerTypeSupport bj = new bjDealerTypeSupport();
		mgr.registerType(bj);

		// create Topic
		mgr.createTopic("Dealer");

		// create Publisherss
		mgr.createPublisher();

		// create DataWriter
		mgr.createWriter();

		// Publish Events

		DataWriter dwriter = mgr.getWriter();
		bjDealerDataWriter bjDealerWriter = bjDealerDataWriterHelper.narrow(dwriter);
		//dealer.bjd_action = shuffling;
		//System.out.println("=== [Dealer "+dealer.uuid+"] writing a message");
		bjDealerWriter.register_instance(dealer);
		int status = bjDealerWriter.write(dealer, HANDLE_NIL.value);
		ErrorHandler.checkStatus(status, "bjDealerDataWriter.write");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// clean up
		mgr.getPublisher().delete_datawriter(bjDealerWriter);
		mgr.deletePublisher();
		mgr.deleteTopic();
		mgr.deleteParticipant();

		}
	}
