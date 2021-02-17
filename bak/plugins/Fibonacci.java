
/**
 * This plugin calculates the n-th Fibonacci number.
 */

public class Fibonacci implements PluginFunction {

	int parameter = 0;
	boolean hasError = false;

	// passing a negative parameter will cause an error
	public boolean hasError() {
		return hasError;
	}

	public void setParameter (int param) {
		parameter = param;
	}

	public int getResult() {
		hasError = false;
		return fib(parameter);
	}

	// you can define additional functions as necessary
	protected int fib (int n) {
		if (n < 0) {
			hasError = true;
			return 0;
		}

		if (n == 0)
			return 0;
		else if (n == 1)
			return 1;
		else
			return fib(n-1) + fib(n-2); 
	}

	public String getPluginName() {
		return "Fibonacci";
	}
	
	public Fibonacci() {
		
	}
	
}

