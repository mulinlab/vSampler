package org.mulinlab.variantsampler.cmdline.tools;


import org.broadinstitute.barclay.argparser.Argument;
import org.broadinstitute.barclay.argparser.CommandLineProgramProperties;
import org.mulinlab.variantsampler.cmdline.CMDProgram;
import org.mulinlab.variantsampler.cmdline.programGroup.DatabaseProgramGroup;
import org.mulinlab.variantsampler.index.IndexMaf;
import org.mulinlab.varnote.constants.GlobalParameter;
import java.io.File;
import java.io.IOException;

@CommandLineProgramProperties(
        summary = BuildIndex.USAGE_SUMMARY + BuildIndex.USAGE_DETAILS,
        oneLineSummary = BuildIndex.USAGE_SUMMARY,
        programGroup = DatabaseProgramGroup.class)

public final class BuildIndex extends CMDProgram {
    static final String USAGE_SUMMARY = "Build Index";
    static final String USAGE_DETAILS =
            "\n\nUsage example:" +
            "\n" +
            "java -jar " + GlobalParameter.PRO_NAME + ".jar BuildIndex -D EUR.gz \n" ;

    @Argument(fullName = "Database", shortName = "D", doc = "The database file.", optional = false)
    private File database = null;

    @Override
    protected int doWork() {
        try {
            IndexMaf indexMaf = new IndexMaf(database.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
