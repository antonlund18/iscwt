package de.intarsys.cwt.swt.image;

import org.eclipse.swt.graphics.PaletteData;

public class ComponentOrderBGR extends ComponentOrder {

	public ComponentOrderBGR() {
		super(new PaletteData(0xFF, 0xFF00, 0xFF0000));
	}

	@Override
	public void fillBandOffsets(int[] bandOffsets) {
		bandOffsets[0] = 2;
		bandOffsets[1] = 1;
		bandOffsets[2] = 0;
	}
}
