package fr.atesab.bot.utils;

import java.util.function.Consumer;

public class BotUtils {
	/**
	 * A quick tool to create an element and add/put elements in it
	 */
	public static <T> T acceptAndGet(T element, Consumer<T> consumer) {
		consumer.accept(element);
		return element;
	}

	public static <T, K, V> T buildWithDefaultValueAndGet(T element, K[] keys, V defaultValue,
			TriConsumer<T, K, V> consumer) {
		return acceptAndGet(element, m -> {
			for (K k : keys)
				consumer.accept(element, k, defaultValue);
		});
	}
}
