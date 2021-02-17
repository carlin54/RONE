
/**
 * This plugin adds one to the parameter.
 */

public class PlusOne implements PluginFunction {

	int parameter = 0;

	public void setParameter (int param) {
		parameter = param;
	}

	public int getResult() {
		return parameter + 1;
	}

	public String getPluginName() {
		return "PlusOne";
	}

	// yes, ths operation can fail, but we are going to ignore this here
	public boolean hasError() {
		return false;
	}
}

