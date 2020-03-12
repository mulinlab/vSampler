package org.mulinlab.variantsampler.mapreduce;

import org.mulinlab.variantsampler.database.DatabaseBuilder;
import org.mulinlab.varnote.config.run.RunConfig;
import org.mulinlab.varnote.filters.iterator.LineFilterIterator;
import org.mulinlab.varnote.operations.query.AbstractQuery;
import org.mulinlab.varnote.operations.readers.query.AbstractFileReader;
import org.mulinlab.varnote.utils.mapreduce.Mapper;
import org.mulinlab.varnote.utils.node.LocFeature;

import java.io.IOException;


public final class DBBuilderMapper implements Mapper {

	private final DatabaseBuilder databaseBuilder;
	private final String chr;

	public DBBuilderMapper(final DatabaseBuilder databaseBuilder, final String chr) {
		super();
		this.databaseBuilder = databaseBuilder;
		this.chr = chr;
	}

	@Override
	public void doMap() {
		try {
			databaseBuilder.buildDatabase(chr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getResult() {
		return databaseBuilder.getMergeResult();
	}
}
