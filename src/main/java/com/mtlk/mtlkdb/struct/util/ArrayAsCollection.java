package com.mtlk.mtlkdb.struct.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ArrayAsCollection<E> implements Collection<E> {
    private final E[] array;

    private ArrayAsCollection(E[] array) {
        this.array = Objects.requireNonNull(array, "array");
    }

    public static <T> ArrayAsCollection<T> of(T[] array) {
        return new ArrayAsCollection<>(array);
    }

    @Override
    public Object[] toArray() {
        return array;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (E element : array) {
            if (Objects.equals(element, o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < array.length;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return array[index++];
            }
        };
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("ArrayAsCollection is read-only");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("ArrayAsCollection is read-only");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("ArrayAsCollection is read-only");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("ArrayAsCollection is read-only");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("ArrayAsCollection is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("ArrayAsCollection is read-only");
    }
}
