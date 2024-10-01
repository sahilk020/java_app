package com.pay10.crm.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CaptchaServlet extends HttpServlet {

	private static final long serialVersionUID = 8485259103090912422L;
	private int height = 0;
	private int width = 0;

	public static final String CAPTCHA_KEY = "captcha_key_name";

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		height = Integer.parseInt(getServletConfig().getInitParameter("height"));
		width = Integer.parseInt(getServletConfig().getInitParameter("width"));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		// Expire response
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Max-Age", 0);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		String ch = String.valueOf(Math.random()).substring(2,8);
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(new Color(46, 163, 242));
		g2d.setColor(Color.WHITE);

		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		Font font = new Font("Verdana", Font.CENTER_BASELINE, 26);
		font = font.deriveFont(20);
		FontRenderContext frc = new FontRenderContext(null, true, true);

		// Calculate size of buffered image.
		LineMetrics lm = font.getLineMetrics(ch, frc);
		g2d.setFont(font);
		
		//get font length for center align
		Rectangle2D r2D = font.getStringBounds(ch, frc);
		long fontLength = Math.round(r2D.getWidth());
		
		g2d.drawString(ch, (width/2-fontLength/2), lm.getAscent());
		
		HttpSession session = req.getSession(true);
		session.setAttribute(CAPTCHA_KEY, ch);

		try (OutputStream outputStream = response.getOutputStream()) {
			ImageIO.write(image, "jpeg", outputStream);
		} finally {
			g2d.dispose();
		}
	}

}