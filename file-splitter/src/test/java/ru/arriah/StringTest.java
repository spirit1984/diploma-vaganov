package ru.arriah;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

/**
 * Created by shevchenko-dv-100705 on 25.11.16.
 */
public class StringTest {



    @Test
    public void genomeDescriptionShouldMatch() {
        Assert.assertTrue(StringUtils.isGeneDescription(">lcl|HQ630064.1_gene_1 [gene=UL56] [locus_tag=ILTV_ORF1] [location=7062..7922]"));
    }

    @Test
    public void genomeParthShouldNotBeMatchedAsDescription() {
        Assert.assertFalse(StringUtils.isGeneDescription("ATGTCTTCAGAGGACACATCGGGATTCCTAACGCCCCCCGCAAGTGATGACGACACTGACCCTTCCGAGC"));
    }

    @Test
    public void patternTextTest() {

        final String desc = ">lcl|HQ630064.1_gene_1 [gene=UL56] [locus_tag=ILTV_ORF1] [location=7062..7922]";

        Matcher m = StringUtils.genePattern.matcher(desc);

        Assert.assertEquals(1, m.groupCount());
        Assert.assertTrue("find", m.find());
        Assert.assertEquals("gene=UL56", m.group(0));
        Assert.assertEquals("UL56", m.group(1));

    }

    @Test
    public void locationWithoutComplementTextTest() {
        final String desc = ">lcl|HQ630064.1_gene_1 [gene=UL56] [locus_tag=ILTV_ORF1] [location=7062..7922]";
        Matcher m = StringUtils.locationPattern.matcher(desc);

        Assert.assertTrue("find", m.find());
        Assert.assertEquals("location=7062", m.group(0));
        Assert.assertEquals("7062", m.group(1));
        Assert.assertEquals("UL56_7062", StringUtils.convertToFileName(desc));

    }

    @Test
    public void locationWithComplementTextTest() {


        final String desc = ">lcl|HQ630064.1_gene_3 [gene=UL54] [locus_tag=ILTV_ORF3] [location=complement(10420..12280)]";


        Matcher m = StringUtils.locationPattern.matcher(desc);

        Assert.assertTrue("find", m.find());
        Assert.assertEquals("location=complement", m.group(0));
        Assert.assertEquals("complement", m.group(1));
        Assert.assertEquals("UL54_complement", StringUtils.convertToFileName(desc));

    }

    @Test
    public void locationWithDpwTextTest() {


        final String desc = ">lcl|HQ630064.1_gene_3 [gene=UL3.5] [locus_tag=ILTV_ORF3] [location=complement(10420..12280)]";


        Matcher m = StringUtils.locationPattern.matcher(desc);

        Assert.assertTrue("find", m.find());
        Assert.assertEquals("location=complement", m.group(0));
        Assert.assertEquals("complement", m.group(1));
        Assert.assertEquals("UL35_complement", StringUtils.convertToFileName(desc));

    }

    @Test
    public void locationMGFTestText() {
        final String desc = ">lcl|FR682468.1_cds_CBW46643.1_1 [gene=MGF_360-1L] [protein=MGF_360-1L] [protein_id=CBW46643.1] [location=complement(852..1934)]";
        Matcher m = StringUtils.locationPattern.matcher(desc);

        Assert.assertTrue("find", m.find());
        Assert.assertEquals("location=complement", m.group(0));
        Assert.assertEquals("complement", m.group(1));
        Assert.assertEquals("MGF3601L_complement", StringUtils.convertToFileName(desc));
    }


}
