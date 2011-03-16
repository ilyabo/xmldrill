package ch.unifr.mme;

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;

/**
 * Everybody's favorite example!
 *
 * @author Jonathan Locke
 */
public class HomePage extends BasePage implements Serializable
{
	/**
	 * Constructor
	 */
	public HomePage()
	{
		add(new Label<Void>("message", "Hello and welcome!"));
	        add(new NavomaticBorder("navomaticBorder"));
	}
}