
/**
 * This plugin squares its argument.
 */

public class Square implements PluginFunction {

	int parameter = 0;

	public void setParameter (int param) {
		parameter = param;
	}

	public int getResult() {
		return parameter * parameter;
	}

	public String getPluginName() {
		return "Square";
	}

	// yes, this operation can fail, but we are going to ignore this here
	public boolean hasError() {
		return false;
	}
}

