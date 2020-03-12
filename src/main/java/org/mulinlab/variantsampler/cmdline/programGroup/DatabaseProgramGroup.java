package org.mulinlab.variantsampler.cmdline.programGroup;

import org.broadinstitute.barclay.argparser.CommandLineProgramGroup;

public class DatabaseProgramGroup implements CommandLineProgramGroup {

    @Override
    public String getName() { return "Database"; }

    @Override
    public String getDescription() { return "Database build related."; }
}