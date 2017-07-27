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

public class ICAnyByteToPlatform implements IImageConverter {

	@Override
	public boolean accept(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		if (colorModel.getTransparency() == Transparency.BITMASK) {
			return false;
		}
		DataBuffer dataBuffer = image.getRaster().getDataBuffer();
		return dataBuffer instanceof DataBufferByte;
	}

	@Override
	public ImageData createImageData(BufferedImage srcImage) {
		ColorConvertOp colorConvertOp = new ColorConvertOp(null);
		ColorSpace destColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel srcColorModel = srcImage.getColorModel();
		ColorModel destColorModel = new ComponentColorModel(destColorSpace,
				false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		int[] bandOffsets = ComponentOrder.PlatformInstance.getBandOffsets();
		WritableRaster destRaster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, srcWidth, srcHeight, srcWidth * 3, 3,
				bandOffsets, new Point(0, 0));
		BufferedImage destImage = new BufferedImage(destColorModel, destRaster,
				false, null);
		colorConvertOp.filter(srcImage, destImage);

		int srcTransparency = srcColorModel.getTransparency();
		byte[] srcData = ((DataBufferByte) srcImage.getRaster().getDataBuffer())
				.getData();
		byte[] destData = ((DataBufferByte) destRaster.getDataBuffer())
				.getData();
		byte[] data;
		byte[] alphaData = null;
		if (srcTransparency == Transparency.TRANSLUCENT) {
			int srcNumComponents = srcColorModel.getNumComponents();
			alphaData = new byte[srcWidth * srcHeight];
			for (int index = 0; index < alphaData.length; index++) {
				alphaData[index] = srcData[index * srcNumComponents
						+ srcNumComponents - 1];
			}
		}
		data = destData;
		ImageData imageData = new ImageData(srcWidth, srcHeight, 24,
				ComponentOrder.PlatformInstance.getPaletteData(), 3, data);
		imageData.alphaData = alphaData;
		return imageData;
	}
}
