package de.intarsys.cwt.swt.image;

import org.eclipse.swt.graphics.PaletteData;

public class ComponentOrderRGB extends ComponentOrder {

	public static final ComponentOrder Instance = new ComponentOrderRGB();

	public ComponentOrderRGB() {
		super(new PaletteData(0xFF0000, 0xFF00, 0xFF));
	}

	@Override
	public void fillBandOffsets(int[] bandOffsets) {
		bandOffsets[0] = 0;
		bandOffsets[1] = 1;
		bandOffsets[2] = 2;
	}
}
