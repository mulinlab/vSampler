package org.mulinlab.variantsampler.cmdline.tools;


import org.broadinstitute.barclay.argparser.Argument;
import org.broadinstitute.barclay.argparser.CommandLineProgramProperties;
import org.mulinlab.variantsampler.cmdline.CMDProgram;
import org.mulinlab.variantsampler.cmdline.programGroup.DatabaseProgramGroup;
import org.mulinlab.variantsampler.database.DatabaseBuilder;
import org.mulinlab.variantsampler.utils.RunFactory;
import org.mulinlab.varnote.constants.GlobalParameter;
import java.io.File;

@CommandLineProgramProperties(
        summary = BuildDatabase.USAGE_SUMMARY + BuildDatabase.USAGE_DETAILS,
        oneLineSummary = BuildDatabase.USAGE_SUMMARY,
        programGroup = DatabaseProgramGroup.class)

public final class BuildDatabase extends CMDProgram {
    static final String USAGE_SUMMARY = "Build Database";
    static final String USAGE_DETAILS =
            "\n\nUsage example:" +
            "\n" +
            "java -jar " + GlobalParameter.PRO_NAME + ".jar BuildDatabase -C db.ini -P EUR \n" ;

    @Argument(fullName = "Config", shortName = "C", doc = "The configuration file that defines the database paths.", optional = false)
    private File config = null;

    @Argument(fullName = "Population", shortName = "P", doc = "Population to select.", optional = false)
    private DatabaseBuilder.Population population = DatabaseBuilder.Population.EUR;

    @Argument(fullName = "Thread", shortName = "T", doc = "Threads to run the program.", optional = true)
    private int thread = 4;

    @Override
    protected int doWork() {
        RunFactory.buildDatabase(config.getAbsolutePath(), population, thread);
        return 0;
    }
}
