package game;

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;

public class FileIO
{
	public static BufferedImage getImage(String location)
	{
		BufferedImage temp = null;
		try
		{
			temp = ImageIO.read(FileIO.class.getResourceAsStream("images\\" + location));
		} catch (IOException e) { System.err.println(e.toString()); }
		return temp;
	}
}