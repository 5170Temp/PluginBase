package io.github.mqzen.menus.titles;

import io.github.mqzen.menus.titles.MenuTitle;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ModernTitle implements MenuTitle {

	@Getter
	private final Component component;
	
	ModernTitle(Component component) {
		this.component = component;
	}
	
	@Override
	public String asString() {
		return LegacyComponentSerializer.legacySection().serialize(component);
	}
}