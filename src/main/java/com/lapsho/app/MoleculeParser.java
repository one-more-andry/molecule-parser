package com.lapsho.app;

import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;


public class MoleculeParser
{
    private static final Pattern NUMBER_AT_THE_END_PATTERN = Pattern.compile("\\d+$");
    private static final Pattern PARENTHESIS_PATTERN = Pattern.compile("((\\()|(\\))|(\\[)|(\\])|([a-z]{2,}))");

    public static Map<String,Integer> getAtoms(String formula) {
        return getAtoms(formula, 1);
    }

    private static Map<String,Integer> getAtoms(String formula, int outerSubscript) {
        validateFormula(formula);
        Map<String,Integer> atoms = new HashMap<>();

        for (String token: parseTokens(formula)) {
            int innerSubscript = getAtomSubscript(token);
            Map<String,Integer> atomsSubset = new HashMap<>();
            int endIndex = token.length() - 1 - ((innerSubscript > 1) ? Integer.toString(innerSubscript).length() : 0);

            if (token.contains("(") || token.contains("[")) {
                atomsSubset = getAtoms(token.substring(1, endIndex), innerSubscript * outerSubscript);

            } else {
                atomsSubset = atomizeMolecule(token, outerSubscript);
            }
            atoms = mergeAtoms(atoms, atomsSubset);
        }

        return atoms;
    }

    private static ArrayList<String> parseTokens(String formula) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder container = new StringBuilder();
        String parenthesisOpenSymbol = null;
        int parenthesisNestingLevel = 0;
        HashMap<String, String> parenthesisPairs = new HashMap<String, String>();
        parenthesisPairs.put("(", ")");
        parenthesisPairs.put("[", "]");
        parenthesisPairs.put("{", "}");

        for (String symbol: formula.split("")) {
            if (parenthesisNestingLevel == 0 && parenthesisPairs.getOrDefault(symbol, null) != null) {
                parenthesisNestingLevel++;
                parenthesisOpenSymbol = symbol;

                if (container.length() > 0) {
                    tokens.add(container.toString());
                }
                container.setLength(0);
                container.append(symbol);
                continue;
            }

            if (parenthesisNestingLevel > 0 && parenthesisOpenSymbol.equals(symbol)) {
                container.append(symbol);
                parenthesisNestingLevel++;
                continue;
            }

            if (parenthesisNestingLevel > 0 && parenthesisPairs.getOrDefault(parenthesisOpenSymbol, null).equals(symbol)) {
                container.append(symbol);
                parenthesisNestingLevel--;
                continue;
            }

            if (Character.isUpperCase(symbol.charAt(0)) && parenthesisNestingLevel == 0 && container.length() > 0) {
                tokens.add(container.toString());
                container.setLength(0);
            }

            container.append(symbol);
        }

        if (container.length() > 0) {
            tokens.add(container.toString());
        }

        return tokens;
    }

    private static void validateFormula(String formula) {
        Matcher parenthesisMatcher = PARENTHESIS_PATTERN.matcher(formula);
        Map<String, Integer> matches = new HashMap<>();

        while (parenthesisMatcher.find()) {
            String group = parenthesisMatcher.group();
            group = Character.isLetter(group.charAt(0)) ? "letter" : group;
            matches.put(group, matches.getOrDefault(group, 0) + 1);
        }

        if (
                matches.getOrDefault("(", 0) != matches.getOrDefault(")", 0) ||
                        matches.getOrDefault("[", 0) != matches.getOrDefault("]", 0) ||
                        matches.getOrDefault("letter", 0) > 0
        ) {
            throw new IllegalArgumentException();
        }
    }

    private static Map<String, Integer> mergeAtoms(Map<String, Integer> m1, Map<String, Integer> m2) {
        return Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue)
                ));
    }

    private static Map<String, Integer> atomizeMolecule(String token, int outerSubscript) {
        Map<String,Integer> atoms = new HashMap<>();
        int innerSubscript = getAtomSubscript(token);
        String atom = (innerSubscript > 1) ?
                token.substring(0, token.length() - Integer.toString(innerSubscript).length()) : token;
        atoms.put(atom, innerSubscript * outerSubscript);

        return atoms;
    }

    private static int getAtomSubscript(String token) {
        Matcher innerSubscriptMatcher = NUMBER_AT_THE_END_PATTERN.matcher(token);

        return (innerSubscriptMatcher.find()) ? Integer.parseInt(innerSubscriptMatcher.group()) : 1;
    }
}
