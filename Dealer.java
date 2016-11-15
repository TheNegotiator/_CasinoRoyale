// The following code modified from from PrismTech HelloWorld example

/************************************************************************
 * LOGICAL_NAME:    Dealer.java
 * FUNCTION:        CR logic for the Dealer. Sends all messages to DealerPublisher.
 * DATE:            October 2016.
 * AUTHROS:         Group 8 - Clayton Burlison, Andrew Collyer, Shaun Sartin, Clint Wetzel
 ************************************************************************/

import DDS.*;
import CR.*;

public class Dealer {

	
	public static void pub(bjDealer dealer){
		DealerPub pub = new DealerPub(dealer);	
	}

	public static void sub(){
		DealerSub sub = new DealerSub();
	}

	public static void main(String[] args) {
		Dealer deal = new Dealer();
		bjDealer dealer = new bjDealer();
		dealer.uuid = 1;
		deal.pub(dealer);
		dealer.uuid = 2;
		deal.pub(dealer);
		dealer.uuid = 3;
		deal.pub(dealer);
		deal.sub();
	}

} // end of dealer class
