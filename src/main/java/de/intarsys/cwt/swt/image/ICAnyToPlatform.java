package de.intarsys.cwt.swt.image;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.eclipse.swt.graphics.ImageData;

public class ICAnyToPlatform implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		return true;
	}

	@Override
	public ImageData createImageData(BufferedImage srcImage) {
		ColorConvertOp colorConvertOp = new ColorConvertOp(null);
		ColorSpace destColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel srcColorModel = srcImage.getColorModel();
		int srcTransparency = srcColorModel.getTransparency();
		boolean srcHasAlpha = srcTransparency != Transparency.OPAQUE;
		ColorModel destColorModel = new ComponentColorModel(destColorSpace,
				srcHasAlpha, false, srcTransparency, DataBuffer.TYPE_BYTE);
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		int[] bandOffsets;
		if (srcHasAlpha) {
			bandOffsets = new int[4];
			bandOffsets[3] = 3;
		} else {
			bandOffsets = new int[3];
		}
		ComponentOrder.PlatformInstance.fillBandOffsets(bandOffsets);
		WritableRaster destRaster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, srcWidth, srcHeight, srcWidth
						* bandOffsets.length, bandOffsets.length, bandOffsets,
				new Point(0, 0));
		BufferedImage destImage = new BufferedImage(destColorModel, destRaster,
				false, null);
		colorConvertOp.filter(srcImage, destImage);

		byte[] destData = ((DataBufferByte) destRaster.getDataBuffer())
				.getData();
		byte[] data;
		byte[] alphaData = null;
		if (srcHasAlpha) {
			data = new byte[srcWidth * srcHeight * 3];
			alphaData = new byte[srcWidth * srcHeight];
			for (int index = 0; index < alphaData.length; index++) {
				data[index * 3] = destData[index * 4];
				data[index * 3 + 1] = destData[index * 4 + 1];
				data[index * 3 + 2] = destData[index * 4 + 2];
				alphaData[index] = destData[index * 4 + 3];
			}
		} else {
			data = destData;
		}
		ImageData imageData = new ImageData(srcWidth, srcHeight, 24,
				ComponentOrder.PlatformInstance.getPaletteData(), 3, data);
		imageData.alphaData = alphaData;
		return imageData;
	}
}
