package gui.converter;

import gui.ConversionSettings;

public interface ConversionStep {
	/**
	 * Executes this step on the data.
	 * @throws ConversionException
	 */
	void execute(ConversionSettings data) throws ConversionException;
}
