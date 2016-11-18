/*
 *    Copyright 2016 Jonathan Beaudoin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xena;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Indexer<E> implements Iterable<E> {
	
	private final int minIndex;
	private final Object[] arr;
	private final IndexerIterator iterator = new IndexerIterator();
	private int size = 0;
	private int highest;
	
	public Indexer(int minIndex, int capacity) {
		this.minIndex = highest = minIndex;
		arr = new Object[capacity];
	}
	
	public Indexer(int capacity) {
		this(0, capacity);
	}
	
	@SuppressWarnings("unchecked")
	public E get(int index) {
		return (E) arr[index];
	}
	
	public E get(E element) {
		int index = indexOf(element);
		return index == -1 ? null : (E) arr[index];
	}
	
	@SuppressWarnings("unchecked")
	public E set(int index, E element) {
		Object previous = arr[index];
		arr[index] = element;
		if (previous == null && element != null) {
			size++;
			if (highest < index) {
				highest = index;
			}
		} else if (previous != null && element == null) {
			size--;
			if (highest == index) {
				highest--;
			}
		}
		return (E) previous;
	}
	
	public int add(E element) {
		int index = nextIndex();
		set(index, element);
		return index;
	}
	
	public void remove(E element) {
		for (int i = minIndex; i <= highest; i++) {
			if (element.equals(arr[i])) {
				set(i, null);
				return;
			}
		}
	}
	
	public boolean contains(E element) {
		if (element == null) {
			return false;
		}
		
		for (E e : this) {
			if (element.equals(e)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void clear() {
		for (int i = minIndex; i < arr.length; i++)
			arr[i] = null;
		size = 0;
	}
	
	public int size() {
		return size;
	}
	
	public int nextIndex() {
		for (int i = minIndex; i < arr.length; i++) {
			if (null == arr[i]) {
				return i;
			}
		}
		throw new IllegalStateException("Out of indices!");
	}
	
	public int indexOf(E element) {
		for (int i = minIndex; i < arr.length; i++) {
			if (element == arr[i]) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		for (E e : this) {
			if (e != null)
				action.accept(e);
		}
	}
	
	@Override
	public Iterator<E> iterator() {
		iterator.pointer = minIndex;
		return iterator;
	}
	
	public Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
	
	private final class IndexerIterator implements Iterator<E> {
		
		private int pointer;
		
		@Override
		public boolean hasNext() {
			return size > 0 && pointer <= highest;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public E next() {
			Object o = arr[pointer++];
			if (o == null && hasNext()) {
				return next();
			}
			return (E) o;
		}
		
		@Override
		public void remove() {
			set(pointer, null);
		}
		
	}
	
}
