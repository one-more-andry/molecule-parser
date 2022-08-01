package com.lapsho.app;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.HashMap;

/**
 * Unit test for simple App.
 */
public class MoleculeParserTest
{
    @Test
    public void assertCountCyclopentadienylironDicarbonylDimer() {
        HashMap<String, Integer> expected = new HashMap<>();
        String formula = "(C5H5)Fe(CO)2CH3";
        expected.put("C", 8);
        expected.put("H", 8);
        expected.put("Fe", 1);
        expected.put("O", 2);

        assertEquals("Not valid result for assertCountCyclopentadienylironDicarbonylDimer: ",
                expected, MoleculeParser.getAtoms(formula));
    }

    @Test
    public void assertCountWater() {
        HashMap<String, Integer> expected = new HashMap<>();
        String formula = "H2O";
        expected.put("H", 2);
        expected.put("O", 1);

        assertEquals("Not valid result for assertCountWater: ",
                expected, MoleculeParser.getAtoms(formula));
    }

    @Test
    public void assertCountWeirdMolecule() {
        HashMap<String, Integer> expected = new HashMap<>();
        String formula = "As2{Be4C5[BCo3(CO2)3]2}4Cu5";
        expected.put("As", 2);
        expected.put("B", 8);
        expected.put("Cu", 5);
        expected.put("Be", 16);
        expected.put("C", 44);
        expected.put("Co", 24);
        expected.put("O", 48);

        assertEquals("Not valid result for assertCountWeirdMolecule: ",
                expected, MoleculeParser.getAtoms(formula));
    }

    @Test
    public void assertCountHexolSulphate() {
        HashMap<String, Integer> expected = new HashMap<>();
        String formula = "{[Co(NH3)4(OH)2]3Co}(SO4)3";
        expected.put("S", 3);
        expected.put("H", 42);
        expected.put("Co", 4);
        expected.put("N", 12);
        expected.put("O", 18);

        assertEquals("Not valid result for assertCountHexolSulphate: ",
                expected, MoleculeParser.getAtoms(formula));
    }

    @Test
    public void assertCountEvilizedWater() {
        HashMap<String, Integer> expected = new HashMap<>();
        String formula = "{((H)2)[O]}";
        expected.put("H", 2);
        expected.put("O", 1);

        assertEquals("Not valid result for assertCountEvilizedWater: ",
                expected, MoleculeParser.getAtoms(formula));
    }

    @Test
    public void assertExceptionSmallLetters() {
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("pim");});
    }

    @Test
    public void assertExceptionUnequalParenthesesNumber() {
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("H2(O");});
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("H2)O");});
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("H2[O");});
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("H2]O");});

        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("(C5H5)Fe(CO2CH3");});
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("(C5H5)FeCO)2CH3");});
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("[C5H5]Fe[CO)2CH3");});
        assertThrows(IllegalArgumentException.class, () -> {MoleculeParser.getAtoms("[C5H5]Fe(CO]2CH3");});
    }

    @Test
    public void assertCountComplexFormula() {
        HashMap<String, Integer> expected = new HashMap<>();
        String formula = "K4[ONm(SO3)52]2OK4(ONm[SO3]52)2OK4[ONm2]2OK4(ONc2)2OK4[ONm[SO3]52]2OK4(ONm(SO3)52)2OH2O";
        expected.put("S", 416);
        expected.put("Nc", 4);
        expected.put("H", 2);
        expected.put("K", 24);
        expected.put("Nm", 12);
        expected.put("O", 1267);

        assertEquals("Not valid result for assertCountComplexFormula: ",
                expected, MoleculeParser.getAtoms(formula));
    }
}
