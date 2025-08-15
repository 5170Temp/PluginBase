package io.github.mqzen.menus.base.pagination;

import io.github.mqzen.menus.base.pagination.Page;
import io.github.mqzen.menus.base.pagination.PageView;
import io.github.mqzen.menus.base.pagination.Pagination;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class PageViewFactory {
	
	private PageViewFactory() {
		throw new UnsupportedOperationException();
	}
	
	static io.github.mqzen.menus.base.pagination.PageView createAuto(io.github.mqzen.menus.base.pagination.Pagination pagination, int index) {
		return new io.github.mqzen.menus.base.pagination.PageView(pagination, index);
	}
	
	static io.github.mqzen.menus.base.pagination.PageView createPlain(io.github.mqzen.menus.base.pagination.Pagination pagination, io.github.mqzen.menus.base.pagination.Page model, int index) {
		return new io.github.mqzen.menus.base.pagination.PageView(pagination, model, index);
	}
	
	static PageView createView(Pagination pagination, @NotNull Page model, int index) {
		return pagination.isAutomatic() ? createAuto(pagination, index) : createPlain(pagination, model, index);
	}
	
}
