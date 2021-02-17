
/**
 * This plugin always returns 42, regardless of the parameter.
 */

public class FourtyTwo implements PluginFunction {

	public void setParameter (int param) {
		// this function doesn't care about its parameter,
		// so it doesn't even store it for later use
	}

	public boolean hasError() {
		// 42 is never wrong, so this function always returns false
		return false;
	}

	public int getResult() {
		return 42;
	}

	public String getPluginName() {
		return "FourtyTwo";
	}
}

