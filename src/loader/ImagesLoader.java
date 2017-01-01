package loader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class ImagesLoader {

	private static String IMAGES_CONFIG = "./assets/images/imsInfo.txt";
	private static String IMAGES_DIR = "./assets/images/";
	private static HashMap<String, ArrayList<BufferedImage>> imagesMap = new HashMap<String, ArrayList<BufferedImage>>();

	public static int ICON_SIZE;

	public static void init() {
		BufferedReader br;
		try {
			// Read image config file
			br = new BufferedReader(new InputStreamReader(ImagesLoader.class.getClassLoader()
					.getResourceAsStream(IMAGES_CONFIG)));

			String line;
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				if (line.equals("") || line.startsWith("//")) {
					continue;
				}

				//// Parse each row from the config file

				// Get image asset file name
				StringTokenizer stk = new StringTokenizer(line, "=");
				stk.nextToken();
				String imageAssetFilename = stk.nextToken();

				// Get number of rows in this image asset file
				line = br.readLine();
				stk = new StringTokenizer(line, "=");
				stk.nextToken();
				int numRows = Integer.parseInt(stk.nextToken());

				// Get number of columns in his image asset file
				line = br.readLine();
				stk = new StringTokenizer(line, "=");
				stk.nextToken();
				int numCols = Integer.parseInt(stk.nextToken());

				// Load the image asset file
				System.out.println(IMAGES_DIR + imageAssetFilename);
				BufferedImage sourceImageBuf = ImageIO
						.read(ImagesLoader.class.getClassLoader().getResourceAsStream(IMAGES_DIR + imageAssetFilename));

				int imWidth = sourceImageBuf.getWidth() / numCols;
				int imHeight = sourceImageBuf.getHeight() / numRows;
				int transparency = sourceImageBuf.getTransparency();

				for (int i = 0; i < numRows; i++) {
					for (int j = 0; j < numCols; j++) {
						// Skip blank lines
						while (true) {
							line = br.readLine();
							if (!(line.startsWith("//") || line.equals("")))
								break;
						}

						// Get image prefix (i.e. name) without no.
						stk = new StringTokenizer(line, "=");
						stk.nextToken();
						String prefix = stk.nextToken();
						if (prefix.equals("null")) {
							continue;
						}

						// Crop the image from the source image buffer and draw it to the new buffer
						BufferedImage imageBuf = new BufferedImage(imWidth, imHeight, transparency);
						Graphics g = imageBuf.getGraphics();
						g.drawImage(sourceImageBuf, 0, 0, imWidth, imHeight, j * imWidth, i * imHeight,
								(j + 1) * imWidth,
								(i + 1) * imHeight, null);
						g.dispose();

						// Store into in-memory hash map
						imagesMap.putIfAbsent(prefix, new ArrayList<>());
						imagesMap.get(prefix).add(imageBuf);
					}
				}
			}
			// All icons have the same size
			ICON_SIZE = imagesMap.get("road").get(0).getWidth();

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage getImage(String prefix) {

		if (!imagesMap.containsKey(prefix)) {
			System.out.println("#0:no element");
			return null;
		}
		return imagesMap.get(prefix).get(0);
	}

	public static BufferedImage getImage(String prefix, int index) {
		if (!imagesMap.containsKey(prefix)) {
			System.out.println("#1:no element");
			return null;
		}
		return imagesMap.get(prefix).get(index);
	}

	public static int getNumOfImages(String prefix) {
		if (!imagesMap.containsKey(prefix)) {
			System.out.println("num: no element");
			return -1;
		}
		return imagesMap.get(prefix).size();
	}
}
