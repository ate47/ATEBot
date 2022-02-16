package fr.atesab.bot.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BotUtils {
	/**
	 * a quick tool to create an element and add/put elements in it
	 */
	public static <T> T acceptAndGet(T element, Consumer<T> consumer) {
		consumer.accept(element);
		return element;
	}

	/**
	 * get a <code>T element</code> and consume it with a pair (<i>x,y</i>) where
	 * <i>y</i> is always the same
	 */
	public static <T, K, V> T buildWithDefaultValueAndGet(T element, K[] keys, V defaultValue,
			TriConsumer<T, K, V> consumer) {
		return buildWithDefaultSuppliedValueAndGet(element, keys, k -> defaultValue, consumer);
	}

	/**
	 * get a <code>T element</code> and consume it with a pair (<i>x,y</i>) where
	 * <i>y</i> is supplied by a {@link Supplier}
	 */
	public static <T, K, V> T buildWithDefaultSuppliedValueAndGet(T element, K[] keys, Function<K, V> defaultValue,
			TriConsumer<T, K, V> consumer) {
		return acceptAndGet(element, m -> {
			for (K k : keys)
				consumer.accept(element, k, defaultValue.apply(k));
		});
	}

	public static void parallelRun(Runnable... runs) {
		Arrays.asList(runs).parallelStream().forEach(Runnable::run);
	}

	public static <K, V> HashMap<K, V> createHashMap(K[] keys, Function<K, V> values) {
		return buildWithDefaultSuppliedValueAndGet(new HashMap<>(), keys, values, (m, k, v) -> m.put(k, v));
	}

	public static <K, V> HashMap<K, V> createHashMap(K[] keys, V defaultValue) {
		return buildWithDefaultValueAndGet(new HashMap<>(), keys, defaultValue, (m, k, v) -> m.put(k, v));
	}
}
