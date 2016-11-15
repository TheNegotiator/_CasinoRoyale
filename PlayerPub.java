import DDS.*;
import CR.*;

public class PlayerPub{
	public PlayerPub(bjPlayer player){
		DDSEntityManager mgr = new DDSEntityManager();
		String partitionName = "Casino Royale";

		// create Domain Participant
		mgr.createParticipant(partitionName);

		// create Type
		bjPlayerTypeSupport bj = new bjPlayerTypeSupport();
		mgr.registerType(bj);

		// create Topic
		mgr.createTopic("Player");

		// create Publisher
		mgr.createPublisher();

		// create DataWriter
		mgr.createWriter();

		// Publish Events

		DataWriter dwriter = mgr.getWriter();
		bjPlayerDataWriter bjPlayerWriter = bjPlayerDataWriterHelper.narrow(dwriter);
		//dealer.bjd_action = shuffling;
		System.out.println("=== [Publisher] writing a message containing :");
		System.out.println("    Player ID : " + player.uuid);	
		bjPlayerWriter.register_instance(player);
		int status = bjPlayerWriter.write(player, HANDLE_NIL.value);
		ErrorHandler.checkStatus(status, "bjPlayerDataWriter.write");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// clean up
		mgr.getPublisher().delete_datawriter(bjPlayerWriter);
		mgr.deletePublisher();
		mgr.deleteTopic();
		mgr.deleteParticipant();

	}
}