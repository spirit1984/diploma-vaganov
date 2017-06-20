package ru.arriah.vetrf

import org.junit.Assert
import org.junit.Test


/**
 * Created by shevchenko-dv-100705 on 20.06.17.
 */
class StringTest {

    @Test
    fun genomeDescriptionShouldMatch() {
        Assert.assertTrue(isGeneDescription(">lcl|HQ630064.1_gene_1 [gene=UL56] [locus_tag=ILTV_ORF1] [location=7062..7922]"))
    }

    @Test
    fun genomeParthShouldNotBeMatchedAsDescription() {
        Assert.assertFalse(isGeneDescription("ATGTCTTCAGAGGACACATCGGGATTCCTAACGCCCCCCGCAAGTGATGACGACACTGACCCTTCCGAGC"))
    }

    @Test
    fun patternTextTest() {
        val desc = ">lcl|HQ630064.1_gene_1 [gene=UL56] [locus_tag=ILTV_ORF1] [location=7062..7922]"
        val m = genePattern.find(desc)
        Assert.assertEquals("gene=UL56", m?.value ?: "FAIL")
    }

    @Test
    fun locationWithoutComplementTextTest() {
        val desc = ">lcl|HQ630064.1_gene_1 [gene=UL56] [locus_tag=ILTV_ORF1] [location=7062..7922]"
        val m = locationPattern.find(desc)
        
        Assert.assertEquals("location=7062", m?.value ?: "FAIL")        
        Assert.assertEquals("UL56_7062", convertToFilename(desc))

    }

    @Test
    fun locationWithComplementTextTest() {
        val desc = ">lcl|HQ630064.1_gene_3 [gene=UL54] [locus_tag=ILTV_ORF3] [location=complement(10420..12280)]"
        val m = locationPattern.find(desc)
        
        Assert.assertEquals("location=complement", m?.value ?: "FAIL")        
        Assert.assertEquals("UL54_complement", convertToFilename(desc))
    }

    @Test
    fun locationWithDpwTextTest() {
        val desc = ">lcl|HQ630064.1_gene_3 [gene=UL3.5] [locus_tag=ILTV_ORF3] [location=complement(10420..12280)]"
        val m = locationPattern.find(desc)
        
        Assert.assertEquals("location=complement", m?.value ?: "FAIL")        
        Assert.assertEquals("UL35_complement", convertToFilename(desc))

    }

    @Test
    fun locationMGFTestText() {
        val desc = ">lcl|FR682468.1_cds_CBW46643.1_1 [gene=MGF_360-1L] [protein=MGF_360-1L] [protein_id=CBW46643.1] [location=complement(852..1934)]"
        val m = locationPattern.find(desc)
        
        Assert.assertEquals("location=complement", m?.value ?: "FAIL")
        Assert.assertEquals("MGF3601L_complement", convertToFilename(desc))
    }



}