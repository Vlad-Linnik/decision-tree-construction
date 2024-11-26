package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class TreeNode {
    private String className;
    private List<String> classLabels;
    private List<String> attributeList;
    private double entropyValue;
    public Map<String, TreeNode> branches = new HashMap<>();
    private List<Map<String, String>> data;
    @SuppressWarnings("unused")
    public String nodeName;

    public String getNodeName() {
        return nodeName;
    }

    public ArrayList<String> getBranchesNames() {
        return new ArrayList<>(branches.keySet());
    }

    public TreeNode getBranch(String name) {
        return branches.get(name);
    }

    public TreeNode(List<Map<String, String>> data, String nodeName, String className) {
        this.className = className;
        this.nodeName = nodeName;
        this.data = data;
        if (!data.isEmpty()) {
            this.attributeList = new ArrayList<>(data.get(0).keySet());
            this.attributeList.remove(className);
        }
        // get unique values from column class names
        this.classLabels = data.stream().map(row -> row.get(className))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        this.entropyValue = calculateEntropy(this.data);
        this.addBranches();
    }

    private int getNumberOfClass(List<Map<String, String>> data, String className) {
        return (int) data.stream()
                .filter(row -> className.equals(row.get(this.className)))
                .count();
    }

    private double calculateEntropy(List<Map<String, String>> data) {
        int size = data.size();
        double entropy = 0;
        for (String className : this.classLabels) {
            int Ppositive = getNumberOfClass(data, className);
            if (Ppositive == size) {
                return 0;
            }
            if (Ppositive == 0) {
                continue;
            }
            double pPos = (double) Ppositive / size;

            entropy -= pPos * log2(pPos);
        }
        return entropy;
    }

    private double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private Map<String, Object> getMaxGainEntropy() {
        double maxGain = Double.NEGATIVE_INFINITY;
        String chosenAttribute = null;
        Map<String, Map<String, Double>> attributeEntropyDict = new HashMap<>();

        for (String attribute : attributeList) {
            double startEntropy = entropyValue;
            Map<String, Double> branchEntropies = new HashMap<>();

            Map<String, List<Map<String, String>>> groupedData = data.stream()
                    .collect(Collectors.groupingBy(row -> row.get(attribute)));

            for (Map.Entry<String, List<Map<String, String>>> entry : groupedData.entrySet()) {
                String choice = entry.getKey();
                List<Map<String, String>> branch = entry.getValue();

                int branchSize = branch.size();
                double branchEntropy = calculateEntropy(branch);
                branchEntropies.put(choice, branchEntropy);

                startEntropy -= ((double) branchSize / data.size()) * branchEntropy;
            }

            attributeEntropyDict.put(attribute, branchEntropies);

            if (startEntropy > maxGain) {
                maxGain = startEntropy;
                chosenAttribute = attribute;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("chosen_attribute", chosenAttribute);
        result.put("max_gain", maxGain);
        result.put("attribute_entropy_dict", attributeEntropyDict.get(chosenAttribute));
        return result;
    }

    public void addBranches() {
        if (this.entropyValue == 0) {
            for (String name : this.classLabels) {
                if (this.getNumberOfClass(this.data, name) > 0) {
                    this.nodeName = name;
                    return;
                }
            }
        }
        Map<String, Object> result = getMaxGainEntropy();
        String chosenAttribute = (String) result.get("chosen_attribute");
        this.nodeName = chosenAttribute;
        @SuppressWarnings("unchecked")
        Map<String, Double> attributeEntropyDict = (Map<String, Double>) result.get("attribute_entropy_dict");
        if (attributeEntropyDict != null) {
            for (String branchValue : attributeEntropyDict.keySet()) {
                List<Map<String, String>> branchData = data.stream()
                        .filter(row -> branchValue.equals(row.get(chosenAttribute)))
                        .collect(Collectors.toList());
                branches.put(branchValue, new TreeNode(branchData, branchValue, className));
            }
        }
    }

    public double getEntropyValue() {
        return entropyValue;
    }
}