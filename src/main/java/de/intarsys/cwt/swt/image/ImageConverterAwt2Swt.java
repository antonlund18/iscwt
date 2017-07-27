/*
 * Copyright (c) 2008, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.cwt.swt.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.ImageData;

public class ImageConverterAwt2Swt {

	private static final Logger Log = PACKAGE.Log;
	private static List<IImageConverter> ImageConverters;

	static {
		ImageConverters = new ArrayList<IImageConverter>();
		ImageConverters.add(new ICComponentRGBByteOpaqueToSame());
		ImageConverters.add(new ICComponentRGBByteOpaqueToPlatform());
		ImageConverters.add(new ICComponentRGBByteTransparentToPlatform());
		ImageConverters.add(new ICComponentGrayByteOpaqueToSame());
		ImageConverters.add(new ICComponentGrayByteTransparentToSame());
		ImageConverters.add(new ICIndexedRGBByteOpaqueToSame());
		ImageConverters.add(new ICComponentByteOpaqueToPlatform());
		ImageConverters.add(new ICComponentByteTransparentToPlatform());
		ImageConverters.add(new ICComponentOpaqueToPlatform());
		ImageConverters.add(new ICAnyByteToPlatform());
		ImageConverters.add(new ICAnyToPlatform());
	}

	private BufferedImage bufferedImage;
	private ImageData imageData;

	public ImageConverterAwt2Swt(BufferedImage paramBufferedImage) {
		bufferedImage = paramBufferedImage;
	}

	protected ImageData createImageData() {
		for (IImageConverter imageConverter : ImageConverters) {
			if (imageConverter.accept(bufferedImage)) {
				try {
					imageData = imageConverter.createImageData(bufferedImage);
					return imageData;
				} catch (Throwable t) {
					// shouldn't happen but if it does: try next converter
					if (t instanceof OutOfMemoryError) {
						// we cannot recover from that however
						throw t;
					}
					Log.log(Level.SEVERE, t.getMessage(), t);
				}
			}
		}
		throw new UnsupportedOperationException();
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public ImageData getImageData() {
		if (imageData == null) {
			imageData = createImageData();
		}
		return imageData;
	}
}
