package fr.atesab.bot.utils;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
	public void accept(A a, B b, C c);
}
