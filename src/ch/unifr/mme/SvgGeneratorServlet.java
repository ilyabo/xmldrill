/**
 * 
 */
package ch.unifr.mme;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author boyandii
 *
 */
public class SvgGeneratorServlet extends HttpServlet {

	private static final long serialVersionUID = -1611847045868578734L;
    private SvgGenerator generator = new SvgGenerator();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("Content-type: image/svg+xml");
		
		PrintWriter out = resp.getWriter();
        out.println(
		        generator.generate()
		);
	}

}
