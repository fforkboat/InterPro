package com.fforkboat.parser.tree;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;

public class SVGForSyntaxTree {
    private final TreeLayout<SyntaxTreeNode> treeLayout;
    private String svgText;

    private TreeForTreeLayout<SyntaxTreeNode> getTree() {
        return treeLayout.getTree();
    }

    private Iterable<SyntaxTreeNode> getChildren(SyntaxTreeNode parent) {
        return getTree().getChildren(parent);
    }

    private Rectangle2D.Double getBoundsOfNode(SyntaxTreeNode node) {
        return treeLayout.getNodeBounds().get(node);
    }

    /**
     * @param treeLayout the {@link TreeLayout} to be rendered as SVG
     */
    public SVGForSyntaxTree(TreeLayout<SyntaxTreeNode> treeLayout) {
        this.treeLayout = treeLayout;
    }

    // -------------------------------------------------------------------
    // generating

    private void generateEdges(StringBuilder result, SyntaxTreeNode parent) {
        if (!getTree().isLeaf(parent)) {
            Rectangle2D.Double b1 = getBoundsOfNode(parent);
            double x1 = b1.getCenterX();
            double y1 = b1.getCenterY();
            for (SyntaxTreeNode child : getChildren(parent)) {
                Rectangle2D.Double b2 = getBoundsOfNode(child);
                result.append(SVGUtil.line(x1, y1, b2.getCenterX(), b2.getCenterY(),
                        "stroke:black; stroke-width:2px;"));

                generateEdges(result, child);
            }
        }
    }

    private void generateBox(StringBuilder result, SyntaxTreeNode syntaxTreeNode) {
        // draw the box in the background
        Rectangle2D.Double box = getBoundsOfNode(syntaxTreeNode);
        result.append(SVGUtil.rect(box.x + 1, box.y + 1, box.width - 2, box.height - 2,
                "fill:orange; stroke:rgb(0,0,0);", "rx=\"10\""));

        // draw the text on top of the box (possibly multiple lines)
        String[] lines = syntaxTreeNode.toString().split("\n");
        int fontSize = 12;
        int x = (int) box.x + fontSize / 2 + 2;
        int y = (int) box.y + fontSize + 1;
        String style = String.format("font-family:sans-serif;font-size:%dpx;",
                fontSize);
        for (int i = 0; i < lines.length; i++) {
            result.append(SVGUtil.text(x, y, style, lines[i]));
            y += fontSize;
        }
    }

    private String generateDiagram() {
        StringBuilder result = new StringBuilder();

        // generate the edges and boxes (with text)
        generateEdges(result, getTree().getRoot());
        for (SyntaxTreeNode textInBox : treeLayout.getNodeBounds().keySet()) {
            generateBox(result, textInBox);
        }

        // generate the svg containing the diagram items (edges and boxes)
        Dimension size = treeLayout.getBounds().getBounds().getSize();
        return SVGUtil.svg(size.width, size.height, result.toString());
    }

    /**
     * @return the rendered tree, described in SVG format.
     */
    public String getSVG() {
        if (svgText == null) {
            svgText = SVGUtil.doc(generateDiagram());
        }
        return svgText;
    }
}