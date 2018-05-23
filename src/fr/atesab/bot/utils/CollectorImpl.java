package fr.atesab.bot.utils;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CollectorImpl<T, R> implements Collector<T, R, R> {
	private Supplier<R> supplier;
	private BiConsumer<R, T> accumulator;
	private BinaryOperator<R> combiner;
	private Function<R, R> finisher;
	public CollectorImpl(Supplier<R> supplier, BiConsumer<R, T> accumulator, BinaryOperator<R> combiner) {
		this(supplier, accumulator, combiner, Function.identity());
	}
	public CollectorImpl(Supplier<R> supplier, BiConsumer<R, T> accumulator, BinaryOperator<R> combiner, Function<R, R> finisher) {
		this.supplier = supplier;
		this.accumulator = accumulator;
		this.combiner = combiner;
		this.finisher = finisher;
	}

	@Override
	public Supplier<R> supplier() {
		return supplier;
	}

	@Override
	public BiConsumer<R, T> accumulator() {
		return accumulator;
	}

	@Override
	public BinaryOperator<R> combiner() {
		return combiner;
	}

	@Override
	public Function<R, R> finisher() {
		return finisher;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}

}
