package org.mulinlab.variantsampler.cmdline.programGroup;

import org.broadinstitute.barclay.argparser.CommandLineProgramGroup;

public class SamplerProgramGroup implements CommandLineProgramGroup {

    @Override
    public String getName() { return "Sampler"; }

    @Override
    public String getDescription() { return "Sampler related."; }
}