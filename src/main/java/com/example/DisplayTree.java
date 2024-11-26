package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class DisplayTree {
    public void generateDotFile(TreeNode node, StringBuilder builder, String parentName, String branchLabel) {
        String currentNodeName = node.nodeName.replace(" ", "_");
        if (parentName != null) {
            builder.append(
                    String.format("\"%s\" -> \"%s\" [label=\"%s\"];\n", parentName, currentNodeName, branchLabel));
        }
        for (Map.Entry<String, TreeNode> branch : node.branches.entrySet()) {
            generateDotFile(branch.getValue(), builder, currentNodeName, branch.getKey());
        }
    }

    public void exportToDot(TreeNode rootNode, String outputFilePath) {
        StringBuilder builder = new StringBuilder("digraph Tree {\n");
        generateDotFile(rootNode, builder, null, "");
        builder.append("}");
        try (PrintWriter writer = new PrintWriter(outputFilePath)) {
            writer.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
