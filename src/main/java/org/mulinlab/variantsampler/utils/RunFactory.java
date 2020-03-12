package org.mulinlab.variantsampler.utils;

import htsjdk.samtools.util.BlockCompressedInputStream;
import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.tribble.util.LittleEndianOutputStream;
import org.mulinlab.variantsampler.database.DatabaseBuilder;
import org.mulinlab.variantsampler.mapreduce.DBBuilderMapper;
import org.mulinlab.variantsampler.mapreduce.SimpleMapReduce;
import org.mulinlab.varnote.utils.mapreduce.MapReduce;
import org.mulinlab.varnote.utils.mapreduce.Mapper;
import org.mulinlab.varnote.utils.mapreduce.Reducer;
import java.io.File;
import java.io.IOException;
import java.util.List;

public final class RunFactory {

    public static void buildDatabase(final String path, final DatabaseBuilder.Population popus, final int thread) {

        System.out.println("popus=" + popus);
        MapReduce<File, String> mr = new SimpleMapReduce<File, String>(thread,  new Reducer<File, Mapper<String>, String>() {
            public File doReducer(List<Mapper<String>> mappers) {
                try {
                    LittleEndianOutputStream out = new LittleEndianOutputStream(new BlockCompressedOutputStream(new File(mappers.get(0).getResult() + ".gz")));

                    byte[] buf = new byte[1024 * 128];
                    for (int i = GP.FROM_CHROM; i <= GP.TO_CHROM; i++) {

                        BlockCompressedInputStream in = new BlockCompressedInputStream(new File(mappers.get(0).getResult() + i + ".gz"));
                        int n = 0;
                        while((n = in.read(buf)) != -1) {
                            out.write(buf, 0, n);
                        }
                        in.close();
                    }

                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        try {
            for(int i = GP.FROM_CHROM; i<= GP.TO_CHROM; i++) {
                DBBuilderMapper mapper = null;
                mapper = new DBBuilderMapper(new DatabaseBuilder(path, popus), String.valueOf(i));
                mr.addMapper(mapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mr.getResult();
    }
}
