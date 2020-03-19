## vSampler
   vSampler is a fast, scalable and versatile tool for sampling matched sets of variants both as a web server and as a local program. Given input variants, vSampler can randomly draw control variants with eight optional matching properties and tissue/cell type-specific annotations by a novel data structure and searching algorithm. These matched random controls could be used to construct null distribution in enrichment/colocalization analysis to estimate the significance of statistical tests empirically or serve as negative training/test data for pathogenic/regulatory variant prediction models. vSampler runs significantly faster and supports both single-nucleotide polymorphisms (SNPs) and small insertions and deletions (Indels). It also provides comprehensive context-specific functional annotations as matching properties. For more information, please visit http://mulinlab.org/vsampler or http://mulinlab.tmu.edu.cn/vsampler.
   
   - We welcome any discussion, suggestion and potential contribution of new functional prediction scores through github or contact Dr. Mulin Jun Li (mulinli{at}connect.hku.hk). 

## vSampler
### System Requirements
   Java Runtime Environment (JRE) version 8.0 or above is required for vSampler. It can be downloaded from the Java web site. Installing the JRE is very easy in Windows OS and Mac OS X. In Linux, you have more work to do. Details of the installation can be found see the http://www.java.com/en/download/help/linux_install.xml.
   
### Download   
This steps guides you from downloading the vSampler program.
1 The latest version of vSampler can be downloaded from https://github.com/mulinlab/vSampler/releases. 
2 Extract the ZIP archive, you should find file called VariantSampler-x.y.z.jar which can be used for run vSampler.
3 You should also download indexed genotype reference panels from https://drive.google.com/drive/folders/1XEnUARZVTRzMj0VIIBi2bd-xLWuG2Shq?usp=sharing

### Test vSampler
To test that you can run vSampler tools, run the following command in your terminal application, providing either the full path to the VariantSampler-x.y.z.jar file:

```shell
java -jar -Xms1g -Xmx4g VariantSampler-x.y.z.jar
```

> The arguments `-Xmx4g` and `-Xms1g` set the initial and maximum Java heap sizes for vSampler as 1G and 4G respectively. Specifying a larger maximum heap size can speed up the analysis. A higher setting like -Xmx8g or even -Xmx20g is required when there is a large number of variants, say 5 million. The number, however, should be less than the size of physical memory of a machine. 
You should see a complete list of all the tools in the vSampler toolkit. 

### Sampler Examples
* Local vSampler requires query file and indexed genotype reference panels as inputs.
* Local vSampler supports five types of query file, including `VCF`, `VCF-like`, `Coord-Only`, `Coord-Allele`, `TAB`. 

```shell
java -jar -Xmx4g -Xms1g VariantSampler-x.y.z.jar Sampler -Q:vcf data/example.vcf -D data/EUR.gz
java -jar -Xmx4g -Xms1g VariantSampler-x.y.z.jar Sampler -Q:vcfLike data/example.vcflike.tsv -D data/EUR.gz
java -jar -Xmx4g -Xms1g VariantSampler-x.y.z.jar Sampler -Q:coordOnly data/example.coordOnly.tsv -D data/EUR.gz
java -jar -Xmx4g -Xms1g VariantSampler-x.y.z.jar Sampler -Q:coordAllele data/example.coordAllele.tsv -D data/EUR.gz
java -jar -Xmx4g -Xms1g VariantSampler-x.y.z.jar Sampler -Q:tab,c=2,b=3,e=4,0=true data/example.tab.tsv -D data/EUR.gz
```
## Copyright
Copyright (c) Mulinlab@Tianjin Medical University 2016-2020. All rights reserved.
