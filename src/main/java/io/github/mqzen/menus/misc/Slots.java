package io.github.mqzen.menus.misc;

import lombok.Getter;

@Getter
public final class Slots {
	
	private final io.github.mqzen.menus.misc.Slot[] slots;
	
	private Slots(io.github.mqzen.menus.misc.Slot[] slots) {
		this.slots = slots;
	}
	
	private Slots(int[] slots) {
		this.slots = new io.github.mqzen.menus.misc.Slot[slots.length];
		for (int i = 0; i < slots.length; i++) {
			this.slots[i] = io.github.mqzen.menus.misc.Slot.of(slots[i]);
		}
	}
	
	public static Slots of(int... slots) {
		return new Slots(slots);
	}
	
	public static Slots of(io.github.mqzen.menus.misc.Slot... slots) {
		return new Slots(slots);
	}
	
	public static Slots ofRows(int[] rows) {
		io.github.mqzen.menus.misc.Slot[] slots = new io.github.mqzen.menus.misc.Slot[rows.length * 9];
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			for (int column = 0; column < 9; column++) {
				slots[i + column] = Slot.of(row, column);
			}
		}
		return new Slots(slots);
	}
	
}
