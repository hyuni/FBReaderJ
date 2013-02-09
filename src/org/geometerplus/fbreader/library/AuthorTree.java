/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.library;

import java.util.Collections;
import java.util.List;

import org.geometerplus.fbreader.book.*;

public class AuthorTree extends LibraryTree {
	public final Author Author;

	AuthorTree(IBookCollection collection, Author author) {
		super(collection);
		Author = author;
	}

	AuthorTree(AuthorListTree parent, Author author, int position) {
		super(parent, position);
		Author = author;
	}

	@Override
	public String getName() {
		return Author.NULL.equals(Author)
			? Library.resource().getResource("unknownAuthor").getValue() : Author.DisplayName;
	}

	@Override
	protected String getStringId() {
		return "@AuthorTree" + getSortKey();
	}

	@Override
	protected String getSortKey() {
		if (Author.NULL.equals(Author)) {
			return null;
		}
		return new StringBuilder()
			.append(" Author:")
			.append(Author.SortKey)
			.append(":")
			.append(Author.DisplayName)
			.toString();
	}

	@Override
	public boolean containsBook(Book book) {
		if (book == null) {
			return false;
		}
		final List<Author> bookAuthors = book.authors();
		return Author.equals(Author.NULL) ? bookAuthors.isEmpty() : bookAuthors.contains(Author);
	}

	@Override
	public Status getOpeningStatus() {
		return Status.ALWAYS_RELOAD_BEFORE_OPENING;
	}

	@Override
	public void waitForOpening() {
		clear();
		for (Book book : Collection.books(Author)) {
			createBookSubTree(book);
		}
	}

	@Override
	public boolean onBookEvent(BookEvent event, Book book) {
		switch (event) {
			case Added:
				return containsBook(book) && createBookSubTree(book);
			case Removed:
				// TODO: implement
			case Updated:
				// TODO: implement
			default:
				return super.onBookEvent(event, book);
		}
	}

	private boolean createBookSubTree(Book book) {
		final SeriesInfo seriesInfo = book.getSeriesInfo();
		if (seriesInfo != null) {
			return getSeriesSubTree(seriesInfo.Title).createBookInSeriesSubTree(book);
		}

		final BookTree temp = new BookTree(Collection, book);
		int position = Collections.binarySearch(subTrees(), temp);
		if (position >= 0) {
			return false;
		} else {
			new BookTree(this, book, - position - 1);
			return true;
		}
	}
}
