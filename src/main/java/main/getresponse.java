package main;

public class getresponse {

    private static volatile boolean response_S= true;
    private static volatile boolean responseDstat= false;
    private static volatile boolean response_end= false;
    private static volatile boolean payment= false;
private static volatile boolean rating_received= false;
    
    public static boolean isRating_received() {
	return rating_received;
}
public static void setRating_received(boolean rating_received) {
	getresponse.rating_received = rating_received;
}
	public static boolean isPayment() {
		return payment;
	}
	public static void setPayment(boolean payment) {
		getresponse.payment = payment;
	}
	public static boolean isResponse_S() {
		return response_S;
	}
	public static void setResponse_S(boolean response_S) {
		getresponse.response_S = response_S;
	}
	public static boolean isResponseDstat() {
		return responseDstat;
	}
	public static void setResponseDstat(boolean responseDstat) {
		getresponse.responseDstat = responseDstat;
	}
	public static boolean isResponse_end() {
		return response_end;
	}
	public static void setResponse_end(boolean response_end) {
		getresponse.response_end = response_end;
	}
    

    
}
