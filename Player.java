
import DDS.*;
import CR.*;

public class Player {
	
	public static void sub(){
		PlayerSub sub = new PlayerSub();
	}

	public static void pub(bjPlayer player){
		PlayerPub pub = new PlayerPub(player);
	}
	
	public static void main(String[] args) {
		bjPlayer player = new bjPlayer();
		player.uuid = 1;
		sub();
		sub();
		sub();
		pub(player);
	}

} // end of player class
